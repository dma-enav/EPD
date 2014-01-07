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
