package dk.dma.epd.common.prototype.model.voct.sardata;

import java.io.Serializable;

public class SARTextLogMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private String msg;

    private Boolean priority;

    private Long originalSender;

    private long originalSentDate;

    /**
     * @param msg
     * @param priority
     * @param originalSender
     * @param originalSentDate
     */
    public SARTextLogMessage(String msg, Boolean priority, Long originalSender,
            long originalSentDate) {
        super();
        this.msg = msg;
        this.priority = priority;
        this.originalSender = originalSender;
        this.originalSentDate = originalSentDate;
    }

    public SARTextLogMessage() {

    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg
     *            the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the priority
     */
    public Boolean getPriority() {
        return priority;
    }

    /**
     * @param priority
     *            the priority to set
     */
    public void setPriority(Boolean priority) {
        this.priority = priority;
    }

    /**
     * @return the originalSender
     */
    public Long getOriginalSender() {
        return originalSender;
    }

    /**
     * @param originalSender
     *            the originalSender to set
     */
    public void setOriginalSender(Long originalSender) {
        this.originalSender = originalSender;
    }

    /**
     * @return the originalSentDate
     */
    public long getOriginalSentDate() {
        return originalSentDate;
    }

    /**
     * @param originalSentDate
     *            the originalSentDate to set
     */
    public void setOriginalSentDate(long originalSentDate) {
        this.originalSentDate = originalSentDate;
    }

}
