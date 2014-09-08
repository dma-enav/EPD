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
import dk.dma.epd.common.prototype.settings.NavSettings;

public class LoadRouteRt3Test {

    private Route _loadRt3Route(String filename) throws URISyntaxException, RouteLoadException {
        URL url = ClassLoader.getSystemResource(filename);
        Assert.assertNotNull(url);
        File file = new File(url.toURI());
        Assert.assertNotNull(file);
        NavSettings navSettings = new NavSettings();
        Route route = RouteLoader.loadRt3(file, navSettings);
        Assert.assertNotNull(route);
        return route;
    }

    @Test
    public void loadTest() throws URISyntaxException, RouteLoadException {
        System.out.println("Loading RT3 file");
        Route route = _loadRt3Route("example.rt3");
        Assert.assertEquals(route.getWaypoints().size(), 11);
        Assert.assertTrue(route.getWaypoints().get(0).getOutLeg().getSpeed() == 6.9);
    }

}
