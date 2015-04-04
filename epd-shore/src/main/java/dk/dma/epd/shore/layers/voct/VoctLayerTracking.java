/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.shore.layers.voct;

import java.util.HashMap;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voct.SARAreaData;
import dk.dma.epd.common.prototype.layers.voct.EffortAllocationAreaGraphics;
import dk.dma.epd.common.prototype.layers.voct.SarGraphics;
import dk.dma.epd.common.prototype.model.voct.SAR_TYPE;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumLineData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointDataSARIS;
import dk.dma.epd.common.prototype.model.voct.sardata.EffortAllocationData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.voct.VOCTUpdateEvent;
import dk.dma.epd.shore.gui.views.JMapFrame;
import dk.dma.epd.shore.voct.SRUManager;
import dk.dma.epd.shore.voct.SRUUpdateEvent;
import dk.dma.epd.shore.voct.SRUUpdateListener;
import dk.dma.epd.shore.voct.VOCTManager;

public class VoctLayerTracking extends VoctLayerCommon implements SRUUpdateListener {
    private static final long serialVersionUID = 1L;
    private OMGraphicList graphics = new OMGraphicList();

    private HashMap<Long, EffortAllocationAreaGraphics> effectiveAreas = new HashMap<>();
    // private HashMap<Long, SRUObject> sruVessels = new HashMap<>();
    private SRUManager sruManager;

    @Override
    public void findAndInit(Object obj) {

        if (obj instanceof JMapFrame) {
            jMapFrame = (JMapFrame) obj;
        }

        if (obj instanceof VOCTManager) {
            voctManager = (VOCTManager) obj;
            voctManager.addListener(this);
            this.voctUpdated(VOCTUpdateEvent.SAR_DISPLAY);
        }
        if (obj instanceof MapBean) {
            mapBean = (MapBean) obj;
        }
        if (obj instanceof SRUManager) {
            this.sruManager = (SRUManager) obj;
            sruManager.setVoctTrackingLayer(this);
            sruManager.addListener(this);
        }
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
            if (voctManager.getSarType() == SAR_TYPE.DATUM_LINE) {
                drawDatumLine();
            }
            if (voctManager.getSarType() == SAR_TYPE.SARIS_DATUM_POINT) {
                drawSarisDatumPoint();
            }

            this.setVisible(true);
        }

