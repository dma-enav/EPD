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
