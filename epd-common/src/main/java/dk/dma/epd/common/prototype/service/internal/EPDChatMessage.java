package dk.dma.epd.common.prototype.service.internal;

import net.maritimecloud.util.Timestamp;
import dma.messaging.MCNotificationSeverity;
import dma.messaging.MaritimeText;

public class EPDChatMessage {

    MaritimeText chatMessage;

    boolean ownMessage;
    Timestamp sendDate;

    public EPDChatMessage(MaritimeText chatMessage, boolean ownMessage, Timestamp sendDate) {
        
        this.chatMessage = chatMessage;
        this.sendDate = sendDate;
        this.ownMessage = ownMessage;
    }

    /**
     * @return the chatMessage
     */
    public MaritimeText getChatMessage() {
        return chatMessage;
    }

    /**
     * @return the ownMessage
     */
    public boolean isOwnMessage() {
        return ownMessage;
    }

    /**
     * @return the sendDate
     */
    public Timestamp getSendDate() {
        return sendDate;
    }

    public String getMsg() {
        return chatMessage.getMsg();
    }

    public MCNotificationSeverity getSeverity() {
        return chatMessage.getSeverity();
    }

    public void setSeverity(MCNotificationSeverity alert) {
        chatMessage.setSeverity(alert);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EPDChatMessage [chatMessage=" + chatMessage + ", ownMessage=" + ownMessage + ", sendDate=" + sendDate
                + ", getChatMessage()=" + getChatMessage() + ", isOwnMessage()=" + isOwnMessage() + ", getSendDate()="
                + getSendDate() + ", getMsg()=" + getMsg() + ", getSeverity()=" + getSeverity() + "]";
    }

}
