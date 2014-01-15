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
public class MultiSourcePntComponentPanel extends OMComponentPanel implements IResilientPntDataListener {

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
}
