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

import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

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
public class GUISettings<OBSERVER extends IGUISettingsObserver> extends
        GUICommonSettings<OBSERVER> {

    /**
     * Key used in the properties file for the setting that specifies if the
     * dock displaying AIS target details should always open when an AIS target
     * has been selected (see {@link #alwaysOpenDock}).
     */
    private static final String KEY_ALWAYS_OPEN_DOCK = "alwaysOpenDock";

    /**
     * Key used in the properties file for the {@link #showDockMessage} setting.
     */
    private static final String KEY_SHOW_DOCK_MSG = "showDockMessage";

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
     * {@inheritDoc}
     */
    @Override
    protected void onLoadSuccess(Properties settings) {
        // Acquire lock in order for settings to be loaded as a single batch.
        this.settingLock.writeLock().lock();
        // Allow super class to initialize its settings fields.
        super.onLoadSuccess(settings);
        // Now initialize own fields with values from properties instance.
        this.setAlwaysOpenDock(PropUtils.booleanFromProperties(settings,
                KEY_ALWAYS_OPEN_DOCK, this.alwaysOpenDock));
        this.setShowDockMessage(PropUtils.booleanFromProperties(settings,
                KEY_SHOW_DOCK_MSG, this.showDockMessage));
        // Batch loaded, release lock.
        this.settingLock.writeLock().unlock();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Properties onSaveSettings() {
        // Acquire read lock in order to save settings as a batch.
        this.settingLock.readLock().lock();
        Properties toSave = super.onSaveSettings();
        toSave.setProperty(KEY_ALWAYS_OPEN_DOCK,
                Boolean.toString(this.alwaysOpenDock));
        toSave.setProperty(KEY_SHOW_DOCK_MSG,
                Boolean.toString(this.showDockMessage));
        // Finished batch save, release lock.
        this.settingLock.readLock().unlock();
        return toSave;
    }
}
