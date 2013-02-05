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
package dk.dma.epd.ship.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gps.GpsHandler;
import dk.dma.epd.ship.service.communication.webservice.ShoreServices;
import dk.dma.epd.ship.status.IStatusComponent;

/**
 * Panel shown below the chart
 */
public class BottomPanel extends OMComponentPanel implements MouseListener, Runnable {

    private static final long serialVersionUID = 1L;
    // private MsiHandler msiHandler;
    // private MsiDialog msiDialog;
    private ShoreServices shoreServices;
    private AisHandler aisHandler;
    private GpsHandler gpsHandler;
    private StatusLabel gpsStatus;
    private StatusLabel aisStatus;
    private StatusLabel shoreServiceStatus;
    private JToolBar toolBar;
    private List<IStatusComponent> statusComponents = new ArrayList<>();

    public BottomPanel() {
        super();
        setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, new Color(255, 255, 255)), new MatteBorder(1, 0, 0, 0,
                new Color(192, 192, 192))));
        FlowLayout flowLayout = (FlowLayout) getLayout();
        flowLayout.setVgap(2);
        flowLayout.setHgap(3);
        flowLayout.setAlignment(FlowLayout.RIGHT);

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        add(toolBar);

        gpsStatus = new StatusLabel("GPS");
        addToolbarComponent(gpsStatus);

        aisStatus = new StatusLabel("AIS");
        addToolbarComponent(aisStatus);

        shoreServiceStatus = new StatusLabel("Shore services");
        addToolbarComponent(shoreServiceStatus);

        new Thread(this).start();
    }

    private void addToolbarComponent(Component component) {
        Component horizontalStrut = Box.createHorizontalStrut(5);
        toolBar.add(horizontalStrut);
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);
        toolBar.add(separator);
        horizontalStrut = Box.createHorizontalStrut(5);
        toolBar.add(horizontalStrut);
        toolBar.add(component);
        component.addMouseListener(this);
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            statusComponents.add(aisHandler);
        } else if (obj instanceof GpsHandler) {
            gpsHandler = (GpsHandler) obj;
            statusComponents.add(gpsHandler);
        } else if (obj instanceof ShoreServices) {
            shoreServices = (ShoreServices) obj;
            statusComponents.add(shoreServices);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /**
         * if (e.getSource() == msiIcon) { if (notifyMsgId > 0) {
         * msiDialog.showMessage(notifyMsgId); } else {
         * msiDialog.setVisible(true); } } else
         **/
        if (e.getSource() instanceof StatusLabel) {
            StatusDialog statusDialog = new StatusDialog();
            statusDialog.showStatus(statusComponents);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void run() {
        while (true) {
            updateStatus();
            Util.sleep(3000);
        }
    }

    private void updateStatus() {
        if (gpsHandler != null) {
            gpsStatus.updateStatus(gpsHandler);
        }
        if (aisHandler != null) {
            aisStatus.updateStatus(aisHandler);
        }
        if (shoreServices != null) {
            shoreServiceStatus.updateStatus(shoreServices);
        }
    }

}
