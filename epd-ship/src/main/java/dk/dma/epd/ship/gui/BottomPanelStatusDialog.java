package dk.dma.epd.ship.gui;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.views.CommonBottomPanelStatusDialog;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.status.PntStatus;
import dk.dma.epd.common.text.Formatter;

public class BottomPanelStatusDialog extends CommonBottomPanelStatusDialog {

    private static final long serialVersionUID = 1L;
    private JLabel lblPositionStatus;
    private JLabel lblLastPNTDataStatus;
    private JLabel lblSourceStatus;

    /**
     * 
     */
    public BottomPanelStatusDialog() {
        super();
        super.setSize(super.getSize().width,
                super.getSize().height+160);
        
        // PNT status
        JPanel pntPanel = new JPanel();
        pntPanel.setBorder(
                new TitledBorder(null, "PNT", TitledBorder.LEADING, TitledBorder.TOP, TITLE_FONT));
        pntPanel.setSize(292, 120);
        pntPanel.setLayout(null);
        statusPanel.add(pntPanel);
        
        JLabel lblPosition = new JLabel("Position:");
        lblPosition.setFont(PLAIN_FONT);
        lblPosition.setBounds(16, 30, 55, 16);
        pntPanel.add(lblPosition, 0);
        
        this.lblPositionStatus = new JLabel("status");
        this.lblPositionStatus.setFont(PLAIN_FONT);
        this.lblPositionStatus.setBounds(121, 30, 165, 16);
        pntPanel.add(this.lblPositionStatus);
        
        JLabel lblSource = new JLabel("Source:");
        lblSource.setFont(PLAIN_FONT);
        lblSource.setBounds(16, 55, 61, 16);
        pntPanel.add(lblSource);
        
        this.lblSourceStatus = new JLabel("status");
        this.lblSourceStatus.setFont(PLAIN_FONT);
        this.lblSourceStatus.setBounds(121, 55, 165, 16);
        pntPanel.add(this.lblSourceStatus);
        
        JLabel lblLastPntData = new JLabel("Last PNT data:");
        lblLastPntData.setFont(PLAIN_FONT);
        lblLastPntData.setBounds(16, 80, 90, 16);
        pntPanel.add(lblLastPntData);
        
        this.lblLastPNTDataStatus = new JLabel("status");
        this.lblLastPNTDataStatus.setFont(PLAIN_FONT);
        this.lblLastPNTDataStatus.setBounds(121, 80, 165, 16);
        pntPanel.add(this.lblLastPNTDataStatus);
        
        super.createStatusPanels();
    }

    @Override
    public void showStatus(List<IStatusComponent> statusComponents) {
        super.showStatus(statusComponents);

        for (IStatusComponent iStatusComponent : statusComponents) {

            ComponentStatus componentStatus = iStatusComponent.getStatus();

            if (componentStatus instanceof PntStatus) {

                PntStatus pntStatus = (PntStatus) componentStatus;
                this.lblPositionStatus.setText(pntStatus.getStatus().toString());
                this.lblLastPNTDataStatus.setText(Formatter.formatLongDateTime(pntStatus.getPntData().getLastUpdated()));

                try {
                    this.lblSourceStatus.setText(pntStatus.getPntData().getPntSource().toString());                    
                } catch (NullPointerException e) {
                    this.lblSourceStatus.setText("N/A");
                }

                super.colorStatusLabel(this.lblPositionStatus);
            }
        }
    }
}
