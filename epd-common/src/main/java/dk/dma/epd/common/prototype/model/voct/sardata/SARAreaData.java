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
package dk.dma.epd.common.prototype.model.voct.sardata;

import dk.dma.enav.model.geometry.Position;

public class SARAreaData {

    Position A;
    Position B;
    Position C;
    Position D;
    Position centre;
    double breadth;
    double length;

    /**
     * @param a
     * @param b
     * @param c
     * @param d
     * @param centre
     * @param breadth
     * @param length
     */
    public SARAreaData(Position a, Position b, Position c, Position d, Position centre, double breadth, double length) {
        A = a;
        B = b;
        C = c;
        D = d;
        this.centre = centre;
        this.breadth = breadth;
        this.length = length;
    }

    /**
     * @return the a
     */
    public Position getA() {
        return A;
    }

    /**
     * @param a
     *            the a to set
     */
    public void setA(Position a) {
        A = a;
    }

    /**
     * @return the b
     */
    public Position getB() {
        return B;
    }

    /**
     * @param b
     *            the b to set
     */
    public void setB(Position b) {
        B = b;
    }

    /**
     * @return the c
     */
    public Position getC() {
        return C;
    }

    /**
     * @param c
     *            the c to set
     */
    public void setC(Position c) {
        C = c;
    }

    /**
     * @return the d
     */
    public Position getD() {
        return D;
    }

    /**
     * @param d
     *            the d to set
     */
    public void setD(Position d) {
        D = d;
    }

    /**
     * @return the centre
     */
    public Position getCentre() {
        return centre;
    }

    /**
     * @param centre
     *            the centre to set
     */
    public void setCentre(Position centre) {
        this.centre = centre;
    }

    /**
     * @return the breadth
     */
    public double getBreadth() {
        return breadth;
    }

    /**
     * @param breadth
     *            the breadth to set
     */
    public void setBreadth(double breadth) {
        this.breadth = breadth;
    }

    /**
     * @return the length
     */
    public double getLength() {
        return length;
    }

    /**
     * @param length
     *            the length to set
     */
    public void setLength(double length) {
        this.length = length;
    }

}
