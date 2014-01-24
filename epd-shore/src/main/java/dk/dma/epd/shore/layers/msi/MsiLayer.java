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
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.layers.GeneralLayer;
import dk.frv.enav.common.xml.msi.MsiLocation;
import dk.frv.enav.common.xml.msi.MsiMessage;


/**
 * Layer handling all msi messages
 *
 */
public class MsiLayer extends GeneralLayer implements IMsiUpdateListener {
    private static final long serialVersionUID = 1L;

    private MsiHandler msiHandler;

    private OMGraphic closest;
    private MsiInfoPanel msiInfoPanel;
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
        super.findAndInit(obj);
        
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler)obj;
            msiHandler.addListener(this);
        }
        if (obj instanceof JMapFrame){
            msiInfoPanel = new MsiInfoPanel();
            mapFrame.getGlassPanel().add(msiInfoPanel);
        }
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
            getMapMenu().msiMenu(msi);
            getMapMenu().setVisible(true);
            getMapMenu().show(this, e.getX()-2, e.getY()-2);
            msiInfoPanel.setVisible(false);
            return true;
        }
        if(selectedGraphic instanceof MsiDirectionalIcon) {
            MsiDirectionalIcon direction = (MsiDirectionalIcon) selectedGraphic;
            getMapMenu().msiDirectionalMenu(direction, this);
            getMapMenu().setVisible(true);
            getMapMenu().show(this, e.getX()-10, e.getY()-10);
            msiInfoPanel.setVisible(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(MouseEvent e) {

        // Show description on hover
        OMGraphic newClosest = getSelectedGraphic(e, MsiSymbolGraphic.class, MsiDirectionalIcon.class);

        if (newClosest != closest && this.isVisible()) {
            Point containerPoint = SwingUtilities.convertPoint(mapBean, e.getPoint(), mapFrame);
            if (newClosest instanceof MsiSymbolGraphic) {
                closest = newClosest;
                MsiSymbolGraphic msiSymbolGraphic = (MsiSymbolGraphic)newClosest;

                int x = (int) containerPoint.getX()+10;
                int y = (int) containerPoint.getY()+10;
                mapFrame.getGlassPanel().setVisible(true);
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
        mapBean.setScale(EPDShore.getInstance().getSettings().getEnavSettings().getMsiTextboxesVisibleAtScale());

    }

    @Override
    public void msiUpdate() {
        doUpdate();
    }

}
