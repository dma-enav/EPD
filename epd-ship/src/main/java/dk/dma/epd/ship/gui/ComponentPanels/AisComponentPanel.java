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
package dk.dma.epd.ship.gui.ComponentPanels;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.ais.message.AisMessage;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.Panels.AisTargetPanel;
import dk.dma.epd.ship.nogo.DynamicNogoHandler;

public class AisComponentPanel extends OMComponentPanel implements ItemListener {

    private static final long serialVersionUID = 1L;
    private AisHandler aisHandler;
    private DynamicNogoHandler dynamicNogoHandler;

    private final AisTargetPanel aisPanel = new AisTargetPanel();

    private long mmsi = -99;

    private JLabel nameLabel;
    private JLabel callsignLabel;
    private JLabel sogLabel;
    private JLabel cogLabel;
    private JLabel dstLabel;
    private JLabel brgLabel;
    private JCheckBox intendedRouteCheckbox;
    private JLabel intendedRouteTitelLabel;
    private JCheckBox dynamicNoGoCheckbox;
    private JLabel dynamicNogoTitelLabel;
    private boolean internalSet;

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
        dynamicNoGoCheckbox = aisPanel.getDynamicNoGoCheckbox();
        dynamicNogoTitelLabel = aisPanel.getDynamicNogoTitelLabel();

        intendedRouteCheckbox.setEnabled(false);
        intendedRouteTitelLabel.setEnabled(false);

        dynamicNoGoCheckbox.setEnabled(false);
        dynamicNogoTitelLabel.setEnabled(false);

        dynamicNoGoCheckbox.addItemListener(this);

    }

    @Override
    public void findAndInit(Object obj) {

        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
        }
        if (dynamicNogoHandler == null && obj instanceof DynamicNogoHandler) {
            dynamicNogoHandler = (DynamicNogoHandler) obj;
        }
    }

    public void receiveHighlight(long mmsi, String name, String callsign,
            float cog, double rhumbLineDistance, double rhumbLineBearing,
            float sog) {

        this.mmsi = mmsi;
        nameLabel.setText(AisMessage.trimText(name));
        callsignLabel.setText(AisMessage.trimText(callsign));
        cogLabel.setText(Float.toString(cog));
        dstLabel.setText(Formatter.formatDistNM(rhumbLineDistance/1852.0));
        brgLabel.setText(Formatter.formatDegrees(rhumbLineBearing, 1));
        sogLabel.setText(Formatter.formatSpeed((double) sog));
    }

    public void receiveHighlight(long mmsi, float cog,
            double rhumbLineDistance, double rhumbLineBearing, float sog) {

        this.mmsi = mmsi;

        nameLabel.setText("N/A");
        callsignLabel.setText("N/A");

        cogLabel.setText(Float.toString(cog));
        dstLabel.setText(Formatter.formatDistNM(rhumbLineDistance));
        brgLabel.setText(Formatter.formatDegrees(rhumbLineBearing, 1));
        sogLabel.setText(Formatter.formatSpeed((double) sog));

    }

    public void resetHighLight() {
        mmsi = -99;
        nameLabel.setText("N/A");
        callsignLabel.setText("N/A");
        cogLabel.setText("N/A");
        dstLabel.setText("N/A");
        brgLabel.setText("N/A");
        sogLabel.setText("N/A");

        dynamicNoGoCheckbox.setSelected(false);

        dynamicNoGoCheckbox.setEnabled(false);
        dynamicNogoTitelLabel.setEnabled(false);

    }

    public void dynamicNogoAvailable(boolean possible) {
        dynamicNoGoCheckbox.setEnabled(possible);
        dynamicNogoTitelLabel.setEnabled(possible);

        // System.out.println(dynamicNogoHandler.getMmsiTarget());
        // System.out.println(mmsi);

        if (dynamicNogoHandler.getMmsiTarget() == mmsi) {
            internalSet = true;
            dynamicNoGoCheckbox.setSelected(true);
            // System.out.println("Selecting it!");
        } else {
            internalSet = true;
            dynamicNoGoCheckbox.setSelected(false);
            // System.out.println("not the selected");
        }

        internalSet = false;
    }

    @Override
    public void itemStateChanged(ItemEvent arg0) {
        if (arg0.getItem() == dynamicNoGoCheckbox) {
            System.out.println("item state changed");

            if (dynamicNoGoCheckbox.isSelected() && !internalSet) {
                dynamicNogoHandler.setMmsiTarget(mmsi);
                
                internalSet = true;
                // new Thread(dynamicNogoHandler.updateNogo());
                dynamicNogoHandler.setDynamicNoGoActive(true);
                // dynamicNogoHandler.forceUpdate();
                System.out.println("It's selected");

                
                dynamicNoGoCheckbox.setSelected(true);
                internalSet = false;
            }
            //
            else {
                if (mmsi == dynamicNogoHandler.getMmsiTarget() && !internalSet) {
                    dynamicNogoHandler.setMmsiTarget(-1);
                    dynamicNogoHandler.setDynamicNoGoActive(false);
                    System.out.println("Deselecting");
                }

            }

        }

    }
}
