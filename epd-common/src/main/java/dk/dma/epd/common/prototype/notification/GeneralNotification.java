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
package dk.dma.epd.common.prototype.notification;

import dk.dma.epd.common.text.Formatter;

/**
 * Class that can be used for general notifications
 */
public class GeneralNotification extends Notification<Object, Object> {
    
    private static final long serialVersionUID = 1L;

    /**
     * Designated constructor
     * 
     * @param value
     * @param id
     * @param type
     */
    public GeneralNotification(Object value, Object id, NotificationType type) {
        super(value, id, type);
    }
    
    /**
     * Constructor
     * 
     * @param value
     * @param id
     */
    public GeneralNotification(Object value, Object id) {
        this(value, id, NotificationType.NOTIFICATION);
    }
    
    /**
     * Constructor
     */
    public GeneralNotification() {
        this(null, System.currentTimeMillis(), NotificationType.NOTIFICATION);
    }
    
    
    /**
     * Returns a HTML description of this notification
     * @return a HTML description of this notification
     */
    @Override
    public String toHtml() {
        StringBuilder html = new StringBuilder("<html>");
        html.append("<table>");
        html.append(String.format("<tr><th>Communications Log from:</th><td>%s</td></tr>", Formatter.formatHtml(title)));
//        html.append(String.format("<tr><th>Date:</th><td>%s</td></tr>", Formatter.formatShortDateTime(date)));
        html.append(String.format("<tr><th valign='top'>Messages:</th><td>%s</td></tr>", Formatter.formatHtml(description)));
        html.append("</table>");
        return html.append("</html>").toString();
    }
}
