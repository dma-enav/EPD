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
package dk.dma.epd.ship.gui.Panels;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;

import dk.dma.epd.ship.EPDShip;

public class LogoPanel extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final JLabel euBalticLogo = new JLabel("");
    private final JLabel efficienseaLogo = new JLabel("");
    


    public LogoPanel(){
        setBorder(null);
        
        efficienseaLogo.setIcon(new ImageIcon(EPDShip.class.getResource("/images/sensorPanel/efficiensea.png")));
        euBalticLogo.setIcon(new ImageIcon(EPDShip.class.getResource("/images/sensorPanel/euBaltic.png")));
//        GroupLayout groupLayout = new GroupLayout(this);
//        groupLayout.setHorizontalGroup(
//            groupLayout.createParallelGroup(Alignment.LEADING)
//                .addGroup(groupLayout.createSequentialGroup()
//                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
//                        .addComponent(euBalticLogo, Alignment.TRAILING)
//                        .addComponent(efficienseaLogo, Alignment.TRAILING))
//                    .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
//        );
//        groupLayout.setVerticalGroup(
//            groupLayout.createParallelGroup(Alignment.TRAILING)
//                .addGroup(groupLayout.createSequentialGroup()
//                    .addContainerGap(730, Short.MAX_VALUE)
//                    .addComponent(efficienseaLogo)
//                    .addPreferredGap(ComponentPlacement.RELATED)
//                    .addComponent(euBalticLogo))
//        );
//        setLayout(groupLayout);
//        
        
        
        GroupLayout groupLayout = new GroupLayout(this);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addComponent(euBalticLogo)
                        .addComponent(efficienseaLogo))
                    .addContainerGap(183, Short.MAX_VALUE))
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                    .addContainerGap(153, Short.MAX_VALUE)
                    .addComponent(efficienseaLogo)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(euBalticLogo))
        );
        this.setLayout(groupLayout);
        
    }
}
