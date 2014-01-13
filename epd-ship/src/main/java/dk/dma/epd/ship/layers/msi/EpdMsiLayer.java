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
package dk.dma.epd.ship.layers.msi;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.MouseDelegator;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.layers.msi.MsiDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiGraphic;
import dk.dma.epd.common.prototype.layers.msi.MsiLayer;
import dk.dma.epd.common.prototype.layers.msi.MsiSymbolGraphic;
import dk.dma.epd.common.prototype.layers.routeEdit.NewRouteContainerLayer;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.event.RouteEditMouseMode;
import dk.dma.epd.ship.gui.MainFrame;
import dk.dma.epd.ship.gui.MapMenu;
import dk.dma.epd.ship.gui.TopPanel;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;

public class EpdMsiLayer extends MsiLayer implements MapMouseListener {
    private static final long serialVersionUID = 1L;

    private MsiHandler msiHandler;

    private OMGraphicList graphics = new OMGraphicList();
    private MapBean mapBean;
    private TopPanel topPanel;
    private MainFrame mainFrame;
    private MsiInfoPanel msiInfoPanel;
    private OMGraphic closest;
    private OMGraphic selectedGraphic;
    private MapMenu msiMenu;

    private MouseDelegator mouseDelegator;
    private LatLonPoint mousePosition;
    private NewRouteContainerLayer newRouteLayer;

    public EpdMsiLayer() {

    }

    public void doUpdate() {
        graphics.clear();
        Date now = PntTime.getInstance().getDate();
        // Get messages
        List<MsiMessageExtended> messages = msiHandler.getMessageList();
        for (MsiMessageExtended message : messages) {
            
            // Not able to show messages without location
            if (!message.msiMessage.hasLocation()) {
                continue;
            }
            
            // Is it valid now
            if (!message.isValidAt(now)) {
                continue;
            }
            
            // Filtering begins here
            if(EPDShip.getInstance().getSettings().getEnavSettings().isMsiFilter()){
                // It is set to be visible
                if(!message.visible) {
                    if(mousePosition == null) {
                        continue;
                    }
                }
                
                // Check proximity to current location (free navigation mode)
                if(mousePosition != null && !message.visible) {
                    double distance = distanceToShip(message);
                    
                    boolean visibleToOther = false;
                    for (int i = 0; i < newRouteLayer.getRoute().getWaypoints().size(); i++) {
                        double distance2 = distanceToPoint(message, newRouteLayer.getRoute().getWaypoints().get(i).getPos());
                        if(distance2 <= EPDShip.getInstance().getSettings().getEnavSettings().getMsiVisibilityFromNewWaypoint()){
                            visibleToOther = true;
                        }
                    }
                    
                    boolean visibleToSelf = distance <= EPDShip.getInstance().getSettings().getEnavSettings().getMsiVisibilityFromNewWaypoint();
                    
                    if (!visibleToSelf && !visibleToOther){
                        continue;
                    }
                }
            }

            
            // Create MSI graphic
            MsiGraphic msiGraphic = new MsiGraphic(message);
            graphics.add(msiGraphic);
            
            if(mapBean != null && message.relevant){
                MsiDirectionalIcon direction = new MsiDirectionalIcon(mapBean);
                direction.setMarker(message);
                graphics.add(direction);
            }
        }
        doPrepare();
    }

    /**
     * Calculates the spherical distance from an MSI warning to the ship's
     * position. Currently just a test-implementation where the mouse simulates
     * the ship's position
     * 
     * @param msiMessageExtended
     *            MSI message to calculate distance for
     * @return Arc distance `c'
     */
    private double distanceToShip(MsiMessageExtended msiMessageExtended) {
        List<MsiPoint> msiPoints = msiMessageExtended.msiMessage.getLocation()
                .getPoints();
        Double distance = Double.MAX_VALUE;
        for (MsiPoint msiPoint : msiPoints) {
            // double currentDistance =
            // GreatCircle.sphericalDistance(ProjMath.degToRad(mousePosition.getLatitude()),
            // ProjMath.degToRad(mousePosition.getLongitude()),
            // ProjMath.degToRad(msiPoint.getLatitude()),
            // ProjMath.degToRad(msiPoint.getLongitude()));
            Position mouseLocation = Position.create(
                    mousePosition.getLatitude(), mousePosition.getLongitude());
            Position msiLocation = Position.create(msiPoint.getLatitude(),
                    msiPoint.getLongitude());
            double currentDistance = Calculator.range(mouseLocation,
                    msiLocation, Heading.GC);
            distance = Math.min(currentDistance, distance);
        }
        return distance;
    }

    /**
     * Calculates the spherical distance from an MSI warning to a given position
     * 
     * @param msiMessageExtended
     *            MSI message to calculate distance for
     * @return Arc distance `c'
     */
    private double distanceToPoint(MsiMessageExtended msiMessageExtended,
            Position position) {
        List<MsiPoint> msiPoints = msiMessageExtended.msiMessage.getLocation()
                .getPoints();
        Double distance = Double.MAX_VALUE;
        for (MsiPoint msiPoint : msiPoints) {
            // double currentDistance =
            // GreatCircle.sphericalDistance(ProjMath.degToRad(mousePosition.getLatitude()),
            // ProjMath.degToRad(mousePosition.getLongitude()),
            // ProjMath.degToRad(msiPoint.getLatitude()),
            // ProjMath.degToRad(msiPoint.getLongitude()));
            Position msiLocation = Position.create(msiPoint.getLatitude(),
                    msiPoint.getLongitude());
            double currentDistance = Calculator.range(position, msiLocation,
                    Heading.GC);
            distance = Math.min(currentDistance, distance);
        }
        return distance;
    }

