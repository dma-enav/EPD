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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.sensor.pnt.PntHandler;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Util;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.service.EnavServiceHandler;

/**
 * Panel shown below the chart
 */
public class BottomPanel extends OMComponentPanel implements MouseListener, Runnable {

    private static final long serialVersionUID = 1L;
    // private MsiHandler msiHandler;
    // private MsiDialog msiDialog;
    private ShoreServicesCommon shoreServices;
    private AisHandler aisHandler;
    private PntHandler pntHandler;
    private EnavServiceHandler enavServiceHandler;
    private StatusLabel pntStatus;
    private StatusLabel aisStatus;
    private StatusLabel shoreServiceStatus;
    private StatusLabel cloudStatus;

    private JToolBar toolBar;
    private List<IStatusComponent> statusComponents = new ArrayList<>();
    private JLabel lblDoNotUse;
    private JPanel statusIcons;
    private JPanel navWarnings;

    public BottomPanel() {
        super();
        setLayout(new BorderLayout(0, 0));

        navWarnings = new JPanel();
        FlowLayout flowLayout = (FlowLayout) navWarnings.getLayout();
        flowLayout.setHgap(10);
        // navWarnings.add(navWarnings);

        lblDoNotUse = new JLabel("Do not use this for navigational purposes");
        lblDoNotUse.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblDoNotUse.setForeground(Color.RED);
        navWarnings.add(lblDoNotUse);
        lblDoNotUse.setHorizontalAlignment(SwingConstants.LEFT);

        add(navWarnings, BorderLayout.WEST);

        statusIcons = new JPanel();

        add(statusIcons, BorderLayout.EAST);

        toolBar = new JToolBar();
        statusIcons.add(toolBar);
        toolBar.setFloatable(false);

        pntStatus = new StatusLabel("PNT");
        addToolbarComponent(pntStatus);
        addSeparator();
        
        aisStatus = new StatusLabel("AIS");
        addToolbarComponent(aisStatus);
        addSeparator();

        shoreServiceStatus = new StatusLabel("Shore services");
        addToolbarComponent(shoreServiceStatus);
        addSeparator();

        cloudStatus = new StatusLabel("Maritime Cloud");
        addToolbarComponent(cloudStatus);        

        new Thread(this).start();
    }

    private void addToolbarComponent(Component component) {
        toolBar.add(component);
        component.addMouseListener(this);
    }
    
    private void addSeparator() {
        Component horizontalStrut = Box.createHorizontalStrut(5);
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);        
        toolBar.add(horizontalStrut);
        toolBar.add(separator);
        toolBar.add(horizontalStrut);
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            statusComponents.add(aisHandler);
        } else if (obj instanceof PntHandler) {
            pntHandler = (PntHandler) obj;
            statusComponents.add(pntHandler);
        } else if (obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
            statusComponents.add(shoreServices);
        }
        if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
            statusComponents.add(enavServiceHandler);
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        /**
         * if (e.getSource() == msiIcon) { if (notifyMsgId > 0) { msiDialog.showMessage(notifyMsgId); } else {
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
        if (pntHandler != null) {
            pntStatus.updateStatus(pntHandler);
        }
        if (aisHandler != null) {
            aisStatus.updateStatus(aisHandler);
        }
        if (shoreServices != null) {
            shoreServiceStatus.updateStatus(shoreServices);
        }
        if (enavServiceHandler != null) {
            cloudStatus.updateStatus(enavServiceHandler);
        }
    }

}
