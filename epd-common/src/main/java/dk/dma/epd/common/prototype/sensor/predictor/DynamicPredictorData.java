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

import net.jcip.annotations.Immutable;
import dk.dma.enav.model.geometry.Position;

/**
 * Class representing dynamic predictor data
 */
@Immutable
public abstract class DynamicPredictorData {

    private final Position position;
    private final Float sog;
    private final float heading;
    private final Float cog;
    private final long time;

    public DynamicPredictorData(Position position, float heading, Float cog, Float sog, long time) {
        super();
        this.position = position;
        this.heading = heading;
        this.cog = cog;
        this.sog = sog;
        this.time = time;
    }

    public Position getPosition() {
        return position;
    }

    public Float getSog() {
        return sog;
    }

    public float getHeading() {
        return heading;
    }

    public Float getCog() {
        return cog;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(", position=").append(position).append(", sog=").append(sog).append(", heading=")
                .append(heading).append(", cog=").append(cog).append(", time=").append(time).append("]");
        return builder.toString();
    }

}
