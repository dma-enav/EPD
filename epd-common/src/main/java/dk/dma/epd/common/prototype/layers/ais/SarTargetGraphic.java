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

import java.awt.Font;

import javax.swing.ImageIcon;

import com.bbn.openmap.omGraphics.OMText;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.SarTarget;
import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;

/**
 * AIS SART graphic
 */
public class SarTargetGraphic extends TargetGraphic {
    private static final long serialVersionUID = 1L;

    private SarTarget sarTarget;

    private SartGraphic newSartMark;
    private SartGraphic oldSartMark;
    private Font font = new Font(Font.SANS_SERIF, Font.BOLD, 11);
    private OMText label = new OMText(0, 0, 0, 0, "", font, OMText.JUSTIFY_CENTER);

    private PastTrackGraphic pastTrackGraphic = new PastTrackGraphic();

    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings, float mapScale) {
        sarTarget = (SarTarget) aisTarget;
        VesselPositionData posData = sarTarget.getPositionData();
        // VesselStaticData staticData = sarTarget.getStaticData();
        Position pos = posData.getPos();

        double lat = pos.getLatitude();
        double lon = pos.getLongitude();

        if (size() == 0) {
            createGraphics();
        }
        
        if (sarTarget.isOld()) {
            oldSartMark.setVisible(true);
            newSartMark.setVisible(false);
            oldSartMark.setLat(lat);
            oldSartMark.setLon(lon);
        } else {
            newSartMark.setVisible(true);
            oldSartMark.setVisible(false);
            newSartMark.setLat(lat);
            newSartMark.setLon(lon);
        }

        label.setLat(lat);
        label.setLon(lon);
        label.setY(30);
        label.setData("AIS SART");

        // Past track graphics
        pastTrackGraphic.update(sarTarget);
    }

    private void createGraphics() {
        ImageIcon newSartIcon = new ImageIcon(SarTargetGraphic.class.getResource("/images/ais/aisSart.png"));
        newSartMark = new SartGraphic(0, 0, newSartIcon.getIconWidth(), newSartIcon.getIconHeight(), newSartIcon, this);
        add(newSartMark);
        newSartMark.setVisible(false);

        ImageIcon oldSartIcon = new ImageIcon(SarTargetGraphic.class.getResource("/images/ais/aisSartOld.png"));
        oldSartMark = new SartGraphic(0, 0, oldSartIcon.getIconWidth(), oldSartIcon.getIconHeight(), oldSartIcon, this);
        add(oldSartMark);
        oldSartMark.setVisible(false);
        
        add(label);
        
        add(pastTrackGraphic);
    }
    
    public SarTarget getSarTarget() {
        return sarTarget;
    }

}
