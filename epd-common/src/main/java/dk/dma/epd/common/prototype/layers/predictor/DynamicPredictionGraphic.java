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

    private VesselOutline outline = new VesselOutline(Color.GRAY, 1.0f);
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
