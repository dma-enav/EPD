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
package dk.dma.epd.common.prototype.gui.route;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DateEditor;
import javax.swing.SpinnerDateModel;
import javax.swing.WindowConstants;
import javax.swing.text.DateFormatter;

import org.jdesktop.swingx.JXDatePicker;

import dk.dma.epd.common.prototype.model.route.Route.EtaAdjust;
import dk.dma.epd.common.prototype.model.route.Route.EtaAdjustType;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Dialog for editing an ETA
 * 
 * @author oleborup
 * 
 */
public class EtaEditDialog extends JDialog implements ActionListener {
    private static final long serialVersionUID = 1L;
    
    static final String TITLE = "ETA edit";
    
    Date eta;
    JLabel lblEta = new JLabel("ETA");
    JButton btnSave = new JButton("Save");
    JButton btnCancel = new JButton("Cancel");
    ButtonGroup adjustGroup;
    JXDatePicker etaDatePicker;
    JSpinner etaTimeSpinner;
    JRadioButton adjust1;
    JRadioButton adjust2;
    JRadioButton adjust3;

    public EtaEditDialog(JFrame owner, Date eta, String wpName) {
        super(owner, TITLE + ": " + wpName, true);
        setLocationRelativeTo(owner);
        this.eta = eta;
        initGUI();
    }

    public EtaEditDialog(Dialog owner, Date eta, String wpName) {
        super(owner, TITLE + ": " + wpName, true);
        setLocationRelativeTo(owner);
        this.eta = eta;
        initGUI();
    }

    private void initGUI() {
        setSize(250, 160);
        setResizable(false);
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        etaDatePicker = new JXDatePicker(eta);
        etaDatePicker.setFormats(new SimpleDateFormat("E dd/MM/yyyy"));
        etaTimeSpinner = new JSpinner(new SpinnerDateModel(eta, null, null, Calendar.HOUR_OF_DAY));
        DateEditor editor = new JSpinner.DateEditor(etaTimeSpinner, "HH:mm");
        DateFormatter formatter = (DateFormatter)editor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false);
        formatter.setOverwriteMode(true);
        formatter.setCommitsOnValidEdit(true);
        etaTimeSpinner.setEditor(editor);

        lblEta.setBounds(6, 6, 37, 16);
        getContentPane().add(lblEta);

        etaDatePicker.setBounds(38, 6, 103, 16);
        getContentPane().add(etaDatePicker);

        etaTimeSpinner.setBounds(153, 6, 71, 16);
        getContentPane().add(etaTimeSpinner);

        adjust1 = new JRadioButton("Adjust leg in and out speeds");
        adjust2 = new JRadioButton("Adjust all ETA's");
        adjust3 = new JRadioButton("Adjust speeds fixed start and end time");
        adjustGroup = new ButtonGroup();
        adjustGroup.add(adjust1);
        adjustGroup.add(adjust2);
        adjustGroup.add(adjust3);

        adjust1.setBounds(6, 34, 250, 16);
        getContentPane().add(adjust1);

        adjust2.setBounds(6, 56, 250, 16);
        getContentPane().add(adjust2);

        adjust3.setBounds(6, 78, 250, 16);
        getContentPane().add(adjust3);

        adjust3.setSelected(true);

        btnSave.setBounds(6, 106, 75, 16);
        btnSave.addActionListener(this);
        getContentPane().add(btnSave);

        btnCancel.setBounds(86, 106, 71, 16);
        btnCancel.addActionListener(this);
        getContentPane().add(btnCancel);

    }

    public EtaAdjust getEtaAdjust() {
        setVisible(true);
        if (eta == null) {
            return null;
        }
        return new EtaAdjust(eta, getAdjustType());
    }
    
    public EtaAdjust getEtaAdjust(int x, int y) {
        setLocation(x, y);
        return getEtaAdjust();
    }

    public EtaAdjustType getAdjustType() {
        if (adjust1.isSelected()) {
            return EtaAdjustType.ADJUST_ADJACENT_LEG_SPEEDS;
        }
        if (adjust2.isSelected()) {
            return EtaAdjustType.ADJUST_ALL_ETA;
        }
        return EtaAdjustType.ADJUST_FIXED_START_AND_END;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnSave) {
            eta = ParseUtils.combineDateTime(etaDatePicker.getDate(), (Date) etaTimeSpinner.getValue());
        } else {
            eta = null;
        }
        setVisible(false);
    }
    
    public static void main(String... args) {
        EtaEditDialog dialog = new EtaEditDialog((JFrame)null, new Date(), "Kurt");
        dialog.setVisible(true);
    }
}
