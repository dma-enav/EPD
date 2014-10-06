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

import com.bbn.openmap.omGraphics.OMGraphicList;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.MsiNmNotification;

import javax.swing.ImageIcon;

/**
 * Graphic for MSI-NM symbol
 */
public class MsiNmNmSymbolGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    protected MsiNmNotification message;

    /**
     * Constructor
     * @param message the MSI-NM message
     */
    public MsiNmNmSymbolGraphic(MsiNmNotification message) {
        this.message = message;
        createSymbol();
        setVague(true);
    }

    /**
     * Returns the message
     * @return the message
     */
    public MsiNmNotification getMsiNmMessage() {
        return message;
    }

    /**
     * Creates the symbol to use for the MSI message
     */
    public void createSymbol() {
        ImageIcon icon;

        if (message.isAcknowledged()) {
            icon = (message.isMsi())
                    ?  EPD.res().getCachedImageIcon("/images/msi/msi_symbol_32.png")
                    :  EPD.res().getCachedImageIcon("/images/msi/nm_symbol_32.png");
        } else {
            icon = (message.isMsi())
                    ?  EPD.res().getCachedImageIcon("/images/msi/msi_unack_symbol_32.png")
                    :  EPD.res().getCachedImageIcon("/images/msi/nm_unack_symbol_32.png");
        }

        Position pos = message.getLocation();
        CenterRaster msiSymbol = new CenterRaster(pos.getLatitude(), pos.getLongitude(), icon.getIconWidth(), icon.getIconHeight(), icon);
        add(msiSymbol);
    }
    
}
