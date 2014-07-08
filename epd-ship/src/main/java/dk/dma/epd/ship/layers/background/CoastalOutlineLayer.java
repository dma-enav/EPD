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
package dk.dma.epd.ship.layers.background;

import java.awt.Graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.layer.shape.MultiShapeLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;

/**
 * Layer for simple coastal outline background
 */
public class CoastalOutlineLayer extends MultiShapeLayer {
    
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private Logger LOG;
    
    public CoastalOutlineLayer() {
        super();
        LOG = LoggerFactory.getLogger(CoastalOutlineLayer.class);
    }
    
    public void forceRedraw() {
        doPrepare();
    }
    
    @Override
    public synchronized OMGraphicList prepare() {
        if (!isVisible()) {
            return null;
        }
        
        //long start = System.nanoTime();
        OMGraphicList list = super.prepare();
        //long end = System.nanoTime();
        //LOG.debug("Time to prepare: "+(end-start)/1000000);
        return list;
    }
    
    @Override
    public void paint(Graphics g) {
        //long start = System.nanoTime();
        super.paint(g);
        //long end = System.nanoTime();
        //LOG.debug("Time to paint: "+(end-start)/1000000);
    }
    
}
