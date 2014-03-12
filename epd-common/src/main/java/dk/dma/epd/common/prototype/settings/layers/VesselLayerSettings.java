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
package dk.dma.epd.common.prototype.settings.layers;

/**
 * @author Janus Varmarken
 */
public class VesselLayerSettings extends LayerSettings<OBSERVER> {
 
    /**
     * The minimum length of the COG vector in minutes.
     */
    private int cogVectorLengthMin = 1;

    /**
     * The maximum length of the COG vector in minutes.
     */
    private int cogVectorLengthMax = 8;

    /**
     * The scale interval that separates two values for cogVectorLength. E.g.: if assigned a value of x, the valid map scale for a
     * cogVectorLength of n minutes is in ](n-1) * x; n * x].
     */
    private float cogVectorLengthScaleInterval = 5000.0f;
    private float cogVectorHideBelow = 0.1f;   
    
    
    private int pastTrackMaxTime = 4 * 60; // In minutes
    private int pastTrackDisplayTime = 30; // In minutes
    private int pastTrackMinDist = 100; // In meters
    private int pastTrackOwnShipMinDist = 20; // In meters
}
