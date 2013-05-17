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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.jcip.annotations.NotThreadSafe;

import dk.dma.ais.sentence.SentenceException;
import dk.dma.epd.common.prototype.sensor.gps.GnssTimeMessage;

/**
 * Proprietary PSTT sentence parser.
 */
@NotThreadSafe
public class PsttSentence {
    
    private GnssTimeMessage gnssTimeMessage;

    public PsttSentence() {
        
    }

    public boolean parse(String msg) throws SentenceException {
        String[] fields = msg.split(",|\\*");
        if (fields.length != 5) {
            throw new SentenceException("Not four fields i PSTT sentence: " + msg);
        }
        
        if (fields[2].equals("00000000") || fields[3].equals("999999")) {
            return false;
        }
        
        String dateStr = fields[2] + " " + fields[3];
        
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
            Date d = dateFormat.parse(dateStr);
            gnssTimeMessage = new GnssTimeMessage(d);
        } catch (ParseException e) {
            throw new SentenceException("Wrong date format in PSTT sentence: " + msg);
        }
        return true;        
    }
    
    public GnssTimeMessage getGnssTimeMessage() {
        return gnssTimeMessage;
    }

}
