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
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.bbn.openmap.gui.OMComponentPanel;

import dk.dma.epd.ship.ais.AisHandler;
import dk.dma.epd.ship.gui.panels.DynamicNoGoPanel;
import dk.dma.epd.ship.nogo.DynamicNogoHandler;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

public class DynamicNoGoComponentPanel extends OMComponentPanel implements DockableComponentPanel {

    private static final long serialVersionUID = 1L;
    private AisHandler aisHandler;
    private DynamicNogoHandler dynamicNogoHandler;

    private final DynamicNoGoPanel nogoPanel = new DynamicNoGoPanel();

    private JLabel statusLabel;
    private JLabel statLabel1;
    private JLabel statLabel2;
    private JLabel statLabel3;
    private JLabel statLabel4;
    private JLabel statLabel5;
    private JLabel statLabel6;
    private JLabel statLabel7;
    
    public DynamicNoGoComponentPanel() {
        super();

//        this.setMinimumSize(new Dimension(10, 150));

        nogoPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setBorder(null);

        setLayout(new BorderLayout(0, 0));
        add(nogoPanel, BorderLayout.NORTH);
        
        statusLabel = nogoPanel.getStatusLabel();
        statusLabel.setText("Inactive");
        
        statLabel1 = nogoPanel.getStatLabel1();
        statLabel2 = nogoPanel.getStatLabel2();
        statLabel3 = nogoPanel.getStatLabel3();
        statLabel4 = nogoPanel.getStatLabel4();
        statLabel5 = nogoPanel.getStatLabel5();
        statLabel6 = nogoPanel.getStatLabel6();
        statLabel7 = nogoPanel.getStatLabel7();
        
        statusLabel.setEnabled(false);
        statLabel1.setEnabled(false);
        statLabel2.setEnabled(false);
        statLabel3.setEnabled(false);
        statLabel4.setEnabled(false);
        statLabel5.setEnabled(false);
        statLabel6.setEnabled(false);
        statLabel7.setEnabled(false);
        
        
        statLabel5.setText("");
        statLabel6.setText("");
        statLabel7.setText("");
        setVisible(false);
    }
    
