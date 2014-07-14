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
