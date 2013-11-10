package dk.dma.epd.shore.gui.voct;

public class VOCTCommunicationTableEntry {

    boolean send;
    boolean sarData;
    boolean AO;
    boolean searchPattern;

    public VOCTCommunicationTableEntry(boolean send, boolean sarData,
            boolean aO, boolean searchPattern) {
        this.send = send;
        this.sarData = sarData;
        AO = aO;
        this.searchPattern = searchPattern;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isSarData() {
        return sarData;
    }

    public void setSarData(boolean sarData) {
        this.sarData = sarData;
    }

    public boolean isAO() {
        return AO;
    }

    public void setAO(boolean aO) {
        AO = aO;
    }

    public boolean isSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(boolean searchPattern) {
        this.searchPattern = searchPattern;
    }

}
