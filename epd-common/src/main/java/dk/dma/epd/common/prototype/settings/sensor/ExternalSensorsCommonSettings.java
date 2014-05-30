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
import java.util.Objects;
import java.util.TimeZone;

import dk.dma.epd.common.prototype.settings.ObservedSettings;
import dk.dma.epd.common.prototype.settings.observers.ExternalSensorsCommonSettingsListener;

/**
 * Maintains settings for external sensors.
 * @author Janus Varmarken
 */
public class ExternalSensorsCommonSettings<OBSERVER extends ExternalSensorsCommonSettingsListener> extends ObservedSettings<OBSERVER> {

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
    
    // TODO implement getters/setters with locking, observer calls etc.
    
    public SensorConnectionType getAisConnectionType() {
        try {
            this.settingLock.readLock().lock();
            return this.aisConnectionType;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setAisConnectionType(final SensorConnectionType aisConnectionType) {
        try {
            this.settingLock.writeLock().lock();
            if(this.aisConnectionType == aisConnectionType) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.aisConnectionType = aisConnectionType;
            for(OBSERVER obs : this.observers) {
                obs.aisConnectionTypeChanged(aisConnectionType);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public String getAisHostOrSerialPort() {
        try {
            this.settingLock.readLock().lock();
            return this.aisHostOrSerialPort;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setAisHostOrSerialPort(final String aisHostOrSerialPort) {
        try {
            this.settingLock.writeLock().lock();
            if(this.aisHostOrSerialPort.equals(aisHostOrSerialPort)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.aisHostOrSerialPort = aisHostOrSerialPort;
            for(OBSERVER obs : this.observers) {
                obs.aisHostOrSerialPortChanged(aisHostOrSerialPort);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public String getAisFilename() {
        try {
            this.settingLock.readLock().lock();
            return this.aisFilename;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setAisFilename(final String aisFilename) {
        try {
            this.settingLock.writeLock().lock();
            if(this.aisFilename.equals(aisFilename)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.aisFilename = aisFilename;
            for(OBSERVER obs : this.observers) {
                obs.aisFilenameChanged(aisFilename);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public int getAisTcpOrUdpPort() {
        try {
            this.settingLock.readLock().lock();
            return this.aisTcpOrUdpPort;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setAisTcpOrUdpPort(final int aisTcpOrUdpPort) {
        try {
            this.settingLock.writeLock().lock();
            if(this.aisTcpOrUdpPort == aisTcpOrUdpPort) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.aisTcpOrUdpPort = aisTcpOrUdpPort;
            for(OBSERVER obs : this.observers) {
                obs.aisTcpOrUdpPortChanged(aisTcpOrUdpPort);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public SensorConnectionType getGpsConnectionType() {
        try {
            this.settingLock.readLock().lock();
            return this.gpsConnectionType;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }
    
    public void setGpsConnectionType(final SensorConnectionType gpsConnectionType) {
        try {
            this.settingLock.writeLock().lock();
            if(this.gpsConnectionType == gpsConnectionType) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
           this.gpsConnectionType = gpsConnectionType;
           for(OBSERVER obs : this.observers) {
               obs.gpsConnectionTypeChanged(gpsConnectionType);
           }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
    
    public String getGpsHostOrSerialPort() {
        try {
            this.settingLock.readLock().lock();
            return this.gpsHostOrSerialPort;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setGpsHostOrSerialPort(final String gpsHostOrSerialPort) {
        try {
            this.settingLock.writeLock().lock();
            if(this.gpsHostOrSerialPort.equals(gpsHostOrSerialPort)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.gpsHostOrSerialPort = gpsHostOrSerialPort;
            for(OBSERVER obs : this.observers) {
                obs.gpsHostOrSerialPortChanged(gpsHostOrSerialPort);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public String getGpsFilename() {
        try {
            this.settingLock.readLock().lock();
            return gpsFilename;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setGpsFilename(final String gpsFilename) {
        try {
            this.settingLock.writeLock().lock();
            if(this.gpsFilename.equals(gpsFilename)) {
                // No change, no need to notify observers.
                return;
            }
            // There ws a change, update and notify observers.
            this.gpsFilename = gpsFilename;
            for(OBSERVER obs : this.observers) {
                obs.gpsFilenameChanged(gpsFilename);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public int getGpsTcpOrUdpPort() {
        try {
            this.settingLock.readLock().lock();
            return gpsTcpOrUdpPort;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setGpsTcpOrUdpPort(final int gpsTcpOrUdpPort) {
        try {
            this.settingLock.writeLock().lock();
            if(this.gpsTcpOrUdpPort == gpsTcpOrUdpPort) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.gpsTcpOrUdpPort = gpsTcpOrUdpPort;
            for(OBSERVER obs : this.observers) {
                obs.gpsTcpOrUdpPortChanged(gpsTcpOrUdpPort);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public SensorConnectionType getMsPntConnectionType() {
        try {
            this.settingLock.readLock().lock();
            return msPntConnectionType;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setMsPntConnectionType(final SensorConnectionType msPntConnectionType) {
        try {
            this.settingLock.writeLock().lock();
            if(this.msPntConnectionType == msPntConnectionType) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msPntConnectionType = msPntConnectionType;
            for(OBSERVER obs : this.observers) {
                obs.msPntConnectionTypeChanged(msPntConnectionType);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public String getMsPntHostOrSerialPort() {
        try {
            this.settingLock.readLock().lock();
            return msPntHostOrSerialPort;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setMsPntHostOrSerialPort(final String msPntHostOrSerialPort) {
        try {
            this.settingLock.writeLock().lock();
            if(this.msPntHostOrSerialPort.equals(msPntHostOrSerialPort)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msPntHostOrSerialPort = msPntHostOrSerialPort;
            for(OBSERVER obs : this.observers) {
                obs.msPntHostOrSerialPortChanged(msPntHostOrSerialPort);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public String getMsPntFilename() {
        try {
            this.settingLock.readLock().lock();
            return msPntFilename;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setMsPntFilename(final String msPntFilename) {
        try {
            this.settingLock.writeLock().lock();
            if(this.msPntFilename.equals(msPntFilename)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msPntFilename = msPntFilename;
            for(OBSERVER obs : this.observers) {
                obs.msPntFilenameChanged(msPntFilename);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public int getMsPntTcpOrUdpPort() {
        try {
            this.settingLock.readLock().lock();
            return msPntTcpOrUdpPort;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setMsPntTcpOrUdpPort(final int msPntTcpOrUdpPort) {
        try {
            this.settingLock.writeLock().lock();
            if(this.msPntTcpOrUdpPort == msPntTcpOrUdpPort) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msPntTcpOrUdpPort = msPntTcpOrUdpPort;
            for(OBSERVER obs : this.observers) {
                obs.msPntTcpOrUdpPortChanged(msPntTcpOrUdpPort);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public PntSourceSetting getPntSource() {
        try {
            this.settingLock.readLock().lock();
            return pntSource;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setPntSource(final PntSourceSetting pntSource) {
        try {
            this.settingLock.writeLock().lock();
            if(this.pntSource == pntSource) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.pntSource = pntSource;
            for(OBSERVER obs : this.observers) {
                obs.pntSourceChanged(pntSource);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public boolean isStartTransponder() {
        try {
            this.settingLock.readLock().lock();
            return startTransponder;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setStartTransponder(final boolean startTransponder) {
        try {
            this.settingLock.writeLock().lock();
            if(this.startTransponder == startTransponder) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.startTransponder = startTransponder;
            for(OBSERVER obs : this.observers) {
                obs.startTransponderChanged(startTransponder);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public double getAisSensorRange() {
        try {
            this.settingLock.readLock().lock();
            return aisSensorRange;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setAisSensorRange(final double aisSensorRange) {
        try {
            this.settingLock.writeLock().lock();
            if(this.aisSensorRange == aisSensorRange) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.aisSensorRange = aisSensorRange;
            for(OBSERVER obs : this.observers) {
                obs.aisSensorRangeChanged(aisSensorRange);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public int getReplaySpeedup() {
        try {
            this.settingLock.readLock().lock();
            return replaySpeedup;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setReplaySpeedup(final int replaySpeedup) {
        try {
            this.settingLock.writeLock().lock();
            if(this.replaySpeedup == replaySpeedup) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.replaySpeedup = replaySpeedup;
            for(OBSERVER obs : this.observers) {
                obs.replaySpeedupChanged(replaySpeedup);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public Date getReplayStartDate() {
        try {
            this.settingLock.readLock().lock();
            return replayStartDate;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setReplayStartDate(final Date replayStartDate) {
        try {
            this.settingLock.writeLock().lock();
            if(Objects.equals(this.replayStartDate, replayStartDate)) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            // Use clones to avoid reference leak.
            this.replayStartDate = replayStartDate == null ? null : (Date) replayStartDate.clone();
            for(OBSERVER obs : this.observers) {
                obs.replayStartDateChanged(replayStartDate == null ? null : (Date) replayStartDate.clone());
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
}
