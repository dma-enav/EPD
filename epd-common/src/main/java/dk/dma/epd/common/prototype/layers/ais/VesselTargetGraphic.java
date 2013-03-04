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
package dk.dma.epd.common.prototype.layers.ais;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import com.bbn.openmap.omGraphics.OMGraphicConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMLine;
import com.bbn.openmap.omGraphics.OMText;
import com.bbn.openmap.proj.Length;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.ais.message.AisMessage;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.RotationalPoly;
import dk.dma.epd.common.math.Vector2D;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTargetSettings;
import dk.dma.epd.common.prototype.enavcloud.CloudIntendedRoute;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;

/**
 * Graphic for vessel target
 */
public class VesselTargetGraphic extends TargetGraphic {

    private static final long serialVersionUID = 1L;

    public static final float STROKE_WIDTH = 1.5f;

    private VesselTarget vesselTarget;

    private OMLine speedVector;
    private Font font;
    private OMText label;
    private double[] speedLL = new double[4];
    private LatLonPoint startPos;
    private LatLonPoint endPos;
    // private RotationalPoly vessel;
    private VesselTargetTriangle vessel;
    private RotationalPoly heading;
    private OMGraphicList marks = new OMGraphicList();
    private Vector2D pixelDist = new Vector2D();
    private Boolean marksVisible = false;
    private int[] markX = { -5, 5 };
    private int[] markY = { 0, 0 };
    private Paint paint;
    private Stroke stroke;
    private IntendedRouteGraphic routeGraphic = new IntendedRouteGraphic();
    private boolean showNameLabel = true;
//    AisSettings aisSettings;
//    NavSettings navSettings;

    public VesselTargetGraphic(AisSettings aisSettings, NavSettings navSettings) {
        super();
//        this.aisSettings = aisSettings;
//        this.navSettings = navSettings;
        this.showNameLabel = aisSettings.isShowNameLabels();
    }

    public VesselTargetGraphic(boolean showName) {
        super();
        this.showNameLabel = showName;
    }

    private void createGraphics() {
        speedVector = new OMLine(0, 0, 0, 0,
                OMGraphicConstants.LINETYPE_STRAIGHT);
        speedVector.setStroke(new BasicStroke(STROKE_WIDTH, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, // Miter limit
                new float[] { 10.0f, 8.0f }, // Dash pattern
                0.0f) // Dash phase
                );

        speedVector.setLinePaint(new Color(74, 97, 205, 255));
        stroke = new BasicStroke(STROKE_WIDTH);
        paint = new Color(74, 97, 205, 255);
        /*
         * int[] vesselX = {0,5,-5,0}; int[] vesselY = {-10,5,5,-10}; vessel =
         * new RotationalPoly(vesselX, vesselY, stroke, paint);
         */
        vessel = new VesselTargetTriangle();

        int[] headingX = { 0, 0 };
        int[] headingY = { 0, -100 };
        heading = new RotationalPoly(headingX, headingY, null, paint);

        font = new Font(Font.SANS_SERIF, Font.PLAIN, 11);
        label = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);

