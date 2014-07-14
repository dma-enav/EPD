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
package dk.dma.epd.common.prototype.gui.settings;

import java.util.concurrent.TimeUnit;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

/**
 * 
 * @author Janus Varmarken
 */
public class IntendedRouteFilterSettingsPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JSpinner spinnerTimeToLive;
    private JSpinner spinnerFilterDistance;
    private JSpinner spinnerNotificationDistance;
    private JSpinner spinnerAlertDistance;
    
    public IntendedRouteFilterSettingsPanel() {
        this.setBounds(6, 260, 438, 140);
        this.setBorder(new TitledBorder(null, "Intended Route Filter"));
        this.setLayout(null);
        
        JLabel lblTimeToLive = new JLabel("Time to live (min)");
        lblTimeToLive.setBounds(103, 22, 110, 16);
        this.add(lblTimeToLive);
        
        spinnerTimeToLive = new JSpinner();
        // TODO consider changing spinner model to use Longs...
        spinnerTimeToLive.setModel(new SpinnerNumberModel(new Long(0), new Long(0), null, new Long(1)));
        spinnerTimeToLive.setBounds(16, 20, 75, 20);
        this.add(spinnerTimeToLive);
        
        JLabel lblFilterDistanceEpsilon = new JLabel("Filter Distance (nm)");
        lblFilterDistanceEpsilon.setBounds(103, 47, 123, 16);
        this.add(lblFilterDistanceEpsilon);
        
        spinnerFilterDistance = new JSpinner();
        spinnerFilterDistance.setModel(new SpinnerNumberModel(new Double(0), new Double(0), null, new Double(0.1)));
        spinnerFilterDistance.setBounds(16, 45, 75, 20);
        this.add(spinnerFilterDistance);
        
        JLabel lblNotificationDistancenm = new JLabel("Notification Distance (nm)");
        lblNotificationDistancenm.setBounds(103, 72, 166, 16);
        this.add(lblNotificationDistancenm);
        
        spinnerNotificationDistance = new JSpinner();
        spinnerNotificationDistance.setModel(new SpinnerNumberModel(new Double(0), new Double(0), null, new Double(0.1)));
        spinnerNotificationDistance.setBounds(16, 70, 75, 20);
        this.add(spinnerNotificationDistance);
        
        JLabel lblAlertDistance = new JLabel("Alert Distance (nm)");
        lblAlertDistance.setBounds(103, 97, 121, 16);
        this.add(lblAlertDistance);
        
        spinnerAlertDistance = new JSpinner();
        spinnerAlertDistance.setModel(new SpinnerNumberModel(new Double(0), new Double(0), null, new Double(0.1)));
        spinnerAlertDistance.setBounds(16, 95, 75, 20);
        this.add(spinnerAlertDistance);
    }
    
    /**
     * Get intended route time to live.
     * @return The time to live <b>in minutes</b>.
     */
    public long getTimeToLive() {
        // TODO consider changing spinner model to use Longs...
        return (Long)this.spinnerTimeToLive.getValue();
    }
    
    public double getNotificationDistance() {
        return (Double)this.spinnerNotificationDistance.getValue();
    }
    
    public double getAlertDistance() {
        return (Double)this.spinnerAlertDistance.getValue();
    }
    
    public double getFilterDistance() {
        return (Double)this.spinnerFilterDistance.getValue();
    }
    
    public void setFilterDistance(double filterDistance) {
        this.spinnerFilterDistance.setValue(filterDistance);
    }
    
    /**
     * Set intended route time to live.
     * @param timeToLiveMillis The time to live <b>in milliseconds</b>.
     */
    public void setTimeToLive(long timeToLiveMillis) {
        this.spinnerTimeToLive.setValue(TimeUnit.MILLISECONDS.toMinutes(timeToLiveMillis));
    }
    
    public void setAlertDistance(double alertDistance) {
        this.spinnerAlertDistance.setValue(alertDistance);
    }
    
    public void setNotificationDistance(double notificationDistance) {
        this.spinnerNotificationDistance.setValue(notificationDistance);
    }
}
