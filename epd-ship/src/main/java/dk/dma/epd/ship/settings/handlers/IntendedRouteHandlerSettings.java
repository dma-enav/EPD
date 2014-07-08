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
package dk.dma.epd.ship.settings.handlers;

import java.io.IOException;

import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;
import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;
import dk.dma.epd.ship.service.IntendedRouteHandler;

/**
 * <p>
 * An instance of this class maintains settings specifically for {@link IntendedRouteHandler} instance(s), but it still obeys to
 * changes to settings that are considered global to all instances of {@link IntendedRouteHandlerCommon}.
 * </p>
 * 
 */
public class IntendedRouteHandlerSettings<OBSERVER extends IIntendedRouteHandlerSettingsObserver> extends
        IntendedRouteHandlerCommonSettings<OBSERVER> {

    /**
     * Specifies if the ship should broadcast it's intendedroute or not
     */
    private boolean broadcastIntendedRoute = true;

    /**
     * Specifies the intended route broadcast time
     */
    private long timeBetweenBroadCast = 1;

    /**
     * Specifies the change in ETA for a new route broadcast to be forced
     */
    private int adaptionTime = 1;

    /**
     * @return the broadcastIntendedRoute
     */
    public boolean isBroadcastIntendedRoute() {
        return broadcastIntendedRoute;
    }

    /**
     * @param broadcastIntendedRoute
     *            the broadcastIntendedRoute to set
     */
    public void setBroadcastIntendedRoute(boolean broadcastIntendedRoute) {

        this.broadcastIntendedRoute = broadcastIntendedRoute;

        for (OBSERVER obs : this.observers) {
            obs.sendIntendedRouteChanged(this.broadcastIntendedRoute);
        }

    }

    /**
     * @return the timeBetweenBroadCast
     */
    public long getTimeBetweenBroadCast() {
        return timeBetweenBroadCast;
    }

    /**
     * @param timeBetweenBroadCast
     *            the timeBetweenBroadCast to set
     */
    public void setTimeBetweenBroadCast(int timeBetweenBroadCast) {
        this.timeBetweenBroadCast = timeBetweenBroadCast;

        for (OBSERVER obs : this.observers) {
            obs.broadcastTimeChanged(this.timeBetweenBroadCast);
        }
    }

    /**
     * @return the adaptionTime
     */
    public int getAdaptionTime() {
        return adaptionTime;
    }

    /**
     * @param adaptionTime
     *            the adaptionTime to set
     */
    public void setAdaptionTime(int adaptionTime) {
        this.adaptionTime = adaptionTime;

        for (OBSERVER obs : this.observers) {
            obs.adaptiveBroadcastTimeChanged(this.adaptionTime);
        }
    }

    @Override
    protected void onLoadFailure(IOException error) {
        // TODO figure out what to do with read error.
    }

    @Override
    protected void onSaveFailure(IOException error) {
        // TODO possibly log save error or simply ignore it.
    }
}
