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
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
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
    public void testRapidResponseWithOneSurfarceDriftPoint(){
        SAROperation operation = new SAROperation(SAR_TYPE.RAPID_RESPONSE);

        DateTime now = DateTime.now();
        DateTime lastKnowPositionTs = now.minusHours(1);
        Position lastKnowPosition = Position.create(61, -51);
        DateTime css = now;
        double xError = 1.0;
        double yError = 0.1;
        double safetyFactor = 1.0;

        RapidResponseData data = new RapidResponseData("1", lastKnowPositionTs, css, lastKnowPosition, xError,  yError, safetyFactor, 0) ;

        List<SARWeatherData> surfaceDriftData = new ArrayList<>();
        surfaceDriftData.add(new SARWeatherData(45.0, 5.0, 15.0, 30.0, lastKnowPositionTs));

        data.setWeatherPoints(surfaceDriftData);

        operation.startRapidResponseCalculations(data);

        assertEquals("61 03.328N", data.getDatum().getLatitudeAsString());
        assertEquals("050 52.939W", data.getDatum().getLongitudeAsString());

        assertEquals(45.78003035557367, data.getRdvDirection(), 0.0);
        assertEquals(4.7754450213160355, data.getRdvDistance(), 0.0);
        assertEquals(4.7754450213160355, data.getRdvSpeed(), 0.0);
        assertEquals(2.5326335063948107, data.getRadius(), 0.0);

        assertEquals("61 06.905N", data.getA().getLatitudeAsString());
        assertEquals("050 52.842W", data.getA().getLongitudeAsString());

        assertEquals("61 03.278N", data.getB().getLatitudeAsString());
        assertEquals("050 45.541W", data.getB().getLongitudeAsString());

        assertEquals("60 59.748N", data.getC().getLatitudeAsString());
        assertEquals("050 53.043W", data.getC().getLongitudeAsString());

        assertEquals("61 03.375N", data.getD().getLatitudeAsString());
        assertEquals("051 00.331W", data.getD().getLongitudeAsString());
    }

    /**
     * This test has been created while porting functionality to the Embryo/embryo-web project.
     * Assertions has been made to match actual outcome rather than to match expected outcome.
     * The reason for this choice was to be able to write unit test in JavaScript in Embryo/embryo-web.
     */
    @Test
    public void testRapidResponseWithTwoSurfarceDriftPoints(){
        SAROperation operation = new SAROperation(SAR_TYPE.RAPID_RESPONSE);

        DateTime now = DateTime.now();
        DateTime lastKnowPositionTs = now.minusHours(1);
        Position lastKnowPosition = Position.create(61, -51);
        DateTime css = now;
        double x = 0.1;
        double y = 0.1;
        double safetyFactor = 1.0;

        RapidResponseData data = new RapidResponseData("1", lastKnowPositionTs, css, lastKnowPosition, x,  y, safetyFactor, 0) ;

        List<SARWeatherData> surfaceDriftData = new ArrayList<>();
        surfaceDriftData.add(new SARWeatherData(45.0, 5.0, 15.0, 30.0, lastKnowPositionTs));
        surfaceDriftData.add(new SARWeatherData(35.0, 8.0, 10.0, 20.0, lastKnowPositionTs.plusMinutes(30)));

        data.setWeatherPoints(surfaceDriftData);

        operation.startRapidResponseCalculations(data);

        assertEquals("61 04.854N", data.getDatum().getLatitudeAsString());
        assertEquals("050 51.794W", data.getDatum().getLongitudeAsString());

        assertEquals(35.37281521097134, data.getRdvDirection(), 0.0);
        assertEquals(3.9141344796902713, data.getRdvDistance(), 0.0);
        assertEquals(7.828268959380543, data.getRdvSpeed(), 0.0);
        assertEquals(1.3742403439070814, data.getRadius(), 0.0);

        assertEquals("61 06.769N", data.getA().getLatitudeAsString());
        assertEquals("050 52.467W", data.getA().getLongitudeAsString());

        assertEquals("61 05.179N", data.getB().getLatitudeAsString());
        assertEquals("050 47.833W", data.getB().getLongitudeAsString());

        assertEquals("61 02.939N", data.getC().getLatitudeAsString());
        assertEquals("050 51.124W", data.getC().getLongitudeAsString());

        assertEquals("61 04.529N", data.getD().getLatitudeAsString());
        assertEquals("050 55.753W", data.getD().getLongitudeAsString());
    }

    /**
     * This test has been created while porting functionality to the Embryo/embryo-web project.
     * Assertions has been made to match actual outcome rather than to match expected outcome.
     * The reason for this choice was to be able to write unit test in JavaScript in Embryo/embryo-web.
     */
    @Test
    public void testDatumPointWithOneSurfarceDriftPoint(){
        SAROperation operation = new SAROperation(SAR_TYPE.DATUM_POINT);

        double allowedUncertainty = 0.00005;

        DateTime now = DateTime.now();
        DateTime lastKnowPositionTs = now.minusHours(1);
        Position lastKnowPosition = Position.create(61, -51);
        DateTime css = now;
        double xError = 1.0;
        double yError = 0.1;
        double safetyFactor = 1.0;

        DatumPointData data = new DatumPointData("1", lastKnowPositionTs, css, lastKnowPosition, xError,  yError, safetyFactor, 0) ;

        List<SARWeatherData> surfaceDriftData = new ArrayList<>();
        surfaceDriftData.add(new SARWeatherData(45.0, 5.0, 15.0, 30.0, lastKnowPositionTs));

        data.setWeatherPoints(surfaceDriftData);

        operation.startDatumPointCalculations(data);

        assertEquals("61 03.328N", data.getDatumDownWind().getLatitudeAsString());
        assertEquals("050 52.939W", data.getDatumDownWind().getLongitudeAsString());
        assertEquals(45.780030, data.getRdvDirectionDownWind(), allowedUncertainty);
        assertEquals(4.7754450213160355, data.getRdvDistanceDownWind(), allowedUncertainty);
        assertEquals(4.7754450213160355, data.getRdvSpeedDownWind(), allowedUncertainty);
        assertEquals(2.5326335063948107, data.getRadiusDownWind(), allowedUncertainty);

        assertEquals("61 03.413N", data.getDatumMax().getLatitudeAsString());
        assertEquals("050 53.115W", data.getDatumMax().getLongitudeAsString());
        assertEquals(44.331598, data.getRdvDirectionMax(), allowedUncertainty);
        assertEquals(4.8383742, data.getRdvDistanceMax(), allowedUncertainty);
        assertEquals(4.7752103, data.getRdvSpeedMax(), allowedUncertainty);
        assertEquals(2.5325631, data.getRadiusMax(), allowedUncertainty);

        assertEquals("61 03.297N", data.getDatumMin().getLatitudeAsString());
        assertEquals("050 52.699W", data.getDatumMin().getLongitudeAsString());
        assertEquals(47.008245, data.getRdvDirectionMin(), allowedUncertainty);
        assertEquals(4.8383743, data.getRdvDistanceMin(), allowedUncertainty);
        assertEquals(4.8383743, data.getRdvSpeedMin(), allowedUncertainty);
        assertEquals(2.5515123, data.getRadiusMin(), allowedUncertainty);


        assertEquals("60 59.801N", data.getA().getLatitudeAsString());
        assertEquals("050 50.788W", data.getA().getLongitudeAsString());

        assertEquals("61 02.460N", data.getB().getLatitudeAsString());
        assertEquals("051 00.292W", data.getB().getLongitudeAsString());

        assertEquals("61 06.878N", data.getC().getLatitudeAsString());
        assertEquals("050 55.017W", data.getC().getLongitudeAsString());

        assertEquals("61 04.229N", data.getD().getLatitudeAsString());
        assertEquals("050 45.497W", data.getD().getLongitudeAsString());
    }

}
