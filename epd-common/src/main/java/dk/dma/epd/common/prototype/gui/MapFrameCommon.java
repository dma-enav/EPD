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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

/**
 * Base class for map frames, currently used by EPDShore. 
 * <p>
 * TODO: Move more common functionality to this class
 */
public class MapFrameCommon extends ComponentFrame implements MouseListener {

    private static final long serialVersionUID = 1L;

    private JPanel glassPanel;
    
    /**
     * Constructor
     * @param title the title of the frame
     */
    public MapFrameCommon(String title) {
        super(title, true, true, true, true);

        initGlassPane();
    }
    
    /**
     * Function for initializing the glasspane
     */
    private void initGlassPane() {
        glassPanel = (JPanel) getGlassPane();
        glassPanel.setLayout(null);
        glassPanel.setVisible(false);
    }

    /**
     * Function for getting the glassPanel of the map frame
     * @return glassPanel the glassPanel of the map frame
     */
    public JPanel getGlassPanel() {
        return glassPanel;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mousePressed(MouseEvent arg0) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mouseReleased(MouseEvent arg0) {
    }
}
