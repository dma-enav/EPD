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
package dk.dma.epd.common.prototype.layers.msi;

import javax.swing.ImageIcon;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;

/**
 * Graphic for MSI symbol 
 */
public class MsiSymbolGraphic extends MsiSymbolPosition {
    private static final long serialVersionUID = 1L;
    
    public MsiSymbolGraphic(MsiMessageExtended message) {
        super(message);
        setVague(true);
    }
    
    
    
    @Override
    public void createSymbol(Position pos) {
        CenterRaster msiSymbol;
        ImageIcon msiSymbolImage;
        int imageWidth;
        int imageHeight;

        if(acknowledged) {
            msiSymbolImage = new ImageIcon(getClass().getResource("/images/msi/msi_symbol_32.png"));
            imageWidth = msiSymbolImage.getIconWidth();
            imageHeight = msiSymbolImage.getIconHeight();
        } else {
            msiSymbolImage = new ImageIcon(getClass().getResource("/images/msi/msi_unack_symbol_32.png"));
            imageWidth = msiSymbolImage.getIconWidth();
            imageHeight = msiSymbolImage.getIconHeight();
        }
        msiSymbol = new CenterRaster(pos.getLatitude(), pos.getLongitude(), imageWidth, imageHeight, msiSymbolImage);
        add(msiSymbol);
    }
    
}
