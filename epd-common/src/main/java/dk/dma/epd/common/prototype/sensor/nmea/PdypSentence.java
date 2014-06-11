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
import java.util.List;
import java.util.TimeZone;

import net.jcip.annotations.NotThreadSafe;
import dk.dma.ais.sentence.Sentence;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.SentenceLine;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorPredictionData;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorStateData;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Proprietary PDYPN ad PDYPP sentence parser Navigational state now:
 * 
 * <pre>
 * $PDYPN, count, lat, lon, heading, cog, sog, length, width, datetime*checksum
 * </pre>
 * 
 * Predicted navigational state:
 * 
 * <pre>
 * $PDYPP, number, lat, long, heading, cog, sog, datetime(utc)*checksum
 * </pre>
 * 
 * Examples
 * 
 * <pre>
 * </pre>
 */
@NotThreadSafe
public class PdypSentence extends Sentence {

    DynamicPredictorData dynamicPredictorData;

    /**
     * Constructor
     */
    public PdypSentence() {
        super();
    }

    /**
     * Parses the given line as a PDYPP and PDYPN message
     * 
     * @param line
     *            the line to parse
     * @return the result (0=success)
     */
    public int parse(String line) throws SentenceException {
        return parse(new SentenceLine(line.trim()));
    }

    /**
     * Parses the given {@code SentenceLine} as a PDYPP or PDYPN message
     * 
     * @param sl
     *            the line to parse
     * @return the result (0=success)
     */
    @Override
    public int parse(SentenceLine sl) throws SentenceException {
        // Do common parsing
        super.baseParse(sl);
        List<String> fields = sl.getFields();

        // Check that there is a least 1 fields
        if (fields.size() < 8) {
            throw new SentenceException("PDYPx sentence '" + sl.getLine() + "' must have at least 8 fields");
        }

        this.formatter = sl.getFields().get(0);

        // Check PDYPP or PDYPN
        boolean state = this.formatter.equals("$PDYPN");
        boolean predicted = this.formatter.equals("$PDYPP");

        if (!state && !predicted) {
            throw new SentenceException("Not PDYPx sentence");
        }

        int num;
        double lat;
        double lon;
        float heading;
        Float sog = null;
        Float cog = null;
        float length = 0;
        float width = 0;
        long time;

        // num
        num = Sentence.parseInt(fields.get(1));

        try {
            lat = ParseUtils.parseDouble(fields.get(2));
            lon = ParseUtils.parseDouble(fields.get(3));
            heading = ParseUtils.parseFloat(fields.get(4));
            if (fields.get(5).length() > 0) {
                cog = ParseUtils.parseFloat(fields.get(5));
            }
            if (fields.get(6).length() > 0) {
                sog = ParseUtils.parseFloat(fields.get(6));
            }

            String dateTimeStr;

            if (state) {
                if (fields.size() < 10) {
                    throw new SentenceException("PDYPN sentence '" + sl.getLine() + "' must have at least 10 fields");
                }
                length = ParseUtils.parseFloat(fields.get(7));
                width = ParseUtils.parseFloat(fields.get(8));
                dateTimeStr = fields.get(9);

            } else {
                dateTimeStr = fields.get(7);
            }
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
            try {
                time = dateFormat.parse(dateTimeStr).getTime();
            } catch (ParseException e) {
                throw new FormatException(e.getMessage());
            }

        } catch (FormatException e) {
            throw new SentenceException(e.getMessage());
        }

        if (state) {
            dynamicPredictorData = new DynamicPredictorStateData(num, Position.create(lat, lon), heading, cog, sog, length, width, time);
        } else {
            dynamicPredictorData = new DynamicPredictorPredictionData(num, Position.create(lat, lon), heading, cog, sog, time);
        }

        return 0;
    }

    public DynamicPredictorData getDynamicPredictorData() {
        return dynamicPredictorData;
    }

    @Override
    public String getEncoded() {
        return null;
    }

}
