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
        super(0, 0, 24, 24, EPDShore.getInstance().getStaticImages().getVesselWhite());
        this.staticImages = EPDShore.getInstance().getStaticImages();
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
