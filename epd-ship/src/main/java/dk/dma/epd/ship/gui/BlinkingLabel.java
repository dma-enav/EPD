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
