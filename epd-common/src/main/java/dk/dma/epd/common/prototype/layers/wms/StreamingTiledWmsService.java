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
package dk.dma.epd.common.prototype.layers.wms;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.bbn.openmap.omGraphics.OMGraphic;
import com.bbn.openmap.omGraphics.OMGraphicList;

public class StreamingTiledWmsService extends TiledWMSService implements Runnable {

    private boolean shouldRun = true;
    CopyOnWriteArrayList<OMGraphic> wmsListSafe = new CopyOnWriteArrayList<>();
    ExecutorService pool =  Executors.newFixedThreadPool(root);
    ExecutorCompletionService<OMGraphicList> completionService = new ExecutorCompletionService<>(pool);
    
    
    public StreamingTiledWmsService(String wmsQuery, int tileNumber) {
        super(wmsQuery,tileNumber);
        
        new Thread(this).start();
    }
    
    @Override
    public void setZoomLevel(float zoom){
        wmsListSafe.clear();
        super.setZoomLevel(zoom);
        needUpdate = true;
    }
    
    
    @Override
    public void setWMSPosition(Double ullon, Double ullat, 
            Double upperLeftLon, 
            Double upperLeftLat, 
            Double lowerRightLon, 
            Double lowerRightLat, 
            int w, int h){
        
        
        wmsListSafe.clear();
        super.setWMSPosition(ullon, ullat, upperLeftLon, upperLeftLat, lowerRightLon, lowerRightLat, w, h);
        needUpdate = true;
    }
    


    @Override
    public OMGraphicList getWmsList() {
        OMGraphicList result = new OMGraphicList();
        //System.out.println(wmsListSafe.size());
        result.addAll(wmsListSafe);
        return result;
    }
    
    

    /**
     * Stop the thread
     */
    public void stop() {
        shouldRun  = false;
    }

    @Override
    public void run() {
        while (shouldRun) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            

            if (needUpdate) {
                System.out.println("heavyTick");
                
                
                
                
                for (SingleWMSService i: wmsInstances) {
                    completionService.submit(i);
                }
                
                for (int i = 0; i< wmsInstances.size(); i++) {
                    Future<OMGraphicList> future;

                    try {
                        future = completionService.take();
                        wmsListSafe.addAll(future.get());
                        needUpdate = false;
                        
                    } catch (InterruptedException | ExecutionException | NullPointerException e ) {
                        
                    }                    
                }
                
                
            } else {
                //System.out.println("lightTick");
            }
        }
        
    }

}
