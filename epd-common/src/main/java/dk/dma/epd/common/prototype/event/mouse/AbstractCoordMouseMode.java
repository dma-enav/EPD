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
package dk.dma.epd.common.prototype.event.mouse;

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.AbstractMouseMode;
import com.bbn.openmap.proj.coords.LatLonPoint;

public class AbstractCoordMouseMode extends AbstractMouseMode implements PropertyChangeListener {

    /**
     * Private fields.
     */
    private static final long serialVersionUID = 1L;
    private Set<IMapCoordListener> coordListeners;
    
    /**
     * Constructs a new AbstractCoordMouseMode.
     * @param mouseId
     *          The ID for the mouse mode.
     * @param shouldComsumeEvents
     *        the mode setting, where the mousemode should
     *        pass the events on to other listeners or not, depending if one of
     *        the listeners used it or not.
     */
    public AbstractCoordMouseMode(String mouseId, boolean shouldComsumeEvents) {
        super(mouseId, shouldComsumeEvents);
        this.coordListeners = new HashSet<IMapCoordListener>();
    }
    
    /**
     * Sends the mouse event point(x, y) to the IMapListeners, if the Set contains
     * any.
     * @param e
     *          The mouse event fired.
     */
    private void fireMouseLocation(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        LatLonPoint llp = null;
        
        // If the Set contains any elements, notify them of the mouse event position.
        if (this.coordListeners.size() > 0) {
            llp = ((MapBean) e.getSource()).getProjection().inverse(x, y);
            if (e.getSource() instanceof MapBean) {
                for (IMapCoordListener listener : this.coordListeners) {
                    listener.receiveCoord(llp);
                }
            }
        }
    }
    
    /**
     * Fires a mouse location and calls the super class method which class the
     * MouseSuport method.
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        this.fireMouseLocation(e);
        super.mouseMoved(e);
    }
    
    /**
     * Fires a mouse location to the IMapListeners, but does not call the super class
     * method for mouseDragged(), since it prevents route editing and zooming.
     */
    @Override
    public void mouseDragged(MouseEvent e) {
       this.fireMouseLocation(e); 
    }
    
    /**
    * Called when a CoordMouseMode is added to a BeanContext, or when another
    * object is added to the BeanContext after that. The CoordMouseMode looks
    * for an InformationDelegator to use to fire the coordinate updates. If
    * another InforationDelegator is added when one is already set, the later
    * one will replace the current one.
    * 
    * @param someObj an object being added to the BeanContext.
    */
    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof IMapCoordListener) {
            this.coordListeners.add((IMapCoordListener) obj);
        }
    }
    
    /**
     * BeanContextMembershipListener method. Called when objects have been
     * removed from the parent BeanContext. If an InformationDelegator is
     * removed from the BeanContext, and it's the same one that is currently
     * held, it will be removed.
     * 
     * @param someObj an object being removed from the BeanContext.
     */
    @Override
    public void findAndUndo(Object someObj) {
        if (someObj instanceof IMapCoordListener) {
            this.coordListeners.remove(someObj);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(String prefix, Properties props) {
        super.setProperties(prefix, props);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getProperties(Properties props) {
        props = super.getProperties(props);
        return props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Properties getPropertyInfo(Properties props) {
        props = super.getPropertyInfo(props);
        return props;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }
}
