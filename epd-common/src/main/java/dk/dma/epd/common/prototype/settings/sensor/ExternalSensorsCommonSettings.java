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
package dk.dma.epd.common.prototype.settings.sensor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.SensorSettings;
import dk.dma.epd.common.prototype.settings.SensorSettings.PntSourceSetting;
import dk.dma.epd.common.prototype.settings.SensorSettings.SensorConnectionType;

/**
 * @author Janus Varmarken
 */
public class ExternalSensorsCommonSettings extends ObservedSettings<OBSERVER> {

    private static final TimeZone TZ_GMT = TimeZone.getTimeZone("GMT+0000");

    /**
     * Enumeration of sensor connection types
     */
    public enum SensorConnectionType {
        NONE("None"), 
        TCP("TCP"), 
        UDP("UDP"), 
        SERIAL("Serial"), 
        FILE("File");
        
        String title;
        
        /**
         * Constructor
         * @param title the title of the enumeration
         */
        private SensorConnectionType(String title) {
            this.title = title;
        }
        
        /**
         * Returns a string representation of this value
         */
        @Override
        public String toString() {
            return title;
        }
        
        /**
         * Parse the parameter as a SensorConnectionType
         * @param type the String to parse
         * @return the corresponding SensorConnectionType
         */
        public static SensorConnectionType parseString(String type) {
            if (type.equalsIgnoreCase("TCP")) {
                return TCP;
            } else if (type.equalsIgnoreCase("UDP")) {
                return UDP;
            } else if (type.equalsIgnoreCase("SERIAL")) {
                return SERIAL;
            } else if (type.equalsIgnoreCase("FILE")) {
                return FILE;
            }
            return NONE;
        }
    }

    /**
     * Enumeration of PNT sources
     */
    public enum PntSourceSetting {
        AUTO("Automatic selection"), 
        AIS("AIS Connection"), 
        GPS("GPS Connection"), 
        MSPNT("Multi-source PNT connection"), 
        NONE("None");
        
        String title;
        
        /**
         * Constructor
         * @param title the title of the enumeration
         */
        private PntSourceSetting(String title) {
            this.title = title;
        }
        
        /**
         * Returns a string representation of this value
         */
        @Override
        public String toString() {
            return title;
        }
        
        /**
         * Parse the parameter as a PntSource
         * @param type the String to parse
         * @return the corresponding PntSource
         */
        public static PntSourceSetting parseString(String type) {
            if (type.equalsIgnoreCase("AUTO")) {
                return AUTO;
            } else if (type.equalsIgnoreCase("AIS")) {
                return AIS;
            } else if (type.equalsIgnoreCase("GPS")) {
                return GPS;
            } else if (type.equalsIgnoreCase("MSPNT")) {
                return MSPNT;
            }
            return NONE;
        }
    }

    private SensorConnectionType aisConnectionType = SensorConnectionType.TCP;
    private String aisHostOrSerialPort = "localhost";
    private String aisFilename = "";
    private int aisTcpOrUdpPort = 4001;

    private SensorConnectionType gpsConnectionType = SensorConnectionType.NONE;
    private String gpsHostOrSerialPort = "COM3";
    private String gpsFilename = "";
    private int gpsTcpOrUdpPort = 8888;

    private SensorConnectionType msPntConnectionType = SensorConnectionType.NONE;
    private String msPntHostOrSerialPort = "COM4";
    private String msPntFilename = "";
    private int msPntTcpOrUdpPort = 9999;

    private PntSourceSetting pntSource = PntSourceSetting.AUTO;

    private boolean startTransponder = true;
    /**
     * If farther away than this range, the messages are discarded In nautical miles (theoretical distance is about 40 miles)
     */
    private double aisSensorRange;

    private int replaySpeedup = 1;
    private Date replayStartDate;
    
    /**
     * Formats the given date in the ISO-8620 format
     * @param date the date to format
     * @return the formatted date
     */
    public static String getISO8620(Date date) {
        SimpleDateFormat iso8601gmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        iso8601gmt.setTimeZone(TZ_GMT);
        return iso8601gmt.format(date);
    }
}
