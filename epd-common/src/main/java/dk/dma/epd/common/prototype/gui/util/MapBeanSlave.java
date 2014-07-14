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
package dk.dma.epd.common.prototype.gui.util;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;

/**
 * A bean slave that automatically updates position whenever projection changes on source bean map
 * 
 * @author jtj-sfs
 */
public class MapBeanSlave implements ProjectionListener{

    protected MapBean sourceBean;
    protected MapBean targetBean;
    
    MapBeanSlave(MapBean sourceBean, MapBean targetBean) {
        this.sourceBean = sourceBean;
        this.targetBean = targetBean;
        
        this.targetBean.setScale(sourceBean.getScale());
        this.targetBean.setCenter(sourceBean.getCenter());
        
        sourceBean.addProjectionListener(this);
        
    }
    
    @Override
    public void projectionChanged(ProjectionEvent arg0) {
        targetBean.setScale(sourceBean.getScale());
        targetBean.setCenter(sourceBean.getCenter());
        
        targetBean.setSize(sourceBean.getSize());
    }
    
    
    

}
