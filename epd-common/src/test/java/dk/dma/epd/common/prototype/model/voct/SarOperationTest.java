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
package dk.dma.epd.common.prototype.model.voct;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARWeatherData;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jesper Tejlgaard on 3/17/15.
 */
public class SarOperationTest {

    /**
     * This test has been created while porting functionality to the Embryo/embryo-web project.
     * Assertions has been made to match actual outcome rather than to match expected outcome.
     * The reason for this choice was to be able to write unit test in JavaScript in Embryo/embryo-web.
     */
    @Test
    public void test(){
        SAROperation operation = new SAROperation(SAR_TYPE.RAPID_RESPONSE);

        DateTime now = DateTime.now();
        DateTime lastKnowPositionTs = now.minusHours(1);
        Position lastKnowPosition = Position.create(61, -51);
        DateTime css = now;
        double x = 0.011;
        double y = 0.068;
        double safetyFactor = 1.0;

        RapidResponseData data = new RapidResponseData("1", lastKnowPositionTs, css, lastKnowPosition, x,  y, safetyFactor, 0) ;

        List<SARWeatherData> surfaceDriftData = new ArrayList<>();
        surfaceDriftData.add(new SARWeatherData(45.0, 5.0, 15.0, 30.0, lastKnowPositionTs));

        data.setWeatherPoints(surfaceDriftData);

        operation.startRapidResponseCalculations(data);

        assertEquals("61 03.328N", data.getDatum().getLatitudeAsString());
        assertEquals("050 52.939W", data.getDatum().getLongitudeAsString());

        assertEquals(45.78003035557367, data.getRdvDirection(), 0.0);
        assertEquals(4.7754450213160355, data.getRdvDistance(), 0.0);
        assertEquals(4.7754450213160355, data.getRdvSpeed(), 0.0);
        assertEquals(1.5116335063948105, data.getRadius(), 0.0);

        assertEquals("61 05.464N", data.getA().getLatitudeAsString());
        assertEquals("050 52.880W", data.getA().getLongitudeAsString());

        assertEquals("61 03.299N", data.getB().getLatitudeAsString());
        assertEquals("050 48.524W", data.getB().getLongitudeAsString());

        assertEquals("61 01.191N", data.getC().getLatitudeAsString());
        assertEquals("050 53.001W", data.getC().getLongitudeAsString());

        assertEquals("61 03.357N", data.getD().getLatitudeAsString());
        assertEquals("050 57.352W", data.getD().getLongitudeAsString());
    }
}
