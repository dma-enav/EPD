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
        NONE("None"), TCP("TCP"), UDP("UDP"), SERIAL("Serial"), FILE("File");

        String title;

        /**
         * Constructor
         * 
         * @param title
         *            the title of the enumeration
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
         * 
         * @param type
         *            the String to parse
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
        AUTO("Automatic selection"), AIS("AIS Connection"), GPS("GPS Connection"), MSPNT("Multi-source PNT connection"), NONE(
                "None");

        String title;

        /**
         * Constructor
         * 
         * @param title
         *            the title of the enumeration
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
         * 
         * @param type
         *            the String to parse
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
    private int aisSerialPortBaudRate = 38400;

    private SensorConnectionType gpsConnectionType = SensorConnectionType.NONE;
    private String gpsHostOrSerialPort = "COM3";
    private String gpsFilename = "";
    private int gpsTcpOrUdpPort = 8888;
    private int gpsSerialPortBaudRate = 38400;

    private SensorConnectionType msPntConnectionType = SensorConnectionType.NONE;
    private String msPntHostOrSerialPort = "COM4";
    private String msPntFilename = "";
    private int msPntTcpOrUdpPort = 9999;
    private int msPntSerialPortBaudRate = 38400;

    private PntSourceSetting pntSource = PntSourceSetting.AUTO;

    private SensorConnectionType dynamicPredictorConnectionType = SensorConnectionType.NONE;
    private String dynamicPredictorHostOrSerialPort = "localhost";
    private int dynamicPredictorTcpOrUdpPort = 8008;
    private int dynamicPredictorSerialPortBaudRate = 38400;
    
    private boolean startPredictionGenerator;

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
     * 
     * @param props
     *            the properties to initialize the sensor settings from
     */
    public void readProperties(Properties props) {
        aisConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "aisConnectionType",
                aisConnectionType.name()));
        aisHostOrSerialPort = props.getProperty(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
        aisTcpOrUdpPort = PropUtils.intFromProperties(props, PREFIX + "aisTcpOrUdpPort", aisTcpOrUdpPort);
        aisSerialPortBaudRate = PropUtils.intFromProperties(props, PREFIX + "aisSerialPortBaudRate", aisSerialPortBaudRate);
        gpsConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "gpsConnectionType",
                gpsConnectionType.name()));
        gpsHostOrSerialPort = props.getProperty(PREFIX + "gpsHostOrSerialPort", gpsHostOrSerialPort);
        gpsTcpOrUdpPort = PropUtils.intFromProperties(props, PREFIX + "gpsTcpOrUdpPort", gpsTcpOrUdpPort);
        gpsSerialPortBaudRate = PropUtils.intFromProperties(props, PREFIX + "gpsSerialPortBaudRate", gpsSerialPortBaudRate);
        msPntConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX + "msPntConnectionType",
                msPntConnectionType.name()));
        msPntHostOrSerialPort = props.getProperty(PREFIX + "msPntHostOrSerialPort", msPntHostOrSerialPort);
        msPntTcpOrUdpPort = PropUtils.intFromProperties(props, PREFIX + "msPntTcpOrUdpPort", msPntTcpOrUdpPort);
        msPntSerialPortBaudRate = PropUtils.intFromProperties(props, PREFIX + "msPntSerialPortBaudRate", msPntSerialPortBaudRate);
        dynamicPredictorConnectionType = SensorConnectionType.parseString(props.getProperty(PREFIX
                + "dynamicPredictorConnectionType", dynamicPredictorConnectionType.name()));
        dynamicPredictorHostOrSerialPort = props.getProperty(PREFIX + "dynamicPredictorHostOrSerialPort",
                dynamicPredictorHostOrSerialPort);
        dynamicPredictorTcpOrUdpPort = PropUtils.intFromProperties(props, PREFIX + "dynamicPredictorTcpOrUdpPort",
                dynamicPredictorTcpOrUdpPort);
        dynamicPredictorSerialPortBaudRate = PropUtils.intFromProperties(props, PREFIX + "dynamicPredictorSerialPortBaudRate",
                dynamicPredictorSerialPortBaudRate);

        startPredictionGenerator = PropUtils.booleanFromProperties(props, PREFIX + "startPredictionGenerator", startPredictionGenerator);
        
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
        pntSource = PntSourceSetting.parseString(props.getProperty(PREFIX + "pntSource", pntSource.name()));
    }

    /**
     * Updates the properties object with the current sensor settings
     * 
     * @param props
     *            the properties to update with the current sensor settings
     */
    public void setProperties(Properties props) {
        props.put(PREFIX + "aisConnectionType", aisConnectionType.name());
        props.put(PREFIX + "aisHostOrSerialPort", aisHostOrSerialPort);
        props.put(PREFIX + "aisTcpOrUdpPort", Integer.toString(aisTcpOrUdpPort));
        props.put(PREFIX + "aisSerialPortBaudRate", Integer.toString(aisSerialPortBaudRate));
        props.put(PREFIX + "gpsConnectionType", gpsConnectionType.name());
        props.put(PREFIX + "gpsHostOrSerialPort", gpsHostOrSerialPort);
        props.put(PREFIX + "gpsTcpOrUdpPort", Integer.toString(gpsTcpOrUdpPort));
        props.put(PREFIX + "gpsSerialPortBaudRate", Integer.toString(gpsSerialPortBaudRate));
        props.put(PREFIX + "msPntConnectionType", msPntConnectionType.name());
        props.put(PREFIX + "msPntHostOrSerialPort", msPntHostOrSerialPort);
        props.put(PREFIX + "msPntTcpOrUdpPort", Integer.toString(msPntTcpOrUdpPort));
        props.put(PREFIX + "msPntSerialPortBaudRate", Integer.toString(msPntSerialPortBaudRate));
        props.put(PREFIX + "dynamicPredictorConnectionType", dynamicPredictorConnectionType.name());
        props.put(PREFIX + "dynamicPredictorHostOrSerialPort", dynamicPredictorHostOrSerialPort);
        props.put(PREFIX + "dynamicPredictorTcpOrUdpPort", Integer.toString(dynamicPredictorTcpOrUdpPort));
        props.put(PREFIX + "dynamicPredictorSerialPortBaudRate", Integer.toString(dynamicPredictorSerialPortBaudRate));

        props.put(PREFIX + "startPredictionGenerator", Boolean.toString(startPredictionGenerator));
        
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
     * 
     * @param date
     *            the date to format
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

    public PntSourceSetting getPntSource() {
        return pntSource;
    }

    public void setPntSource(PntSourceSetting pntSource) {
        this.pntSource = pntSource;
    }

    public int getAisSerialPortBaudRate() {
        return aisSerialPortBaudRate;
    }

    public void setAisSerialPortBaudRate(int aisSerialPortBaudRate) {
        this.aisSerialPortBaudRate = aisSerialPortBaudRate;
    }

    public int getGpsSerialPortBaudRate() {
        return gpsSerialPortBaudRate;
    }

    public void setGpsSerialPortBaudRate(int gpsSerialPortBaudRate) {
        this.gpsSerialPortBaudRate = gpsSerialPortBaudRate;
    }

    public int getMsPntSerialPortBaudRate() {
        return msPntSerialPortBaudRate;
    }

    public void setMsPntSerialPortBaudRate(int msPntSerialPortBaudRate) {
        this.msPntSerialPortBaudRate = msPntSerialPortBaudRate;
    }

    public SensorConnectionType getDynamicPredictorConnectionType() {
        return dynamicPredictorConnectionType;
    }

    public void setDynamicPredictorConnectionType(SensorConnectionType dynamicPredictorConnectionType) {
        this.dynamicPredictorConnectionType = dynamicPredictorConnectionType;
    }

    public String getDynamicPredictorHostOrSerialPort() {
        return dynamicPredictorHostOrSerialPort;
    }

    public void setDynamicPredictorHostOrSerialPort(String dynamicPredictorHostOrSerialPort) {
        this.dynamicPredictorHostOrSerialPort = dynamicPredictorHostOrSerialPort;
    }

    public int getDynamicPredictorTcpOrUdpPort() {
        return dynamicPredictorTcpOrUdpPort;
    }

    public void setDynamicPredictorTcpOrUdpPort(int dynamicPredictorTcpOrUdpPort) {
        this.dynamicPredictorTcpOrUdpPort = dynamicPredictorTcpOrUdpPort;
    }

    public int getDynamicPredictorSerialPortBaudRate() {
        return dynamicPredictorSerialPortBaudRate;
    }

    public void setDynamicPredictorSerialPortBaudRate(int dynamicPredictorSerialPortBaudRate) {
        this.dynamicPredictorSerialPortBaudRate = dynamicPredictorSerialPortBaudRate;
    }
    
    public boolean isStartPredictionGenerator() {
        return startPredictionGenerator;
    }
    
    public void setStartPredictionGenerator(boolean startPredictionGenerator) {
        this.startPredictionGenerator = startPredictionGenerator;
    }

}
