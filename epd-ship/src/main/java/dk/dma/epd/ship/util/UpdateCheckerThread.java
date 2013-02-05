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
package dk.dma.epd.ship.util;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.EPDShip;

/**
 * Thread to check for new version
 */
public class UpdateCheckerThread extends MapHandlerChild implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(UpdateCheckerThread.class);

    private HttpRequest httpRequest;
    private Desktop desktop;
    private Boolean newVersion;
    private String newestVersion;
    private boolean hasNotified;

    public UpdateCheckerThread() {
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        if (!EPDShip.getVersion().equals("?")) {
            new Thread(this).start();
        }
    }

    @Override
    public void run() {
        while (true) {
            // Sleep 10 secs
            Util.sleep(10000);

            // Get the newest version
            getNewestVersion();

            // Maybe notify
            if (newVersion != null && newVersion && !hasNotified) {
                notifyNewVersion();
            }

            // Sleep 6 hours
            Util.sleep(6 * 60 * 60 * 1000);
        }
    }

    private void notifyNewVersion() {
        int choice = JOptionPane.showConfirmDialog(EPDShip.getMainFrame(),
                "A newer version is available.\nDo you want to close the application and open the download website?",
                "Version " + newestVersion + " available", JOptionPane.YES_NO_OPTION);
        hasNotified = true;
        if (choice == JOptionPane.YES_OPTION) {
            if (desktop.isSupported(Desktop.Action.BROWSE)) {
                try {
                    desktop.browse(new URI(EPDShip.getSettings().getEnavSettings().getUpdateServer()));
                } catch (Exception e) {
                    LOG.error("Failed to open browser with new version: " + e.getMessage());
                    return;
                }
            }
            Util.sleep(1000);
            EPDShip.closeApp();
        }
    }

    private void getNewestVersion() {
        newVersion = null;
        httpRequest = new HttpRequest("/eeins/version.txt", EPDShip.getSettings().getEnavSettings());
        httpRequest.init();
        try {
            httpRequest.makeRequest();
            newestVersion = new String(httpRequest.getResponseBody());
            if (new Float(newestVersion) > new Float(EPDShip.getVersion())) {
                newVersion = true;
            } else {
                newVersion = false;
            }
        } catch (Exception e) {
            LOG.error("Failed to get newest version number: " + e.getMessage());
        }
    }

}
