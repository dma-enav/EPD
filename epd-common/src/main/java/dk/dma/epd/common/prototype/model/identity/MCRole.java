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
package dk.dma.epd.common.prototype.model.identity;

/**
 * Role of an actor in the maritime cloud.
 * 
 * @author David Andersen Camre
 * 
 */
public enum MCRole {
    SHP, SHO, VTS, MRC, PRT, COM, PLT, AUH, MAS, OTH;

    
    public String getDescription() {
        switch (this) {
        case VTS:
            return "Vessel traffic service center";
        case MRC:
            return "Maritime Rescue Coordination Centre";
        default:
            return this.toString();
        }
    }

    public String toLongString() {

        switch (this) {
        case SHP:
            return "Ship";
        case SHO:
            return "Shipowner";
        case VTS:
            return "VTS";
        case PRT:
            return "Port";
        case COM:
            return "Commercial";
        case PLT:
            return "Pilot";
        case AUH:
            return "Authority";
        case MAS:
            return "MAS";
        case OTH:
            return "Other";
        default:
            return this.name();
        }
    }
}
