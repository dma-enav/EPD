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

public class SARISTarget {

    private String name;
    private String formula;
    private double divergenceAngle;
    private String type;

    /**
     * @param name
     * @param formula
     * @param divergenceAngle
     * @param type
     */
    public SARISTarget(String name, String formula, double divergenceAngle, String type) {
        this.name = name;
        this.formula = formula;
        this.divergenceAngle = divergenceAngle;
        this.type = type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the formula
     */
    public String getFormula() {
        return formula;
    }

    /**
     * @param formula
     *            the formula to set
     */
    public void setFormula(String formula) {
        this.formula = formula;
    }

    /**
     * @return the divergenceAngle
     */
    public double getDivergenceAngle() {
        return divergenceAngle;
    }

    /**
     * @param divergenceAngle
     *            the divergenceAngle to set
     */
    public void setDivergenceAngle(double divergenceAngle) {
        this.divergenceAngle = divergenceAngle;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
