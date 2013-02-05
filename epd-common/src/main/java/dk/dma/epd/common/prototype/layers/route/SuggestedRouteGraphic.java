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
package dk.dma.epd.common.prototype.layers.route;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisAdressedRouteSuggestion;

/**
 * Graphic for a suggested route
 */
public class SuggestedRouteGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    private List<Position> routeWaypoints;
    private AisAdressedRouteSuggestion routeSuggestion;
    private Stroke stroke;

    public SuggestedRouteGraphic(AisAdressedRouteSuggestion routeSuggestion, Stroke stroke) {
        this.routeSuggestion = routeSuggestion;
        this.stroke = stroke;
        routeWaypoints = routeSuggestion.getWaypoints();
        initGraphics();
        setVague(true);
    }

    public void initGraphics() {

        Stroke backgroundStroke = new BasicStroke(
                10.0f, // Width
                BasicStroke.CAP_ROUND, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                null, // Dash pattern
                0.0f);

        Position prevPoint = null;
        Position nextPoint = null;
        for (Position geoLocation : routeWaypoints) {
            nextPoint = geoLocation;
            if (prevPoint != null) {
                OMLine leg = new OMLine(prevPoint.getLatitude(), prevPoint.getLongitude(), nextPoint.getLatitude(), nextPoint
                        .getLongitude(), OMGraphicConstants.LINETYPE_RHUMB);
                leg.setStroke(stroke);
                leg.setLinePaint(new Color(183, 68, 237, 255));
                add(leg);
                
                if (!routeSuggestion.isReplied()) {
                    OMLine legBackground = new OMLine(prevPoint.getLatitude(), prevPoint.getLongitude(), nextPoint.getLatitude(),
                            nextPoint.getLongitude(), OMGraphicConstants.LINETYPE_RHUMB);
                    legBackground.setStroke(backgroundStroke);
                    legBackground.setLinePaint(new Color(42, 172, 12, 120));                
                    add(legBackground);
                }
            }
            prevPoint = nextPoint;
        }
    }

    public AisAdressedRouteSuggestion getRouteSuggestion() {
        return routeSuggestion;
    }

    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}
