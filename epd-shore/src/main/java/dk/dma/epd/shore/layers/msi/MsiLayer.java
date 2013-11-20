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
package dk.dma.epd.shore.layers.msi;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.msi.MsiDirectionalIcon;
import dk.dma.epd.common.prototype.layers.msi.MsiGraphic;
import dk.dma.epd.common.prototype.layers.msi.MsiSymbolGraphic;
import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.event.DragMouseMode;
import dk.dma.epd.shore.event.NavigationMouseMode;
import dk.dma.epd.shore.event.SelectMouseMode;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.gui.views.MapMenu;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;


/**
 * Layer handling all msi messages
 *
 */
public class MsiLayer extends OMGraphicHandlerLayer implements MapMouseListener, IMsiUpdateListener {
    private static final long serialVersionUID = 1L;

    private MsiHandler msiHandler;

    private OMGraphicList graphics = new OMGraphicList();
    private MapBean mapBean;
    private JMapFrame jMapFrame;

    private OMGraphic closest;
    private MsiInfoPanel msiInfoPanel;
    private MapMenu msiMenu;
    private OMGraphic selectedGraphic;

    /**
     * Constructor for the layer
     */
    public MsiLayer() {

    }

    /**
     * Call an update on messages if something has changed
     */
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


    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler)obj;
            msiHandler.addListener(this);
        }
        if (obj instanceof MapBean){
            mapBean = (MapBean)obj;
        }
        if (obj instanceof JMapFrame){
            jMapFrame = (JMapFrame) obj;
            msiInfoPanel = new MsiInfoPanel();
            jMapFrame.getGlassPanel().add(msiInfoPanel);
        }
        if (obj instanceof MapMenu){
            msiMenu = (MapMenu) obj;
        }
    }

    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[3];
        ret[0] = DragMouseMode.MODEID; // "DragMouseMode"
        ret[1] = NavigationMouseMode.MODEID; // "ZoomMouseMoude"
        ret[2] = SelectMouseMode.MODEID; // "SelectMouseMode"
        return ret;
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        if(e.getButton() != MouseEvent.BUTTON3){
            return false;
        }

        selectedGraphic = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), 5.0f);
        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof MsiSymbolGraphic || omGraphic instanceof MsiDirectionalIcon) {
                selectedGraphic = omGraphic;
                break;
            }
        }

        if(selectedGraphic instanceof MsiSymbolGraphic){
            MsiSymbolGraphic msi = (MsiSymbolGraphic) selectedGraphic;
            msiMenu.msiMenu(msi);
            msiMenu.setVisible(true);
            msiMenu.show(this, e.getX()-2, e.getY()-2);
            msiInfoPanel.setVisible(false);
            return true;
        }
        if(selectedGraphic instanceof MsiDirectionalIcon) {
            MsiDirectionalIcon direction = (MsiDirectionalIcon) selectedGraphic;
            msiMenu.msiDirectionalMenu(direction, this);
            msiMenu.setVisible(true);
            msiMenu.show(this, e.getX()-10, e.getY()-10);
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
//        if(mouseDelegator.getActiveMouseModeID() == RouteEditMouseMode.modeID) {
//            mousePosition = null;
//            doUpdate();
//        }
    }

    @Override
    public void mouseMoved() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean mouseMoved(MouseEvent e) {

        // Show description on hover
        OMGraphic newClosest = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(), 3.0f);

        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof MsiSymbolGraphic || omGraphic instanceof MsiDirectionalIcon) {
                newClosest = omGraphic;
                break;
            }
        }

        if (newClosest != closest && this.isVisible()) {
            Point containerPoint = SwingUtilities.convertPoint(mapBean, e.getPoint(), jMapFrame);
            if (newClosest instanceof MsiSymbolGraphic) {
                closest = newClosest;
                MsiSymbolGraphic msiSymbolGraphic = (MsiSymbolGraphic)newClosest;

                int x = (int) containerPoint.getX()+10;
                int y = (int) containerPoint.getY()+10;
                jMapFrame.getGlassPanel().setVisible(true);
                msiInfoPanel.showMsiInfo(msiSymbolGraphic.getMsiMessage());
                if(mapBean.getProjection().getWidth() - x < msiInfoPanel.getWidth()){
                    x -= msiInfoPanel.getWidth()+20;
                }
                if(mapBean.getProjection().getHeight() - y < msiInfoPanel.getHeight()){
                    y -= msiInfoPanel.getHeight()+20;
                }
                msiInfoPanel.setPos(x, y);

                msiInfoPanel.setVisible(true);
                return true;
            } else if (newClosest instanceof MsiDirectionalIcon) {
                closest = newClosest;
                msiInfoPanel.setVisible(true);
                return true;
            } else {
                msiInfoPanel.setVisible(false);
                closest = null;
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean mousePressed(MouseEvent arg0) {
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {

        return false;
    }

    @Override
    public synchronized OMGraphicList prepare() {

        graphics.project(getProjection());
        return graphics;
    }

    /**
     * Move and center the map around a specific msi message
     * @param msiMessage
     */
    public void zoomTo(MsiMessage msiMessage) {
        if (!msiMessage.hasLocation()) {
            return;
        }



        MsiLocation msiLocation = msiMessage.getLocation();
        Position center = msiLocation.getCenter();
        mapBean.setCenter(center.getLatitude(), center.getLongitude());
        mapBean.setScale(EPDShore.getSettings().getEnavSettings().getMsiTextboxesVisibleAtScale());

    }

    @Override
    public void msiUpdate() {
        doUpdate();
    }

}
