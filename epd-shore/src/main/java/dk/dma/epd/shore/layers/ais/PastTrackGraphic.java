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
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.omGraphics.OMGraphicList;

import dk.dma.enav.model.geometry.CoordinateSystem;
import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.shore.ais.PastTrackPoint;

/**
 * Graphic for intended route
 */
public class PastTrackGraphic extends OMGraphicList {
    private static final long serialVersionUID = 1L;
    
    private static Color LEG_COLOR = Color.black;
    private static Color LOST_LEG_COLOR = Color.lightGray;

    private PastTrackLegGraphic activeWpLine;
    private double[] activeWpLineLL = new double[4];
    private String name;
    private boolean arrowsVisible;
    private long mmsi = -1;

    private List<PastTrackLegGraphic> routeLegs = new ArrayList<>();
    private List<PastTrackWpCircle> routeWps = new ArrayList<>();
    
    private static final Logger LOG = LoggerFactory.getLogger(PastTrackGraphic.class);

    public PastTrackGraphic() {
        super();
        Position nullGeoLocation = Position.create(0, 0);
        activeWpLine = new PastTrackLegGraphic(0, this, true,
                nullGeoLocation, nullGeoLocation, LEG_COLOR);
        setVisible(false);
    }
    
    

    public long getMmsi() {
        return mmsi;
    }



    public void setMmsi(long mmsi) {
        this.mmsi = mmsi;
    }


    /**
     * Determines when a past track leg can be presumed "lost", caused by
     * missing readings from the AIS transponder.
     * 
     * @param start start point of leg
     * @param end end point of leg
     * @return if the start and end point constitutes a lost leg
     */
    private boolean presumeLostLeg(PastTrackPoint start, PastTrackPoint end) {
        double dist = end.getPosition().distanceTo(start.getPosition(), CoordinateSystem.CARTESIAN);
        long time = (end.getDate().getTime() - start.getDate().getTime()) / 1000L; // seconds
        
        // The start and end points are actually from a filtered set of AIS readings, 
        // with a minimum distance between them of at least AisSettings.pastTrackMinDist,
        // so, we cannot merely look at the time between the readings.
        // Instead, we check the speed between readings.
        
        return time > 60  &&        // More than 1 minute between readings
               dist / time > 1.0;   // speed higher than 1 m/s
    }

    /**
     * Adds a new past track leg line
     * @param index index of leg in list of past track records
     * @param start start point of leg
     * @param end end point of leg
     */
    private void makeLegLine(int index, PastTrackPoint start, PastTrackPoint end) {
    
        Color legColor = presumeLostLeg(start, end) ? LOST_LEG_COLOR : LEG_COLOR;
        
        PastTrackLegGraphic leg = new PastTrackLegGraphic(
                index, 
                this,
                false, 
                start.getPosition(), 
                end.getPosition(), 
                legColor);
        routeLegs.add(leg);
        add(leg);
    }

    private void makeWpCircle(int index, PastTrackPoint p) {
        PastTrackWpCircle wpCircle = new PastTrackWpCircle(this, index,
                p.getPosition().getLatitude(), p.getPosition().getLongitude(), 0, 0, 2, 2, p.getDate());
        wpCircle.setStroke(new BasicStroke(3));
        wpCircle.setLinePaint(LEG_COLOR);
        
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
            PastTrackPoint end = null;
            try {
                end = it.next();
            } catch (ConcurrentModificationException e) {
                LOG.error("Tried to iterate over pastTrackPoints, but failed even though ConcurrentSkipListSet should never throw this exception");
                LOG.error(e.getLocalizedMessage());
                return;
            }
                
            
            
            count++;
            makeWpCircle(count, end);
            makeLegLine(count, start, end);
            
            start = end;
        }
        

        activeWpLineLL[0] = pos.getLatitude();
        activeWpLineLL[1] = pos.getLongitude();
        activeWpLineLL[2] = start.getPosition().getLatitude();
        activeWpLineLL[3] = start.getPosition().getLongitude();
        activeWpLine.setLL(activeWpLineLL);
    }
}
