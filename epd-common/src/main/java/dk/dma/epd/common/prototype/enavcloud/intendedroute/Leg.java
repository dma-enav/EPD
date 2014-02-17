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
package dk.dma.epd.common.prototype.enavcloud.intendedroute;

public class Leg {

    private Double speed;
    private Double xtdPort;
    private Double xtdStarboard;
    private HeadingType headingType;

    public Leg() {

    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getXtdPort() {
        return xtdPort;
    }

    public void setXtdPort(Double xtdPort) {
        this.xtdPort = xtdPort;
    }

    public Double getXtdStarboard() {
        return xtdStarboard;
    }

    public void setXtdStarboard(Double xtdStarboard) {
        this.xtdStarboard = xtdStarboard;
    }

    public HeadingType getHeadingType() {
        return headingType;
    }

    public void setHeadingType(HeadingType headingType) {
        this.headingType = headingType;
    }

}
