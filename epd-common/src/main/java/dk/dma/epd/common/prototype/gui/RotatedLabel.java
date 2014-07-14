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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * A {@linkplain JLabel} subclass that can show the label vertically.
 */
public class RotatedLabel extends JLabel {

    private static final long serialVersionUID = 1L;

    /**
     * The direction to display the label in
     */
    public enum Direction {
        HORIZONTAL(false),
        VERTICAL_UP(true),
        VERTICAL_DOWN(true);
        
        boolean vertical;
        private Direction(boolean vertical) {
            this.vertical = vertical;
        }
        
        public boolean isVertical() { 
            return vertical; 
        }
      }
    
    private Direction direction = Direction.HORIZONTAL;
    private boolean isPainting;
    
    /**
     * Constructor
     */
    public RotatedLabel() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getSize() {
        Dimension size = super.getSize();
        if (isPainting && direction.isVertical()) {
            return new Dimension(size.height, size.width);
        }
        return size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getHeight() {
        return getSize().height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getWidth() {
      return getSize().width;
    }

    /**
     * Sets the direction of the label text.
     * If the direction changes from vertical to horizontal, or vice versa,
     * the size of the label is adjusted accordingly
     * 
     * @param direction the new direction of the label
     */
    public void setDirection(Direction direction) {
        if (this.direction.isVertical() != direction.isVertical()) {
            setSize(getHeight(), getWidth());
        } 
        this.direction = direction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void paintComponent(Graphics g) {
          Graphics2D g2 = (Graphics2D) g.create();

          switch (direction) {
          case VERTICAL_UP:
            g2.translate(0, getSize().getHeight());
            g2.transform(AffineTransform.getQuadrantRotateInstance(-1));
            break;
          case VERTICAL_DOWN:
            g2.transform(AffineTransform.getQuadrantRotateInstance(1));
            g2.translate(0, -getSize().getWidth());
            break;
          default:
          }

          isPainting = true;
          super.paintComponent(g2);
          isPainting = false;
    }   
    
    
    /**
     * Test method
     * @param args
     */
    public static void main(String... args) {
        JFrame f = new JFrame("TEST");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(null);
        p.setSize(300, 300);
        f.getContentPane().add(p);
        
        RotatedLabel l = new RotatedLabel();
        l.setHorizontalAlignment(SwingConstants.CENTER);
        l.setText("testing rotation");
        p.add(l);
        l.setBackground(Color.yellow);
        l.setOpaque(true);
        l.setSize(l.getPreferredSize().width + 20, l.getPreferredSize().height + 4);
        l.setLocation(50,  50);
        l.setBorder(BorderFactory.createLineBorder(Color.black));
        l.setDirection(Direction.VERTICAL_DOWN);
        f.setBounds(100, 100, 400, 400);
        f.setVisible(true);
    }
}
