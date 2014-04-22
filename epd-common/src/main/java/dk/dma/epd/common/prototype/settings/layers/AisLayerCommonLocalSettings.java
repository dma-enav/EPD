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

import dk.dma.epd.common.prototype.layers.ais.AisLayerCommon;

/**
 * <p>
 * An instance of this class maintains settings that are local to one (or more)
 * {@link AisLayerCommon} instance(s), but it still obeys to changes to settings
 * that are considered global to all instances of {@link AisLayerCommon}.
 * </p>
 * <p>
 * This is implemented in the following manner: Clients can change the state of
 * the local settings by invoking the different setters of this class on the
 * local settings instance. Invoking these setters will fire notifications to
 * all the {@link IObserver}s registered with the local
 * settings instance.<br/>
 * However, this class is also an {@link IObserver}
 * itself, and its instances are registered as observers of the singleton
 * instance of {@link AisLayerCommonGlobalSettings}. As such, whenever a global
 * setting is changed, instances of this class are notified. These instances
 * will update their local version of the value updated on the global singleton
 * and will then subsequently notify their own observers of the change to the
 * local value.
 * </p>
 * 
 * @param <OBSERVER>
 *            The type of the observers observing the
 *            {@code AisLayerCommonLocalSettings} for changes.
 * @author Janus Varmarken
 */
public class AisLayerCommonLocalSettings<OBSERVER extends AisLayerCommonSettings.IObserver>
        extends AisLayerCommonSettings<OBSERVER> implements
        AisLayerCommonSettings.IObserver {

    /**
     * Creates a new {@link AisLayerCommonLocalSettings} and registers this
     * instance as an observer of the singleton instance of
     * {@link AisLayerCommonGlobalSettings}.
     */
    public AisLayerCommonLocalSettings() {
        super();
        // Register self as observant of the global AIS settings.
        // As we want to acknowledge/obey to changes to the global settings.
        // TODO is this run if we start to serialize this class?
        AisLayerCommonGlobalSettings.getInstance().addObserver(this);
    }

    /**
     * Invoked when the global value for display of AIS name labels has changed.
     */
    @Override
    public void showAllAisNameLabelsChanged(boolean oldValue, boolean newValue) {
        // The "display AIS labels" was changed on the global settings object.
        // We need to obey to this for our local settings:
        this.setShowAllAisNameLabels(newValue);
    }

    /**
     * Invoked when the global value for display of past tracks has changed.
     */
    @Override
    public void showAllPastTracksChanged(boolean oldValue, boolean newValue) {
        // The "display all past tracks" was changed on the global settings
        // object.
        // We need to obey to this for our local settings:
        this.setShowAllPastTracks(newValue);
    }

    /**
     * Invoked when the global value for the layer redraw interval has changed.
     */
    @Override
    public void layerRedrawIntervalChanged(int oldValue, int newValue) {
        /*
         * The "set layer redraw interval" was changed on the global settings
         * object. We need to obey to this for this local settings instance.
         */
        this.setLayerRedrawInterval(newValue);
    }

    /**
     * Invoked when the global value for the movement vector length minimum has
     * changed.
     */
    @Override
    public void movementVectorLengthMinChanged(int newMinLengthMinutes) {
        /*
         * The setting was changed on the global AIS layer settings instance. We
         * need to obey to this for this local settings instance.
         */
        this.setMovementVectorLengthMin(newMinLengthMinutes);
    }

    /**
     * Invoked when the global value for the movement vector length maximum has
     * changed.
     */
    @Override
    public void movementVectorLengthMaxChanged(int newMaxLengthMinutes) {
        /*
         * The setting was changed on the global AIS layer settings instance. We
         * need to obey to this for this local settings instance.
         */
        this.setMovementVectorLengthMax(newMaxLengthMinutes);
    }

    /**
     * Invoked when the global value for the scale difference between two
     * successive values for the length of the movement vector has changed.
     */
    @Override
    public void movementVectorLengthStepSizeChanged(float newStepSize) {
        /*
         * The setting was changed on the global AIS layer settings instance. We
         * need to obey to this for this local settings instance.
         */
        this.setMovementVectorLengthStepSize(newStepSize);
    }

    /**
     * Invoked when the global value for the minimum speed a vessel must travel
     * with, in order for its movement vector to be shown, has changed.
     */
    @Override
    public void movementVectorHideBelowChanged(float newMinSpeed) {
        /*
         * The setting was changed on the global AIS layer settings instance. We
         * need to obey to this for this local settings instance.
         */
        this.setMovementVectorHideBelow(newMinSpeed);
    }

    /**
     * Invoked when the global value for layer visibility has changed.
     */
    @Override
    public void isVisibleChanged(boolean newValue) {
        /*
         * The setting was changed on the global AIS layer settings instance. We
         * need to obey to this for this local settings instance.
         */
        this.setVisible(newValue);
    }

    /**
     * Invoked when the global value for graphic interact tolerance has changed.
     */
    @Override
    public void graphicInteractToleranceChanged(float newValue) {
        /*
         * The setting was changed on the global AIS layer settings instance. We
         * need to obey to this for this local settings instance.
         */
        this.setGraphicInteractTolerance(newValue);
    }
}
