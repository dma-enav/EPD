/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.common.prototype.sensor.predictor;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    @JsonCreator
    public DynamicPredictorData(@JsonProperty("position") Position position,
            @JsonProperty("heading") float heading,
            @JsonProperty("cog") Float cog, @JsonProperty("sog") Float sog,
            @JsonProperty("time") long time) {
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
        builder.append(", position=").append(position).append(", sog=")
                .append(sog).append(", heading=").append(heading)
                .append(", cog=").append(cog).append(", time=").append(time)
                .append("]");
        return builder.toString();
    }

}
