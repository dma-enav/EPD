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
package dk.dma.epd.common.prototype.route;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.model.route.RouteLoadException;
import dk.dma.epd.common.prototype.model.route.RouteLoader;
import dk.dma.epd.common.prototype.settings.handlers.RouteManagerCommonSettings;

public class LoadRouteKmlTest {

    private void loadKmlRoute(String filename) throws URISyntaxException, RouteLoadException {
        URL url = ClassLoader.getSystemResource(filename);
        Assert.assertNotNull(url);
        File file = new File(url.toURI());
        Assert.assertNotNull(file);
        RouteManagerCommonSettings<?> routeMgrSettings = new RouteManagerCommonSettings<>();
        Route route = RouteLoader.loadKml(file, routeMgrSettings);
        Assert.assertNotNull(route);
    }

    @Test
    public void loadWp() throws URISyntaxException, RouteLoadException {
        System.out.println("Loading KML wp file");
        loadKmlRoute("kmlwp.kml");
    }

    @Test
    public void loadRoute() throws URISyntaxException, RouteLoadException {
        System.out.println("Loading KML route file");
        loadKmlRoute("kmlroute.kml");
    }

}
