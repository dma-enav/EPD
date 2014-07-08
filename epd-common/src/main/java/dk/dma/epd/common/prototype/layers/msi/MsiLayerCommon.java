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


import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;

/**
 * Base layer class for handling all MSI messages
 */
public abstract class MsiLayerCommon extends EPDLayerCommon  implements IMsiUpdateListener {

    private static final long serialVersionUID = 1L;

    protected MsiHandler msiHandler;
    private MsiInfoPanel msiInfoPanel = new MsiInfoPanel();
    
    /**
     * Constructor
     */
    public MsiLayerCommon() {
        super();
        
        // Register the info panels
        registerInfoPanel(msiInfoPanel, MsiSymbolGraphic.class, MsiDirectionalIcon.class);        
    }
    
    /**
     * Generate the graphics based on the current list of MSI messages
     */
    public void doUpdate() {
        graphics.clear();
        Date now = PntTime.getInstance().getDate();
        // Get messages
        List<MsiMessageExtended> messages = msiHandler.getMessageList();
        for (MsiMessageExtended message : messages) {
            
            // Not able to show messages without location
            if (!message.msiMessage.hasLocation()) {
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
            MsiGraphic msiGraphic = new MsiGraphic(message);
            graphics.add(msiGraphic);
            
            if(mapBean != null && message.relevant){
                MsiDirectionalIcon direction = new MsiDirectionalIcon(mapBean);
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
    protected boolean filterMessage(MsiMessageExtended message) {
        return true;
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public void msiUpdate() {
        doUpdate();
    }

    /**
     * Move and center the map around a specific msi message
     * @param msiMessage
     */
    public void zoomTo(MsiMessage msiMessage) {
        if (!msiMessage.hasLocation()) {
            return;
        }

        MsiLocation msiLocation = msiMessage.getLocation();
        Position center = msiLocation.getCenter();
        mapBean.setCenter(center.getLatitude(), center.getLongitude());
        mapBean.setScale(EPD.getInstance().getSettings().getEnavSettings().getMsiTextboxesVisibleAtScale());
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        if (newClosest instanceof MsiSymbolGraphic) {
            MsiSymbolGraphic msiSymbolGraphic = (MsiSymbolGraphic)newClosest;
            msiInfoPanel.showMsiInfo(msiSymbolGraphic.getMsiMessage());
            
        } else if (newClosest instanceof MsiDirectionalIcon) {
            MsiDirectionalIcon msiDirectionalIcon = (MsiDirectionalIcon)newClosest;
            msiInfoPanel.showMsiInfo(msiDirectionalIcon.getMessage());
            
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
        
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler) obj;
            msiHandler.addListener(this);
        }
    }
}
