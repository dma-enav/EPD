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
package dk.dma.epd.common.prototype.status;

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
