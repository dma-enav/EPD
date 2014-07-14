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
package dk.dma.epd.common.prototype.ais;

/**
 * Model enum for AtoN types.
 * 
 * @author Janus Varmarken
 */
public enum AtoNTargetType {

    /*
     * These are the actual AtoN types. Each type has an integer type code and a
     * human-friendly name.
     */
    DEFAULT(0, "Type not specified"), REFERENCE_POINT(1, "Reference point"), RACON(
            2, "Racon"), FIXED_STRUCTURE_OFFSHORE(3,
            "Fixed structure offshore/obstruction"), EMERGENCY_WRECK_MARK(4,
            "Emergency Wreck Mark"), LIGHT_WITHOUT_SECTORS(5,
            "Light, without sectors"), LIGHT_WITH_SECTORS(6,
            "Light, with sectors"), LEADING_LIGHT_FRONT(7,
            "Leading Light Front"), LEADING_LIGHT_REAR(8, "Leading Light Rear"), BEACON_CARDINAL_N(
            9, "Beacon, Cardinal N"), BEACON_CARDINAL_E(10,
            "Beacon, Cardinal E"), BEACON_CARDINAL_S(11, "Beacon, Cardinal S"), BEACON_CARDINAL_W(
            12, "Beacon, Cardinal W"), BEACON_PORT_HAND(13, "Beacon, Port hand"), BEACON_STARBOARD_HAND(
            14, "Beacon, Starboard hand"), BEACON_PREFERRED_CHANNEL_PORT_HAND(
            15, "Beacon, Preferred Channel Port hand"), BEACON_PREFERRED_CHANNEL_STARBOARD_HAND(
            16, "Beacon, Preferred Channel Starboard hand"), BEACON_ISOLATED_DANGER(
            17, "Beacon, Isolated danger"), BEACON_SAFE_WATER(18,
            "Beacon, Safe water"), BEACON_SPECIAL_MARK(19,
            "Beacon, Special mark"), FLOATING_CARDINAL_MARK_N(20,
            "Floating, Cardinal Mark N"), FLOATING_CARDINAL_MARK_E(21,
            "Floating, Cardinal Mark E"), FLOATING_CARDINAL_MARK_S(22,
            "Floating, Cardinal Mark S"), FLOATING_CARDINAL_MARK_W(23,
            "Floating, Cardinal Mark W"), PORT_HAND_MARK(24, "Port hand Mark"), STARBOARD_HAND_MARK(
            25, "Starboard hand Mark"), PREFERRED_CHANNEL_PORT_HAND(26,
            "Preferred Channel Port hand"), PREFERRED_CHANNEL_STARBOARD_HAND(
            27, "Preferred Channel Starboard hand"), ISOLATED_DANGER(28,
            "Isolated danger"), SAFE_WATER(29, "Safe water"), SPECIAL_MARK(30,
            "Special Mark"), LIGHT_VESSEL_OR_LANBY_OR_RIGS(31,
            "Light Vessel / LANBY / Rigs");

    /**
     * Code that identifies the type of this AtoN.
     */
    private int atonTypeCode;

    /**
     * Pretty print name of this AtoN type.
     */
    private String prettyName;

    AtoNTargetType(int atonTypeCode, String prettyName) {
        this.atonTypeCode = atonTypeCode;
        this.prettyName = prettyName;
    }

    public String getPrettyName() {
        return this.prettyName;
    }

    public int getAtoNTypeCode() {
        return this.atonTypeCode;
    }

    /**
     * Get the AtoNTargetType representation for a specific type code.
     * 
     * @param atonTypeCode
     *            The AtoN type code.
     * @return The corresponding AtoNTargetType enum or null if atonTypeCode is
     *         an invalid type code.
     */
    public static AtoNTargetType getAtoNTargetTypeFromTypeCode(int atonTypeCode) {
        if (atonTypeCode >= 0 && atonTypeCode < AtoNTargetType.values().length) {
            // Index equals type code.
            // Note that if the specification changes s.t. codes are no longer
            // sequential, this must be updated.
            return AtoNTargetType.values()[atonTypeCode];
        } else {
            return null;
        }
    }
}
