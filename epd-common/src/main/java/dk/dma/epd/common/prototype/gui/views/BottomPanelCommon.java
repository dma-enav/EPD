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
package dk.dma.epd.common.prototype.gui.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.gui.StatusLabel;
import dk.dma.epd.common.prototype.service.MaritimeCloudServiceCommon;
import dk.dma.epd.common.prototype.shoreservice.ShoreServicesCommon;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.util.Util;

/**
 * Panel shown below the chart
 */
public class BottomPanelCommon extends OMComponentPanel implements MouseListener, Runnable {

    private static final long serialVersionUID = 1L;

    private ShoreServicesCommon shoreServices;
    private AisHandlerCommon aisHandler;
    private MaritimeCloudServiceCommon maritimeCloudService;

    private StatusLabel aisStatus = new StatusLabel("AIS");
    private StatusLabel shoreServiceStatus = new StatusLabel("Shore services");
    private StatusLabel cloudStatus = new StatusLabel("Maritime Cloud");

    protected List<IStatusComponent> statusComponents = new ArrayList<IStatusComponent>();
    private JToolBar statusToolBar = new JToolBar();
    private JPanel statusIcons = new JPanel();
    
    /**
     * Constructor
     */
    public BottomPanelCommon() {
        super();
        setLayout(new BorderLayout());

        
        add(statusIcons, BorderLayout.EAST);
        statusIcons.add(statusToolBar);
        statusToolBar.setFloatable(false);
        statusToolBar.addMouseListener(this);

        
        // Add the status components
        addStatusComponents();
        
        new Thread(this).start();
    }

    /**
     * Adds the status components
     */
    protected void addStatusComponents() {
        addToolbarComponent(aisStatus);
        addSeparator();
        addToolbarComponent(shoreServiceStatus);
        addSeparator();
        addToolbarComponent(cloudStatus);        
    }
    
    /**
     * Adds the given component to the toolbar
     * @param component the component to add
     */
    protected void addToolbarComponent(Component component) {
        statusToolBar.add(component);
        component.addMouseListener(this);
    }
    
    /**
     * Adds a separator to the toolbar
     */
    protected void addSeparator() {
        Component horizontalStrut = Box.createHorizontalStrut(5);
        JSeparator separator = new JSeparator();
        separator.setOrientation(SwingConstants.VERTICAL);        
        statusToolBar.add(horizontalStrut);
        statusToolBar.add(separator);
        statusToolBar.add(horizontalStrut);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof AisHandlerCommon) {
            aisHandler = (AisHandlerCommon) obj;
            statusComponents.add(aisHandler);
        } else if (obj instanceof ShoreServicesCommon) {
            shoreServices = (ShoreServicesCommon) obj;
            statusComponents.add(shoreServices);
        } else if (obj instanceof MaritimeCloudServiceCommon) {
            maritimeCloudService = (MaritimeCloudServiceCommon) obj;
            statusComponents.add(maritimeCloudService);
        }

    }
    
    /**
     * Main thread run method. Updates the status
     */
    @Override
    public void run() {
        while (true) {
            updateStatus();
            Util.sleep(3000);
        }
    }

    /**
     * Updates the status 
     */
    protected void updateStatus() {
        if (aisHandler != null) {
            aisStatus.updateStatus(aisHandler);
        }
        if (shoreServices != null) {
            shoreServiceStatus.updateStatus(shoreServices);
        }
        if (maritimeCloudService != null) {
            cloudStatus.updateStatus(maritimeCloudService);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getSource() instanceof StatusLabel) {
            BottomPanelStatusDialog statusDialog = new BottomPanelStatusDialog();
            statusDialog.showStatus(statusComponents);
            statusDialog.setVisible(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

}
