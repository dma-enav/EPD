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
package dk.dma.epd.shore.voct;

import java.util.Date;

public class SRUCommunicationObject {

    SRU sru;
    Date lastMessageRecieved;

    // List<VOCTSARBroadCast> sruCommunicationObjects = new
    // ArrayList<VOCTSARBroadCast>();

    // List<Position> positions = new ArrayList<Position>();
    // List<Date> messageDates = new ArrayList<Date>();

    // private double heading;
    private boolean visible = true;

    // private Route intendedSearchPattern;

    public SRUCommunicationObject(SRU sru) {
        this.sru = sru;
    }

    public SRU getSru() {
        return sru;
    }

    //
    // public synchronized void addBroadcastMessage(VOCTSARBroadCast message) {
    //
    // positions.add(Position.create(message.getLat(), message.getLon()));
    // this.heading = message.getHeading();
    //
    // if (message.getIntendedSearchPattern() != null) {
    //
    // intendedSearchPattern = new Route(
    // message.getIntendedSearchPattern());
    // }
    // messageDates.add(new Date(message.getDate()));
    //
    // // sruCommunicationObjects.add(message);
    // }
    //
    // public Date getLastMessageDate() {
    // if (messageDates.size() == 0) {
    // return null;
    // } else {
    // return messageDates.get(messageDates.size() - 1);
    // }
    // }
    // visible
    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @return the lastMessageRecieved
     */
    public Date getLastMessageRecieved() {
        return lastMessageRecieved;
    }

    /**
     * @param lastMessageRecieved
     *            the lastMessageRecieved to set
     */
    public void setLastMessageRecieved(Date lastMessageRecieved) {
        this.lastMessageRecieved = lastMessageRecieved;
    }

    /**
     * @param visible
     *            the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}
