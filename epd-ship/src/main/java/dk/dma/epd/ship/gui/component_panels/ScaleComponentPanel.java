/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dk.dma.epd.ship.gui.component_panels;

import java.awt.BorderLayout;
import java.util.Date;
import java.util.Locale;

import javax.swing.border.EtchedBorder;

import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.common.prototype.sensor.pnt.PntTime;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.ship.gui.ChartPanel;
import dk.dma.epd.ship.gui.panels.ScalePanel;

public class ScaleComponentPanel extends OMComponentPanel implements Runnable, ProjectionListener, DockableComponentPanel  {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final ScalePanel scalePanel = new ScalePanel();
    private PntTime gnssTime;
    private ChartPanel chartPanel;
    
    public ScaleComponentPanel(){
        super();
        
//        this.setMinimumSize(new Dimension(10, 25));
        
        scalePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);
        setLayout(new BorderLayout(0, 0));
        add(scalePanel, BorderLayout.NORTH);
        new Thread(this).start();
        setVisible(false);
        
    }
    

    @Override
    public void projectionChanged(ProjectionEvent arg0) {
        setScale(chartPanel.getMap().getProjection().getScale());
    }
    
    public void setScale(float scale){
        scalePanel.getScaleLabel().setText("Scale: " + String.format(Locale.US, "%3.0f", scale));
    }


    @Override
    public void run() {
        while (true) {
            if (gnssTime != null) {
                Date now = gnssTime.getDate();
                scalePanel.getTimeLabel().setText(Formatter.formatLongDateTime(now));
            }
            
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
    
    /****************************************/
    /** DockableComponentPanel methods     **/
    /****************************************/

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDockableComponentName() {
        return "Scale";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInDefaultLayout() {
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInPanelsMenu() {
        return true;
    }
}
