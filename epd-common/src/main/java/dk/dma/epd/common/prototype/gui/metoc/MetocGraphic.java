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
package dk.dma.epd.common.prototype.gui.metoc;

import java.util.Date;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.frv.enav.common.xml.metoc.MetocForecast;
import dk.frv.enav.common.xml.metoc.MetocForecastPoint;

/**
 * Metoc graphic for route
 */
public class MetocGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private Route route;
    private boolean activeRoute;
    private int step = 1;
    private EnavSettings eNavSettings;

    public MetocGraphic(Route route, boolean activeRoute, EnavSettings eNavSettings) {
        this.route = route;
        this.activeRoute = activeRoute;
        this.eNavSettings = eNavSettings;
        paintMetoc();
    }
    
    public void paintMetoc(){
        clear();
        MetocForecast metocForecast = route.getMetocForecast();
        List<MetocForecastPoint> forecasts = metocForecast.getForecasts();
        Date now = GnssTime.getInstance().getDate();
        for (int i = 0; i < forecasts.size(); i += step) {
            MetocForecastPoint metocPoint = forecasts.get(i);

            // If active route, only show if 2 min in future or more
            if (activeRoute) {
                long fromNow = (metocPoint.getTime().getTime() - now.getTime()) / 1000 / 60;
                if (fromNow < 2) {
                    continue;
                }
            }

            MetocPointGraphic metocPointGraphic = new MetocPointGraphic(metocPoint, this, eNavSettings);
            add(metocPointGraphic);
            
        }
    }

    public Route getRoute() {
        return route;
    }

    public void setStep(int step) {
        this.step = step;
    }
    
    public int getStep() {
        return step;
    }
}
