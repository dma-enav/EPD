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
package dk.dma.epd.common.prototype.service;

import dk.dma.epd.common.prototype.notification.Notification.NotificationSeverity;
import dma.messaging.AbstractMCChatMessageService;
import dma.messaging.MCChatMessage;
import dma.messaging.MCChatMessageService;
import dma.messaging.MCNotificationSeverity;
import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.net.MessageHeader;
import net.maritimecloud.net.mms.MmsClient;
import net.maritimecloud.util.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of a Maritime Cloud chat service
 */
public class ChatServiceHandlerCommon extends EnavServiceHandlerCommon {

    private static final Logger LOG = LoggerFactory.getLogger(ChatServiceHandlerCommon.class);

    private List<MCChatMessageService> chatServiceList = new ArrayList<>();
    protected List<IChatServiceListener> listeners = new CopyOnWriteArrayList<>();
    private ConcurrentHashMap<MaritimeId, ChatServiceData> chatMessages = new ConcurrentHashMap<>();

    /**
     * Constructor
     */
    public ChatServiceHandlerCommon() {
        super();

        // Schedule a refresh of the chat services approximately every minute
        scheduleWithFixedDelayWhenConnected(new Runnable() {
            @Override
            public void run() {
                fetchChatServices();
            }
        }, 5, 64, TimeUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(final MmsClient connection) {
        // Refresh the service list
        fetchChatServices();

        // Register a cloud chat service
        try {
            getMmsClient().endpointRegister(new AbstractMCChatMessageService() {
                @Override
                protected void sendMessage(MessageHeader header, MCChatMessage msg) {
                    receiveChatMessage(header.getSender(), msg);
                }
            }).awaitRegistered(4, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            LOG.error("Error hooking up services", e);
        }
    }

    /**
     * Refreshes the list of chat services
     */
    private void fetchChatServices() {
        try {
            chatServiceList = getMmsClient().endpointLocate(MCChatMessageService.class).findAll().get();

            List<MaritimeId> newChatTargets = new ArrayList<>();
            
            // Create an empty chat service data for new chat services
            for (MCChatMessageService chatService : chatServiceList) {
                if (!chatMessages.containsKey(chatService.getRemoteId())) {
                    getOrCreateChatServiceData(chatService.getRemoteId());
                    newChatTargets.add(chatService.getRemoteId());
                    LOG.info("Found new chat serves: " + chatService.getRemoteId());
                }
            }

            // Notify listeners
            for (MaritimeId id : newChatTargets) {
                fireChatMessagesUpdated(id);
            }
        } catch (Exception e) {
            LOG.error("Failed looking up chat services", e.getMessage());
        }
    }

    /**
     * Returns the chat services list
     * 
     * @return the chat services list
     */
    public List<MCChatMessageService> getChatServiceList() {
        return chatServiceList;
    }

    /**
     * Checks the given MMSI in the chat service list
     * 
     * @param id the MMSI of the ship to search for
     * @return if the MMSI supports chat
     */
    public boolean availableForChat(MaritimeId id) {
        return availableForChat(MaritimeCloudUtils.toMmsi(id));
    }
    
    /**
     * Checks the given MMSI in the chat service list
     * 
     * @param mmsi the MMSI of the ship to search for
     * @return if the MMSI supports chat
     */
    public boolean availableForChat(long mmsi) {
        return MaritimeCloudUtils.findServiceWithMmsi(chatServiceList, mmsi) != null;
    }

    /**
     * Sends a chat message to the given ship
     * 
     * @param targetId the id of the ship
     * @param message the message
     * @param severity the severity
     */
    public void sendChatMessage(MaritimeId targetId, String message, NotificationSeverity severity) {

        // Create a new chat message
        MCChatMessage chatMessage = new MCChatMessage();
        chatMessage.setMsg(message);
        chatMessage.setOwnMessage(true);
        switch (severity) {
            case ALERT:  chatMessage.setSeverity(MCNotificationSeverity.ALERT); break;
            case MESSAGE:  chatMessage.setSeverity(MCNotificationSeverity.MESSAGE); break;
            case WARNING:  chatMessage.setSeverity(MCNotificationSeverity.WARNING); break;
        }
        chatMessage.setSendDate(Timestamp.now());

        LOG.info("Sending chat message to maritime id: " + targetId);

        // Store the message
        getOrCreateChatServiceData(targetId).addChatMessage(chatMessage);

        // Find a matching chat end point and send the message
        MCChatMessageService chatMessageService = MaritimeCloudUtils
                .findServiceWithMmsi(chatServiceList, MaritimeCloudUtils.toMmsi(targetId));
        if (chatMessageService != null) {
            chatMessageService.sendMessage(chatMessage);
        } else {
            LOG.error("Could not find chat service for maritime id: " + targetId);
            return;
        }

        // Notify listeners
        fireChatMessagesUpdated(targetId);
    }

    /**
     * Returns all the stored chat messages
     * 
     * @return the chatMessages
     */
    public ConcurrentHashMap<MaritimeId, ChatServiceData> getChatMessages() {
        return chatMessages;
    }

    /**
     * Returns all the stored chat messages for the given maritime id
     * 
     * @param id the maritime id
     * @return the chatMessages
     */
    public ChatServiceData getChatServiceData(MaritimeId id) {
        return chatMessages.get(id);
    }

    /**
     * Returns all the stored chat messages for the given maritime id.
     * Creates a new empty element if it did not exist in advance
     * 
     * @param id the maritime id
     * @return the chatMessages
     */
    public ChatServiceData getOrCreateChatServiceData(MaritimeId id) {
        if (!chatMessages.containsKey(id)) {
            chatMessages.put(id, new ChatServiceData(id));
        }
        return chatMessages.get(id);
    }
    
    /**
     * Called upon receiving a new chat message. Broadcasts the message to all listeners.
     * 
     * @param senderId the id of the sender
     * @param message the message
     */
    protected void receiveChatMessage(MaritimeId senderId, MCChatMessage message) {
        message.setOwnMessage(false);
        getOrCreateChatServiceData(senderId).addChatMessage(message);

        // Notify listeners
        fireChatMessagesUpdated(senderId);
    }
    
    /**
     * Clears the chat message for the given maritime id.
     * 
     * @param id the maritime id
     */
    public void clearChatMessages(MaritimeId id) {
        ChatServiceData chatData = getChatServiceData(id);
        if (chatData != null) {
            chatData.getMessages().clear();
            
            // Notify listeners
            fireChatMessagesUpdated(id);
        }
    }
    
    /**
     * Called when the chat message exchange has been updated for the given maritime id
     * 
     * @param targetId the maritime id of the target
     */
    synchronized void fireChatMessagesUpdated(MaritimeId targetId) {
        for (IChatServiceListener listener : listeners) {
            listener.chatMessagesUpdated(targetId);
        }
    }
    

    /**
     * Adds an chat service listener
     * 
     * @param listener
     *            the listener to add
     */
    public void addListener(IChatServiceListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes an chat service listener
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeListener(IChatServiceListener listener) {
        listeners.remove(listener);
    }

    /**
     * Interface implemented by chat service listeners
     */
    public interface IChatServiceListener {

        /**
         * Called when the chat message exchange has been updated for the given maritime id
         * 
         * @param targetId the maritime id of the target
         */
        void chatMessagesUpdated(MaritimeId targetId);
    }

}
