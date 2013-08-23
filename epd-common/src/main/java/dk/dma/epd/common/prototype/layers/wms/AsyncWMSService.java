package dk.dma.epd.common.prototype.layers.wms;

import com.bbn.openmap.proj.Projection;

public interface AsyncWMSService {
    void queue(Projection p);
}
