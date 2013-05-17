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
package dk.dma.epd.ship.status;

import net.jcip.annotations.ThreadSafe;

/**
 * Abstract base class for status components
 */
@ThreadSafe
public abstract class ComponentStatus {
    
    public enum Status {
        OK, ERROR, UNKNOWN, PARTIAL
    }
    
    protected Status status = Status.UNKNOWN;
    protected String name = "Component";
    protected String shortStatusText;
    
    public ComponentStatus(String name) {
        this.name = name;        
    }
    
    public ComponentStatus(String name, Status status) {
        this(name);
        this.status = status;
    }
    
    public synchronized Status getStatus() {
        return status;
    }
    
    public synchronized void setStatus(Status status) {
        this.status = status;
    }
    
    public synchronized String getShortStatusText() {
        return shortStatusText;
    }
    
    public synchronized void setShortStatusText(String shortStatusText) {
        this.shortStatusText = shortStatusText;
    }
    
    public synchronized String getName() {
        return name;
    }
    
    public synchronized void setName(String name) {
        this.name = name;
    }
    
    public abstract String getStatusHtml();
    
}
