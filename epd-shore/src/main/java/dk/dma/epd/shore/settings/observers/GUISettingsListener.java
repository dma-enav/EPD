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
package dk.dma.epd.shore.settings.observers;

import dk.dma.epd.common.prototype.settings.observers.GUICommonSettingsListener;
import dk.dma.epd.shore.settings.gui.GUISettings;

/**
 * Interface for observing a {@link GUISettings} for changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface GUISettingsListener extends GUICommonSettingsListener {

    /**
     * Invoked when {@link GUISettings#getWorkspace()} has changed.
     * 
     * @param workspacePath
     *            See return value of {@link GUISettings#getWorkspace()}.
     */
    void workspaceChanged(String workspacePath);

}
