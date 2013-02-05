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
package dk.dma.epd.shore.status;

/**
 * Abstract base class for status components
 */
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getShortStatusText() {
        return shortStatusText;
    }

    public void setShortStatusText(String shortStatusText) {
        this.shortStatusText = shortStatusText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract String getStatusHtml();

}
