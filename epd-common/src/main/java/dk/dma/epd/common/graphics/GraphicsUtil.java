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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

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

    /**
     * Creates a copy of the given color with the specified transparency
     * 
     * @param color the color to create a transparent copy of
     * @param transparency the transparency, between 0 and 255.
     * @return the transparent version of the color
     */
    public static Color transparentColor(Color color, int transparency) {
        if (color == null) {
            return null;
        }
        transparency = Math.min(Math.max(0, transparency), 255);
        return new Color(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                transparency);
    }
    
    /**
     * Blends two colors and returns the result
     * 
     * @param c0 the first color
     * @param c1 the second color
     * @return the blended result
     */
    public static Color blendColors(Color c0, Color c1) {
        double totalAlpha = c0.getAlpha() + c1.getAlpha();
        double weight0 = c0.getAlpha() / totalAlpha;
        double weight1 = c1.getAlpha() / totalAlpha;

        double r = weight0 * c0.getRed() + weight1 * c1.getRed();
        double g = weight0 * c0.getGreen() + weight1 * c1.getGreen();
        double b = weight0 * c0.getBlue() + weight1 * c1.getBlue();
        double a = Math.max(c0.getAlpha(), c1.getAlpha());

        return new Color((int) r, (int) g, (int) b, (int) a);
    }
    
    /**
     * Fixes the size of a {@linkplain JComponent} to the given width and height.
     * <p>
     * If a value of -1 is passed along, the preferred size is used instead.
     * 
     * @param comp the component to fix the size of
     * @param width the fixed width
     * @param height the fixed height
     */
    public static void fixSize(JComponent comp, int width, int height) {
        // Sanity check
        if (comp == null) {
            return;
        }
        
        if (width == -1) {
            width = (int)comp.getPreferredSize().getWidth();
        }
        if (height == -1) {
            height = (int)comp.getPreferredSize().getHeight();
        }
        Dimension dim = new Dimension(width, height);
        comp.setPreferredSize(dim);
        comp.setMaximumSize(dim);
        comp.setMinimumSize(dim);
        comp.setSize(dim);
    }

    /**
     * Fixes the size of a {@linkplain JComponent} to the given width
     * <p>
     * If a value of -1 is passed along, the preferred size is used instead.
     * 
     * @param comp the component to fix the size of
     * @param width the fixed width
     */
    public static void fixSize(JComponent comp, int width) {
        fixSize(comp, width, -1);
    }

    /**
     * Centers the given window on the main screen
     * 
     * @param frame the window to center
     */
    public static void centerWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation(x, y);
    }}
