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

import dk.dma.epd.common.prototype.settings.gui.GUICommonSettings;
import dk.dma.epd.common.prototype.settings.layers.LayerSettings;

/**
 * Extends common GUI settings (see {@link GUICommonSettings}) with GUI settings
 * specific to EPD Ship. GUI settings are primarily targeted at Swing components
 * such as frames, menus, docks etc. Settings specifying how vessels or other
 * units are to be painted on a layer should be placed in {@link LayerSettings}
 * or any of its subclasses.
 * 
 * @author Janus Varmarken
 */
public class GUISettings<OBSERVER extends GUISettings.IObserver> extends
        GUICommonSettings<OBSERVER> {

    /**
     * Setting specifying if the dock that displays AIS target details should
     * always open when an AIS target has been selected.
     */
    private boolean alwaysOpenDock = true;

    /**
     * Setting specifying if an "Open AIS target details dock?" dialog should be
     * shown when an AIS target has been selected and the AIS target details
     * dock is not open.
     */
    private boolean showDockMessage = true;

    /**
     * Get the setting that specifies if the dock that displays AIS target
     * details should always open when an AIS target has been selected.
     * 
     * @return {@code true} if the AIS target details dock should automatically
     *         open when an AIS target has been selected, {@code false} if the
     *         AIS target details dock should not automatically open when an AIS
     *         target has been selected.
     */
    public boolean isAlwaysOpenDock() {
        try {
            this.settingLock.readLock().lock();
            return this.alwaysOpenDock;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if the dock that displays AIS target
     * details should always open when an AIS target has been selected.
     * 
     * @param alwaysOpenDock
     *            {@code true} if the AIS target details dock should
     *            automatically open when an AIS target has been selected,
     *            {@code false} if the AIS target details dock should not
     *            automatically open when an AIS target has been selected.
     */
    public void setAlwaysOpenDock(final boolean alwaysOpenDock) {
        try {
            this.settingLock.writeLock().lock();
            if (this.alwaysOpenDock == alwaysOpenDock) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify.
            this.alwaysOpenDock = alwaysOpenDock;
            for (OBSERVER obs : this.observers) {
                obs.isAlwaysOpenDockChanged(alwaysOpenDock);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Get the setting that specifies if an "Open AIS target details dock?"
     * should be shown when an AIS target has been selected and the AIS target
     * details dock is not currently open.
     * 
     * @return {@code true} if the "Open AIS target details dock?" dialog should
     *         be shown, {@code false} if it shouldn't.
     */
    public boolean isShowDockMessage() {
        try {
            this.settingLock.readLock().lock();
            return this.showDockMessage;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies if an "Open AIS target details dock?"
     * should be shown when an AIS target has been selected and the AIS target
     * details dock is not currently open.
     * 
     * @param showDockMessage
     *            {@code true} if the "Open AIS target details dock?" dialog
     *            should be shown, {@code false} if it shouldn't.
     */
    public void setShowDockMessage(final boolean showDockMessage) {
        try {
            this.settingLock.writeLock().lock();
            if (this.showDockMessage == showDockMessage) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify.
            this.showDockMessage = showDockMessage;
            for (OBSERVER obs : this.observers) {
                obs.isShowDockMessageChanged(showDockMessage);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
    
    /**
     * Interface for observing a {@link GUISettings} for changes.
     * 
     * @author Janus Varmarken
     */
    public interface IObserver extends GUICommonSettings.IObserver {

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
}
