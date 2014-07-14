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
public abstract class NotificationDetailPanel<N extends Notification<?,?>> extends JPanel {

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
       contentLbl.setHorizontalAlignment(SwingConstants.LEFT);
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
