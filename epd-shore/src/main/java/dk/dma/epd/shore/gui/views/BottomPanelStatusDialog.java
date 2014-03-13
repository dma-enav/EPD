package dk.dma.epd.shore.gui.views;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.views.CommonBottomPanelStatusDialog;
import dk.dma.epd.common.prototype.status.IStatusComponent;

public class BottomPanelStatusDialog extends CommonBottomPanelStatusDialog {

    private static final long serialVersionUID = 1L;
    
    public BottomPanelStatusDialog(IStatusComponent status) {
        super();
        super.setSize(super.getSize().width,
                super.getSize().height+160);
        
        if (status != null) {
            
            JPanel wmsPanel = new JPanel();
            wmsPanel.setBorder(
                    new TitledBorder(null, "WMS", TitledBorder.LEADING, TitledBorder.TOP, TITLE_FONT));
            wmsPanel.setSize(292, 120);
            wmsPanel.setLayout(null);
            super.statusPanel.add(wmsPanel);
            
            JLabel lblPosition = new JLabel("Status:");
            lblPosition.setFont(PLAIN_FONT);
            lblPosition.setBounds(16, 30, 55, 16);
            wmsPanel.add(lblPosition, 0);
            
            JLabel lblPositionStatus = new JLabel(status.getStatus().getStatus().toString());
            lblPositionStatus.setFont(PLAIN_FONT);
            lblPositionStatus.setBounds(121, 30, 165, 16);
            wmsPanel.add(lblPositionStatus, 0);
            super.colorStatusLabel(lblPositionStatus);
        }
        
        
        super.createStatusPanels();
    }
}
