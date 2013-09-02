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
package dk.dma.epd.ship.gui.voct;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import dk.dma.epd.ship.gui.ComponentFrame;
import dk.dma.epd.ship.gui.MainFrame;

/**
 * Dialog shown when route suggestion is received
 */
public class SARInvitationRequest extends ComponentFrame implements ActionListener, Runnable {
    private static final long serialVersionUID = 1L;
    


    private JButton acceptBtn;
    private JButton rejectBtn;
    private JButton notedBtn;
    private JButton zoomBtn;
    private JPanel routePanel;
    private MainFrame mainFrame;
    private JLabel lblMhvBopa;
    private JLabel lblDynamicMercy;
    private JLabel label;
    
    public SARInvitationRequest(MainFrame mainFrame) {
        super();
        this.mainFrame = mainFrame;
        setResizable(false);
        setTitle("Search and Rescue Request");
        
        setSize(380, 228);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLocationRelativeTo(mainFrame);        

        initGui();
        
        new Thread(this).start();
    }
    
    
    private void initGui() {
        
        zoomBtn = new JButton("Zoom to");
        zoomBtn.setToolTipText("Zoom to the suggested route on map");
        zoomBtn.addActionListener(this);
        
        routePanel = new JPanel();
        routePanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Search and Rescue Request", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(routePanel, GroupLayout.PREFERRED_SIZE, 355, Short.MAX_VALUE)
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(routePanel, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(281, Short.MAX_VALUE))
        );
        
        JLabel lblOscMhvBopa = new JLabel("OSC:");
        
        JLabel lblOperationName = new JLabel("Operation Name:");
        
        JLabel lblSarId = new JLabel("SAR ID:");
        
        JLabel lblRequestYourVessel = new JLabel("Requests your vessel to join as SRU");
        acceptBtn = new JButton("Accept");
        acceptBtn.setToolTipText("Indicate that suggested route will be used");
        acceptBtn.addActionListener(this);
        
        rejectBtn = new JButton("Reject");
        rejectBtn.setToolTipText("Reject the suggested route");
        rejectBtn.addActionListener(this);
        
        notedBtn = new JButton("Postpone");
        notedBtn.setToolTipText("Acknowledge reception, but route suggestion will or cannot be used");
        notedBtn.addActionListener(this);
        
        lblMhvBopa = new JLabel("MHV 911 Bopa");
        
        lblDynamicMercy = new JLabel("Dynamic Mercy");
        
        label = new JLabel("1352431");
                
        GroupLayout gl_routePanel = new GroupLayout(routePanel);
        gl_routePanel.setHorizontalGroup(
            gl_routePanel.createParallelGroup(Alignment.TRAILING)
                .addGroup(gl_routePanel.createSequentialGroup()
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.TRAILING)
                        .addGroup(Alignment.LEADING, gl_routePanel.createSequentialGroup()
                            .addComponent(lblRequestYourVessel)
                            .addPreferredGap(ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                            .addComponent(zoomBtn))
                        .addGroup(Alignment.LEADING, gl_routePanel.createSequentialGroup()
                            .addGroup(gl_routePanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblOperationName)
                                .addComponent(lblSarId)
                                .addComponent(lblOscMhvBopa))
                            .addGap(18)
                            .addGroup(gl_routePanel.createParallelGroup(Alignment.LEADING)
                                .addComponent(lblMhvBopa)
                                .addComponent(label)
                                .addComponent(lblDynamicMercy)))
                        .addGroup(gl_routePanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(acceptBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addGap(18)
                            .addComponent(rejectBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                            .addComponent(notedBtn, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)))
                    .addGap(26))
        );
        gl_routePanel.setVerticalGroup(
            gl_routePanel.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_routePanel.createSequentialGroup()
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_routePanel.createSequentialGroup()
                            .addGroup(gl_routePanel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblOscMhvBopa)
                                .addComponent(lblMhvBopa))
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(gl_routePanel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblOperationName)
                                .addComponent(lblDynamicMercy))
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addGroup(gl_routePanel.createParallelGroup(Alignment.BASELINE)
                                .addComponent(lblSarId)
                                .addComponent(label))
                            .addPreferredGap(ComponentPlacement.UNRELATED)
                            .addComponent(lblRequestYourVessel)
                            .addPreferredGap(ComponentPlacement.RELATED, 50, Short.MAX_VALUE))
                        .addGroup(Alignment.TRAILING, gl_routePanel.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(zoomBtn)
                            .addGap(29)))
                    .addGroup(gl_routePanel.createParallelGroup(Alignment.BASELINE)
                        .addComponent(acceptBtn)
                        .addComponent(rejectBtn)
                        .addComponent(notedBtn)))
        );
        routePanel.setLayout(gl_routePanel);
        getContentPane().setLayout(groupLayout);
        
        
    }


    @Override
    public void run() {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
