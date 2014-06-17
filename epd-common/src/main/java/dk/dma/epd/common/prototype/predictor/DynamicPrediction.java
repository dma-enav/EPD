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