    @Override
    public synchronized OMGraphicList prepare() {
        // for (OMGraphic graphic : graphics) {
        // MsiGraphic msiGraphic = (MsiGraphic) graphic;
        // if(mapBean.getProjection().getScale() <=
        // EeINS.getSettings().getEnavSettings().getMsiTextboxesVisibleAtScale()
        // && !msiGraphic.getMessage().acknowledged){
        // if(!msiGraphic.getTextBoxVisible())
        // msiGraphic.showTextBox();
        // } else {
        // if(msiGraphic.getTextBoxVisible())
        // msiGraphic.hideTextBox();
        // }
        // }
        graphics.project(getProjection());
        return graphics;
    }

    public void zoomTo(MsiMessage msiMessage) {
        if (!msiMessage.hasLocation()) {
            return;
        }

        MsiLocation msiLocation = msiMessage.getLocation();
        Position center = msiLocation.getCenter();
        mapBean.setCenter(center.getLatitude(), center.getLongitude());
        mapBean.setScale(EPDShip.getInstance().getSettings().getEnavSettings()
                .getMsiTextboxesVisibleAtScale());
    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler) obj;
        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
        if (obj instanceof TopPanel) {
            topPanel = (TopPanel) obj;
        }
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
            msiInfoPanel = new MsiInfoPanel();
            mainFrame.getGlassPanel().add(msiInfoPanel);
        }
        if (obj instanceof MapMenu) {
            msiMenu = (MapMenu) obj;
        }
        if (obj instanceof MouseDelegator) {
            mouseDelegator = (MouseDelegator) obj;
        }
        if (obj instanceof NewRouteContainerLayer) {
            newRouteLayer = (NewRouteContainerLayer) obj;
        }
    }

    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[3];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = RouteEditMouseMode.MODE_ID;
        ret[2] = DragMouseMode.MODE_ID;
        return ret;
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON3) {
            return false;
        }

        selectedGraphic = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                5.0f);
        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof MsiSymbolGraphic
                    || omGraphic instanceof MsiDirectionalIcon) {
                selectedGraphic = omGraphic;
                break;
            }
        }

        if (selectedGraphic instanceof MsiSymbolGraphic) {
            MsiSymbolGraphic msi = (MsiSymbolGraphic) selectedGraphic;
//            mainFrame.getGlassPane().setVisible(false);
            msiMenu.msiMenu(topPanel, msi);
            msiMenu.setVisible(true);
            msiMenu.show(this, e.getX() - 2, e.getY() - 2);
            msiInfoPanel.setVisible(false);
            return true;
        }
        if (selectedGraphic instanceof MsiDirectionalIcon) {
            MsiDirectionalIcon direction = (MsiDirectionalIcon) selectedGraphic;
            mainFrame.getGlassPane().setVisible(false);
            msiMenu.msiDirectionalMenu(topPanel, direction, this);
            msiMenu.setVisible(true);
            msiMenu.show(this, e.getX() - 10, e.getY() - 10);
            msiInfoPanel.setVisible(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        if (mouseDelegator.getActiveMouseModeID() == RouteEditMouseMode.MODE_ID) {
            mousePosition = null;
            doUpdate();
        }
    }

    @Override
    public void mouseMoved() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean mouseMoved(MouseEvent e) {
        // Testing mouse mode for the MSI relevancy
        if (mouseDelegator.getActiveMouseModeID() == RouteEditMouseMode.MODE_ID) {
            LatLonPoint mousePosition = ((MapBean) e.getSource())
                    .getProjection().inverse(e.getPoint());
            this.mousePosition = mousePosition;
            doUpdate();
        }

        // Show description on hover
        OMGraphic newClosest = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                3.0f);

        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof MsiSymbolGraphic
                    || omGraphic instanceof MsiDirectionalIcon) {
                newClosest = omGraphic;
                break;
            }
        }

        if (newClosest != closest) {
            Point containerPoint = SwingUtilities.convertPoint(mapBean,
                    e.getPoint(), mainFrame);
            if (newClosest instanceof MsiSymbolGraphic) {
                closest = newClosest;
                MsiSymbolGraphic msiSymbolGraphic = (MsiSymbolGraphic) newClosest;
                msiInfoPanel.setPos((int) containerPoint.getX(),
                        (int) containerPoint.getY() - 10);
                msiInfoPanel.showMsiInfo(msiSymbolGraphic.getMsiMessage());
                mainFrame.getGlassPane().setVisible(true);
                return true;
            } else if (newClosest instanceof MsiDirectionalIcon) {
                closest = newClosest;
                mainFrame.getGlassPane().setVisible(true);
                return true;
            } else {
                msiInfoPanel.setVisible(false);
                mainFrame.getGlassPane().setVisible(false);
                closest = null;
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        // if (e.getButton() == MouseEvent.BUTTON1) {
        // int mouseX = e.getX();
        // int mouseY = e.getY();
        //
        // OMGraphic newClosest = null;
        // OMList<OMGraphic> allClosest = graphics.findAll(mouseX, mouseY,
        // 1.0f);
        //
        // for (OMGraphic omGraphic : allClosest) {
        // if (omGraphic instanceof MsiSymbolGraphic) {
        // newClosest = omGraphic;
        // break;
        // }
        // }
        //
        // if (newClosest instanceof MsiSymbolGraphic) {
        // closest = newClosest;
        // MsiSymbolGraphic msiSymbolGraphic = (MsiSymbolGraphic) newClosest;
        // if (topPanel != null && topPanel.getMsiDialog() != null) {
        // topPanel.getMsiDialog().showMessage(msiSymbolGraphic.msiMessage.getMessageId());
        // return true;
        // }
        // }
        //
        // }
        return false;
    }

}
