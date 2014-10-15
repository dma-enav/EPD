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
package dk.dma.epd.shore.gui.views.menuitems;

import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;
import dk.dma.epd.shore.gui.views.JMapFrame;

/**
 * Toggles the visibility of the Layer Toggling window
 */
public class TakeScreenshot extends JMenuItem implements IMapMenuAction {

    private static final long serialVersionUID = 1L;

    private JMapFrame jmapframe;

    public TakeScreenshot(String text) {
        super();
        setText(text);
    }

    @Override
    public void doAction() {
        BufferedImage img = getScreenShot(jmapframe.getContentPane());
        // JOptionPane.showMessageDialog(null,
        // new JLabel(
        // new ImageIcon(img.getScaledInstance(img.getWidth(null) / 2, img.getHeight(null) / 2, Image.SCALE_SMOOTH))));
        try {
            // write the image as a PNG
            ImageIO.write(img, "png", new File("screenshot.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static BufferedImage getScreenShot(Component component) {

        BufferedImage image = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
        // call the Component's paint method, using
        // the Graphics object of the image.
        component.paint(image.getGraphics()); // alternately use .printAll(..)
        return image;
    }

    /**
     * @param jmapframe
     *            the jmapframe to set
     */
    public void setJmapframe(JMapFrame jmapframe) {
        this.jmapframe = jmapframe;
    }

}
