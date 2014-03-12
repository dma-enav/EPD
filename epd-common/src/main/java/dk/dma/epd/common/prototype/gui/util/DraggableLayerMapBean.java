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
package dk.dma.epd.common.prototype.gui.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import com.bbn.openmap.BufferedLayerMapBean;

/**
 * This sub-class of the {@linkplain BufferedLayerMapBean} will
 * turn off repainting when in dragging mode.
 * <p>
 * In dragging mode, it will instead paint a buffered snapshot of the map.
 * <p>
 * In EPDShore, painting the map whilst dragging will paint on top of e.g. the tool-bar,
 * the status area and the layer toggling panel.<br/>
 * Calling {@linkplain #addClipComponents()} with these components, will set up a 
 * clipping region to avoid painting on top of them.
 */
public class DraggableLayerMapBean extends BufferedLayerMapBean {

    private static final long serialVersionUID = 1L;
    
    boolean dragging;
    Point2D startPnt;
    Point2D lastPnt;
    Area clipComponentArea;
    List<Component> clipComponents = new ArrayList<>();
    BufferedImage buffer;
    
    /**
     * Constructor
     */
    public DraggableLayerMapBean() {
        super();
    }
    
    /**
     * Adds components whose bounds should be clipped when painting during dragging
     * @param clipComponents the components to add to the repaint clip region
     */
    public void addClipComponents(Component... clipComponents) {
        for (Component clipComponent : clipComponents) {
            this.clipComponents.add(clipComponent);
        }
    }
    
    /**
     * Returns if the map bean is in dragging mode
     * @return if the map bean is in dragging mode
     */
    public synchronized boolean isDragging() {
        return dragging;
    }

    /**
     * Flags that the map bean enters dragging mode
     */
    public synchronized void startDragging(MouseEvent e) {
        if (!dragging) {
            dragging = true;
            startPnt = lastPnt = getNonRotatedLocation(e);
            
            // Compute the clipping area
            clipComponentArea = new Area();
            for (Component clipComponent : clipComponents) {
                if (clipComponent.isVisible()) {
                    Rectangle bounds = clipComponent.getBounds();
                    Point offset = SwingUtilities.convertPoint(clipComponent, 0, 0, this);
                    Point location = bounds.getLocation();
                    location.move(offset.x, offset.y);
                    bounds.setLocation(location);
                    if (bounds.intersects(getBounds())) {
                        clipComponentArea.add(new Area(bounds));
                    }
                }
            }
            
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
            super.paint(buffer.getGraphics());
        }
    }

    /**
     * Flags that the map bean exits dragging mode
     */
    public synchronized void stopDragging(MouseEvent e) {
        if (dragging) {
            dragging = false;
            buffer.flush();
            buffer = null;
            updateProjection();
        }
    }

    /**
     * Called whenever the layer is being dragged.
     * Prior to calling this method, the {@linkplain #startDragging()}
     * method should have been called.
     */
    public synchronized void drag(MouseEvent e) {
        if (dragging) {
            lastPnt = getNonRotatedLocation(e);
            paint(getGraphics());
        }
    }
    
    /**
     * Repaint method.
     * <p>
     * Turns off repainting when the {@code dragging} flag is set.
     */
    @Override
    public void repaint() {
        if (!dragging) {
            super.repaint();
        }
    }
    
    /**
     * Override the actual paint method
     * @param g the graphical context
     */
    @Override
    public void paint(Graphics g) {
        if (!dragging) {
            super.paint(g);
        } else if (buffer != null) {
            Graphics2D g2 = (Graphics2D) g;
            int x0 = (int)(lastPnt.getX() - startPnt.getX());
            int y0 = (int)(lastPnt.getY() - startPnt.getY());
            
            Shape saveClip = g2.getClip();
            
            // Do no paint on top of the clipComponentArea or the drawing buffer (to avoid flickering) 
            Rectangle2D bufferRect = new Rectangle2D.Double(x0, y0, buffer.getWidth(), buffer.getHeight());
            Area outside = new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            outside.subtract(new Area(bufferRect));
            outside.subtract(clipComponentArea);
            g2.setClip(outside);
                            
            // Paint background
            g2.setPaint(Color.BLACK);
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setPaint(Color.DARK_GRAY);
            for (int y = y0 % 100; y < getHeight(); y += 100) {
                for (int x = x0 % 100; x < getWidth(); x += 100) {
                    g2.drawLine(x, 0, x, getHeight());
                }
                g2.drawLine(0, y, getWidth(), y);
            }
            
            // Paint buffered map (except on top of the clipComponentArea)
            outside = new Area(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));
            outside.subtract(clipComponentArea);
            g2.setClip(outside);
            g.drawImage(buffer, x0, y0, null);
            
            g2.setClip(saveClip);
        }
    }

    /**
     * Updates the projection of the map bean
     */
    private void updateProjection() {
        if (!startPnt.equals(lastPnt)) {
            
            Point2D center = getProjection().forward(getProjection().getCenter());
            center.setLocation(center.getX() - lastPnt.getX() + startPnt.getX(), center.getY() - lastPnt.getY() + startPnt.getY());
            setCenter(projection.inverse(center));
            startPnt = lastPnt;
        }
    }
}
