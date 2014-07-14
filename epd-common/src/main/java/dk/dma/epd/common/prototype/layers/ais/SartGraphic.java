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
package dk.dma.epd.common.prototype.layers.ais;

import javax.swing.ImageIcon;

import dk.dma.epd.common.graphics.CenterRaster;

/**
 * Graphic for AIS SART symbol
 */
public class SartGraphic extends CenterRaster {
    private static final long serialVersionUID = 1L;
    
    private SarTargetGraphic sarTargetGraphic;

    public SartGraphic(double lat, double lon, int i, int j, ImageIcon imageIcon, SarTargetGraphic sarTargetGraphic) {
        super(lat, lon, i, j, imageIcon);
        this.sarTargetGraphic = sarTargetGraphic;
    }
    
    public SarTargetGraphic getSarTargetGraphic() {
        return sarTargetGraphic;
    }

}
