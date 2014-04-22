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
package dk.dma.epd.shore.settings.gui;

import dk.dma.epd.common.prototype.settings.gui.GUICommonSettings;

/**
 * Extends {@link GUICommonSettings} with shore specific GUI settings.
 * 
 * @author Janus Varmarken
 */
public class GUISettings<OBSERVER extends GUISettings.IObserver> extends
        GUICommonSettings<OBSERVER> {
    /**
     * Points to a file containing the workspace layout.
     */
    private String workspace = "";

    /**
     * Gets the setting that specifies the path to the workspace file.
     * 
     * @return The path to the workspace file.
     */
    public String getWorkspace() {
        try {
            this.settingLock.readLock().lock();
            return this.workspace;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Changes the setting that specifies the path to the workspace file.
     * 
     * @param workspace
     *            The new path to the workspace file.
     */
    public void setWorkspace(final String workspace) {
        try {
            this.settingLock.writeLock().lock();
            if (this.workspace.equals(workspace)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.workspace = workspace;
            for (OBSERVER obs : this.observers) {
                obs.workspaceChanged(workspace);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    /**
     * Interface for observing a {@link GUISettings} for changes.
     * 
     * @author Janus Varmarken
     * 
     */
    public interface IObserver extends GUICommonSettings.IObserver {

        /**
         * Invoked when {@link GUISettings#getWorkspace()} has changed.
         * 
         * @param workspacePath
         *            See return value of {@link GUISettings#getWorkspace()}.
         */
        void workspaceChanged(String workspacePath);
    }
}
