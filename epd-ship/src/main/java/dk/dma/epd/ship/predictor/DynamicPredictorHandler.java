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
package dk.dma.epd.ship.predictor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.sensor.predictor.DynamicPredictorData;
import dk.dma.epd.common.prototype.sensor.predictor.IDynamicPredictorListener;
import net.jcip.annotations.ThreadSafe;

/**
 * Class for handling and distributing dynamic prediction information
 */
@ThreadSafe
public class DynamicPredictorHandler extends MapHandlerChild implements Runnable, IDynamicPredictorListener {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicPredictorHandler.class);
    
    public DynamicPredictorHandler() {
        EPD.startThread(this, "DynamicPredictorHandler");
    }
    
    @Override
    public void dynamicPredictorUpdate(DynamicPredictorData dynamicPredictorData) {
        LOG.info("Received dynamic preditor data");
        
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub

    }


}
