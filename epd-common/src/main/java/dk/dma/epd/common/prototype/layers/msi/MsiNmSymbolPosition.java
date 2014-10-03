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
import dk.dma.epd.common.prototype.notification.MsiNmNotification;

/**
 * Graphic for MSI-NM
 */
public abstract class MsiNmSymbolPosition extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    
    protected MsiNmNotification message;
    protected boolean acknowledged;

    public MsiNmSymbolPosition(MsiNmNotification message) {
        super();
        this.message = message;
        Position centerLocation = message.getLocation();
        acknowledged = message.isAcknowledged();

        createSymbol(centerLocation);
    }

    public abstract void createSymbol(Position geoLocation);
    
    public MsiNmNotification getMsiNmMessage() {
        return message;
    }
}
