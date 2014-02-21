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
package dk.dma.epd.ship.event;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Properties;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.PanMouseMode;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.ship.EPDShip;

/**
 * Mouse mode for route edit 
 */
public class RouteEditMouseMode extends AbstractCoordMouseMode {

    private static final long serialVersionUID = 1L;
    
    /**
     * Mouse Mode identifier, which is "RouteEdit".
     */
    public static final transient String MODE_ID = "RouteEdit";    
    public static final String OPAQUENESS_PROPERTY = "opaqueness";
    public static final String LEAVE_SHADOS_PROPERTY = "leaveShadow";
    public static final String USE_CURSOR_PROPERTY = "useCursor";
    public static final float DEFAULT_OPAQUENESS = 0.5f;
    
    private boolean isDragged;
    private BufferedImage bufferedRenderingImage;
    private int oX, oY;
    private float opaqueness;
    private boolean leaveShadow;
    private boolean useCursor;
    
    private BufferedImage onScreenMap;

    /**
     * Construct a RouteEditMouseMode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public RouteEditMouseMode() {
        this(true);
    }

    /**
     * Construct a NavMouseMode. Lets you set the consume mode. If the events
     * are consumed, then a MouseEvent is sent only to the first
     * MapMouseListener that successfully processes the event. If they are not
     * consumed, then all of the listeners get a chance to act on the event.
     * 
     * @param shouldConsumeEvents the mode setting.
     */
    public RouteEditMouseMode(boolean shouldConsumeEvents) {
        super(MODE_ID, true);
        this.useCursor   = true;
        this.leaveShadow = true;
        this.opaqueness  = DEFAULT_OPAQUENESS;
        // override the default cursor
        this.setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActive(boolean val) {
        if (!val && this.bufferedRenderingImage != null) {
            this.bufferedRenderingImage.flush();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(String prefix, Properties props) {
        super.setProperties(prefix, props);
        prefix = PropUtils.getScopedPropertyPrefix(prefix);

        this.opaqueness = PropUtils.floatFromProperties(
                props, 
                prefix + OPAQUENESS_PROPERTY, 
                this.opaqueness);
        
        this.leaveShadow = PropUtils.booleanFromProperties(
                props, 
                prefix + LEAVE_SHADOS_PROPERTY, 
                this.leaveShadow);

        this.useCursor = PropUtils.booleanFromProperties(
                props, 
                prefix + USE_CURSOR_PROPERTY, 
                this.useCursor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getProperties(Properties props) {
        props = super.getProperties(props);
        String prefix = PropUtils.getScopedPropertyPrefix(this);
        props.put(prefix + OPAQUENESS_PROPERTY, Float.toString(this.opaqueness));
        props.put(prefix + LEAVE_SHADOS_PROPERTY,
                Boolean.toString(this.leaveShadow));
        props.put(prefix + USE_CURSOR_PROPERTY, Boolean.toString(this.useCursor));
        return props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getPropertyInfo(Properties props) {
        props = super.getPropertyInfo(props);

        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                OPAQUENESS_PROPERTY,
                "Transparency",
                "Transparency level for moving map, between 0 (clear) and 1 (opaque).",
                null);
        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                LEAVE_SHADOS_PROPERTY,
                "Leave Shadow",
                "Display current map in background while panning.",
                "com.bbn.openmap.util.propertyEditor.YesNoPropertyEditor");

        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                USE_CURSOR_PROPERTY,
                "Use Cursor",
                "Use hand cursor for mouse mode.",
                "com.bbn.openmap.util.propertyEditor.YesNoPropertyEditor");

        return props;
    }

    /**
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     *      The first click for drag, the image is generated. This image is
     *      redrawing when the mouse is move, but, I need to repain the original
     *      image.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        
        // Create and set drag cursor.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image cursorImg = toolkit.getImage(this.getClass().getResource("/images/toolbar/drag_on_mouse.png"));
        Cursor cursor   = toolkit.createCustomCursor(cursorImg, new Point(0, 0), "drag");
        EPDShip.getInstance().getMainFrame().getChartPanel().setCursor(cursor);

        // Obtain map.
        MapBean map = (MapBean) e.getSource();
        Point2D point = map.getNonRotatedLocation(e);
        int x = (int) point.getX();
        int y = (int) point.getY();
        
        // If the window is not being dragged.
        if (!this.isDragged) {
            
            EPDShip.getInstance().getMainFrame().getChartPanel().getDragMapRenderer().updateFinalBuffer();
            
            // Create a image of the current view. 
            this.onScreenMap = new BufferedImage(map.getWidth(), map.getHeight(), BufferedImage.TYPE_INT_RGB);
            map.paint(this.onScreenMap.getGraphics());
            this.oX = x;
            this.oY = y;
            
            // Hide the map.
            EPDShip.getInstance().getMainFrame().getChartPanel().getMap().setVisible(false);
            
            // Window is now being dragged.
            this.isDragged = true;
                
        // If the window is being dragged.
        } else {
        
            // Get start start x and y.
            final int startX = map.getWidth();
            final int startY = map.getHeight();

            // Get the moving potition.
            final int posX = startX - (x - oX);
            final int posY = startY - (y - oY);

            
            final BufferedImage offScreenMap = EPDShip.getInstance().getMainFrame()
                    .getChartPanel().getDragMapRenderer().getFinalBuffer().getSubimage(posX,
                    posY, map.getWidth(), map.getHeight());
            
            final BufferedImage renderImage = offScreenMap;
            renderImage.getGraphics().drawImage(onScreenMap,x-oX,y-oY,null);

            EPDShip.getInstance().getMainFrame().getChartPanel().getGraphics().drawImage(
                    renderImage, 0, 0, null);
        }
        
        super.mouseDragged(e);
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     *      Make Pan event for the map.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (isDragged && e.getSource() instanceof MapBean) {

            MapBean mb = (MapBean) e.getSource();
            Projection proj = mb.getProjection();
            Point2D center = proj.forward(proj.getCenter());

            Point2D pnt = mb.getNonRotatedLocation(e);
            int x = (int) pnt.getX();
            int y = (int) pnt.getY();

            
            center.setLocation(center.getX() - x + oX, center.getY() - y + oY);
            
            //this will trigger "projection changed" in SimpleOffScreenMapRenderer
            //which listens to mb
            mb.setCenter(proj.inverse(center));

            this.isDragged = false;
            EPDShip.getInstance().getMainFrame().getChartPanel().getMap().setVisible(true);
        }
        super.mouseReleased(e);
    }
}
