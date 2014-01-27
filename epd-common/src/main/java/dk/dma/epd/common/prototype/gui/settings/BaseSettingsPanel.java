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
package dk.dma.epd.common.prototype.gui.settings;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.gui.settings.ISettingsListener.Type;
import dk.dma.epd.common.prototype.settings.Settings;

/**
 * Abstract base class that may be implemented by settings panels.
 * <p>
 * It provides crude functionality for tracking if the settings were changed.
 */
public abstract class BaseSettingsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(BaseSettingsPanel.class);
    protected CopyOnWriteArrayList<ISettingsListener> listeners = new CopyOnWriteArrayList<>();
    protected String name;
    protected ImageIcon icon;
    protected boolean loaded;

    private Settings settings;
    
    /**
     * Constructor
     * 
     * @param name the name of the settings panel
     */
    public BaseSettingsPanel(String name) {
        this(name, null);
    }
    
    /**
     * Constructor
     * 
     * @param name the name of the settings panel
     * @param icon the icon of the settings panel
     */
    public BaseSettingsPanel(String name, ImageIcon icon) {
        this.name = name;
        this.icon = icon;
    }
    
    /**
     * Returns the name associated with this settings panel
     * @return the name associated with this settings panel
     */
    public String getName() {
        return name;
    }
    
    /**
     * Returns the image icon associated with this settings panel
     * @return the image icon associated with this settings panel
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /**
     * Returns if the settings have been changed since they were loaded
     * @return if the settings have been changed since they were loaded
     */
    public final boolean settingsChanged() {
        if (!loaded) {
            return false;
        }
        return checkSettingsChanged();
    }
    
    /**
     * Returns if the settings have been changed since they were loaded
     * <p>
     * Should be implemented by sub-classes.
     * 
     * @return if the settings have been changed since they were loaded
     */
    protected abstract boolean checkSettingsChanged();

    /**
     * Initializes the UI from the current EPD settings
     */
    public final void loadSettings() {
        doLoadSettings();
        this.loaded = true;
    }

    /**
     * Initializes the UI from the current EPD settings.
     * <p>
     * Should be implemented by sub-classes.
     */
    protected abstract void doLoadSettings();

    /**
     * Saves the settings and notifies listeners if 
     * the settings have changed.
     * @return if the settings were saved
     */
    public final boolean saveSettings() {
        if (settingsChanged()) {
            LOG.info("Settings " + getClass().getName() + " was changed and will be saved");
            doSaveSettings();
            fireSettingsChanged();
            return true;
        }
        return false;
    }
    
    /**
     * Called when the settings panel needs to be saved.
     * <p>
     * Should be implemented by sub-classes.
     */
    protected abstract void doSaveSettings();

    /**
     * Notifies all listeners that the settings were changed
     */
    protected abstract void fireSettingsChanged();
    
    /**
     * Notifies all listeners that settings of the given type have been changed
     * @param type the type settings that have been updated
     */
    protected void fireSettingsChanged(Type type) {
        if (type != null) {
            for (ISettingsListener listener : listeners) {
                listener.settingsChanged(type);
            }
        }
    }
    
    public void setSettings(Settings settings) {
        this.settings = settings;
    }
    
    public Settings getSettings() {
        return this.settings;
    }
    
    /**
     * Utility method that returns if the two objects are identical or not.
     * The comparison works on the string representation of the objects, and so,
     * can only be used for certain comparisons.
     * <p>
     * A blank string representation is identical to {@code null} in this comparison.
     * 
     * @param o1 the first object
     * @param o2 the second object
     * @return true if the string representation of the objects differs
     */
    protected boolean changed(Object o1, Object o2) {
        if (o1 == o2) {
            return false;
        } else if (o1 == null) {
            return !"".equals(o2.toString());
        } else if (o2 == null) {
            return !"".equals(o1.toString());
        } else if (o1 instanceof Number && o2 instanceof Number) {
            return Math.abs(Double.parseDouble(o1.toString()) - Double.parseDouble(o2.toString())) > 0.0001;
        }
        return !o1.toString().equals(o2.toString());
    }
    
    /**
     * Adds a listener that gets called when settings are changed
     * @param listener the listener to add
     */
    public synchronized void addListener(ISettingsListener listener) {
        listeners.addIfAbsent(listener);
    }

    /**
     * Removes a listener 
     * @param listener the listener to remove
     */
    public synchronized void removeListener(ISettingsListener listener) {
        listeners.remove(listener);
    }
}
