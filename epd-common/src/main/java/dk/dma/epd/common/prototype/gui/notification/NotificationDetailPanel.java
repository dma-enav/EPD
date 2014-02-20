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
package dk.dma.epd.common.prototype.gui.notification;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.notification.Notification;

/**
 * Base class for notification detail panels.
 * <p>
 * The default implementation and a {@linkplain JLabel} and
 * displays the notifications in this.
 */
public class NotificationDetailPanel<N extends Notification<?,?>> extends JPanel {

   private static final long serialVersionUID = 1L;
   
   protected JLabel contentLbl = new JLabel();
   protected N notification;

   /**
    * Constructor
    */
   public NotificationDetailPanel() {
       super();
   }
   
   /**
    * Builds the user interface.
    * <p>
    * Default implementation adds a label
    */
   protected void buildGUI() {
       setLayout(new BorderLayout());
       setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
       contentLbl.setVerticalAlignment(SwingConstants.TOP);
       add(contentLbl, BorderLayout.CENTER);
   }
   
   /**
    * Returns the current notification 
    * @return the current notification 
    */
   public N getNotification() {
       return notification;
   }
   
   /**
    * Sets the current notification.
    * <p>
    * Default implementation bangs the notification 
    * description into a label.
    * 
    * @param notification the current notification
    */
   public void setNotification(N notification) {
       this.notification = notification;
       
       if (notification == null) {
           contentLbl.setText("");
       } else {
           contentLbl.setText(notification.toHtml());
       }
   }
}
