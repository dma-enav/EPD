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
package dk.dma.epd.shore.gui.voct;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.shore.ais.AisHandler;

public class AISSelectionList extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;
    List<String> targetList;
    JList<String> list;
    DefaultListModel<String> listModel;
    private SRUAddEditDialog sruAddEditDialog;
    private JButton btnUseSelected;

    public AISSelectionList(AisHandler aisHandler, Point point,
            SRUAddEditDialog sruAddEditDialog) {
        setTitle("AIS Targets");
        this.setModal(true);
        this.setResizable(false);
        this.sruAddEditDialog = sruAddEditDialog;

        double x = point.getX() + 471;

        setBounds((int) x, 100, 200, 364);

        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        btnUseSelected = new JButton("Use Selected");
        btnUseSelected.addActionListener(this);
        panel.add(btnUseSelected);

        listModel = new DefaultListModel<String>();

        list = new JList<String>(listModel);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(list);

        getContentPane().add(scrollPane, BorderLayout.CENTER);

        Map<Long, VesselTarget> vesselMap = aisHandler.getVesselTargets();
        targetList = new ArrayList<String>();

        for (Entry<Long, VesselTarget> entry : vesselMap.entrySet()) {
            String mmsi = " (" + String.valueOf(entry.getKey()) + ")";

            String name;

            VesselTarget vesselTarget = entry.getValue();
            if (vesselTarget.getStaticData() != null) {
                if (vesselTarget.getStaticData().getName() != null) {
                    name = AisMessage.trimText(vesselTarget.getStaticData()
                            .getName());

                } else {
                    name = "N/A";
                }
            } else {
                name = "N/A";
            }

            targetList.add(name + mmsi);

        }

        Collections.sort(targetList);

        for (int i = 0; i < targetList.size(); i++) {
            listModel.addElement(targetList.get(i));
        }

        this.setVisible(true);
    }


//    @Override
//    public void dispose() {
//        super.dispose();
//
//        
//        if (list.getSelectedIndex() < 0){
//            //No target selected
//            sruAddEditDialog.setMmsi(-1);            
//        }
//        
//
//    }


    @Override
    public void actionPerformed(ActionEvent arg0) {

        if (list.getSelectedIndex() > -1){
            String selectedTarget = list.getSelectedValue();
//            for (int i = 0; i < selectedTarget.split("\\(").length; i++) {
//                System.out.println(selectedTarget.split("\\(")[i]);
//            }
            String mmsi = selectedTarget.split("\\(")[1];
            mmsi = mmsi.substring(0, mmsi.length()-1);
            
            sruAddEditDialog.setMmsi(Long.valueOf(mmsi));
            dispose();
            
        }
        
    }

}
