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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import net.maritimecloud.core.id.MaritimeId;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon.IChatServiceListener;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon.StrategicRouteListener;
import dk.dma.epd.ship.gui.panels.STCCCommunicationPanel;
import dk.dma.epd.ship.service.StrategicRouteHandler;

public class STCCCommunicationComponentPanel extends OMComponentPanel implements Runnable, StrategicRouteListener,
        IChatServiceListener, DockableComponentPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final STCCCommunicationPanel commsPanel = new STCCCommunicationPanel();
    private PntTime gnssTime;
    private StrategicRouteHandler strategicRouteHandler;
    private ChatServiceHandlerCommon chatServiceHandler;

    public STCCCommunicationComponentPanel() {
        super();

        // this.setMinimumSize(new Dimension(10, 25));

        commsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        setLayout(new BorderLayout(0, 0));
        add(commsPanel, BorderLayout.NORTH);
        setVisible(true);
        new Thread(this).start();

    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }

    }

    @Override
    public void findAndInit(Object obj) {
        if (gnssTime == null && obj instanceof PntTime) {
            gnssTime = (PntTime) obj;
        } else if (obj instanceof StrategicRouteHandler) {
            strategicRouteHandler = (StrategicRouteHandler) obj;
            strategicRouteHandler.addStrategicRouteListener(this);
        } else if (obj instanceof ChatServiceHandlerCommon && chatServiceHandler == null) {
            chatServiceHandler = (ChatServiceHandlerCommon) obj;
            chatServiceHandler.addListener(this);
        }
    }

    @Override
    public void strategicRouteUpdate() {
        Long stccMmsi = strategicRouteHandler.getStccMmsi();
        if (stccMmsi != null) {
            commsPanel.activateChat(stccMmsi.intValue());
        } else {
            commsPanel.deactivateChat();
        }

    }

    @Override
    public void chatMessagesUpdated(MaritimeId targetId) {
        commsPanel.updateChatMessagePanel();
    }
    
    /****************************************/
    /** DockableComponentPanel methods     **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "STCC Communication";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInDefaultLayout() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInPanelsMenu() {
        return true;
    }
}
