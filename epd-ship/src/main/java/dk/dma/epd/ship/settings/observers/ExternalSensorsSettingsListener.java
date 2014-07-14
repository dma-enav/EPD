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
package dk.dma.epd.ship.settings.observers;

import dk.dma.epd.common.prototype.settings.observers.ExternalSensorsCommonSettingsListener;
import dk.dma.epd.common.prototype.settings.sensor.ExternalSensorsCommonSettings.SensorConnectionType;
import dk.dma.epd.ship.settings.sensor.ExternalSensorsSettings;

/**
 * @author Janus Varmarken
 */
public interface ExternalSensorsSettingsListener extends
        ExternalSensorsCommonSettingsListener {

    void onStartDynamicPredictionGeneratorChanged(
            ExternalSensorsSettings source, boolean start);

    void onGpsSerialPortBaudRateChanged(ExternalSensorsSettings source, int baudRate);
    
    void onMsPntSerialPortBaudRateChanged(ExternalSensorsSettings source, int baudRate);
    
    void onDynamicPredictorConnectionTypeChanged(ExternalSensorsSettings source, SensorConnectionType connType);
    
    void onDynamicPredictorHostOrSerialPortChanged(ExternalSensorsSettings source, String hostOrSerialPort);
    
    void onDynamicPredictorTcpOrUdpPortChanged(ExternalSensorsSettings source, int tcpOrUdpPort);
    
    void onDynamicPredictorSerialPortBaudRateChanged(ExternalSensorsSettings source, int baudRate);
}
