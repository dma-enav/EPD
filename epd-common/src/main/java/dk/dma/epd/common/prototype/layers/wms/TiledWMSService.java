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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.omGraphics.OMGraphicList;

public class TiledWMSService extends AbstractWMSService {
    private static final Logger LOG = LoggerFactory
            .getLogger(TiledWMSService.class);
    
    protected ArrayList<SingleWMSService> wmsInstances = new ArrayList<>();
    protected int root;
    private int sqrRoot;
    protected volatile boolean needUpdate;
    
    public TiledWMSService(String wmsQuery, int tileNumber) {
        super(wmsQuery);
        
        for (int i=0; i<tileNumber; i++) {
            wmsInstances.add(new SingleWMSService(wmsQuery));
        }
        
        this.root = tileNumber;
        this.sqrRoot = (int) Math.sqrt(root);
    }
    
    @Override
    public void setZoomLevel(float zoom){
        super.setZoomLevel(zoom);
     
        for (SingleWMSService i: wmsInstances) {
            i.setZoomLevel(zoomLevel);
        }
        
        needUpdate = true;
        
    }
    
    @Override
    public void setWMSPosition(Double ullon, Double ullat, 
            Double upperLeftLon, 
            Double upperLeftLat, 
            Double lowerRightLon, 
            Double lowerRightLat, 
            int w, int h){
        
        super.setWMSPosition(ullon, ullat, upperLeftLon, upperLeftLat, lowerRightLon, lowerRightLat, w, h);
        
        Double rectWidth = (lowerRightLon-upperLeftLon)/sqrRoot;
        Double rectHeight = (lowerRightLat-upperLeftLat)/sqrRoot;
        
        int rectW = (int)Math.round(wmsWidth/sqrRoot);
        int rectH = (int)Math.round(wmsHeight/sqrRoot);
        
        Iterator<SingleWMSService>it = wmsInstances.iterator();
        for (int i=0; i<sqrRoot; i++){
            Double lonOffset = rectWidth*i;
            for (int j=0; j<sqrRoot; j++) {
                Double latOffset = rectHeight*j;

                AbstractWMSService s = it.next();
                
                Double minLon = wmsullon+lonOffset;
                Double maxLon = wmsullon+lonOffset+rectWidth;
                Double minLat = wmsullat+latOffset;
                Double maxLat = wmsullat+latOffset+rectHeight;
                
                s.setWMSPosition(minLon+rectWidth/2,minLat+rectHeight/2,
                        minLon, minLat, maxLon, maxLat,rectW,rectH);
            }
        }
        
        needUpdate = true;
    }
    
    
    @Override
    public OMGraphicList getWmsList() {                
        if (!needUpdate) {
            return wmsList;
        }
        
        OMGraphicList result = new OMGraphicList(); 
        
        //why not make this a field? because we want total separation between results
        ExecutorService es = Executors.newCachedThreadPool();
        
        try {
            List<Future<OMGraphicList>> futures = es.invokeAll(wmsInstances, 10, TimeUnit.SECONDS);
            
            
            for (Future<OMGraphicList> f: futures) {
                try {
                    result.addAll(f.get());
                    System.out.println("added tile");
                } catch (CancellationException e) {
                    LOG.debug("WMS TILE CANCELLED");
                }
                
                
            }
            
            needUpdate = false;
            
            
            
        } catch (InterruptedException | ExecutionException e ) {
            
        }
    
        // Singlethreaded alternative
        /*
        for (SingleWMSService s: wmsInstances) {
            result.addAll(s.getWmsList());
        }
        */
        
        wmsList = result;
        
        return result;
     
    }
    
    

}
