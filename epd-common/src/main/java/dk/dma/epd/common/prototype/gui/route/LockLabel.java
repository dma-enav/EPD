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
package dk.dma.epd.common.prototype.gui.route;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import dk.dma.epd.common.prototype.EPD;

public class LockLabel extends ButtonLabelCommon {

    private static final long serialVersionUID = 1L;
    boolean toggled;
    ImageIcon unlockedIcon;
    ImageIcon lockedIcon;
    RoutePropertiesRow ownRow;
    RoutePropertiesDialogCommon routePropertiesDialog;

    public LockLabel(RoutePropertiesDialogCommon routePropertiesDialog) {

        unlockedIcon = toolbarIcon("images/toolbar/lock-unlock.png");
        lockedIcon = toolbarIcon("images/toolbar/lock.png");
        this.setIcon(unlockedIcon);
        styleButton(this);
        
        this.setBackground(new Color(48, 48, 48));

        this.routePropertiesDialog = routePropertiesDialog;
    }

    public RoutePropertiesRow getOwnRow() {
        return ownRow;
    }

    public void setOwnRow(RoutePropertiesRow ownRow) {
        this.ownRow = ownRow;
    }

    // Needs fixing
    public ImageIcon toolbarIcon(String imgpath) {
        return EPD.res().getCachedImageIcon(imgpath);
    }

    public void unlockButton(){
        this.setIcon(unlockedIcon);
        ownRow.unlock();
        toggled = false;
    }
    
    public void lockButton(){
        this.setIcon(lockedIcon);
        ownRow.lock();
        toggled = true;
    }
    
    @Override
    public void styleButton(final JLabel label) {

//        label.setOpaque(true);
//        label.setBorder(new MatteBorder(1, 1, 1, 1, new Color(65, 65,
//                65)));

        label.setPreferredSize(new Dimension(17, 20));
        // label.setFont(defaultFont);
        // label.setForeground(textColor);
        // // label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        // label.setBorder(BorderFactory
        // .createMatteBorder(1, 1, 1, 1, borderColor));
        // label.setBackground(standardColor);
        // label.setOpaque(true);
        //
        // label.setHorizontalAlignment(SwingConstants.CENTER);

        // if (!ownRow.isLast()) {

        label.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

                if (!ownRow.isLast()) {
                    if (label.isEnabled()) {
                        if (toggled) {
                            // Untoggle it
                            label.setIcon(unlockedIcon);
                            ownRow.unlock();
                            // label.setBackground(standardColor);
                        } else {
                            // Toggle it
                            // label.setBackground(clickedColor);
                            label.setIcon(lockedIcon);
                            ownRow.lock();
                        }

                        toggled = !toggled;
                        routePropertiesDialog.checkLocks();

                    }
                }
            }
        });

    }

    public void setSelected(boolean selected) {

        // Toggle it
        if (selected && this.isEnabled()) {
//            this.setBackground(clickedColor);
//            this.setBorder(BorderFactory.createCompoundBorder(
//                    toolPaddingBorder, toolInnerEtchedBorder));
        } else {
            // Untoggle it
//            this.setBorder(toolPaddingBorder);
//            this.setBackground(standardColor);
        }
        toggled = selected;
    }

    public void setSelected(boolean selected, int icon) {

        if (selected && this.isEnabled()) {
//            this.setBackground(clickedColor);
        } else {
//            this.setBackground(standardColor);
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
//                        label.setBorder(toolPaddingBorder);
//                        label.setBackground(standardColor);
                        // toggled = false;
                    } else {
                        // Toggle it
//                        label.setBackground(clickedColor);
//                        label.setBorder(BorderFactory.createCompoundBorder(
//                                toolPaddingBorder, toolInnerEtchedBorder));
                        label.setOpaque(true);
                        // toggled = true;
                    }

                    toggled = !toggled;

                }
            }
        });
    }

}