    public void newRequest(){
        statusLabel.setEnabled(true);
        statLabel1.setEnabled(false);
        statLabel2.setEnabled(false);
        statLabel3.setEnabled(false);
        statLabel4.setEnabled(false);
        statLabel5.setEnabled(true);
        statLabel6.setEnabled(true);
        statLabel7.setEnabled(true);
        
        statusLabel.setText("Connecting...");
        statusLabel.setForeground(Color.GREEN);
        statLabel1.setText("N/A");
        statLabel2.setText("N/A");
        statLabel3.setText("N/A");
        statLabel4.setText("N/A");
        
        statLabel5.setText("Target Vessel: " + aisHandler.getVesselTarget(dynamicNogoHandler.getMmsiTarget()).getStaticData().getTrimmedName());
        statLabel6.setText("Requesting NoGo");
        statLabel7.setText("Please standby");

    }

    
    /**
     * Errorcode -1 means server experinced a timeout 
     * Errorcode 0 means everything went ok 
     * Errorcode 1 is the standby message 
     * Errorcode 17 means no data 
     * Errorcode 18 means no tide data
     * @param nogoFailed 
     * @param errorCode Own
     * @param errorCode  Target
     * @param polygons own
     * @param polygons target
     * @param valid from 
     * @param valid to 
     * @param own draught 
     * @param target draught 
     * 
     * @param completed
     */
    public void requestCompleted(boolean nogoFailed, int errorCodeOwn, int errorCodeTarget, List<NogoPolygon> polygonsOwn, List<NogoPolygon> polygonsTarget, Date validFrom, Date validTo, float draughtOwn, float draughtTarget){
        if (nogoFailed){
            statusLabel.setText("Failed");
            statusLabel.setForeground(Color.RED);
            statLabel5.setText("");
            statLabel6.setText("Connection to shore timed out");
            statLabel7.setText("Try again in a few minutes");
            
            statLabel1.setEnabled(false);
            statLabel2.setEnabled(false);
            statLabel3.setEnabled(false);
            statLabel4.setEnabled(false);
        }else{
            
            int draughtOwnInt = Math.round(draughtOwn);
            int draughtTargetInt = Math.round(draughtTarget);
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM , HH:mm");
            
            if (validFrom == null){
                validFrom = new Date();
            }
            
            if (validTo == null){
                validTo = new Date();
            }
            
            String validFromStr = sdf.format(validFrom);
            String validToStr = sdf.format(validTo);
            
            if (errorCodeOwn == 17 && errorCodeTarget == 17){
                statusLabel.setText("Failed");
                statusLabel.setForeground(Color.RED);
                statLabel5.setText("No data for region");

                statLabel1.setText("N/A");
                statLabel2.setText("N/A");
                statLabel3.setText("N/A");
                statLabel4.setText("N/A");
                
                statLabel1.setEnabled(false);
                statLabel2.setEnabled(false);
                statLabel3.setEnabled(false);
                statLabel4.setEnabled(false);
                
                statLabel6.setText("Retrying in a few min");
                return;
            }
            if (errorCodeOwn == 17 || errorCodeTarget == 17){
                statusLabel.setText("Limited");
                statusLabel.setForeground(Color.RED);
                statLabel5.setText("No data for one of the ship regions");

                statLabel1.setText(validFromStr);
                statLabel2.setText(validToStr);
                statLabel3.setText(Integer.toString(draughtOwnInt)  + " meters");
                statLabel4.setText(Integer.toString(draughtTargetInt)  + " meters");
                
                statLabel6.setText("Retrying in a few min");
                statLabel7.setText("");
                return;
            }
            
            if (errorCodeOwn == 18 && errorCodeTarget == 18){
                statusLabel.setText("Limited");
                statusLabel.setForeground(Color.ORANGE);

                statLabel1.setText(validFromStr);
                statLabel2.setText(validToStr);
                statLabel3.setText(Integer.toString(draughtOwnInt)  + " meters");
                statLabel4.setText(Integer.toString(draughtTargetInt)  + " meters");
                statLabel5.setText("No tide data available for region");
                statLabel6.setText("Retrying in");
                statLabel7.setText("a few minutes");
                
                return;
            }
            
            if (errorCodeOwn == 18 || errorCodeTarget == 18){
                statusLabel.setText("Limited");
                statusLabel.setForeground(Color.ORANGE);

                statLabel1.setText(validFromStr);
                statLabel2.setText(validToStr);
                statLabel3.setText(Integer.toString(draughtOwnInt)  + " meters");
                statLabel4.setText(Integer.toString(draughtTargetInt)  + " meters");
                statLabel5.setText("No tide data for one of the vessel");
                statLabel6.setText("Retrying in");
                statLabel7.setText("a few minutes");
    
                return;
            }

            if (errorCodeOwn == 0 && errorCodeTarget == 0){
                statusLabel.setText("Success");
                statusLabel.setForeground(Color.GREEN);
                statLabel1.setText(validFromStr);
                statLabel2.setText(validToStr);
                statLabel3.setText(Integer.toString(draughtOwnInt) + " meters");
                statLabel4.setText(Integer.toString(draughtTargetInt)  + " meters");
                statLabel5.setText("");
                statLabel6.setText("");
                statLabel7.setText("");
                
                statLabel1.setEnabled(true);
                statLabel2.setEnabled(true);
                statLabel3.setEnabled(true);
                statLabel4.setEnabled(true);
                return;

            }

            if (polygonsOwn.size() == 0 && polygonsTarget.size() == 0){
                statusLabel.setText("Success");
                statusLabel.setForeground(Color.GREEN);
                statLabel1.setText(validFromStr);
                statLabel2.setText(validToStr);
                statLabel3.setText(Integer.toString(draughtOwnInt)  + " meters");
                statLabel4.setText(Integer.toString(draughtTargetInt)  + " meters");
                statLabel5.setText("Entire region is Go");
                statLabel6.setText("");
                statLabel7.setText("");
                
                statLabel1.setEnabled(true);
                statLabel2.setEnabled(true);
                statLabel3.setEnabled(true);
                statLabel4.setEnabled(true);
                return;

            }
        }
    }
    
    public void inactive(){
        statusLabel.setEnabled(false);
        statLabel1.setEnabled(false);
        statLabel2.setEnabled(false);
        statLabel3.setEnabled(false);
        statLabel4.setEnabled(false);
        statLabel5.setEnabled(false);

        statusLabel.setText("Inactive");
        statLabel1.setText("N/A");
        statLabel2.setText("N/A");
        statLabel3.setText("N/A");
        statLabel4.setText("N/A");
        statLabel5.setText("");
        statLabel6.setText("");
        statLabel7.setText("");
    }
            
            
    
    @Override
    public void findAndInit(Object obj) {

        if (aisHandler == null && obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
        }
        if (dynamicNogoHandler == null && obj instanceof DynamicNogoHandler) {
            dynamicNogoHandler = (DynamicNogoHandler) obj;
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
        return "Dynamic NoGo";
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInDefaultLayout() {
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean includeInPanelsMenu() {
        return true;
    }
}
