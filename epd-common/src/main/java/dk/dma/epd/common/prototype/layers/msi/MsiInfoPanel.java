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

import dk.dma.epd.common.prototype.gui.util.InfoPanel;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.frv.enav.common.xml.msi.MsiMessage;

/**
 * MSI mouse over info
 */
public class MsiInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public MsiInfoPanel() {
        super();
    }

    /**
     * Display a msi message
     * @param message
     */
    public void showMsiInfo(MsiMessage message) {
        String encText = message.getEncText();
        if (encText == null){
            encText = "No MSI Message attached";
        }
        showText(encText);
    }

    /**
     * Display a msi message
     * @param message
     */
    public void showMsiInfo(MsiMessageExtended message) {
        if (message != null && message.getMsiMessage() != null) {
            showMsiInfo(message.getMsiMessage());
        }
    }
}
