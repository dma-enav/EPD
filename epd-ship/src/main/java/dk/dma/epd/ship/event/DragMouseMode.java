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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Properties;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.PanMouseMode;
import com.bbn.openmap.image.ImageScaler;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.ship.EPDShip;

/**
 * Mouse mode for route edit
 */
public class DragMouseMode extends AbstractCoordMouseMode {

    private static final long serialVersionUID = 1L;

    private final Logger LOG = LoggerFactory.getLogger(DragMouseMode.class);

    /**
     * Mouse Mode identifier, which is "RouteEdit".
     */
    public static final transient String MODE_ID = "DragMouse";
    public static final String OPAQUENESS_PROPERTY = "opaqueness";
    public static final String LEAVE_SHADOS_PROPERTY = "leaveShadow";
    public static final String USE_CURSOR_PROPERTY = "useCursor";
    public static final float DEFAULT_OPAQUENESS = 0.5f;

    private boolean isPanning;
    private BufferedImage bufferedMapImage;
    private int beanBufferWidth;
    private int beanBufferHeight;
    private int oX, oY;
    private float opaqueness;
    private boolean leaveShadow;
    private boolean useCursor;
    boolean layerMouseDrag;
    boolean mouseDragged;

    /**
     * Construct a RouteEditMouseMode. Sets the ID of the mode to the modeID,
     * the consume mode to true, and the cursor to the crosshair.
     */
    public DragMouseMode() {
        this(true);
    }

