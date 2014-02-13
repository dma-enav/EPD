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
package dk.dma.epd.common.prototype.gui.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JDialog;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.WindowConstants;

/**
 * Displays the textual status of the services in the bottom panel
 */
public class BottomPanelStatusDialog extends JDialog implements ActionListener {

    /**
     * Private fields.
     */
    private static final long serialVersionUID = 1L;
    private JLabel statusLabel = new JLabel("");
    
    /**
     * Constructor
     */
    public BottomPanelStatusDialog() {
        super(EPD.getInstance().getMainFrame(), "Status", true);
        
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(300, 400);
        setLocationRelativeTo(EPD.getInstance().getMainFrame());
        getContentPane().setLayout(new BorderLayout(10, 10));
        
        statusLabel.setVerticalAlignment(SwingConstants.TOP);
        JScrollPane scrollPanel = new JScrollPane(statusLabel);
        getContentPane().add(scrollPanel, BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(this);
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnClose);
        getContentPane().add(btnPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Update status text of each status component.
     * @param statusComponents
     */
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
        
        statusLabel.setText("");
        statusLabel.setText(buf.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Close the window.
        dispose();
    }
}
