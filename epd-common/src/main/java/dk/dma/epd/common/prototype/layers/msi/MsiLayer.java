package dk.dma.epd.common.prototype.layers.msi;

import java.awt.event.MouseEvent;

import com.bbn.openmap.event.MapMouseListener;
import com.bbn.openmap.layer.OMGraphicHandlerLayer;

public abstract class MsiLayer extends OMGraphicHandlerLayer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public abstract void doUpdate();
    

}
