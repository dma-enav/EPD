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
package dk.dma.epd.common.prototype.service;

import com.bbn.openmap.MapHandlerChild;

import net.maritimecloud.net.MaritimeCloudClient;
import dk.dma.epd.common.prototype.service.MaritimeCloudServiceCommon.IMaritimeCloudListener;
import dk.dma.epd.common.prototype.status.CloudStatus;


/**
 * Abstract base for all e-Navigation services.
 * <p>
 * The base implementation will hook up to the {@linkplain MaritimeCloudServiceCommon}
 * and register as a listener
 */
public abstract class EnavServiceHandlerCommon extends MapHandlerChild implements IMaritimeCloudListener {
    
    protected MaritimeCloudServiceCommon maritimeCloudService;

    /**
     * Returns a reference to the {@linkplain MaritimeCloudServiceCommon}
     * @return a reference to the {@linkplain MaritimeCloudServiceCommon}
     */
    public MaritimeCloudServiceCommon getMaritimeCloudService() {
        return maritimeCloudService;
    }
    
    /**
     * Returns a reference to the cloud client connection
     * @return a reference to the cloud client connection
     */
    public MaritimeCloudClient getMaritimeCloudConnection() {
        return (maritimeCloudService == null) ? null : maritimeCloudService.getConnection();
    }
    
    /**
     * Returns a reference to the cloud status 
     * @return a reference to the cloud status 
     */
    public CloudStatus getStatus() {
        return (maritimeCloudService == null) ? null : maritimeCloudService.getStatus();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        
        if (obj instanceof MaritimeCloudServiceCommon) {
            maritimeCloudService = (MaritimeCloudServiceCommon)obj;
            maritimeCloudService.addListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void findAndUndo(Object obj) {        
        
        if (obj instanceof MaritimeCloudServiceCommon) {
            maritimeCloudService.removeListener(this);
            maritimeCloudService = null;
        }
        
        super.findAndUndo(obj);
    }
    
    /****************************************/
    /** IMaritimeCloudListener functions   **/
    /****************************************/
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudConnected(MaritimeCloudClient connection) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudDisconnected() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudError(String error) {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cloudPeriodicTask() {
    }
}
