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
package dk.dma.epd.common.prototype.layers.msi;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;

/**
 * Graphic for MSI
 */
public abstract class MsiSymbolPosition extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    
    protected MsiMessage msiMessage;
    protected boolean acknowledged;

    public MsiSymbolPosition(MsiMessageExtended message) {
        super();
        this.msiMessage = message.msiMessage;
        MsiLocation msiLocation = msiMessage.getLocation();
        acknowledged = message.acknowledged;        
        
        // Determine where to place MSI symbols
        switch (msiLocation.getLocationType()) {
        case POINT:
        case POINTS:
            /*
             * Place symbol in each point 
             */
            for (MsiPoint point : msiLocation.getPoints()) {
                createSymbol(Position.create(point.getLatitude(), point.getLongitude()));
            }            
            break;
        case POLYGON:
            /*
             * Place symbol in center of polygon
             */
            createSymbol(msiLocation.getCenter());
            break;
        case POLYLINE:
            /*
             * Place a symbol in middle point 
             */
            MsiPoint middle =  msiLocation.getPoints().get(msiLocation.getPoints().size() / 2);
            createSymbol(Position.create(middle.getLatitude(), middle.getLongitude()));
            break;
        default:
            break;
        }
    }

    public abstract void createSymbol(Position geoLocation);
    
    public MsiMessage getMsiMessage() {
        return msiMessage;
    }
}
