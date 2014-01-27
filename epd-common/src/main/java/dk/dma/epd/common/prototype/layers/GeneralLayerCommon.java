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
package dk.dma.epd.common.prototype.layers;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapEventUtils;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.MainFrameCommon;
import dk.dma.epd.common.prototype.gui.MapFrameCommon;
import dk.dma.epd.common.prototype.gui.MapMenuCommon;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.gui.util.InfoPanel.InfoPanelBinding;

/**
 * Common EPD layer subclass that may be sub-classed by other layers.
 */
public abstract class GeneralLayerCommon extends OMGraphicHandlerLayer implements MapMouseListener {

    private static final long serialVersionUID = 1L;

    protected InfoPanelBinding infoPanels = new InfoPanelBinding();
    
    protected OMGraphicList graphics = new OMGraphicList();
    protected MapBean mapBean;
    protected MainFrameCommon mainFrame;
    protected MapMenuCommon mapMenu;
    protected MapFrameCommon mapFrame;

    protected OMGraphic closest;
    
    /** 
     * {@inheritDoc}
     */
    @Override
    public synchronized OMGraphicList prepare() {
        if (getProjection() == null) {
            return graphics;
        }
        graphics.project(getProjection(), true);
        return graphics;
    }
    
    /**
     * Called when a bean is added to the bean context
     * @param obj the bean being added
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        } else if (obj instanceof MainFrameCommon) {
            mainFrame = (MainFrameCommon) obj;
            if (mainFrame.getGlassPanel() != null) {
                // EPDShip case
                addInfoPanelsToGlassPane();
            }
        } else if (obj instanceof MapMenuCommon) {
            mapMenu = (MapMenuCommon) obj;
        } else if (obj instanceof MapFrameCommon) {
            mapFrame = (MapFrameCommon) obj;
            if (mapFrame.getGlassPanel() != null) {
                // EPDShore case
                addInfoPanelsToGlassPane();
            }
        }
    }

    /**
     * Called when a bean is removed from the bean context
     * @param obj the bean being removed
     */
    @Override
    public void findAndUndo(Object obj) {
        // Important notice:
        // The mechanism for adding and removing beans has been used in 
        // a wrong way in epd-shore, which has multiple ChartPanels.
        // When the "global" beans are added to a new ChartPanel, they
        // will be removed from the other ChartPanels using findAndUndo.
        // Hence, we do not reset the references to mapBean and mainFrame
        
        super.findAndUndo(obj);
    }

