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

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.prototype.ais.AisTarget;
import dk.dma.epd.common.prototype.settings.AisSettings;
import dk.dma.epd.common.prototype.settings.NavSettings;

/**
 * Abstract base class graphic for AIS targets
 */
public abstract class TargetGraphic extends OMGraphicList {

    private static final long serialVersionUID = 1L;
    
    public TargetGraphic() {
        super();
    }

    /**
     * Oh dear, this is truly messed up!
     * <p>
     * The {@code setVisible()} of {@linkplain OMGraphicList} will set 
     * the visibility attribute of it's child elements unless the {@code vague}
     * property is set. This is not possible, however, since this will cause 
     * selection to stop working.
     * <p>
     * So, we override the {@code setVisible()} instead.
     * 
     * @param visible whether to set the target visible or not
     */
    @Override
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    @Override
    public boolean isVisible() {
        return this.visible;
    }
    
    public abstract void update(AisTarget aisTarget, AisSettings aisSettings, NavSettings navSettings, float mapScale);

    public abstract void setMarksVisible(Projection projection, AisSettings aisSettings, NavSettings navSettings);
    
}
