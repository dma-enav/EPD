/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.ship.risk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.frv.enav.common.xml.risk.response.Risk;
import dk.frv.enav.common.xml.risk.response.RiskList;

public class RiskHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RiskHandler.class);

    public enum RiskLevel {
        HIGH, MEDIUM, LOW, UNKNOWN
    };

    private Map<Long, RiskList> riskListMap = new HashMap<>();
    private static final Object MUTEX = new Object();

    public RiskHandler() {
        super();
        EPDShip.startThread(this, "RiskHandler");
    }

    @Override
    public void run() {

        while (EPDShip.getInstance().getSettings().getAisSettings().isShowRisk()) {
            List<RiskList> riskLists = new ArrayList<>();
            try {
                riskLists = EPDShip.getInstance().getShoreServices().getRiskIndexes(54.75, 56.0, 10.65, 11.25);
            } catch (ShoreServiceException e) {
                LOG.warn("cannot get risk indexes", e);
            }
            synchronized (MUTEX) {
                riskListMap.clear();
                for (RiskList list : riskLists) {
                    riskListMap.put(list.getMmsi().longValue(), list);
                }
            }
            Util.sleep(10000);
        }

    }

    public void toggleRiskHandler(boolean onOff) {

        EPDShip.getInstance().getSettings().getAisSettings().setShowRisk(onOff);

        if (onOff) {
            // start a new one
            EPDShip.getInstance().startRiskHandler();
        } else {
            // stopping, clear the index map as it wont be updated any longer.
            riskListMap.clear();
        }

    }

    public RiskList getRiskList(Long mmsi) {
        return riskListMap.get(mmsi);

    }

    public Risk getRiskLevel(Long mmsi) {
        
        RiskList list = riskListMap.get(mmsi);
        
        if (list == null || list.getRisks().isEmpty()) {
            return null;
        }
        /*
         * get total risk
         */
        for (Risk risk : list.getRisks()) {
            
            if(risk.getAccidentType().equals("MACHINERYFAILURE")){
                /*
                 * MACHINERYFAILURE is total risk for all incident type 
                 * 
                 */
                return risk;
                
            }
        }
        return null;
    }
}
