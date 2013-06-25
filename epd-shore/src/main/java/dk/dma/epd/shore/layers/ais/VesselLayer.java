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
package dk.dma.epd.shore.layers.ais;

import javax.swing.ImageIcon;

import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.utils.StaticImages;

/**
 * Vessel layer that creates a directed vessel with icon
 *
 * @author Claes N. Ladefoged, claesnl@gmail.com
 *
 */
public class VesselLayer extends CenterRaster {
    private static final long serialVersionUID = 1L;
    private long MMSI;
    private ImageIcon vesselIcon;
    private double lat;
    private double lon;
    private double trueHeading;
    private String shipType;
    private StaticImages staticImages;
    private Vessel vessel;

    /**
     * Initialize a vessel with default icon
     * @param MMSI Key of vessel
     * @param vessel
     */
    public VesselLayer(long MMSI, Vessel vessel) {
        super(0, 0, 24, 24, EPDShore.getStaticImages().getVesselWhite());
        this.staticImages = EPDShore.getStaticImages();
        this.MMSI = MMSI;

        this.vessel = vessel;
    }

    /**
     * Rotates vessel icon
     * @param trueHeading Direction of vessel icon
     */
    public void setHeading(double trueHeading) {
        if (this.trueHeading != trueHeading) {
            this.trueHeading = trueHeading;
            this.setRotationAngle(Math.toRadians(trueHeading - 90));
        }
    }



    public Vessel getVessel() {
        return vessel;
    }

    /**
     * Moves the vessel icon
     * @param lat Latitude position of vessel icon
     * @param lon Longitude position of vessel icon
     */
    public void setLocation(double lat, double lon) {
        if (this.lat != lat || this.lon != lon) {
            this.lat = lat;
            this.lon = lon;
            this.setLat(lat);
            this.setLon(lon);
        }
    }

    /**
     * Changes vessel icon based on ship type
     * @param shipType Ship type relative to "GUIDELINES FOR THE INSTALLATION OF A SHIPBORNE AUTOMATIC IDENTIFICATION SYSTEM (AIS)"
     */
    public void setImageIcon(String shipType) {
        System.out.println("This shiptype: " + this.shipType + " new shipType = " + shipType);
        if(this.shipType != shipType) {
            this.shipType = shipType;
            if (shipType.startsWith("Passenger")) {
                vesselIcon = staticImages.getVesselBlue();
            } else if (shipType.startsWith("Cargo")) {
                vesselIcon = staticImages.getVesselLightgreen();
            } else if (shipType.startsWith("Tug")) {
                vesselIcon = staticImages.getVesselCyan();
            } else if (shipType.startsWith("Tanker")) {
                vesselIcon = staticImages.getVesselRed();
            } else if (shipType.startsWith("Port")) {
                vesselIcon = staticImages.getVesselCyan();
            } else if (shipType.startsWith("Dredging")) {
                vesselIcon = staticImages.getVesselWhite0();
            } else if (shipType.startsWith("Sailing")) {
                vesselIcon = staticImages.getVesselBrown();
            } else if (shipType.startsWith("Pleasure")) {
                vesselIcon = staticImages.getVesselMagenta();
            } else if (shipType.startsWith("Sar")) {
                vesselIcon = staticImages.getVesselCyan();
            } else if (shipType.startsWith("Fishing")) {
                vesselIcon = staticImages.getVesselBrown();
            } else if (shipType.startsWith("Diving")) {
                vesselIcon = staticImages.getVesselCyan();
            } else if (shipType.startsWith("Pilot")) {
                vesselIcon = staticImages.getVesselCyan();
            } else if (shipType.startsWith("Undefined")) {
                vesselIcon = staticImages.getVesselLightgray();
            } else if (shipType.startsWith("Unknown")) {
                vesselIcon = staticImages.getVesselLightgray();
            } else {
                vesselIcon = staticImages.getVesselLightgray();
            }
            /*
            if (shipType.startsWith("Passenger"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/blue1_90.png"));
            else if (shipType.startsWith("Cargo"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/lightgreen1_90.png"));
            else if (shipType.startsWith("Tug"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
            else if (shipType.startsWith("Tanker"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/red1_90.png"));
            else if (shipType.startsWith("Port"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
            else if (shipType.startsWith("Dredging"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/white0.png"));
            else if (shipType.startsWith("Sailing"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/brown1_90.png"));
            else if (shipType.startsWith("Pleasure"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/magenta1_90.png"));
            else if (shipType.startsWith("Sar"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
            else if (shipType.startsWith("Fishing"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/brown1_90.png"));
            else if (shipType.startsWith("Diving"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
            else if (shipType.startsWith("Pilot"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/cyan1_90.png"));
            else if (shipType.startsWith("Undefined"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/lightgray1_90.png"));
            else if (shipType.startsWith("Unknown"))
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/lightgray1_90.png"));
            else {
                vesselIcon = new ImageIcon(ESD.class.getResource("/images/vesselIcons/lightgray1_90.png"));
            }
            */
            this.setImageIcon(vesselIcon);
        }
    }

    /**
     * Get the MMSI attached to the layer
     * @return MMSI Key of vessel
     */
    public long getMMSI() {
        return this.MMSI;
    }
}
