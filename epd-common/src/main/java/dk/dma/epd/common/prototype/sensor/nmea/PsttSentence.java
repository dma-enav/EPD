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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import net.jcip.annotations.NotThreadSafe;
import dk.dma.ais.sentence.SentenceException;

/**
 * Proprietary PSTT sentence parser.
 */
@NotThreadSafe
public class PsttSentence {
    
    private PntMessage pntMessage;

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
            pntMessage = new PntMessage(PntSource.GPS, d.getTime());
        } catch (ParseException e) {
            throw new SentenceException("Wrong date format in PSTT sentence: " + msg);
        }
        return true;        
    }
    
    public PntMessage getPntMessage() {
        return pntMessage;
    }

}
