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
package dk.dma.epd.shore.gui.views;

import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContext;
import java.beans.beancontext.BeanContextChild;
import java.beans.beancontext.BeanContextChildSupport;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import com.bbn.openmap.LightMapHandlerChild;

import dk.dma.epd.common.prototype.gui.SetupDialogCommon;
import dk.dma.epd.common.prototype.settings.Settings;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.gui.settingtabs.ShoreAisSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreCloudSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreConnectionSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreMapSettingsPanel;
import dk.dma.epd.shore.gui.settingtabs.ShoreMapFramesSettingsPanel;
import dk.dma.epd.shore.services.shore.ShoreServices;

public class SetupDialogShore extends SetupDialogCommon implements LightMapHandlerChild, BeanContextChild, BeanContextMembershipListener {
    
    private static final long serialVersionUID = 1L;
    
    private BeanContextChildSupport beanContextChildSupport = new BeanContextChildSupport(this);
    
    private ShoreCloudSettingsPanel shoreSettings;
    private ShoreMapSettingsPanel mapSettings;
    private ShoreAisSettingsPanel aisSettings;
    private ShoreMapFramesSettingsPanel windowsSettings;
    private ShoreConnectionSettingsPanel connectionPanel;
    
    public SetupDialogShore(JFrame mainFrame) {
        
        super(mainFrame, "Setup", JTabbedPane.LEFT);
        
        // Resize the dialog to make more room for tabs on the right side.
        this.setSize(800, super.getHeight()-200);
        
        this.connectionPanel = new ShoreConnectionSettingsPanel();
        this.shoreSettings   = new ShoreCloudSettingsPanel();
        this.mapSettings     = new ShoreMapSettingsPanel();
        this.windowsSettings = new ShoreMapFramesSettingsPanel();
        this.aisSettings     = new ShoreAisSettingsPanel();
        
        // Register the panels for shore setup.
        super.registerSettingsPanels(
                this.connectionPanel,
                this.shoreSettings,
                this.mapSettings,
                this.windowsSettings,
                this.aisSettings
                );
        
        super.setActivePanel(1);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean b) {
    	
    	super.getAcceptButton().setEnabled(!b);
    	super.getHandlerTimer().start();
    	super.setVisible(b);
    }
    
    /**
     * {@inheritDoc}
     */
    public void loadSettings(Settings settings) {
        super.loadSettings(settings);
    }
    
    public void findAndInitialize(Iterator<?> it) {
    	while(it.hasNext()) {
    		findAndInit(it.next());
    	}
    }

    /**
     * {@inheritDoc}
     */
	@Override
	public void findAndInit(Object obj) {
				
		if (obj instanceof AisHandler) {
			// To get status about the AIS targets. 
			connectionPanel.addStatusComponent((AisHandler) obj);
			
		} else if (obj instanceof ShoreServices) {
			// To get status about Shore Services.
			connectionPanel.addStatusComponent((ShoreServices) obj);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findAndUndo(Object obj) {}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanContext(BeanContext bc) throws PropertyVetoException {
				
		if (bc != null) {
			if (this.beanContextChildSupport.getBeanContext() == null) {
				
				bc.addBeanContextMembershipListener(this);
				this.beanContextChildSupport.setBeanContext(bc);
				findAndInitialize(bc.iterator());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BeanContext getBeanContext() {
		return this.beanContextChildSupport.getBeanContext();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addVetoableChangeListener(String name,
			VetoableChangeListener vcl) {
		
		this.beanContextChildSupport.addVetoableChangeListener(name, vcl);	
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeVetoableChangeListener(String name,
			VetoableChangeListener vcl) {
		
		this.beanContextChildSupport.removeVetoableChangeListener(name, vcl);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void childrenAdded(BeanContextMembershipEvent bcme) {

		if (bcme.getBeanContext().equals(getBeanContext())) {	
			findAndInitialize(bcme.iterator());
		}		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void childrenRemoved(BeanContextMembershipEvent bcme) {
		
		Iterator<?> it = bcme.iterator();
		
		while (it.hasNext()) {
			findAndUndo(it.next());			
		}
	}

	/**
	 * Sets the panel tabs to show map frames settings and sets the
	 * tabbed pane to a specific map frame. 
	 * @param activeMapWindow
	 */
	public void goToSpecifMapSettings(JMapFrame activeMapWindow) {
		
		// Go to the map frames settings.
		super.setActivePanel(4);
		this.windowsSettings.showSettingsFor(activeMapWindow);
		
	}
}
