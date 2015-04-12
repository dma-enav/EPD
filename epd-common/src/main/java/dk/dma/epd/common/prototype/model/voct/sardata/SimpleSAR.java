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
package dk.dma.epd.common.prototype.model.voct.sardata;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voct.DatumPointSARISDTO;
import dk.dma.enav.model.voct.SARAreaData;

public class SimpleSAR extends SARData {

    private static final long serialVersionUID = 1L;

 
    Position datum;
//    private List<SARAreaData> sarAreaData;
    Position A;
    Position B;
    Position C;
    Position D;
    
    private double timeElasped;
    
    public SimpleSAR(String sarID, DateTime TLKP, DateTime CSS, double x,
            double y, double safetyFactor, int searchObject, Position A,
            Position B, Position C, Position D, Position datum) {
        super(sarID, TLKP, CSS, datum, x, y, safetyFactor, searchObject);

//        this.A = A;
//        this.B = B;
//        this.C = C;
//        this.D = D;
        this.datum = datum;

        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        
//        sarAreaData = new ArrayList<SARAreaData>();
//        
//        SARAreaData sarArea = new SARAreaData(A, B, C, D, datum, breadth, length);
//        sarAreaData.add(sarArea);
        // Query user for:

        // Position Last Known Position
        // Time of lkp
        // Commence Search Start

        // Search Object
        timeElasped = (double) (getCSSDate().getMillis() - getLKPDate().getMillis()) / 60 / 60 / 1000;

    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }




    /**
     * @return the a
     */
    public Position getA() {
        return A;
    }

    /**
     * @return the b
     */
    public Position getB() {
        return B;
    }

    /**
     * @return the c
     */
    public Position getC() {
        return C;
    }

    /**
     * @return the d
     */
    public Position getD() {
        return D;
    }

    /**
     * @return the datum
     */
    public Position getDatum() {
        return datum;
    }

    
    
    
    /**
     * @return the timeElasped
     */
    public double getTimeElasped() {
        return timeElasped;
    }

    @Override
    public String generateHTML() {
        return "Uhm hello there mate";
    }
}
