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

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

public class StreamingTiledWmsService extends TiledWMSService implements
        Runnable, AsyncWMSService {

    private boolean shouldRun = true;
    ConcurrentHashMap<String, OMGraphicList> cache = new ConcurrentHashMap<String, OMGraphicList>();
    ConcurrentHashMap<String, OMGraphicList> tmpCache = new ConcurrentHashMap<String, OMGraphicList>();
    LinkedBlockingDeque<Projection> projectionJobs = new LinkedBlockingDeque<>(
            1);

    private Thread t;
    private ExecutorService handOffPool = Executors.newFixedThreadPool(1);

    public StreamingTiledWmsService(String wmsQuery, int tileNumber) {
        super(wmsQuery, tileNumber);

        this.t = new Thread(this);
        this.t.start();
    }
    
    public StreamingTiledWmsService(String wmsQuery, int tileNumber, ConcurrentHashMap<String, OMGraphicList> sharedCache) {
        super(wmsQuery, tileNumber);
        this.cache = sharedCache;
        
        this.t = new Thread(this);
        this.t.start();
    }

    @Override
    public OMGraphicList getWmsList(Projection p) {
        final OMGraphicList result = new OMGraphicList();
        final String key = getID(p);
        if (cache.containsKey(key)) {
            // LOG.debug("CACHE HIT");
            tmpCache.remove(key);
            result.addAll(cache.get(key));
        } else if (tmpCache.containsKey(key)) {
            // LOG.debug("TMPCACHE HIT");
            result.addAll(tmpCache.get(key));
        }

        return result;
    }

    /**
     * Stop the thread
     */
    public void stop() {
        shouldRun = false;
    }

    @Override
    public void run() {
        while (shouldRun) {
            Projection job = null;
            try {
                // blocks until projection bbox job ready
                job = projectionJobs.takeLast();

                Thread.sleep(1000); // only take jobs every second
                // LOG.debug("JOB TAKEN");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (cache.containsKey(getID(job))) {
                // LOG.debug("CACHE HIT!");
            } else {
                asyncDownload(job);
            }
        }
    }

    /**
     * Complete asynchronous download and update of caches
     * 
     * @param job
     */
    public void asyncDownload(final Projection job) {
        handOffPool.execute(new Runnable() {

            @Override
            public void run() {
                Collection<SingleWMSService> workers = getTiles(job);
                OMGraphicList result = new OMGraphicList();

                ExecutorService pool = Executors.newFixedThreadPool(4);
                ExecutorCompletionService<OMGraphicList> completionService = new ExecutorCompletionService<>(
                        pool);

                for (SingleWMSService w : workers) {
                    completionService.submit(w);
                }

                boolean allSuccess = true;
                for (int i = 0; i < workers.size(); i++) {
                    Future<OMGraphicList> future;
                    try {
                        future = completionService.poll(Math.max(100,5000/(i+1)),
                                TimeUnit.MILLISECONDS);
                        OMGraphicList tile = future.get();
                        result.addAll(tile);
                        tmpCache.putIfAbsent(getID(job), new OMGraphicList());
                        tmpCache.get(getID(job)).addAll(tile);
                    } catch (InterruptedException | ExecutionException
                            | NullPointerException e) {
                        allSuccess = false;
                        //LOG.debug("A Tile failed to download within the alotted time. (~5000ms)");
                    }
                }

                if (allSuccess) {
                    tmpCache.remove(getID(job));
                    cache.put(getID(job), result);
                } else {
                    queue(job);
                }

            }
        });


    }

    public void queue(Projection p) {
        //p = normalizeProjection(p);
        if (this.projectionJobs.offer(p)) {

        } else {
            LOG.debug("Queue is full, kicking old job in favor of new");
            try {
                this.projectionJobs.takeFirst();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            this.projectionJobs.offer(p);
        }

    }

    // meh!
    public String getBbox(Projection p) {
        String meh = (new SingleWMSService(wmsQuery, p)).getBbox();
        // System.out.println(meh);
        return meh;
    }

    public String getID(Projection p) {
        return getBbox(p)+Math.round(p.getScale());
    }

}
