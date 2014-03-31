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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapEventUtils;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.IMapFrame;
import dk.dma.epd.common.prototype.gui.MainFrameCommon;
import dk.dma.epd.common.prototype.gui.MapMenuCommon;
import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.gui.util.InfoPanel.InfoPanelBinding;
import dk.dma.epd.common.prototype.layers.util.LayerVisiblityListener;

/**
 * Common EPD layer subclass that may be sub-classed by other layers.
 * <p>
 * The {@code EPDLayerCommon} class automatically binds the {@code mapBean}, {@code mainFrame} and {@code mapMenu} beans.
 * <p>
 * Additionally, it binds the bean ({@code mapFrame}) that implements the {@linkplain IMapFrame} interface.<br>
 * In EPDShip this bean will be the {@code MainFrame} class bean, and in EPDShore it will be the {@code JMapFrame} class bean.
 * <p>
 * Utility methods such as {@linkplain #convertPoint(Point)} and {@linkplain #getGlassPanel()} thus works on the {@code mapFrame}
 * bean.
 * <p>
 * {@code EPDLayerCommon} also provides standardized ways of binding {@code InfoPanel} panels to {@linkplain OMGraphic} classes, as
 * well as container managed handling of left- and right-button mouse clicks, for mouse selection and displaying the context menu
 * respectively.
 */
public abstract class EPDLayerCommon extends OMGraphicHandlerLayer implements MapMouseListener {

    private static final long serialVersionUID = 1L;
    
    /** Whether or not to hide the glass panel when info panels are hidden **/
    private static final boolean HIDE_GLASS_PANEL = false;

    protected OMGraphicList graphics = new AntialiasedGraphicList();

    protected InfoPanelBinding infoPanels = new InfoPanelBinding();
    protected List<Class<?>> mouseClickClasses = new CopyOnWriteArrayList<>();
    protected List<Class<?>> mapMenuClasses = new CopyOnWriteArrayList<>();
    protected OMGraphicList infoPanelsGraphics = graphics;
    protected OMGraphicList mouseClickGraphics = graphics;
    protected OMGraphicList mapMenuGraphics = graphics;

    protected MapBean mapBean;
    protected MainFrameCommon mainFrame;
    protected MapMenuCommon mapMenu;
    protected IMapFrame mapFrame;

    protected OMGraphic closest;

    private Timer timer;
    private CopyOnWriteArrayList<LayerVisiblityListener> visibilityListener = new CopyOnWriteArrayList<>();

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

    /***************************************/
    /** Life-cycle functionality **/
    /***************************************/

    /**
     * Called when a bean is added to the bean context
     * 
     * @param obj
     *            the bean being added
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);

        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        } else if (obj instanceof MainFrameCommon) {
            mainFrame = (MainFrameCommon) obj;
        } else if (obj instanceof MapMenuCommon) {
            mapMenu = (MapMenuCommon) obj;
        }

        // For EPDShip the IMapFrame is the MainFrame
        // For EPDShore the IMapFrame is the JMapFrame
        if (obj instanceof IMapFrame) {
            mapFrame = (IMapFrame) obj;
            addInfoPanelsToGlassPane();
        }
    }

    /**
     * Called when a bean is removed from the bean context
     * 
     * @param obj
     *            the bean being removed
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
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        synchronized (graphics) {
            graphics.clear();
        }
        synchronized (this) {
            if (timer != null) {
                timer.stop();
            }
        }
        super.dispose();
    }

    /***************************************/
    /** Timer functionality **/
    /***************************************/

