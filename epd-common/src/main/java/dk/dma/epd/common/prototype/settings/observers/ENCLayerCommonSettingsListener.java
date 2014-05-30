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
package dk.dma.epd.common.prototype.settings.observers;

import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings;
import dk.dma.epd.common.prototype.settings.layers.ENCLayerCommonSettings.ENCColorScheme;

/**
 * Interface for observing an {@link ENCLayerCommonSettings} for changes.
 * 
 * @author Janus Varmarken
 */
public interface ENCLayerCommonSettingsListener extends LayerSettingsListener {
    /**
     * Invoked when the setting, specifying if ENC should be loaded on
     * application launch, has been changed.
     * 
     * @param useEnc
     *            The updated value: {@code true} if ENC should be loaded on
     *            application launch, {@code false} if ENC should not be loaded
     *            on application launch.
     */
    void isEncInUseChanged(boolean useEnc);

    /**
     * Invoked when {@link ENCLayerCommonSettings#isS52ShowText()} has changed.
     * 
     * @param showText
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#isS52ShowText()} for its
     *            interpretation.
     */
    void isS52ShowTextChanged(boolean showText);

    /**
     * Invoked when {@link ENCLayerCommonSettings#isS52ShallowPattern()} has
     * changed.
     * 
     * @param useShallowPattern
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#isS52ShallowPattern()} for its
     *            interpretation.
     */
    void isS52ShallowPatternChanged(boolean useShallowPattern);

    /**
     * Invoked when {@link ENCLayerCommonSettings#getS52ShallowContour()} has
     * changed.
     * 
     * @param shallowContour
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#getS52ShallowContour()} for its
     *            interpretation.
     */
    void s52ShallowContourChanged(int shallowContour);

    /**
     * Invoked when {@link ENCLayerCommonSettings#getS52SafetyDepth()} has
     * changed.
     * 
     * @param safetyDepth
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#getS52SafetyDepth()} for its
     *            interpretation.
     */
    void s52SafetyDepthChanged(int safetyDepth);

    /**
     * Invoked when {@link ENCLayerCommonSettings#getS52SafetyContour()} has
     * changed.
     * 
     * @param safetyContour
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#getS52SafetyContour()} for its
     *            interpretation.
     */
    void s52SafetyContourChanged(int safetyContour);

    /**
     * Invoked when {@link ENCLayerCommonSettings#getS52DeepContour()} has
     * changed.
     * 
     * @param deepContour
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#getS52DeepContour()} for its
     *            interpretation.
     */
    void s52DeepContourChanged(int deepContour);

    /**
     * Invoked when {@link ENCLayerCommonSettings#isUseSimplePointSymbols()} has
     * changed.
     * 
     * @param useSimplePointSymbols
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#isUseSimplePointSymbols()} for
     *            its interpretation.
     */
    void isUseSimplePointSymbolsChanged(boolean useSimplePointSymbols);

    /**
     * Invoked when {@link ENCLayerCommonSettings#isUsePlainAreas()} has
     * changed.
     * 
     * @param usePlainAreas
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#isUsePlainAreas()} for its
     *            interpretation.
     */
    void isUsePlainAreasChanged(boolean usePlainAreas);

    /**
     * Invoked when {@link ENCLayerCommonSettings#isS52TwoShades()} has changed.
     * 
     * @param s52TwoShades
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#isS52TwoShades()} for its
     *            interpretation.
     */
    void isS52TwoShadesChanged(boolean s52TwoShades);

    /**
     * Invoked when {@link ENCLayerCommonSettings#getEncColorScheme()} has
     * changed.
     * 
     * @param newScheme
     *            The new value. Refer to
     *            {@link ENCLayerCommonSettings#getEncColorScheme()} and
     *            {@link ENCColorScheme} for its interpretation.
     */
    void encColorSchemeChanged(ENCColorScheme newScheme);
}
