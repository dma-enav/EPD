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

import java.util.List;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;

public class DatumPointDataSARIS extends SARData {

    private static final long serialVersionUID = 1L;

    private List<SARISTarget> sarisTarget;

    public DatumPointDataSARIS(String sarID, DateTime TLKP, DateTime CSS, Position LKP, double x, double y, double safetyFactor,
            int searchObject) {
        super(sarID, TLKP, CSS, LKP, x, y, safetyFactor, searchObject);
    }

}
