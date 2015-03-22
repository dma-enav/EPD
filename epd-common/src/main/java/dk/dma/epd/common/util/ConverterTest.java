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

import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 * Created by Jesper Tejlgaard on 3/17/15.
 */
public class ConverterTest {

    /**
     * This test has been created while porting functionality to the Embryo/embryo-web project.
     * Assertions has been made to match actual outcome rather than to match expected outcome.
     * The reason for this choice was to be able to write unit test in JavaScript in Embryo/embryo-web.
     */
    @Test
    public void testNmToMeters(){
        double result = Converter.nmToMeters(5.0);
        assertEquals(9260.0, result, 0.0);
    }
}
