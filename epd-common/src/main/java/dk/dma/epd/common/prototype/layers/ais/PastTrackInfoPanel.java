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
package dk.dma.epd.common.prototype.layers.ais;

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
        
        try{
            StringBuilder str = new StringBuilder();
            str.append("<html>");
            str.append("<b>Past Track Point</b><br/>");
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
