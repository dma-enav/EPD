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
package dk.dma.epd.ship.layers.voct;

import java.awt.event.MouseEvent;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMList;
import com.bbn.openmap.proj.coords.LatLonPoint;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.layers.voct.AreaInternalGraphics;
import dk.dma.epd.common.prototype.layers.voct.EffectiveSRUAreaGraphics;
import dk.dma.epd.common.prototype.layers.voct.SarEffectiveAreaLines;
import dk.dma.epd.common.prototype.layers.voct.SarGraphics;
import dk.dma.epd.common.prototype.layers.voct.SearchPatternTemp;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.VOCTUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.VOCTUpdateListener;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.ship.event.DragMouseMode;
import dk.dma.epd.ship.event.NavigationMouseMode;
import dk.dma.epd.ship.service.voct.VOCTManager;

public class VoctLayer extends OMGraphicHandlerLayer implements
        MapMouseListener, VOCTUpdateListener {
    private static final long serialVersionUID = 1L;

    // private DynamicNogoHandler dynamicNogoHandler = null;

    private OMGraphicList graphics = new OMGraphicList();
    private OMGraphic selectedGraphic;
    private boolean dragging;
    private MapBean mapBean;
    private VOCTManager voctManager;
    
    EffectiveSRUAreaGraphics effectiveArea;

    public VoctLayer() {
        // drawSAR();
    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

    @Override
    public void findAndInit(Object obj) {

        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
            voctManager.addListener(this);
            voctManager.setVoctLayer(this);
        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
    }

    @Override
    public MapMouseListener getMapMouseListener() {
        return this;
    }

    @Override
    public String[] getMouseModeServiceList() {
        String[] ret = new String[2];
        ret[0] = NavigationMouseMode.MODE_ID; // "Gestures"
        ret[1] = DragMouseMode.MODE_ID;
        return ret;
    }

    @Override
    public boolean mousePressed(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean mouseReleased(MouseEvent e) {
        if (dragging) {
            dragging = false;
            // doPrepare();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseEvent e) {
        // System.out.println("Mouse Clicked");
        if (e.getButton() != MouseEvent.BUTTON3) {
            return false;
        }

        selectedGraphic = null;
        OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                5.0f);

        for (OMGraphic omGraphic : allClosest) {
            if (omGraphic instanceof AreaInternalGraphics) {
                // System.out.println("Selected Effective Area");
                selectedGraphic = omGraphic;
                break;
            }
        }

        // if (selectedGraphic instanceof WaypointCircle) {
        // WaypointCircle wpc = (WaypointCircle) selectedGraphic;
        // // mainFrame.getGlassPane().setVisible(false);
        // waypointInfoPanel.setVisible(false);
        // routeMenu.routeWaypointMenu(wpc.getRouteIndex(), wpc.getWpIndex());
        // routeMenu.setVisible(true);
        // // routeMenu.show(this, e.getX() - 2, e.getY() - 2);
        // routeMenu(e);
        // return true;
        // }

        return false;
    }

    @Override
    public void mouseEntered(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void mouseExited(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean mouseDragged(MouseEvent e) {
        // System.out.println("Mouse dragged!");
        if (!javax.swing.SwingUtilities.isLeftMouseButton(e)) {
            return false;
        }

        if (!dragging) {
            // mainFrame.getGlassPane().setVisible(false);
            selectedGraphic = null;
            OMList<OMGraphic> allClosest = graphics.findAll(e.getX(), e.getY(),
                    2.0f);
            for (OMGraphic omGraphic : allClosest) {
                if (omGraphic instanceof SarEffectiveAreaLines) {
                    // System.out.println("selected something");
                    selectedGraphic = omGraphic;
                    break;
                } else {
                    if (omGraphic instanceof AreaInternalGraphics) {
                        // System.out.println("selected something");
                        selectedGraphic = omGraphic;
                        // break;
                    }
                    // if (|| omGraphic instanceof AreaInternalGraphics)
                }
            }
        }

        if (selectedGraphic instanceof SarEffectiveAreaLines) {
            // System.out.println("Selected line");
            SarEffectiveAreaLines selectedLine = (SarEffectiveAreaLines) selectedGraphic;

            // If bottom or top we can only adjust latitude

            // If sides we can adjust longitude

            // New Position of line
            LatLonPoint newLatLon = mapBean.getProjection().inverse(
                    e.getPoint());

            Position newPos = Position.create(newLatLon.getLatitude(),
                    newLatLon.getLongitude());

            selectedLine.updateArea(newPos);

            doPrepare();
            dragging = true;
            return true;

        }

        if (selectedGraphic instanceof AreaInternalGraphics) {
            // System.out.println("Moving box");
            AreaInternalGraphics selectedArea = (AreaInternalGraphics) selectedGraphic;

            // New Center
            LatLonPoint newLatLon = mapBean.getProjection().inverse(
                    e.getPoint());

            Position newPos = Position.create(newLatLon.getLatitude(),
                    newLatLon.getLongitude());

            if (!dragging) {
                // System.out.println("only once? first time?");
                selectedArea.adjustInternalPosition(newPos);
            }

            // if (!(newPos == initialBoxRelativePosition)){
            selectedArea.moveRelative(newPos, voctManager.getSarData());
            // }

            doPrepare();
            dragging = true;
            return true;

        }


        return false;
    }

    @Override
    public boolean mouseMoved(MouseEvent paramMouseEvent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void mouseMoved() {
        graphics.deselect();
        repaint();
    }

    @Override
    public void voctUpdated(VOCTUpdateEvent e) {

        if (e == VOCTUpdateEvent.SAR_CANCEL) {
            graphics.clear();
            this.setVisible(false);
        }

        if (e == VOCTUpdateEvent.SAR_DISPLAY) {
            if (voctManager.getSarType() == SAR_TYPE.RAPID_RESPONSE) {
                drawRapidResponse();
            }
            if (voctManager.getSarType() == SAR_TYPE.DATUM_POINT) {
                drawDatumPoint();
            }
            
            this.setVisible(true);
        }

        if (e == VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY) {
            createEffectiveArea();
            this.setVisible(true);
        }

    }
    
    
    
    
    private void drawDatumPoint() {

        DatumPointData data = (DatumPointData) voctManager.getSarData();

        Position A = data.getA();
        Position B = data.getB();
        Position C = data.getC();
        Position D = data.getD();

        Position datumDownWind = data.getDatumDownWind();
        Position datumMin = data.getDatumMin();
        Position datumMax = data.getDatumMax();
        
        double radiusDownWind = data.getRadiusDownWind();
        double radiusMin = data.getRadiusMin();
        double radiusMax = data.getRadiusMax();

        Position LKP = data.getLKP();
        Position WTCPoint = data.getWtc();

        graphics.clear();

//        public SarGraphics(Position datumDownWind, Position datumMin, Position datumMax, double radiusDownWind, double radiusMin, double radiusMax, Position LKP, Position current) {
        SarGraphics sarGraphics = new SarGraphics(datumDownWind, datumMin, datumMax, radiusDownWind, radiusMin, radiusMax, LKP, WTCPoint, A, B, C, D);

        graphics.add(sarGraphics);

        doPrepare();
    }


    private void drawRapidResponse() {

        RapidResponseData data = (RapidResponseData) voctManager.getSarData();

        Position A = data.getA();
        Position B = data.getB();
        Position C = data.getC();
        Position D = data.getD();

        Position datum = data.getDatum();
        double radius = data.getRadius();

        Position LKP = data.getLKP();
        Position WTCPoint = data.getWtc();

        graphics.clear();

        SarGraphics sarGraphics = new SarGraphics(datum, radius, A, B, C, D,
                LKP, WTCPoint);
        graphics.add(sarGraphics);

        System.out.println("A is: " + A.getLongitude());
        System.out.println("B is: " + B);
        System.out.println("C is: " + C);
        System.out.println("D is: " + D);

        System.out.println("Datum is: " + datum);

        doPrepare();
    }

    private void createEffectiveArea() {
        // Probability of Detection Area - updateable

        if (graphics.contains(effectiveArea)) {
            graphics.remove(effectiveArea);
        }

        SARData data = voctManager.getSarData();

        // PoD for each SRU, initialized with an effective area? possibly a
        // unique ID

        double effectiveAreaSize = voctManager.getSarData()
                .getEffortAllocationData().getEffectiveAreaSize();

        System.out.println("EFFECTIVE AREA IS " + effectiveAreaSize);

        // Effective Area: 10 nm2 Initialize by creating box
        double width = Math.sqrt(effectiveAreaSize);
        double height = Math.sqrt(effectiveAreaSize);


        
        
        
//        Position startingPosition = data.getLKP();
//
//        if (voctManager.getSarType() == SAR_TYPE.RAPID_RESPONSE) {
//            startingPosition = ((RapidResponseData) data).getA();
//        }

        effectiveArea = new EffectiveSRUAreaGraphics(width,
                height, data);

        graphics.add(effectiveArea);

        doPrepare();
    }

    public void drawPoints(Position A, Position B) {
        SearchPatternTemp testLine = new SearchPatternTemp(A, B);
        graphics.add(testLine);
        doPrepare();
    }
    
    public void updateEffectiveAreaLocation(SARData sarData){
        effectiveArea.updateEffectiveAreaSize(sarData);
    }
}
