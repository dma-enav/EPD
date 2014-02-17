package dk.dma.epd.common.prototype.enavcloud.intendedroute;

import java.util.Date;

public class Waypoint {
    
    private double latitude;
    private double longitude;
    private Double rot;
    private Date eta;
    private Double turnRad;
    private Leg outLeg;
    
    public Waypoint() {
        
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Double getRot() {
        return rot;
    }

    public void setRot(Double rot) {
        this.rot = rot;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public Double getTurnRad() {
        return turnRad;
    }

    public void setTurnRad(Double turnRad) {
        this.turnRad = turnRad;
    }
    
    public Leg getOutLeg() {
        return outLeg;
    }
    
    public void setOutLeg(Leg outLeg) {
        this.outLeg = outLeg;
    }
    
}
