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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.common.prototype.model.route.IntendedRoute;
import dk.dma.epd.common.prototype.service.IntendedRouteHandlerCommon;

/**
 * Toggle displaying the intended route for a vessel target
 */
public class IntendedRouteToggle extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;

    private IntendedRoute intendedRoute;
    private IntendedRouteHandlerCommon intendedRouteHandler;
    private AisHandlerCommon aisHandler;

    /**
     * Constructor
     */
    public IntendedRouteToggle() {
        super();
    }

    /**
     * Called when the menu item is enacted
     */
    @Override
    public void doAction() {
        System.out.println("Toggle visiblity");
        if (aisHandler != null) {

            // Try to find exiting target
            VesselTarget vesselTarget = aisHandler.getVesselTarget(intendedRoute.getMmsi());
            if (vesselTarget != null) {
                System.out.println("Visibility Updated on vessel target");
                vesselTarget.setShowIntendedRoute(!vesselTarget.isShowIntendedRoute());
                intendedRoute.setVisible(vesselTarget.isShowIntendedRoute());
            }else{
                intendedRoute.setVisible(!intendedRoute.isVisible());
            }
        } else {
            intendedRoute.setVisible(!intendedRoute.isVisible());
        }

        intendedRouteHandler.fireIntendedEvent(intendedRoute);
    }

    /**
     * Sets the intended route
     * 
     * @param intendedRoute
     *            the intended route
     */
    public void setIntendedRoute(IntendedRoute intendedRoute) {
        this.intendedRoute = intendedRoute;
    }

    /**
     * Sets the intended route handler
     * 
     * @param intendedRouteHandler
     *            tSets the intended route handler
     */
    public void setIntendedRouteHandler(IntendedRouteHandlerCommon intendedRouteHandler) {
        this.intendedRouteHandler = intendedRouteHandler;
    }

    /**
     * @param aisHandler
     *            the aisHandler to set
     */
    public void setAisHandler(AisHandlerCommon aisHandler) {
        this.aisHandler = aisHandler;
    }

}