    /**
     * Starts the timer with the given initial delay and subsequent delays.
     * <p>
     * When the timer is triggered, it will call the {@linkplain #timerAction()} method, which sub-classes should override.
     */
    protected synchronized void startTimer(int initialDelay, int delay) {
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timerAction();
            }
        });
        timer.setInitialDelay(initialDelay);
        timer.setRepeats(true);
        timer.setCoalesce(true);
        timer.start();
    }

    /**
     * Re-starts the timer with the initial delay and subsequent delays that was specified upon first starting the timer using the
     * {@linkplain #startTimer(int, int)} method.
     * <p>
     * When the timer is triggered, it will call the {@linkplain #timerAction()} method, which sub-classes should override.
     */
    protected synchronized void restartTimer() {
        if (timer == null) {
            throw new IllegalStateException("Timer must be started before being restarted");
        }
        timer.restart();
    }

    /**
     * Stops the timer
     */
    protected synchronized void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * Sub-classes using the timer functionality should override this method, which gets called by the timer.
     */
    protected void timerAction() {
    }

    /***************************************/
    /** Mouse handling functionality **/
    /***************************************/

    /**
     * Returns {@code this} as the {@linkplain MapMouseListener}
     * 
     * @return this
     */
    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    /**
     * Returns the mouse mode service list
     * 
     * @return the mouse mode service list
     */
    @Override
    public String[] getMouseModeServiceList() {
        return EPD.getInstance().getDefaultMouseModeServiceList();
    }

    /**
     * Provides default behavior for mouse clicks.
     * <p>
     * If graphics classes have been registered using {@linkplain #registerMouseClickClasses()} for left-clicks and
     * {@linkplain #registerMapMenuClasses()} for right-clicks, it is checked whether one of these classes have been clicked.
     * 
     * @param evt
     *            the mouse event
     */
    @Override
    public boolean mouseClicked(MouseEvent evt) {
        if (evt.getButton() == MouseEvent.BUTTON1 && !mouseClickClasses.isEmpty()) {

            OMGraphic clickedGraphics = getSelectedGraphic(mouseClickGraphics, evt,
                    mouseClickClasses.toArray(new Class<?>[mouseClickClasses.size()]));
            if (clickedGraphics != null) {
                // Clean up any info panels
                hideInfoPanels();
                if (HIDE_GLASS_PANEL) {
                    getGlassPanel().setVisible(false);
                }
                
                // Allow custom handling of right-clicks by sub-classes
                handleMouseClick(clickedGraphics, evt);
                return true;

            } else {
                handleMouseClick(null, evt);
            }

        } else if (evt.getButton() == MouseEvent.BUTTON3 && !mapMenuClasses.isEmpty()) {
            OMGraphic clickedGraphics = getSelectedGraphic(mapMenuGraphics, evt,
                    mapMenuClasses.toArray(new Class<?>[mapMenuClasses.size()]));
            if (clickedGraphics != null) {
                // Clean up any info panels
                hideInfoPanels();
                if (HIDE_GLASS_PANEL) {
                    getGlassPanel().setVisible(false);
                }

                // Allow custom map menu initialization by sub-classes
                initMapMenu(clickedGraphics, evt);

                // Display the menu
                int yOffset = 2;
                if (mainFrame.getHeight() < evt.getYOnScreen() + mapMenu.getHeight()) {
                    yOffset = mapMenu.getHeight();
                }
                mapMenu.show(this, evt.getX() - 2, evt.getY() - yOffset);
                return true;
            }
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
     * Default implementation of the mouseMoved method that opens {@linkplain InfoPanel} based on the graphics closest to the mouse
     * location.
     * <p>
     * In order to use this mechanism, first register the info panels using the {@code registerInfoPanel()} method.
     * 
     * @param evt
     *            the mouse event
     * @return if the event was handled
     */
    @Override
    public boolean mouseMoved(MouseEvent evt) {

        if (!isVisible() || mapMenu == null || mapMenu.isVisible()) {
            return false;
        }

        if (!infoPanels.isEmpty()) {
            OMGraphic newClosest = getSelectedGraphic(infoPanelsGraphics, evt, infoPanels.getGraphicsList());
            
            if (newClosest != null && newClosest.isVisible()) {
                
                InfoPanel infoPanel = infoPanels.getInfoPanel(newClosest.getClass());
                
                // Register the distance between the mouse position and the closest graphics
                infoPanel.setMouseDist(newClosest.distance(evt.getPoint().getX(), evt.getPoint().getY()));                
                
                if (newClosest != closest) {                    
                    closest = newClosest;
                    Point containerPoint = convertPoint(evt.getPoint());

                    infoPanel.setPos((int) containerPoint.getX(), (int) containerPoint.getY() - 10);
                    
                    // Allow custom initialization by sub-classes
                    if (initInfoPanel(infoPanel, newClosest, evt, containerPoint)) {
                        infoPanel.setVisible(true);
                        getGlassPanel().setVisible(true);
                    }
                    
                    // Hides all but the info panel closest to the mouse point
                    checkInfoPanelVisiblity();
                }
                                
                return true;
            } else if (newClosest == null) {
                closest = null;
                hideInfoPanels();
            }
        }

        return false;
    }

    /**
     * Hides all but the info panel closest to the mouse point
     */
    private synchronized void checkInfoPanelVisiblity() {
        InfoPanel closestInfoPanel = null;
        for (Component component : getGlassPanel().getComponents()) {
            if (component instanceof InfoPanel && component.isVisible()) {
                InfoPanel infoPanel = (InfoPanel)component;
                if (closestInfoPanel != null && infoPanel.getMouseDist() < closestInfoPanel.getMouseDist()) {
                    closestInfoPanel.setVisible(false);
                    closestInfoPanel = infoPanel;
                } else if (closestInfoPanel != null) {
                    infoPanel.setVisible(false);
                } else if (closestInfoPanel == null) {
                    closestInfoPanel = infoPanel;
                }
            }
        }
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
     * 
     * @return the mouse selection tolerance
     */
    public float getMouseSelectTolerance() {
        return EPD.getInstance().getSettings().getGuiSettings().getMouseSelectTolerance();
    }

    /**
     * Returns the first graphics element placed at the mouse event location that matches any of the types passed along.
     * 
     * @param evt
     *            the mouse event
     * @param types
     *            the possible types
     * @return the first matching graphics element
     */
    public final OMGraphic getSelectedGraphic(MouseEvent evt, Class<?>... types) {
        return getSelectedGraphic(graphics, evt, types);
    }

    /**
     * Returns the first graphics element placed at the mouse event location that matches any of the types passed along.<br>
     * The {@code graphicsList} is the list of graphics to search.
     * 
     * @param graphicsList
     *            the graphics list to search
     * @param evt
     *            the mouse event
     * @param types
     *            the possible types
     * @return the first matching graphics element
     */
    public final OMGraphic getSelectedGraphic(OMGraphicList graphicsList, MouseEvent evt, Class<?>... types) {
        return MapEventUtils.getSelectedGraphic(graphicsList, evt, getMouseSelectTolerance(), types);
    }

    /***************************************/
    /** Misc functionality **/
    /***************************************/

    /**
     * Returns a reference to the main frame
     * 
     * @return a reference to the main frame
     */
    public MainFrameCommon getMainFrame() {
        return mainFrame;
    }

    /**
     * Returns a reference to the map menu
     * 
     * @return a reference to the map menu
     */
    public MapMenuCommon getMapMenu() {
        return mapMenu;
    }

    /**
     * Returns a reference to the map frame.
     * <p>
     * For EPDShip the IMapFrame is the MainFrame.<br>
     * For EPDShore the IMapFrame is the JMapFrame
     * 
     * @return a reference to the map frame
     */
    public IMapFrame getMapFrame() {
        return mapFrame;
    }

    /**
     * Returns a reference to the glass pane of the map frame, i.e. of the main frame (EPDShip) or of the map frame (EPDShore)
     * 
     * @return a reference to the glass pane
     */
    public JPanel getGlassPanel() {
        return getMapFrame().getGlassPanel();
    }

    /**
     * Converts a point in {@linkplain MapBean} coordinates the that of the {@linkplain IMapFrame}
     * 
     * @param point
     *            the point to convert
     * @return the converted point
     */
    public Point convertPoint(Point aPoint) {
        return SwingUtilities.convertPoint(mapBean, aPoint, mapFrame.asComponent());
    }

    /**
     * Register the graphics classes that are handled upon left-clicking the mouse.
     * 
     * @param graphics
     *            the graphics classes that are handled upon left-clicking the mouse.
     */
    @SafeVarargs
    protected final void registerMouseClickClasses(Class<? extends OMGraphic>... graphics) {
        for (Class<? extends OMGraphic> g : graphics) {
            mouseClickClasses.add(g);
        }
    }

    /**
     * Register the graphics classes that are handled upon right-clicking the mouse, i.e. the classes that triggers displaying the
     * context menu
     * 
     * @param graphics
     *            the graphics classes that are handled upon right-clicking the mouse.
     */
    @SafeVarargs
    protected final void registerMapMenuClasses(Class<? extends OMGraphic>... graphics) {
        for (Class<? extends OMGraphic> g : graphics) {
            mapMenuClasses.add(g);
        }
    }

    /**
     * Register the graphics classes that are handled upon left-clicking and right-clicking the mouse
     * 
     * @param graphics
     *            the graphics classes that are handled upon left- and right-clicking the mouse.
     */
    @SafeVarargs
    protected final void registerMouseClickAndMapMenuClasses(Class<? extends OMGraphic>... graphics) {
        registerMouseClickClasses(graphics);
        registerMapMenuClasses(graphics);
    }

    /**
     * Sub-classes using {@linkplain #registerMouseClickClasses()} should override this method, which will be called when one of the
     * registered classes is right-clicked.
     * <p>
     * If none of the registered {@linkplain OMGraphic} classes is clicked, this method is called with null as the
     * {@code clickedGraphics} parameter.
     * 
     * @param clickedGraphics
     *            the clicked graphics that triggered the call
     * @param evt
     *            the mouse event
     */
    protected void handleMouseClick(OMGraphic clickedGraphics, MouseEvent evt) {
    }

    /**
     * Sub-classes using {@linkplain #registerMapMenuClasses()} should override this method, which will be called when one of the
     * registered classes is left-clicked.
     * 
     * @param clickedGraphics
     *            the clicked graphics that triggered the call
     * @param evt
     *            the mouse event
     */
    protected void initMapMenu(OMGraphic clickedGraphics, MouseEvent evt) {
    }

    /**
     * Registers the {@linkplain InfoPanel} binding.
     * <p>
     * These panels will automatically be added to the glass pane and will automatically be displayed in the {@code mouseMoved}
     * method.
     * <p>
     * Override the {@linkplain #initInfoPanel()} method to initialize the info panel about to be shown.
     * 
     * @param infoPanels
     *            the {@linkplain InfoPanel} panels to register
     * @param graphics
     *            the list of {@linkplain OMGraphic} elements that triggers the info panel
     */
    @SafeVarargs
    protected final void registerInfoPanel(InfoPanel infoPanel, Class<? extends OMGraphic>... graphics) {
        infoPanels.addBinding(infoPanel, graphics);
    }

    /**
     * For sub-classes using the info-panel registration, override this method to initialize the info panel before it is displayed.
     * <p>
     * The default implementation of {@code mouseMoved()} will find the info panel to display and call this method for custom
     * initialization.
     * <p>
     * Return whether to display the info panel or not
     * 
     * @param infoPanel
     *            the info panel about to be displayed
     * @param newClosest
     *            the mouse-over graphics that triggered the info panel
     * @param evt
     *            the mouse event
     * @param containerPoint
     *            the current container point
     * @return whether to display the info panel or not
     */
    protected boolean initInfoPanel(InfoPanel infoPanel, OMGraphic newClosest, MouseEvent evt, Point containerPoint) {
        return false;
    }

    /**
     * Called when a glass pane has been resolved. Adds all info panels to the glass pane
     */
    private void addInfoPanelsToGlassPane() {
        for (InfoPanel infoPanel : infoPanels.getInfoPanels()) {
            getGlassPanel().add(infoPanel);
        }
    }

    /**
     * Hides all info panels.
     */
    protected void hideInfoPanels() {
        for (InfoPanel infoPanel : infoPanels.getInfoPanels()) {
            infoPanel.setVisible(false);
        }
    }

    /**
     * Publish the change of layers visibility to all listeners
     * 
     */
    private void notifyVisibilityListeners() {
        for (LayerVisiblityListener listener : visibilityListener) {
            listener.visibilityChanged(this);
        }
    }

    /**
     * Add visibility Listener
     * 
     * @param targetListener
     */
    public final void addVisibilityListener(LayerVisiblityListener targetListener) {
        visibilityListener.add(targetListener);
    }

    /**
     * Remove visibility Listener
     * 
     * @param targetListener
     */
    public final void removeVisibilityListener(LayerVisiblityListener targetListener) {
        visibilityListener.remove(targetListener);
    }

    @Override
    public void setVisible(boolean show) {
        super.setVisible(show);

        notifyVisibilityListeners();
    }

}

/***************************************/
/** Helper classes **/
/***************************************/

/**
 * Sub-class of {@linkplain OMGraphicList} that turns on anti-aliasing
 */
class AntialiasedGraphicList extends OMGraphicList {

    private static final long serialVersionUID = 1L;

    /**
     * Turn on anti-aliasing
     */
    @Override
    public void render(Graphics g) {
        Graphics2D image = (Graphics2D) g;
        image.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.render(image);
    }
}
