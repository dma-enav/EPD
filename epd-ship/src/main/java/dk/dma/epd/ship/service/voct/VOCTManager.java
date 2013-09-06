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
package dk.dma.epd.ship.service.voct;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.model.route.IRoutesUpdateListener;
import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.VOCTUpdateListener;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.voct.SARInput;

/**
 * The VOCTManager is responsible for maintaining current VOCT Status and all
 * information relevant to the VOCT
 * 
 * The VOCT Manager can be initiated through the cloud or manually by the user
 * 
 * 
 */

public class VOCTManager implements Runnable, Serializable {

    private static final long serialVersionUID = 1L;
    private SAROperation sarOperation;
    private static final Logger LOG = LoggerFactory
            .getLogger(VOCTManager.class);

    private boolean hasSar;

    private SARInput sarInputDialog;

    private CopyOnWriteArrayList<VOCTUpdateListener> listeners = new CopyOnWriteArrayList<>();

    public VOCTManager() {
        EPDShip.startThread(this, "VOCTManager");
        LOG.info("Started VOCT Manager");
    }

    public void initializeSarOperation() {
        LOG.info("Started new SAR Operation");
        if (!hasSar) {
            hasSar = true;

            // Create the GUI input boxes

            // Voct specific test
            sarInputDialog = new SARInput(this);
            sarInputDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            sarInputDialog.setVisible(true);

        } else {
            // Cannot inititate a SAR without terminating the existing one, show existing dialog?
            sarInputDialog.setVisible(true);
        }

    }

    public void setSarType(SAR_TYPE type) {
        sarOperation = new SAROperation(type, this);
    }

    public void setSARVariables() {
        // sarOperation
    }

    /**
     * User has clicked the Cancel button, abort operation and reset
     */
    public void cancelSarOperation() {
        sarOperation = null;
        hasSar = false;

        notifyListeners(VOCTUpdateEvent.SAR_CANCEL);
    }

    @Override
    public void run() {

        // Maintanaince routines
        while (true) {
            Util.sleep(10000);

        }

    }

    public static VOCTManager loadVOCTManager() {

        // Where we load or serialize old VOCTS
        return new VOCTManager();

    }

    public void notifyListeners(VOCTUpdateEvent e) {
        for (VOCTUpdateListener listener : listeners) {
            listener.voctUpdated(e);
        }

        // Persist update VOCT info
        // saveToFile();
    }

    public void addListener(VOCTUpdateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(VOCTUpdateListener listener) {
        listeners.remove(listener);
    }

}
