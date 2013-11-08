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
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.ship.gui.ChartPanel;
import dk.dma.epd.ship.gui.panels.MonaLisaCommunicationPanel;

public class MonaLisaCommunicationComponentPanel extends OMComponentPanel implements Runnable, ProjectionListener  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final MonaLisaCommunicationPanel commsPanel = new MonaLisaCommunicationPanel();
    private PntTime gnssTime;
    private ChartPanel chartPanel;
    
    public MonaLisaCommunicationComponentPanel(){
        super();
        
//        this.setMinimumSize(new Dimension(10, 25));
        
        commsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        setLayout(new BorderLayout(0, 0));
        add(commsPanel, BorderLayout.NORTH);
        setVisible(true);
        new Thread(this).start();
        
        
    }
    

    @Override
    public void projectionChanged(ProjectionEvent arg0) {
        setScale(chartPanel.getMap().getProjection().getScale());
    }
    
    public void setScale(float scale){
//        commsPanel.getScaleLabel().setText("Scale: " + String.format(Locale.US, "%3.0f", scale));
    }


    @Override
    public void run() {
        while (true) {
//            if (gnssTime != null) {
//                Date now = gnssTime.getDate();
////                commsPanel.getTimeLabel().setText(Formatter.formatLongDateTime(now));
//            }
            
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) { }
        }
        
    }

    
    @Override
    public void findAndInit(Object obj) {
        if (gnssTime == null && obj instanceof PntTime) {
            gnssTime = (PntTime)obj;
        }
        if (obj instanceof ChartPanel) {
            chartPanel = (ChartPanel)obj;
            chartPanel.getMap().addProjectionListener(this);
            return;
        }
    }
    

}
