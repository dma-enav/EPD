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

import java.awt.Window;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JDialog;
import javax.swing.SwingConstants;

import com.bbn.openmap.Environment;
import com.bbn.openmap.I18n;
import com.bbn.openmap.LightMapHandlerChild;
import com.bbn.openmap.PropertyConsumer;
import com.bbn.openmap.gui.WindowSupport;

/**
 * Base class for bean-context enabled dialogs.
 * <p>
 * The funcitonality is copied more or less verbatim from {@linkplain ComponentFrame}.
 */
public abstract class ComponentDialog extends JDialog 
    implements PropertyConsumer, BeanContextChild, BeanContextMembershipListener, LightMapHandlerChild {

    private static final long serialVersionUID = 1L;

    protected I18n i18n = Environment.getI18n();

    protected int orientation = SwingConstants.HORIZONTAL;

    protected boolean isolated;

    protected BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(this);

    /**
     * Constructor
     * @param window
     * @param title the title
     */
    protected ComponentDialog(Window window, String title, ModalityType type) {
        super(window, title, type);
    }

    protected WindowSupport windowSupport;

    public void setWindowSupport(WindowSupport ws) {
        windowSupport = ws;
    }

    public WindowSupport getWindowSupport() {
        return windowSupport;
    }

    protected String propertyPrefix;

    @Override
    public void setProperties(java.util.Properties props) {
        setProperties(getPropertyPrefix(), props);
    }

    @Override
    public void setProperties(String prefix, java.util.Properties props) {
        setPropertyPrefix(prefix);
    }

    @Override
    public Properties getProperties(Properties props) {
        if (props == null) {
            props = new Properties();
        }
        return props;
    }

    @Override
    public Properties getPropertyInfo(Properties list) {
        if (list == null) {
            list = new Properties();
        }
        return list;
    }

    @Override
    public void setPropertyPrefix(String prefix) {
        propertyPrefix = prefix;
    }

    @Override
    public String getPropertyPrefix() {
        return propertyPrefix;
    }

    @Override
    public void findAndInit(Object obj) {
    }

    @Override
    public void findAndUndo(Object obj) {
    }

    public void findAndInit(Iterator<?> it) {
        while (it.hasNext()) {
            findAndInit(it.next());
        }
    }

    @Override
    public void childrenAdded(BeanContextMembershipEvent bcme) {
        if (!isolated || bcme.getBeanContext().equals(getBeanContext())) {
            findAndInit(bcme.iterator());
        }
    }

    @Override
    public void childrenRemoved(BeanContextMembershipEvent bcme) {
        Iterator<?> it = bcme.iterator();
        while (it.hasNext()) {
            findAndUndo(it.next());
        }
    }

    @Override
    public BeanContext getBeanContext() {
        return beanContextChildSupport.getBeanContext();
    }

    @Override
    public void setBeanContext(BeanContext in_bc) throws PropertyVetoException {

        if (in_bc != null) {
            if (!isolated || beanContextChildSupport.getBeanContext() == null) {
                in_bc.addBeanContextMembershipListener(this);
                beanContextChildSupport.setBeanContext(in_bc);
                findAndInit(in_bc.iterator());
            }
        }
    }

    @Override
    public void addVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
        beanContextChildSupport.addVetoableChangeListener(propertyName, in_vcl);
    }

    @Override
    public void removeVetoableChangeListener(String propertyName, VetoableChangeListener in_vcl) {
        beanContextChildSupport.removeVetoableChangeListener(propertyName, in_vcl);
    }

    public void fireVetoableChange(String name, Object oldValue, Object newValue) throws PropertyVetoException {
        beanContextChildSupport.fireVetoableChange(name, oldValue, newValue);
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public boolean isIsolated() {
        return isolated;
    }

    public void setIsolated(boolean isolated) {
        this.isolated = isolated;
    }
    
}
