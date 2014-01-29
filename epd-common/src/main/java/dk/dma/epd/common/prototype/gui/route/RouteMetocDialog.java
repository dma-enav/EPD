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
package dk.dma.epd.common.prototype.gui.route;

import java.awt.Color;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.StringWriter;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.communication.webservice.ShoreServiceException;
import dk.dma.epd.common.prototype.gui.metoc.MetocRequestDialog;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteMetocSettings;
import dk.dma.epd.common.prototype.monalisa.XMLDialog;
import dk.dma.epd.common.prototype.route.RouteManagerCommon;
import dk.dma.epd.common.prototype.shoreservice.Metoc;
import dk.dma.epd.common.text.Formatter;
import dk.frv.enav.common.xml.metoc.MetocDataTypes;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.request.MetocForecastRequest;
/**
 * Dialog with METOC settings 
 */
public class RouteMetocDialog extends JDialog implements ActionListener, FocusListener  {

    private static final long serialVersionUID = 1L;
    
    private JTextField windLimit;
    private JTextField currentLimit;
    private JTextField waveLimit;
    private JCheckBox showCheckbox;
    private JLabel currentLabel;
    private JLabel currentMetocDataLbl;
    private JLabel intervalLbl;
    private JComboBox<String> intervalDb;
    private JButton requestBtn;
    private JCheckBox windCb;
    private JCheckBox currentCb;
    private JCheckBox wavesCb;
    private JCheckBox seaLevelCb;
    private JCheckBox densityCb;
    private JLabel windLimitLbl;
    private JLabel currentLimitLbl;
    private JLabel waveLimitLbl;
    private JPanel statusPanel;
    private JPanel typesPanel;
    private JPanel warnLimitsPanel;
    
    JButton closeBtn;
    
    private RouteManagerCommon routeManager;
    private Route route;
    private JPanel providerPanel;
    private JComboBox<String> providerBox;
    private JCheckBox chckbxShowRawRequest;



    public RouteMetocDialog(Window parent, RouteManagerCommon routeManager, int routeId) {
        super(parent, "Route METOC properties", Dialog.ModalityType.APPLICATION_MODAL);
        
        this.routeManager = routeManager;
        if (routeManager.isActiveRoute(routeId)) {
            route = routeManager.getActiveRoute();
        } else {
            route = routeManager.getRoute(routeId);
        }
        
        setSize(270, 517);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);
                
