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
public class MetocHandlerCommonSettings extends HandlerSettings<OBSERVER> {
    
    /**
     * How long should METOC for route be considered valid
     */
    private int metocTtl = 60; // min
    /**
     * The minimum interval between metoc polls for active route 0 - never
     */
    private int activeRouteMetocPollInterval = 5; // min
    /**
     * The tolerance of how long we may drift from plan before METOC is considered invalid 
     */
    private int metocTimeDiffTolerance = 15; // 15 min
}
