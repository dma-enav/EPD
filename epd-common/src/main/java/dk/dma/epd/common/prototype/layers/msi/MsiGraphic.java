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
package dk.dma.epd.common.prototype.layers.msi;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.frv.enav.common.xml.msi.MsiMessage;

public class MsiGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;
    MsiMessageExtended message;
    private MsiMessage msiMessage;
    //private MsiTextBox msiTextBox;
    
    public MsiGraphic(MsiMessageExtended message) {
        super();
        this.message = message; 
        this.msiMessage = message.msiMessage;
        
        // Create text box and hide it
//        msiTextBox = new MsiTextBox(msiMessage, message.acknowledged);
//        add(msiTextBox);
//        msiTextBox.setVisible(false);
        
        // Create symbol graphic
        MsiSymbolGraphic msiSymbolGrahic = new MsiSymbolGraphic(message);
        add(msiSymbolGrahic);
        
        // Create location grahic
        MsiLocationGraphic msiLocationGraphic = new MsiLocationGraphic(msiMessage);
        add(msiLocationGraphic);
    }
    
//    public void showTextBox(){
//        msiTextBox.setVisible(true);
//    }
//    
//    public void hideTextBox(){
//        msiTextBox.setVisible(false);
//    }
//    
//    public boolean getTextBoxVisible(){
//        return msiTextBox.isVisible();
//    }
    
    public MsiMessageExtended getMessage() {
        return message;
    }
}
