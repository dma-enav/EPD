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
 * @author Janus Varmarken
 */
public class AisHandlerCommonSettings extends HandlerSettings<OBSERVER> {
    private int sartPrefix = 970;
    private String[] simulatedSartMmsi = {}; // Specify comma-separated mmsi
                                             // list to simulate SarTarget's

    private boolean strict = true; // Strict timeout rules

    /**
     * In minutes.
     */
    private int pastTrackMaxTime = 4 * 60;

    /**
     * In minutes. TODO consider moving to layer settings.
     */
    private int pastTrackDisplayTime = 30;

    /**
     * In meters. TODO move to handler settings.
     */
    private int pastTrackMinDist = 100;

    /**
     * In meters. TODO move to ship specific handler settings or reuse
     * {@link #pastTrackMinDist} by creating an instance for ownship(handler).
     */
    private int pastTrackOwnShipMinDist = 20;
}
