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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

/**
 * Graphics-related utility methods
 */
public class GraphicsUtil {

    public static RenderingHints ANTIALIAS_HINT = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
    
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
     * Resizes the image down (not up) so that it fits proportionally 
     * within the given bounds
     * 
     * @param originalImage
     * @param type the image type
     * @param width the maximum image width
     * @param height the maximum image height
     * @return the scaled image
     */
    public static Image resizeImage(Image originalImage, int type, int width, int height){
        // Value of 0 means we don't care
        width = (width == 0) ? originalImage.getWidth(null) : width;
        height = (height == 0) ? originalImage.getHeight(null) : height;
        
        // Check if we need to scale at all
        if (originalImage.getWidth(null) <= width && originalImage.getHeight(null) <= height) {
            return originalImage;
        }
        
        float scaleW = (float)originalImage.getWidth(null) / (float)width;
        float scaleH = (float)originalImage.getHeight(null) / (float)height;
        float scale = Math.max(scaleW, scaleH);
        
        int newWidth = (int)((float)originalImage.getWidth(null) / scale);
        int newHeight = (int)((float)originalImage.getHeight(null) / scale);
        
        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, type);
        Graphics2D g = resizedImage.createGraphics();
        g.setComposite(AlphaComposite.Src);        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g.dispose();    
     
        return resizedImage;
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
     * Converts the color into a Hex representation
     * suitable for HTML color attributes.
     * 
     * @param color the color to convert
     * @return the HTML Hex version of the color
     */
    public static String toHtmlColor(Color color) {
        String rgb = Integer.toHexString(color.getRGB());
        return "#" + rgb.substring(2, rgb.length());
    }
    
    /**
     * Fixes the size of a {@linkplain JComponent} to the given width and height.
     * <p>
     * If a value of -1 is passed along, the preferred size is used instead.
     * 
     * @param comp the component to fix the size of
     * @param width the fixed width
     * @param height the fixed height
     * @return the updated component
     */
    public static <T extends JComponent> T fixSize(T comp, int width, int height) {
        // Sanity check
        if (comp == null) {
            return null;
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
        return comp;
    }

    /**
     * Fixes the size of a {@linkplain JComponent} to the given width
     * <p>
     * If a value of -1 is passed along, the preferred size is used instead.
     * 
     * @param comp the component to fix the size of
     * @param width the fixed width
     * @return the updated component
     */
    public static <T extends JComponent> T fixSize(T comp, int width) {
        return fixSize(comp, width, -1);
    }

    /**
     * Sets the minimum size of the {@linkplain JComponent}
     * 
     * @param comp the component to fix the size of
     * @return the updated component
     */
    public static <T extends JComponent> T minSize(T comp, int width) {
        // Sanity check
        if (comp == null) {
            return null;
        }
        
        comp.setMinimumSize(new Dimension(width, comp.getMinimumSize().height));
        return comp;
    }
    
    /**
     * Turns the font of given {@linkplain JComponent} into bold
     * 
     * @param comp the component to turn bold
     * @return the updated component
     */
    public static <T extends JComponent> T bold(T comp) {
        comp.setFont(comp.getFont().deriveFont(Font.BOLD));
        return comp;
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
    }

    /**
     * Returns the layered pane that is the top level container for the 
     * given component
     * 
     * @param component the component to fetch the top level container for
     * @return the top level container
     */
    public static JLayeredPane getTopLevelContainer(JComponent component) {
        Window window = SwingUtilities.windowForComponent(component);
        if (window instanceof JWindow) {
            return ((JWindow)window).getLayeredPane();
        } else if (window instanceof JFrame) {
            return ((JFrame)window).getLayeredPane();
        } else if (window instanceof JDialog) {
            return ((JDialog)window).getLayeredPane();
        }
        return null;
    }
    
    /**
     * Recursively sets the enabled state of the component and its child components
     * 
     * @param component the component to set the enabled state of
     * @param enabled the enabled state
     */
    public static void setEnabled(Component component, boolean enabled) {
        if (component != null) {
            component.setEnabled(enabled);
            if (component instanceof Container) {
                Container container = (Container)component;
                for (Component child : container.getComponents()) {
                    setEnabled(child, enabled);
                }
            }
        }
    }
}

