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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.panels.AisTargetPanel;

public class AisComponentPanel extends OMComponentPanel implements DockableComponentPanel {

    private static final long serialVersionUID = 1L;
    private AisHandler aisHandler;

    private final AisTargetPanel aisPanel = new AisTargetPanel();

    private JLabel nameLabel;
    private JLabel callsignLabel;
    private JLabel sogLabel;
    private JLabel cogLabel;
    private JLabel dstLabel;
    private JLabel brgLabel;
    private JCheckBox intendedRouteCheckbox;
    private JLabel intendedRouteTitelLabel;

    public AisComponentPanel() {
        super();

        // this.setMinimumSize(new Dimension(10, 195));

        aisPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);

        setLayout(new BorderLayout(0, 0));
        add(aisPanel, BorderLayout.NORTH);

        nameLabel = aisPanel.getNameLabel();
        callsignLabel = aisPanel.getCallsignLabel();
        sogLabel = aisPanel.getSogLabel();
        cogLabel = aisPanel.getCogLabel();
        dstLabel = aisPanel.getDstLabel();
        brgLabel = aisPanel.getBrgLabel();
        intendedRouteCheckbox = aisPanel.getIntendedRouteCheckbox();
        intendedRouteTitelLabel = aisPanel.getIntendedRouteTitelLabel();

        intendedRouteCheckbox.setEnabled(false);
        intendedRouteTitelLabel.setEnabled(false);

        setVisible(false);
    }

    @Override
    public void findAndInit(Object obj) {

        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
        }

    }

    public void receiveHighlight(long mmsi, String name, String callsign, float cog, double rhumbLineDistance,
            double rhumbLineBearing, float sog) {

        // this.mmsi = mmsi;
        nameLabel.setText(AisMessage.trimText(name));
        callsignLabel.setText(AisMessage.trimText(callsign));
        cogLabel.setText(Float.toString(cog));
        dstLabel.setText(Formatter.formatDistNM(rhumbLineDistance / 1852.0));
        brgLabel.setText(Formatter.formatDegrees(rhumbLineBearing, 1));
        sogLabel.setText(Formatter.formatSpeed((double) sog));
    }

    public void receiveHighlight(long mmsi, float cog, double rhumbLineDistance, double rhumbLineBearing, float sog) {

        // this.mmsi = mmsi;

        nameLabel.setText("N/A");
        callsignLabel.setText("N/A");

        cogLabel.setText(Float.toString(cog));
        dstLabel.setText(Formatter.formatDistNM(rhumbLineDistance));
        brgLabel.setText(Formatter.formatDegrees(rhumbLineBearing, 1));
        sogLabel.setText(Formatter.formatSpeed((double) sog));

    }

    public void resetHighLight() {
        // mmsi = -99;
        nameLabel.setText("N/A");
        callsignLabel.setText("N/A");
        cogLabel.setText("N/A");
        dstLabel.setText("N/A");
        brgLabel.setText("N/A");
        sogLabel.setText("N/A");

    }

    /****************************************/
    /** DockableComponentPanel methods **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "AIS Target";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInDefaultLayout() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInPanelsMenu() {
        return true;
    }
}