    /**
     * Construct a NavMouseMode. Lets you set the consume mode. If the events
     * are consumed, then a MouseEvent is sent only to the first
     * MapMouseListener that successfully processes the event. If they are not
     * consumed, then all of the listeners get a chance to act on the event.
     * 
     * @param shouldConsumeEvents
     *            the mode setting.
     */
    public DragMouseMode(boolean shouldConsumeEvents) {
        // super(modeID, shouldConsumeEvents);
        super(MODE_ID, true);
        setUseCursor(true);
        setLeaveShadow(true);
        setOpaqueness(DEFAULT_OPAQUENESS);
        // override the default cursor
        setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void setActive(boolean val) {
        if (!val) {
            if (bufferedMapImage != null) {
                bufferedMapImage.flush();
            }
            beanBufferWidth = 0;
            beanBufferHeight = 0;

        }
    }

    /**
     * @return Returns the useCursor.
     */
    public boolean isUseCursor() {
        return useCursor;
    }

    /**
     * @param useCursor
     *            The useCursor to set.
     */
    public void setUseCursor(boolean useCursor) {
        this.useCursor = useCursor;
        if (useCursor) {
            /*
             * For who like make his CustomCursor
             */
            try {
                Toolkit tk = Toolkit.getDefaultToolkit();
                ImageIcon pointer = new ImageIcon(getClass().getResource(
                        "pan.gif"));
                Dimension bestSize = tk.getBestCursorSize(
                        pointer.getIconWidth(), pointer.getIconHeight());
                Image pointerImage = ImageScaler.getOptimalScalingImage(
                        pointer.getImage(), (int) bestSize.getWidth(),
                        (int) bestSize.getHeight());
                Cursor cursor = tk.createCustomCursor(pointerImage, new Point(
                        0, 0), "PP");
                setModeCursor(cursor);
                return;
            } catch (Exception e) {
                // Problem finding image probably, just move on.
            }
        }

        setModeCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    @Override
    public void setProperties(String prefix, Properties props) {
        super.setProperties(prefix, props);
        prefix = PropUtils.getScopedPropertyPrefix(prefix);

        opaqueness = PropUtils.floatFromProperties(props, prefix
                + OPAQUENESS_PROPERTY, opaqueness);
        leaveShadow = PropUtils.booleanFromProperties(props, prefix
                + LEAVE_SHADOS_PROPERTY, leaveShadow);

        setUseCursor(PropUtils.booleanFromProperties(props, prefix
                + USE_CURSOR_PROPERTY, isUseCursor()));

    }

    @Override
    public Properties getProperties(Properties props) {
        props = super.getProperties(props);
        String prefix = PropUtils.getScopedPropertyPrefix(this);
        props.put(prefix + OPAQUENESS_PROPERTY, Float.toString(getOpaqueness()));
        props.put(prefix + LEAVE_SHADOS_PROPERTY,
                Boolean.toString(isLeaveShadow()));
        props.put(prefix + USE_CURSOR_PROPERTY, Boolean.toString(isUseCursor()));
        return props;
    }

    @Override
    public Properties getPropertyInfo(Properties props) {
        props = super.getPropertyInfo(props);

        PropUtils
                .setI18NPropertyInfo(
                        i18n,
                        props,
                        PanMouseMode.class,
                        OPAQUENESS_PROPERTY,
                        "Transparency",
                        "Transparency level for moving map, between 0 (clear) and 1 (opaque).",
                        null);
        PropUtils.setI18NPropertyInfo(i18n, props, PanMouseMode.class,
                LEAVE_SHADOS_PROPERTY, "Leave Shadow",
                "Display current map in background while panning.",
                "com.bbn.openmap.util.propertyEditor.YesNoPropertyEditor");

        PropUtils.setI18NPropertyInfo(i18n, props, PanMouseMode.class,
                USE_CURSOR_PROPERTY, "Use Cursor",
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
    public void mouseDragged(MouseEvent arg0) {
        if (arg0.getSource() instanceof MapBean) {
//            System.out.println(arg0);
//            super.mouseDragged(arg0);

             if(!mouseDragged) {
            layerMouseDrag = mouseSupport.fireMapMouseDragged(arg0);
             }
            
//            System.out.println(layerMouseDrag);
             
             
            if (!layerMouseDrag) {
                
                mouseDragged = true;

                MapBean mb = (MapBean) arg0.getSource();
                Point2D pnt = mb.getNonRotatedLocation(arg0);
                int x = (int) pnt.getX();
                int y = (int) pnt.getY();

                if (!isPanning) {

                    /*
                     * Making the image
                     */
                    try {
                        long time = System.currentTimeMillis();
                        bufferedMapImage = EPDShip.getMainFrame()
                                .getChartPanel().getDragMapRenderer().call();
                        long end = System.currentTimeMillis();

                        System.out.println("Time to get screenshot: "
                                + (end - time));

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    isPanning = true;
//                    EPDShip.getMainFrame().getChartPanel().getMap()
//                            .setVisible(false);

                    oX = x;
                    oY = y;

                } else {
                    if (bufferedMapImage != null) {
                        final int startX = mb.getWidth();
                        final int startY = mb.getHeight();

                        final int posX = startX - (x - oX);
                        final int posY = startY - (y - oY);

                        // LOG.debug("posX ="+posX+" posY="+posY);
                        if (posX > 0
                                && (posX + startX) < (bufferedMapImage
                                        .getWidth())
                                && posY > 0
                                && (posY + startY) < bufferedMapImage
                                        .getHeight()) {
                            final BufferedImage renderImage;

                            renderImage = bufferedMapImage.getSubimage(posX,
                                    posY, mb.getWidth(), mb.getHeight());

                            ((Graphics2D) mb.getGraphics(true)).drawImage(
                                    renderImage, 0, 0, null);

                        }
                    }
                }
            }
        }
        // }
        // super.mouseDragged(arg0);
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     *      Make Pan event for the map.
     */
    @Override
    public void mouseReleased(MouseEvent arg0) {
        if (isPanning && arg0.getSource() instanceof MapBean) {

            MapBean mb = (MapBean) arg0.getSource();
            Projection proj = mb.getProjection();
            Point2D center = proj.forward(proj.getCenter());

            Point2D pnt = mb.getNonRotatedLocation(arg0);
            int x = (int) pnt.getX();
            int y = (int) pnt.getY();

            EPDShip.getMainFrame().getChartPanel().getDragMapRenderer()
                    .updateTargetMap();
            MapBean offScreenBean = EPDShip.getMainFrame().getChartPanel()
                    .getDragMapRenderer().getTargetBean();

            center.setLocation(center.getX() - x + oX, center.getY() - y + oY);

            // finally set both centers
            offScreenBean.setCenter(proj.inverse(center));
            offScreenBean.getParent().repaint();
            mb.setCenter(proj.inverse(center));

            isPanning = false;
//<<<<<<< .merge_file_a01328
//            EPDShip.getMainFrame().getChartPanel().getMap().setVisible(true);
//=======
            // bufferedMapImage = null; //clean up when not active...
            mouseDragged = false;
//>>>>>>> .merge_file_a20664
        }
        super.mouseReleased(arg0);
    }

    public boolean isLeaveShadow() {
        return leaveShadow;
    }

    public void setLeaveShadow(boolean leaveShadow) {
        this.leaveShadow = leaveShadow;
    }

    public float getOpaqueness() {
        return opaqueness;
    }

    public void setOpaqueness(float opaqueness) {
        this.opaqueness = opaqueness;
    }

    public boolean isPanning() {
        return isPanning;
    }

    public int getOX() {
        return oX;
    }

    public int getOY() {
        return oY;
    }

    /**
     * Instantiates new image buffers if needed.<br>
     * This method is synchronized to avoid creating the images multiple times
     * if width and height doesn't change.
     * 
     * @param w
     *            mapBean's width.
     * @param h
     *            mapBean's height.
     */
    public synchronized void createBuffers(int w, int h) {
        if (w > 0 && h > 0 && (w != beanBufferWidth || h != beanBufferHeight)) {
            beanBufferWidth = w;
            beanBufferHeight = h;
            createBuffersImpl(w, h);
        }
    }

    /**
     * Instantiates new image buffers.
     * 
     * @param w
     *            Non-zero mapBean's width.
     * @param h
     *            Non-zero mapBean's height.
     */
    protected void createBuffersImpl(int w, int h) {
        // Release system resources used by previous images...
        if (bufferedMapImage != null) {
            bufferedMapImage.flush();
        }
        // New images...
        bufferedMapImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }
}
