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
package dk.dma.epd.common.prototype.layers.ais;

import javax.swing.ImageIcon;

import com.bbn.openmap.proj.Projection;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.ais.AtoNTarget;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;

/**
 * Graphic for AtoN target
 */
public class AtonTargetGraphic extends TargetGraphic {

    private static final long serialVersionUID = 1L;
    private ImageIcon atonImage = new ImageIcon(AtonTargetGraphic.class.getResource("/images/aton/aton.png"));
    private CenterRaster atonMark;
    private  AtoNTarget atonTarget;
    
    public AtonTargetGraphic() {
        super();
        setVague(true);
    }
    
    @Override
    public void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings) {
        atonTarget = (AtoNTarget)aisTarget;
        Position pos = atonTarget.getPos();
        float lat = (float)pos.getLatitude();
        float lon = (float)pos.getLongitude();
        
        atonMark = new CenterRaster(lat, lon, atonImage.getIconWidth(), atonImage.getIconHeight(),atonImage);
        add(atonMark);
    }

    @Override
    public void setMarksVisible(Projection projection, AisSettings aisSettings, NavSettings navSettings) {
        
        
    }

    public AtoNTarget getAtonTarget() {
        return atonTarget;
    }

    
    
}
