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
