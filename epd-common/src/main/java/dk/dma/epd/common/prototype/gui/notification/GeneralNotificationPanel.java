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

import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;

import dk.dma.epd.common.prototype.notification.GeneralNotification;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.notification.NotificationType;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;

/**
 * A panel for general notifications
 */
public class GeneralNotificationPanel extends NotificationPanel<GeneralNotification> {

    private static final long serialVersionUID = 1L;

    private static final String[] NAMES = { "", "", "Date", "Title", "Time To" };

    /**
     * Constructor
     */
    public GeneralNotificationPanel(NotificationCenterCommon notificationCenter) {
        super(notificationCenter);

        table.getColumnModel().getColumn(0).setMaxWidth(18);
        table.getColumnModel().getColumn(1).setMaxWidth(18);
        table.getColumnModel().getColumn(2).setPreferredWidth(30);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(30);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NotificationType getNotitficationType() {
        return NotificationType.NOTIFICATION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NotificationTableModel<GeneralNotification> initTableModel() {
        return new NotificationTableModel<GeneralNotification>() {
            private static final long serialVersionUID = 1L;

            @Override
            public String[] getColumnNames() {
                return NAMES;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex <= 1) {
                    return ImageIcon.class;
                } else {
                    return super.getColumnClass(columnIndex);
                }
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Notification<?, ?> notification = getNotification(rowIndex);

                switch (columnIndex) {
                case 0:
                    return !notification.isRead() ? ICON_UNREAD : (notification.isAcknowledged() ? ICON_ACKNOWLEDGED : null);
                case 1:
                    return notification.getSeverity() == NotificationSeverity.ALERT ? ICON_ALERT
                            : (notification.getSeverity() == NotificationSeverity.WARNING ? ICON_WARNING : null);
                case 2:
                    return Formatter.formatShortDateTimeNoTz(notification.getDate());
                case 3:
                    return notification.getTitle();
                case 4:
                    long timeLeft = notification.getDate().getTime() - PntTime.getInstance().getDate().getTime();

//                    System.out.println("Notification Date: "+ notification.getDate() + " In Mili: " +  notification.getDate().getTime());
//                    System.out.println("Current Date: "+  PntTime.getInstance().getDate() + " In Mili: " +  PntTime.getInstance().getDate().getTime());
                    
                    String timeLeftStr = String.format(
                            "%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes(timeLeft),
                            TimeUnit.MILLISECONDS.toSeconds(timeLeft) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft)));

                    return timeLeftStr;
                default:
                }
                return null;
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NotificationDetailPanel<GeneralNotification> initNotificationDetailPanel() {
        return new GeneralNotificationDetailPanel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doRefreshNotifications() {
        refreshTableData();
        // notifyListeners();
    }

}

/**
 * The detail panel for general notifications
 */
class GeneralNotificationDetailPanel extends NotificationDetailPanel<GeneralNotification> {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public GeneralNotificationDetailPanel() {
        super();
        buildGUI();
    }
}
