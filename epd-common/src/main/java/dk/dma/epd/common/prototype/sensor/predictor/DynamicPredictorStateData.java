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

import dk.dma.enav.model.geometry.Position;
import net.jcip.annotations.Immutable;

@Immutable
public class DynamicPredictorStateData extends DynamicPredictorData {

    private final int count;
    private final float length;
    private final float width;

    @JsonCreator
    public DynamicPredictorStateData(@JsonProperty("count") int count,
            @JsonProperty("position") Position position,
            @JsonProperty("heading") float heading,
            @JsonProperty("cog") Float cog, @JsonProperty("sog") Float sog,
            @JsonProperty("length") float length,
            @JsonProperty("width") float width, @JsonProperty("time") long time) {
        super(position, heading, cog, sog, time);
        this.count = count;
        this.length = length;
        this.width = width;
    }

    public int getCount() {
        return count;
    }

    public float getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("DynamicPredictorStateData [count=").append(count)
                .append(", length=").append(length).append(", width=")
                .append(width).append(super.toString());
        return builder.toString();
    }

}
