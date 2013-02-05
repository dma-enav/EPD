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
package dk.dma.epd.common.prototype.communication.webservice;

import java.util.HashMap;

/**
 * Error codes for shore services
*/
public final class ShoreServiceErrorCode {
    
    public static final int OK = 0;
    public static final int NO_CONTACT = 1;
    public static final int SERVER_ERROR = 2;
    public static final int NO_OWN_SHIP_DATA = 3;
    public static final int NO_VALID_GPS_DATA = 4;
    public static final int INTERNAL_ERROR = 5;
    public static final int NO_CONNECTION_TO_SERVER = 6;
    public static final int INVALID_RESPONSE = 7;
    public static final int SERVICE_ERROR = 8;    
        
    private static ShoreServiceErrorCode instance;
    
    private HashMap<Integer, String> errorMsg = new HashMap<>();
    
    private ShoreServiceErrorCode() {
        errorMsg.put(OK, "OK");
        errorMsg.put(NO_CONTACT, "No contact to shore server");
        errorMsg.put(SERVER_ERROR, "Error on shore server");
        errorMsg.put(NO_OWN_SHIP_DATA, "No own ship data available");
        errorMsg.put(NO_VALID_GPS_DATA, "No valid GPS data");
        errorMsg.put(INTERNAL_ERROR, "Internal system error");
        errorMsg.put(NO_CONNECTION_TO_SERVER, "No connection to shore");
        errorMsg.put(INVALID_RESPONSE, "Invalid response");
        errorMsg.put(SERVICE_ERROR, "Service error");
    }
    
    public static String getErrorMessage(int errorCode) {
        ShoreServiceErrorCode errorCodes = getInstance();
        if (errorCodes.errorMsg.containsKey(errorCode)) {
            return errorCodes.errorMsg.get(errorCode);
        }
        return "Unknown error";
    }
    
    public static ShoreServiceErrorCode getInstance() {
        synchronized (ShoreServiceErrorCode.class) {
            if (instance == null) {
                instance = new ShoreServiceErrorCode();
            }
            return instance;            
        }
    }

}
