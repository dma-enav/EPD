/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
