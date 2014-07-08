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
package dk.dma.epd.common.prototype.model.route;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.settings.NavSettings;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.common.util.ParseUtils;

/**
 * Utility class for loading routes in different file formats
 */
public class RouteLoader {

    private static final Logger LOG = LoggerFactory.getLogger(RouteLoader.class);

    public static Route loadRou(File file, NavSettings navSettings) throws RouteLoadException {
        Route route = null;
        try {
            RouParser rouParser = new RouParser();
            route = rouParser.parse(file, navSettings);
        } catch (IOException e) {
            throw new RouteLoadException("Failed to load route: " + e.getMessage());
        }
        return route;
    }

    @SuppressWarnings("resource")
    public static Route loadSimple(File file) throws RouteLoadException {
        Route route = new Route();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            boolean firstLine = true;
            String line = null;
            String formatErrorMsg = "Unrecognized route format";
            RouteLeg previousLeg = null;
            RouteWaypoint wp = null;
            while ((line = reader.readLine()) != null) {
                // Ignore empty lines and comments
                if (line.length() == 0 || line.startsWith("//") || line.startsWith("#")) {
                    continue;
                }
                // Split line by tab
                String[] fields = line.split("\t");
                // Handle first line name\tdeparture\tdestination
                if (firstLine) {
                    if (fields.length == 0) {
                        LOG.error("First line has no fields: " + line);
                        throw new RouteLoadException(formatErrorMsg);
                    }
                    route.setName(fields[0]);
                    if (fields.length >= 3) {
                        route.setDestination(fields[2]);
                    }
                    if (fields.length >= 2) {
                        route.setDeparture(fields[1]);
                    }
                    firstLine = false;
                    formatErrorMsg = "Error in route format";
                } else {
                    // Handle waypoint lines
                    if (fields.length < 7) {
                        LOG.error("Waypoint line has less than seven fields: " + line);
                        throw new RouteLoadException(formatErrorMsg);
                    }

                    // Create new waypoint
                    wp = new RouteWaypoint();
                    // Set end wp on previous leg
                    if (previousLeg != null) {
                        previousLeg.setEndWp(wp);
                    }
                    // Create leg
                    RouteLeg leg = new RouteLeg();
                    leg.setStartWp(wp);
                    wp.setOutLeg(leg);
                    wp.setInLeg(previousLeg);
                    previousLeg = leg;

                    // Set name
                    wp.setName(fields[0]);
                    // Get position
                    try {
                        wp.setPos(Position.create(ParseUtils.parseLatitude(fields[1]), ParseUtils.parseLongitude(fields[2])));
                    } catch (FormatException e) {
                        throw new RouteLoadException(formatErrorMsg + ": Error in position");
                    }
                    // Get turn radius
                    try {
                        wp.setTurnRad(ParseUtils.parseDouble(fields[6].trim()));
                    } catch (FormatException e) {
                        throw new RouteLoadException(formatErrorMsg + ": Error in turn radius");
                    }

                    // Get speed
                    try {
                        leg.setSpeed(ParseUtils.parseDouble(fields[3].trim()));
                    } catch (FormatException e) {
                        throw new RouteLoadException(formatErrorMsg + ": Error in speed");
                    }

                    // Get heading
                    try {
                        leg.setHeading(ParseUtils.parseInt(fields[4].trim()) == 1 ? Heading.RL : Heading.GC);
                    } catch (FormatException e) {
                        throw new RouteLoadException(formatErrorMsg + ": Error in heading");
                    }

                    // Get XTD
                    String xtd = fields[5];
                    String xtdStarboard = xtd;
                    String xtdPort = xtd;
                    if (xtd.contains(",")) {
                        String[] xtdParts = xtd.split(",");
                        if (xtdParts.length != 2) {
                            throw new RouteLoadException(formatErrorMsg + ": Error in XTD");
                        }
                        xtdStarboard = xtdParts[0];
                        xtdPort = xtdParts[1];
                    }
                    try {
                        leg.setXtdStarboard(ParseUtils.parseDouble(xtdStarboard.trim()));
                        leg.setXtdPort(ParseUtils.parseDouble(xtdPort.trim()));
                    } catch (FormatException e) {
                        throw new RouteLoadException(formatErrorMsg + ": Error in XTD");
                    }

                    // Calculate rot
                    wp.calcRot();

                    // Add waypoint
                    route.getWaypoints().add(wp);

                }
            }
            // Remove leg from last waypoint
            route.getWaypoints().getLast().setOutLeg(null);
            reader.close();
        } catch (IOException e) {
            LOG.error("Failed to load route file: " + e.getMessage());
            throw new RouteLoadException("Error reading route file");
        }

