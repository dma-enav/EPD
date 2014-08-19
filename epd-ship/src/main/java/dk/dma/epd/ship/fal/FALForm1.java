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

public class FALForm1 implements Serializable {

    private static final long serialVersionUID = 1L;

    // If false is depature
    boolean arrival;

    private String nameAndTypeOfShip = "";
    private String imoNumber = "";
    private String callSign = "";
    private String voyageNumber;
    private String portOfArrivalDeapture = "";
    private String DateAndTimeOfArrivalDepature = "";
    private String flagStateOfShip = "";
    private String nameOfMaster = "";
    private String lastPortOfCall = "";
    private String certificateOfRegistry = "";
    private String grossTonnage = "";
    private String nameAndContactDetalsOfShipsAgent = "";
    private String netTonnage = "";
    private String positionOfTheShip = "";
    private String briefParticulars = "";
    private String briefDescriptionOfCargo = "";
    private String numberOfCrew = "";
    private String numberOfPassengers = "";
    private String remarks = "";

    private int cargoDeclarationCount;
    private int shipStoresDeclarationCount;
    private int crewListCount;
    private int passengerListCount;
    private int crewEffectsDeclarationCount;
    private int maritimeDeclarationOfHealthCount;

    private String shipWasteRequirements;
    private String signature;

    public FALForm1() {

    }

    /**
     * @param arrival
     * @param nameAndTypeOfShip
     * @param imoNumber
     * @param callSign
     * @param voyageNumber
     * @param portOfArrivalDeapture
     * @param dateAndTimeOfArrivalDepature
     * @param flagStateOfShip
     * @param nameOfMaster
     * @param lastPortOfCall
     * @param certificateOfRegistry
     * @param grossTonnage
     * @param nameAndContactDetalsOfShipsAgent
     * @param netTonnage
     * @param positionOfTheShip
     * @param briefParticulars
     * @param briefDescriptionOfCargo
     * @param numberOfCrew
     * @param numberOfPassengers
     * @param remarks
     * @param cargoDeclarationCount
     * @param shipStoresDeclarationCount
     * @param crewListCount
     * @param passengerListCount
     * @param crewEffectsDeclarationCount
     * @param maritimeDeclarationOfHealthCount
     * @param shipWasteRequirements
     * @param signature
     */
    public FALForm1(boolean arrival, String nameAndTypeOfShip, String imoNumber, String callSign, String voyageNumber,
            String portOfArrivalDeapture, String dateAndTimeOfArrivalDepature, String flagStateOfShip, String nameOfMaster,
            String lastPortOfCall, String certificateOfRegistry, String grossTonnage, String nameAndContactDetalsOfShipsAgent,
            String netTonnage, String positionOfTheShip, String briefParticulars, String briefDescriptionOfCargo,
            String numberOfCrew, String numberOfPassengers, String remarks, int cargoDeclarationCount,
            int shipStoresDeclarationCount, int crewListCount, int passengerListCount, int crewEffectsDeclarationCount,
            int maritimeDeclarationOfHealthCount, String shipWasteRequirements, String signature) {
        this.arrival = arrival;
        this.nameAndTypeOfShip = nameAndTypeOfShip;
        this.imoNumber = imoNumber;
        this.callSign = callSign;
        this.voyageNumber = voyageNumber;
        this.portOfArrivalDeapture = portOfArrivalDeapture;
        DateAndTimeOfArrivalDepature = dateAndTimeOfArrivalDepature;
        this.flagStateOfShip = flagStateOfShip;
        this.nameOfMaster = nameOfMaster;
        this.lastPortOfCall = lastPortOfCall;
        this.certificateOfRegistry = certificateOfRegistry;
        this.grossTonnage = grossTonnage;
        this.nameAndContactDetalsOfShipsAgent = nameAndContactDetalsOfShipsAgent;
        this.netTonnage = netTonnage;
        this.positionOfTheShip = positionOfTheShip;
        this.briefParticulars = briefParticulars;
        this.briefDescriptionOfCargo = briefDescriptionOfCargo;
        this.numberOfCrew = numberOfCrew;
        this.numberOfPassengers = numberOfPassengers;
        this.remarks = remarks;
        this.cargoDeclarationCount = cargoDeclarationCount;
        this.shipStoresDeclarationCount = shipStoresDeclarationCount;
        this.crewListCount = crewListCount;
        this.passengerListCount = passengerListCount;
        this.crewEffectsDeclarationCount = crewEffectsDeclarationCount;
        this.maritimeDeclarationOfHealthCount = maritimeDeclarationOfHealthCount;
        this.shipWasteRequirements = shipWasteRequirements;
        this.signature = signature;
    }

    /**
     * @return the arrival
     */
    public boolean isArrival() {
        return arrival;
    }

