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
package dk.dma.epd.ship.util.route;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage8;
import dk.dma.ais.message.AisPosition;
import dk.dma.ais.message.binary.AisApplicationMessage;
import dk.dma.ais.message.binary.BroadcastIntendedRoute;
import dk.dma.ais.proprietary.IProprietarySourceTag;
import dk.dma.ais.sentence.Vdm;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.util.function.Consumer;

/**
 * Receive AIS messages and inject intended route broadcasts
 */
public class AisRouteInject implements Consumer<AisMessage> {
    
    private List<TimePoint> route;
    private PrintWriter out;
    private int currentWpIndex;
    private int mmsi;
    private int routePoints = 15;
    private Date lastBroadcast;    
    
    public AisRouteInject(String outFilename, List<TimePoint> route, int mmsi) throws IOException {
        this.route = route;
        this.mmsi = mmsi;
        FileWriter outFile = new FileWriter(outFilename);
        out = new PrintWriter(outFile);
        currentWpIndex = 0;
    }

    @Override
    public void accept(AisMessage aisMessage) {
        IProprietarySourceTag tag = aisMessage.getSourceTag();
        if (tag != null) {
            out.println(tag.getSentence());
        }
        out.println(aisMessage.getVdm().getOrgLinesJoined());
        
        if (tag == null || tag.getTimestamp() == null) {
            return;
        }
        
        boolean passedWp = false;
        boolean minTimepassed = false;
        int wpToUse = currentWpIndex;
        
        // Determine if feed time has passed current WP time
        TimePoint currentWp = route.get(currentWpIndex);
        if (tag.getTimestamp().after(currentWp.getTime())) {
            passedWp = true;
            wpToUse++;
        } else {
            // Determine if minTime passed
            if (lastBroadcast != null) {
                long elapsed = tag.getTimestamp().getTime() - lastBroadcast.getTime();
                if (elapsed > 6 * 60 * 1000) {
                    minTimepassed = true;
                }
            }
        }
        
        if (!minTimepassed && !passedWp) {
            return;
        }
        
        // List of waypoint to use
        List<TimePoint> aisRoute = new ArrayList<>();
        for (int i=wpToUse; aisRoute.size() < routePoints && i < route.size(); i++) {
            aisRoute.add(route.get(i));
        }
        
        lastBroadcast = tag.getTimestamp();
        
        if (passedWp) {
            currentWpIndex++;
        }
        
        if (aisRoute.size() < 2) {
            return;
        }
        
        // Find duration in minutes
        Date start = aisRoute.get(0).getTime();
        Date end = aisRoute.get(aisRoute.size() - 1).getTime();
        long duration = end.getTime() - start.getTime();
        duration = duration / 1000 / 60;
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        
        // Make application specific message
        BroadcastIntendedRoute intendedRoute = new BroadcastIntendedRoute();
        intendedRoute.setStartMonth(cal.get(Calendar.MONTH) + 1);
        intendedRoute.setStartDay(cal.get(Calendar.DAY_OF_MONTH));
        intendedRoute.setStartHour(cal.get(Calendar.HOUR_OF_DAY));
        intendedRoute.setStartMin(cal.get(Calendar.MINUTE));
        intendedRoute.setDuration((int)duration);
        for (TimePoint point : aisRoute) {
            Position wp = Position.create(point.getLatitude(), point.getLongitude());
            if (wp.getLatitude() < 54 || wp.getLatitude() > 60 || wp.getLongitude() < 8 || wp.getLongitude() > 14) {
                System.out.println("ERROR: Wrong wp in AIS broadcast: " + wp + " TimePoint: " + point);
            }
            AisPosition aisPosition = new AisPosition(wp);
            intendedRoute.addWaypoint(aisPosition);
        }
        intendedRoute.setWaypointCount(intendedRoute.getWaypoints().size());
        intendedRoute.addWaypoint(new AisPosition(Position.create(0, 0)));
        
        AisMessage8 msg8 = new AisMessage8();
        msg8.setUserId(mmsi);
        msg8.setAppMessage(intendedRoute);
        Vdm vdm = new Vdm();
        vdm.setTalker("AI");
        vdm.setTotal(1);
        vdm.setNum(1);
        try {
            vdm.setMessageData(msg8);
        } catch (SixbitException e1) {
            e1.printStackTrace();
            System.exit(1);
        }
        vdm.setSequence(0);
        String encoded = vdm.getEncoded();
        out.println(encoded);
        
        // Verify
        try {
            vdm = new Vdm();
            vdm.parse(encoded);
            AisMessage msg = AisMessage.getInstance(vdm);
            msg8 = (AisMessage8) msg;
            AisApplicationMessage appMessage = msg8.getApplicationMessage();
            @SuppressWarnings("unused")
            BroadcastIntendedRoute routeInformation = (BroadcastIntendedRoute) appMessage;
//            System.out.println("encoded: " + encoded);
//            System.out.println("BroadcastRouteInformation: " + routeInformation + "\n---");
        } catch (Exception e) {
            System.err.println("Some exception: " + e.getMessage());
        }

    }

}
