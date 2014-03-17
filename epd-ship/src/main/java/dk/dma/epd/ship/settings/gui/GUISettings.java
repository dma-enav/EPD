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

/**
 * @author Janus Varmarken
 */
public class GUISettings<OBSERVER extends IGUISettingsObserver> extends
        GUICommonSettings<OBSERVER> {

    /**
     * Key used in the properties file for the setting that specifies if the
     * dock should always open. TODO: Get DNC to describe purpose of setting
     * better.
     */
    private static final String KEY_ALWAYS_OPEN_DOCK = "alwaysOpenDock";

    /**
     * Key used in the properties file for the setting that specifies if the
     * dock message should be shown. TODO: Get DNC to describe purpose of
     * setting better.
     */
    private static final String KEY_SHOW_DOCK_MSG = "showDockMessage";

    /**
     * Setting specifying if the dock should always open. TODO: Get DNC to
     * describe purpose of setting better.
     */
    private boolean alwaysOpenDock = true;

    /**
     * Setting specifying if the dock message should be shown. TODO: Get DNC to
     * describe purpose of setting better.
     */
    private boolean showDockMessage = true;

    /**
     * TODO: Fix Javadoc when DNC has described purpose of setting.
     * 
     * @return the alwaysOpenDock
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
     * TODO: Fix Javadoc when DNC has described purpose of setting.
     * 
     * @param alwaysOpenDock
     *            the alwaysOpenDock to set
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
     * TODO: Fix Javadoc when DNC has described purpose of setting.
     * 
     * @return the showDockMessage
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
     * TODO: Fix Javadoc when DNC has described purpose of setting.
     * 
     * @param showDockMessage
     *            the showDockMessage to set
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
    
    // TODO implement onSaveSettings.
}
