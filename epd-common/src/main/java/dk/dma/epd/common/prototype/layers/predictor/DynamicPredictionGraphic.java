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
package dk.dma.epd.common.prototype.layers.predictor;

import java.awt.Color;
import java.awt.Paint;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.prototype.layers.ais.VesselDot;
import dk.dma.epd.common.prototype.layers.ais.VesselOutline;
import dk.dma.epd.common.prototype.layers.ais.VesselTriangle;
import dk.dma.epd.common.prototype.zoom.ZoomLevel;

/**
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public class DynamicPredictionGraphic extends OMGraphicList {

    private VesselOutline outline = new VesselOutline(Color.GRAY, 3.5f);
    private VesselTriangle triangle = new VesselTriangle();
    private VesselDot dot = new VesselDot();

    /**
     * Create a new {@link DynamicPredictionGraphic}.
     */
    public DynamicPredictionGraphic() {

    }

    private void drawAccordingToScale(float scale) {
        // clear old display
        this.clear();
        // figure what display to use
        switch (ZoomLevel.getFromScale(scale)) {
        case VESSEL_OUTLINE:
            this.add(outline);
            break;
        case VESSEL_TRIANGLE:
            this.add(triangle);
            break;
        case VESSEL_DOT:
            this.add(dot);
            break;
        }
    }

    @Override
    public void setFillPaint(Paint arg0) {
        super.setFillPaint(arg0);
        this.dot.setFillPaint(arg0);
        /*
         * We do not set fill paint on outline and triangle in order to keep
         * these transparent.
         */
    }

    @Override
    public void setLinePaint(Paint paint) {
        super.setLinePaint(paint);
        this.outline.setLinePaint(paint);
        this.triangle.setLinePaint(paint);
        this.dot.setLinePaint(paint);
    }

    @Override
    public boolean generate(Projection proj, boolean forceProjectAll) {
        if (proj == null) {
            return true;
        }
        this.drawAccordingToScale(proj.getScale());
        return super.generate(proj, forceProjectAll);
    }

    public void update(VesselPortrayalData data) {
        this.outline.updateGraphic(data);
        this.triangle.updateGraphic(data);
        this.dot.updateGraphic(data);
    }
}
