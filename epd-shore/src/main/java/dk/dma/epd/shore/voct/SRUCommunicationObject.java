package dk.dma.epd.shore.voct;

import java.util.ArrayList;
import java.util.List;

public class SRUCommunicationObject {

    
    SRU sru;
    List<Object> sruCommunicationObjects = new ArrayList<>();
    
    public SRUCommunicationObject(SRU sru){
        this.sru = sru;
    }

    public SRU getSru() {
        return sru;
    }
    
    

}
