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
package dk.dma.epd.common.prototype.predictor;

import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.ThreadSafe;
import net.maritimecloud.net.MaritimeCloudClient;
import net.maritimecloud.net.broadcast.BroadcastListener;
import net.maritimecloud.net.broadcast.BroadcastMessageHeader;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.service.EnavServiceHandlerCommon;
import dk.dma.epd.common.prototype.service.MaritimeCloudUtils;

/**
 * Class for handling and distributing dynamic prediction information.
 * Clients can receive notifications by implementing {@link IDynamicPredictionsListener} and registering as an observer of a {@link DynamicPredictorHandlerCommon}.
 */
@ThreadSafe
public class DynamicPredictorHandlerCommon extends EnavServiceHandlerCommon implements Runnable {
    
//    private static final long TIMEOUT = 30 * 1000; // 30 sec

    private final CopyOnWriteArrayList<IDynamicPredictionsListener> listeners = new CopyOnWriteArrayList<>();

    public DynamicPredictorHandlerCommon() {
        super();
        EPD.startThread(this, "DynamicPredictorHandler");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
            // TODO how to check time out for predictions from vessels other than own ship?
//            // Distribute timeout
//            if (System.currentTimeMillis() - lastPrediction > TIMEOUT) {
//                for (IDynamicPredictionsListener listener : listeners) {
//                    listener.receivePredictions(null);
//                }
//            }
        }

    }

    public void addListener(IDynamicPredictionsListener listener) {
        if (listener == this) {
            throw new IllegalArgumentException("Cannot add self as observer of self.");
        }
        listeners.add(listener);
    }

    public void removeListener(IDynamicPredictionsListener listener) {
        listeners.remove(listener);
    }
    
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
        super.cloudConnected(connection);
        // Listen for dynamic prediction broadcasts from cloud
        connection.broadcastListen(DynamicPrediction.class, new BroadcastListener<DynamicPrediction>() {
            @Override
            public void onMessage(BroadcastMessageHeader header,
                    DynamicPrediction prediction) {
                assert MaritimeCloudUtils.toMmsi(header.getId()) == prediction.getMmsi();
                // Notify listeners of dynamic prediction received from cloud.
                System.out.println("RECEIVED DYNAMIC PREDICTION BROADCAST FROM CLOUD");
                DynamicPredictorHandlerCommon.this.publishDynamicPrediction(prediction);
            }
        });
    }
    
    /**
     * Publishes a {@link DynamicPrediction} to all registered listeners.
     * @param prediction The dynamic prediction to publish.
     */
    protected void publishDynamicPrediction(DynamicPrediction prediction) {
        for(IDynamicPredictionsListener listener : this.listeners) {
            listener.receivePredictions(prediction);
        }
    }
    
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
    }
}
