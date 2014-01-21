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
package dk.dma.epd.shore.gui.settingtabs;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.settings.BaseSettingsPanel;
import dk.dma.epd.common.prototype.status.AisStatus;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.status.ShoreServiceStatus;
import dk.dma.epd.common.prototype.status.WMSStatus;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;

public class ConnectionStatus extends BaseSettingsPanel {

    private static final long serialVersionUID = 1L;
    
    private List<IStatusComponent> statusComponents = new ArrayList<IStatusComponent>();

    private JLabel shoreServicesOK;
    private JLabel shoreServicesLastDate;
    private JLabel aisReceptionOK;
    private JLabel aisSendingOK;
    private JLabel aislastReceptionDate;
    private JLabel aisLastSendingDate;
    private JLabel wmsContactOK;
    private JLabel wmsLastContactDate;


    public ConnectionStatus() {
        super("Connections", EPDShore.res().getCachedImageIcon("images/settings/connections.png"));

        setBackground(GuiStyler.backgroundColor);
        setBounds(10, 11, 493, 600);
        setLayout(null);

        JPanel contentStatus = new JPanel();
        contentStatus.setBackground(GuiStyler.backgroundColor);
        contentStatus.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "Connection Status", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        contentStatus.setBounds(10, 11, 473, 393);
        add(contentStatus);
        contentStatus.setLayout(null);

        JPanel shoreServices = new JPanel();
        shoreServices.setBackground(GuiStyler.backgroundColor);
        shoreServices.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "Shore Services", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        shoreServices.setBounds(10, 30, 453, 76);
        contentStatus.add(shoreServices);
        shoreServices.setLayout(null);

        JLabel shoreServicesContact = new JLabel("Contact:");
        GuiStyler.styleText(shoreServicesContact);
        shoreServicesContact.setBounds(10, 21, 85, 14);
        shoreServices.add(shoreServicesContact);

        shoreServicesOK = new JLabel("OK");
        GuiStyler.styleText(shoreServicesOK);
        shoreServicesOK.setBounds(126, 21, 142, 14);
        shoreServices.add(shoreServicesOK);

        JLabel shoreServicesLast = new JLabel("Last Contact:");
        GuiStyler.styleText(shoreServicesLast);
        shoreServicesLast.setBounds(10, 43, 85, 14);
        shoreServices.add(shoreServicesLast);

        shoreServicesLastDate = new JLabel("null");
        GuiStyler.styleText(shoreServicesLastDate);
        shoreServicesLastDate.setBounds(126, 46, 142, 14);
        shoreServices.add(shoreServicesLastDate);

        JPanel aisPanel = new JPanel();
        aisPanel.setBackground(GuiStyler.backgroundColor);
        aisPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "AIS", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        aisPanel.setBounds(10, 117, 453, 124);
        contentStatus.add(aisPanel);
        aisPanel.setLayout(null);

        JLabel aisReception = new JLabel("Reception:");
        GuiStyler.styleText(aisReception);
        aisReception.setBounds(10, 21, 85, 14);
        aisPanel.add(aisReception);

        aisReceptionOK = new JLabel("OK");
        GuiStyler.styleText(aisReceptionOK);
        aisReceptionOK.setBounds(126, 21, 142, 14);
        aisPanel.add(aisReceptionOK);

        JLabel aisSending = new JLabel("Sending:");
        GuiStyler.styleText(aisSending);
        aisSending.setBounds(10, 43, 85, 14);
        aisPanel.add(aisSending);

        aisSendingOK = new JLabel("OK");
        GuiStyler.styleText(aisSendingOK);
        aisSendingOK.setBounds(126, 41, 142, 14);
        aisPanel.add(aisSendingOK);

        JLabel aisLastRecieved = new JLabel("Last Reception:");
        GuiStyler.styleText(aisLastRecieved);
        aisLastRecieved.setBounds(10, 65, 85, 14);
        aisPanel.add(aisLastRecieved);

        JLabel aisLastSending = new JLabel("Last Sending:");
        GuiStyler.styleText(aisLastSending);
        aisLastSending.setBounds(10, 87, 85, 14);
        aisPanel.add(aisLastSending);

        aislastReceptionDate = new JLabel("null");
        GuiStyler.styleText(aislastReceptionDate);
        aislastReceptionDate.setBounds(126, 65, 142, 14);
        aisPanel.add(aislastReceptionDate);

        aisLastSendingDate = new JLabel("null");
        GuiStyler.styleText(aisLastSendingDate);
        aisLastSendingDate.setBounds(126, 87, 142, 14);
        aisPanel.add(aisLastSendingDate);

        JPanel wmsPanel = new JPanel();
        wmsPanel.setBackground(GuiStyler.backgroundColor);
        wmsPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1, new Color(70, 70, 70)), "WMS", TitledBorder.LEADING, TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));
        wmsPanel.setBounds(10, 252, 453, 86);
        contentStatus.add(wmsPanel);
        wmsPanel.setLayout(null);

        JLabel wmsContact = new JLabel("Contact:");
        GuiStyler.styleText(wmsContact);
        wmsContact.setBounds(10, 21, 85, 14);
        wmsPanel.add(wmsContact);

        JLabel wmsLastContact = new JLabel("Last Contact:");
        GuiStyler.styleText(wmsLastContact);
        wmsLastContact.setBounds(10, 43, 85, 14);
        wmsPanel.add(wmsLastContact);

        wmsContactOK = new JLabel("OK");
        GuiStyler.styleText(wmsContactOK);
        wmsContactOK.setBounds(126, 21, 142, 14);
        wmsPanel.add(wmsContactOK);

        wmsLastContactDate = new JLabel("null");
        GuiStyler.styleText(wmsLastContactDate);
        wmsLastContactDate.setBounds(126, 43, 142, 14);
        wmsPanel.add(wmsLastContactDate);
    }

    /**
     * Adds a status component to the list held by the panel
     * @param statusComponent the status component to add
     */
    public void addStatusComponent(IStatusComponent statusComponent) {
        statusComponents.add(statusComponent);
    }
    
    private void showStatus() {
        for (IStatusComponent statusComponent : statusComponents) {
            ComponentStatus componentStatus = statusComponent.getStatus();

            if (componentStatus.getName().equals("AIS")){
                componentStatus.getStatus();
                AisStatus aisStatus = (AisStatus) componentStatus;
                aisSendingOK.setText(aisStatus.getSendStatus().toString());
                aisReceptionOK.setText(aisStatus.getReceiveStatus().toString());

                aislastReceptionDate.setText(Formatter.formatLongDateTime(aisStatus.getLastReceived()));

                aisLastSendingDate.setText(Formatter.formatLongDateTime(aisStatus.getLastSent()));

                if (aisSendingOK.getText().equals("OK")){
                    aisSendingOK.setForeground(new Color(130, 165, 80));
                }
                if (aisSendingOK.getText().equals("ERROR")){
                    aisSendingOK.setForeground(new Color(165, 80, 80));
                }
                if (aisSendingOK.getText().equals("UNKNOWN")){
                    aisSendingOK.setForeground(new Color(208, 192, 61));
                }
                if (aisSendingOK.getText().equals("PARTIAL")){
                    aisSendingOK.setForeground(new Color(208, 192, 61));
                }

                if (aisReceptionOK.getText().equals("OK")){
                    aisReceptionOK.setForeground(new Color(130, 165, 80));
                }
                if (aisReceptionOK.getText().equals("ERROR")){
                    aisReceptionOK.setForeground(new Color(165, 80, 80));
                }
                if (aisReceptionOK.getText().equals("UNKNOWN")){
                    aisReceptionOK.setForeground(new Color(208, 192, 61));
                }
                if (aisSendingOK.getText().equals("PARTIAL")){
                    aisSendingOK.setForeground(new Color(208, 192, 61));
                }


            }

            if (componentStatus.getName().equals("Shore services")){

                ShoreServiceStatus shoreServiceStatus = (ShoreServiceStatus) componentStatus;

                shoreServicesOK.setText(shoreServiceStatus.getStatus().toString());

                shoreServicesLastDate.setText(Formatter.formatLongDateTime(shoreServiceStatus.getLastContact()));

                if (shoreServicesOK.getText().equals("OK")){
                    shoreServicesOK.setForeground(new Color(130, 165, 80));
                }
                if (shoreServicesOK.getText().equals("ERROR")){
                    shoreServicesOK.setForeground(new Color(165, 80, 80));
                }
                if (shoreServicesOK.getText().equals("UNKNOWN")){
                    shoreServicesOK.setForeground(new Color(208, 192, 61));
                }

            }



            if (componentStatus.getName().equals("WMS services")){

                WMSStatus wmsStatus = (WMSStatus) componentStatus;

                wmsContactOK.setText(wmsStatus.getStatus().toString());

                wmsLastContactDate.setText(Formatter.formatLongDateTime(wmsStatus.getLastContact()));

                if (wmsContactOK.getText().equals("OK")){
                    wmsContactOK.setForeground(new Color(130, 165, 80));
                }
                if (wmsContactOK.getText().equals("ERROR")){
                    wmsContactOK.setForeground(new Color(165, 80, 80));
                }
                if ("UNKNOWN".equals(wmsContactOK.getText())){
                    wmsContactOK.setForeground(new Color(208, 192, 61));
                }

            }

        }
        setVisible(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings(){
        showStatus();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
    }
}
