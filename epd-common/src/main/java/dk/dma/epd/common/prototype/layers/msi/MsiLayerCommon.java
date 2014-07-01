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
package dk.dma.epd.common.prototype.layers.msi;


import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.layers.EPDLayerCommon;
import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;
import dk.dma.epd.common.prototype.settings.layers.MSILayerCommonSettings;
import dk.dma.epd.common.prototype.settings.observers.MSILayerCommonSettingsListener;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;

/**
 * Base layer class for handling all MSI messages
 */
public abstract class MsiLayerCommon extends EPDLayerCommon  implements MSILayerCommonSettingsListener, IMsiUpdateListener {

    private static final long serialVersionUID = 1L;

    protected MsiHandler msiHandler;
    private MsiInfoPanel msiInfoPanel = new MsiInfoPanel();
    
    /**
     * Constructor
     */
    public MsiLayerCommon(MSILayerCommonSettings<? extends MSILayerCommonSettingsListener> localSettings) {
        super(Objects.requireNonNull(localSettings));
        
        // Register the info panels
        registerInfoPanel(msiInfoPanel, MsiSymbolGraphic.class, MsiDirectionalIcon.class);        
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public MSILayerCommonSettings<? extends MSILayerCommonSettingsListener> getSettings() {
        return (MSILayerCommonSettings<? extends MSILayerCommonSettingsListener>) super.getSettings();
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
        mapBean.setScale(getSettings().getMsiTextboxesVisibleAtScale());
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

    /*
     * [Begin settings listener methods]
     */
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void isVisibleChanged(LayerSettings<?> source, boolean newValue) {
        if (source == this.getSettings()) {
            this.setVisible(newValue);
        }
    }

    @Override
    public void msiTextboxesVisibleAtScaleChanged(int scale) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void msiVisibilityFromNewWaypointChanged(double newValue) {
        // TODO Auto-generated method stub
        
    }
    
    /*
     * [End settings listener methods]
     */
}
