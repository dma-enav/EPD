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

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.reader.AisReader;
import dk.dma.ais.reader.AisReaders;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.Converter;

/**
 * Inject intended route broadcasts into AIS stream based on vessel tracks and generated routes.
 */
public class RouteGenerator {
    
    static final Logger LOG = LoggerFactory.getLogger(RouteGenerator.class);

    private String inFilename;
    private String outFilename;
    private TrackCollector trackCollector;
    private List<TimePoint> route;

    public RouteGenerator(String inFilename, String outDir, int mmsi) {
        this.inFilename = inFilename;
        this.outFilename = outDir + "/route-" + mmsi + ".txt";
        trackCollector = new TrackCollector(mmsi);
    }

    public void collectTrack() throws Exception {
        // Make reader for input file
        AisReader aisReader = AisReaders.createReaderFromInputStream(new FileInputStream(inFilename));
        // Register handler
        aisReader.registerHandler(trackCollector);
        // Start reader thread
        aisReader.start();
        // Wait for thread to finish
        aisReader.join();
    }

    public void generateRoute() {
        // Create route generator
        IRouteGenerator generator;
        //generator = new SimpleRouteGenerator();
        generator = new ApproxRouteGenerator();

        // Generate route
        route = generator.generateRoute(trackCollector.getSortedTrack());
    }

    public void saveRoute() throws Exception {
        if (route == null) {
            return;
        }
        List<Double> speeds = new ArrayList<>();        
        // Determine speeds
        for (int i=1; i < route.size(); i++) {
            TimePoint p1 = route.get(i - 1);
            TimePoint p2 = route.get(i);
            double dist = Converter.metersToNm(p1.getPos().rhumbLineDistanceTo(p2.getPos()));
            double time = Math.abs(p2.getTime().getTime() - p1.getTime().getTime()) / 3600000.0;
            double speed = dist / time; 
            speeds.add(speed);
        }
        // Add last speed
        speeds.add(12.0);
        
        FileWriter outFile = new FileWriter(outFilename);
        PrintWriter out = new PrintWriter(outFile);
        out.println("Generated route");
        int i = 0;
        for (TimePoint point : route) {
            List<String> fields = new ArrayList<String>();
            fields.add(String.format("WP_%03d", i));
            fields.add(Formatter.latToPrintable(point.getLatitude()));
            fields.add(Formatter.lonToPrintable(point.getLongitude()));            
            fields.add(String.format(Locale.US, "%.5f", speeds.get(i)));
            fields.add("1");
            fields.add("0.100");
            fields.add("0.500");
            out.println(StringUtils.join(fields.iterator(), "\t"));
            i++;
        }
        outFile.close();
    }

    public static void inject(String inFilename, String outFilename, int mmsi) throws Exception {
        LOG.info("Generate for MMSI: " + mmsi);
        RouteGenerator routeInjector = new RouteGenerator(inFilename, outFilename, mmsi);
        LOG.info("First pass - collect track");
        routeInjector.collectTrack();
        LOG.info("Generate route");
        routeInjector.generateRoute();
        LOG.info("Save route");
        routeInjector.saveRoute();
    }

    public static void usage() {
        LOG.info("Usage: RouteGenerator <infile> <outfile> <mmsi>");
        System.exit(0);
    }

}
