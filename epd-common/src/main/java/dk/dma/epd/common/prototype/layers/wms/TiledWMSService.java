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
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Proj;
import com.bbn.openmap.proj.Projection;

public class TiledWMSService extends AbstractWMSService {

    protected int root;
    protected int sqrRoot;

    @SuppressWarnings("unused")
    private Projection projection;

    public TiledWMSService(String wmsQuery, int tileNumber) {
        super(wmsQuery);
        this.root = tileNumber;
        this.sqrRoot = (int) Math.sqrt(root);
    }
    /**
     * get a single tile if width < 1500 otherwise, get four tiles
     * @param p projection
     * @return list of rasterizable image tiles
     */
    protected final Collection<SingleWMSService> getTiles(Projection p) {
        
        if (p.getWidth() > 2000 || p.getHeight() > 1400) {
            return getTiles(p,sqrRoot);
        }
        
        LinkedList<SingleWMSService> l = new LinkedList<>();
        l.add(new SingleWMSService(wmsQuery, p.makeClone()));
        return l;
    }
    
    protected final Collection<SingleWMSService> getTiles(Projection p, int sqrRoot) {
        super.setWMSPosition(p);
        super.setZoomLevel(p.getScale());
        this.setProjection(p);

        // TODO: there is certainly an issue here since i'm overlaying tiles
        // based on 2D geometry. I really should use projection geometry
        Double rectWidth = (lowerRightLon - upperLeftLon) / sqrRoot;
        Double rectHeight = (lowerRightLat - upperLeftLat) / sqrRoot;

        // Double meanRectWidth =
        // CoordinateSystem.GEODETIC.distanceBetween(Position.create(upperLeftLat,upperLeftLon),
        // Position.create(upperLeftLat,lowerRightLon))/sqrRoot;
        // Double meanRectHeight =
        // CoordinateSystem.GEODETIC.distanceBetween(Position.create(upperLeftLat,upperLeftLon),
        // Position.create(lowerRightLat, upperLeftLon))/sqrRoot;

        int rectW = (int) Math.round(wmsWidth / sqrRoot);
        int rectH = (int) Math.round(wmsHeight / sqrRoot);
        
        //rectW = 20;
        //rectH = 20;

        Collection<SingleWMSService> wmsInstances = new ArrayList<>();
        for (int i = 0; i < root; i++) {
            wmsInstances.add(new SingleWMSService(wmsQuery, p));
        }

        Iterator<SingleWMSService> it = wmsInstances.iterator();

        for (int i = 0; i < sqrRoot; i++) {
            Double lonOffset = rectWidth * i;
            for (int j = 0; j < sqrRoot; j++) {
                Double latOffset = rectHeight * j;

                SingleWMSService s = it.next();

                Double minLon = wmsullon + lonOffset;
                //Double maxLon = wmsullon + lonOffset + rectWidth;
                Double minLat = wmsullat + latOffset;
                //Double maxLat = wmsullat + latOffset + rectHeight;
                
                Proj pCurrent = (Proj) p.makeClone();
                
                pCurrent.setWidth(rectW+1); //overlap one pixel
                pCurrent.setHeight(rectH+1);
                pCurrent.setCenter(minLat+rectHeight/2,minLon+rectWidth/2);
                
                s.setProjection(pCurrent);
                s.setWMSPosition(pCurrent);
                s.setZoomLevel(pCurrent);

                // TODO: we cheat here, we add 1 pixel to width and height to
                // overlap all tiles, but we really should change the projection
                // as well
                /*
                s.setWMSPosition(minLon, minLat, minLon, minLat, maxLon, maxLat, rectW -10,
                        rectH -10);

                s.setZoomLevel(p.getScale());
                */
            }
        }

        return wmsInstances;
        
    } 
    
    
    /*
     * Experimented with various pre-fetching strategies
     */
    /*
    protected final Collection<SingleWMSService> getTiles(Projection p) {
        
        LinkedList<SingleWMSService> l = new LinkedList<>();
        
        Projection pCurrent = p.makeClone();
        
        
        int degrees = 45;
        for (int i=0; i<8; i++) {
            pCurrent = p.makeClone();
            Proj pC = (Proj)pCurrent;
            pCurrent.pan(degrees*i);
            
            l.add(new SingleWMSService(wmsQuery, pCurrent.makeClone()));
        }
        
        return l;
    }*/
    
    private void setProjection(final Projection p) {
        this.projection = p;

    }

    @Override
    public OMGraphicList getWmsList(Projection p) {

        OMGraphicList result = new OMGraphicList();

        // why not make this a field? because we want total separation between
        // results
        ExecutorService es = Executors.newCachedThreadPool();

        Collection<SingleWMSService> wmsInstances = this.getTiles(p);

        try {
            List<Future<OMGraphicList>> futures = es.invokeAll(wmsInstances,
                    10, TimeUnit.SECONDS);

            for (Future<OMGraphicList> f : futures) {
                try {
                    result.addAll(f.get());
                    System.out.println("added tile");
                } catch (CancellationException e) {
                    LOG.debug("WMS TILE CANCELLED");
                }

            }

        } catch (InterruptedException | ExecutionException e) {

        }

        // Singlethreaded alternative
        /*
         * for (SingleWMSService s: wmsInstances) {
         * result.addAll(s.getWmsList(p)); }
         */

        return result;

    }

}
