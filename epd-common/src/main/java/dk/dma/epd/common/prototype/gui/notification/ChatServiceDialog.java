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

import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.BOTH;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.NORTHWEST;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;
import net.maritimecloud.net.service.ServiceEndpoint;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dk.dma.epd.common.prototype.notification.NotificationAlert.AlertType;
import dk.dma.epd.common.prototype.notification.NotificationAlert;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;

/**
 * Main chat service dialog
 */
public class ChatServiceDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    JComboBox<ChatServiceTarget> targetComboBox = new JComboBox<>();
    JTextField senderNameTxt = new JTextField();
    JTextArea messageTxt = new JTextArea();
    JButton sendBtn = new JButton("Send");
    JButton cancelBtn = new JButton("Cancel");
    
    JComboBox<NotificationSeverity> severityComboBox = new JComboBox<>(NotificationSeverity.values());
    JCheckBox alertPopUp = new JCheckBox("Pop-up");
    JCheckBox alertSystemTray = new JCheckBox("System tray");
    JCheckBox alertBeep = new JCheckBox("Beep");

    /**
     * Constructor
     */
    public ChatServiceDialog(Window parent) {
        super(parent, "Send Message", Dialog.ModalityType.APPLICATION_MODAL);

        setBounds(100, 100, 320, 350);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        
        // Support of test mode
        if (EPD.getInstance() != null) {
            setLocationRelativeTo(EPD.getInstance().getMainFrame());        
            sendBtn.setIcon(EPD.res().getCachedImageIcon("images/buttons/ok.png"));    
            cancelBtn.setIcon(EPD.res().getCachedImageIcon("images/buttons/cancel.png"));    
        }
        
        initGUI();
    }

    /**
     * Set up the graphical user interface
     */
    protected void initGUI() {
        JPanel content = new JPanel(new BorderLayout());
        getContentPane().add(content);
        
        // *** Target panel
        JPanel targetPanel = new JPanel(new GridBagLayout());
        targetPanel.setBorder(new TitledBorder("Target"));
        content.add(targetPanel, BorderLayout.NORTH);
        
        Insets insets5  = new Insets(5, 5, 5, 5);
        int gridY = 0;        
        targetPanel.add(new JLabel("MMSI:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(targetComboBox, new GridBagConstraints(1, gridY++, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        targetPanel.add(new JLabel("Type:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(severityComboBox, new GridBagConstraints(1, gridY++, 3, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));

        targetPanel.add(new JLabel("Action:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(alertPopUp, new GridBagConstraints(1, gridY, 1, 1, 1.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(alertBeep, new GridBagConstraints(2, gridY, 1, 1, 1.0, 0.0, WEST, NONE, insets5, 0, 0));
        targetPanel.add(alertSystemTray, new GridBagConstraints(3, gridY++, 1, 1, 1.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        // *** Message panel
        JPanel messagePanel = new JPanel(new GridBagLayout());
        messagePanel.setBorder(new TitledBorder("Message"));
        content.add(messagePanel, BorderLayout.CENTER);
        
        gridY = 0;
        messagePanel.add(new JLabel("Sender:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        messagePanel.add(senderNameTxt, new GridBagConstraints(1, gridY++, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        
        messagePanel.add(new JLabel("Message:"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, NORTHWEST, NONE, insets5, 0, 0));
        messagePanel.add(new JScrollPane(messageTxt), new GridBagConstraints(1, gridY++, 1, 1, 1.0, 1.0, WEST, BOTH, insets5, 0, 0));

        // *** Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(insets5));
        content.add(buttonPanel, BorderLayout.SOUTH);
        
        sendBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        buttonPanel.add(sendBtn);
        buttonPanel.add(cancelBtn);
        
        getRootPane().setDefaultButton(cancelBtn);
    }
    
    /**
     * Users of the {@code ChatServiceDialog} should
     * use this method to initialize and display the dialog.
     * 
     * @param id the selected maritime id
     */
    public void init(final MaritimeId id) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {
                loadData();
                setSelectedMaritimeId(id);
                setVisible(true);
            }
        });
    }
    
    /**
     * Users of the {@code ChatServiceDialog} should
     * use this method to initialize and display the dialog
     */
    public void init() {
        ChatServiceTarget target = (ChatServiceTarget)targetComboBox.getSelectedItem();
        MaritimeId id = (target == null) ? null : target.getId();
        init(id);
    }
    
    /**
     * Sets the selected maritime id
     * @param id the selected maritime id
     */
    public void setSelectedMaritimeId(MaritimeId id) {
        int selectedIndex = -1;
        if (id != null) {
            for (int x = 0; x < targetComboBox.getItemCount(); x++) {
                ChatServiceTarget target = targetComboBox.getItemAt(x);
                if (id.equals(target.getId())) {
                    selectedIndex = x;
                    break;
                }
            }
        }
        targetComboBox.setSelectedIndex(selectedIndex);
    }

    /**
     * Initializes the panel
     */
    private void loadData() {
        
        AisHandlerCommon aisHandler = EPD.getInstance().getAisHandler();
        
        targetComboBox.removeAllItems();
        for (ServiceEndpoint<ChatServiceMessage, Void> service : 
                EPD.getInstance().getChatServiceHandler().getChatServiceList()) {
            Integer mmsi = MaritimeCloudUtils.toMmsi(service.getId());
            if (mmsi == null) {
                continue;
            }
            String name = null;
            VesselTarget selectedShip = aisHandler.getVesselTarget(mmsi.longValue());
            if (selectedShip != null && selectedShip.getStaticData() != null) {
                name = selectedShip.getStaticData().getTrimmedName();
            }
            targetComboBox.addItem(new ChatServiceTarget(service.getId(), name));
        }
        
        messageTxt.setText("");
    }
    
    /**
     * Called when a button is clicked
     */
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == sendBtn) {
            
            List<NotificationAlert> alerts = new ArrayList<>();
            if (alertBeep.isSelected()) {
                alerts.add(new NotificationAlert(AlertType.BEEP));
            }
            if (alertSystemTray.isSelected()) {
                alerts.add(new NotificationAlert(AlertType.SYSTEM_TRAY));
            }
            if (alertPopUp.isSelected()) {
                alerts.add(new NotificationAlert(AlertType.POPUP));
            }
            
            if (targetComboBox.getSelectedItem() != null) {
                ChatServiceTarget target = (ChatServiceTarget)targetComboBox.getSelectedItem();
                EPD.getInstance().getChatServiceHandler().sendChatMessage(
                        target.getId(), 
                        messageTxt.getText(), 
                        senderNameTxt.getText(),
                        (NotificationSeverity)severityComboBox.getSelectedItem(),
                        alerts);
            }
            
            setVisible(false);
        } else if (ae.getSource() == cancelBtn) {
            setVisible(false);
        }
    }
    
    /**
     * Test method
     */
    public static void main(String... args) {
        ChatServiceDialog d = new ChatServiceDialog(null);
        d.targetComboBox.addItem(new ChatServiceTarget(new MmsiId(1234873298), "Kurt"));
        d.targetComboBox.addItem(new ChatServiceTarget(new MmsiId(99927834), null));
        d.targetComboBox.addItem(new ChatServiceTarget(new MmsiId(2343254), "Mercandia IV"));
        d.targetComboBox.setSelectedIndex(-1);
        d.setVisible(true);
    }
    
    /**
     * Helper class that defines a chat service target
     */
    static class ChatServiceTarget {
        MaritimeId id;
        String name;
        
        public ChatServiceTarget(MaritimeId id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public MaritimeId getId() {
            return id;
        }

        public String getName() {
            return name;
        }        
        
        @Override
        public String toString() {
            StringBuilder str = new StringBuilder();
            str.append(MaritimeCloudUtils.toMmsi(id)); 
            if (name != null) {
                str.append(" - ").append(name);
            } else if (MaritimeCloudUtils.isSTCC(id)) {
                str.append(" - STCC");
            }
            return str.toString();
        }
    }
    
}
