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
package dk.dma.epd.common.prototype.settings.handlers;

/**
 * Interface for observing a {@link MetocHandlerCommonSettings} for changes.
 * 
 * @author Janus Varmarken
 */
public interface IMetocHandlerCommonSettingsObserver extends
        IHandlerSettingsObserver {
    /**
     * Invoked when {@link MetocHandlerCommonSettings#getMetocTtl()} has
     * changed.
     * 
     * @param newMetocTtl
     *            The new METOC time to live in minutes. See
     *            {@link MetocHandlerCommonSettings#getMetocTtl()} for more
     *            details.
     */
    void metocTtlChanged(int newMetocTtl);

    /**
     * Invoked when
     * {@link MetocHandlerCommonSettings#getActiveRouteMetocPollInterval()} has
     * changed.
     * 
     * @param newInterval
     *            The new interval. See
     *            {@link MetocHandlerCommonSettings#getActiveRouteMetocPollInterval()}
     *            for more details.
     */
    void activeRouteMetocPollIntervalChanged(int newInterval);

    /**
     * Invoked when
     * {@link MetocHandlerCommonSettings#getMetocTimeDiffTolerance()} has
     * changed.
     * 
     * @param metocTimeDiffTolerance
     *            The new tolerance. See
     *            {@link MetocHandlerCommonSettings#getMetocTimeDiffTolerance()}
     *            for more details.
     */
    void metocTimeDiffToleranceChanged(int metocTimeDiffTolerance);
}
