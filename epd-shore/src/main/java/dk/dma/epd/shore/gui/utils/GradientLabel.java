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
package dk.dma.epd.shore.gui.utils;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.Icon;
import javax.swing.JLabel;

public class GradientLabel extends JLabel
{
  // ------------------------------ FIELDS ------------------------------

  /**
     *
     */
    private static final long serialVersionUID = 1L;
private Color start;
  private Color end;

  // --------------------------- CONSTRUCTORS ---------------------------

  public GradientLabel( String text )
  {
    super( text );

    start = Color.LIGHT_GRAY;
    end = getBackground();
  }

  public GradientLabel( String text, Icon icon, Color start, Color end )
  {
    super( text );
    this.start = start;
    this.end = end;
  }

  // -------------------------- OTHER METHODS --------------------------

  public void paint( Graphics g )
  {
    int width = getWidth();
    int height = getHeight();

    // Create the gradient paint
    GradientPaint paint = new GradientPaint( 0, 0, start, 0, height, end, false );

    // we need to cast to Graphics2D for this operation
    Graphics2D g2d = ( Graphics2D )g;

    // save the old paint
    Paint oldPaint = g2d.getPaint();

    // set the paint to use for this operation
    g2d.setPaint( paint );

    // fill the background using the paint
    g2d.fillRect( 0, 0, width, height );

    // restore the original paint
    g2d.setPaint( oldPaint );

    super.paint( g );
  }
}
