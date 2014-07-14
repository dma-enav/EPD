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

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.ais.AisTarget;

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
    
    /**
     * Update this {@link TargetGraphic} with new AIS data.
     * @param aisTarget An updated version of the {@link AisTarget} displayed by this {@link TargetGraphic}.
     * @param mapScale The current scale of the map in which this {@link TargetGraphic} resides.
     */
    public abstract void update(AisTarget aisTarget, float mapScale);
    
}
