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
package dk.dma.epd.shore.layers.voct;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.PastTrackSortedSet;
import dk.dma.epd.common.prototype.layers.ais.PastTrackGraphic;
import dk.dma.epd.shore.voct.SRUCommunicationObject;

public class SRUObject extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private SRUVesselGraphic sruVesselGraphic;
    private PastTrackGraphic pastTrackGraphic = new PastTrackGraphic();
//    private IntendedRouteGraphic routeGraphic = new IntendedRouteGraphic();

    private SRUCommunicationObject sruCommunicationObject;
    PastTrackSortedSet pastTrack = new PastTrackSortedSet();

    /**
     * SRU Object creation, the SRU handles its own position, pasttrack graphics
     * and intendedroute
     * 
     * @param sruCommunicationObject
     */

    public SRUObject(SRUCommunicationObject sruCommunicationObject) {
        super();

        this.sruCommunicationObject = sruCommunicationObject;

        sruVesselGraphic = new SRUVesselGraphic(sruCommunicationObject.getSru()
                .getMmsi());

        this.add(sruVesselGraphic);
//        this.add(routeGraphic);
        this.add(pastTrackGraphic);
        
        
    }

    @Override
    public boolean isVisible(){
        return sruCommunicationObject.isVisible();
    }
    
    public void updateSRU() {

        Position latestPosition = sruCommunicationObject.getPositions().get(
                sruCommunicationObject.getPositions().size() - 1);

        pastTrack.addPosition(latestPosition, 1);

        System.out.println(latestPosition);

        if (sruVesselGraphic.getLat() != latestPosition.getLatitude()
                && sruVesselGraphic.getLon() != latestPosition.getLongitude()) {
            sruVesselGraphic.setLocation(latestPosition.getLatitude(),
                    latestPosition.getLongitude());

//            pastTrackGraphic.update(pastTrack, latestPosition);
        }

        if (sruVesselGraphic.getTrueHeading() != sruCommunicationObject
                .getHeading()) {
            sruVesselGraphic.setHeading(sruCommunicationObject.getHeading());
        }

//        if (sruCommunicationObject.getIntendedSearchPattern() != null) {
//            routeGraphic.update(null, "", new CloudIntendedRoute(
//                    sruCommunicationObject.getIntendedSearchPattern()
//                            .getFullRouteData()), latestPosition);
//        }

        // if (sruVesselGraphic.getLat() ==)

        // Position might have changed
        // If position has changed also update pasttrack graphics

        // Heading could also have changed

        // Possible new route

    }

}
