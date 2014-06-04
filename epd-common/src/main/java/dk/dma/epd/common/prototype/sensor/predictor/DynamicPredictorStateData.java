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
package dk.dma.epd.common.prototype.sensor.predictor;

import dk.dma.enav.model.geometry.Position;
import net.jcip.annotations.Immutable;

@Immutable
public class DynamicPredictorStateData extends DynamicPredictorData {

    private final int count;
    private final int length;
    private final int width;

    public DynamicPredictorStateData(int count, Position position, double heading, Double cog, Double sog, int length,
            int width, long time) {
        super(position, heading, cog, sog, time);
        this.count = count;
        this.length = length;
        this.width = width;
    }
    
    public int getCount() {
        return count;
    }

    public int getLength() {
        return length;
    }
    
    public int getWidth() {
        return width;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DynamicPredictorStateData [count=").append(count).append(", length=").append(length).append(", width=")
                .append(width).append(super.toString());
        return builder.toString();
    }
    
    

}
