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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import net.maritimecloud.core.id.MaritimeId;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import dk.dma.epd.common.prototype.model.identity.IdentityHandler;
import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.ChatServiceHandlerCommon.IChatServiceListener;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;
import dk.dma.epd.common.prototype.service.StrategicRouteHandlerCommon.StrategicRouteListener;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.panels.STCCCommunicationPanel;
import dk.dma.epd.ship.service.StrategicRouteHandler;

public class STCCCommunicationComponentPanel extends OMComponentPanel implements Runnable, StrategicRouteListener,
        IChatServiceListener {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final STCCCommunicationPanel commsPanel = new STCCCommunicationPanel();
    private PntTime gnssTime;
    private StrategicRouteHandler strategicRouteHandler;
    private ChatServiceHandlerCommon chatServiceHandler;
    private IdentityHandler identityHandler;
    private AisHandler aisHandler;

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
        if (strategicRouteHandler.getStccMmsi() != null) {
            commsPanel.activateChat(strategicRouteHandler.getStccMmsi().intValue());
        } else {
            commsPanel.deactivateChat();
        }

    }

    @Override
    public void chatMessageReceived(MaritimeId senderId, ChatServiceMessage message) {
        int id = MaritimeCloudUtils.toMmsi(senderId);
        String senderName = getActorName(id);
        String chatMessage = Formatter.formateTimeFromDate(message.getSendDate()) + " - " + senderName + " : "
                + message.getMessage();

        commsPanel.addChatMessage(chatMessage);

    }

    @Override
    public void chatMessageSent(long recipientId, ChatServiceMessage message) {
        String senderName = "You";
        String chatMessage = Formatter.formateTimeFromDate(message.getSendDate()) + " - " + senderName + " : "
                + message.getMessage();

        commsPanel.addChatMessage(chatMessage);
    }

    private String getActorName(int id) {
        String actorName = id + "";

        // Look up name in identityHandler and aisHandler, if none exists use the given one
        if (identityHandler.actorExists(id)) {
            actorName = identityHandler.getActor(id).getName();
        } else {
            if (aisHandler.getVesselTarget((long) id) != null) {
                if (aisHandler.getVesselTarget((long) id).getStaticData() != null) {
                    actorName = aisHandler.getVesselTarget((long) id).getStaticData().getName();
                }
            }
        }

        return actorName;
    }

}
