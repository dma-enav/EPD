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
package dk.dma.epd.ship.layers;

import java.awt.Image;

import javax.swing.ImageIcon;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.graphics.CenterRaster;

public class TestLayer extends GeneralLayer {

    private static final long serialVersionUID = 1L;

    public TestLayer() {
        super();

        ImageIcon image = new ImageIcon("C:/image.png");

        // You can rescale it
        Image img = image.getImage();
        Image newimg = img.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);

        ImageIcon rescaled = new ImageIcon(newimg);
        // You can also use the image with directly ie. image.getIconWidth() and height

        // The center of the image
        Position pos = Position.create(56.5, 12.5);

        CenterRaster imageRaster = new CenterRaster(pos.getLatitude(), pos.getLongitude(), rescaled.getIconWidth(),
                rescaled.getIconHeight(), rescaled);

        // And you can set the angle
        imageRaster.setRotationAngle(45);

        graphics.add(imageRaster);
    }

}
