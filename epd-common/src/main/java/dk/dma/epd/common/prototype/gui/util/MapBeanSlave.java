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
