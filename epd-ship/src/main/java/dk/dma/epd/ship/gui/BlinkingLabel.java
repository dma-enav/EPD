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
package dk.dma.epd.ship.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import dk.dma.epd.common.util.Util;

/**
 * A blinking label implementation 
 */
public class BlinkingLabel extends JLabel implements Runnable {
    private static final long serialVersionUID = 1L;
    
    private boolean show, blink;
    int period;
    ImageIcon[] images;

    public BlinkingLabel(int period, ImageIcon[] images) {
        this.period = period;
        this.images = images;
        this.setVisible(true);
        new Thread(this).start();
    }

    @Override
    public void run() {
        while(true){
            setIcon(images[0]);
            while (blink) {
                show = !show;
                if (show) {
                    this.setIcon(images[0]);
                } else {
                    this.setIcon(images[1]);
                }
    
                repaint();
                Util.sleep(period);
            }
            Util.sleep(5000);
        }
    }

    public boolean isBlink() {
        return blink;
    }

    public void setBlink(boolean blink) {
        this.blink = blink;
    }

}
