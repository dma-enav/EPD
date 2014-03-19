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
package dk.dma.epd.ship.gui.identity;

import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.ComponentDialog;
import dk.dma.epd.common.prototype.model.identity.MaritimeIdentity;
import dk.dma.epd.ship.EPDShip;

/**
 * Sends a strategic route to an STCC
 */
public class IdentityViewer extends ComponentDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    JLabel idTxtLbl;
    JLabel nameTxtLbl;
    JLabel roleTxtLbl;
    JLabel affTxtLbl;
    JLabel descripnTxtLbl;

    private JComboBox<String> serviceListComboBox = new JComboBox<>();

    private JButton cancelBtn = new JButton("Close", EPDShip.res().getCachedImageIcon("images/buttons/cancel.png"));

    private final JButton btnViewIdentityInfo = new JButton("View Info", EPDShip.res().getCachedImageIcon(
            "images/buttons/information.png"));

    /**
     * Create the frame.
     */
    public IdentityViewer(JFrame frame, MaritimeIdentity identity) {
        super(frame, "Maritime Identity", Dialog.ModalityType.MODELESS);
        setTitle("Maritime Identity");

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocation(100, 100);

        initGUI();

        // Hook up enter key to send and escape key to cancel
        getRootPane().setDefaultButton(cancelBtn);
        getRootPane()
                .registerKeyboardAction(this, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);

        pack();

        loadValues(identity);
    }

    public void loadValues(MaritimeIdentity identity) {
        idTxtLbl.setText(String.valueOf(identity.getMaritimeID()));

        nameTxtLbl.setText(identity.getName());

        roleTxtLbl.setText(identity.getRole().getDescription());

        affTxtLbl.setText(identity.getAffiliation());

        descripnTxtLbl.setText(identity.getDescription());

        serviceListComboBox.removeAllItems();

        for (int i = 0; i < identity.getServices().size(); i++) {
            serviceListComboBox.addItem(identity.getServices().get(i).getName());
        }

    }

    /**
     * Initialize the GUI
     */
    /**
     * Initialize the GUI
     */
    public void initGUI() {

        JPanel content = new JPanel(new GridBagLayout());
        setContentPane(content);
        Insets insets5 = new Insets(5, 5, 5, 5);

        // *******************
        // *** Target panel
        // *******************
        JPanel stccPanel = new JPanel(new GridBagLayout());
        stccPanel.setBorder(new TitledBorder("Identity Details:"));
        content.add(stccPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets5, 0, 0));
        JLabel lblMaritimeId = new JLabel("Maritime ID:");
        stccPanel.add(lblMaritimeId, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));

        idTxtLbl = new JLabel("N/A");
        GridBagConstraints gbc_idTxtLbl = new GridBagConstraints();
        gbc_idTxtLbl.fill = GridBagConstraints.HORIZONTAL;
        gbc_idTxtLbl.insets = insets5;
        gbc_idTxtLbl.gridx = 1;
        gbc_idTxtLbl.gridy = 0;
        stccPanel.add(idTxtLbl, gbc_idTxtLbl);

        JLabel lblName = new JLabel("Name:");
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.anchor = GridBagConstraints.WEST;
        gbc_lblName.insets = insets5;
        gbc_lblName.gridx = 0;
        gbc_lblName.gridy = 1;
        stccPanel.add(lblName, gbc_lblName);

        nameTxtLbl = new JLabel("N/A");
        GridBagConstraints gbc_nameTxtLbl = new GridBagConstraints();
        gbc_nameTxtLbl.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTxtLbl.insets = insets5;
        gbc_nameTxtLbl.gridx = 1;
        gbc_nameTxtLbl.gridy = 1;
        stccPanel.add(nameTxtLbl, gbc_nameTxtLbl);

        JLabel lblRole = new JLabel("Role:");
        GridBagConstraints gbc_lblRole = new GridBagConstraints();
        gbc_lblRole.anchor = GridBagConstraints.WEST;
        gbc_lblRole.insets = insets5;
        gbc_lblRole.gridx = 0;
        gbc_lblRole.gridy = 2;
        stccPanel.add(lblRole, gbc_lblRole);

        roleTxtLbl = new JLabel("N/A");
        GridBagConstraints gbc_roleTxtLbl = new GridBagConstraints();
        gbc_roleTxtLbl.fill = GridBagConstraints.HORIZONTAL;
        gbc_roleTxtLbl.insets = insets5;
        gbc_roleTxtLbl.gridx = 1;
        gbc_roleTxtLbl.gridy = 2;
        stccPanel.add(roleTxtLbl, gbc_roleTxtLbl);

        JLabel lblAffiliation = new JLabel("Affiliation:");
        GridBagConstraints gbc_lblAffiliation = new GridBagConstraints();
        gbc_lblAffiliation.anchor = GridBagConstraints.WEST;
        gbc_lblAffiliation.insets = insets5;
        gbc_lblAffiliation.gridx = 0;
        gbc_lblAffiliation.gridy = 3;
        stccPanel.add(lblAffiliation, gbc_lblAffiliation);

        affTxtLbl = new JLabel("N/A");
        GridBagConstraints gbc_affTxtLbl = new GridBagConstraints();
        gbc_affTxtLbl.fill = GridBagConstraints.HORIZONTAL;
        gbc_affTxtLbl.insets = insets5;
        gbc_affTxtLbl.gridx = 1;
        gbc_affTxtLbl.gridy = 3;
        stccPanel.add(affTxtLbl, gbc_affTxtLbl);

        JLabel lblDescription = new JLabel("Description:");
        GridBagConstraints gbc_lblDescription = new GridBagConstraints();
        gbc_lblDescription.anchor = GridBagConstraints.WEST;
        gbc_lblDescription.insets = insets5;
        gbc_lblDescription.gridx = 0;
        gbc_lblDescription.gridy = 4;
        stccPanel.add(lblDescription, gbc_lblDescription);

        descripnTxtLbl = new JLabel("N/A");
        GridBagConstraints gbc_descripnTxtLbl = new GridBagConstraints();
        gbc_descripnTxtLbl.fill = GridBagConstraints.HORIZONTAL;
        gbc_descripnTxtLbl.insets = insets5;
        gbc_descripnTxtLbl.gridx = 1;
        gbc_descripnTxtLbl.gridy = 4;
        stccPanel.add(descripnTxtLbl, gbc_descripnTxtLbl);

        JLabel lblServicesAvailable = new JLabel("Services Available:");
        GridBagConstraints gbc_lblServicesAvailable = new GridBagConstraints();
        gbc_lblServicesAvailable.anchor = GridBagConstraints.WEST;
        gbc_lblServicesAvailable.insets = insets5;
        gbc_lblServicesAvailable.gridx = 0;
        gbc_lblServicesAvailable.gridy = 5;
        stccPanel.add(lblServicesAvailable, gbc_lblServicesAvailable);

        serviceListComboBox.addActionListener(this);
        stccPanel.add(serviceListComboBox, new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, new Insets(5, 5, 5, 0),
                0, 0));

        GridBagConstraints gbc_btnViewIdentityInfo = new GridBagConstraints();
        gbc_btnViewIdentityInfo.anchor = GridBagConstraints.EAST;
        gbc_btnViewIdentityInfo.gridx = 1;
        gbc_btnViewIdentityInfo.gridy = 6;
        stccPanel.add(btnViewIdentityInfo, gbc_btnViewIdentityInfo);
        btnViewIdentityInfo.setEnabled(false);
        btnViewIdentityInfo.addActionListener(this);

        btnViewIdentityInfo.setVisible(false);
        
        // *******************
        // *** Send panel
        // *******************
        JPanel sendPanel = new JPanel();
        content.add(sendPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, CENTER, HORIZONTAL, new Insets(5, 5, 0, 0), 0, 0));
        cancelBtn.addActionListener(this);
        sendPanel.add(cancelBtn);

        
        
        this.setMinimumSize(new Dimension(350, 265));
        this.setPreferredSize(new Dimension(350, 265));
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {

    }

    /**
     * Called when one of the buttons are clicked or if one of the combo-boxes changes value
     */
    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

}
