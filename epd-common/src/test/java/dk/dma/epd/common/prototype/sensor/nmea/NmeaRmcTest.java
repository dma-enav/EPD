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
package dk.dma.epd.common.prototype.sensor.nmea;

import org.junit.Assert;
import org.junit.Test;

import dk.dma.ais.sentence.SentenceException;

public class NmeaRmcTest {
   
    @Test
    public void parseGpRmcTest() throws SentenceException {
        String line = "$GPRMC,101134,A,5153.5205,N,00125.2184,E,003.1,194.1,010313,0,E*68";
        RmcSentence sentence = new RmcSentence.GpRmcSentence();
        Assert.assertEquals(sentence.parse(line), 0);
    }

    @Test
    public void parseElRmcTest() throws SentenceException {
        String line = "$ELRMC,095755,A,5154.9566,N,00125.8246,E,006.8,210.5,010313,0,E*72";
        RmcSentence sentence = new RmcSentence.ElRmcSentence();
        Assert.assertEquals(sentence.parse(line), 0);
    }

}
