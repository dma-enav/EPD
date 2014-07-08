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
package dk.dma.epd.ship.layers.ais;

import java.text.DecimalFormat;

import dk.dma.epd.common.prototype.ais.AisTarget.Status;
import dk.dma.epd.common.prototype.ais.AtoNTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget.AisClass;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.frv.enav.common.xml.risk.response.Risk;
import dk.frv.enav.common.xml.risk.response.RiskList;

/**
 * AIS target mouse over info
 */
public class AisTargetInfoPanel extends InfoPanel implements Runnable {
    private static final long serialVersionUID = 1L;

    private VesselTarget vesselTarget;

    public AisTargetInfoPanel() {
        super();
        new Thread(this).start();
    }

    public void showAtonInfo(AtoNTarget atonTarget) {
        String name = atonTarget.getTrimmedName();
        Status status = atonTarget.getStatus();
        StringBuilder str = new StringBuilder();
        str.append("<html>");

        if (atonTarget.getVirtual() == 1) {
            str.append("Virtual AtoN");
        } else {
            str.append("Physical AtoN");
        }
        str.append("<br>" + name + "</br>");
        str.append("<br>Type: " + atonTarget.getAtonType().getPrettyName()
                + "</br>");
        str.append("<br>Status: " + status + "</br>");
        str.append("</html>");
        showText(str.toString());
    }

    public void showAisInfo(VesselTarget vesselTarget) {
        this.vesselTarget = vesselTarget;
        VesselStaticData staticData = vesselTarget.getStaticData();
        String name = null;
        String callsign = null;
        if (staticData != null) {
            name = staticData.getTrimmedName();
            callsign = staticData.getTrimmedCallsign();
        }
        VesselPositionData positionData = vesselTarget.getPositionData();
        String cog = "N/A";
        String sog = "N/A";

        if (positionData != null) {
            cog = Formatter.formatDegrees((double) positionData.getCog(), 0);
            sog = Formatter.formatSpeed((double) positionData.getSog());
        }

        StringBuilder str = new StringBuilder();
        str.append("<html>");
        if (name != null) {
            str.append(name + " (" + vesselTarget.getMmsi() + ")");
        } else {
            str.append(vesselTarget.getMmsi());
        }
        if (vesselTarget.getAisClass() == AisClass.B) {
            str.append(" [<b>B</b>]");
        }
        str.append("<br/>");

        /*
         * Get the risk object
         */
        RiskList riskList = EPDShip.getInstance().getRiskHandler().getRiskList(
                vesselTarget.getMmsi());
        // if( riskList!=null && !riskList.getRisks().isEmpty()){
        // Risk risk = riskList.getRisks().iterator().next();
        // str.append("length : " + risk.getLength() + " m " );
        //
        // }

        if (callsign != null) {
            str.append(callsign + "<br/>");
        }
        str.append("COG " + cog + "  SOG " + sog + "<br/>");

        Risk riskIndex = EPDShip.getInstance().getRiskHandler().getRiskLevel(
                vesselTarget.getMmsi());
        if (riskIndex != null) {
            DecimalFormat fmt = new DecimalFormat("#.####");
            str.append("risk index : "
                    + fmt.format(riskIndex.getRiskNorm())
                    + " ("
                    + fmt.format(riskIndex.getProbability()
                            * riskIndex.getConsequence() * 1000000)
                    + "$) <br/>");
        }

        if (riskList != null) {
            double total = 0;
            for (Risk risk : riskList.getRisks()) {
                if (risk.getAccidentType().equals("MACHINERYFAILURE")) {
                    continue;
                }
                total += risk.getRiskNorm();
            }
            for (Risk risk : riskList.getRisks()) {
                if (risk.getAccidentType().equals("MACHINERYFAILURE")) {
                    continue;
                }
                str.append(risk.getAccidentType() + " : "
                        + (int) (risk.getRiskNorm() / total * 100) + " % <br/>");
            }
        }
        str.append("</html>");

        showText(str.toString());
    }

    @Override
    public void run() {
        while (true) {
            Util.sleep(10000);
            if (this.isVisible() && vesselTarget != null) {
                showAisInfo(vesselTarget);
            }
        }
    }
}
