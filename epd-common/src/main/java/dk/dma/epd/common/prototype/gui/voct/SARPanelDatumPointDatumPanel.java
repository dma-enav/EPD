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
package dk.dma.epd.common.prototype.gui.voct;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class SARPanelDatumPointDatumPanel extends JPanel {

    private JLabel datumLatDownWind;
    private JLabel datumLonDownWind;
    private JLabel rdvDistanceDownWind;
    private JLabel datumRadiusDownWind;
    private JLabel datumLatMin;
    private JLabel datumLonMin;
    private JLabel rdvDistanceMin;
    private JLabel datumRadiusMin;
    private JLabel datumLatMax;
    private JLabel datumLonMax;
    private JLabel rdvDistanceMax;
    private JLabel datumRadiusMax;

    private static final long serialVersionUID = 1L;

    public SARPanelDatumPointDatumPanel() {

        setBorder(new TitledBorder(null, "Position of Datum to LKP",
                TitledBorder.LEADING, TitledBorder.TOP, null, null));
        GridBagConstraints gbc_datumPanel = new GridBagConstraints();
        gbc_datumPanel.insets = new Insets(0, 0, 5, 0);
        gbc_datumPanel.fill = GridBagConstraints.BOTH;
        gbc_datumPanel.gridx = 0;
        gbc_datumPanel.gridy = 4;

        GridBagLayout gbl_datumPanel = new GridBagLayout();
        gbl_datumPanel.columnWidths = new int[] { 0, 0, 0, 0, 0 };
        gbl_datumPanel.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_datumPanel.columnWeights = new double[] { 1.0, 1.0, 1.0, 1.0, 1.0 };
        gbl_datumPanel.rowWeights = new double[] { 1.0, 1.0, 1.0, 1.0 };
        setLayout(gbl_datumPanel);

        JLabel lblLatitude = new JLabel("Latitude");
        GridBagConstraints gbc_lblLatitude = new GridBagConstraints();
        gbc_lblLatitude.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblLatitude.insets = new Insets(0, 0, 5, 5);
        gbc_lblLatitude.gridx = 1;
        gbc_lblLatitude.gridy = 0;
        add(lblLatitude, gbc_lblLatitude);

        JLabel lblLongitude = new JLabel("Longitude");
        GridBagConstraints gbc_lblLongitude = new GridBagConstraints();
        gbc_lblLongitude.insets = new Insets(0, 0, 5, 5);
        gbc_lblLongitude.gridx = 2;
        gbc_lblLongitude.gridy = 0;
        add(lblLongitude, gbc_lblLongitude);

        JLabel lblRdv = new JLabel("RDV");
        GridBagConstraints gbc_lblRdv = new GridBagConstraints();
        gbc_lblRdv.insets = new Insets(0, 0, 5, 5);
        gbc_lblRdv.gridx = 3;
        gbc_lblRdv.gridy = 0;
        add(lblRdv, gbc_lblRdv);

        JLabel lblRadius = new JLabel("Radius");
        GridBagConstraints gbc_lblRadius = new GridBagConstraints();
        gbc_lblRadius.insets = new Insets(0, 0, 5, 0);
        gbc_lblRadius.gridx = 4;
        gbc_lblRadius.gridy = 0;
        add(lblRadius, gbc_lblRadius);

        JLabel lblDownwind = new JLabel("Downwind:");
        GridBagConstraints gbc_lblDownwind = new GridBagConstraints();
        gbc_lblDownwind.insets = new Insets(0, 0, 5, 5);
        gbc_lblDownwind.gridx = 0;
        gbc_lblDownwind.gridy = 1;
        add(lblDownwind, gbc_lblDownwind);

        datumLatDownWind = new JLabel("N/A");
        GridBagConstraints gbc_datumLat = new GridBagConstraints();
        gbc_datumLat.insets = new Insets(0, 0, 5, 5);
        gbc_datumLat.gridx = 1;
        gbc_datumLat.gridy = 1;
        add(datumLatDownWind, gbc_datumLat);

        datumLonDownWind = new JLabel("N/A");
        GridBagConstraints gbc_datumLon = new GridBagConstraints();
        gbc_datumLon.insets = new Insets(0, 0, 5, 5);
        gbc_datumLon.gridx = 2;
        gbc_datumLon.gridy = 1;
        add(datumLonDownWind, gbc_datumLon);

        rdvDistanceDownWind = new JLabel("N/A");
        GridBagConstraints gbc_rdvDistance = new GridBagConstraints();
        gbc_rdvDistance.insets = new Insets(0, 0, 5, 5);
        gbc_rdvDistance.gridx = 3;
        gbc_rdvDistance.gridy = 1;
        add(rdvDistanceDownWind, gbc_rdvDistance);

        datumRadiusDownWind = new JLabel("N/A");
        GridBagConstraints gbc_datumRadius = new GridBagConstraints();
        gbc_datumRadius.insets = new Insets(0, 0, 5, 0);
        gbc_datumRadius.gridx = 4;
        gbc_datumRadius.gridy = 1;
        add(datumRadiusDownWind, gbc_datumRadius);

        JLabel lblMin = new JLabel("Min");
        GridBagConstraints gbc_lblMin = new GridBagConstraints();
        gbc_lblMin.insets = new Insets(0, 0, 5, 5);
        gbc_lblMin.gridx = 0;
        gbc_lblMin.gridy = 2;
        add(lblMin, gbc_lblMin);

        datumLatMin = new JLabel("N/A");
        GridBagConstraints gbc_datumLatMin = new GridBagConstraints();
        gbc_datumLatMin.insets = new Insets(0, 0, 5, 5);
        gbc_datumLatMin.gridx = 1;
        gbc_datumLatMin.gridy = 2;
        add(datumLatMin, gbc_datumLatMin);

        datumLonMin = new JLabel("N/A");
        GridBagConstraints gbc_datumLonMin = new GridBagConstraints();
        gbc_datumLonMin.insets = new Insets(0, 0, 5, 5);
        gbc_datumLonMin.gridx = 2;
        gbc_datumLonMin.gridy = 2;
        add(datumLonMin, gbc_datumLonMin);

        rdvDistanceMin = new JLabel("N/A");
        GridBagConstraints gbc_rdvDistanceMin = new GridBagConstraints();
        gbc_rdvDistanceMin.insets = new Insets(0, 0, 5, 5);
        gbc_rdvDistanceMin.gridx = 3;
        gbc_rdvDistanceMin.gridy = 2;
        add(rdvDistanceMin, gbc_rdvDistanceMin);

        datumRadiusMin = new JLabel("N/A");
        GridBagConstraints gbc_datumRadiusMin = new GridBagConstraints();
        gbc_datumRadiusMin.insets = new Insets(0, 0, 5, 0);
        gbc_datumRadiusMin.gridx = 4;
        gbc_datumRadiusMin.gridy = 2;
        add(datumRadiusMin, gbc_datumRadiusMin);

        JLabel lblMax = new JLabel("Max");
        GridBagConstraints gbc_lblMax = new GridBagConstraints();
        gbc_lblMax.insets = new Insets(0, 0, 0, 5);
        gbc_lblMax.gridx = 0;
        gbc_lblMax.gridy = 3;
        add(lblMax, gbc_lblMax);

        datumLatMax = new JLabel("N/A");
        GridBagConstraints gbc_datumLatMax = new GridBagConstraints();
        gbc_datumLatMax.insets = new Insets(0, 0, 0, 5);
        gbc_datumLatMax.gridx = 1;
        gbc_datumLatMax.gridy = 3;
        add(datumLatMax, gbc_datumLatMax);

        datumLonMax = new JLabel("N/A");
        GridBagConstraints gbc_datumLonMax = new GridBagConstraints();
        gbc_datumLonMax.insets = new Insets(0, 0, 0, 5);
        gbc_datumLonMax.gridx = 2;
        gbc_datumLonMax.gridy = 3;
        add(datumLonMax, gbc_datumLonMax);

        rdvDistanceMax = new JLabel("N/A");
        GridBagConstraints gbc_rdvDistanceMax = new GridBagConstraints();
        gbc_rdvDistanceMax.insets = new Insets(0, 0, 0, 5);
        gbc_rdvDistanceMax.gridx = 3;
        gbc_rdvDistanceMax.gridy = 3;
        add(rdvDistanceMax, gbc_rdvDistanceMax);

        datumRadiusMax = new JLabel("N/A");
        GridBagConstraints gbc_datumRadiusMax = new GridBagConstraints();
        gbc_datumRadiusMax.gridx = 4;
        gbc_datumRadiusMax.gridy = 3;
        add(datumRadiusMax, gbc_datumRadiusMax);
    }

    public void setDatumLatDownWind(String datumLatTxt) {
        datumLatDownWind.setText(datumLatTxt);
    }

    public void setDatumLonDownWind(String datumLonTxt) {
        datumLonDownWind.setText(datumLonTxt);
    }

    public void setrdvDistanceDownWind(String rdvDistanceTxt) {
        rdvDistanceDownWind.setText(rdvDistanceTxt);
    }

    public void setdatumRadiusDownWind(String datumRadiusTxt) {
        datumRadiusDownWind.setText(datumRadiusTxt);
    }

    public void setDatumLatMin(String datumLatTxt) {
        datumLatMin.setText(datumLatTxt);
    }

    public void setDatumLonMin(String datumLonTxt) {
        datumLonMin.setText(datumLonTxt);
    }

    public void setrdvDistanceMin(String rdvDistanceTxt) {
        rdvDistanceMin.setText(rdvDistanceTxt);
    }

    public void setdatumRadiusMin(String datumRadiusTxt) {
        datumRadiusMin.setText(datumRadiusTxt);
    }
    
    public void setDatumLatMax(String datumLatTxt) {
        datumLatMax.setText(datumLatTxt);
    }

    public void setDatumLonMax(String datumLonTxt) {
        datumLonMax.setText(datumLonTxt);
    }

    public void setrdvDistanceMax(String rdvDistanceTxt) {
        rdvDistanceMax.setText(rdvDistanceTxt);
    }

    public void setdatumRadiusMax(String datumRadiusTxt) {
        datumRadiusMax.setText(datumRadiusTxt);
    }
}
