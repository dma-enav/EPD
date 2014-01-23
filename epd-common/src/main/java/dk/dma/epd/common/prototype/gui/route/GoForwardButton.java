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
package dk.dma.epd.common.prototype.gui.route;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;

import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.event.HistoryPosition;
import dk.dma.epd.common.prototype.gui.views.CommonChartPanel;

/**
 * 
 * @author adamduehansen
 *
 */
public class GoForwardButton extends ButtonLabelCommon implements MouseListener {
    
    private static final long serialVersionUID = 1L;
    private HistoryListener historyListener;
    private CommonChartPanel chartPanel;
    private GoBackButton goBackButton;

    public GoForwardButton(ImageIcon icon) {
        super(icon);
        this.addMouseListener(this);
    }
    
    public void setHistoryListener(HistoryListener historyListener) {
        this.historyListener = historyListener;
    }
    
    public void setChartPanel(CommonChartPanel chartPanel) {
        this.chartPanel = chartPanel;
    }
    
    public void setBackButton(GoBackButton backButton) {
        this.goBackButton = backButton;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        HistoryPosition hpos = historyListener.goOneElementForward();
        
        if (hpos != null) {
            chartPanel.goToPosition(hpos.getPosition());
            chartPanel.getMap().setScale(hpos.getZoomScale());
            this.toogleButton();
        }
    }
    
    private void toogleButton() {
        if (this.historyListener.containsElements()) {
            this.goBackButton.setEnabled(true);
        }
        
        if (this.historyListener.isAtHighestElement()) {
            this.setEnabled(false);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
