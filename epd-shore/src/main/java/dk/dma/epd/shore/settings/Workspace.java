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
package dk.dma.epd.shore.settings;

import java.awt.Dimension;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * Map/chart settings
 */
public class Workspace implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "workspace.";

    boolean validWorkspace;
    private List<String> name = new ArrayList<String>();
    private List<Dimension> size = new ArrayList<Dimension>();
    private List<Point> position = new ArrayList<Point>();
    // private Point position = new Point(10, 10);
    private List<Boolean> locked = new ArrayList<Boolean>();
    private List<Boolean> alwaysInFront = new ArrayList<Boolean>();
    private List<LatLonPoint> center = new ArrayList<LatLonPoint>();
    // private LatLonPoint center = new LatLonPoint.Double(56, 11);
    private List<Float> scale = new ArrayList<Float>();
    private List<Boolean> maximized = new ArrayList<Boolean>();
    private List<Point> layerPanelPosition = new ArrayList<>();
    private List<Boolean> layerPanelVisible = new ArrayList<>();
    private Point toolbarPosition = new Point();
    private Point notificationAreaPosition = new Point();
    private Point statusPosition = new Point();
    private boolean statusVisible = true;

    public Workspace() {
    }

    public List<Boolean> getAlwaysInFront() {
        return alwaysInFront;
    }

    public List<LatLonPoint> getCenter() {
        return center;
    }

    public List<String> getName() {
        return name;
    }

    public Point getNotificationAreaPosition() {
        return notificationAreaPosition;
    }

    public List<Point> getPosition() {
        return position;
    }

    public List<Float> getScale() {
        return scale;
    }

    public List<Dimension> getSize() {
        return size;
    }

    public Point getStatusPosition() {
        return statusPosition;
    }

    public Point getToolbarPosition() {
        return toolbarPosition;
    }

    public List<Boolean> isLocked() {
        return locked;
    }

    public List<Boolean> isMaximized() {
        return maximized;
    }

    public boolean isValidWorkspace() {
        return validWorkspace;
    }

    public List<Point> getLayerPanelPosition() {
        return layerPanelPosition;
    }

    public List<Boolean> getLayerPanelVisible() {
        return layerPanelVisible;
    }

    /**
     * Read the properties element and set the internal variables
     *
     * @param props
     */
    public void readProperties(Properties props) {

        // Load properties which require a try/catch.
        try {
            Collections.addAll(name, props.getProperty(PREFIX + "name").split("//"));

            String[] w = props.getProperty(PREFIX + "size_w").split("//");
            String[] h = props.getProperty(PREFIX + "size_h").split("//");

            for (int i = 0; i < w.length; i++) {
                size.add(new Dimension((int) Double.parseDouble(w[i]), (int) Double.parseDouble(h[i])));
            }

            String[] x = props.getProperty(PREFIX + "position_x").split("//");
            String[] y = props.getProperty(PREFIX + "position_y").split("//");

            for (int i = 0; i < x.length; i++) {
                position.add(new Point((int) Double.parseDouble(x[i]), (int) Double.parseDouble(y[i])));
            }

            String[] lockedInput = props.getProperty(PREFIX + "locked").split("//");
            for (String element : lockedInput) {
                locked.add(Boolean.parseBoolean(element));
            }

            String[] maximizedInput = props.getProperty(PREFIX + "maximized").split("//");
            for (String element : maximizedInput) {
                maximized.add(Boolean.parseBoolean(element));
            }

            String[] alwaysInFrontInput = props.getProperty(PREFIX + "alwaysInFront").split("//");
            for (String element : alwaysInFrontInput) {
                alwaysInFront.add(Boolean.parseBoolean(element));
            }

            String[] center_lat = props.getProperty(PREFIX + "center_lat").split("//");
            String[] center_lon = props.getProperty(PREFIX + "center_lon").split("//");

            for (int i = 0; i < w.length; i++) {
                center.add(new LatLonPoint.Double(Double.parseDouble(center_lat[i]), Double.parseDouble(center_lon[i])));
            }

            String[] scaleInput = props.getProperty(PREFIX + "scale").split("//");
            for (String element : scaleInput) {
                scale.add(Float.parseFloat(element));
            }
            
            // Layer panel properties
            if (props.getProperty(PREFIX + "layerPanel_x") != null &&
                    props.getProperty(PREFIX + "layerPanel_y") != null &&
                    props.getProperty(PREFIX + "layerPanel_visible") != null) {
                x = props.getProperty(PREFIX + "layerPanel_x").split("//");
                y = props.getProperty(PREFIX + "layerPanel_y").split("//");
                for (int i = 0; i < x.length; i++) {
                    layerPanelPosition.add(new Point((int) Double.parseDouble(x[i]), (int) Double.parseDouble(y[i])));
                }
                String[] visible = props.getProperty(PREFIX + "layerPanel_visible").split("//");
                for (String element : visible) {
                    layerPanelVisible.add(Boolean.parseBoolean(element));
                }
            }

            validWorkspace = true;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Load properties for toolbar, notification frame, and status panel.
        double x_pos = PropUtils.doubleFromProperties(props, PREFIX + "toolbar_pos_x", toolbarPosition.getX());
        double y_pos = PropUtils.doubleFromProperties(props, PREFIX + "toolbar_pos_y", toolbarPosition.getY());
        toolbarPosition.setLocation(x_pos, y_pos);
        
        x_pos = PropUtils.doubleFromProperties(props, PREFIX + "notification_pos_x",
                notificationAreaPosition.getX());
        y_pos = PropUtils.doubleFromProperties(props, PREFIX + "notification_pos_y",
                notificationAreaPosition.getY());
        notificationAreaPosition.setLocation(x_pos, y_pos);
        
        x_pos = PropUtils.doubleFromProperties(props, PREFIX + "status_pos_x", statusPosition.getX());
        y_pos = PropUtils.doubleFromProperties(props, PREFIX + "status_pos_y", statusPosition.getY());
        statusPosition.setLocation(x_pos, y_pos);
        statusVisible = PropUtils.booleanFromProperties(props, PREFIX + "statusVisible", statusVisible);

    }

    /**
     * Set the properties to the value from the internal, usually called when
     * saving settings to file
     *
     * @param props
     */
    public void setProperties(Properties props, List<JMapFrame> mapWindows) {
        String name = "";
        String size_h = "";
        String size_w = "";
        String position_x = "";
        String position_y = "";
        String locked = "";
        String center_lat = "";
        String center_lon = "";
        String scale = "";
        String alwaysInFront = "";
        String maximized = "";
        String layerPanel_x = "";
        String layerPanel_y = "";
        String layerPanel_visible = "";


        for (int i = 0; i < mapWindows.size(); i++) {
            name = name + mapWindows.get(i).getTitle() + "//";
            size_h = size_h + mapWindows.get(i).getSize().getHeight() + "//";
            size_w = size_w + mapWindows.get(i).getSize().getWidth() + "//";
            position_x = position_x + mapWindows.get(i).getLocation().getX() + "//";
            position_y = position_y + mapWindows.get(i).getLocation().getY() + "//";
            locked = locked + mapWindows.get(i).isLocked() + "//";
            maximized = maximized + mapWindows.get(i).isMaximum() + "//";
            center_lat = center_lat + mapWindows.get(i).getChartPanel().getMap().getCenter().getY() + "//";
            center_lon = center_lon + mapWindows.get(i).getChartPanel().getMap().getCenter().getX() + "//";
            scale = scale + mapWindows.get(i).getChartPanel().getMap().getScale() + "//";
            alwaysInFront = alwaysInFront + mapWindows.get(i).isInFront() + "//";
            layerPanel_x = layerPanel_x + mapWindows.get(i).getLayerTogglingPanel().getLocation().getX() + "//";
            layerPanel_y = layerPanel_y + mapWindows.get(i).getLayerTogglingPanel().getLocation().getY() + "//";
            layerPanel_visible = layerPanel_visible + mapWindows.get(i).getLayerTogglingPanel().isVisible() + "//";
        }
        props.put(PREFIX + "name", name);
        props.put(PREFIX + "size_h", size_h);
        props.put(PREFIX + "size_w", size_w);
        props.put(PREFIX + "position_x", position_x);
        props.put(PREFIX + "position_y", position_y);
        props.put(PREFIX + "locked", locked);
        props.put(PREFIX + "maximized", maximized);
        props.put(PREFIX + "center_lat", center_lat);
        props.put(PREFIX + "center_lon", center_lon);
        props.put(PREFIX + "scale", scale);
        props.put(PREFIX + "alwaysInFront", alwaysInFront);
        props.put(PREFIX + "layerPanel_x", layerPanel_x);
        props.put(PREFIX + "layerPanel_y", layerPanel_y);
        props.put(PREFIX + "layerPanel_visible", layerPanel_visible);

        props.put(PREFIX + "toolbar_pos_x", Double.toString(toolbarPosition.getX()));
        props.put(PREFIX + "toolbar_pos_y", Double.toString(toolbarPosition.getY()));

        props.put(PREFIX + "notification_pos_x", Double.toString(notificationAreaPosition.getX()));
        props.put(PREFIX + "notification_pos_y", Double.toString(notificationAreaPosition.getY()));

        props.put(PREFIX + "status_pos_x", Double.toString(statusPosition.getX()));
        props.put(PREFIX + "status_pos_y", Double.toString(statusPosition.getY()));
        props.put(PREFIX + "statusVisible", String.valueOf(statusVisible));

    }

    public void setAlwaysInFront(List<Boolean> alwaysInFront) {
        this.alwaysInFront = alwaysInFront;
    }

    public void setCenter(List<LatLonPoint> center) {
        this.center = center;
    }

    public void setLocked(List<Boolean> locked) {
        this.locked = locked;
    }

    public void setMaximized(List<Boolean> maximized) {
        this.maximized = maximized;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public void setNotificationAreaPosition(Point notificationAreaPosition) {
        this.notificationAreaPosition = notificationAreaPosition;
    }

    public void setPosition(List<Point> position) {
        this.position = position;
    }

    public void setScale(List<Float> scale) {
        this.scale = scale;
    }

    public void setSize(List<Dimension> size) {
        this.size = size;
    }

    public void setStatusPosition(Point statusPosition) {
        this.statusPosition = statusPosition;
    }

    public void setToolbarPosition(Point toolbarPosition) {
        this.toolbarPosition = toolbarPosition;
    }

    public void setValidWorkspace(boolean validWorkspace) {
        this.validWorkspace = validWorkspace;
    }

    public boolean isStatusVisible() {
        return statusVisible;
    }

    public void setStatusVisible(boolean statusVisible) {
        this.statusVisible = statusVisible;
    }

}
