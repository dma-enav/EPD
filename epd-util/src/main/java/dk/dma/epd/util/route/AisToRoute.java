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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beust.jcommander.Parameter;
import com.google.inject.Injector;

import dk.dma.ais.message.AisMessage;
import dk.dma.ais.reader.AisReader;
import dk.dma.ais.reader.AisReaders;
import dk.dma.commons.app.AbstractCommandLineTool;
import dk.dma.enav.util.function.Consumer;

public class AisToRoute extends AbstractCommandLineTool {
    
    /** The logger. */
    static final Logger LOG = LoggerFactory.getLogger(AisToRoute.class);
    
    @Parameter(names = "-in", required = true, description = "Input AIS file")
    String in;
    
    @Parameter(names = "-out", required = true, description = "Output directory")
    String outDir;
    
    @Parameter(names = "-mmsi", required = false, description = "MMSI numbers to make route for, comma separated. Default all.")
    List<Integer> mmsis;
    
    @Override
    protected void run(Injector injector) throws Exception {
        inject(in, outDir, mmsis);        
    }
    
    public static void main(String[] args) throws Exception {
        new AisToRoute().execute(args);
    }
    
    private static void inject(String inFilename, String outDir, Collection<Integer> mmsis) throws Exception {
        if (mmsis == null) {
            final Set<Integer> mmsisFromFile = new HashSet<Integer>();
            AisReader reader = AisReaders.createReaderFromFile(inFilename);
            reader.registerHandler(new Consumer<AisMessage>() {         
                @Override
                public void accept(AisMessage aisMessage) {
                    mmsisFromFile.add(aisMessage.getUserId());
                }
            });
            reader.start();
            reader.join();
            mmsis = mmsisFromFile;
        }        
        
        for (Integer mmsi : mmsis) {
            LOG.info("Generate for MMSI: " + mmsi);
            RouteGenerator routeInjector = new RouteGenerator(inFilename, outDir, mmsi);
            LOG.info("First pass - collect track");
            routeInjector.collectTrack();
            LOG.info("Generate route");
            routeInjector.generateRoute();
            LOG.info("Save route");
            routeInjector.saveRoute();

        }
}


}