    /**
     * Returns {@code this} as the {@linkplain MapMouseListener}
     * @return this
     */
    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    /**
     * Returns the mouse mode service list
     * @return the mouse mode service list
     */
    @Override
    public String[] getMouseModeServiceList() {
        return  EPD.getInstance().getDefaultMouseModeServiceList();
    }

    
    /**
     * Provides default behavior for right-clicks by
     * showing the general menu.
     * 
     * @param evt the mouse event
     */
    @Override
    public boolean mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON3) {
            mapMenu.generalMenu(true);
            mapMenu.setVisible(true);

            if (mainFrame.getHeight() < evt.getYOnScreen() + mapMenu.getHeight()) {
                mapMenu.show(this, evt.getX() - 2, evt.getY() - mapMenu.getHeight());
            } else {
                mapMenu.show(this, evt.getX() - 2, evt.getY() - 2);
            }
            return true;
        }

        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseDragged(MouseEvent arg0) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseMoved() {
    }

    /**
     * Default implementation of the mouseMoved method that opens {@linkplain InfoPanel}
     * based on the graphics closest to the mouse location.
     * <p>
     * In order to use this mechanism, first register the info panels using the
     * {@code registerInfoPanel()} method.
     * 
     * @param evt the mouse event
     * @return if the event was handled
     */
    @Override
    public boolean mouseMoved(MouseEvent evt) {
        
        if (!isVisible() || mapMenu.isVisible()) {
            return false;
        }
        
        if (!infoPanels.isEmpty()) {
            OMGraphic newClosest = getSelectedGraphic(evt, infoPanels.getGraphicsList());
            
            if (newClosest != null && newClosest.isVisible() && newClosest != closest) {
                closest = newClosest;
                Point containerPoint = SwingUtilities.convertPoint(mapBean, evt.getPoint(), getMapContainer());
                
                InfoPanel infoPanel = infoPanels.getInfoPanel(newClosest.getClass());
                infoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);
                // Allow custom initialization by sub-classes
                initInfoPanel(infoPanel, newClosest, evt, containerPoint);
                infoPanel.setVisible(true);
                getGlassPanel().setVisible(true);
                return true;
            } else if (newClosest == null) {
                closest = null;
                for (InfoPanel infoPanel : infoPanels.getInfoPanels()) {
                    infoPanel.setVisible(false);
                }
            }
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mousePressed(MouseEvent arg0) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean mouseReleased(MouseEvent arg0) {
        return false;
    }

    /**
     * Returns the mouse selection tolerance
     * @return the mouse selection tolerance
     */
    public float getMouseSelectTolerance() {
        return EPD.getInstance().getSettings().getGuiSettings().getMouseSelectTolerance();
    }
    
    /**
     * Returns the first graphics element placed at the mouse event location
     * that matches any of the types passed along. 
     * 
     * @param evt the mouse event
     * @param types the possible types
     * @return the first matching graphics element
     */
    public final OMGraphic getSelectedGraphic(MouseEvent evt, Class<?>... types) {
        return MapEventUtils.getSelectedGraphic(graphics, evt, getMouseSelectTolerance(), types);
    }
    
    /**
     * Returns a reference to the main frame
     * @return a reference to the main frame
     */
    public MainFrameCommon getMainFrame() {
        return mainFrame;
    }   

    /**
     * Returns a reference to the map menu
     * @return a reference to the map menu
     */
    public MapMenuCommon getMapMenu() {
        return mapMenu;
    }   

    /**
     * Returns a reference to the map frame
     * @return a reference to the map frame
     */
    public MapFrameCommon getMapFrame() {
        return mapFrame;
    }

    /**
     * Returns a reference to the glass pane of the map container,
     * i.e. of the main frame (EPDShip) or of the map frame (EPDShore)
     * 
     * @return a reference to the glass pane
     */
    public JPanel getGlassPanel() {
        return (mapFrame != null) ? mapFrame.getGlassPanel() : mainFrame.getGlassPanel();
    }    
    
    /**
     * Returns a reference to the map container, 
     * i.e. the main frame (EPDShip) or map frame (EPDShore)
     * 
     * @return a reference to the glass pane
     */
    public Component getMapContainer() {
        return (mapFrame != null) ? mapFrame : mainFrame;
    }

    /**
     * Registers the {@linkplain InfoPanel} binding.
     * <p>
     * These panels will automatically be added to the glass pane and
     * will automatically be displayed in the {@code mouseMoved} method.
     * 
     * @param infoPanels the {@linkplain InfoPanel} panels to register
     * @param graphics the list of {@linkplain OMGraphic} elements that triggers the info panel
     */
    @SafeVarargs
    protected final void registerInfoPanel(InfoPanel infoPanel, Class<? extends OMGraphic>... graphics) {
        infoPanels.addBinding(infoPanel, graphics);
    }
    
    /**
     * For sub-classes using the info-panel registration, override this method to initialize the
     * info panel before it is displayed.
     * <p>
     * The default implementation of {@code mouseMoved()} will find the info panel to display
     * and call this method for custom initialization.
     * 
     * @param infoPanel the info panel about to be displayed
     * @param newClosest the mouse-over graphics that triggered the info panel
     * @param evt the mouse event
     * @param containerPoint the current container point
     */
    protected void initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
    }

    /**
     * Called when a glass pane has been resolved.
     * Adds all info panels to the glass pane
     */
    private void addInfoPanelsToGlassPane() {
        for (InfoPanel infoPanel : infoPanels.getInfoPanels()) {
            getGlassPanel().add(infoPanel);
        }
    }    
}
