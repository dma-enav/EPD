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
package dk.dma.epd.ship.monalisa;

import dk.dma.epd.ship.route.sspa.RouteresponseType;

public class SSPAResponse {

    private RouteresponseType monaLisaResponse;
    private String errorMessage;
    private boolean isValid = true;
    
    public SSPAResponse(RouteresponseType monaLisaResponse, String errorMessage) {
        if (monaLisaResponse == null){
            isValid = false;
        }
        this.monaLisaResponse = monaLisaResponse;
        this.errorMessage = errorMessage;
    }

    public RouteresponseType getMonaLisaResponse() {
        return monaLisaResponse;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
    
    public boolean isValid(){
        return isValid;
    }

    
    
}
