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
    private final Double sog;
    private final double heading;
    private final Double cog;
    private final long time;

    public DynamicPredictorData(Position position, double heading, Double cog, Double sog, long time) {
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

    public Double getSog() {
        return sog;
    }

    public double getHeading() {
        return heading;
    }

    public Double getCog() {
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
