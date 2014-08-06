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
package dk.dma.epd.common.prototype.layers.intendedroute;

import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.model.intendedroute.IntendedRouteFilterMessage;
import dk.dma.epd.common.text.Formatter;

public class IntendedRouteCPAInfoPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    public IntendedRouteCPAInfoPanel() {
        super();
    }

    public void showWpInfo(IntendedRouteCPAGraphic tcpaInfo) {

        IntendedRouteFilterMessage filterMessage = tcpaInfo.getMessage();
        if (filterMessage == null) {
            showText("");
            return;
        }

        StringBuilder str = new StringBuilder();
        str.append("<html>");
        str.append("<b>Route CPA Warning</b><br/>");
        str.append(filterMessage.getMessage() + "<br/>");
        str.append("At " + Formatter.formatYodaTime(filterMessage.getTime1()) + "<br/>");
        str.append("</table>");
        str.append("</html>");

        showText(str.toString());

    }

}
