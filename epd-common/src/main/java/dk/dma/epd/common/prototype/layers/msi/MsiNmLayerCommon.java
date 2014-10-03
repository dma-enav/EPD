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
package dk.dma.epd.common.prototype.layers.msi;


import com.bbn.openmap.omGraphics.OMGraphic;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon;
import dma.msinm.MCMsiNmService;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import static dk.dma.epd.common.prototype.service.MsiNmServiceHandlerCommon.IMsiNmServiceListener;

/**
 * Base layer class for handling all MSI-NM messages
 */
public abstract class MsiNmLayerCommon extends EPDLayerCommon  implements IMsiNmServiceListener {

    private static final long serialVersionUID = 1L;

    protected MsiNmServiceHandlerCommon msiNmHandler;
    private MsiNmInfoPanel msiNmInfoPanel = new MsiNmInfoPanel();
    
    /**
     * Constructor
     */
    public MsiNmLayerCommon() {
        super();
        
        // Register the info panels
        registerInfoPanel(msiNmInfoPanel, MsiNmNmSymbolGraphic.class, MsiNmDirectionalIcon.class);
    }
    
    /**
     * Generate the graphics based on the current list of MSI messages
     */
    public void doUpdate() {
        graphics.clear();
        Date now = PntTime.getDate();
        boolean showFiltered = EPD.getInstance().getSettings().getEnavSettings().isMsiFilter();

        // Get messages
        for (MsiNmNotification message : msiNmHandler.getMsiNmMessages(showFiltered)) {
            
            // Not able to show messages without location
            if (message.getLocation() == null) {
                continue;
            }
            
            // Is it valid now
            if (!message.isValidAt(now)) {
                continue;
            }
            
            // Is the message included in the filter
            if (!filterMessage(message)) {
                continue;
            }
            
            // Create MSI graphic
            MsiNmGraphic msiNmGraphic = new MsiNmGraphic(message);
            graphics.add(msiNmGraphic);
            
            if(mapBean != null && message.isFiltered()){
                MsiNmDirectionalIcon direction = new MsiNmDirectionalIcon(mapBean);
                direction.setMarker(message);
                graphics.add(direction);
            }
        }
        doPrepare();
    }
    
    /**
     * If filtering is turned on, return whether to include the message or not
     * @param message the message to check
     * @return whether to include the message or not
     */
    protected boolean filterMessage(MsiNmNotification message) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void msiNmServicesChanged(List<MCMsiNmService> msiNmServiceList) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void msiNmMessagesChanged(List<MsiNmNotification> msiNmMessages) {
        doUpdate();
    }

    /**
     * Move and center the map around a specific msi message
     * @param message the message
     */
    public void zoomTo(MsiNmNotification message) {
        if (message.getLocation() != null) {
            mapBean.setCenter(message.getLocation().getLatitude(), message.getLocation().getLongitude());
            mapBean.setScale(EPD.getInstance().getSettings().getEnavSettings().getMsiTextboxesVisibleAtScale());
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof MsiNmNmSymbolGraphic) {
            MsiNmNmSymbolGraphic msiNmSymbolGraphic = (MsiNmNmSymbolGraphic)newClosest;
            msiNmInfoPanel.showMsiInfo(msiNmSymbolGraphic.getMsiNmMessage());
            
        } else if (newClosest instanceof MsiNmDirectionalIcon) {
            MsiNmDirectionalIcon msiNmDirectionalIcon = (MsiNmDirectionalIcon)newClosest;
            msiNmInfoPanel.showMsiInfo(msiNmDirectionalIcon.getMessage());
            
        }
        return true;
    }
    
    /**
     * Called when a new bean is added to the bean context
     * @param obj the bean being added
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof MsiNmServiceHandlerCommon) {
            msiNmHandler = (MsiNmServiceHandlerCommon) obj;
            msiNmHandler.addListener(this);
        }
    }
}
