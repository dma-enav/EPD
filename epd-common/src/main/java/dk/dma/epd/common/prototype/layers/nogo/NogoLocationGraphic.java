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
package dk.dma.epd.common.prototype.layers.nogo;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Date;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMPoint;
import com.bbn.openmap.omGraphics.OMPoly;

import dk.dma.enav.model.geometry.Position;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

/**
 * Graphic for MSI location/area
 */
@SuppressWarnings("unused")
public class NogoLocationGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private NogoPolygon polygon;
    
    private Date validFrom;
    private Date validTo;
    private int draught;
    private String message;
    private int errorCode;
    private Position northWest;
    private Position southEast;

    private Color nogoColor = Color.red;

    private Rectangle hatchFillRectangle;
    private BufferedImage hatchFill;
    
    private boolean frame;

    public NogoLocationGraphic(NogoPolygon polygon, Date validFrom,
            Date validTo, Double draught, String message,
            Position northWest, Position southEast, int errorCode,
            boolean frame, Color color) {
        super();
        this.polygon = polygon;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.draught = Math.abs(draught.intValue());
        this.message = message;
        this.northWest = northWest;
        this.southEast = southEast;
        this.errorCode = errorCode;
        this.frame = frame;
        this.nogoColor = color;

        // System.out.println(message);
        // Draw the data
        if (polygon != null && (errorCode == 18 || errorCode == 0)) {
            hatchFill = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D big = hatchFill.createGraphics();
            Composite originalComposite = big.getComposite();
            big.setComposite(makeComposite(0.2f));
            big.setColor(nogoColor);
            big.drawLine(0, 0, 10, 10);

            hatchFillRectangle = new Rectangle(0, 0, 10, 10);
            big.setComposite(originalComposite);

            // drawAreaBox();
            // drawPolyline();
            drawPolygon();
            // drawPoints();

        }
        
        // Draw the message
        if (errorCode == -1 || errorCode == 1 || errorCode == 17) {
            OMPoint polyPoint = new OMPoint(0, 0);

            //Dummy point to make selection appear
            // polyPoint.setVisible(false);
            add(polyPoint);
        }
        
        if (frame && errorCode != 17){
            drawAreaBox();
        }

    }

    private AlphaComposite makeComposite(float alpha) {
        int type = AlphaComposite.SRC_OVER;
        return AlphaComposite.getInstance(type, alpha);
    }

    private void drawPolyline() {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        double[] polyPoints = new double[polygon.getPolygon().size() * 2];
        int j = 0;
        for (int i = 0; i < polygon.getPolygon().size(); i++) {
            polyPoints[j] = polygon.getPolygon().get(i).getLat();
            polyPoints[j + 1] = polygon.getPolygon().get(i).getLon();
            j += 2;
        }
        OMPoly poly = new OMPoly(polyPoints, OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);

        poly.setLinePaint(nogoColor);
        poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

        poly.setIsPolygon(true);

        add(poly);

    }

    private void drawPoints() {
        for (int i = 0; i < polygon.getPolygon().size(); i++) {
            OMPoint polyPoint = new OMPoint(polygon.getPolygon().get(i)
                    .getLat(), polygon.getPolygon().get(i).getLon());

            polyPoint.setLinePaint(nogoColor);
            polyPoint.setFillPaint(new Color(0, 0, 0, 10));
            polyPoint.setTextureMask(new TexturePaint(hatchFill,
                    hatchFillRectangle));
            add(polyPoint);
        }

    }

    private void drawPolygon() {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        double[] polyPoints = new double[polygon.getPolygon().size() * 2 + 2];
        int j = 0;
        for (int i = 0; i < polygon.getPolygon().size(); i++) {
            polyPoints[j] = polygon.getPolygon().get(i).getLat();
            polyPoints[j + 1] = polygon.getPolygon().get(i).getLon();
            j += 2;
        }
        polyPoints[j] = polyPoints[0];
        polyPoints[j + 1] = polyPoints[1];
        OMPoly poly = new OMPoly(polyPoints, OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);
        poly.setLinePaint(clear);
        poly.setFillPaint(new Color(0, 0, 0, 1));
        poly.setTextureMask(new TexturePaint(hatchFill, hatchFillRectangle));

        add(poly);

    }

    @SuppressWarnings("deprecation")
    private void drawAreaBox() {
        // space for lat-lon points plus first lat-lon pair to close the polygon
        

        
        
        // Four lines are needed

        double[] westernLine = new double[4];
        westernLine[0] = northWest.getLatitude();
        westernLine[1] = northWest.getLongitude();
        westernLine[2] = southEast.getLatitude();
        westernLine[3] = northWest.getLongitude();

        OMPoly poly = new OMPoly(westernLine, OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);

        
        
        double[] easternLine = new double[4];
        easternLine[0] = northWest.getLatitude();
        easternLine[1] = southEast.getLongitude();
        easternLine[2] = southEast.getLatitude();
        easternLine[3] = southEast.getLongitude();

        OMPoly poly1 = new OMPoly(easternLine, OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);

        double[] northernLine = new double[4];
        northernLine[0] = northWest.getLatitude();
        northernLine[1] = northWest.getLongitude();
        northernLine[2] = northWest.getLatitude();
        northernLine[3] = southEast.getLongitude();

        OMPoly poly2 = new OMPoly(northernLine, OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);

        double[] southernLine = new double[4];
        southernLine[0] = southEast.getLatitude();
        southernLine[1] = northWest.getLongitude();
        southernLine[2] = southEast.getLatitude();
        southernLine[3] = southEast.getLongitude();

        OMPoly poly3 = new OMPoly(southernLine, OMGraphicConstants.DECIMAL_DEGREES,
                OMGraphicConstants.LINETYPE_RHUMB, 1);

        if (nogoColor == Color.ORANGE){
            poly.setLineColor(Color.GRAY);
            poly1.setLineColor(Color.GRAY);
            poly2.setLineColor(Color.GRAY);
            poly3.setLineColor(Color.GRAY);
        }
        
        add(poly);
        add(poly1);
        add(poly2);
        add(poly3);

    }

//    @Override
//    public void render(Graphics gr) {
//
//        Graphics2D image = (Graphics2D) gr;
//        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                RenderingHints.VALUE_ANTIALIAS_ON);
//        super.render(image);
//        Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
//
//        String message0 = "NoGo Active, only valid from " + validFrom + " to "
//                + validTo;
//        String message1 = "Do not use this for navigational purposes!";
//        String message2 = "Only valid for draughts at " + draught
//                + " meters and below";
//
//        String messageTide = "NoGo Active, no Tide Data found showing only static depth";
//
//        gr.setFont(font);
//        gr.setColor(Color.red);
//
//        // Errorcode -1 means server experinced a timeout
//        // Errorcode 0 means everything went ok
//        // Errorcode 1 is the standby message
//        // Errorcode 17 means no data
//        // Errorcode 18 means no tide data
//
//        if (frame) {
//
//            if (errorCode == 0) {
//                gr.drawString(message0, 5, 20);
//                gr.drawString(message1, 5, 40);
//                gr.drawString(message2, 5, 60);
//            }
//
//            if (errorCode == 18) {
//                gr.drawString(messageTide, 5, 20);
//                gr.drawString(message1, 5, 40);
//                gr.drawString(message2, 5, 60);
//            }
//
//            if (errorCode == -1 || errorCode == 1 || errorCode == 17) {
//                gr.drawString(message, 5, 20);
//
//            }
//        }
//    }

}
