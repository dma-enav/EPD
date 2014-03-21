package dk.dma.epd.shore.gui.views;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.views.CommonBottomPanelStatusDialog;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.shore.EPDShore;

public class BottomPanelStatusDialog extends CommonBottomPanelStatusDialog {

    private static final long serialVersionUID = 1L;
    private JLabel lblPositionStatus;
    
    public BottomPanelStatusDialog() {
        super();
        
        super.createStatusPanels(createPanels());
        
//        super.timer.start();
        
        // Set location and visible to true.
        this.setLocationRelativeTo(EPDShore.getInstance().getMainFrame());
//        this.setVisible(true);
    }

    private List<JPanel> createPanels() {
        
        if (EPDShore.getInstance().getSettings().getMapSettings().isUseWms()) {
            
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
            
            lblPositionStatus = new JLabel();
            lblPositionStatus.setFont(PLAIN_FONT);
            lblPositionStatus.setBounds(121, 30, 165, 16);
            wmsPanel.add(lblPositionStatus, 0);
            super.colorStatusLabel(lblPositionStatus);
            
            List<JPanel> statusPanels = new ArrayList<JPanel>();
            statusPanels.add(wmsPanel);
            return statusPanels;
            
        } else {   
            return null;
        }        
    }
    
    @Override
    public void showStatus() {
        super.showStatus();
        
        for (IStatusComponent iStatusComponent : super.statusComponents) {
            ComponentStatus componentStatus = iStatusComponent.getStatus();
            
            if (componentStatus.getName().equals("WMS")) {
                this.lblPositionStatus.setText(componentStatus.getStatus().toString());
                super.colorStatusLabel(lblPositionStatus);
            }
        }
    }
}
