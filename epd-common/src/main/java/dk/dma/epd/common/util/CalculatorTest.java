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
package dk.dma.epd.common.util;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.Heading;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

/**
 * Created by Jesper Tejlgaard on 3/17/15.
 */
public class CalculatorTest {

    /**
     * This test has been created while porting functionality to the Embryo/embryo-web project.
     * Assertions has been made to match actual outcome rather than to match expected outcome.
     * The reason for this choice was to be able to write unit test in JavaScript in Embryo/embryo-web.
     */
    @Test
    public void testFindPosition() throws FormatException{
        double lat = ParseUtils.parseLatitude("61 00.000N");
        double lon = ParseUtils.parseLongitude("051 00.000W");
        Position start = Position.create(lat, lon);

        Position result = Calculator.findPosition(start, 45.0, 9260.0);

        assertNotNull(result);
        assertEquals("61 03.530N", result.getLatitudeAsString());
        assertEquals("050 52.699W", result.getLongitudeAsString());
    }

    /**
     * This test has been created while porting functionality to the Embryo/embryo-web project.
     * Assertions has been made to match actual outcome rather than to match expected outcome.
     * The reason for this choice was to be able to write unit test in JavaScript in Embryo/embryo-web.
     */
    @Test
    public void testRange() throws FormatException{
        double lat = ParseUtils.parseLatitude("61 00.000N");
        double lon = ParseUtils.parseLongitude("051 00.000W");
        Position start = Position.create(lat, lon);

        lat = ParseUtils.parseLatitude("61 03.530N");
        lon = ParseUtils.parseLongitude("050 52.699W");
        Position end = Position.create(lat, lon);

        double result = Calculator.range(start, end, Heading.RL);

        assertEquals(5.000017243489917, result, 0.0);
    }
}