        initGui();
        updateFields();
    }
    
    private void updateFields() {
        if (route.getRouteMetocSettings() == null) {
            // Set default settings
            route.setRouteMetocSettings(routeManager.getDefaultRouteMetocSettings());
        }
        RouteMetocSettings metocSettings = route.getRouteMetocSettings();
        MetocForecast metocForecast = route.getMetocForecast();
        
        // Enabled or not
        if (metocForecast == null) {
            currentMetocDataLbl.setText("None");
        } else {
            currentMetocDataLbl.setText(Formatter.formatLongDateTime(metocForecast.getCreated()));            
        }
        showCheckbox.setSelected(metocSettings.isShowRouteMetoc());
        
        // Interval
        intervalDb.getModel().setSelectedItem(Integer.toString(metocSettings.getInterval()));
        
        // METOC data
        windCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.WI));
        currentCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.CU));
        wavesCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.WA));
        seaLevelCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.SE));
        densityCb.setSelected(metocSettings.getDataTypes().contains(MetocDataTypes.DE));
        
        // Warn limits
        windLimit.setText(String.format("%.2f", metocSettings.getWindWarnLimit()));
        windLimit.addFocusListener(this);
        currentLimit.setText(String.format("%.2f", metocSettings.getCurrentWarnLimit()));
        currentLimit.addFocusListener(this);
        waveLimit.setText(String.format("%.2f", metocSettings.getWaveWarnLimit()));
        waveLimit.addFocusListener(this);
        
        //show raw xml
        if (chckbxShowRawRequest.isSelected()) {
            try {
                MetocForecastRequest req = Metoc.generateMetocRequest(route, EPD.getInstance().getPosition());
                
                JAXBContext context = JAXBContext.newInstance(MetocForecastRequest.class);
                Marshaller m = context.createMarshaller();
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
                StringWriter st = new StringWriter();
                
                m.marshal(req, st);
                
                new XMLDialog(st.toString(), "RAW XML Request").setVisible(true);
                
            } catch (ShoreServiceException | JAXBException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    
    void saveValues() {
        RouteMetocSettings metocSettings = route.getRouteMetocSettings();
        
        metocSettings.setShowRouteMetoc(showCheckbox.isSelected());
        metocSettings.setInterval(Integer.parseInt((String)intervalDb.getSelectedItem()));
        
        Set<MetocDataTypes> dataTypes = metocSettings.getDataTypes();
        dataTypes.clear();
        if (windCb.isSelected()) {
            dataTypes.add(MetocDataTypes.WI);
        }
        if (currentCb.isSelected()) {
            dataTypes.add(MetocDataTypes.CU);
        }
        if (wavesCb.isSelected()) {
            dataTypes.add(MetocDataTypes.WA);
        }
        if (seaLevelCb.isSelected()) {
            dataTypes.add(MetocDataTypes.SE);
        }
        if (densityCb.isSelected()) {
            dataTypes.add(MetocDataTypes.DE);
        }
        
        metocSettings.setWindWarnLimit(parseFieldVal(windLimit, metocSettings.getWindWarnLimit()));
        metocSettings.setCurrentWarnLimit(parseFieldVal(currentLimit, metocSettings.getCurrentWarnLimit()));
        metocSettings.setWaveWarnLimit(parseFieldVal(waveLimit, metocSettings.getWaveWarnLimit()));
        
        metocSettings.setProvider((String)providerBox.getSelectedItem());
    }
    
    private void requestMetoc() {
        showCheckbox.setSelected(true);
        saveValues();        
        MetocRequestDialog.requestMetoc(this, routeManager, route);
        updateFields();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeBtn) {
            saveValues();
            dispose();
        } else if (e.getSource() == requestBtn) {
            requestMetoc();            
        }
        
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        if (!(e.getSource() instanceof JTextField)) {
            return;
        }
        RouteMetocSettings metocSettings = route.getRouteMetocSettings();
        if (e.getSource() == windLimit) {
            parseFieldVal(windLimit, metocSettings.getWindWarnLimit());
        }
        if (e.getSource() == currentLimit) {
            parseFieldVal(currentLimit, metocSettings.getCurrentWarnLimit());
        }
        if (e.getSource() == waveLimit) {
            parseFieldVal(waveLimit, metocSettings.getWaveWarnLimit());
        }
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        
    }
    
    private static Double parseFieldVal(JTextField field, Double defaultVal) {
        Double val = defaultVal;
        String strVal = field.getText();
        // Be relaxed
        strVal = strVal.replace(',', '.');
        try {
            val = Double.parseDouble(strVal);                        
        } catch (NumberFormatException e) { }
        field.setText(String.format("%.2f", val));
        return val; 
    }
    
    
    private void initGui() {
        showCheckbox = new JCheckBox("Show route METOC (if available)");
        showCheckbox.setSelected(true);
        showCheckbox.setEnabled(true);  
        currentLabel = new JLabel("Current METOC data:");        
        currentMetocDataLbl = new JLabel("None");
        currentMetocDataLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));        
        intervalLbl = new JLabel("Point interval (min)");        
        intervalDb = new JComboBox<>();
        intervalDb.setModel(new DefaultComboBoxModel<>(new String[] {"15", "30", "45", "60"}));
        intervalDb.setSelectedIndex(0);
        intervalDb.setMaximumRowCount(4);        
        requestBtn = new JButton("Request METOC");
        requestBtn.addActionListener(this);
        closeBtn = new JButton("Close");
        closeBtn.addActionListener(this);
        
        windCb = new JCheckBox("Wind");
        currentCb = new JCheckBox("Current");
        wavesCb = new JCheckBox("Waves");
        seaLevelCb = new JCheckBox("Sea Level");
        densityCb = new JCheckBox("Density");
        

        windLimitLbl = new JLabel("Wind speed m/s");
        currentLimitLbl = new JLabel("Current speed kn");
        waveLimitLbl = new JLabel("Mean wave height m");
        windLimit = new JTextField();
        windLimit.setColumns(10);
        currentLimit = new JTextField();
        currentLimit.setColumns(10);
        waveLimit = new JTextField();
        waveLimit.setColumns(10);
        
        

        statusPanel = new JPanel();
        

        
        statusPanel.setBorder(new TitledBorder(null, "METOC status", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      
        typesPanel = new JPanel();
        typesPanel.setBorder(new TitledBorder(null, "METOC data", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));        
        warnLimitsPanel = new JPanel();
        warnLimitsPanel.setBorder(new TitledBorder(null, "Warn limits", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        providerPanel = new JPanel();
        providerPanel.setBorder(new TitledBorder(null, "METOC provider", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
        
        providerBox = new JComboBox<String>();
        providerBox.addItem("dmi");
        providerBox.addItem("fco");
        providerBox.addActionListener(this);
        providerBox.addFocusListener(this);
      
        
        providerBox.setMaximumRowCount(2);
        GroupLayout gl_providerPanel = new GroupLayout(providerPanel);
        gl_providerPanel.setHorizontalGroup(
            gl_providerPanel.createParallelGroup(Alignment.LEADING)
                .addComponent(providerBox, 0, 247, Short.MAX_VALUE)
        );
        gl_providerPanel.setVerticalGroup(
            gl_providerPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_providerPanel.createSequentialGroup()
                    .addComponent(providerBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        providerPanel.setLayout(gl_providerPanel);
        
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap(190, Short.MAX_VALUE)
                    .addComponent(closeBtn, GroupLayout.PREFERRED_SIZE, 69, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(providerPanel, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap())
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(typesPanel, GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(warnLimitsPanel, GroupLayout.PREFERRED_SIZE, 259, Short.MAX_VALUE)
                    .addContainerGap())
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, 266, Short.MAX_VALUE)
                    .addGap(3))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGap(7)
                    .addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, 164, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(providerPanel, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(typesPanel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(warnLimitsPanel, GroupLayout.PREFERRED_SIZE, 111, GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeBtn)
                    .addGap(17))
        );
        
        GroupLayout gl_warnLimitsPanel = new GroupLayout(warnLimitsPanel);
        gl_warnLimitsPanel.setHorizontalGroup(
            gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_warnLimitsPanel.createSequentialGroup()
                    .addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING)
                            .addGroup(gl_warnLimitsPanel.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(windLimitLbl)
                                .addGap(41))
                            .addGroup(gl_warnLimitsPanel.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(currentLimitLbl, GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addPreferredGap(ComponentPlacement.RELATED)))
                        .addGroup(gl_warnLimitsPanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(waveLimitLbl, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)))
                    .addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING, false)
                        .addComponent(waveLimit, GroupLayout.PREFERRED_SIZE, 45, GroupLayout.PREFERRED_SIZE)
                        .addComponent(currentLimit, 0, 0, Short.MAX_VALUE)
                        .addComponent(windLimit, 0, 0, Short.MAX_VALUE))
                    .addGap(78))
        );
        gl_warnLimitsPanel.setVerticalGroup(
            gl_warnLimitsPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_warnLimitsPanel.createSequentialGroup()
                    .addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(windLimitLbl)
                        .addComponent(windLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(currentLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(currentLimitLbl))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_warnLimitsPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(waveLimit, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(waveLimitLbl))
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        warnLimitsPanel.setLayout(gl_warnLimitsPanel);
        
        GroupLayout gl_typesPanel = new GroupLayout(typesPanel);
        gl_typesPanel.setHorizontalGroup(
            gl_typesPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_typesPanel.createSequentialGroup()
                    .addGroup(gl_typesPanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_typesPanel.createSequentialGroup()
                            .addComponent(windCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(currentCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_typesPanel.createSequentialGroup()
                            .addComponent(wavesCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(seaLevelCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
                        .addComponent(densityCb, GroupLayout.PREFERRED_SIZE, 88, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(72, Short.MAX_VALUE))
        );
        gl_typesPanel.setVerticalGroup(
            gl_typesPanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(Alignment.LEADING, gl_typesPanel.createSequentialGroup()
                    .addGroup(gl_typesPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(windCb)
                        .addComponent(currentCb))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_typesPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(wavesCb)
                        .addComponent(seaLevelCb))
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(densityCb)
                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        typesPanel.setLayout(gl_typesPanel);
        
        chckbxShowRawRequest = new JCheckBox("Show Raw Request (debug)");
        
        GroupLayout gl_statusPanel = new GroupLayout(statusPanel);
        gl_statusPanel.setHorizontalGroup(
            gl_statusPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_statusPanel.createSequentialGroup()
                    .addGroup(gl_statusPanel.createParallelGroup(Alignment.LEADING)
                        .addComponent(chckbxShowRawRequest)
                        .addGroup(gl_statusPanel.createSequentialGroup()
                            .addGap(63)
                            .addComponent(requestBtn))
                        .addGroup(gl_statusPanel.createSequentialGroup()
                            .addComponent(intervalLbl)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(intervalDb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGroup(gl_statusPanel.createSequentialGroup()
                            .addComponent(currentLabel)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(currentMetocDataLbl))
                        .addComponent(showCheckbox))
                    .addContainerGap(119, Short.MAX_VALUE))
        );
        gl_statusPanel.setVerticalGroup(
            gl_statusPanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_statusPanel.createSequentialGroup()
                    .addComponent(showCheckbox)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(chckbxShowRawRequest)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addGroup(gl_statusPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(intervalLbl)
                        .addComponent(intervalDb, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(ComponentPlacement.UNRELATED)
                    .addGroup(gl_statusPanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(currentLabel)
                        .addComponent(currentMetocDataLbl))
                    .addGap(18)
                    .addComponent(requestBtn)
                    .addContainerGap())
        );
        statusPanel.setLayout(gl_statusPanel);
        getContentPane().setLayout(groupLayout);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                closeBtn.requestFocus();
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveValues();
            }
        });
        
    }
}
