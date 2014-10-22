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
package dk.dma.epd.common.prototype.layers.tiles;

import java.util.Properties;

import com.bbn.openmap.layer.imageTile.MapTileLayer;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

public class TileLayer extends MapTileLayer {

    private static final long serialVersionUID = 1L;

    public TileLayer() {
        super();

        Properties tileProperties = new Properties();
        tileProperties.setProperty("noCoverageZoom", "15");
        tileProperties.setProperty("rootDir", "E:/Sjaelland/");
        tileProperties.setProperty("attribution", "Map provided MapQuest");
        this.setProperties(tileProperties);

//         this.setZoomLevel(10);
    }

    @Override
    public void setZoomLevel(int zoomlevel) {
        System.out.println("Zoom level set to " + zoomLevel);
        super.setZoomLevel(zoomlevel);
    }

    /**
     * OMGraphicHandlerLayer method, called with projection changes or whenever else doPrepare() is called. Calls getTiles on the
     * map tile factory.
     * 
     * @return OMGraphicList that contains tiles to be displayed for the current projection.
     */
    @Override
    public synchronized OMGraphicList prepare() {

        Projection projection = getProjection();

        if (projection == null) {
            return null;
        }

        if (tileFactory != null) {
            OMGraphicList newList = new OMGraphicList();
            setList(newList);

            System.out.println("Zoom level stuff is " + zoomLevel);
            return tileFactory.getTiles(projection, zoomLevel, newList);
        }
        return null;
    }

}
