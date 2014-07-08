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
package dk.dma.epd.common.prototype.gui.ais;

import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.sensor.pnt.PntData;
import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Util;

/**
 * Dialog with SART details
 */
public class SartDetailsDialog extends JDialog implements Runnable {
    private static final long serialVersionUID = 1L;

    private JLabel detailsLbl;
    private SarTarget sarTarget;
    private JFrame mainFrame;
    private PntHandler pntHandler;

    /**
     * Constructor
     * 
     * @param mainFrame the main frame
     * @param sarTarget the sar target
     * @param pntHandler the gps handler
     */
    public SartDetailsDialog(JFrame mainFrame, SarTarget sarTarget, PntHandler pntHandler) {
        super();
        this.mainFrame = mainFrame;
        this.sarTarget = sarTarget;
        this.pntHandler = pntHandler;
        setResizable(false);
        setTitle("SART details");
        setSize(275, 130);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        setAlwaysOnTop(true);
        setLocationRelativeTo(mainFrame);

        initGui();

        showDetails();
        
        position();

        new Thread(this).start();
    }

    /**
     * Displays the details of the sar target
     */
    private void showDetails() {
        StringBuilder str = new StringBuilder();
        Date now = PntTime.getInstance().getDate();
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
        if (pntHandler != null) {
            PntData pntData = pntHandler.getCurrentData();
            if (pntData != null && !pntData.isBadPosition()) {
                Position pos = pntData.getPosition();                
                if (pos != null && sarPos != null) {
                    dst = Calculator.range(pos, sarPos, Heading.RL);
                    hdg = Calculator.bearing(pos, sarPos, Heading.RL);
                    if (pntData.getSog() != null && pntData.getSog() > 1) {
                        ttg = Math.round(dst / pntData.getSog() * 60 * 60 * 1000);
                        eta = new Date(now.getTime() + ttg);
                    }
                }
            }
        }
        str.append("RNG " + Formatter.formatDistNM(dst, 2) + " - BRG " + Formatter.formatDegrees(hdg, 0) + "<br/>");
        str.append("TTG " + Formatter.formatTime(ttg) + " - ETA " + Formatter.formatLongDateTime(eta));

        str.append("</html>");

        detailsLbl.setText(str.toString());
    }

    /**
     * Initializes the GUI
     */
    private void initGui() {
        getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        detailsLbl = new JLabel("");
        detailsLbl.setHorizontalAlignment(SwingConstants.LEFT);
        detailsLbl.setVerticalAlignment(SwingConstants.TOP);
        getContentPane().add(detailsLbl);        
    }
    
    /**
     * Positions the dialog in the main frame
     */
    private void position() {
        validate();
        Rectangle rect = mainFrame.getBounds();
        int x = (int)rect.getX() + 20;
        int y = (int)rect.getY() + 70;
        setLocation(x, y);        
        setVisible(true);
    }

    /**
     * Periodically updates the details
     */
    @Override
    public void run() {
        while (true) {
            Util.sleep(5000);
            showDetails();
        }
    }
}
