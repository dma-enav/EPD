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
package dk.dma.epd.common.prototype.settings.observers;

import dk.dma.epd.common.prototype.settings.layers.PastTrackSettings;

/**
 * Interface for observing a {@link PastTrackSettings} for changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface PastTrackSettingsListener {

    /**
     * Invoked when {@link PastTrackSettings#getPastTrackMaxTime()} has changed.
     * 
     * @param maxTime
     *            The new past track max time. See
     *            {@link PastTrackSettings#getPastTrackMaxTime()} for more
     *            details.
     */
    void pastTrackMaxTimeChanged(int maxTime);

    /**
     * Invoked when {@link PastTrackSettings#getPastTrackDisplayTime()} has
     * changed.
     * 
     * @param displayTime
     *            The new past track display time. See
     *            {@link PastTrackSettings#getPastTrackDisplayTime()} for more
     *            details.
     */
    void pastTrackDisplayTimeChanged(int displayTime);

    /**
     * Invoked when {@link PastTrackSettings#getPastTrackMinDist()} has changed.
     * 
     * @param minDist
     *            The new past track minimum distance. See
     *            {@link PastTrackSettings#getPastTrackMinDist()} for more
     *            details.
     */
    void pastTrackMinDistChanged(int minDist);

}
