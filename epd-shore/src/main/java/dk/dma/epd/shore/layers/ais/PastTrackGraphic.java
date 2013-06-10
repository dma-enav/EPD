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

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.shore.ais.PastTrackPoint;

/**
 * Graphic for intended route
 */
public class PastTrackGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;

    private PastTrackLegGraphic activeWpLine;
    private double[] activeWpLineLL = new double[4];
    private Color legColor = Color.black;
    private String name;
    private boolean arrowsVisible;
    private long mmsi = -1;

    private List<PastTrackLegGraphic> routeLegs = new ArrayList<>();
    private List<PastTrackWpCircle> routeWps = new ArrayList<>();

    public PastTrackGraphic() {
        super();
        Position nullGeoLocation = Position.create(0, 0);
        activeWpLine = new PastTrackLegGraphic(0, this, true,
                nullGeoLocation, nullGeoLocation, legColor);
        setVisible(false);
    }
    
    

    public long getMmsi() {
        return mmsi;
    }



    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }



    private void makeLegLine(int index, Position start, Position end) {
        PastTrackLegGraphic leg = new PastTrackLegGraphic(index, this,
                false, start, end, legColor);
        routeLegs.add(leg);
        add(leg);
    }

    private void makeWpCircle(int index, PastTrackPoint p) {
        PastTrackWpCircle wpCircle = new PastTrackWpCircle(this, index,
                p.getPosition().getLatitude(), p.getPosition().getLongitude(), 0, 0, 2, 2, p.getDate());
        wpCircle.setStroke(new BasicStroke(3));
        wpCircle.setLinePaint(legColor);
        
        routeWps.add(wpCircle);
        add(wpCircle);
    }



    public String getName() {
        return name;
    }

    public void showArrowHeads(boolean show) {
        if (this.arrowsVisible != show) {
            for (PastTrackLegGraphic routeLeg : routeLegs) {
                routeLeg.setArrows(show);
            }
            this.arrowsVisible = show;
        }
    }
    
    @Override
    public void setVisible(boolean visible){
        super.setVisible(visible);
        
        if (!visible){
            clear();
            routeLegs.clear();
            routeWps.clear();
        }
    }
    /**
     * updates gui PastTrack using any sort of Collection for improved performance
     * @param pastTrackPoints
     * @param pos
     */
    public void update(Collection<PastTrackPoint> pastTrackPoints, Position pos) {
     // Set visible if not visible
        if (!isVisible()) {
            setVisible(true);
        }

        clear();
        routeLegs.clear();
        routeWps.clear();
        
        add(activeWpLine);

        Iterator<PastTrackPoint> it = pastTrackPoints.iterator();
        
        if (!it.hasNext()) {
            return;
        }
        
        PastTrackPoint start = it.next();
        makeWpCircle(0, start);
        int count = 0;            

        while (it.hasNext()) {
            PastTrackPoint end = it.next();
            
            count++;
            makeWpCircle(count, end);
            makeLegLine(count, start.getPosition(),end.getPosition());
            
            start = end;
        }
        

        activeWpLineLL[0] = pos.getLatitude();
        activeWpLineLL[1] = pos.getLongitude();
        activeWpLineLL[2] = start.getPosition().getLatitude();
        activeWpLineLL[3] = start.getPosition().getLongitude();
        activeWpLine.setLL(activeWpLineLL);
    }

    /*public void update(List<PastTrackPoint> pastTrackPoints, Position pos) {
        
        // Set visible if not visible
        if (!isVisible()) {
            setVisible(true);
        }

        clear();
        routeLegs.clear();
        routeWps.clear();
        
        add(activeWpLine);
        
        
        if (pastTrackPoints.isEmpty()) {
            return;
        }

        List<Position> waypoints = new ArrayList<>();
        

        
        for (int i = 0; i < pastTrackPoints.size(); i++) {
            waypoints.add(pastTrackPoints.get(i).getPosition());
        }
        
//            List<Position> waypoints = cloudIntendedRoute.getWaypoints();
        // Make first WP circle
        makeWpCircle(0, waypoints.get(0));
        for (int i=0; i < waypoints.size() - 1; i++) {
            Position start = waypoints.get(i);
            Position end = waypoints.get(i + 1);
            
            // Make wp circle
            makeWpCircle(i + 1, end);
            
            // Make leg line
            makeLegLine(i + 1, start, end);
        }
        
        // Update leg to first waypoint
        Position activeWpPos = pastTrackPoints.get(pastTrackPoints.size()-1).getPosition();
        activeWpLineLL[0] = pos.getLatitude();
        activeWpLineLL[1] = pos.getLongitude();
        activeWpLineLL[2] = activeWpPos.getLatitude();
        activeWpLineLL[3] = activeWpPos.getLongitude();
        activeWpLine.setLL(activeWpLineLL);

    }*/
}
