package dk.dma.epd.shore.gui.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JDialog;

import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.shore.EPDShore;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.JButton;

public class BottomPanelStatusDialog extends JDialog implements ActionListener {

    /**
     * Private fields.
     */
    private static final long serialVersionUID = 1L;
    private JLabel statusLabel;
    
    /**
     * Constructor
     */
    public BottomPanelStatusDialog() {
        
        super(EPDShore.getInstance().getMainFrame(), "Status", true);
        this.setResizable(false);
        this.setSize(250, 340);
        this.setLocationRelativeTo(EPDShore.getInstance().getMainFrame());
        getContentPane().setLayout(null);
        
        statusLabel = new JLabel("");
        statusLabel.setVerticalAlignment(SwingConstants.TOP);
        statusLabel.setBounds(6, 6, 238, 266);
        getContentPane().add(statusLabel);
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(this);
        btnClose.setBounds(75, 283, 100, 29);
        getContentPane().add(btnClose);
    }
    
    /**
     * Update status text of each status component.
     * @param statusComponents
     */
    public void updateStatusLabel(List<IStatusComponent> statusComponents) {
        
        System.out.println(statusComponents.size());
        
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
        
        this.statusLabel.setText("");
        this.statusLabel.setText(buf.toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        // Close the window.
        this.dispose();
    }
}
