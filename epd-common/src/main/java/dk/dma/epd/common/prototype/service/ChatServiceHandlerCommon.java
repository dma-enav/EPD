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
package dk.dma.epd.common.prototype.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.enavcloud.ChatService;
import dk.dma.epd.common.prototype.enavcloud.ChatService.ChatServiceMessage;
import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.service.ServiceEndpoint;
import net.maritimecloud.net.service.invocation.InvocationCallback;

/**
 * An implementation of a Maritime Cloud chat service
 */
public class ChatServiceHandlerCommon extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServiceHandlerCommon.class);
    
    private List<ServiceEndpoint<ChatServiceMessage, Void>> chatServiceList = new ArrayList<>();
    
    /**
     * Constructor
     */
    public ChatServiceHandlerCommon() {
        super();
        
        // Schedule a refresh of the chat services approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override public void run() {
                fetchChatServices();
            }}, 5, 64, TimeUnit.SECONDS);        
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        // Refresh the service list
        fetchChatServices();

        // Register a cloud route suggestion service
        try {
            getMaritimeCloudConnection().serviceRegister(
                    ChatService.INIT,
                    new InvocationCallback<ChatServiceMessage, Void>() {
                        public void process(
                                ChatServiceMessage message,
                                Context<Void> context) {

                            getStatus().markCloudReception();
                            
                            receiveChatMessage(context.getCaller(), message);
                        }
                    }).awaitRegistered(4, TimeUnit.SECONDS);
            
        } catch (InterruptedException e) {
            getStatus().markFailedSend();
            getStatus().markFailedReceive();
            LOG.error("Error hooking up services", e);
        }
    }
    
    /**
     * Refreshes the list of chat services
     */
    private void fetchChatServices() {
        try {
            chatServiceList = getMaritimeCloudConnection().serviceLocate(ChatService.INIT).nearest(Integer.MAX_VALUE).get();
        } catch (Exception e) {
            getStatus().markFailedReceive();
            LOG.error("Failed looking up route suggestion services", e.getMessage());
        }
    }

    /**
     * Returns the chat services list
     * @return the chat services list
     */
    public List<ServiceEndpoint<ChatServiceMessage, Void>> getChatServiceList() {
        return chatServiceList;
    }

    /**
     * Checks for a ship with the given mmsi in the chat service list
     * 
     * @param mmsi the mmsi of the ship to search for
     * @return if one such ship is available
     */
    public boolean shipAvailableForChatSuggestion(int mmsi) {
        return MaritimeCloudUtils.findServiceWithMmsi(chatServiceList, mmsi) != null;
    }

    /**
     * Sends a chat message to the given ship
     * 
     * @param mmsi the mmsi of the ship
     * @param message the message
     * @param sender the sender
     */
    public void sendChatMessage(MaritimeId targetId, String message, String sender)  {

        Integer mmsi = MaritimeCloudUtils.toMmsi(targetId);
        long id = System.currentTimeMillis();
        
        // Find a matching chat end point
        ServiceEndpoint<ChatServiceMessage, Void> end 
            = MaritimeCloudUtils.findServiceWithMmsi(chatServiceList, (int)mmsi);

        // Create a new chat message
        ChatServiceMessage chatMessage = new ChatServiceMessage(message, id, System.currentTimeMillis(), sender);

        LOG.info("Sending chat messasge to mmsi: " + mmsi + " with ID: " + chatMessage.getId());

        if (end != null) {
            end.invoke(chatMessage);
        } else {
            // notifyRouteExchangeListeners();
            LOG.error("Could not find chat service for MMSI " + mmsi);
        }

    }
    
    /**
     * Called upon receiving a new chat message
     * 
     * @param senderId the id of the sender
     * @param message the message
     */
    protected void receiveChatMessage(MaritimeId senderId, ChatServiceMessage message) {
        System.out.println("*** RECEIVED MESSAGE " + message);
    }
}
