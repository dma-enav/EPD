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
package dk.dma.epd.ship.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import dk.dma.epd.common.prototype.gui.route.ButtonLabelCommon;

public class ToggleButtonLabel extends ButtonLabelCommon {

    private static final long serialVersionUID = 1L;
    boolean toggled;
    public static Color clickedColor = new Color(80, 80, 80);
    public static Color standardColor = new Color(128, 128, 128);

    public ToggleButtonLabel(String text) {
        super(text);

    }

    public ToggleButtonLabel(ImageIcon toolbarIcon) {
        super(toolbarIcon);
    }

    @Override
    public void styleButton(final JLabel label) {

        label.setPreferredSize(new Dimension(80, 25));
        label.setFont(defaultFont);
        label.setForeground(textColor);
        // label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        label.setBorder(BorderFactory
                .createMatteBorder(1, 1, 1, 1, borderColor));
        label.setBackground(standardColor);
        label.setOpaque(true);

        label.setHorizontalAlignment(SwingConstants.CENTER);

        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (label.isEnabled()) {
                    if (toggled) {
                        // Untoggle it
                        label.setBackground(standardColor);
                    } else {
                        // Toggle it
                        label.setBackground(clickedColor);
                    }

                    toggled = !toggled;

                }
            }
        });
    }

    public void setSelected(boolean selected) {

        // Toggle it
        if (selected && this.isEnabled()) {
            this.setBackground(clickedColor);
            this.setBorder(BorderFactory.createCompoundBorder(
                    toolPaddingBorder, toolInnerEtchedBorder));
        } else {
            // Untoggle it
            this.setBorder(toolPaddingBorder);
            this.setBackground(standardColor);
        }
        toggled = selected;
    }

    public void setSelected(boolean selected, int icon) {

        if (selected && this.isEnabled()) {
            this.setBackground(clickedColor);
        } else {
            this.setBackground(standardColor);
        }
        toggled = selected;

    }

    public boolean isSelected() {
        return toggled;
    }

    @Override
    public void styleIconButton(final JLabel label) {
        label.setPreferredSize(new Dimension(40, 25));

        label.setOpaque(true);
        label.setBorder(toolPaddingBorder);
        label.setBackground(standardColor);

        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (label.isEnabled()) {
                    if (toggled) {
                        // Untoggle it
                        label.setBorder(toolPaddingBorder);
                        label.setBackground(standardColor);
                        // toggled = false;
                    } else {
                        // Toggle it
                        label.setBackground(clickedColor);
                        label.setBorder(BorderFactory.createCompoundBorder(
                                toolPaddingBorder, toolInnerEtchedBorder));
                        label.setOpaque(true);
                        // toggled = true;
                    }

                    toggled = !toggled;

                }
            }
        });
    }

}