        // Do nothing
        if (e == VOCTUpdateEvent.EFFORT_ALLOCATION_DISPLAY) {

        }

    }

    private void drawSarisDatumPoint() {

        graphics.clear();

        DatumPointDataSARIS data = (DatumPointDataSARIS) voctManager.getSarData();

        for (int i = 0; i < data.getSarisTarget().size(); i++) {

            SARAreaData sarArea = data.getSarAreaData().get(i);

            SarGraphics sarAreaGraphic = new SarGraphics(sarArea.getA(), sarArea.getB(), sarArea.getC(), sarArea.getD(),
                    sarArea.getCentre(), data.getSarisTarget().get(i).getName());

            graphics.add(sarAreaGraphic);
        }

        doPrepare();
        this.setVisible(true);
    }

    public void removeEffectiveArea(long mmsi) {
        if (effectiveAreas.containsKey(mmsi)) {
//          System.out.println("Removing existing");
          EffortAllocationAreaGraphics area = effectiveAreas.get(mmsi);
          graphics.remove(area);
          effectiveAreas.remove(mmsi);
      }
        doPrepare();
    }

    public void drawEffectiveArea(long mmsi) {
//        System.out.println("Drawing effective Area on tracking layer");
        // effectiveAreas

        if (effectiveAreas.containsKey(mmsi)) {
//            System.out.println("Removing existing");
            EffortAllocationAreaGraphics area = effectiveAreas.get(mmsi);
            graphics.remove(area);
            effectiveAreas.remove(mmsi);
        }

        if (voctManager.getSarData().getEffortAllocationData().containsKey(mmsi)) {

            EffortAllocationData effortAllocationData = voctManager.getSarData().getEffortAllocationData().get(mmsi);

//            System.out.println("The effort allocation is : " + effortAllocationData);
//            System.out.println(sruManager.getSRUs(mmsi).getName());

            EffortAllocationAreaGraphics area = new EffortAllocationAreaGraphics(effortAllocationData.getEffectiveAreaA(),
                    effortAllocationData.getEffectiveAreaB(), effortAllocationData.getEffectiveAreaC(),
                    effortAllocationData.getEffectiveAreaD(), 0, sruManager.getSRUs(mmsi).getName());

            effectiveAreas.put(mmsi, area);

            graphics.add(area);

            doPrepare();
        }
    }

    private void drawRapidResponse() {

        RapidResponseData data = (RapidResponseData) voctManager.getSarData();

        Position A = data.getA();
        Position B = data.getB();
        Position C = data.getC();
        Position D = data.getD();

        graphics.clear();

        SarGraphics sarGraphics = new SarGraphics(A, B, C, D);
        graphics.add(sarGraphics);

        doPrepare();
    }

    private void drawDatumPoint() {

        DatumPointData data = (DatumPointData) voctManager.getSarData();

        Position A = data.getA();
        Position B = data.getB();
        Position C = data.getC();
        Position D = data.getD();

        graphics.clear();

        SarGraphics sarGraphics = new SarGraphics(A, B, C, D);
        graphics.add(sarGraphics);

        doPrepare();
    }

    private void drawDatumLine() {

        // Create as many data objects as is contained

        // Clear all previous
        graphics.clear();

        DatumLineData datumLineData = (DatumLineData) voctManager.getSarData();

        for (int i = 0; i < datumLineData.getDatumPointDataSets().size(); i++) {

//            System.out.println("Creating area " + i);
            DatumPointData data = datumLineData.getDatumPointDataSets().get(i);

            Position A = data.getA();
            Position B = data.getB();
            Position C = data.getC();
            Position D = data.getD();

            SarGraphics sarGraphics = new SarGraphics(A, B, C, D);
            graphics.add(sarGraphics);
        }

        // public SarGraphics(Position datumDownWind, Position datumMin,
        // Position datumMax, double radiusDownWind, double radiusMin, double
        // radiusMax, Position LKP, Position current) {

        doPrepare();

    }

    @Override
    public synchronized OMGraphicList prepare() {
        graphics.project(getProjection());
        return graphics;
    }

    @Override
    public void sruUpdated(SRUUpdateEvent e, long mmsi) {

        // if (e == SRUUpdateEvent.SRU_ACCEPT) {
        // // A SRU has accepted - create the object - possibly overwrite
        // // existing
        //
        // // Retrieve and remove the old
        // if (sruVessels.containsKey(mmsi)) {
        // graphics.remove(sruVessels.get(mmsi));
        // sruVessels.remove(mmsi);
        // }
        //
        // SRUObject sruObject = new SRUObject(sruManager
        // .getsRUCommunication().get(mmsi));
        // sruVessels.put(mmsi, sruObject);
        // graphics.add(sruObject);
        //
        // doPrepare();
        //
        // }

        // if (e == SRUUpdateEvent.SRU_REJECT) {
        // // A SRU has rejected - remove the object if it exist
        // // Retrieve and remove the old
        // if (sruVessels.containsKey(mmsi)) {
        // graphics.remove(sruVessels.get(mmsi));
        // sruVessels.remove(mmsi);
        //
        // doPrepare();
        // }
        //
        // }
        //
        // if (e == SRUUpdateEvent.BROADCAST_MESSAGE) {
        // // SRU Broadcast - a new SRU broadcast message has been recieved,
        // // update stuff
        //
        // if (sruVessels.containsKey(mmsi)) {
        //
        // sruVessels.get(mmsi).updateSRU();
        //
        // doPrepare();
        // }
        //
        // }
        //
        // if (e == SRUUpdateEvent.SRU_REMOVED) {
        // // SRU Broadcast - a new SRU broadcast message has been recieved,
        // // update stuff
        //
        // if (sruVessels.containsKey(mmsi)) {
        // graphics.remove(sruVessels.get(mmsi));
        // sruVessels.remove(mmsi);
        // doPrepare();
        // }
        //
        // }
    }

}
