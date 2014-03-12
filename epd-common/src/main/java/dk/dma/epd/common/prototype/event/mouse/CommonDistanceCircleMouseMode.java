package dk.dma.epd.common.prototype.event.mouse;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

public class CommonDistanceCircleMouseMode extends AbstractCoordMouseMode {

    private static final long serialVersionUID = 1L;
    private ChartPanelCommon chartPanel;
    private String previousActiveMouseModeID;
    
    public static final transient String MODE_ID = "DistanceCircle";

    /**
     * 
     * @param chartPanel
     */
    public CommonDistanceCircleMouseMode(ChartPanelCommon chartPanel) {
        super(MODE_ID, false);
        
        this.chartPanel = chartPanel;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
        if (e.getButton() == MouseEvent.BUTTON1 || 
                e.getButton() == MouseEvent.BUTTON3) {
            
            System.out.println("Go go distance mouse!");
            mouseSupport.fireMapMouseClicked(e);
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        EPD.getInstance().getMainFrame().getActiveChartPanel().getMap().setCursor(
                Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }
    
    /**
     * Set the MODE_ID of the mouse mode that was active prior to setting this
     * mouse mode as the active mouse mode.
     * 
     * @param modeID
     */
    public void setPreviousMouseModeModeID(String modeID) {
        this.previousActiveMouseModeID = modeID;
    }

    /**
     * Get the MODE_ID of the mouse mode that was the active mouse mode prior to
     * setting this mouse mode as the active mouse mode.
     * 
     * @return The MODE_ID of the previous active mouse mode, or null if no
     *         previous active mouse mode was registered (via setter call).
     */
    public String getPreviousMouseMode() {
        return this.previousActiveMouseModeID;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
    }
}
