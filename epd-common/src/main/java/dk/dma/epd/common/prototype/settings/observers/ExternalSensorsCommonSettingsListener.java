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
package dk.dma.epd.common.prototype.settings.observers;

import java.util.Date;

import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings;
import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings.PntSourceSetting;
import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings.SensorConnectionType;

/**
 * Interface for observing an {@link ExternalSensorsCommonSettings} for changes.
 * 
 * @author Janus Varmarken
 * 
 */
public interface ExternalSensorsCommonSettingsListener {

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getAisConnectionType()}
     * has changed.
     * 
     * @param connType
     *            The new connection type for AIS.
     */
    void aisConnectionTypeChanged(SensorConnectionType connType);

    /**
     * Invoked when
     * {@link ExternalSensorsCommonSettings#getAisHostOrSerialPort()} has
     * changed.
     * 
     * @param aisHostOrSerialPort
     *            The new value for the setting.
     */
    void aisHostOrSerialPortChanged(String aisHostOrSerialPort);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getAisFilename()} has
     * changed.
     * 
     * @param aisFilename
     *            The new name of the AIS file.
     */
    void aisFilenameChanged(String aisFilename);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getAisTcpOrUdpPort()}
     * has changed.
     * 
     * @param port
     *            The new port to use.
     */
    void aisTcpOrUdpPortChanged(int port);

    /**
     * Invoked when
     * {@link ExternalSensorsCommonSettings#getAisSerialPortBaudRate()} has
     * changed.
     * 
     * @param aisSerialPortBaudRate
     *            The new baud rate.
     */
    void aisSerialPortBaudRateChanged(int aisSerialPortBaudRate);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getGpsConnectionType()}
     * has changed.
     * 
     * @param connType
     *            The new connection type for GPS.
     */
    void gpsConnectionTypeChanged(SensorConnectionType connType);

    /**
     * Invoked when
     * {@link ExternalSensorsCommonSettings#getGpsHostOrSerialPort()} has
     * changed.
     * 
     * @param gpsHostOrSerialPort
     *            The new host or serial port.
     */
    void gpsHostOrSerialPortChanged(String gpsHostOrSerialPort);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getGpsFilename()} has
     * changed.
     * 
     * @param gpsFilename
     *            The new filename for the GPS file.
     */
    void gpsFilenameChanged(String gpsFilename);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getGpsTcpOrUdpPort()}
     * has changed.
     * 
     * @param port
     *            The new TCP or UDP port for GPS.
     */
    void gpsTcpOrUdpPortChanged(int port);

    /**
     * Invoked when
     * {@link ExternalSensorsCommonSettings#getMsPntConnectionType()} has
     * changed.
     * 
     * @param connType
     *            The new connection type for multi source PNT.
     */
    void msPntConnectionTypeChanged(SensorConnectionType connType);

    /**
     * Invoked when
     * {@link ExternalSensorsCommonSettings#getMsPntHostOrSerialPort()} has
     * changed.
     * 
     * @param msPntHostOrSerialPort
     *            The new multi source PNT host or serial port.
     */
    void msPntHostOrSerialPortChanged(String msPntHostOrSerialPort);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getMsPntFilename()} has
     * changed.
     * 
     * @param msPntFilename
     *            The new multi source PNT file name.
     */
    void msPntFilenameChanged(String msPntFilename);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getMsPntTcpOrUdpPort()}
     * has changed.
     * 
     * @param msPntTcpOrUdpPort
     *            The new multi source PNT TCP or UDP port.
     */
    void msPntTcpOrUdpPortChanged(int msPntTcpOrUdpPort);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getPntSource()} has
     * changed.
     * 
     * @param pntSource
     *            The new PNT source.
     */
    void pntSourceChanged(PntSourceSetting pntSource);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#isStartTransponder()}
     * has changed.
     * 
     * @param startTransponder
     *            If the transponder should start automatically.
     */
    void startTransponderChanged(boolean startTransponder);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getAisSensorRange()}
     * has changed.
     * 
     * @param aisSensorRange
     *            The new AIS sensor range.
     */
    void aisSensorRangeChanged(double aisSensorRange);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getReplaySpeedup()} has
     * changed.
     * 
     * @param replaySpeedup
     *            The new replay speedup.
     */
    void replaySpeedupChanged(int replaySpeedup);

    /**
     * Invoked when {@link ExternalSensorsCommonSettings#getReplayStartDate()}
     * has changed.
     * 
     * @param replayStartDate
     *            The new replay start date.
     */
    void replayStartDateChanged(Date replayStartDate);

}
