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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.sensor.rpnt.IResilientPntDataListener;
import dk.dma.epd.common.prototype.sensor.rpnt.MultiSourcePntHandler;
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData;
import dk.dma.epd.ship.gui.panels.MultiSourcePntPanel;

/**
 * Panel that displays the status of the multi-source PNT
 */
public class MultiSourcePntComponentPanel extends OMComponentPanel implements IResilientPntDataListener, DockableComponentPanel {

    private static final long serialVersionUID = 1L;

    private MultiSourcePntPanel msPntPanel = new MultiSourcePntPanel();
    private MultiSourcePntHandler msPntHandler;
    
    /**
     * Constructor
     */
    public MultiSourcePntComponentPanel() {
        super();
        
        msPntPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        
        setLayout(new BorderLayout(0, 0));
        add(msPntPanel, BorderLayout.NORTH);
        setVisible(false);
    }
    
    /**
     * Called upon receiving a new {@code ResilientPntData} update
     * @param rpntData the updated {@code ResilientPntData} data
     */
    @Override
    public void rpntDataUpdate(ResilientPntData rpntData) {
        // Update the panel
        msPntPanel.setRpntData(rpntData);
    }

    /**
     * Called when a new bean is set on the context
     * @param obj the bean
     */
    @Override
    public void findAndInit(Object obj) {
        super.findAndInit(obj);
        if (msPntHandler == null && obj instanceof MultiSourcePntHandler) {
            msPntHandler = (MultiSourcePntHandler)obj;
            msPntHandler.addResilientPntDataListener(this);
        }
    }

    /**
     * Called when a bean is remove from the context
     * @param obj the bean
     */
    @Override
    public void findAndUndo(Object obj) {
        if (obj == msPntHandler) {
            msPntHandler.removeResilientPntDataListener(this);
            msPntHandler = null;
        }
        super.findAndUndo(obj);
    }

    /****************************************/
    /** DockableComponentPanel methods     **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "Resilient PNT";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInDefaultLayout() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInPanelsMenu() {
        return true;
    }
}
