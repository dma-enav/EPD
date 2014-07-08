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
