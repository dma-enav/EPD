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
package dk.dma.epd.common.prototype.layers.ais;

import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.text.Formatter;

/**
 * InfoPanel that displays information about the past-track points
 */
public class PastTrackInfoPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public PastTrackInfoPanel() {
        super();
    }

    /**
     * Displays a descriptive text about the past-track point given by the wpCircle parameter
     * @param wpCircle the graphics element for a past-track point
     */
    public void showWpInfo(PastTrackWpCircle wpCircle) {
        long mmsi = wpCircle.getPastTrackGraphic().getMmsi();
        if (mmsi == -1) {
            showText("");
            return;
        }
        
        String name = "N/A";
        MobileTarget mobileTarget = wpCircle.getPastTrackGraphic().getMobileTarget();
        if (mobileTarget != null && mobileTarget.getStaticData() != null) {
            name = String.format("%s (%d)", 
                    mobileTarget.getStaticData().getTrimmedName(),
                    mobileTarget.getMmsi());
        }
        
        try{
            StringBuilder str = new StringBuilder();
            str.append("<html>");
            str.append("<b>" + name + "</b><br/>");
            str.append("<table border='0' cellpadding='2'>");
            str.append("<tr><td>Time:</td><td>" + Formatter.formatShortDateTime(wpCircle.getDate()) + "</td></tr>");
            str.append("</table>");
            str.append("</html>");
            showText(str.toString());
            
        } catch(Exception e) {
            StringBuilder str = new StringBuilder();
            showText(str.toString());
        }        
    }
}
