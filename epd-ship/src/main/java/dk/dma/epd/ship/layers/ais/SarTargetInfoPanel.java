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
package dk.dma.epd.ship.layers.ais;

import java.util.Date;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.sensor.gps.GpsData;
import dk.dma.epd.common.prototype.sensor.gps.GpsHandler;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;

/**
 * AIS SART mouse over info
 */
public class SarTargetInfoPanel extends InfoPanel implements Runnable {
    private static final long serialVersionUID = 1L;

    private GpsHandler gpsHandler;
    private SarTarget sarTarget;

    public SarTargetInfoPanel() {
        super();
        new Thread(this).start();
    }

    public synchronized void showSarInfo(SarTarget sarTarget) {
        this.sarTarget = sarTarget;
        StringBuilder str = new StringBuilder();
        Date now = GnssTime.getInstance().getDate();
        Date lastReceived = sarTarget.getLastReceived();
        Date firstReceived = sarTarget.getFirstReceived();
        long elapsedLast = now.getTime() - lastReceived.getTime();
        long elapsedFirst = now.getTime() - firstReceived.getTime();
        str.append("<html><b>AIS SART - MMSI " + sarTarget.getMmsi() + "</b><br/>");
        
        Position sarPos = null;
        if (sarTarget.getPositionData() != null) {
            sarPos = sarTarget.getPositionData().getPos();
        }
        if (sarPos != null) {
            str.append(Formatter.latToPrintable(sarPos.getLatitude()) + " ");
            str.append(Formatter.lonToPrintable(sarPos.getLongitude()) + "<br/>");
        }
        
        str.append("Last reception  " + Formatter.formatTime(elapsedLast) + " [" + Formatter.formatLongDateTime(lastReceived)
                + "]<br/>");
        str.append("First reception " + Formatter.formatTime(elapsedFirst) + " [" + Formatter.formatLongDateTime(firstReceived)
                + "]<br/>");
        Double dst = null;
        Double hdg = null;
        Long ttg = null;
        Date eta = null;
        if (gpsHandler != null) {
            GpsData gpsData = gpsHandler.getCurrentData();
            if (gpsData != null && !gpsData.isBadPosition()) {
                Position pos = gpsData.getPosition();                
                if (pos != null && sarPos != null) {
                    dst = Calculator.range(pos, sarPos, Heading.RL);
                    hdg = Calculator.bearing(pos, sarPos, Heading.RL);
                    if (gpsData.getSog() != null && gpsData.getSog() > 1) {
                        ttg = Math.round(dst / gpsData.getSog() * 60 * 60 * 1000);
                        eta = new Date(now.getTime() + ttg);
                    }
                }
            }
        }
        str.append("RNG " + Formatter.formatDistNM(dst, 2) + " - BRG " + Formatter.formatDegrees(hdg, 0) + "<br/>");
        str.append("TTG " + Formatter.formatTime(ttg) + " - ETA " + Formatter.formatLongDateTime(eta));

        str.append("</html>");

        showText(str.toString());
    }

    public synchronized void setGpsHandler(GpsHandler gpsHandler) {
        this.gpsHandler = gpsHandler;
    }

    @Override
    public void run() {
        while (true) {
            Util.sleep(10000);
            if (this.isVisible() && sarTarget != null) {
                showSarInfo(sarTarget);
            }
        }
    }

}
