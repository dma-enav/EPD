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

public class StaticFalShipData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nameAndTypeOfShip = "";
    private String callSign = "";
    private String imoNumber = "";
    private String flagStateOfShip = "";
    private String nameOfMaster = "";
    private String certificateOfRegistry = "";
    private String grossTonnage = "";
    private String netTonnage = "";
    private String nameAndContactDetalsOfShipsAgent = "";

    public StaticFalShipData() {
    }

    /**
     * @param nameAndTypeOfShip
     * @param callSign
     * @param imoNumber
     * @param flagStateOfShip
     * @param nameOfMaster
     * @param certificateOfRegistry
     * @param grossTonnage
     * @param netTonnage
     * @param nameAndContactDetalsOfShipsAgent
     */
    public StaticFalShipData(String nameAndTypeOfShip, String callSign, String imoNumber, String flagStateOfShip,
            String nameOfMaster, String certificateOfRegistry, String grossTonnage, String netTonnage,
            String nameAndContactDetalsOfShipsAgent) {
        this.nameAndTypeOfShip = nameAndTypeOfShip;
        this.callSign = callSign;
        this.imoNumber = imoNumber;
        this.flagStateOfShip = flagStateOfShip;
        this.nameOfMaster = nameOfMaster;
        this.certificateOfRegistry = certificateOfRegistry;
        this.grossTonnage = grossTonnage;
        this.netTonnage = netTonnage;
        this.nameAndContactDetalsOfShipsAgent = nameAndContactDetalsOfShipsAgent;
    }

    /**
     * @return the nameAndTypeOfShip
     */
    public String getNameAndTypeOfShip() {
        return nameAndTypeOfShip;
    }

    /**
     * @param nameAndTypeOfShip
     *            the nameAndTypeOfShip to set
     */
    public void setNameAndTypeOfShip(String nameAndTypeOfShip) {
        this.nameAndTypeOfShip = nameAndTypeOfShip;
    }

    /**
     * @return the callSign
     */
    public String getCallSign() {
        return callSign;
    }

    /**
     * @param callSign
     *            the callSign to set
     */
    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    /**
     * @return the imoNumber
     */
    public String getImoNumber() {
        return imoNumber;
    }

    /**
     * @param imoNumber
     *            the imoNumber to set
     */
    public void setImoNumber(String imoNumber) {
        this.imoNumber = imoNumber;
    }

    /**
     * @return the flagStateOfShip
     */
    public String getFlagStateOfShip() {
        return flagStateOfShip;
    }

    /**
     * @param flagStateOfShip
     *            the flagStateOfShip to set
     */
    public void setFlagStateOfShip(String flagStateOfShip) {
        this.flagStateOfShip = flagStateOfShip;
    }

    /**
     * @return the nameOfMaster
     */
    public String getNameOfMaster() {
        return nameOfMaster;
    }

    /**
     * @param nameOfMaster
     *            the nameOfMaster to set
     */
    public void setNameOfMaster(String nameOfMaster) {
        this.nameOfMaster = nameOfMaster;
    }

    /**
     * @return the certificateOfRegistry
     */
    public String getCertificateOfRegistry() {
        return certificateOfRegistry;
    }

    /**
     * @param certificateOfRegistry
     *            the certificateOfRegistry to set
     */
    public void setCertificateOfRegistry(String certificateOfRegistry) {
        this.certificateOfRegistry = certificateOfRegistry;
    }

    /**
     * @return the grossTonnage
     */
    public String getGrossTonnage() {
        return grossTonnage;
    }

    /**
     * @param grossTonnage
     *            the grossTonnage to set
     */
    public void setGrossTonnage(String grossTonnage) {
        this.grossTonnage = grossTonnage;
    }

    /**
     * @return the netTonnage
     */
    public String getNetTonnage() {
        return netTonnage;
    }

    /**
     * @param netTonnage
     *            the netTonnage to set
     */
    public void setNetTonnage(String netTonnage) {
        this.netTonnage = netTonnage;
    }

    /**
     * @return the nameAndContactDetalsOfShipsAgent
     */
    public String getNameAndContactDetalsOfShipsAgent() {
        return nameAndContactDetalsOfShipsAgent;
    }

    /**
     * @param nameAndContactDetalsOfShipsAgent
     *            the nameAndContactDetalsOfShipsAgent to set
     */
    public void setNameAndContactDetalsOfShipsAgent(String nameAndContactDetalsOfShipsAgent) {
        this.nameAndContactDetalsOfShipsAgent = nameAndContactDetalsOfShipsAgent;
    }

}
