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

import java.util.List;

import net.jcip.annotations.NotThreadSafe;
import dk.dma.ais.sentence.Sentence;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.SentenceLine;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorData;

/**
 * Proprietary PDYPR sentence parser. TODO
 * 
 * <pre>
 * format
 * </pre>
 */
@NotThreadSafe
public class PdyprSentence extends Sentence {

    DynamicPredictorData dynamicPredictorData;

    /**
     * Constructor
     */
    public PdyprSentence() {
        super();
        this.formatter = "PDYPR";
    }

    /**
     * Checks if the given line is an PDYPR message
     * 
     * @param line
     *            the line to check
     * @return if the given line is an PDYPR message
     */
    public static boolean isPdypr(String line) {
        return line.indexOf("$PDYPR") >= 0;
    }

    /**
     * Parses the given line as a PDYPR message
     * 
     * @param line
     *            the line to parse
     * @return the result (0=success)
     */
    public int parse(String line) throws SentenceException {
        return parse(new SentenceLine(line.trim()));
    }

    /**
     * Parses the given {@code SentenceLine} as a PDYPR message
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

        // Check PDYPR
        if (!this.formatter.equals("PDYPR")) {
            throw new SentenceException("Not PDYPR sentence");
        }

        // Check that there is a least 1 fields
        if (fields.size() < 1) {
            throw new SentenceException("PDYPR sentence '" + sl.getLine() + "' must have TODO fields");
        }
        
        dynamicPredictorData = new DynamicPredictorData();

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
