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

import javax.swing.SwingUtilities;

import dk.dma.epd.common.prototype.ais.VesselPositionData;
import dk.dma.epd.common.prototype.ais.VesselStaticData;
import dk.dma.epd.common.prototype.ais.VesselTarget;
import dk.dma.epd.common.prototype.ais.VesselTarget.AisClass;
import dk.dma.epd.common.prototype.gui.util.HtmlInfoPanel;
import dk.dma.epd.common.text.Formatter;

/**
 * AIS target info panel that provides the basic set of AIS target mouse over
 * details. Sub classes can add additional information by overriding
 * {@link #produceBodyContent(VesselTarget)}.
 * 
 * @author Janus Varmarken
 */
@SuppressWarnings("serial")
public class AisTargetInfoPanelCommon extends HtmlInfoPanel<VesselTarget> {

    private final String na = "N/A";

    /**
     * Builds and shows this {@code AisTargetInfoPanelCommon}.
     * 
     * @param dataObject
     *            A {@code VesselTarget} that contains the data that is to be
     *            displayed in this {@code AisTargetInfoPanelCommon}.
     */
    public void showAisInfoLabel(VesselTarget dataObject) {
        if (!SwingUtilities.isEventDispatchThread()) {
            // TODO change to assert?
            throw new RuntimeException(
                    this.getClass().getSimpleName()
                            + " cannot be updated outside the Java Event Dispatch Thread.");
        }
        this.showText(this.buildHtml(dataObject));
    }

    /**
     * Adds vessel name, call sign, COG and SOG to the HTML.
     * 
     * @param dataObject
     *            {@code VesselTarget} containing the aforementioned data.
     */
    @Override
    protected void produceBodyContent(VesselTarget dataObject) {
        VesselStaticData staticData = dataObject.getStaticData();
        String name = null, callsign = null;
        if (staticData != null) {
            name = staticData.getTrimmedName();
            callsign = staticData.getTrimmedCallsign();
        }
        VesselPositionData positionData = dataObject.getPositionData();
        String cog = na, sog = na;

        if (positionData != null) {
            cog = Formatter.formatDegrees((double) positionData.getCog(), 0);
            sog = Formatter.formatSpeed((double) positionData.getSog());
        }

        if (name != null) {
            this.builder.append(name + " (" + dataObject.getMmsi() + ")");
        } else {
            this.builder.append(dataObject.getMmsi());
        }
        if (dataObject.getAisClass() == AisClass.B) {
            this.builder.append(" [" + BOLD_START + "B" + BOLD_END + "]");
        }
        this.builder.append(BR_TAG);

        if (callsign != null) {
            this.builder.append(callsign + BR_TAG);
        }
        this.builder.append("COG " + cog + "  SOG " + sog + BR_TAG);
    }
}
