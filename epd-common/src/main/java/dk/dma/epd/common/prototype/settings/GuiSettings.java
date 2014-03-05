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
package dk.dma.epd.common.prototype.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.Properties;

import com.bbn.openmap.util.PropUtils;

/**
 * General GUI settings
 */
public class GuiSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    protected static final String PREFIX = "gui.";

    private boolean maximized;
    private Point appLocation = new Point(10, 10);
    private Dimension appDimensions = new Dimension(1280, 800);
    private boolean riskNogoDisabled;
    private boolean alwaysOpenDock = true;
    private boolean showDockMessage = true;
    private float mouseSelectTolerance = 5.0f;
    private boolean fullscreen;
    
    
    public GuiSettings() {

    }
    
    public static String getPrefix() {
        return PREFIX;
    }

    public void readProperties(Properties props) {
        maximized = PropUtils.booleanFromProperties(props, PREFIX + "maximized", maximized);
        double x = PropUtils.doubleFromProperties(props, PREFIX + "appLocation_x", appLocation.getX());
        double y = PropUtils.doubleFromProperties(props, PREFIX + "appLocation_y", appLocation.getY());
        appLocation.setLocation(x, y);
        double w = PropUtils.doubleFromProperties(props, PREFIX + "appDimensions_w", appDimensions.getWidth());
        double h = PropUtils.doubleFromProperties(props, PREFIX + "appDimensions_h", appDimensions.getHeight());
        appDimensions.setSize(w, h);
        riskNogoDisabled = PropUtils.booleanFromProperties(props, PREFIX + "riskNogoDisabled", riskNogoDisabled);
        alwaysOpenDock = PropUtils.booleanFromProperties(props, PREFIX + "alwaysOpenDock", alwaysOpenDock);
        showDockMessage = PropUtils.booleanFromProperties(props, PREFIX + "showDockMessage", showDockMessage);
        mouseSelectTolerance = PropUtils.floatFromProperties(props, PREFIX + "mouseSelectTolerance", mouseSelectTolerance);
        fullscreen = PropUtils.booleanFromProperties(props, PREFIX + "fullscreen", fullscreen);
        
        
    }

    public void setProperties(Properties props) {
        props.put(PREFIX + "maximized", Boolean.toString(maximized));
        props.put(PREFIX + "appLocation_x", Double.toString(appLocation.getX()));
        props.put(PREFIX + "appLocation_y", Double.toString(appLocation.getY()));
        props.put(PREFIX + "appDimensions_w", Double.toString(appDimensions.getWidth()));
        props.put(PREFIX + "appDimensions_h", Double.toString(appDimensions.getHeight()));
        props.put(PREFIX + "riskNogoDisabled", Boolean.toString(riskNogoDisabled));
        props.put(PREFIX + "alwaysOpenDock", Boolean.toString(alwaysOpenDock));
        props.put(PREFIX + "showDockMessage", Boolean.toString(showDockMessage));
        props.put(PREFIX + "mouseSelectTolerance", Float.toString(mouseSelectTolerance));
        props.put(PREFIX + "fullscreen", Boolean.toString(fullscreen));
    }

    
    
    public boolean isAlwaysOpenDock() {
        return alwaysOpenDock;
    }

    public void setAlwaysOpenDock(boolean alwaysOpenDock) {
        this.alwaysOpenDock = alwaysOpenDock;
    }

    public boolean isShowDockMessage() {
        return showDockMessage;
    }

    public void setShowDockMessage(boolean showDockMessage) {
        this.showDockMessage = showDockMessage;
    }

    public Point getAppLocation() {
        return appLocation;
    }

    public void setAppLocation(Point appLocation) {
        this.appLocation = appLocation;
    }

    public Dimension getAppDimensions() {
        return appDimensions;
    }

    public void setAppDimensions(Dimension appDimensions) {
        this.appDimensions = appDimensions;
    }

    public boolean isMaximized() {
        return maximized;
    }

    public void setMaximized(boolean maximized) {
        this.maximized = maximized;
    }
    
    public boolean isRiskNogoDisabled() {
        return riskNogoDisabled;
    }
    
    public void setRiskNogoDisabled(boolean riskNogoDisabled) {
        this.riskNogoDisabled = riskNogoDisabled;
    }

    /**
     * @return the mouseSelectTolerance
     */
    public float getMouseSelectTolerance() {
        return mouseSelectTolerance;
    }

    /**
     * @param mouseSelectTolerance the mouseSelectTolerance to set
     */
    public void setMouseSelectTolerance(float mouseSelectTolerance) {
        this.mouseSelectTolerance = mouseSelectTolerance;
    }
 
    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }
}
