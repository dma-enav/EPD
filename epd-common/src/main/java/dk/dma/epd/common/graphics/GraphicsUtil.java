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
package dk.dma.epd.common.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 * Graphics-related utility methods
 */
public class GraphicsUtil {

    /**
     * Generates a {@linkplain TexturePaint} of the given size, using
     * the given text as a pattern.
     * 
     * @param text the text to display in the texture paint
     * @param font the font to use for the text
     * @param textColor the text color
     * @param bgColor the background color
     * @param width the width of the texture
     * @param height the height of the texture
     * @return the texture paint
     */
    public static Paint generateTexturePaint(String text, Font font, Color textColor, Color bgColor, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setColor(bgColor);
        g2.fillRect(0, 0, width, height);
        g2.setColor(textColor);
        g2.setFont(font);
        // Draw the text centered in the bitmap
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(text, 
                (width - fm.stringWidth(text)) / 2, 
                fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);
        return new TexturePaint(bi, new Rectangle(0, 0, width, height));        
    }    
}
