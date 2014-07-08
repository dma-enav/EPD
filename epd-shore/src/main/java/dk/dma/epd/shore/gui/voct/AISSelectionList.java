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
package dk.dma.epd.shore.gui.voct;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import dk.dma.ais.virtualnet.common.table.TargetTableEntry;
import dk.dma.ais.virtualnet.transponder.gui.SelectTargetList;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.shore.ais.AisHandler;

public class AISSelectionList extends JDialog implements ActionListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;

    private Integer selectedTarget;
    private final SelectTargetList list = new SelectTargetList();
    private final JButton selectButton = new JButton("Select");
    private final JButton cancelButton = new JButton("Cancel");
    SRUAddEditDialog sruAddEditDialog;

    public AISSelectionList(AisHandler aisHandler, Point point, SRUAddEditDialog sruAddEditDialog) {
        setTitle("AIS Targets");
        this.setModal(true);
        setResizable(false);
        double x = point.getX() + 471;

        setBounds((int) x, 100, 200, 364);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        this.sruAddEditDialog = sruAddEditDialog;

        JPanel btnPanel = new JPanel();
        selectButton.setPreferredSize(new Dimension(75, 28));
        selectButton.setEnabled(false);
        selectButton.addActionListener(this);
        btnPanel.add(selectButton);
        cancelButton.setPreferredSize(new Dimension(75, 28));
        cancelButton.addActionListener(this);
        btnPanel.add(cancelButton);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);

        list.addListSelectionListener(this);
        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        listScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(listScroller, BorderLayout.CENTER);

        getContentPane().add(list.getFilterField(), BorderLayout.NORTH);
        list.getFilterField().setPreferredSize(new Dimension(294, 24));

        Map<Long, VesselTarget> vesselMap = aisHandler.getVesselTargets();

        for (Entry<Long, VesselTarget> entry : vesselMap.entrySet()) {
            // String mmsi = " (" + String.valueOf(entry.getKey()) + ")";

            String name;

            VesselTarget vesselTarget = entry.getValue();
            if (vesselTarget.getStaticData() != null) {
                if (vesselTarget.getStaticData().getName() != null) {
                    name = vesselTarget.getStaticData().getTrimmedName();

                } else {
                    name = "N/A";
                }
            } else {
                name = "N/A";
            }
            TargetTableEntry tableEntry = new TargetTableEntry();
            tableEntry.setMmsi(entry.getKey().intValue());
            tableEntry.setName(name);
            // targetList.add(name + mmsi);
            list.addTarget(tableEntry);
        }

        // Hitting the escape key should simulate clicking "Cancel"
        ActionListener escAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelButton.doClick();
            }
        };
        getRootPane().registerKeyboardAction(escAction, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Hitting the enter key should simulate clicking "Select"
        ActionListener enterAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectButton.doClick();
            }
        };
        getRootPane().registerKeyboardAction(enterAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        this.setVisible(true);
    }

    public Integer getSelectedTarget() {
        return selectedTarget;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == cancelButton) {
            selectedTarget = null;
            this.setVisible(false);
        } else if (e.getSource() == selectButton) {

            if (list.getSelectedIndex() > -1) {
                TargetTableEntry selectedTarget = list.getSelectedValue();
                // for (int i = 0; i < selectedTarget.split("\\(").length; i++) {
                // System.out.println(selectedTarget.split("\\(")[i]);
                // }
                // String mmsi = selectedTarget.getMmsi()+"";
                // mmsi = mmsi.substring(0, mmsi.length()-1);

                sruAddEditDialog.setMmsi(selectedTarget.getMmsi());
                dispose();

            }

            // if (list.getSelectedIndex() >= 0) {
            // this.selectedTarget = list.getSelectedValue().getMmsi();
            // this.setVisible(false);
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() == list) {
            selectButton.setEnabled(list.getSelectedIndex() >= 0);
        }

    }

    // @Override
    // public void dispose() {
    // super.dispose();
    //
    //
    // if (list.getSelectedIndex() < 0){
    // //No target selected
    // sruAddEditDialog.setMmsi(-1);
    // }
    //
    //
    // }

    // @Override
    // public void actionPerformed(ActionEvent arg0) {
    //
    // if (list.getSelectedIndex() > -1){
    // String selectedTarget = list.getSelectedValue();
    // // for (int i = 0; i < selectedTarget.split("\\(").length; i++) {
    // // System.out.println(selectedTarget.split("\\(")[i]);
    // // }
    // String mmsi = selectedTarget.split("\\(")[1];
    // mmsi = mmsi.substring(0, mmsi.length()-1);
    //
    // sruAddEditDialog.setMmsi(Long.valueOf(mmsi));
    // dispose();
    //
    // }
    //
    // }

}
