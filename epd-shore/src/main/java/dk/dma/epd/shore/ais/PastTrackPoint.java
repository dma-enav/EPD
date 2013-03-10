package dk.dma.epd.shore.ais;

import java.util.Date;

import dk.dma.enav.model.geometry.Position;

public class PastTrackPoint {

    Date date;
    Position position;
    
    public PastTrackPoint(Date date, Position position){
        this.date = date;
        this.position = position;
    }

    public Date getDate() {
        return date;
    }

    public Position getPosition() {
        return position;
    }

public String toString(){
    return "Date " + date + " Position: " + position; 
}
    
}
