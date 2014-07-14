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
