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
package dk.dma.epd.shore.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Objects;

import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * Individual settings for a map frame in the workspace.
 * 
 * @author Janus Varmarken
 */
public class MapFrameSettings extends ObservedSettings<MapFrameSettings.IObserver> {
    
    private boolean alwaysInFront;
    private boolean layerPanelVisible = true;
    private Point layerPanelPosition = new Point(2, 20);
    private boolean locked;
    private boolean maximized;
    private String frameName;
    
    private Point framePosition;
    private Dimension frameDimension;
    
//    /*
//     * THIS SHOULD BE MOVED TO GUISETTINGS!
//     */
//    private boolean statusVisible;
//    
//    /*
//     * THIS SHOULD BE MOVED TO GUISETTINGS!
//     */
//    private Point statusPos;
//    
//    /*
//     * THIS SHOULD BE MOVED TO GUISETTINGS!
//     */
//    private Point toolbarPos;
//    
//    /*
//     * THIS SHOULD BE MOVED TO GUISETTINGS!
//     */
//    private Point notificationPos;
    
    public boolean isAlwaysInFront() {
        try {
            this.settingLock.readLock().lock();
            return alwaysInFront;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setAlwaysInFront(final boolean alwaysInFront) {
        try {
            this.settingLock.writeLock().lock();
            if(this.alwaysInFront == alwaysInFront) {
                return;
            }
            this.alwaysInFront = alwaysInFront;
            for(IObserver obs : this.observers) {
                obs.alwaysInFrontChanged(this, alwaysInFront);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public boolean isLayerPanelVisible() {
        try {
            this.settingLock.readLock().lock();
            return layerPanelVisible;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setLayerPanelVisible(final boolean layerPanelVisible) {
        try {
            this.settingLock.writeLock().lock();
            if(this.layerPanelVisible == layerPanelVisible) {
                return;
            }
            this.layerPanelVisible = layerPanelVisible;
            for(IObserver obs : this.observers) {
                obs.layerPanelVisibleChanged(this, layerPanelVisible);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public Point getLayerPanelPosition() {
        try {
            this.settingLock.readLock().lock();
            return new Point(layerPanelPosition);
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setLayerPanelPosition(final Point layerPanelPosition) {
        try {
            this.settingLock.writeLock().lock();
            if (Objects.equals(this.layerPanelPosition, layerPanelPosition)) {
                return;
            }
            this.layerPanelPosition = new Point(layerPanelPosition);
            for(IObserver obs : this.observers) {
                obs.layerPanelPositionChanged(this, new Point(layerPanelPosition));
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public boolean isLocked() {
        try {
            this.settingLock.readLock().lock();
            return locked;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setLocked(final boolean locked) {
        try {
            this.settingLock.writeLock().lock();
            if(this.locked = locked) {
                return;
            }
            this.locked = locked;
            for(IObserver obs : this.observers) {
                obs.lockedChanged(this, locked);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public boolean isMaximized() {
        try {
            this.settingLock.readLock().lock();
            return maximized;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setMaximized(final boolean maximized) {
        try {
            this.settingLock.writeLock().lock();
            if(this.maximized == maximized) {
                return;
            }
            this.maximized = maximized;
            for(IObserver obs : this.observers) {
                obs.maximizedChanged(this, maximized);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public String getFrameName() {
        try {
            this.settingLock.readLock().lock();
            return frameName;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setFrameName(final String frameName) {
        try {
            this.settingLock.writeLock().lock();
            if(Objects.equals(this.frameName, frameName)) {
                return;
            }
            this.frameName = frameName;
            for(IObserver obs : this.observers) {
                obs.frameNameChanged(this, frameName);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public Point getFramePosition() {
        try {
            this.settingLock.readLock().lock();
            return new Point(framePosition);
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setFramePosition(final Point framePosition) {
        try {
            this.settingLock.writeLock().lock();
            if(Objects.equals(this.framePosition, framePosition)) {
                return;
            }
            this.framePosition = new Point(framePosition);
            for(IObserver obs : this.observers) {
                obs.framePositionChanged(this, new Point(framePosition));
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public Dimension getFrameDimension() {
        try {
            this.settingLock.readLock().lock();
            return new Dimension(frameDimension);
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setFrameDimension(final Dimension frameDimension) {
        try {
            this.settingLock.writeLock().lock();
            if(Objects.equals(this.frameDimension, frameDimension)) {
                return;
            }
            this.frameDimension = new Dimension(frameDimension);
            for(IObserver obs : this.observers) {
                obs.frameDimensionChanged(this, new Dimension(frameDimension));
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public interface IObserver {
        
        void alwaysInFrontChanged(MapFrameSettings source, boolean newValue);
        
        void layerPanelVisibleChanged(MapFrameSettings source, boolean newValue);
        
        void layerPanelPositionChanged(MapFrameSettings source, Point newPos);
        
        void lockedChanged(MapFrameSettings source, boolean newValue);
        
        void maximizedChanged(MapFrameSettings source, boolean newValue);
        
        void frameNameChanged(MapFrameSettings source, String newValue);
        
        void framePositionChanged(MapFrameSettings source, Point newValue);
        
        void frameDimensionChanged(MapFrameSettings source, Dimension newValue);
    }
    
}
