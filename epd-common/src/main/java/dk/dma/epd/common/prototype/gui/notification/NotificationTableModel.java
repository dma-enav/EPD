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

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.Notification;

/**
 * Base class for notification table models
 */
public abstract class NotificationTableModel<N extends Notification<?,?>> extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    
    protected static final ImageIcon ICON_UNREAD        = EPD.res().getCachedImageIcon("images/notifications/unread.png");
    protected static final ImageIcon ICON_ACKNOWLEDGED  = EPD.res().getCachedImageIcon("images/notifications/acknowledged.png");
    protected static final ImageIcon ICON_ALERT         = EPD.res().getCachedImageIcon("images/notifications/error.png");
    protected static final ImageIcon ICON_WARNING       = EPD.res().getCachedImageIcon("images/notifications/warning.png");
    
    List<N> notifications = new ArrayList<>();

    /**
     * Returns the list of column names
     * @return
     */
    public abstract String[] getColumnNames();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount() {
        return notifications.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getColumnCount() {
        return getColumnNames().length;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int columnIndex) {
        return getColumnNames()[columnIndex];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }
    
    /**
     * Returns the notification at the given row
     * @param row the row
     * @return the notification at the given row
     */
    public N getNotification(int row) {
        if (row < 0 || row >= notifications.size()) {
            return null;
        }
        return notifications.get(row);
    }

    /**
     * Returns the notifications of this table model
     * @return the notifications of this table model
     */
    public List<N> getNotifications() {
        return notifications;
    }

    /**
     * Sets the notifications of this table model.
     * Make sure to call fireTableDataChanged afterwards.
     * @param notifications the notifications of this table model
     */
    public void setNotifications(List<N> notifications) {
        this.notifications = notifications;
    }
}
