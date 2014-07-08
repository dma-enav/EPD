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
package dk.dma.epd.common.prototype.model.voct;

import java.util.HashMap;

public class WeatherCorrectionFactors {

    private static HashMap<Integer, Double> PIWAndSmallBoats = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> otherObjects = new HashMap<Integer, Double>();
    
    static {
        PIWAndSmallBoats.put(0, 1.0);
        PIWAndSmallBoats.put(1, 0.5);
        PIWAndSmallBoats.put(2, 0.25);
        
        otherObjects.put(0, 1.0);
        otherObjects.put(1, 0.9);
        otherObjects.put(2, 0.9);
    }

    /**
     * @return the pIWAndSmallBoats
     */
    public static HashMap<Integer, Double> getPIWAndSmallBoats() {
        return PIWAndSmallBoats;
    }

    /**
     * @return the otherObjects
     */
    public static HashMap<Integer, Double> getOtherObjects() {
        return otherObjects;
    }
    
    
}
