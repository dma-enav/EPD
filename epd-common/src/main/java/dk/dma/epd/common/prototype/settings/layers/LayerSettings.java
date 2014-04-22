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
package dk.dma.epd.common.prototype.settings.layers;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.settings.ISettingsObserver;
import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * <p>
 * A base class for maintaining settings that apply to an individual layer. I.e.
 * this class should be used as an abstract base class when writing classes that
 * store settings that are specifically targeted at a given type of layer.
 * </p>
 * <p>
 * If you discover a setting that is relevant to <b>all</b> layer types, you
 * should place that setting in this class.
 * </p>
 * <p>
 * This class inherits from {@link ObservedSettings} which allows clients to
 * register for notifications of changes to any setting maintained by this
 * class.
 * </p>
 * 
 * @param <OBSERVER>
 *            The type of the observers observing the {@code LayerSettings} for
 *            changes.
 * @author Janus Varmarken
 */
public abstract class LayerSettings<OBSERVER extends LayerSettings.IObserver>
        extends ObservedSettings<OBSERVER> {
    /*
     * Add settings that are relevant to all layer types here.
     */

    /**
     * Specifies if the layer should be displayed.
     */
    private boolean visible = true;

    /**
     * Specifies the radius of an invisible circle surrounding the mouse cursor
     * for which any overlapping {@link OMGraphic} is considered interactable
     * (i.e. can be clicked, hovered etc.). Increasing this value will make the
     * layer more tolerant to imprecise mouse selection/pointing.
     */
    private float graphicInteractTolerance = 5.0f;

    /**
     * Get if the layer should be displayed.
     * 
     * @return {@code true} if the layer should be displayed, {@code false} if
     *         it should be hidden.
     */
    public boolean isVisible() {
        try {
            this.settingLock.readLock().lock();
            return this.visible;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Set if the layer should be displayed.
     * 
     * @param visible
     *            {@code true} to display the layer, {@code false} to hide the
     *            layer.
     */
    public void setVisible(boolean visible) {
        try {
            this.settingLock.writeLock().lock();
            if (this.visible == visible) {
                // No change, no need to notify observers.
                return;
            }
            this.visible = visible;
            for (OBSERVER obs : this.observers) {
                obs.isVisibleChanged(this.visible);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the value that specifies the radius (in pixels) of an invisible
     * circle surrounding the mouse cursor for which any overlapping
     * {@link OMGraphic} is considered interactable (i.e. can be clicked,
     * hovered etc.).
     * 
     * @return The interaction radius in pixels.
     */
    public float getGraphicInteractTolerance() {
        try {
            this.settingLock.readLock().lock();
            return this.graphicInteractTolerance;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Set the value that specifies the radius (in pixels) of an invisible
     * circle surrounding the mouse cursor for which any overlapping
     * {@link OMGraphic} is considered interactable (i.e. can be clicked,
     * hovered etc.). Increasing this value will make the layer more tolerant to
     * imprecise mouse selection/pointing.
     * 
     * @param graphicInteractTolerance
     *            The new interaction radius in pixels.
     */
    public void setGraphicInteractTolerance(float graphicInteractTolerance) {
        try {
            this.settingLock.writeLock().lock();
            if (this.graphicInteractTolerance == graphicInteractTolerance) {
                // No change, no need to notify observers.
                return;
            }
            this.graphicInteractTolerance = graphicInteractTolerance;
            for (OBSERVER obs : this.observers) {
                obs.graphicInteractToleranceChanged(this.graphicInteractTolerance);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Base interface for observing a {@link LayerSettings} for changes. I.e.
     * <i>this interface should only contain callbacks for changes to settings that
     * are relevant to all layer types.</i>
     * 
     * @author Janus Varmarken
     */
    public interface IObserver extends ISettingsObserver {
        /*
         * Specify setting-changed callbacks that are relevant to all layer types
         * here.
         */

        /**
         * Invoked when the visibility of a layer is toggled on/off.
         * 
         * @param newValue
         *            {@code true} if the layer should now be visible, {@code false}
         *            if the layer should now be invisible.
         */
        void isVisibleChanged(boolean newValue);

        /**
         * Invoked when the graphic interact tolerance setting has been changed.
         * 
         * @param newValue
         *            The new tolerance level in pixels.
         */
        void graphicInteractToleranceChanged(float newValue);
    }
    
}
