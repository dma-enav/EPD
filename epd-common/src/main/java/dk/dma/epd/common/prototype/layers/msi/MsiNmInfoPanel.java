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
import dk.dma.epd.common.prototype.notification.MsiNmNotification;
import org.apache.commons.lang.StringUtils;

/**
 * MSI-NM mouse over info
 */
public class MsiNmInfoPanel extends InfoPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     */
    public MsiNmInfoPanel() {
        super();
    }

    /**
     * Display a msi-nm message
     * @param message
     */
    public void showMsiInfo(MsiNmNotification message) {
        String text = "";
        if (message != null && message.get().getDescs().size() > 0) {
            text = StringUtils.defaultString(message.get().getDescs().get(0).getTitle());
        }
        showText(text);
    }
}
