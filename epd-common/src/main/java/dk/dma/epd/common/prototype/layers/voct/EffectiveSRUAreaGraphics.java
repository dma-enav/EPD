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
package dk.dma.epd.common.prototype.layers.voct;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointData;
import dk.dma.epd.common.prototype.model.voct.sardata.RapidResponseData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.util.Calculator;
import dk.dma.epd.common.util.Converter;

public class EffectiveSRUAreaGraphics extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    AreaInternalGraphics effectiveArea;
    SarEffectiveAreaLines topLine;
    SarEffectiveAreaLines bottomLine;
    SarEffectiveAreaLines leftLine;
    SarEffectiveAreaLines rightLine;

    Position A;
    Position B;
    Position C;
    Position D;
    Double totalSize;

    double verticalBearing = 180;
    double horizontalBearing = 90;

    SARData sarData;

    public enum LineType {
        TOP, BOTTOM, LEFT, RIGHT
    }

    public EffectiveSRUAreaGraphics(Double width, Double height, SARData data) {
        super();

        this.sarData = data;

        totalSize = width * height;

        // Find center point
        Position centerPosition = null;

        if (sarData instanceof RapidResponseData) {
            RapidResponseData rapidResponseData = (RapidResponseData) data;
            centerPosition = rapidResponseData.getDatum();
        }

        if (sarData instanceof DatumPointData) {
            DatumPointData datumData = (DatumPointData) data;
            verticalBearing = Calculator.bearing(datumData.getA(),
                    datumData.getD(), Heading.RL);
            horizontalBearing = Calculator.bearing(datumData.getA(),
                    datumData.getB(), Heading.RL);
            centerPosition = datumData.getDatumDownWind();
        }

        // Find A position
        Position topCenter = Calculator.findPosition(centerPosition,
                Calculator.reverseDirection(verticalBearing),
                Converter.nmToMeters(height / 2));
        
        A = Calculator.findPosition(topCenter,
                Calculator.reverseDirection(horizontalBearing),
                Converter.nmToMeters(width / 2));

        B = Calculator.findPosition(A, horizontalBearing,
                Converter.nmToMeters(width));

        C = Calculator.findPosition(A, verticalBearing,
                Converter.nmToMeters(height));
        D = Calculator.findPosition(C, horizontalBearing,
                Converter.nmToMeters(width));

        sarData.getEffortAllocationData().setEffectiveAreaA(A);
        sarData.getEffortAllocationData().setEffectiveAreaB(B);
        sarData.getEffortAllocationData().setEffectiveAreaC(C);
        sarData.getEffortAllocationData().setEffectiveAreaD(D);

        effectiveArea = new AreaInternalGraphics(A, B, C, D, width, height,
                this, verticalBearing, horizontalBearing);

        topLine = new SarEffectiveAreaLines(A, B, LineType.TOP, this);
        bottomLine = new SarEffectiveAreaLines(C, D, LineType.BOTTOM, this);

        leftLine = new SarEffectiveAreaLines(A, C, LineType.LEFT, this);

        rightLine = new SarEffectiveAreaLines(B, D, LineType.RIGHT, this);

        add(effectiveArea);
        add(bottomLine);
        add(topLine);
        add(leftLine);
        add(rightLine);

    }

    public void updateLines(Position A, Position B, Position C, Position D) {
        
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;

        topLine.updateLine(A, B);
        bottomLine.updateLine(C, D);
        leftLine.updateLine(A, C);
        rightLine.updateLine(B, D);

    }

    public void updateLength(LineType type, Position newPos) {

        double height = 0;
        double width = 0;

        if (type == LineType.BOTTOM) {

            // if (Calculator.bearing(A, newPos, Heading.RL) > 0){

            // We update C point
            C = newPos;

            // New length
            height = Calculator.range(A, C, Heading.GC);

            // Recalculate width
            width = totalSize / height;

            // if (height > 1 && width > 1) {

            // Recalculate B and D
            B = Calculator.findPosition(A, horizontalBearing,
                    Converter.nmToMeters(width));

            D = Calculator.findPosition(C, horizontalBearing,
                    Converter.nmToMeters(width));

            effectiveArea.updatePosition(A, B, C, D, width, height);

            updateLines(A, B, C, D);
            // }
            // }

        }
        if (type == LineType.TOP) {

            // Make sure it doesn\t go over and place A under C
//            if (newPos.getLatitude() - 0.001 > C.getLatitude()) {

                // We update A point
                A = newPos;

                // New length
                height = Calculator.range(A, C, Heading.RL);

                // Recalculate width
                width = totalSize / height;

                // Recalculate B and D
                B = Calculator.findPosition(A, horizontalBearing,
                        Converter.nmToMeters(width));

                D = Calculator.findPosition(C, horizontalBearing,
                        Converter.nmToMeters(width));

                effectiveArea.updatePosition(A, B, C, D, width, height);

                updateLines(A, B, C, D);
//            }
        }

        if (type == LineType.LEFT) {

            // We update A point
            A = newPos;

            // New width

            // New length
            width = Calculator.range(A, B, Heading.RL);

            // Recalculate width
            height = totalSize / width;

            // Recalculate C and D
            C = Calculator.findPosition(A, verticalBearing,
                    Converter.nmToMeters(height));

            D = Calculator.findPosition(C, horizontalBearing,
                    Converter.nmToMeters(width));

            effectiveArea.updatePosition(A, B, C, D, width, height);

            updateLines(A, B, C, D);

        }

        if (type == LineType.RIGHT) {

            // We update B point
            B = newPos;

            // New width

            // New length
            width = Calculator.range(A, B, Heading.RL);

            // Recalculate width
            height = totalSize / width;

            // Recalculate C and D
            C = Calculator.findPosition(A, verticalBearing,
                    Converter.nmToMeters(height));

            D = Calculator.findPosition(C, horizontalBearing,
                    Converter.nmToMeters(width));

            effectiveArea.updatePosition(A, B, C, D, width, height);

            updateLines(A, B, C, D);

        }

//        System.out.println("Updating effective area values");

        // sarData.getEffortAllocationData().setEffectiveAreaA(A);
        // sarData.getEffortAllocationData().setEffectiveAreaB(B);
        // sarData.getEffortAllocationData().setEffectiveAreaC(C);
        // sarData.getEffortAllocationData().setEffectiveAreaD(D);

        // sarData.getEffortAllocationData().setEffectiveAreaHeight(height);
        // sarData.getEffortAllocationData().setEffectiveAreaWidth(width);

        // Top or bottom has been changed

        // If bottom

        // A is the same, we get a new B.
        // Using two points we get a new length, using new length we must
        // calculate new width and get points for that

    }
    
    
    
    public void updateEffectiveAreaSize(SARData sarData){
        sarData.getEffortAllocationData().setEffectiveAreaA(A);
        sarData.getEffortAllocationData().setEffectiveAreaB(B);
        sarData.getEffortAllocationData().setEffectiveAreaC(C);
        sarData.getEffortAllocationData().setEffectiveAreaD(D);
    }

}
