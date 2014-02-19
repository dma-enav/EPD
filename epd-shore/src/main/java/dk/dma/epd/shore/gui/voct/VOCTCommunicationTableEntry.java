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
package dk.dma.epd.shore.gui.voct;

public class VOCTCommunicationTableEntry {

    boolean send;
    boolean sarData;
    boolean AO;
    boolean searchPattern;

    public VOCTCommunicationTableEntry(boolean send, boolean sarData,
            boolean aO, boolean searchPattern) {
        this.send = send;
        this.sarData = sarData;
        AO = aO;
        this.searchPattern = searchPattern;
    }

    public boolean isSend() {
        return send;
    }

    public void setSend(boolean send) {
        this.send = send;
    }

    public boolean isSarData() {
        return sarData;
    }

    public void setSarData(boolean sarData) {
        this.sarData = sarData;
    }

    public boolean isAO() {
        return AO;
    }

    public void setAO(boolean aO) {
        AO = aO;
    }

    public boolean isSearchPattern() {
        return searchPattern;
    }

    public void setSearchPattern(boolean searchPattern) {
        this.searchPattern = searchPattern;
    }

}