    /**
     * @param arrival
     *            the arrival to set
     */
    public void setArrival(boolean arrival) {
        this.arrival = arrival;
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
     * @return the voyageNumber
     */
    public String getVoyageNumber() {
        return voyageNumber;
    }

    /**
     * @param voyageNumber
     *            the voyageNumber to set
     */
    public void setVoyageNumber(String voyageNumber) {
        this.voyageNumber = voyageNumber;
    }

    /**
     * @return the portOfArrivalDeapture
     */
    public String getPortOfArrivalDeapture() {
        return portOfArrivalDeapture;
    }

    /**
     * @param portOfArrivalDeapture
     *            the portOfArrivalDeapture to set
     */
    public void setPortOfArrivalDeapture(String portOfArrivalDeapture) {
        this.portOfArrivalDeapture = portOfArrivalDeapture;
    }

    /**
     * @return the dateAndTimeOfArrivalDepature
     */
    public String getDateAndTimeOfArrivalDepature() {
        return DateAndTimeOfArrivalDepature;
    }

    /**
     * @param dateAndTimeOfArrivalDepature
     *            the dateAndTimeOfArrivalDepature to set
     */
    public void setDateAndTimeOfArrivalDepature(String dateAndTimeOfArrivalDepature) {
        DateAndTimeOfArrivalDepature = dateAndTimeOfArrivalDepature;
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
     * @return the lastPortOfCall
     */
    public String getLastPortOfCall() {
        return lastPortOfCall;
    }

    /**
     * @param lastPortOfCall
     *            the lastPortOfCall to set
     */
    public void setLastPortOfCall(String lastPortOfCall) {
        this.lastPortOfCall = lastPortOfCall;
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
     * @return the positionOfTheShip
     */
    public String getPositionOfTheShip() {
        return positionOfTheShip;
    }

    /**
     * @param positionOfTheShip
     *            the positionOfTheShip to set
     */
    public void setPositionOfTheShip(String positionOfTheShip) {
        this.positionOfTheShip = positionOfTheShip;
    }

    /**
     * @return the briefParticulars
     */
    public String getBriefParticulars() {
        return briefParticulars;
    }

    /**
     * @param briefParticulars
     *            the briefParticulars to set
     */
    public void setBriefParticulars(String briefParticulars) {
        this.briefParticulars = briefParticulars;
    }

    /**
     * @return the briefDescriptionOfCargo
     */
    public String getBriefDescriptionOfCargo() {
        return briefDescriptionOfCargo;
    }

    /**
     * @param briefDescriptionOfCargo
     *            the briefDescriptionOfCargo to set
     */
    public void setBriefDescriptionOfCargo(String briefDescriptionOfCargo) {
        this.briefDescriptionOfCargo = briefDescriptionOfCargo;
    }

    /**
     * @return the numberOfCrew
     */
    public String getNumberOfCrew() {
        return numberOfCrew;
    }

    /**
     * @param numberOfCrew
     *            the numberOfCrew to set
     */
    public void setNumberOfCrew(String numberOfCrew) {
        this.numberOfCrew = numberOfCrew;
    }

    /**
     * @return the numberOfPassengers
     */
    public String getNumberOfPassengers() {
        return numberOfPassengers;
    }

    /**
     * @param numberOfPassengers
     *            the numberOfPassengers to set
     */
    public void setNumberOfPassengers(String numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    /**
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks
     *            the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the cargoDeclarationCount
     */
    public int getCargoDeclarationCount() {
        return cargoDeclarationCount;
    }

    /**
     * @param cargoDeclarationCount
     *            the cargoDeclarationCount to set
     */
    public void setCargoDeclarationCount(int cargoDeclarationCount) {
        this.cargoDeclarationCount = cargoDeclarationCount;
    }

    /**
     * @return the shipStoresDeclarationCount
     */
    public int getShipStoresDeclarationCount() {
        return shipStoresDeclarationCount;
    }

    /**
     * @param shipStoresDeclarationCount
     *            the shipStoresDeclarationCount to set
     */
    public void setShipStoresDeclarationCount(int shipStoresDeclarationCount) {
        this.shipStoresDeclarationCount = shipStoresDeclarationCount;
    }

    /**
     * @return the crewListCount
     */
    public int getCrewListCount() {
        return crewListCount;
    }

    /**
     * @param crewListCount
     *            the crewListCount to set
     */
    public void setCrewListCount(int crewListCount) {
        this.crewListCount = crewListCount;
    }

    /**
     * @return the passengerListCount
     */
    public int getPassengerListCount() {
        return passengerListCount;
    }

    /**
     * @param passengerListCount
     *            the passengerListCount to set
     */
    public void setPassengerListCount(int passengerListCount) {
        this.passengerListCount = passengerListCount;
    }

    /**
     * @return the crewEffectsDeclarationCount
     */
    public int getCrewEffectsDeclarationCount() {
        return crewEffectsDeclarationCount;
    }

    /**
     * @param crewEffectsDeclarationCount
     *            the crewEffectsDeclarationCount to set
     */
    public void setCrewEffectsDeclarationCount(int crewEffectsDeclarationCount) {
        this.crewEffectsDeclarationCount = crewEffectsDeclarationCount;
    }

    /**
     * @return the maritimeDeclarationOfHealthCount
     */
    public int getMaritimeDeclarationOfHealthCount() {
        return maritimeDeclarationOfHealthCount;
    }

    /**
     * @param maritimeDeclarationOfHealthCount
     *            the maritimeDeclarationOfHealthCount to set
     */
    public void setMaritimeDeclarationOfHealthCount(int maritimeDeclarationOfHealthCount) {
        this.maritimeDeclarationOfHealthCount = maritimeDeclarationOfHealthCount;
    }

    /**
     * @return the shipWasteRequirements
     */
    public String getShipWasteRequirements() {
        return shipWasteRequirements;
    }

    /**
     * @param shipWasteRequirements
     *            the shipWasteRequirements to set
     */
    public void setShipWasteRequirements(String shipWasteRequirements) {
        this.shipWasteRequirements = shipWasteRequirements;
    }

    /**
     * @return the signature
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @param signature
     *            the signature to set
     */
    public void setSignature(String signature) {
        this.signature = signature;
    }

}
