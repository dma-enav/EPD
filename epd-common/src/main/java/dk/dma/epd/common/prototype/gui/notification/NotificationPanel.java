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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.notification.NotificationType;

/**
 * Base class for notification panels
 */
public abstract class NotificationPanel<N extends Notification<?,?>> extends JPanel implements ListSelectionListener {

    private static final long serialVersionUID = 1L;

    protected List<NotificationPanelListener> listeners = new CopyOnWriteArrayList<>();
    
    protected NotificationTableModel<N> tableModel;
    protected JTable table = new JTable();
    protected JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    protected NotificationDetailPanel<N> notificationDetailPanel;
    
    // Standard actions
    protected JButton acknowledgeBtn = new JButton("Acknowledge", EPD.res().getCachedImageIcon("images/notifications/tick.png"));
    protected JButton gotoBtn = new JButton("Goto", EPD.res().getCachedImageIcon("images/notifications/map-pin.png"));
    protected JButton deleteBtn = new JButton("Delete", EPD.res().getCachedImageIcon("images/notifications/cross.png"));
    
    /**
     * Constructor
     */
    public NotificationPanel() {
        super(new BorderLayout());
        
        add(splitPane, BorderLayout.CENTER);
        
        // Initialize the table
        tableModel = initTableModel();
        table.setModel(tableModel);
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.setShowHorizontalLines(false);
        table.setShowVerticalLines(false);
        table.setShowGrid(false);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        //table.setFocusable(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        splitPane.add(scrollPane);

        JPanel detailPanel = new JPanel(new BorderLayout());
        splitPane.add(detailPanel);
        
        // Initialize the button panel
        JPanel buttonPanel = initButtonPanel(); 
        detailPanel.add(buttonPanel, BorderLayout.NORTH);
        
        notificationDetailPanel = initNotificationDetailPanel();
        detailPanel.add(notificationDetailPanel, BorderLayout.CENTER);
        
        table.getSelectionModel().addListSelectionListener(this);        
        splitPane.setDividerLocation(300);
        
        // Update the enabled state of the buttons
        updateButtonEnabledState();
    }

    /**
     * Returns the notification type
     * @return the notification type
     */
    public abstract NotificationType getNotitficationType();
    
    /**
     * Initialize the table model
     * @return the table model
     */
    protected abstract NotificationTableModel<N> initTableModel();

    /**
     * Returns the list of notifications
     * @return the list of notifications
     */
    public List<N> getNotifications() {
        return tableModel.getNotifications();
    }
    
    /**
     * Returns the notification with the given identifier.
     * Returns null if none is found.
     * 
     * @return the notification with the given identifier
     */
    public N getNotificationById(Object id) {
        // TODO: Consider using an ID look-up map instead
        if (id != null) {
            for (N notification : getNotifications()) {
                if (id.equals(notification.getId())) {
                    return notification;
                }
            }
        }
        return null;
    }
    
    /**
     * Adds the buttons to the button panel.
     * Sub-class can override to customize the list of buttons to display
     * 
     * @param buttonPanel the button panel to add the buttons to
     * @return the button panel
     */
    protected JPanel initButtonPanel() {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, UIManager.getColor("Separator.shadow")),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        buttonPanel.add(acknowledgeBtn);
        buttonPanel.add(gotoBtn);
        buttonPanel.add(deleteBtn);
        
        acknowledgeBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                acknowledgeSelectedNotification();
            }});
        
        gotoBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                gotoSelectedNotification();
            }});
        
        deleteBtn.addActionListener(new ActionListener() {            
            @Override public void actionPerformed(ActionEvent e) {
                deleteSelectedNotification();
            }});
        
        return buttonPanel;
    }
    
    /**
     * Initialize the notification detail panel
     * @return the notification detail panel
     */
    protected abstract NotificationDetailPanel<N> initNotificationDetailPanel();    
    
    /*************************************/
    /** Selection methods               **/
    /*************************************/
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        setSelectedNotification();
        updateButtonEnabledState();
    }

    /**
     * Returns the list of selected notifications
     * @return the list of selected notifications
     */
    public List<N> getSelectedNotifications() {
        List<N> selection = new ArrayList<>();
        for (int row : table.getSelectedRows()) {
            selection.add(tableModel.getNotification(row));
        }
        return selection;
    }
    
    /**
     * Returns the selected notification if a single row is selected
     * @return the selected notification if a single row is selected
     */
    public N getSelectedNotification() {
        int[] rows = table.getSelectedRows();
        if (rows.length == 1) {
            return tableModel.getNotification(rows[0]);
        }
        return null;
    }
    
    /**
     * This method is called whenever the selection changes
     * to update the enabled state of the buttons
     */
    protected void updateButtonEnabledState() {
        N n = getSelectedNotification();
        acknowledgeBtn.setEnabled(n != null && n.isRead() && !n.isAcknowledged());
        deleteBtn.setEnabled(n != null && n.isAcknowledged());
        gotoBtn.setEnabled(n != null && n.getLocation() != null);
    }
    
    /**
     * Updates the detail panel with the currently selected notification
     */
    protected void setSelectedNotification() {
        N notification = getSelectedNotification();
        if (notification != null) {
            if (!notification.isRead()) {
                notification.setRead(true);
                table.repaint();
                notifyListeners();
            }
            notificationDetailPanel.setNotification(notification);
        } else {
            notificationDetailPanel.setNotification(null);
        }
    }
    
    /**
     * Selects the given row index
     * @param rowIndex the row to select
     */
    public void setSelectedRow(int rowIndex) {
        rowIndex = Math.min(Math.max(rowIndex, 0), table.getRowCount() - 1);
        table.getSelectionModel().setSelectionInterval(rowIndex, rowIndex);
        table.scrollRectToVisible(table.getCellRect(rowIndex, -1, true));
    }

    /**
     * Selects the notification with the given id
     * @param id the id of the notification to select
     */
    public void setSelectedId(Object id) {
        for (int row = 0; row < tableModel.getNotifications().size(); row++) {
            if (id.equals(tableModel.getNotification(row).getId())) {
                setSelectedRow(row);
                return;
            }
        }
    }
    
    /**
     * Selects the first unacknowledged row.
     * If none are found, the selection does not change.
     */
    protected void selectFirstUnacknowledgedRow() {
        // Change the selection to the next unacknowledged
        for (int row = 0; row < tableModel.getNotifications().size(); row++) {
            if (!tableModel.getNotification(row).isAcknowledged()) {
                setSelectedRow(row);
                return;
            }
        }
    }
    
    /*************************************/
    /** Action methods                  **/
    /*************************************/
    
    /**
     * Marks the currently selected notification as acknowledged
     */
    public void acknowledgeSelectedNotification() {
        acknowledgeNotification(getSelectedNotification());
    }
    
    /**
     * Marks the given notification as acknowledged
     * 
     * @param notification the notification to acknowledge
     */
    public void acknowledgeNotification(N notification) {
        if (notification != null && !notification.isAcknowledged()) {
            notification.setAcknowledged(true);
            notification.setRead(true); // Implied by acknowledged
            table.repaint();
            updateButtonEnabledState();
            selectFirstUnacknowledgedRow();
            notifyListeners();
        }    
    }
    
    /**
     * Deletes the currently selected notification
     */
    public void deleteSelectedNotification() {
        deleteNotification(getSelectedNotification());
    }
    
    /**
     * Deletes the given notification
     * 
     * @param notification the notification to acknowledge
     */
    public void deleteNotification(N notification) {
        if (notification != null) {
            tableModel.notifications.remove(notification);
            tableModel.fireTableDataChanged();
            updateButtonEnabledState();
            notifyListeners();
        }
    }

    /**
     * Zoom to the currently selected notification
     */
    public void gotoSelectedNotification() {
        N notification = getSelectedNotification();
        if (notification != null) {
            EPD.getInstance().getMainFrame().zoomToPosition(notification.getLocation());
        }
    }
    
    /**
     * This method should be called whenever the notification list should refresh 
     * itself from the back end.
     * <p>
     * It will ensure that the update happens in the Swing event tread.
     */
    public final void refreshNotifications() {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    doRefreshNotifications();
                }
            });
        } else {
            doRefreshNotifications();
        }
    }
    
    /**
     * Called whenever the notification list should refresh itself
     * from the back end.
     * <p>
     * Guaranteed to be run in the Swing event tread.
     */
    protected void doRefreshNotifications() {
        notifyListeners();
    }
    
    /**
     * This method should be called in order to add a single notification.
     * <p>
     * It will ensure that the update happens in the Swing event tread.
     */
    public final void addNotification(final N notification) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    doAddNotification(notification);
                }
            });
        } else {
            doAddNotification(notification);
        }
    }
    
    /**
     * Called in order to add a single notification.
     * <p>
     * Guaranteed to be run in the Swing event tread.
     */
    protected void doAddNotification(N notification) {
        tableModel.getNotifications().add(notification);
        tableModel.fireTableDataChanged();
        notifyListeners();
    }
    
    /**
     * Set the alignment of the given column
     * 
     * @param columnIndex the column to set the alignment for
     * @param alignment the {@linkplain JLabel} alignment constant
     */
    protected void setCellAlignment(int columnIndex, int alignment) {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);
        table.getColumnModel().getColumn(columnIndex).setCellRenderer( rightRenderer );
    }
    
    /**
     * This method will refresh the table data using
     * {@code DefaultTableModel.fireTableDataChanged()} but
     * preserve the selection
     */
    protected void refreshTableData() {
        // Store the currently selected notifications
        Set<Object> oldSelection = new HashSet<>();
        for (N notification : getSelectedNotifications()) {
            oldSelection.add(notification.get());
        }
        
        // Update the table model
        tableModel.fireTableDataChanged();
        
        // Restore the selection
        int row = 0;
        for (N notification : tableModel.getNotifications()) {
            if (oldSelection.contains(notification.get())) {
                table.addRowSelectionInterval(row, row);
            }
            row++;
        }
    }
    
    /*************************************/
    /** Listener methods                **/
    /*************************************/
    
    /**
     * Adds a listener for notification statistics
     * @param listener the listener to add
     */
    public void addListener(NotificationPanelListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a listener for notification statistics
     * @param listener the listener to remove
     */
    public void removeListener(NotificationPanelListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies all listeners with the current statistics of the notifications
     */
    protected void notifyListeners() {
        // Compute the statistics
        NotificationStatistics stats = new NotificationStatistics();
        for (N n : tableModel.getNotifications()) {
            stats.count++;
            if (!n.isRead()) { stats.unreadCount++; }
            if (!n.isAcknowledged()) { stats.unacknowledgedCount++; }
            if (n.getSeverity() == NotificationSeverity.WARNING) { stats.warningCount++; }
            if (n.getSeverity() == NotificationSeverity.WARNING && !n.isAcknowledged()) { stats.unacknowledgedWarningCount++; }
            if (n.getSeverity() == NotificationSeverity.ALERT) { stats.alertCount++; }
            if (n.getSeverity() == NotificationSeverity.ALERT && !n.isAcknowledged()) { stats.unacknowledgedAlertCount++; }
        }
        
        // Notify listeners
        for (NotificationPanelListener listener : listeners) {
            listener.notificationsUpdated(stats);
        }
    }
    
    /*************************************/
    /** Helper classes                  **/
    /*************************************/
    
    /**
     * Contains statistics about the list of notifications
     */
    public static class NotificationStatistics {
        public int count;
        public int unreadCount;
        public int unacknowledgedCount;
        public int warningCount;
        public int unacknowledgedWarningCount;
        public int alertCount;
        public int unacknowledgedAlertCount;
    }
    
    /**
     * Listener interface
     */
    public interface NotificationPanelListener {
        void notificationsUpdated(NotificationStatistics stats);
    }
}
