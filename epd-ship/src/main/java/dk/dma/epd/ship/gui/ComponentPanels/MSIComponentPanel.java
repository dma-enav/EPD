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
package dk.dma.epd.ship.gui.ComponentPanels;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.BlinkingLabel;
import dk.dma.epd.ship.gui.Panels.MSIPanel;

public class MSIComponentPanel extends OMComponentPanel implements
IMsiUpdateListener {

    private static final long serialVersionUID = 1L;
    private MsiHandler msiHandler;
    
    private final MSIPanel msiPanel = new MSIPanel();
    private BlinkingLabel msiIcon;
    private JLabel msgLabel;
    private JLabel filterLabel;
    
    
    public MSIComponentPanel() {
        super();
        
//        this.setMinimumSize(new Dimension(10, 55));
        
        msiPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        
        msiIcon = msiPanel.getMsiIcon();
        msgLabel = msiPanel.getMsgLabel();
        filterLabel = msiPanel.getFilter();
        
        setLayout(new BorderLayout(0, 0));
        add(msiPanel, BorderLayout.NORTH);
        setVisible(false);
    }

    
    @Override
    public void findAndInit(Object obj) {

        if (msiHandler == null && obj instanceof MsiHandler) {
            msiHandler = (MsiHandler)obj;
            msiUpdate();
        }
    }


    @Override
    public void msiUpdate() {
        
        if (EPDShip.getSettings().getEnavSettings().isMsiFilter()) {
            msgLabel.setText(Integer.toString(msiHandler.getUnAcknowledgedFilteredMSI()));
        }else{
            msgLabel.setText(Integer.toString(msiHandler.getUnAcknowledgedMSI()));
        }
        
        if (EPDShip.getSettings().getEnavSettings().isMsiFilter()) {
            filterLabel.setText("On");
        }else{
            filterLabel.setText("Off");
        }
//            int firstUnAckFiltered = msiHandler.getFirstNonAcknowledgedFiltered();
//            // There are no MSI to acknowledge
//            if (firstUnAckFiltered != -1) {
//                MsiMessageExtended msiMessageFiltered = msiHandler.getFilteredMessageList().get(firstUnAckFiltered);
////                notifyMsgId = msiMessageFiltered.msiMessage.getMessageId();
//                encText = msiMessageFiltered.msiMessage.getEncText();
//            }
//
//        } else {
//            int firstUnAck = msiHandler.getFirstNonAcknowledged();
//            MsiMessageExtended msiMessage = msiHandler.getMessageList().get(firstUnAck);
////            notifyMsgId = msiMessage.msiMessage.getMessageId();
//            encText = msiMessage.msiMessage.getEncText();
//        }
//        
        

        if (msiHandler.isPendingImportantMessages()) {
            msiIcon.setVisible(true);
            msiIcon.setBlink(true);
    
        } else {
            msiIcon.setBlink(false);
        }
        
        
    }
    

}
