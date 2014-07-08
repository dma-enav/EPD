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
package dk.dma.epd.common.prototype.gui.voct;

import java.awt.Dialog;
import java.awt.Window;

import javax.swing.WindowConstants;

import dk.dma.epd.common.prototype.gui.ComponentDialog;

public class VOCTAdditionalInfoDialog extends ComponentDialog {

    private static final long serialVersionUID = 1L;
    private VOCTAdditionalInfoPanel additionalInfoPanel;

    public VOCTAdditionalInfoDialog(Window window) {

        super(window, "Additional Info", Dialog.ModalityType.MODELESS);
        
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setBounds(100, 100, 400, 400);
        if (window != null) {
            setLocationRelativeTo(window);
        }
        
        additionalInfoPanel = new VOCTAdditionalInfoPanel(true);

        add(additionalInfoPanel);

    }

}
