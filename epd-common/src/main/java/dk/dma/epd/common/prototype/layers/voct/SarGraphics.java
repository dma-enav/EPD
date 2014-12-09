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
package dk.dma.epd.common.prototype.layers.voct;

import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;

public class SarGraphics extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    // private NogoPolygon polygon;

    // private MsiTextBox msiTextBox;

    public SarGraphics(Position datum, double radius, Position LKP, List<Position> currents, List<Position> winds) {
        super();

        // this.polygon = polygon;

        // Create location grahic

        SarCircleGraphic sarCircle = new SarCircleGraphic(datum, radius);

        // currents is first
        // winds is the last
//        System.out.println(currents.size());

        if (winds.size() == 00) {
            SarLinesGraphics sarLines = new SarLinesGraphics(LKP, currents.get(0), datum, true, "Datum");
            add(sarLines);
        } else {
            for (int i = 0; i < winds.size(); i++) {

                SarLinesGraphics sarLines = null;

                // First one, connect from LKP
                if (i == 0) {
//                    System.out.println("first");
                    sarLines = new SarLinesGraphics(LKP, currents.get(i), winds.get(i));

                } else {
//                    System.out.println("next");
                    sarLines = new SarLinesGraphics(winds.get(i - 1), currents.get(i), winds.get(i));
                }

                add(sarLines);

            }

            // SarLinesGraphics sarLines = new SarLinesGraphics(winds.get(winds.size()-1), currents.get(currents.size()-1), datum,
            // false, "Datum");
            // add(sarLines);

            SarLinesGraphics sarLines2 = new SarLinesGraphics(LKP, datum, "Datum");
            add(sarLines2);

        }

        add(sarCircle);

    }

    /**
     * Constructor used for Datum Point Graphics
     * 
     * @param datumDownWind
     * @param datumMin
     * @param datumMax
     * @param radiusDownWind
     * @param radiusMin
     * @param radiusMax
     * @param LKP
     * @param current
     */
    public SarGraphics(Position datumDownWind, Position datumMin, Position datumMax, double radiusDownWind, double radiusMin,
            double radiusMax, Position LKP, Position current) {
        super();

        // this.polygon = polygon;

        // Create location grahic

        SarCircleGraphic sarCircleDownWind = new SarCircleGraphic(datumDownWind, radiusDownWind);
        SarCircleGraphic sarCircleMin = new SarCircleGraphic(datumMin, radiusMin);
        SarCircleGraphic sarCircleMax = new SarCircleGraphic(datumMax, radiusMax);

        SarLinesGraphics sarLinesDownWind = new SarLinesGraphics(LKP, current, datumDownWind, true, "Datum DW");
        SarLinesGraphics sarLinesMin = new SarLinesGraphics(LKP, current, datumMin, false, "Datum Min");
        SarLinesGraphics sarLinesMax = new SarLinesGraphics(LKP, current, datumMax, false, "Datum Max");

        add(sarCircleDownWind);
        add(sarCircleMin);
        add(sarCircleMax);

        add(sarLinesDownWind);
        add(sarLinesMin);
        add(sarLinesMax);
    }

    public SarGraphics(Position A, Position B, Position C, Position D, Position center, String areaName) {
        super();

        // this.polygon = polygon;

        // Create location grahic

        SarAreaGraphic sarArea = new SarAreaGraphic(A, B, C, D);
        add(sarArea);
    }

    public SarGraphics(Position datumDownWind, Position datumMin, Position datumMax, double radiusDownWind, double radiusMin,
            double radiusMax, Position LKP, Position current, int dsp) {
        super();

        // this.polygon = polygon;

        // Create location grahic

        SarCircleGraphic sarCircleDownWind = new SarCircleGraphic(datumDownWind, radiusDownWind);
        SarCircleGraphic sarCircleMin = new SarCircleGraphic(datumMin, radiusMin);
        SarCircleGraphic sarCircleMax = new SarCircleGraphic(datumMax, radiusMax);

        SarLinesGraphics sarLinesDownWind = new SarLinesGraphics(LKP, current, datumDownWind, true, "Datum DW", dsp);
        SarLinesGraphics sarLinesMin = new SarLinesGraphics(LKP, current, datumMin, false, "Datum Min", dsp);
        SarLinesGraphics sarLinesMax = new SarLinesGraphics(LKP, current, datumMax, false, "Datum Max", dsp);

        add(sarCircleDownWind);
        add(sarCircleMin);
        add(sarCircleMax);

        add(sarLinesDownWind);
        add(sarLinesMin);
        add(sarLinesMax);
    }

    public SarGraphics(Position A, Position B, Position C, Position D) {

        SarAreaGraphic sarArea = new SarAreaGraphic(A, B, C, D);
        add(sarArea);
    }

}
