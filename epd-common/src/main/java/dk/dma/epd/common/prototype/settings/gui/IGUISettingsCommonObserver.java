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

import dk.dma.epd.common.prototype.settings.ISettingsObserver;

/**
 * Interface used to observe a {@link GUICommonSettings} for changes.
 * 
 * @author Janus Varmarken
 */
public interface IGUISettingsCommonObserver extends ISettingsObserver {

    /**
     * Invoked when the setting, specifying if the application should run in
     * full screen mode, has been changed.
     * 
     * @param fullscreen
     *            {@code true} if the application should run in full screen
     *            mode, {@code false} if the application should not run in full
     *            screen mode.
     */
    void isFullscreenChanged(boolean fullscreen);

    /**
     * Invoked when the setting, specifying if the main frame of the application
     * should be maximized, has been changed.
     * 
     * @param maximized
     *            {@code true} if the main frame of the application should be
     *            maximized, {@code false} if the main frame of the application
     *            should not be maximized.
     */
    void isMaximizedChanged(boolean maximized);

    /**
     * Invoked when the setting, specifying the dimensions of the main frame of
     * the application, has been changed.
     * 
     * @param newDimension
     *            The new dimension value.
     */
    void appDimensionsChanged(Dimension newDimension);

    /**
     * Invoked when the setting, specifying the location of the application on
     * screen, has been changed.
     * 
     * @param newLocation
     *            A {@link Point} representing where the location of the top
     *            left corner of the main frame of the application should be
     *            placed on the screen.
     */
    void appScreenLocationChanged(Point newLocation);

}
