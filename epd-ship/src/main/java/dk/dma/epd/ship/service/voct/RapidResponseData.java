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
package dk.dma.epd.ship.service.voct;

import dk.dma.enav.model.geometry.Position;

public class RapidResponseData {

    Position LKP;
    Position datum;
    Position wtc;

    double radius;

    Position A;
    Position B;
    Position C;
    Position D;

    public RapidResponseData(Position lKP, Position datum, Position wtc,
            double radius) {
        super();
        LKP = lKP;
        this.datum = datum;
        this.wtc = wtc;
        this.radius = radius;
    }

    public void setBox(Position A, Position B, Position C, Position D) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
    }

    /**
     * @return the lKP
     */
    public Position getLKP() {
        return LKP;
    }

    /**
     * @return the datum
     */
    public Position getDatum() {
        return datum;
    }

    /**
     * @return the wtc
     */
    public Position getWtc() {
        return wtc;
    }

    /**
     * @return the radius
     */
    public double getRadius() {
        return radius;
    }

    /**
     * @return the a
     */
    public Position getA() {
        return A;
    }

    /**
     * @return the b
     */
    public Position getB() {
        return B;
    }

    /**
     * @return the c
     */
    public Position getC() {
        return C;
    }

    /**
     * @return the d
     */
    public Position getD() {
        return D;
    }

}
