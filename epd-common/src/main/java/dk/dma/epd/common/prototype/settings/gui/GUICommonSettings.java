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
package dk.dma.epd.common.prototype.settings.gui;

import java.awt.Dimension;
import java.awt.Point;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.bbn.openmap.omGraphics.OMGraphic;

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;
import dk.dma.epd.common.prototype.settings.observers.GUICommonSettingsListener;

/**
 * This class maintains the most abstract GUI settings such as window size and
 * window location. GUI settings are primarily targeted at Swing components such
 * as frames, menus, docks etc. Settings specifying how vessels or other units
 * are to be painted on a layer should be placed in {@link LayerSettings} or any
 * of its subclasses.
 * 
 * @author Janus Varmarken
 */
public class GUICommonSettings<OBSERVER extends GUICommonSettingsListener>
        extends ObservedSettings<OBSERVER> implements GUICommonSettingsListener {

    /**
     * Setting specifying if the application should run in fullscreen.
     */
    private boolean fullscreen;

    /**
     * Setting specifying if the application window should be maximized.
     */
    private boolean maximized;

    /**
     * Setting specifying the screen coordinates for the top left corner of the
     * application window.
     */
    private Point appLocation = new Point(10, 10);

    /**
     * Setting specifying the application window dimensions.
     */
    private Dimension appDimensions = new Dimension(1280, 800);

    /**
     * Specifies the radius of an invisible circle surrounding the mouse cursor
     * for which any overlapping {@link OMGraphic} is considered interactable
     * (i.e. can be clicked, hovered etc.). Increasing this value will make the
     * layer more tolerant to imprecise mouse selection/pointing.
     */
    private float graphicInteractTolerance = 5.0f;

    public GUICommonSettings() {
        super();
        this.yamlEmitter = new Yaml(new GUICommonSettingsRepresenter());
    }

    /**
     * Gets the setting specifying if the application should run in full screen
     * mode.
     * 
     * @return {@code true} if the application should run in full screen mode,
     *         {@code false} if the application should not run in full screen
     *         mode.
     */
    public boolean isFullscreen() {
        try {
            this.settingLock.readLock().lock();
            return this.fullscreen;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting specifying if the application should run in full
     * screen mode.
     * 
     * @param fullscreen
     *            {@code true} if the application should run in full screen
     *            mode, {@code false} if the application should not run in full
     *            screen mode.
     */
    public void setFullscreen(final boolean fullscreen) {
        try {
            this.settingLock.writeLock().lock();
            if (this.fullscreen == fullscreen) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify.
            this.fullscreen = fullscreen;
            for (OBSERVER obs : this.observers) {
                obs.isFullscreenChanged(fullscreen);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Gets the setting specifying if the main frame of the application should
     * be maximized.
     * 
     * @return {@code true} if the main frame of the application should be
     *         maximized, {@code false} if the main frame of the application
     *         should not be maximized.
     */
    public boolean isMaximized() {
        try {
            this.settingLock.readLock().lock();
            return this.maximized;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting specifying if the main frame of the application
     * should be maximized.
     * 
     * @param maximized
     *            {@code true} if the main frame of the application should be
     *            maximized, {@code false} if the main frame of the application
     *            should not be maximized.
     */
    public void setMaximized(final boolean maximized) {
        try {
            this.settingLock.writeLock().lock();
            if (this.maximized == maximized) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify.
            this.maximized = maximized;
            for (OBSERVER obs : this.observers) {
                obs.isMaximizedChanged(maximized);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting specifying the application screen location, i.e. the
     * screen coordinates for the top left corner of the main frame of the
     * application.
     * 
     * @return The screen coordinates of the top left corner of the main frame
     *         of the application wrapped in a {@link Point}. The returned
     *         {@link Point} instance is a copy of the setting value and hence
     *         <b>not</b> a direct reference to the setting value. This is done
     *         in order to protect against reference leak which could result in
     *         unsynchronized access or modification of the setting value.
     */
    public Point getAppLocation() {
        // TODO does YamlBeans handle this getter correctly?
        try {
            this.settingLock.readLock().lock();
            return new Point(this.appLocation);
        } finally {
            this.settingLock.readLock().unlock();
        }

    }

    /**
     * Changes the setting specifying the application screen location, i.e. the
     * screen coordinates for the top left corner of the main frame of the
     * application.
     * 
     * @param newAppLocation
     *            The new application screen location. Note: this setter creates
     *            a copy of this argument so that no client will have a direct
     *            reference to the setting value. This is done in order to
     *            ensure that all accesses to, or modifications of, the fields
     *            of {@code GUICommonSettings} remain synchronized.
     */
    public void setAppLocation(Point newAppLocation) {
        try {
            this.settingLock.writeLock().lock();
            Point copy = new Point(newAppLocation);
            // Point equality is based on (x,y) comparison, see docs.
            if (this.appLocation.equals(copy)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify.
            this.appLocation = copy;
            for (OBSERVER obs : this.observers) {
                // Feed each observer with its own Point instance.
                obs.appScreenLocationChanged(new Point(this.appLocation));
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting specifying the dimensions of the main frame of the
     * application.
     * 
     * @return The dimensions of the main frame of the application wrapped in a
     *         {@link Dimension} instance. The returned {@link Dimension}
     *         instance is a copy of the setting value and hence <b>not</b> a
     *         direct reference to the setting value. This is done in order to
     *         protect against reference leak which could result in
     *         unsynchronized access or modification of the setting value.
     */
    public Dimension getAppDimensions() {
        // TODO does YamlBeans handle this getter correctly?
        try {
            this.settingLock.readLock().lock();
            return new Dimension(this.appDimensions);
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting specifying the dimensions of the main frame of the
     * application.
     * 
     * @param newAppDimensions
     *            the appDimensions to set
     */
    public void setAppDimensions(Dimension newAppDimensions) {
        // TODO does YamlBeans handle this setter correctly?
        try {
            this.settingLock.writeLock().lock();
            Dimension copy = new Dimension(newAppDimensions);
            // Equality is value based, see docs.
            if (this.appDimensions.equals(copy)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify.
            this.appDimensions = copy;
            for (OBSERVER obs : this.observers) {
                // Feed each observer with its own Dimension instance.
                obs.appDimensionsChanged(new Dimension(this.appDimensions));
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
     * A specialized {@link Representer} that skips recursive properties in
     * {@link Dimension} and {@link Point} objects.
     * 
     * @author Janus Varmarken
     * 
     */
    protected static class GUICommonSettingsRepresenter extends Representer {

        @Override
        protected NodeTuple representJavaBeanProperty(Object javaBean,
                Property property, Object propertyValue, Tag customTag) {
            if (javaBean instanceof Dimension
                    && "size".equals(property.getName())) {
                // Do not serialize as it produces infinite recursion.
                return null;
            } else if (javaBean instanceof Point
                    && "location".equals(property.getName())) {
                // Do not serialize as it produces infinite recursion.
                return null;
            }
            return super.representJavaBeanProperty(javaBean, property,
                    propertyValue, customTag);
        }

    }

    /*
     * Begin: Listener methods that are only used if this instance observes
     * another instance of this class.
     */

    @Override
    public void isFullscreenChanged(boolean fullscreen) {
        // Obey to change in observed instance.
        this.setFullscreen(fullscreen);
    }

    @Override
    public void isMaximizedChanged(boolean maximized) {
        // Obey to change in observed instance.
        this.setMaximized(maximized);
    }

    @Override
    public void appDimensionsChanged(Dimension newDimension) {
        // Obey to change in observed instance.
        this.setAppDimensions(newDimension);
    }

    @Override
    public void appScreenLocationChanged(Point newLocation) {
        // Obey to change in observed instance.
        this.setAppLocation(newLocation);
    }

    @Override
    public void graphicInteractToleranceChanged(float newValue) {
        // Obey to change in observed instance.
        this.setGraphicInteractTolerance(newValue);
    }

    /*
     * End: Listener methods that are only used if this instance observes
     * another instance of this class.
     */
}
