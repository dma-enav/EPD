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
package dk.dma.epd.shore.layers.route;

import java.util.Date;

import dk.dma.epd.common.prototype.model.route.RouteMetocSettings;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.gui.utils.InfoPanel;
import dk.frv.enav.common.xml.metoc.MetocDataTypes;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;


/**
 * Metoc mouse over info
 */
public class MetocInfoPanel extends InfoPanel {
    private static final long serialVersionUID = 1L;

    public MetocInfoPanel() {
        super();
    }

    public void showText(MetocForecastPoint pointForecast, RouteMetocSettings settings){
        Date date = pointForecast.getTime();
        Double meanWaveDirection = null;
        if (pointForecast.getMeanWaveDirection() != null) {
            meanWaveDirection = pointForecast.getMeanWaveDirection().getForecast();
        }
        Double meanWaveHeight = null;
        if (pointForecast.getMeanWaveHeight() != null) {
            meanWaveHeight = pointForecast.getMeanWaveHeight().getForecast();
        }
        Double meanWavePeriod = null;
        if (pointForecast.getMeanWavePeriod() != null) {
            meanWavePeriod = pointForecast.getMeanWavePeriod().getForecast();
        }
        Double currentDirection = null;
        if (pointForecast.getCurrentDirection() != null) {
            currentDirection = pointForecast.getCurrentDirection().getForecast();
        }
        Double currentSpeed = null;
        if (pointForecast.getCurrentSpeed() != null) {
            currentSpeed = pointForecast.getCurrentSpeed().getForecast() * (3.6d/1.852d);
        }
        Double windSpeed = null;
        if (pointForecast.getWindSpeed() != null) {
            windSpeed = pointForecast.getWindSpeed().getForecast();
        }
        Double windDirection = null;
        if (pointForecast.getWindDirection() != null) {
            windDirection = pointForecast.getWindDirection().getForecast();
        }
        String meanWaveStr = "";
        if (meanWavePeriod != null) {
            meanWaveStr = " (" + Formatter.formatDouble(meanWavePeriod, 2) + " sec)";
        }
        Double seaLevel = null;
        if (pointForecast.getSeaLevel() != null){
            seaLevel = pointForecast.getSeaLevel().getForecast();
        }
        StringBuilder buf = new StringBuilder();
        buf.append("<html>");
        buf.append("<b>METOC DATA for "+Formatter.formatLongDateTime(date)+"</b><br/>");
        buf.append("<table cellpadding='0' cellspacing='2'>");
        if (settings.getDataTypes().contains(MetocDataTypes.CU) || currentSpeed != null || currentDirection != null) {
            buf.append("<tr><td>Current:</td><td>"+Formatter.formatCurrentSpeed(currentSpeed, 1)+ " - " +Formatter.formatDegrees(currentDirection, 0)+"</td></tr>");
        }
        if (settings.getDataTypes().contains(MetocDataTypes.WI) || windSpeed != null || windDirection != null) {
            buf.append("<tr><td>Wind:</td><td>"+Formatter.formatWindSpeed(windSpeed, 0)+" - "+Formatter.formatDegrees(windDirection, 0)+"</td></tr>");
        }
        if (settings.getDataTypes().contains(MetocDataTypes.WA) || meanWaveDirection != null) {
            buf.append("<tr><td>Waves:</td><td>"+Formatter.formatMeters(meanWaveHeight, 1)+" - "+Formatter.formatDegrees(meanWaveDirection, 0)+ meanWaveStr + "</td></tr>");
        }
        if (settings.getDataTypes().contains(MetocDataTypes.SE) || seaLevel != null) {
            buf.append("<tr><td>Sea level:</td><td>"+Formatter.formatMeters(seaLevel, 1)+"</td></tr>");
        }
        buf.append("</table></html>");
        showText(buf.toString());
    }

}
