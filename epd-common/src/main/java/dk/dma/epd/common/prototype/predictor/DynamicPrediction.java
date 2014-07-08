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
package dk.dma.epd.common.prototype.predictor;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import net.maritimecloud.net.broadcast.BroadcastMessage;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;

/**
 * Encapsulation of a full dynamic prediction. A dynamic prediction consists of
 * a list of prediction data points and vessel identification data.
 * 
 * @author Janus Varmarken
 */
public class DynamicPrediction extends BroadcastMessage {

    /**
     * MMSI of vessel.
     */
    private final long mmsi;

    /**
     * Dynamic prediction meta/state data.
     */
    private final DynamicPredictorStateData headerData;

    /**
     * List of prediction data points that make up this dynamic prediction.
     */
    private final List<DynamicPredictorPredictionData> predictionDataPoints;

    @JsonCreator
    public DynamicPrediction(
            @JsonProperty("mmsi") long mmsi,
            @JsonProperty("headerData") DynamicPredictorStateData headerData,
            @JsonProperty("predictionDataPoints") List<DynamicPredictorPredictionData> predictionDataPoints) {
        this.mmsi = mmsi;
        this.headerData = headerData;
        this.predictionDataPoints = predictionDataPoints;
    }

    /**
     * Get MMSI of the vessel associated with this prediction.
     * 
     * @return The MMSI of the vessel associated with this prediction.
     */
    public long getMmsi() {
        return this.mmsi;
    }

    /**
     * Get the list of data points used in this prediction.
     * 
     * @return The list of data points used in this prediction.
     */
    public List<DynamicPredictorPredictionData> getPredictionDataPoints() {
        return this.predictionDataPoints;
    }

    /**
     * Gets header data for this dynamic prediction.
     * 
     * @return Header data for this dynamic prediction.
     */
    public DynamicPredictorStateData getHeaderData() {
        return this.headerData;
    }
}
