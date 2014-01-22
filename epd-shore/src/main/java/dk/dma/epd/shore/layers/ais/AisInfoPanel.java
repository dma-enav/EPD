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
package dk.dma.epd.shore.layers.ais;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.text.Formatter;

/**
 * AIS mouse over info
 */
public class AisInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public AisInfoPanel() {
        super();
    }

    /**
     * Display a AIS info
     * 
     * @param vessel
     */
    public void showAisInfo(VesselTarget vessel) {
        if (vessel != null) {
            VesselStaticData vsd = vessel.getStaticData();
            VesselPositionData vpd = vessel.getPositionData();
            
            StringBuilder sb = new StringBuilder();
            sb.append("<HTML>");
            
            if (vsd != null) {
                String name = vsd.getName() != null ? AisMessage.trimText(vsd.getName()) : "N/A";
                sb.append(name);
                sb.append(" (" + vessel.getMmsi() + ")");                
            } else {
                sb.append("N/A (" + vessel.getMmsi() + ")");
            }
            
            if(vpd != null) {
                sb.append("<BR/>COG ");
                sb.append(Formatter.formatDouble(new Double(vpd.getCog()), 2));                
                sb.append("° SOG ");
                sb.append(Formatter.formatSpeed(new Double(vpd.getSog())));
            }
            
            sb.append("</HTML>");
            showText(sb.toString());
        }
    }
}
