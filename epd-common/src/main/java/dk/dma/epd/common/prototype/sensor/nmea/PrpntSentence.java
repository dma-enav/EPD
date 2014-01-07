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

import dk.dma.ais.sentence.Sentence;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.SentenceLine;
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData;
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData.JammingFlag;
import dk.dma.epd.common.util.EnumUtils;
import net.jcip.annotations.NotThreadSafe;

/**
 * Proprietary RPNT sentence parser.
 * 
 * Format of the RPNT sentence, as defined by Chris Hargreaves from Research and Radionavigation:
 * <pre>
 * $PRPNT,1.0,1,A,008.5,005.3,003.1,116*2A
 * 
 * 1.0 = Sentence version number (may be subject to change pending software development)
 * 1 = PNT Source to use (1 = GPS ‘GPRMC’, 2 = eLoran ‘ELRMC’, 3 = Radar ‘RDRMC’ etc… 0 = do-not-use flag)
 * A = GPS Jamming Flag A-ok; J=Jamming; S=Spoofing (future addition)
 * 008.5 = HPL in meters (8.5m), if this breaches the HAL, will set the validity flag to ‘V’ for corresponding navigation ‘RMC’ sentence.
 * 005.3 = Error-ellipse major axis (5.3m), for eLoran this is equal to the HAL (different for GPS due to Ionospheric biasing issues, see Appendix A when I write it!)
 * 003.1 = Error-ellipse minor axis (3.1m)
 * 116 = Bearing of ellipse major-axis from True North.
 * (last three largely for portrayal purposes)
 * *2A = usual bit-xor checksum
 * </pre>
 */
@NotThreadSafe
public class PrpntSentence extends Sentence {

    static final String SUPPORTED_PRPNT_VERSION = "1.0";
    
    ResilientPntData rpntData;
    
    /**
     * Constructor
     */
    public PrpntSentence() {
        super();
        this.formatter = "PRPNT";
    }

    /**
     * Checks if the given line is an PRPNT message
     * @param line the line to check
     * @return if the given line is an PRPNT message
     */
    public static boolean isPrpnt(String line) {
        return line.indexOf("$PRPNT") >= 0;
    }

    /**
     * Parses the given line as a PRPNT message
     * 
     * @param line the line to parse
     * @return the result (0=success)
     */
    public int parse(String line) throws SentenceException {
        return parse(new SentenceLine(line.trim()));
    }

    /**
     * Parses the given {@code SentenceLine} as a PRPNT message
     * 
     * @param sl the line to parse
     * @return the result (0=success)
     */
    @Override
    public int parse(SentenceLine sl) throws SentenceException {
        // Do common parsing
        super.baseParse(sl);
        List<String> fields = sl.getFields();

        // Check RMC
        if (!this.formatter.equals("PRPNT")) {
            throw new SentenceException("Not PRPNT sentence");
        }
        
        // Check that there is a least 10 fields
        if (fields.size() != 9) {
            throw new SentenceException("PRPNT sentence '" + sl.getLine() + "' must have nine fields");
        }
        
        // Field 1: Check that the version is supported
        if (!SUPPORTED_PRPNT_VERSION.equals(fields.get(1))) {
            throw new SentenceException("Unsupported version '" + fields.get(1) + 
                    "'. Supported version: " + SUPPORTED_PRPNT_VERSION);
        }
        
        // Field 2: Parse the PNT Source
        PntSource pntSource = EnumUtils.findByKey(PntSource.class, parseInt(fields.get(2)));
        if (pntSource == null) {
            throw new SentenceException("Invalid PNT source: " + fields.get(2));
        }
        
        // Field 3: Parse the GPS Jamming Flag
        JammingFlag jammingFlag = EnumUtils.findByKey(JammingFlag.class, fields.get(3));
        if (jammingFlag == null) {
            throw new SentenceException("Invalid GPS Jamming Flag: " + fields.get(3));
        }
        
        // Field 4: Parse the horizontal protection level
        double hpl = parseDoubleUsingErrorMsg(fields.get(4), "Invalid HPL: ");
        
        // Field 5: Parse the error-ellipse major axis
        double errorEllipseMajorAxis = parseDoubleUsingErrorMsg(fields.get(5), "Invalid error-ellipse major axis: ");

        // Field 6: Parse the error-ellipse minor axis
        double errorEllipseMinorAxis = parseDoubleUsingErrorMsg(fields.get(6), "Invalid error-ellipse minor axis: ");

        // Field 7: Parse bearing of the error-ellipse major axis
        double errorEllipseBearing = parseDoubleUsingErrorMsg(fields.get(7), "Invalid error-ellipse bearing: ");

        // Instantiate the RPNT data from the fields
        rpntData = new ResilientPntData(
                pntSource,
                jammingFlag,
                hpl,
                errorEllipseMajorAxis, 
                errorEllipseMinorAxis, 
                errorEllipseBearing);
        
        // NB: bizarrely, this method either returns 0 or throws an exception
        return 0;
    }
    
    /**
     * Parses the given string as a double and returns the result.
     * Throws an exception if the string cannot be parsed as a double.
     * @param txt the text to parse
     * @param errorMessage the error message to include in the exception in 
     *          case of an error
     * @return the parsed double
     */
    private double parseDoubleUsingErrorMsg(String txt, String errorMessage) throws SentenceException {
        try {
            return Double.parseDouble(txt);
        } catch (NumberFormatException e) {
            throw new SentenceException(errorMessage + txt);
        }
    }
    
    /**
     * Returns the parsed RPNT data
     * @return the parsed RPNT data
     */
    public ResilientPntData getRpntData() {
        return rpntData;
    }

    /**
     * Get the encoded sentence
     */
    @Override
    public String getEncoded() {
        // Not used
        return null;
    }
}