        add(label);
        add(0, vessel);
        add(speedVector);
        add(heading);
        add(marks);
        add(routeGraphic);
    }

    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings) {

        if (aisTarget instanceof VesselTarget) {

            vesselTarget = (VesselTarget) aisTarget;
            VesselPositionData posData = vesselTarget.getPositionData();
            VesselStaticData staticData = vesselTarget.getStaticData();
            VesselTargetSettings targetSettings = vesselTarget.getSettings();
//            AisIntendedRoute aisIntendedRoute = vesselTarget.getAisRouteData();
            CloudIntendedRoute cloudIntendedRoute = vesselTarget.getIntendedRoute();

            Position pos = posData.getPos();
            double trueHeading = posData.getTrueHeading();
            boolean noHeading = false;
            if (trueHeading == 511) {
                trueHeading = vesselTarget.getPositionData().getCog();
                noHeading = true;
            }

            double lat = pos.getLatitude();
            double lon = pos.getLongitude();

            if (size() == 0) {
                createGraphics();
            }

            // Disable Risk for now

            // Set color based on risk index

            // Risk risk =
            // EeINS.getRiskHandler().getRiskLevel(vesselTarget.getMmsi());

            // if(risk==null){
            // vessel.setLinePaint(new Color(74, 97, 205, 255));
            // }
            // else if( risk.getRiskNorm()< 0.002){
            // vessel.setLinePaint(Color.GREEN);
            // }else if( risk.getRiskNorm()> 0.01){
            // vessel.setLinePaint(Color.RED);
            // }else{
            // System.out.println("risk :"+ risk);
            // int green = (int)((0.01-risk.getRiskNorm())/0.01*255);
            // System.out.println(green);
            // vessel.setLinePaint(new Color(255,green<0?255:green,51));
            // }

            double sog = vesselTarget.getPositionData().getSog();
            double cogR = Math.toRadians(vesselTarget.getPositionData()
                    .getCog());
            double hdgR = Math.toRadians(trueHeading);

            // vessel.setLocation(lat, lon, OMGraphic.DECIMAL_DEGREES, hdgR);
            vessel.update(lat, lon, OMGraphicConstants.DECIMAL_DEGREES, hdgR,
                    this);
            heading.setLocation(lat, lon, OMGraphicConstants.DECIMAL_DEGREES,
                    hdgR);
            if (noHeading) {
                heading.setVisible(false);
            }

            speedLL[0] = (float) pos.getLatitude();
            speedLL[1] = (float) pos.getLongitude();
            this.startPos = new LatLonPoint.Double(lat, lon);

            float length = (float) Length.NM.toRadians(aisSettings
                    .getCogVectorLength() * (sog / 60.0));

            this.endPos = startPos.getPoint(length, cogR);
            speedLL[2] = endPos.getLatitude();
            speedLL[3] = endPos.getLongitude();
            speedVector.setLL(speedLL);

            // Do not show speed vector if moored
            // speedVector.setVisible(posData.getNavStatus() != 5);

            // Add minute marks
            marks.clear();
            for (int i = 1; i < 6; i++) {
                float newMarker = (float) Length.NM.toRadians(navSettings
                        .getCogVectorLength() / 6 * i * (sog / 60.0));
                LatLonPoint marker = startPos.getPoint(newMarker, (float) cogR);
                RotationalPoly vtm = new RotationalPoly(markX, markY, stroke,
                        paint);
                vtm.setLocation(marker.getLatitude(), marker.getLongitude(),
                        OMGraphicConstants.DECIMAL_DEGREES, cogR);
                marks.add(vtm);
            }

            if (!marksVisible) {
                marks.setVisible(false);
            }

            // Set label
            label.setLat(lat);
            label.setLon(lon);
            if (trueHeading > 90 && trueHeading < 270) {
                label.setY(-10);
            } else {
                label.setY(20);
            }

            // Determine name
            String name;
            if (staticData != null) {
                name = AisMessage.trimText(staticData.getName());
            } else {
                Long mmsi = vesselTarget.getMmsi();
                name = "ID:" + mmsi.toString();
            }
            label.setData(name);

            if (showNameLabel) {
                label.setVisible(true);
            } else {
                label.setVisible(false);
            }
            // Intended route graphic
            routeGraphic.update(vesselTarget, name, cloudIntendedRoute, pos);
            if (!targetSettings.isShowRoute()) {
                routeGraphic.setVisible(false);
            }

        }
    }

    @Override
    public void setMarksVisible(Projection projection, AisSettings aisSettings, NavSettings navSettings) {
        if (startPos != null && endPos != null) {
            Point2D start = projection.forward(startPos);
            Point2D end = projection.forward(endPos);
            pixelDist.setValues(start.getX(), start.getY(), end.getX(),
                    end.getY());
            if (pixelDist.norm() < aisSettings.getShowMinuteMarksAISTarget()) {
                marksVisible = false;
                marks.setVisible(false);
            } else {
                marksVisible = true;
                marks.setVisible(true);
            }
        }
    }

    public VesselTarget getVesselTarget() {
        return vesselTarget;
    }

    public void setShowNameLabel(boolean showNameLabel) {
        this.showNameLabel = showNameLabel;
    }

    public boolean getShowNameLabel() {
        return showNameLabel;
    }

    public IntendedRouteGraphic getRouteGraphic() {
        return routeGraphic;
    }

}
