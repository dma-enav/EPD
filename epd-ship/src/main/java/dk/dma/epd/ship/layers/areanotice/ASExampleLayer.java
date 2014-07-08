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
package dk.dma.epd.ship.layers.areanotice;

import java.awt.Color;

import com.bbn.openmap.Layer;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.omGraphics.OMGraphicList;

/**
 * Simple example layer to display ASshapes.
 */
public class ASExampleLayer extends Layer {

    private static final long serialVersionUID = 1L;

    /*
     * A list of graphics to be painted on the map.
     */
    private OMGraphicList omgraphics;

    public ASExampleLayer() {
        omgraphics = new OMGraphicList();
        createGraphics(omgraphics);
    }

    public ASPoly createASPoly(int scaleFactor, int precision, double latitude, double longitude, int p1A, int p1D, int p2A,
            int p2D, int p3A, int p3D, int p4A, int p4D) {
        double[] lpoints = new double[10];
        ASPoly aspoly = new ASPoly(scaleFactor, precision, latitude, longitude, p1A, p1D, p2A, p2D, p3A, p3D, p4A, p4D, lpoints);
        return aspoly;

    }

    public ASCircleOrPoint createCircleOP(int scaleFactor, int precision, double latitude, double longitude, int radius) {
        ASCircleOrPoint coP = new ASCircleOrPoint(scaleFactor, precision, latitude, longitude, radius);
        coP.setLinePaint(Color.black);
        return coP;
    }

    public ASArc createASArc(int scaleFactor, int precision, double latitude, double longitude, int radius, int leftBound,
            int rightBound) {
        ASArc asarc = new ASArc(scaleFactor, precision, latitude, longitude, radius, leftBound, rightBound);
        asarc.setLinePaint(Color.green);
        return asarc;
    }

    public ASRectangle createRectPoly(int scaleFactor, int precision, double latitude, double longitude, int eDimension,
            int nDimension, double angle, double[] latlon) {
        ASRectangle rect = new ASRectangle(scaleFactor, precision, latitude, longitude, eDimension, nDimension, angle, latlon);
        rect.setLinePaint(Color.blue);
        return rect;
    }

    public ASTextBox createTextBox(int xpos, int ypos, int width, int height, java.lang.String text)

    {
        ASTextBox textbox = new ASTextBox(xpos, ypos, width, height, text);
        return textbox;

    }

    /*
     * Clears and then fills the given OMGraphicList. Creates three lines for
     * display on the map.
     */
    public OMGraphicList createGraphics(OMGraphicList graphics) {

        graphics.clear();

        // graphics.add(createTextBox(0, 0, 300, 40,
        // ASNoticeDescription.getDescription(55)));

        graphics.add(createCircleOP(3, 0, 55.1d, 14.9d, 35));
        double[] latlo = new double[10]; // 54.48f,12.528f
        graphics.add(createRectPoly(3, 0, 54.48d, 12.528d, 35, 15, -50, latlo));
        graphics.add(createASArc(3, 0, 55.286f, 12.467f, 100, 98, 111));
        double[] ar1 = new double[10];
        double[] ar2 = new double[10];
        double[] ar3 = new double[10];
        ASPoly poly = new ASPoly(3, 0, 54.2d, 14.3d, 14, 40, 15, 50, 20, 10, 270, 80, ar1);
        ASPoly poly2 = new ASPoly(3, 0, poly.getEndlatitude(), poly.getEndlongitude(), 30, 40, 46, 50, 70, 10, 45, 8, ar2);
        ASPoly poly3 = new ASPoly(3, 0, poly2.getEndlatitude(), poly2.getEndlongitude(), -30, 10, 21, 14, -21, 10, 134, 80, ar3);
        poly.setLinePaint(Color.red);
        graphics.add(poly);
        graphics.add(poly2);
        graphics.add(poly3);

        graphics.add(createTextBox(0, 0, 300, 40, ASNoticeDescription.getDescription(55)));

        return graphics;
    }

    // ----------------------------------------------------------------------
    // Layer overrides
    // ----------------------------------------------------------------------

    /*
     * Renders the graphics list.
     */
    @Override
    public void paint(java.awt.Graphics g) {
        omgraphics.render(g);
    }

    // ----------------------------------------------------------------------
    // ProjectionListener interface implementation
    // ----------------------------------------------------------------------

    /*
     */
    @Override
    public void projectionChanged(ProjectionEvent e) {
        omgraphics.project(e.getProjection(), true);
        repaint();
    }
    
}
