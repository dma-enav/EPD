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
package dk.dma.epd.shore.gui.notification;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.WEST;
import static dk.dma.epd.common.graphics.GraphicsUtil.minSize;
import static dk.dma.epd.common.graphics.GraphicsUtil.bold;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.enavcloud.StrategicRouteService.StrategicRouteMessage;
import dk.dma.epd.common.prototype.model.route.StrategicRouteNegotiationData;
import dk.dma.epd.common.prototype.notification.StrategicRouteNotificationDetailPanelCommon;
import dk.dma.epd.common.text.Formatter;

/**
 * Shore-specific panel that displays relevant strategic detail information 
 * for the selected route in the notification center.
 */
public class StrategicRouteNotificationDetailPanel extends StrategicRouteNotificationDetailPanelCommon<StrategicRouteNotification> {

    private static final long serialVersionUID = 1L;
    
    JLabel mmsiTxt, nameTxt, callSignTxt, destinationTxt, sogTxt;
    JLabel typeTxt, lengthTxt, widthTxt, draughtTxt, cogTxt;
    JLabel[] infoLabels;
    
    /**
     * Constructor
     */
    public StrategicRouteNotificationDetailPanel() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPanel createInfoPanel() {
        // Initialize member variables here, since this method 
        // is called by super's constructor
        mmsiTxt = new JLabel(" ");
        nameTxt = new JLabel(" ");
        callSignTxt = new JLabel(" ");
        destinationTxt = new JLabel(" ");
        sogTxt = new JLabel(" ");

        typeTxt = new JLabel(" ");
        lengthTxt = new JLabel(" ");
        widthTxt = new JLabel(" ");
        draughtTxt = new JLabel(" ");
        cogTxt = new JLabel(" ");
        
        String[] infoTitles = { 
            "MMSI:", "Name:", "Call Sign:", "Destination:", "SOG:", 
            "Type:", "Lenght:", "Width:", "Draught:",  "COG:" };
        
        infoLabels = new JLabel[] {
                mmsiTxt, nameTxt, callSignTxt, destinationTxt, sogTxt,
                typeTxt, lengthTxt, widthTxt, draughtTxt, cogTxt };
        
        int[] infoCols = { 5, 5 };
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        Insets insets2  = new Insets(2, 5, 2, 5);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Ship Info"));
        for (int col = 0, index = 0; col < infoCols.length; col++) {
            for (int row = 0; row < infoCols[col]; row++, index++) {
                infoPanel.add(bold(new JLabel(infoTitles[index])), 
                        new GridBagConstraints(col * 2, row, 1, 1, 0.0, 0.0, WEST, NONE, insets2, 0, 0));
                infoPanel.add(minSize(infoLabels[index], 40), 
                        new GridBagConstraints(col * 2 + 1, row, 1, 1, 1.0, 0.0, WEST, HORIZONTAL, insets2, 0, 0));
            }
        }

        return infoPanel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNotification(StrategicRouteNotification notification) {
        super.setNotification(notification);

        // If no notification is set, reset all fields
        if (notification == null) {
            for (int x = 0; x < infoLabels.length; x++) {
                infoLabels[x].setText("XXX");
            }
            return;
        }
                
        StrategicRouteNegotiationData routeData = notification.get();

        // Start by re-setting all info fields to N/A
        for (int x = 0; x < infoLabels.length; x++) {
            infoLabels[x].setText("N/A");
        }
        mmsiTxt.setText(String.valueOf(routeData.getMmsi()));
        
        VesselStaticData staticData = notification.getVesselStaticData();
        if (staticData != null) {
            nameTxt.setText(staticData.getTrimmedName());
            nameTxt.setToolTipText(staticData.getTrimmedName());
            callSignTxt.setText(staticData.getTrimmedCallsign());
            if (!StringUtils.isBlank(staticData.getTrimmedDestination())) {
                destinationTxt.setText(staticData.getTrimmedDestination());
                destinationTxt.setToolTipText(staticData.getTrimmedDestination());
            }
            typeTxt.setText(staticData.getShipType().toString());
            typeTxt.setToolTipText(staticData.getShipType().toString());
            lengthTxt.setText(staticData.getDimBow() + staticData.getDimStern() + "");
            widthTxt.setText(staticData.getDimPort() + staticData.getDimStarboard() + "");
            draughtTxt.setText(staticData.getDraught() / 10 + "");
        }
        
        VesselPositionData positionData = notification.getVesselPositionData();
        if (positionData != null) {
            sogTxt.setText(Formatter.formatCurrentSpeed((double)positionData.getSog()));
            cogTxt.setText(Formatter.formatDegrees((double)positionData.getCog(), 0));            
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected String getMessageViewTitle(StrategicRouteMessage routeMessage) {
        return routeMessage.isFromStcc() ? "Sent to ship" : "Received from ship";
    }
}
