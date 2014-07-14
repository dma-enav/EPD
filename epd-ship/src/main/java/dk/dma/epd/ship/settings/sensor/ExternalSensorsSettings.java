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
package dk.dma.epd.ship.settings.sensor;

import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings;
import dk.dma.epd.ship.settings.observers.ExternalSensorsSettingsListener;

/**
 * @author Janus Varmarken
 */
public class ExternalSensorsSettings extends
        ExternalSensorsCommonSettings<ExternalSensorsSettingsListener> {

    private boolean startDynamicPredictionGenerator;
    private int gpsSerialPortBaudRate = 38400;
    private int msPntSerialPortBaudRate = 38400;
    private SensorConnectionType dynamicPredictorConnectionType = SensorConnectionType.NONE;
    private String dynamicPredictorHostOrSerialPort = "localhost";
    private int dynamicPredictorTcpOrUdpPort = 8008;
    private int dynamicPredictorSerialPortBaudRate = 38400;

    public boolean isStartDynamicPredictionGenerator() {
        try {
            this.settingLock.readLock().lock();
            return startDynamicPredictionGenerator;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setStartDynamicPredictionGenerator(
            final boolean startDynamicPredictionGenerator) {
        try {
            this.settingLock.writeLock().lock();
            if (this.startDynamicPredictionGenerator == startDynamicPredictionGenerator) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.startDynamicPredictionGenerator = startDynamicPredictionGenerator;
            for (ExternalSensorsSettingsListener obs : this.observers) {
                obs.onStartDynamicPredictionGeneratorChanged(this,
                        startDynamicPredictionGenerator);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public int getGpsSerialPortBaudRate() {
        try {
            this.settingLock.readLock().lock();
            return gpsSerialPortBaudRate;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setGpsSerialPortBaudRate(final int gpsSerialPortBaudRate) {
        try {
            this.settingLock.writeLock().lock();
            if (this.gpsSerialPortBaudRate == gpsSerialPortBaudRate) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.gpsSerialPortBaudRate = gpsSerialPortBaudRate;
            for (ExternalSensorsSettingsListener obs : this.observers) {
                obs.onGpsSerialPortBaudRateChanged(this, gpsSerialPortBaudRate);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public int getMsPntSerialPortBaudRate() {
        try {
            this.settingLock.readLock().lock();
            return msPntSerialPortBaudRate;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setMsPntSerialPortBaudRate(final int msPntSerialPortBaudRate) {
        try {
            this.settingLock.writeLock().lock();
            if (this.msPntSerialPortBaudRate == msPntSerialPortBaudRate) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.msPntSerialPortBaudRate = msPntSerialPortBaudRate;
            for (ExternalSensorsSettingsListener obs : this.observers) {
                obs.onMsPntSerialPortBaudRateChanged(this, msPntSerialPortBaudRate);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }
    
    public SensorConnectionType getDynamicPredictorConnectionType() {
        try {
            this.settingLock.readLock().lock();
            return dynamicPredictorConnectionType;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setDynamicPredictorConnectionType(final 
            SensorConnectionType dynamicPredictorConnectionType) {
        try {
            this.settingLock.writeLock().lock();        
            if (this.dynamicPredictorConnectionType == dynamicPredictorConnectionType) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.dynamicPredictorConnectionType = dynamicPredictorConnectionType;
            for (ExternalSensorsSettingsListener obs : this.observers) {
                obs.onDynamicPredictorConnectionTypeChanged(this, dynamicPredictorConnectionType);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public String getDynamicPredictorHostOrSerialPort() {
        try {
            this.settingLock.readLock().lock();
            return dynamicPredictorHostOrSerialPort;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setDynamicPredictorHostOrSerialPort(
            final String dynamicPredictorHostOrSerialPort) {
        try {
            this.settingLock.writeLock().lock();
            if (this.dynamicPredictorHostOrSerialPort == dynamicPredictorHostOrSerialPort) {
                // No change, no need to notify observers.
                return;
            }
            // There was a change, update and notify observers.
            this.dynamicPredictorHostOrSerialPort = dynamicPredictorHostOrSerialPort;
            for (ExternalSensorsSettingsListener obs : this.observers) {
                obs.onDynamicPredictorHostOrSerialPortChanged(this, dynamicPredictorHostOrSerialPort);
            }
        } finally {
            this.settingLock.writeLock().unlock();
        }
    }

    public int getDynamicPredictorTcpOrUdpPort() {
        try {            
            this.settingLock.readLock().lock();
            return dynamicPredictorTcpOrUdpPort;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setDynamicPredictorTcpOrUdpPort(final int dynamicPredictorTcpOrUdpPort) {
      try {
          this.settingLock.writeLock().lock();
          if (this.dynamicPredictorTcpOrUdpPort == dynamicPredictorTcpOrUdpPort) {
              // No change, no need to notify observers.
              return;
          }
          // There was a change, update and notify observers.
          this.dynamicPredictorTcpOrUdpPort = dynamicPredictorTcpOrUdpPort;
          for (ExternalSensorsSettingsListener obs : this.observers) {
              obs.onDynamicPredictorTcpOrUdpPortChanged(this, dynamicPredictorTcpOrUdpPort);
          }
      } finally {
          this.settingLock.writeLock().unlock();
      } 
    }

    public int getDynamicPredictorSerialPortBaudRate() {
        try {
            this.settingLock.readLock().lock();
            return dynamicPredictorSerialPortBaudRate;
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    public void setDynamicPredictorSerialPortBaudRate(
            final int dynamicPredictorSerialPortBaudRate) {
      try {
          this.settingLock.writeLock().lock();
          if (this.dynamicPredictorSerialPortBaudRate == dynamicPredictorSerialPortBaudRate) {
              // No change, no need to notify observers.
              return;
          }
          // There was a change, update and notify observers.
          this.dynamicPredictorSerialPortBaudRate = dynamicPredictorSerialPortBaudRate;
          for (ExternalSensorsSettingsListener obs : this.observers) {
              obs.onDynamicPredictorSerialPortBaudRateChanged(this, dynamicPredictorSerialPortBaudRate);
          }
      } finally {
          this.settingLock.writeLock().unlock();
      } 
    }
    
//    try {
//        this.settingLock.writeLock().lock();
//        if ( == ) {
//            // No change, no need to notify observers.
//            return;
//        }
//        // There was a change, update and notify observers.
//        for (ExternalSensorsSettingsListener obs : this.observers) {
//            
//        }
//    } finally {
//        this.settingLock.writeLock().unlock();
//    }
}
