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
package dk.dma.epd.shore.event;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.PanMouseMode;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.proj.Projection;
import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.common.prototype.gui.views.CommonChartPanel;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.views.ChartPanel;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * Mouse mode for dragging
 */
public class DragMouseMode extends AbstractCoordMouseMode {

    private static final long serialVersionUID = 1L;

    /**
     * Mouse Mode identifier, which is "Drag".
     */
    public static final transient String MODEID = "Drag";


    public static final String OPAQUENESSPROPERTY = "opaqueness";
    public static final String LEAVESHADOWPROPERTY = "leaveShadow";
    public static final String USECURSORPPROPERTY = "useCursor";
    public static final float DEFAULT_OPAQUENESS = 0.0f;

    private boolean isPanning;
    private BufferedImage bufferedMapImage;
    private BufferedImage bufferedRenderingImage;
    private int beanBufferWidth;
    private int beanBufferHeight;
    private int oX, oY;
    private float opaqueness;
    private boolean leaveShadow;
    private boolean useCursor;
    private JPanel glassFrame;
    boolean layerMouseDrag;
    Cursor dragCursorMouseClicked;
    Cursor dragCursor;

    private BufferedImage onScreenMap;
    private CommonChartPanel chartPanel;

    private JMapFrame jMapFrame;

    /**
     * Construct a Drag Mouse Mode. Sets the ID of the mode to the modeID, the
     * consume mode to true, and the cursor to the crosshair.
     */
    public DragMouseMode() {
        this(true);
        
        isPanning = false;
        beanBufferWidth = 0;
        beanBufferHeight = 0;
    }

    /**
     * Construct a DragMouseMode. Lets you set the consume mode. If the events
     * are consumed, then a MouseEvent is sent only to the first
     * MapMouseListener that successfully processes the event. If they are not
     * consumed, then all of the listeners get a chance to act on the event.
     *
     * @param shouldConsumeEvents the mode setting.
     */
    public DragMouseMode(boolean shouldConsumeEvents) {
//        super(modeID, shouldConsumeEvents);
        super(MODEID, true);
        setUseCursor(true);
        setLeaveShadow(true);
        setOpaqueness(DEFAULT_OPAQUENESS);
        
        isPanning = false;
        beanBufferWidth = 0;
        beanBufferHeight = 0;
        // override the default cursor
//
//        //Get the default toolkit

//        Toolkit toolkit = Toolkit.getDefaultToolkit();
//
//        //Load an image for the cursor
//        Image image = toolkit.getImage("images/toolbar/drag_mouse.png");
//        dragCursor = toolkit.createCustomCursor(image, new Point(0,0), "Drag");
//
//        Image image2 = toolkit.getImage("images/toolbar/drag_on_mouse.png");
//        dragCursorMouseClicked = toolkit.createCustomCursor(image2, new Point(0,0), "Drag_on_mouse");

//        setModeCursor(dragCursor);

//        setModeCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    private void setCursors(){
        dragCursor = EPDShore.getStaticImages().getDragCursor();
        dragCursorMouseClicked = EPDShore.getStaticImages().getDragCursorMouseClicked();
    }

    /**
     * Instantiates new image buffers if needed.<br>
     * This method is synchronized to avoid creating the images multiple times
     * if width and height doesn't change.
     *
     * @param w mapBean's width.
     * @param h mapBean's height.
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
     * @param w Non-zero mapBean's width.
     * @param h Non-zero mapBean's height.
     */
    protected void createBuffersImpl(int w, int h) {
        // Release system resources used by previous images...
        if (bufferedMapImage != null) {
            bufferedMapImage.flush();
        }
        if (bufferedRenderingImage != null) {
            bufferedRenderingImage.flush();
        }
        // New images...
        bufferedMapImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        bufferedRenderingImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    /**
     * Find and init bean function used in initializing other classes
     */
    public void findAndInit(Object someObj) {
        if (someObj instanceof JMapFrame) {
            setCursors();
            glassFrame = ((JMapFrame) someObj).getGlassPanel();
            glassFrame.setCursor(dragCursor);
        }

        super.findAndInit(someObj);
    }

    /**
     * Return opaqueness
     * @return
     */
    public float getOpaqueness() {
        return opaqueness;
    }

    /**
     * Get oX
     * @return
     */
    public int getOX() {
        return oX;
    }

    /**
     * Get oY
     * @return
     */
    public int getOY() {
        return oY;
    }

    /**
     * Get properties
     */
    public Properties getProperties(Properties props) {
        props = super.getProperties(props);
        String prefix = PropUtils.getScopedPropertyPrefix(this);
        props.put(prefix + OPAQUENESSPROPERTY, Float.toString(getOpaqueness()));
        props.put(prefix + LEAVESHADOWPROPERTY,
                Boolean.toString(isLeaveShadow()));
        props.put(prefix + USECURSORPPROPERTY, Boolean.toString(isUseCursor()));
        return props;
    }

    /**
     * Get property info
     */
    public Properties getPropertyInfo(Properties props) {
        props = super.getPropertyInfo(props);

        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                OPAQUENESSPROPERTY,
                "Transparency",
                "Transparency level for moving map, between 0 (clear) and 1 (opaque).",
                null);
        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                LEAVESHADOWPROPERTY,
                "Leave Shadow",
                "Display current map in background while panning.",
                "com.bbn.openmap.util.propertyEditor.YesNoPropertyEditor");

