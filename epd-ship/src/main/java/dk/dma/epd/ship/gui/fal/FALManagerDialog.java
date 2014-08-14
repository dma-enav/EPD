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
package dk.dma.epd.ship.gui.fal;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 * Route manager dialog
 */
public class FALManagerDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private FALManagerPanel falPanel;

    /**
     * Constructor
     * 
     * @param parent
     *            the parent frame
     */
    public FALManagerDialog(JFrame parent) {
        super(parent, "FAL Report Manager", false);

        setSize(600, 430);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);

        // Add the route panel
        falPanel = new FALManagerPanel(this);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(falPanel, BorderLayout.CENTER);

        getRootPane().setDefaultButton(falPanel.getCloseButton());

        setOpacity((float) 0.95);

    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        setOpacity((float) 0.95);

        //
        // getRootPane ().setOpaque (false);
        //
        // getContentPane ().setBackground(new Color (48, 48, 48, 200));

        // routePanel.getRootPane ().setOpaque (false);
        // routePanel.setBackground(new Color (48, 48, 48, 200));

    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        // this.repaint();
        // getRootPane ().setOpaque (false);
        // getContentPane ().setBackground(new Color (48, 48, 48, 200));

        // System.out.println("Set location yo");

        //
        // if (routePanel != null){
        // routePanel.getRootPane ().setOpaque (false);
        // routePanel.setBackground(new Color (48, 48, 48, 200));
        // }

    }

}
