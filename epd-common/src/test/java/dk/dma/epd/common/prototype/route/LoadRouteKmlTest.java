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

public class LoadRouteKmlTest {

    private void loadKmlRoute(String filename) throws URISyntaxException, RouteLoadException {
        URL url = ClassLoader.getSystemResource(filename);
        Assert.assertNotNull(url);
        File file = new File(url.toURI());
        Assert.assertNotNull(file);
        NavSettings navSettings = new NavSettings();
        Route route = RouteLoader.loadKml(file, navSettings);
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