        PropUtils.setI18NPropertyInfo(i18n,
                props,
                PanMouseMode.class,
                USECURSORPPROPERTY,
                "Use Cursor",
                "Use hand cursor for mouse mode.",
                "com.bbn.openmap.util.propertyEditor.YesNoPropertyEditor");

        return props;
    }

    /**
     * Return if it should leave a shadow
     * @return
     */
    public boolean isLeaveShadow() {
        return leaveShadow;
    }

    /**
     * Return if it is in panning mode
     * @return
     */
    public boolean isPanning() {
        return isPanning;
    }

    /**
     * @return Returns the useCursor.
     */
    public boolean isUseCursor() {
        return useCursor;
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
            super.mouseDragged(arg0);

            // if(!mouseDragged) {
            layerMouseDrag = mouseSupport.fireMapMouseDragged(arg0);
            // }
            if (!layerMouseDrag) {

                MapBean mb = (MapBean) arg0.getSource();

                if (chartPanel == null) {
                    chartPanel = findChartPanel(mb);
                }
                
                if (jMapFrame == null) {
                    jMapFrame = findJMapFrame(mb);
                }
                Point2D pnt = chartPanel.getMap().getNonRotatedLocation(arg0);
                int x = (int) pnt.getX();
                int y = (int) pnt.getY();
                
                if (!isPanning) {

                    isPanning = true;
                    

                    //chartPanel.getDragMapRenderer().updateOutImg();

                    onScreenMap = new BufferedImage(chartPanel.getWidth(),chartPanel.getHeight(),BufferedImage.TYPE_INT_RGB);
                    mb.paint(onScreenMap.getGraphics());
                    oX = x;
                    oY = y;
                    
                    for(Component c :chartPanel.getComponents()) {
                        c.setVisible(false);
                    }
                    
                    chartPanel.getMap().setVisible(true);
                    
                    

                } else {
                    
                    final int startX = chartPanel.getWidth();
                    final int startY = chartPanel.getHeight();

                    final int posX = startX - (x - oX);
                    final int posY = startY - (y - oY);

                    try {
                        
                        //LOG.debug("Time to get offScreenMap: ")
                        final BufferedImage offScreenMap = chartPanel.getDragMapRenderer().getOutImg().getSubimage(posX,
                                posY, chartPanel.getWidth(), chartPanel.getHeight());
                        
                        final BufferedImage renderImage = offScreenMap;
                        //renderImage.getGraphics().drawImage(offScreenMap,0,0,null);                        
                        renderImage.getGraphics().drawImage(onScreenMap,x-oX,y-oY,null);

                        chartPanel.getGraphics().drawImage(
                                renderImage, 0, 0, null);

                    } catch (RasterFormatException e) {
                        //was out of bounds, sorry
                        //throw e;

                    }
                }
            }
        }
    }
    
    public CommonChartPanel findChartPanel(Container parent) {
        for (int i=0; i<10; i++) {
            System.out.println("parent = "+parent.getClass());
            if (parent instanceof CommonChartPanel){
                chartPanel = (CommonChartPanel)parent;
                
                return (CommonChartPanel) parent;
            } else {
                parent = parent.getParent();
            }
            
        }
        
        return null;
    }
    
    public JMapFrame findJMapFrame(Container parent) {
        for (int i=0; i<10; i++) {
            if (parent instanceof JMapFrame){
                jMapFrame = (JMapFrame)parent;
                return (JMapFrame) parent;
            } else {
                parent = parent.getParent();
            }
            
        }
        
        return null;
    }    


    /**
     * Event on mouse pressed
     */
    public void mousePressed(MouseEvent arg0){
        glassFrame.setCursor(dragCursorMouseClicked);
//        chartPanel.getMap().setCursor(dragCursorMouseClicked);
        }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     *      Make Pan event for the map.
     */
    public void mouseReleased(MouseEvent arg0) {
        if (isPanning && arg0.getSource() instanceof MapBean) {

            MapBean mb = (MapBean) arg0.getSource();
            Projection proj = mb.getProjection();
            Point2D center = proj.forward(proj.getCenter());

            Point2D pnt = mb.getNonRotatedLocation(arg0);
            int x = (int) pnt.getX();
            int y = (int) pnt.getY();

            
            center.setLocation(center.getX() - x + oX, center.getY() - y + oY);

            //this will trigger "projection changed" in SimpleOffScreenMapRenderer
            mb.setCenter(proj.inverse(center));

            isPanning = false;

            for(Component c :chartPanel.getComponents()) {
                c.setVisible(false);
            }
        }
        super.mouseReleased(arg0);
        glassFrame.setCursor(dragCursor);
    }

    /**
     * Projection (the map) has changed
     * @param e
     */
    public void projectionChanged(ProjectionEvent e) {
        Object obj = e.getSource();
        if (obj instanceof MapBean) {
            MapBean mb = (MapBean) obj;
            int w = mb.getWidth();
            int h = mb.getHeight();
            createBuffers(w, h);
        }
    }

    /**
     * Set as active mouse mode
     */
    public void setActive(boolean val) {
        if (!val) {
            if (bufferedMapImage != null) {
                bufferedMapImage.flush();
            }
            if (bufferedRenderingImage != null) {
                bufferedRenderingImage.flush();
            }
            beanBufferWidth = 0;
            beanBufferHeight = 0;
            bufferedMapImage = null;
            bufferedRenderingImage = null;
        }
    }

    /**
     * Set leave shadow
     * @param leaveShadow
     */
    public void setLeaveShadow(boolean leaveShadow) {
        this.leaveShadow = leaveShadow;
    }

    /**
     * Set opaqueness
     * @param opaqueness
     */
    public void setOpaqueness(float opaqueness) {
        this.opaqueness = opaqueness;
    }

    /**
     * Set properties
     */
    public void setProperties(String prefix, Properties props) {
        super.setProperties(prefix, props);
        prefix = PropUtils.getScopedPropertyPrefix(prefix);

        opaqueness = PropUtils.floatFromProperties(props, prefix
                + OPAQUENESSPROPERTY, opaqueness);
        leaveShadow = PropUtils.booleanFromProperties(props, prefix
                + LEAVESHADOWPROPERTY, leaveShadow);

        setUseCursor(PropUtils.booleanFromProperties(props, prefix
                + USECURSORPPROPERTY, isUseCursor()));

    }
    


    /**
     * @param useCursor The useCursor to set.
     */
    public void setUseCursor(boolean useCursor) {
        this.useCursor = useCursor;
        if (useCursor) {
            /*
             * For who like make his CustomCursor
             */
            try {
//                Toolkit tk = Toolkit.getDefaultToolkit();
//                ImageIcon pointer = new ImageIcon(getClass().getResource("pan.gif"));
//                Dimension bestSize = tk.getBestCursorSize(pointer.getIconWidth(),
//                        pointer.getIconHeight());
//                Image pointerImage = ImageScaler.getOptimalScalingImage(pointer.getImage(),
//                        (int) bestSize.getWidth(),
//                        (int) bestSize.getHeight());
//                Cursor cursor = tk.createCustomCursor(pointerImage,
//                        new Point(0, 0),
//                        "PP");
                glassFrame.setCursor(dragCursor);

                return;
            } catch (Exception e) {
                // Problem finding image probably, just move on.
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        glassFrame.setCursor(dragCursor);
        super.mouseEntered(e);
    }
}