        return route;
    }

    public static Route loadKml(File file, NavSettings navSettings) throws RouteLoadException {
        KmlParser kmlParser = new KmlParser(file, navSettings);
        return kmlParser.parse();
    }

    public static Route loadRt3(File file, NavSettings navSettings) throws RouteLoadException {
        Route route = new Route();

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(file);
            // Normalize text representation
            doc.getDocumentElement().normalize();

            // Get name
            route.setName(doc.getDocumentElement().getAttribute("RtName"));
            if (route.getName() == null) {
                route.setName("NO NAME");
            }

            // Get waypoints
            NodeList waypointsList = doc.getElementsByTagName("WayPoints");
            if (waypointsList == null || waypointsList.getLength() < 1) {
                throw new RouteLoadException("Failed to parse RT3, no WayPoints node");
            }
            Element waypointsNode = (Element) waypointsList.item(0);
            NodeList waypoints = waypointsNode.getElementsByTagName("WayPoint");
            if (waypoints == null || waypoints.getLength() == 0) {
                throw new RouteLoadException("Failed to parse RT3, no WayPoint nodes");
            }

            RouteLeg lastLeg = null;

            // Iterate thorugh waypoints
            for (int i = 1; i < waypoints.getLength(); i++) {
                // Get waypoint element
                Element wpElem = (Element) waypoints.item(i);

                // Create route objects
                RouteWaypoint wp = new RouteWaypoint();
                RouteLeg outLeg = new RouteLeg();
                wp.setInLeg(lastLeg);
                wp.setOutLeg(outLeg);
                outLeg.setStartWp(wp);
                if (lastLeg != null) {
                    lastLeg.setEndWp(wp);
                }

                // Set defaults
                wp.setSpeed(navSettings.getDefaultSpeed());
                wp.setTurnRad(navSettings.getDefaultTurnRad());
                outLeg.setXtdPort(navSettings.getDefaultXtd());
                outLeg.setXtdStarboard(navSettings.getDefaultXtd());
                wp.setName(makeWpName(i + 1));

                // Wp name
                String name = wpElem.getAttribute("WPName");
                if (name != null && name.length() > 0) {
                    wp.setName(name);
                }

                // Lat and lon
                Double lat = ParseUtils.parseDouble(wpElem.getAttribute("Lat"));
                Double lon = ParseUtils.parseDouble(wpElem.getAttribute("Lon"));
                if (lat == null || lon == null) {
                    throw new RouteLoadException("Missing latitude/longitude for WP " + wp.getName());
                }
                // TODO recalculation from unknown format
                if (lat > 180 || lon > 90) {
                    throw new RouteLoadException("RT3 position projection is unknown");
                }
                wp.setPos(Position.create(lat, lon));

                // Turn rad
                String turnRad = wpElem.getAttribute("TurnRadius");
                if (turnRad != null && turnRad.length() > 0) {
                    wp.setTurnRad(ParseUtils.parseDouble(turnRad));
                }

                // XTE
                String xte = wpElem.getAttribute("PortXTE");
                if (xte != null && xte.length() > 0) {
                    outLeg.setXtdPort(ParseUtils.parseDouble(xte));
                }
                xte = wpElem.getAttribute("StbXTE");
                if (xte != null && xte.length() > 0) {
                    outLeg.setXtdStarboard(ParseUtils.parseDouble(xte));
                }

                // Leg type
                String legType = wpElem.getAttribute("LegType");
                if (legType != null && !legType.equals("0")) {
                    outLeg.setHeading(Heading.GC);
                } else {
                    outLeg.setHeading(Heading.RL);
                }

                wp.setSpeed(outLeg.getSpeed());
                route.getWaypoints().add(wp);
            }

            route.getWaypoints().getLast().setOutLeg(null);

        } catch (IOException e) {
            LOG.error("Failed to load RT3 route file: " + e.getMessage());
            throw new RouteLoadException("Error reading route file");
        } catch (Exception e) {
            LOG.error("Failed to parse RT3 route file: " + e.getMessage());
            throw new RouteLoadException("Error parsing RT3 route file");
        }

        return route;
    }

    public static String makeWpName(int i) {
        return String.format("WP_%03d", i);
    }

    public static Route pertinaciousLoad(File file, NavSettings navSettings) throws RouteLoadException {
        Route route = null;
        try {
            route = loadSimple(file);
        } catch (RouteLoadException e) {
            try {
                route = loadRou(file, navSettings);
            } catch (RouteLoadException e1) {
                try {
                    route = loadRt3(file, navSettings);
                } catch (RouteLoadException e2) {
                }
            }
        }

        if (route == null) {
            throw new RouteLoadException("Route file could no be recognized as any readable format");
        }

        return route;
    }

    public static boolean saveSimple(Route route, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file));) {

            // Write the header, making sure null isn't printed to the file
            String header = new String(route.getName() != null ? route.getName()
                    : "" + "\t" + route.getDestination() != null ? route.getDestination()
                            : "" + "\t" + route.getDeparture() != null ? route.getDeparture() : "");
            writer.write(header);
            writer.newLine();
            writer.flush();

            // write the waypoints to the file
            LinkedList<RouteWaypoint> routeWaypoints = route.getWaypoints();
            for (RouteWaypoint routeWaypoint : routeWaypoints) {
                if (routeWaypoint.getOutLeg() != null) {
                    Double turnRad = routeWaypoint.getTurnRad() != null ? routeWaypoint.getTurnRad() : new Double(0.0);
                    writer.write(routeWaypoint.getName() + "\t" + Formatter.latToPrintable(routeWaypoint.getPos().getLatitude())
                            + "\t" + Formatter.lonToPrintable(routeWaypoint.getPos().getLongitude()) + "\t"
                            + routeWaypoint.getOutLeg().getSpeed() + "\t" + (routeWaypoint.getOutLeg().getHeading().ordinal() + 1)
                            + "\t" + routeWaypoint.getOutLeg().getXtdStarboard() + "," + routeWaypoint.getOutLeg().getXtdPort()
                            + "\t" + turnRad);
                    writer.newLine();
                    writer.flush();
                } else {
                    writer.write(routeWaypoint.getName() + "\t" + Formatter.latToPrintable(routeWaypoint.getPos().getLatitude())
                            + "\t" + Formatter.lonToPrintable(routeWaypoint.getPos().getLongitude()) + "\t" +
                            // Last waypoint doesn't have an out leg
                            "0.00" + "\t" + "0" + "\t" + "0.0,0.0" + "\t" + routeWaypoint.getTurnRad());
                    writer.newLine();
                    writer.flush();
                }
            }
        } catch (IOException e) {
            LOG.error("Failed to save route file: " + e.getMessage());
            return false;
        }
        return true;
    }

}
