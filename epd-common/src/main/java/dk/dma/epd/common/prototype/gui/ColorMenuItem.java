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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

/**
 * This color selection panel can be used as a menu item for selecting a color.
 * <p>
 * The class is initialized with the {@linkplain JPopupMenu} it is inserted in,
 * along with the list of colors to choose from and the currently selected color.
 * <p>
 * The colors are displayed as a horizontal list of circles that can be clicked.
 * <p>
 * Hook up as a {@code ColorMenuItemListener} listener in order to be notified
 * when a color is selected.
 */
public class ColorMenuItem extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int INDENT = 4;
    private static final int CELL_SIZE = 18;
    
    private JPopupMenu menu;
    private Color[] colors = {};
    private int selectionIndex = -1;
    private Ellipse2D.Double[] circles;
    private Color selectionBackgroundColor = (Color)UIManager.get("Menu.selectionBackground");
    private Color backgroundColor = (Color)UIManager.get("Menu.background");
    private Point pos;
    private List<ColorMenuItemListener> listeners = new CopyOnWriteArrayList<>();
    
    /**
     * Constructor
     */
    public ColorMenuItem() {
        super();
        
        setOpaque(false);
        setLayout(null);
        
        addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) { repaint(e.getPoint()); }
            @Override public void mouseEntered(MouseEvent e) { repaint(e.getPoint()); }
            @Override public void mousePressed(MouseEvent e) { handleClick(e); }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) { repaint(e.getPoint()); }
        });
    }
    
    /**
     * Constructor
     * 
     * @param menu the pop up menu
     * @param colors the list of colors to display
     * @param selection the currently selected color
     */
    public ColorMenuItem(JPopupMenu menu, Color[] colors, Color selection) {
        this();
        init(menu, colors, selection);
    }
    
    /**
     * Initializes the color menu item with the list of colors to display
     * and the currently selected color
     * 
     * @param menu the pop up menu
     * @param colors the list of colors to display
     * @param selection the currently selected color
     */
    public void init(JPopupMenu menu, Color[] colors, Color selection) {
        this.menu = menu;
        this.colors = getColors(colors, selection);
        
        // Prepare the color circles
        circles = new Ellipse2D.Double[this.colors.length];
        for (int x = 0; x < this.colors.length; x++) {
            circles[x] = new Ellipse2D.Double(INDENT + x * CELL_SIZE + 3, INDENT + 3, CELL_SIZE - 6, CELL_SIZE - 6);
        }
        
        setMinimumSize(new Dimension(2 * INDENT + CELL_SIZE * this.colors.length, 2 * INDENT + CELL_SIZE));
        setPreferredSize(new Dimension(2 * INDENT + CELL_SIZE * this.colors.length, 2 * INDENT + CELL_SIZE));
    }
    
    /**
     * Checks if the selection is included in the list of colors
     * and adds it to the result if not.
     * 
     * @param colors the colors
     * @param selection the selection color
     * @return the colors including the selection
     */
    private Color[] getColors(Color[] colors, Color selection) {
        boolean selectionFound = false;
        for (int x = 0; x < colors.length; x++) {
            if (colors[x].equals(selection)) {
                selectionFound = true;
                selectionIndex = x;
                break;
            }
        }

        int xx = 0;
        Color[] cols;
        if (!selectionFound && selection != null) {
            cols = new Color[colors.length + 1];
            cols[xx++] = selection;
            selectionIndex = 0;
        } else {
            cols = new Color[colors.length];
        }
        for (int x = 0; x < colors.length; x++) {
            cols[xx++] = colors[x];
        }
        return cols;
    }
    
    /**
     * Strips the alpha component of the color
     * @param color the color
     * @return the same color with no alpha component
     */
    static Color stripAlpha(Color color){
        return new Color(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    /**
     * Return a highlighted version of the color
     * @param color the color to highlight
     * @return a highlighted version of the color
     */
    private Color highlight(Color color) {
        // Sanity check
        if (color == null) {
            return null;
        }
        
        int bgGrayLevel = (backgroundColor.getRed() + backgroundColor.getGreen() + backgroundColor.getBlue()) / 3;
        boolean darken = bgGrayLevel > 127;
        return darken ? color.darker() : color.brighter();
    }
    
    /**
     * Called when the mouse is clicked within the panel
     * @param e the mouse event
     */
    private void handleClick(MouseEvent e) {
        menu.setVisible(false);
        
        // Notify listeners
        int clickIndex = currentMouseColorIndex();
        if (clickIndex >= 0) {
            for (ColorMenuItemListener listener : listeners) {
                listener.colorSelected(colors[clickIndex]);
            }
        }
    }
    
    /**
     * Records the last mouse position and repaints the panel
     * @param pos the last mouse position
     */
    private void repaint(Point pos) {
        this.pos = pos;
        repaint();
    }
    
    /**
     * Returns the index of the current mouse position color
     * or -1 if outside the colors.
     * 
     * @return the index of the current mouse position color
     */
    private int currentMouseColorIndex() {
        // Check if the mouse is outside the colors
        if (pos == null ||
                pos.x < INDENT || 
                pos.x > INDENT + colors.length * CELL_SIZE ||
                pos.y < INDENT || 
                pos.y > INDENT + CELL_SIZE) {
            return -1;
        }
        return (pos.x - INDENT) / CELL_SIZE;
    }
    
    /**
     * Paints the component
     * @param g the graphics context
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(backgroundColor);
        g2.fillRect(0, 0, getWidth(), getHeight());
        
        int mouseIndex = currentMouseColorIndex();
        for (int x = 0; x < colors.length; x++) {
            if (x == mouseIndex) {
                g2.setColor(selectionBackgroundColor);
                g2.fillRect(INDENT + x * CELL_SIZE, INDENT, CELL_SIZE, CELL_SIZE);
            }
            
            if (x == selectionIndex) {
                g2.setColor(highlight(selectionBackgroundColor));
                g2.drawRect(INDENT + x * CELL_SIZE, INDENT, CELL_SIZE, CELL_SIZE);
            }
            
            g2.setColor(stripAlpha(colors[x]));
            g2.fill(circles[x]);
            g2.setStroke(new BasicStroke(2));
            g2.setColor(highlight(stripAlpha(colors[x])));
            g2.draw(circles[x]);
        }
    }
    
    /**
     * Adds a new listener for color selection
     * @param listener the listener to add
     */
    public void addListener(ColorMenuItemListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Removes a listener
     * @param listener the listener to remove
     */
    public void removeListener(ColorMenuItemListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Test method
     */
    public static void main(String... args) {
        JFrame frame = new JFrame();
        final JButton btn = new JButton("Test");
        frame.getContentPane().add(btn);
        frame.setBounds(300, 200, 100, 100);
        final JPopupMenu menu = new JPopupMenu();
        menu.add(new JMenuItem("Select a color from the menu below"));
        ColorMenuItem colorMenuItem = new ColorMenuItem(
                menu,
                new Color[] { Color.red, Color.blue, Color.white, Color.black, Color.gray },
                Color.green);
        menu.add(colorMenuItem);
        btn.addActionListener(new ActionListener() {
            @Override  public void actionPerformed(ActionEvent e) {
                menu.show(btn, 20, 20);
            }});
        frame.setVisible(true);
    }
    
    /**
     * Interface to be implemented by listeners of color selection
     */
    public interface ColorMenuItemListener {
        void colorSelected(Color color);
    }    
}
