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
package dk.dma.epd.ship.gui.panels.VOCT;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.gui.voct.EffortAllocationPanelCommon;
import dk.dma.epd.common.prototype.model.voct.sardata.EffortAllocationData;
import dk.dma.epd.common.prototype.model.voct.sardata.SARData;
import dk.dma.epd.common.text.Formatter;

public class EffortAllocationPanel extends EffortAllocationPanelCommon {

    private static final long serialVersionUID = 1L;
    JLabel poDVal;
    JLabel searchAreaSizeVal;
    JLabel searchCraftGroundSpeedVal;
    JLabel trackSpacingVal;
    JSpinner timeSpentSearchingVal;

    public EffortAllocationPanel() {
        this.setBorder(new TitledBorder(null, "Effort Allocation", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        //
        // GridBagConstraints gbc_effortAllocationPanel = new GridBagConstraints();
        // gbc_effortAllocationPanel.insets = new Insets(0, 0, 5, 0);
        // gbc_effortAllocationPanel.fill = GridBagConstraints.BOTH;
        // gbc_effortAllocationPanel.gridx = 0;
        // gbc_effortAllocationPanel.gridy = 7;
        // sarStartedPanel.add(effortAllocationPanel, gbc_effortAllocationPanel);

        GridBagLayout gbl_effortAllocationPanel = new GridBagLayout();
        gbl_effortAllocationPanel.columnWidths = new int[] { 0, 0, 0 };
        gbl_effortAllocationPanel.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
        gbl_effortAllocationPanel.columnWeights = new double[] { 1.0, 1.0, Double.MIN_VALUE };
        gbl_effortAllocationPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE };
        setLayout(gbl_effortAllocationPanel);

        JLabel lblProbabilityOfDetection = new JLabel("Probability of Detection:");
        GridBagConstraints gbc_lblProbabilityOfDetection = new GridBagConstraints();
        gbc_lblProbabilityOfDetection.insets = new Insets(0, 0, 5, 5);
        gbc_lblProbabilityOfDetection.gridx = 0;
        gbc_lblProbabilityOfDetection.gridy = 0;
        add(lblProbabilityOfDetection, gbc_lblProbabilityOfDetection);

        poDVal = new JLabel("N/A");
        GridBagConstraints gbc_PoDVal = new GridBagConstraints();
        gbc_PoDVal.insets = new Insets(0, 0, 5, 0);
        gbc_PoDVal.gridx = 1;
        gbc_PoDVal.gridy = 0;
        add(poDVal, gbc_PoDVal);

        JLabel lblEffectiveSearchArea = new JLabel("Effective Search Area:");
        GridBagConstraints gbc_lblEffectiveSearchArea = new GridBagConstraints();
        gbc_lblEffectiveSearchArea.insets = new Insets(0, 0, 5, 5);
        gbc_lblEffectiveSearchArea.gridx = 0;
        gbc_lblEffectiveSearchArea.gridy = 1;
        add(lblEffectiveSearchArea, gbc_lblEffectiveSearchArea);

        searchAreaSizeVal = new JLabel("N/A");
        GridBagConstraints gbc_searchAreaSizeVal = new GridBagConstraints();
        gbc_searchAreaSizeVal.insets = new Insets(0, 0, 5, 0);
        gbc_searchAreaSizeVal.gridx = 1;
        gbc_searchAreaSizeVal.gridy = 1;
        add(searchAreaSizeVal, gbc_searchAreaSizeVal);

        JLabel lblSearchCraftGround = new JLabel("Search Craft Ground Speed:");
        GridBagConstraints gbc_lblSearchCraftGround = new GridBagConstraints();
        gbc_lblSearchCraftGround.insets = new Insets(0, 0, 5, 5);
        gbc_lblSearchCraftGround.gridx = 0;
        gbc_lblSearchCraftGround.gridy = 2;
        add(lblSearchCraftGround, gbc_lblSearchCraftGround);

        searchCraftGroundSpeedVal = new JLabel("N/A");
        GridBagConstraints gbc_searchCraftGroundSpeedVal = new GridBagConstraints();
        gbc_searchCraftGroundSpeedVal.insets = new Insets(0, 0, 5, 0);
        gbc_searchCraftGroundSpeedVal.gridx = 1;
        gbc_searchCraftGroundSpeedVal.gridy = 2;
        add(searchCraftGroundSpeedVal, gbc_searchCraftGroundSpeedVal);

        JLabel lblTrackSpacing = new JLabel("Track Spacing:");
        GridBagConstraints gbc_lblTrackSpacing = new GridBagConstraints();
        gbc_lblTrackSpacing.insets = new Insets(0, 0, 5, 5);
        gbc_lblTrackSpacing.gridx = 0;
        gbc_lblTrackSpacing.gridy = 3;
        add(lblTrackSpacing, gbc_lblTrackSpacing);

        trackSpacingVal = new JLabel("N/A");
        GridBagConstraints gbc_trackSpacingVal = new GridBagConstraints();
        gbc_trackSpacingVal.insets = new Insets(0, 0, 5, 0);
        gbc_trackSpacingVal.gridx = 1;
        gbc_trackSpacingVal.gridy = 3;
        add(trackSpacingVal, gbc_trackSpacingVal);

        JLabel lblTimeSpentSearching = new JLabel("Time Spent Searching:");
        GridBagConstraints gbc_lblTimeSpentSearching = new GridBagConstraints();
        gbc_lblTimeSpentSearching.insets = new Insets(0, 0, 0, 5);
        gbc_lblTimeSpentSearching.gridx = 0;
        gbc_lblTimeSpentSearching.gridy = 4;
        add(lblTimeSpentSearching, gbc_lblTimeSpentSearching);

        timeSpentSearchingVal = new JSpinner();
        timeSpentSearchingVal.setEnabled(false);
        GridBagConstraints gbc_timeSpentSearchingVal = new GridBagConstraints();
        gbc_timeSpentSearchingVal.gridx = 1;
        gbc_timeSpentSearchingVal.gridy = 4;
        add(timeSpentSearchingVal, gbc_timeSpentSearchingVal);
    }

    @Override
    public void resetValues() {
        poDVal.setText("N/A");
        searchAreaSizeVal.setText("N/A");
        searchCraftGroundSpeedVal.setText("N/A");
        trackSpacingVal.setText("N/A");
        timeSpentSearchingVal.setValue(0);
        timeSpentSearchingVal.setEnabled(false);

    }

    @Override
    public void effortAllocationComplete(SARData data) {

        EffortAllocationData effortAllocationData = data.getEffortAllocationData().get(0L);

        poDVal.setText(effortAllocationData.getPod() * 100 + "%");
        searchAreaSizeVal.setText(Formatter.formatDouble(effortAllocationData.getEffectiveAreaSize(), 2) + " nm2");
        searchCraftGroundSpeedVal.setText(Formatter.formatDouble(effortAllocationData.getGroundSpeed(), 0) + " knots");

        trackSpacingVal.setText(Formatter.formatDouble(effortAllocationData.getTrackSpacing(), 2) + " nm");

        timeSpentSearchingVal.setValue(effortAllocationData.getSearchTime());

    }

}
