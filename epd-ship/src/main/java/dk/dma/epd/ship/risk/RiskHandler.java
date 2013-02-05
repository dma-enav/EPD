/* Copyright (c) 2011 Danish Maritime Authority
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
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

        while (EPDShip.getSettings().getAisSettings().isShowRisk()) {
            //VesselTarget ownShip = EeINS.getAisHandler().getOwnShip();
            List<RiskList> riskLists = new ArrayList<>();
            try {
                riskLists = EPDShip.getShoreServices().getRiskIndexes(54.75, 56.0, 10.65, 11.25);
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

        EPDShip.getSettings().getAisSettings().setShowRisk(onOff);

        if (onOff) {
            // start a new one
            EPDShip.startRiskHandler();
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
