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
package dk.dma.epd.common.prototype.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import dk.dma.epd.common.prototype.event.GoBackMouseListener;
import dk.dma.epd.common.prototype.event.HistoryListener;
import dk.dma.epd.common.prototype.gui.route.ButtonLabelCommon;
import dk.dma.epd.common.prototype.gui.views.ChartPanelCommon;

/**
 * This class is a GUI component of going backwards in history.
 * 
 * @author adamduehansen
 *
 */
public class GoBackButton extends ButtonLabelCommon {
    
    /****************\
    * private fields *
    \****************/
    private static final long serialVersionUID = 1L;
    private HistoryListener historyListener;    // The history listener associated with this button. 
    private ChartPanelCommon chartPanel;        // The chart panel which controls projection.
    private GoForwardButton goForwardButton;    // The opposite button of this: the going forward button.
    
    public static Font defaultFont = new Font("Arial", Font.PLAIN, 11);
    public static Color textColor = new Color(237, 237, 237);
    public static Color clickedColor = new Color(80, 80, 80);
    public static Color standardColor = new Color(128, 128, 128);
    public static Color borderColor =  new Color(45, 45, 45);
    
    public static Border toolPaddingBorder = BorderFactory.createMatteBorder(2, 2, 2, 2, borderColor);
    public static Border toolInnerEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
            new Color(37, 37, 37), borderColor);
    
    /**
     * Constructs a new button with an arrow.
     * 
     * @param icon 
     *          Path to arrow.
     */
    public GoBackButton() {
        super(new ImageIcon(GoBackButton.class.getResource("/images/navigation_buttons/go-back.png")));
    }
    
    /****************\
    * public methods *
    \****************/
    
    public void styleButton(final JLabel label){

        label.setPreferredSize(new Dimension(80, 25));
        label.setFont(defaultFont);
        label.setForeground(textColor);
        label.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,borderColor));
        label.setBackground(standardColor);
        label.setOpaque(true);
        
        label.setHorizontalAlignment(SwingConstants.CENTER);
        
        label.addMouseListener(new MouseAdapter() {  
            @Override
            public void mousePressed(MouseEvent e) {
                if (label.isEnabled()){
                label.setBackground(clickedColor);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (label.isEnabled()){
                    
                label.setBackground(standardColor);
                }
            }
        });
    }

    public void styleIconButton(final JLabel label){
        label.setPreferredSize(new Dimension(40, 25));
        
        label.setOpaque(true);
        label.setBorder(toolPaddingBorder);
        label.setBackground(standardColor);
        
        label.addMouseListener(new MouseAdapter() {  
            @Override
            public void mousePressed(MouseEvent e) {
                if (label.isEnabled()){
                    label.setBackground(clickedColor);
                    label.setBorder(BorderFactory.createCompoundBorder(toolPaddingBorder, toolInnerEtchedBorder));
                    label.setOpaque(true);
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (label.isEnabled()){
                    
                    label.setBorder(toolPaddingBorder);
                    label.setBackground(standardColor);
                }
            }
        });
    }
    
    /**
     * Adds a HistoryListener to the button.
     * 
     * @param historyListener
     */
    public void setHistoryListener(HistoryListener historyListener) {
        this.historyListener = historyListener;
    }
    
    /**
     * Sets the ChartPanel for the button.
     * 
     * @param chartPanel
     */
    public void setChartPanel(ChartPanelCommon chartPanel) {
        this.chartPanel = chartPanel;
    }
    
    /**
     * Sets the GoForwardButton of the button.
     * 
     * @param goForwardButton
     */
    public void setGoForwardButton(GoForwardButton goForwardButton) {
        this.goForwardButton = goForwardButton;
    }
    
    public void initMouseListener() {
        addMouseListener(new GoBackMouseListener(this, this.goForwardButton, this.historyListener, this.chartPanel));
    }
}
