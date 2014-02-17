package dk.dma.epd.common.prototype.enavcloud.intendedroute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IntendedRouteMessage {

    private int activeWpIndex;
    private List<Date> plannedEtas = new ArrayList<>();
    private ArrayList<Waypoint> waypoints = new ArrayList<>();

    public IntendedRouteMessage() {

    }

    public int getActiveWpIndex() {
        return activeWpIndex;
    }

    public void setActiveWpIndex(int activeWpIndex) {
        this.activeWpIndex = activeWpIndex;
    }

    public List<Date> getPlannedEtas() {
        return plannedEtas;
    }

    public void setPlannedEtas(List<Date> plannedEtas) {
        this.plannedEtas = plannedEtas;
    }

    public ArrayList<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

}
