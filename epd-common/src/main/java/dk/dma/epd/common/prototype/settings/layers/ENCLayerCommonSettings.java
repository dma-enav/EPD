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
package dk.dma.epd.common.prototype.settings.layers;

/**
 * @author Janus Varmarken
 */
public class ENCLayerCommonSettings extends LayerSettings<OBSERVER> {
    
    private boolean useEnc = true;
    private boolean s52ShowText;
    private boolean s52ShallowPattern;
    private int s52ShallowContour = 6;
    private int s52SafetyDepth = 8;
    private int s52SafetyContour = 8;
    private int s52DeepContour = 10;
    private boolean useSimplePointSymbols = true;
    private boolean usePlainAreas;
    private boolean s52TwoShades;
    private String color = "Day";
    
}
