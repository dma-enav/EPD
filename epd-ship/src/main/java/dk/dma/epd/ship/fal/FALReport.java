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
package dk.dma.epd.ship.fal;

import java.io.Serializable;

public class FALReport implements Serializable {

    private static final long serialVersionUID = 1L;

    private String falReportName;
    private long id;
    private FALForm1 falform1;

    public FALReport() {
        this.id = System.currentTimeMillis();
    }

    public FALReport(long id) {
        this.id = id;
    }

    public FALReport(FALReport copy) {

        this.id = System.currentTimeMillis();
        this.falform1 = copy.getFalform1();

        this.falReportName = copy.getFalReportName() + " copy";

    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the falform1
     */
    public FALForm1 getFalform1() {
        return falform1;
    }

    /**
     * @param falform1
     *            the falform1 to set
     */
    public void setFalform1(FALForm1 falform1) {
        this.falform1 = falform1;
    }

    /**
     * @return the falReportName
     */
    public String getFalReportName() {
        return falReportName;
    }

    /**
     * @param falReportName
     *            the falReportName to set
     */
    public void setFalReportName(String falReportName) {
        this.falReportName = falReportName;
    }

}
