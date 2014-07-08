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
package dk.dma.epd.ship.layers.routeedit;

import dk.dma.epd.common.prototype.layers.routeedit.RouteEditLayerCommon;
import dk.dma.epd.ship.event.RouteEditMouseMode;

/**
 * Layer for drawing new route. When active it will use a panning mouse mode.
 */
public class RouteEditLayer extends RouteEditLayerCommon {

    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor
     */
    public RouteEditLayer() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[1];
        ret[0] = RouteEditMouseMode.MODE_ID;
        return ret;
    }
}
