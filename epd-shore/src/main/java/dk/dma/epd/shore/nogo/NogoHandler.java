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
package dk.dma.epd.shore.nogo;

import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import net.jcip.annotations.ThreadSafe;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.layers.nogo.NogoLayer;
import dk.dma.epd.common.prototype.nogo.NogoHandlerCommon;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.shore.EPDShore;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * Component for handling NOGO areas
 */
@ThreadSafe
public class NogoHandler extends NogoHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(NogoHandler.class);

    @Override
    public synchronized void updateNogo(boolean useSlices, int minutesBetween) {

        if (requestInProgress) {
            JOptionPane.showMessageDialog(EPDShore.getInstance().getMainFrame(),
                    "Please wait for the previous NoGo request to be completed before initiating a new",
                    "Unable to comply with NoGo request", JOptionPane.WARNING_MESSAGE);
        } else {

            LOG.info("New NoGo Requested Initiated");
            requestInProgress = true;
            // If the dock isn't visible should it show it?

            this.useSlices = useSlices;
            // this.minutesBetween = minutesBetween;

            resetLayer();

            // Setup the panel
            if (this.useSlices) {
                // nogoPanel.activateMultiple();
                // nogoPanel.newRequestMultiple();
            } else {
                // nogoPanel.activateSingle();
                // nogoPanel.newRequestSingle();

            }

            super.updateNogo(useSlices, minutesBetween);

            // Calculate slices
            if (this.useSlices) {
                // nogoPanel.initializeSlider(nogoData.size());

            }

        }
    }

    /**
     * Handles a failed NoGo request, either because of data error, or no connection
     */
    @Override
    public void nogoTimedOut() {
        if (this.useSlices) {
            // nogoPanel.nogoFailedMultiple();
        } else {
            // nogoPanel.nogoFailedSingle();
        }
    }

    @Override
    public void noNetworkConnection() {
        if (this.useSlices) {
            // nogoPanel.noConnectionMultiple();
        } else {
            // nogoPanel.noConnectionSingle();
        }
    }

    @Override
    protected void updatePanelCompleteMultiple(int errorcode, List<NogoPolygon> polygons, DateTime validFrom, DateTime validTo,
            Double draught, int i) {

        // nogoPanel.requestCompletedMultiple(errorcode, polygons, validFrom, validFrom, draught, i);

    }

    @Override
    protected void updatePanelCompleteSingle(int errorcode, List<NogoPolygon> polygons, Date validFrom, Date validTo, Double draught) {
        // nogoPanel.requestCompletedSingle(errorcode, polygons, validFrom, validTo, draught);
    }

    @Override
    protected void updatePanelCompletedSlices(int completedSlices, int i) {
        // nogoPanel.setCompletedSlices(completedSlices, i);
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
        }
        if (obj instanceof NogoLayer) {
            nogoLayer = (NogoLayer) obj;
        }
        // if (obj instanceof NoGoComponentPanel) {
        // nogoPanel = (NoGoComponentPanel) obj;
        // }

    }

}
