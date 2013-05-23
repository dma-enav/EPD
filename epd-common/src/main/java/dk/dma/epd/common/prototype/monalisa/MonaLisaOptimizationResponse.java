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
package dk.dma.epd.common.prototype.monalisa;

public class MonaLisaOptimizationResponse {

    String type;
    String response;
    boolean valid;
    
    public MonaLisaOptimizationResponse(String message, String response) {
        this.type = message;
        this.response = response;
    }


    public String getType() {
        return type;
    }


    public void setType(String message) {
        this.type = message;
    }


    public String getResponse() {
        return response;
    }


    public void setResponse(String response) {
        this.response = response;
    }
    
    
    
    
}
