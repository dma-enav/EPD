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
package dk.dma.epd.ship.event;

/**
 * A synchronized singleton made for only keeping track of clicks once for all layers
 */
public final class ClickTimer {
    
    private static ClickTimer clickTimer;
    private long startTime;
    private int interval;
    
    private ClickTimer() {
        
    }
    
    public static synchronized ClickTimer getClickTimer(){
        if(clickTimer == null){
            clickTimer = new ClickTimer();
        }
        return clickTimer;
    }
    
    public void startTime(){
        startTime = System.currentTimeMillis();
    }
    
    public boolean isIntervalExceeded(){
        long endTime = System.currentTimeMillis();
        long difference = endTime - startTime;
        return difference > interval;
    }
    
    public void setInterval(int interval) {
        this.interval = interval;
    }
}
