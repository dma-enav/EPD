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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joda.time.DateTime;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.panels.NoGoPanel;
import dk.dma.epd.ship.nogo.NoGoDataEntry;
import dk.dma.epd.ship.nogo.NogoHandler;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

public class NoGoComponentPanel extends OMComponentPanel implements DockableComponentPanel, ChangeListener {

    private static final long serialVersionUID = 1L;
    private AisHandler aisHandler;
    private NogoHandler nogoHandler;

    private final NoGoPanel nogoPanel = new NoGoPanel();

    private JLabel statusLabel;
    private JLabel validFromLabel;
    private JLabel validToLabel;
    private JLabel draughtLabel;
    private JLabel additionalTxttLabel;
//    private JLabel additionalTxt2Label;

    private JSlider slider;

    public NoGoComponentPanel() {
        super();

        nogoPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);

        setLayout(new BorderLayout(0, 0));
        add(nogoPanel, BorderLayout.NORTH);

        nogoPanel.initLabels();

        setVisible(false);

        slider = nogoPanel.getSlider();
        slider.addChangeListener(this);

    }

    public void setCompletedSlices(int completedSlices, int total) {
        nogoPanel.setCompletedRequests(completedSlices, total);
    }

    public void initializeSlider(int count) {
        nogoPanel.initializeSlider(count);
    }

    public void activateSingle() {
        nogoPanel.activateSinglePanel();
    }

    public void activateMultiple() {
        nogoPanel.activateSliderPanel();
    }

    public void newRequestSingle() {
        nogoPanel.newRequestSingle();
    }

    public void newRequestMultiple() {
        nogoPanel.newRequestMultiple();
    }

    public void nogoFailedSingle() {
        nogoPanel.singleRequestFailed();
    }

    public void nogoFailedMultiple() {
        nogoPanel.multipleRequestFailed();
    }

    public void noConnectionSingle() {

        nogoPanel.noConnectionSingle();

    }

    public void noConnectionMultiple() {
        nogoPanel.noConnectionMultiple();
    }

    /**
     * Errorcode -1 means server experinced a timeout Errorcode 0 means everything went ok Errorcode 1 is the standby message
     * Errorcode 17 means no data Errorcode 18 means no tide data
     * 
     * @param nogoFailed
     * @param errorCode
     *            Own
     * @param errorCode
     *            Target
     * @param polygons
     *            own
     * @param polygons
     *            target
     * @param valid
     *            from
     * @param valid
     *            to
     * @param own
     *            draught
     * @param target
     *            draught
     * 
     * @param completed
     */
    public void requestCompletedSingle(int errorCodeOwn, List<NogoPolygon> polygonsOwn, Date validFrom, Date validTo, Double draught) {
        nogoPanel.requestCompletedSingle(errorCodeOwn, polygonsOwn, validFrom, validTo, draught);
    }

    /**
     * Errorcode -1 means server experinced a timeout Errorcode 0 means everything went ok Errorcode 1 is the standby message
     * Errorcode 17 means no data Errorcode 18 means no tide data
     * 
     * @param nogoFailed
     * @param errorCode
     *            Own
     * @param errorCode
     *            Target
     * @param polygons
     *            own
     * @param polygons
     *            target
     * @param valid
     *            from
     * @param valid
     *            to
     * @param own
     *            draught
     * @param target
     *            draught
     * 
     * @param completed
     */
    public void requestCompletedMultiple(int errorCodeOwn, List<NogoPolygon> polygonsOwn, DateTime dateTime, DateTime dateTime2,
            Double draught, int id) {
        nogoPanel.requestCompletedMultiple(errorCodeOwn, polygonsOwn, new Date(dateTime.getMillis()), new Date(dateTime2.getMillis()), draught, id);
    }

    public void inactive() {
        statusLabel.setEnabled(false);
        validFromLabel.setEnabled(false);
        validToLabel.setEnabled(false);
        draughtLabel.setEnabled(false);
        additionalTxttLabel.setEnabled(false);

        statusLabel.setText("Inactive");
        validFromLabel.setText("");
        validToLabel.setText("");
        draughtLabel.setText("");
        additionalTxttLabel.setText("");
    }

    @Override
    public void findAndInit(Object obj) {

        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
        }
        if (nogoHandler == null && obj instanceof NogoHandler) {
            nogoHandler = (NogoHandler) obj;
        }
    }

    /****************************************/
    /** DockableComponentPanel methods **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "NoGo";
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

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (!source.getValueIsAdjusting()) {
            int index = (int) source.getValue();

            NoGoDataEntry entry = nogoHandler.getNogoData().get(index - 1);

            nogoPanel.setToAndFromSliderOptions(new Date(entry.getValidFrom().getMillis()),
                    new Date(entry.getValidTo().getMillis()));
            nogoHandler.showNoGoIndex(index);

        }
    }
}
