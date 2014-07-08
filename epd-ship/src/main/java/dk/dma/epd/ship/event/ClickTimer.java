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
