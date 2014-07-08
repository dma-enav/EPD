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
package dk.dma.epd.common.prototype.gui.metoc;

import java.util.Date;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
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
        Date now = PntTime.getInstance().getDate();
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
