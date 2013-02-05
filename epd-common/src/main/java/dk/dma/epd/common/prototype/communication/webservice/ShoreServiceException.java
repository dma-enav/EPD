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

/**
 * Shore service exception 
 */
public class ShoreServiceException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    private int errroCode;
    private String extraMessage;
    
    public ShoreServiceException(int errorCode, String extraMessage) {
        this(errorCode);
        this.extraMessage = extraMessage;
    }
    
    public ShoreServiceException(int errorCode) {
        super(ShoreServiceErrorCode.getErrorMessage(errorCode));
        this.errroCode = errorCode;
    }
    
    public int getErrroCode() {
        return errroCode;
    }
    
    public void setErrroCode(int errroCode) {
        this.errroCode = errroCode;
    }
    
    public void setExtraMessage(String extraMessage) {
        this.extraMessage = extraMessage;
    }
    
    public String getExtraMessage() {
        return extraMessage;
    }

}
