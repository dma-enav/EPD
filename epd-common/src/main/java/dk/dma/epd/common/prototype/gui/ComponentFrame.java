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
package dk.dma.epd.common.prototype.gui;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JInternalFrame;
import javax.swing.SwingConstants;

import com.bbn.openmap.Environment;
import com.bbn.openmap.I18n;
import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.PropertyConsumer;
import com.bbn.openmap.gui.WindowSupport;

/**
 * Abstract base class for frames that are also components
 */
public abstract class ComponentFrame extends JInternalFrame implements PropertyConsumer, BeanContextChild, BeanContextMembershipListener,
        LightMapHandlerChild {

    private static final long serialVersionUID = 1L;

    protected I18n i18n = Environment.getI18n();

    protected int orientation = SwingConstants.HORIZONTAL;

    protected boolean isolated;

    protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(this);

    protected WindowSupport windowSupport;

    protected String propertyPrefix;

    /**
     * Constructor
     */
    protected ComponentFrame() {
        super();
    }

    /**
     * Constructor used in creating gui elements with arguments
     * @param title Title
     * @param resizable Resizable
     * @param closable Closeable
     * @param maximizable Maximizable
     * @param iconifiable Iconifiable
     */
    protected ComponentFrame(String title, boolean resizable, boolean closable, boolean maximizable, boolean iconifiable) {
        super( title,  resizable,  closable,  maximizable,  iconifiable);
    }

    /**
     * Add listener
     */
    public void addVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
        beanContextChildSupport.addVetoableChangeListener(propertyName, in_vcl);
    }

    /**
     * Event on children added
     */
    public void childrenAdded(BeanContextMembershipEvent bcme) {
        if (!isolated || bcme.getBeanContext().equals(getBeanContext())) {
            findAndInit(bcme.iterator());
        }
    }

    /**
     * Child element removed event
     */
    public void childrenRemoved(BeanContextMembershipEvent bcme) {
        Iterator<?> it = bcme.iterator();
        while (it.hasNext()) {
            findAndUndo(it.next());
        }
    }

    /**
     * Find and init bean function used in initializing other classes
     */
    public void findAndInit(Iterator<?> it) {
        while (it.hasNext()) {
            findAndInit(it.next());
        }
    }

    public void findAndInit(Object obj) {
    }

    public void findAndUndo(Object obj) {
    }

    /**
     * Fire vetoable change
     */
    public void fireVetoableChange(String name, Object oldValue, Object newValue) throws PropertyVetoException {
        beanContextChildSupport.fireVetoableChange(name, oldValue, newValue);
    }

    /**
     * Return bean context
     */
    public BeanContext getBeanContext() {
        return beanContextChildSupport.getBeanContext();
    }

    /**
     * Return orientation
     * @return
     */
    public int getOrientation() {
        return orientation;
    }

    /**
     * Return properties
     */
    public Properties getProperties(Properties props) {
        if (props == null) {
            props = new Properties();
        }
        return props;
    }

    /**
     * Get property info
     */
    public Properties getPropertyInfo(Properties list) {
        if (list == null) {
            list = new Properties();
        }
        return list;
    }

    /**
     * Get property prefix
     */
    public String getPropertyPrefix() {
        return propertyPrefix;
    }

    /**
     * Get windowSupport
     * @return
     */
    public WindowSupport getWindowSupport() {
        return windowSupport;
    }

    /**
     * Get isIsolated
     * @return
     */
    public boolean isIsolated() {
        return isolated;
    }

    /**
     * Remove toable Change listeners
     */
    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
        beanContextChildSupport.removeVetoableChangeListener(propertyName, in_vcl);
    }

    /**
     * Set the bean context
     */
    public void setBeanContext(BeanContext in_bc) throws PropertyVetoException {

        if (in_bc != null) {
            if (!isolated || beanContextChildSupport.getBeanContext() == null) {
                in_bc.addBeanContextMembershipListener(this);
                beanContextChildSupport.setBeanContext(in_bc);
                findAndInit(in_bc.iterator());
            }
        }
    }

    /**
     * Set isolated
     * @param isolated
     */
    public void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }

    /**
     * Set orientation
     * @param orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    /**
     * Set properties
     */
    public void setProperties(java.util.Properties props) {
        setProperties(getPropertyPrefix(), props);
    }

    /**
     * Set properties
     */
    public void setProperties(String prefix, java.util.Properties props) {
        setPropertyPrefix(prefix);

        // String realPrefix =
        // PropUtils.getScopedPropertyPrefix(prefix);
    }

    /**
     * Set propertyrefix
     */
    public void setPropertyPrefix(String prefix) {
        propertyPrefix = prefix;
    }

    /**
     * Set window support
     * @param ws
     */
    public void setWindowSupport(WindowSupport ws) {
        windowSupport = ws;
    }
}
