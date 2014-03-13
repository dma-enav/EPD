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
package dk.dma.epd.util.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.google.inject.Injector;

import dk.dma.commons.app.AbstractCommandLineTool;

public class AisToRoute extends AbstractCommandLineTool {
    
    /** The logger. */
    static final Logger LOG = LoggerFactory.getLogger(AisToRoute.class);
    
    @Parameter(names = "-in", required = true, description = "Input AIS file")
    String in;
    
    @Parameter(names = "-out", required = true, description = "Out route file")
    String out;
    
    @Parameter(names = "-mmsi", required = true, description = "MMSI number to make route for")
    Integer mmsi;
    
    @Override
    protected void run(Injector injector) throws Exception {
        inject(in, out, mmsi);        
    }
    
    public static void main(String[] args) throws Exception {
        new AisToRoute().execute(args);
    }
    
    private static void inject(String inFilename, String outFilename, int mmsi) throws Exception {
        LOG.info("Generate for MMSI: " + mmsi);
        RouteGenerator routeInjector = new RouteGenerator(inFilename, outFilename, mmsi);
        LOG.info("First pass - collect track");
        routeInjector.collectTrack();
        LOG.info("Generate route");
        routeInjector.generateRoute();
        LOG.info("Save route");
        routeInjector.saveRoute();
//        LOG.info("Inject broadcasts");
//        routeInjector.injectBroadcasts();
}


}
