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
package dk.dma.epd.ship.settings.gui;

import dk.dma.epd.common.prototype.settings.gui.IGUISettingsCommonObserver;

/**
 * Interface for observing {@link GUISettings} for changes.
 * 
 * @author Janus Varmarken
 */
public interface IGUISettingsObserver extends IGUISettingsCommonObserver {

    /**
     * Invoked when the setting, specifying if the AIS target details dock
     * should always open when an AIS target has been selected (see
     * {@link GUISettings#isAlwaysOpenDock()}), has changed.
     * 
     * @param alwaysOpenDock
     *            The new value. Refer to {@link GUISettings#isAlwaysOpenDock()}
     *            for its interpretation.
     */
    void isAlwaysOpenDockChanged(boolean alwaysOpenDock);

    /**
     * Invoked when the setting, specifying if an
     * "Open AIS target details dock?" dialog should be shown, has changed.
     * Refer to {@link GUISettings#isShowDockMessage()} for details of this
     * setting.
     * 
     * @param showDockMessage
     *            The new value. Refer to
     *            {@link GUISettings#isShowDockMessage()} for its
     *            interpretation.
     */
    void isShowDockMessageChanged(boolean showDockMessage);

}
