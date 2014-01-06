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
package dk.dma.epd.common.prototype.settings;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Sensor settings
 */
public class SensorSettings implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(SensorSettings.class);

    private static final String PREFIX = "sensor.";
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
    public enum PntSource {
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
        private PntSource(String title) {
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
        public static PntSource parseString(String type) {
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

    private PntSource pntSource = PntSource.AUTO;

    private boolean startTransponder = true;
    /**
     * If farther away than this range, the messages are discarded In nautical miles (theoretical distance is about 40 miles)
     */
    private double aisSensorRange;

    private int replaySpeedup = 1;
    private Date replayStartDate;

    /**
     * Constructor
     */
    public SensorSettings() {
    }

    /**
     * Reads and initializes the sensor settings from the properties object
     * @param props the properties to initialize the sensor settings from
     */
    public void readProperties(Properties props) {
        aisConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "aisConnectionType",
                aisConnectionType.name()));
        aisHostOrSerialPort = props.getProperty(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
        aisTcpOrUdpPort = PropUtils.intFromProperties(props, PREFIX + "aisTcpOrUdpPort", aisTcpOrUdpPort);
        gpsConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "gpsConnectionType",
                gpsConnectionType.name()));
        gpsHostOrSerialPort = props.getProperty(PREFIX + "gpsHostOrSerialPort", gpsHostOrSerialPort);
        gpsTcpOrUdpPort = PropUtils.intFromProperties(props, PREFIX + "gpsTcpOrUdpPort", gpsTcpOrUdpPort);
        msPntConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "msPntConnectionType",
                msPntConnectionType.name()));
        msPntHostOrSerialPort = props.getProperty(PREFIX + "msPntHostOrSerialPort", msPntHostOrSerialPort);
        msPntTcpOrUdpPort = PropUtils.intFromProperties(props, PREFIX + "msPntTcpOrUdpPort", msPntTcpOrUdpPort);
        startTransponder = PropUtils.booleanFromProperties(props, PREFIX + "startTransponder", startTransponder);
        aisSensorRange = PropUtils.doubleFromProperties(props, PREFIX + "aisSensorRange", aisSensorRange);
        aisFilename = props.getProperty(PREFIX + "aisFilename", aisFilename);
        gpsFilename = props.getProperty(PREFIX + "gpsFilename", gpsFilename);
        msPntFilename = props.getProperty(PREFIX + "msPntFilename", msPntFilename);
        replaySpeedup = PropUtils.intFromProperties(props, PREFIX + "replaySpeedup", replaySpeedup);
        String replayStartStr = props.getProperty(PREFIX + "replayStartDate", "");
        if (replayStartStr.length() > 0) {
            try {
                replayStartDate = ParseUtils.parseIso8602(replayStartStr);
                LOG.info("replayStartDate: " + replayStartDate);
            } catch (FormatException e) {
                LOG.error("Failed to parse replayStartDate");
            }
        }
        pntSource = PntSource.parseString(props.getProperty(PREFIX + "pntSource", pntSource.name()));
    }

    /**
     * Updates the properties object with the current sensor settings
     * @param props the properties to update with the current sensor settings
     */
    public void setProperties(Properties props) {
        props.put(PREFIX + "aisConnectionType", aisConnectionType.name());
        props.put(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
        props.put(PREFIX + "aisTcpOrUdpPort", Integer.toString(aisTcpOrUdpPort));
        props.put(PREFIX + "gpsConnectionType", gpsConnectionType.name());
        props.put(PREFIX + "gpsHostOrSerialPort", gpsHostOrSerialPort);
        props.put(PREFIX + "gpsTcpOrUdpPort", Integer.toString(gpsTcpOrUdpPort));
        props.put(PREFIX + "msPntConnectionType", msPntConnectionType.name());
        props.put(PREFIX + "msPntHostOrSerialPort", msPntHostOrSerialPort);
        props.put(PREFIX + "msPntTcpOrUdpPort", Integer.toString(msPntTcpOrUdpPort));
        props.put(PREFIX + "startTransponder", Boolean.toString(startTransponder));
        props.put(PREFIX + "aisSensorRange", Double.toString(aisSensorRange));
        props.put(PREFIX + "aisFilename", aisFilename);
        props.put(PREFIX + "gpsFilename", gpsFilename);
        props.put(PREFIX + "msPntFilename", msPntFilename);
        props.put(PREFIX + "replaySpeedup", Integer.toString(replaySpeedup));
        String replayStartStr = "";
        if (replayStartDate != null) {
            replayStartStr = getISO8620(replayStartDate);
        }
        props.put(PREFIX + "replayStartDate", replayStartStr);
        props.put(PREFIX + "pntSource", pntSource.name());
    }
    
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

    
    /**** Getters and setters ****/
    
    public SensorConnectionType getMsPntConnectionType() {
        return msPntConnectionType;
    }

    public void setMsPntConnectionType(SensorConnectionType msPntConnectionType) {
        this.msPntConnectionType = msPntConnectionType;
    }

    public String getMsPntHostOrSerialPort() {
        return msPntHostOrSerialPort;
    }

    public void setMsPntHostOrSerialPort(String msPntHostOrSerialPort) {
        this.msPntHostOrSerialPort = msPntHostOrSerialPort;
    }

    public String getMsPntFilename() {
        return msPntFilename;
    }

    public void setMsPntFilename(String msPntFilename) {
        this.msPntFilename = msPntFilename;
    }

    public int getMsPntTcpOrUdpPort() {
        return msPntTcpOrUdpPort;
    }

    public void setMsPntTcpOrUdpPort(int msPntTcpOrUdpPort) {
        this.msPntTcpOrUdpPort = msPntTcpOrUdpPort;
    }

    public SensorConnectionType getAisConnectionType() {
        return aisConnectionType;
    }

    public void setAisConnectionType(SensorConnectionType aisConnectionType) {
        this.aisConnectionType = aisConnectionType;
    }

    public String getAisHostOrSerialPort() {
        return aisHostOrSerialPort;
    }

    public void setAisHostOrSerialPort(String aisHostOrSerialPort) {
        this.aisHostOrSerialPort = aisHostOrSerialPort;
    }

    public int getAisTcpOrUdpPort() {
        return aisTcpOrUdpPort;
    }

    public void setAisTcpOrUdpPort(int aisTcpOrUdpPort) {
        this.aisTcpOrUdpPort = aisTcpOrUdpPort;
    }

    public SensorConnectionType getGpsConnectionType() {
        return gpsConnectionType;
    }

    public void setGpsConnectionType(SensorConnectionType gpsConnectionType) {
        this.gpsConnectionType = gpsConnectionType;
    }

    public String getGpsHostOrSerialPort() {
        return gpsHostOrSerialPort;
    }

    public void setGpsHostOrSerialPort(String gpsHostOrSerialPort) {
        this.gpsHostOrSerialPort = gpsHostOrSerialPort;
    }

    public int getGpsTcpOrUdpPort() {
        return gpsTcpOrUdpPort;
    }

    public void setGpsTcpOrUdpPort(int gpsTcpOrUdpPort) {
        this.gpsTcpOrUdpPort = gpsTcpOrUdpPort;
    }

    public boolean isStartTransponder() {
        return startTransponder;
    }

    public void setStartTransponder(boolean startTransponder) {
        this.startTransponder = startTransponder;
    }

    public double getAisSensorRange() {
        return aisSensorRange;
    }

    public void setAisSensorRange(double aisSensorRange) {
        this.aisSensorRange = aisSensorRange;
    }

    public String getAisFilename() {
        return aisFilename;
    }

    public void setAisFilename(String aisFilename) {
        this.aisFilename = aisFilename;
    }

    public String getGpsFilename() {
        return gpsFilename;
    }

    public void setGpsFilename(String gpsFilename) {
        this.gpsFilename = gpsFilename;
    }

    public int getReplaySpeedup() {
        return replaySpeedup;
    }

    public void setReplaySpeedup(int replaySpeedup) {
        this.replaySpeedup = replaySpeedup;
    }

    public Date getReplayStartDate() {
        return replayStartDate;
    }

    public void setReplayStartDate(Date replayStartDate) {
        this.replayStartDate = replayStartDate;
    }

    public PntSource getPntSource() {
        return pntSource;
    }

    public void setPntSource(PntSource pntSource) {
        this.pntSource = pntSource;
    }

}
