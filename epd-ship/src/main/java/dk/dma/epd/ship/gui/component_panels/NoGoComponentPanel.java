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
    // private JLabel additionalTxt2Label;

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
        nogoPanel.requestCompletedMultiple(errorCodeOwn, polygonsOwn, new Date(dateTime.getMillis()),
                new Date(dateTime2.getMillis()), draught, id);
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
        // if (!source.getValueIsAdjusting()) {
        int index = (int) source.getValue();

        NoGoDataEntry entry = nogoHandler.getNogoData().get(index - 1);

        nogoPanel.setToAndFromSliderOptions(new Date(entry.getValidFrom().getMillis()), new Date(entry.getValidTo().getMillis()));
        nogoHandler.showNoGoIndex(index);

        // }else{

        // System.out.println("Value is adjusting " + (int) source.getValue());
        // }
    }
}
