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
package dk.dma.epd.ship.service.communication.ais;

import dk.dma.ais.reader.SendRequest;

/**
 * Thread for sending intended routes
 */
public class AisIntendedRouteSendThread extends AisSendThread {

    public AisIntendedRouteSendThread(SendRequest sendRequest, AisServices aisServices) {
        super(sendRequest, aisServices);
    }
    
    @Override
    public void run() {
        super.run();
        
        if (abk != null && abk.isSuccess()) {
            aisServices.setLastIntendedRouteBroadcast();
        }
        
    }

}
