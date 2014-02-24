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
package dk.dma.epd.common.prototype.layers.intendedroute;

import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;

public class IntendedRouteTCPAInfoPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    public IntendedRouteTCPAInfoPanel() {
        super();
    }

    public void showWpInfo(IntendedRouteTCPAGraphic tcpaInfo) {

        IntendedRouteFilterMessage filterMessage = tcpaInfo.getMessage();
        if (filterMessage == null) {
            showText("");
            return;
        }

         StringBuilder str = new StringBuilder();
         str.append("<html>");
         str.append("<b>Intended route TCPA Warning</b><br/>");
         str.append(filterMessage.getMessage() + "<br/>");
         str.append("</table>");
         str.append("</html>");
        
         showText(str.toString());

    }



}
