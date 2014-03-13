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

    public RouteGenerator(String inFilename, String outFilename, int mmsi) {
        this.inFilename = inFilename;
        this.outFilename = outFilename;
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
