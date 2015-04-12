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

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.util.MCTypeConverter;

public class SimpleSAR extends SARData {

    private static final long serialVersionUID = 1L;

    private Position datum;
    private Position A;
    private Position B;
    private Position C;
    private Position D;
    private double timeElasped;

    public SimpleSAR(dma.voct.SimpleSAR simpleSar) {
        super(simpleSar.getSarID(), new DateTime(simpleSar.getLKPDate()
                .getTime()), new DateTime(simpleSar.getCSSDate().getTime()),
                Position.create(simpleSar.getDatum().getLatitude(), simpleSar
                        .getDatum().getLongitude()), simpleSar.getX(),
                simpleSar.getY(), simpleSar.getSafetyFactor(), simpleSar
                        .getSearchObject());

        this.datum = Position.create(simpleSar.getDatum().getLatitude(), simpleSar.getDatum().getLongitude());
        
        this.A = Position.create(simpleSar.getA().getLatitude(), simpleSar.getA().getLongitude());
        this.B = Position.create(simpleSar.getB().getLatitude(), simpleSar.getB().getLongitude());
        this.C = Position.create(simpleSar.getC().getLatitude(), simpleSar.getC().getLongitude());
        this.D = Position.create(simpleSar.getD().getLatitude(), simpleSar.getD().getLongitude());

        this.timeElasped = simpleSar.getTimeElapsed();
        
        
    }

    public SimpleSAR(String sarID, DateTime TLKP, DateTime CSS, double x,
            double y, double safetyFactor, int searchObject, Position A,
            Position B, Position C, Position D, Position datum) {

        super(sarID, TLKP, CSS, datum, x, y, safetyFactor, searchObject);

        // this.A = A;
        // this.B = B;
        // this.C = C;
        // this.D = D;
        this.datum = datum;

        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;

        // sarAreaData = new ArrayList<SARAreaData>();
        //
        // SARAreaData sarArea = new SARAreaData(A, B, C, D, datum, breadth,
        // length);
        // sarAreaData.add(sarArea);
        // Query user for:

        // Position Last Known Position
        // Time of lkp
        // Commence Search Start

        // Search Object
        timeElasped = (double) (getCSSDate().getMillis() - getLKPDate()
                .getMillis()) / 60 / 60 / 1000;

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

    public dma.voct.SimpleSAR getModelData() {
        dma.voct.SimpleSAR simpleSar = new dma.voct.SimpleSAR();

        simpleSar.setA(MCTypeConverter.getMaritimeCloudPositin(A));

        simpleSar.setB(MCTypeConverter.getMaritimeCloudPositin(B));
        simpleSar.setC(MCTypeConverter.getMaritimeCloudPositin(C));
        simpleSar.setD(MCTypeConverter.getMaritimeCloudPositin(D));

        simpleSar.setDatum(MCTypeConverter.getMaritimeCloudPositin(datum));

        simpleSar.setCSSDate(MCTypeConverter
                .getMaritimeCloudTimeStamp(getCSSDate()));

        simpleSar.setLKPDate(MCTypeConverter
                .getMaritimeCloudTimeStamp(getLKPDate()));

        simpleSar.setSafetyFactor(getSafetyFactor());

        simpleSar.setSarID(getSarID());

        simpleSar.setSearchObject(getSearchObject());
        simpleSar.setTimeElapsed(getTimeElasped());
        simpleSar.setX(getX());
        simpleSar.setY(getY());

        return simpleSar;

    }
}
