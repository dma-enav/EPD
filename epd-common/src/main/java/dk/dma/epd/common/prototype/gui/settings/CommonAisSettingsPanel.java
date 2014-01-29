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

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.border.TitledBorder;

/**
 * 
 * @author adamduehansen
 *
 */
public class CommonAisSettingsPanel extends BaseSettingsPanel {

    private static final long serialVersionUID = 1L;
    private JPanel transponderPanel;

    /**
     * Constructs a new CommonAisSettingsPanel object.
     */
    public CommonAisSettingsPanel() {
        super("AIS", new ImageIcon(CommonAisSettingsPanel.class.getResource
                ("/images/settings/ais.png")));
        this.setLayout(null);
        
        
        /************** Transponder settings ***************/
        
        transponderPanel = new JPanel();
        transponderPanel.setBounds(6, 6, 438, 82);
        transponderPanel.setBorder(new TitledBorder(
                null, "Transponder Settings", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        transponderPanel.setLayout(null);
        
        // HTTP settings panel components.
        JCheckBox chckbxAllowSending = new JCheckBox("Allow Sending");
        chckbxAllowSending.setBounds(16, 20, 128, 20);
        transponderPanel.add(chckbxAllowSending);
        
        JCheckBox checkBoxStrictTimeout = new JCheckBox("Strict timeout");
        checkBoxStrictTimeout.setBounds(16, 45, 128, 20);
        transponderPanel.add(checkBoxStrictTimeout);
        
        this.add(transponderPanel);
    }
    
 
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkSettingsChanged() {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doLoadSettings() {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doSaveSettings() {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void fireSettingsChanged() {
        // TODO Auto-generated method stub
        
    }

    public JPanel getTransponderPanel() {
        return this.transponderPanel;
    }
}
