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
package dk.dma.epd.ship.layers.areanotice;

import java.awt.Color;

import com.bbn.openmap.omGraphics.OMCircle;
import com.bbn.openmap.proj.Length;

/**
 * Circle drawed in a given lat/lon point and radius in meters scaled with
 * scaleFactor.
 * 
 */
public class ASCircleOrPoint extends OMCircle {

    private static final long serialVersionUID = 1L;

    public ASCircleOrPoint(int scaleFactor, int precision, double latitude, double longitude, int radius) {
        super(latitude, longitude, radius, Length.METER);
        Length units = Length.METER;
        super.setLatLon(latitude, longitude);
        super.setRadius(radius * (float) Math.pow(10, scaleFactor), units);
        super.setLinePaint(Color.black);
    }

}
