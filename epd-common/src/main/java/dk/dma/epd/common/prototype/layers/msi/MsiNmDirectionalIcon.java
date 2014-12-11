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
package dk.dma.epd.common.prototype.layers.msi;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.proj.coords.LatLonPoint;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import dk.dma.epd.common.util.Calculator;

import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * Graphic for MSI-NM icon showing relevant off chart MSI
 */
public class MsiNmDirectionalIcon extends OMGraphicList implements ProjectionListener {
    private static final long serialVersionUID = -6808339529053676255L;
    private static final int IMAGE_SIZE = 42;

    private static final ImageIcon DIRECTION_IMAGE = EPD.res().getCachedImageIcon("/images/msi/msi_direction_arrow_transparent_42.png");
    private static final ImageIcon MSI_MARKER_IMAGE = EPD.res().getCachedImageIcon("/images/msi/msi_direction_transparent_42.png");
    private static final ImageIcon NM_MARKER_IMAGE = EPD.res().getCachedImageIcon("/images/msi/nm_direction_transparent_42.png");

    private Point2D intersection;
    private MapBean mapBean;
    private MsiNmNotification message;
    
    public MsiNmDirectionalIcon(MapBean mapBean) {
        super();
        setVague(true);
        this.mapBean = mapBean;
        mapBean.addProjectionListener(this);
    }
    
    public void setMarker(MsiNmNotification message) {
        this.message = message;
        Position msiNmLocation = message.getLocation();
        LatLonPoint center = (LatLonPoint) mapBean.getCenter();
        Position geoCenter = Position.create(center.getLatitude(), center.getLongitude());
        double bearing = Calculator.bearing(geoCenter, msiNmLocation, Heading.RL);
        
        Projection projection = mapBean.getProjection();
        Point2D projectedMSI = projection.forward(msiNmLocation.getLatitude(), msiNmLocation.getLongitude());
        
        Point2D origin = new Point2D.Double(mapBean.getWidth()*0.5f, mapBean.getHeight()*0.5f);
        Line2D direction = new Line2D.Double(origin, projectedMSI);
        
        double boxWidth = mapBean.getWidth()-IMAGE_SIZE/2;
        double boxHeight = mapBean.getHeight()-IMAGE_SIZE/2;
        Line2D topFrame = new Line2D.Double(IMAGE_SIZE/2,IMAGE_SIZE/2,boxWidth,IMAGE_SIZE/2);
        Line2D rightFrame = new Line2D.Double(boxWidth,IMAGE_SIZE/2,boxWidth,boxHeight);
        Line2D bottomFrame = new Line2D.Double(IMAGE_SIZE/2,boxHeight,boxWidth,boxHeight);
        Line2D leftFrame = new Line2D.Double(IMAGE_SIZE/2,IMAGE_SIZE/2,IMAGE_SIZE/2,boxHeight); 
        
        boolean intersects = false;
        
        if(intersects(direction,topFrame)) {
            intersects = true;
        }
        if(intersects(direction,rightFrame)) {
            intersects = true;
        }
        if(intersects(direction,bottomFrame)) {
            intersects = true;
        }
        if(intersects(direction,leftFrame)) {
            intersects = true;
        }
        
        if(!intersects) {
            return;
        }

        int x = Math.round((float) intersection.getX());
        int y = Math.round((float) intersection.getY());

        CenterRaster directionRaster = new CenterRaster(x, y, DIRECTION_IMAGE);
        directionRaster.setRotationAngle(Math.toRadians(bearing));

        ImageIcon icon = message.isMsi() ? MSI_MARKER_IMAGE : NM_MARKER_IMAGE;
        CenterRaster markerRaster = new CenterRaster(x, y, icon);
        
        add(markerRaster);
        add(directionRaster);
    }
    
    public boolean intersects(Line2D direction, Line2D frame) {
        double d = (frame.getY2() - frame.getY1()) * (direction.getX2() - direction.getX1()) - 
                   (frame.getX2() - frame.getX1()) * (direction.getY2() - direction.getY1());
        
        double n_a = (frame.getX2() - frame.getX1()) * (direction.getY1() - frame.getY1()) - 
                        (frame.getY2() - frame.getY1()) * (direction.getX1() - frame.getX1()); 
        
        double n_b = (direction.getX2() - direction.getX1()) * (direction.getY1() - frame.getY1()) - 
                        (direction.getY2() - direction.getY1()) * (direction.getX1() - frame.getX1());
        
        if(d == 0) {
            return false;
        }
        
        double ua = n_a / d;
        double ub = n_b / d;
        
        if(ua >= 0d && ua <= 1d && ub >= 0d && ub <= 1d) {
            intersection = new Point2D.Double();
            intersection.setLocation(
                    direction.getX1() + ua * (direction.getX2() - direction.getX1()),
                    direction.getY1() + ua * (direction.getY2() - direction.getY1()));
            return true;
        }
        return false;
    }

    @Override
    public void projectionChanged(ProjectionEvent e) {
        clear();
        setMarker(message);
    }
    
    @Override
    public void render(Graphics gr) {
        Graphics2D image = (Graphics2D) gr;
        image.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        super.render(image);
    }
    
    public MsiNmNotification getMessage() {
        return message;
    }
}
