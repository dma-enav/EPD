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
package dk.dma.epd.ship.gui;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import dk.dma.epd.ship.status.ComponentStatus;
import dk.dma.epd.ship.status.IStatusComponent;

/**
 * Status dialog with detailed status for given list of status components
 */
public class StatusDialog extends JDialog {
    private static final long serialVersionUID = 1L;
    private JLabel statusLbl;
    
    public StatusDialog() {
        super((Frame)null, "Status", true);
        
        setSize(300, 330);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        statusLbl = new JLabel();
        statusLbl.setVerticalAlignment(SwingConstants.TOP);
        
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();                
            }
        });
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addGap(92)
                            .addComponent(closeBtn, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE))
                        .addGroup(groupLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(statusLbl, GroupLayout.DEFAULT_SIZE, 264, Short.MAX_VALUE)))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addComponent(statusLbl, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                    .addGap(18)
                    .addComponent(closeBtn)
                    .addContainerGap())
        );
        getContentPane().setLayout(groupLayout);
    }
    
    public void showStatus(List<IStatusComponent> statusComponents) {
        StringBuilder buf = new StringBuilder();
        buf.append("<html>");
        for (IStatusComponent statusComponent : statusComponents) {
            ComponentStatus componentStatus = statusComponent.getStatus();            
            buf.append("<h4>" + componentStatus.getName() + "</h4>");
            buf.append("<p>");
            buf.append(componentStatus.getStatusHtml());            
            buf.append("</p>");
        }
        buf.append("</html>");
        
        statusLbl.setText(buf.toString());
        setVisible(true);
    }

}
