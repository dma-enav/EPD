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
package dk.dma.epd.common.prototype.model.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.micromata.opengis.kml.v_2_2_0.Coordinate;
import de.micromata.opengis.kml.v_2_2_0.Document;
import de.micromata.opengis.kml.v_2_2_0.Feature;
import de.micromata.opengis.kml.v_2_2_0.Folder;
import de.micromata.opengis.kml.v_2_2_0.Geometry;
import de.micromata.opengis.kml.v_2_2_0.Kml;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import de.micromata.opengis.kml.v_2_2_0.Placemark;
import de.micromata.opengis.kml.v_2_2_0.Point;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.settings.NavSettings;

/**
 * Parser for KML waypoints and route files
 */
public class KmlParser {

    private final NavSettings navSettings;
    private final Kml kml;

    public KmlParser(File file, NavSettings navSettings) throws RouteLoadException {
        this.navSettings = navSettings;
        try {
            kml = Kml.unmarshal(new FileInputStream(file));
        } catch (IOException e) {
            throw new RouteLoadException("Could not load KML file: " + e.getMessage());
        }
        if (kml == null) {
            throw new RouteLoadException("Unable to parse KML");
        }
    }

    public Route parse() throws RouteLoadException {
        String routeName = "Imported from KML";
        Route route = new Route();
        Feature feature = kml.getFeature();
        if (feature == null) {
            throw new RouteLoadException("No feature in KML file");
        }
        Folder folder = null;
        // Find the folder
        if (feature instanceof Folder) {
            folder = (Folder) feature;
        }
        Document doc = null;
        if (feature instanceof Document) {
            doc = (Document) feature;
            if (doc.getName() != null) {
                routeName = doc.getName();
            }
            for (Feature docFeature : doc.getFeature()) {
                if (docFeature instanceof Folder && folder == null) {
                    folder = (Folder) docFeature;
                }
            }
        }
        if (doc == null && folder == null) {
            throw new RouteLoadException("No document or folder in KML file");
        }
        List<Feature> features = null;
        if (folder != null) {
            features = folder.getFeature();            
        } else {
            features = doc.getFeature();
        }        
        List<String> wpNames = new ArrayList<>();
        List<Position> positions = new ArrayList<>();
        for (Feature folderFeature : features) {
            if (!(folderFeature instanceof Placemark)) {
                continue;
            }
            Placemark plMark = (Placemark) folderFeature;
            Geometry geometry = plMark.getGeometry();
            if (geometry instanceof Point) {
                List<Coordinate> coords = ((Point) geometry).getCoordinates();
                if (coords.size() != 1) {
                    throw new RouteLoadException("Waypoint has more than one coordinate");
                }
                wpNames.add(plMark.getName() != null ? plMark.getName() : RouteLoader.makeWpName(wpNames.size() + 1));
                positions.add(Position.create(coords.get(0).getLatitude(), coords.get(0).getLongitude()));
            } else if (geometry instanceof LineString) {
                routeName = plMark.getName();
                List<Coordinate> coords = ((LineString) geometry).getCoordinates();
                for (Coordinate coordinate : coords) {
                    wpNames.add(RouteLoader.makeWpName(wpNames.size() + 1));
                    positions.add(Position.create(coordinate.getLatitude(), coordinate.getLongitude()));
                }
            }
        }

        RouteLeg lastLeg = null;
        for (int i = 0; i < positions.size(); i++) {
            // Create route objects
            RouteWaypoint wp = new RouteWaypoint();
            RouteLeg outLeg = new RouteLeg();
            wp.setInLeg(lastLeg);
            wp.setOutLeg(outLeg);
            outLeg.setStartWp(wp);
            if (lastLeg != null) {
                lastLeg.setEndWp(wp);
            }

            // Set wp attributes
            wp.setName(wpNames.get(i));
            wp.setPos(positions.get(i));
            wp.setSpeed(navSettings.getDefaultSpeed());
            wp.setTurnRad(navSettings.getDefaultTurnRad());

            // Create leg properties
            outLeg.setXtdPort(navSettings.getDefaultXtd());
            outLeg.setXtdStarboard(navSettings.getDefaultXtd());
            outLeg.setHeading(Heading.RL);

            if (i == positions.size() - 1) {
                wp.setOutLeg(null);
            }

            route.getWaypoints().add(wp);

            lastLeg = outLeg;
        }

        route.setName(routeName);
        return route;
    }

}
