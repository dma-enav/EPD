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
package dk.dma.epd.ship.layers.areanotice;


/**
 * A class with returns string of the notice description according to IOM
 * documentation It provides possibility to return general description type
 * getDescType(int) and description detail getDescDetail
 */
public class ASNoticeDescription {

    /**
     * Method which returns general description type of AreaNotice
     */
    public static String getDescType(int descId) {
        String descType;
        switch (descId) {
        case 0:
            descType = "Caution Area";
            break;
        case 1:
            descType = "Caution Area";
            break;
        case 2:
            descType = "Caution Area";
            break;
        case 3:
            descType = "Caution Area";
            break;
        case 4:
            descType = "Caution Area";
            break;
        case 5:
            descType = "Caution Area";
            break;
        case 6:
            descType = "Caution Area";
            break;
        case 7:
            descType = "Caution Area";
            break;
        case 8:
            descType = "Caution Area";
            break;
        case 9:
            descType = "Caution Area";
            break;
        case 10:
            descType = "Caution Area";
            break;
        case 11:
            descType = "Caution Area";
            break;
        case 12:
            descType = "Caution Area";
            break;
        case 13:
            descType = "Caution Area";
            break;
        case 14:
            descType = "Caution Area";
            break;
        case 15:
            descType = "Caution Area";
            break;
        case 16:
            descType = "Caution Area";
            break;
        case 17:
            descType = "Caution Area";
            break;
        case 18:
            descType = "Caution Area";
            break;
        case 19:
            descType = "Caution Area";
            break;
        case 20:
            descType = "Caution Area";
            break;
        case 21:
            descType = "Caution Area";
            break;
        case 23:
            descType = "Environmental Caution Area";
            break;
        case 24:
            descType = "Environmental Caution Area";
            break;
        case 25:
            descType = "Environmental Caution Area";
            break;
        case 26:
            descType = "Environmental Caution Area";
            break;
        case 27:
            descType = "Environmental Caution Area";
            break;
        case 28:
            descType = "Environmental Caution Area";
            break;
        case 29:
            descType = "Environmental Caution Area";
            break;
        case 30:
            descType = "Environmental Caution Area";
            break;
        case 32:
            descType = "Restricted Area";
            break;
        case 33:
            descType = "Restricted Area";
            break;
        case 34:
            descType = "Restricted Area";
            break;
        case 35:
            descType = "Restricted Area";
            break;
        case 36:
            descType = "Restricted Area";
            break;
        case 37:
            descType = "Restricted Area";
            break;
        case 38:
            descType = "Restricted Area";
            break;
        case 40:
            descType = "Anchorage Area";
            break;
        case 41:
            descType = "Anchorage Area";
            break;
        case 42:
            descType = "Anchorage Area";
            break;
        case 43:
            descType = "Anchorage Area";
            break;
        case 44:
            descType = "Anchorage Area";
            break;
        case 45:
            descType = "Anchorage Area";
            break;
        case 56:
            descType = "Security Alert";
            break;
        case 57:
            descType = "Security Alert";
            break;
        case 58:
            descType = "Security Alert";
            break;
        case 64:
            descType = "Distress Area";
            break;
        case 65:
            descType = "Distress Area";
            break;
        case 66:
            descType = "Distress Area";
            break;
        case 67:
            descType = "Distress Area";
            break;
        case 68:
            descType = "Distress Area";
            break;
        case 69:
            descType = "Distress Area";
            break;
        case 70:
            descType = "Distress Area";
            break;
        case 71:
            descType = "Distress Area";
            break;
        case 72:
            descType = "Distress Area";
            break;
        case 73:
            descType = "Distress Area";
            break;
        case 74:
            descType = "Distress Area";
            break;
        case 75:
            descType = "Distress Area";
            break;
        case 76:
            descType = "Distress Area";
            break;
        case 80:
            descType = "Instruction";
            break;
        case 81:
            descType = "Instruction";
            break;
        case 82:
            descType = "Instruction";
            break;
        case 83:
            descType = "Instruction";
            break;
        case 84:
            descType = "Instruction";
            break;
        case 85:
            descType = "Instruction";
            break;
        case 88:
            descType = "Information";
            break;
        case 89:
            descType = "Information";
            break;
        case 90:
            descType = "Information";
            break;
        case 91:
            descType = "Information";
            break;
        case 92:
            descType = "Information";
            break;
        case 93:
            descType = "VTS active target";
            break;
        case 94:
            descType = "Rouge or suspicious vessel";
            break;
        case 95:
            descType = "Vessel requesting non-distress assistance";
            break;
        case 96:
            descType = "Chart Feature";
            break;
        case 97:
            descType = "Chart Feature";
            break;
        case 98:
            descType = "Chart Feature";
            break;
        case 99:
            descType = "Chart Feature";
            break;
        case 100:
            descType = "Chart Feature";
            break;
        case 101:
            descType = "Chart Feature";
            break;
        case 102:
            descType = "Chart Feature";
            break;
        case 103:
            descType = "Chart Feature";
            break;
        case 104:
            descType = "Chart Feature";
            break;
        case 105:
            descType = "Chart Feature";
            break;
        case 106:
            descType = "Chart Feature";
            break;
        case 107:
            descType = "Chart Feature";
            break;
        case 108:
            descType = "Chart Feature";
            break;
        case 112:
            descType = "Report from ship";
            break;
        case 114:
            descType = "Report from ship";
            break;
        case 120:
            descType = "Route";
            break;
        case 121:
            descType = "Route";
            break;
        case 122:
            descType = "Route";
            break;
        case 125:
            descType = "Other";
            break;
        case 126:
            descType = "Cancellation";
            break;
        default:
            descType = "Undefined";
            break;
        }

        return descType;
    }

    /*
     * Method which returns specific area type description details.
     */
    public static String getDescDetail(int descId) {
        String descDetail;
        switch (descId) {
        case 0:
            descDetail = "Marine mammals habitat";
            break;
        case 1:
            descDetail = "Marine mammals in area - reduce speed";
            break;
        case 2:
            descDetail = "Marine mammals in area - stay clear";
            break;
        case 3:
            descDetail = "Marine mammals in area - report sightings";
            break;
        case 4:
            descDetail = "Protected habitat - reduce speed";
            break;
        case 5:
            descDetail = "Protected habitat - stay clear";
            break;
        case 6:
            descDetail = "Protected habitat - no fishing or anchoring";
            break;
        case 7:
            descDetail = "Derelicts (drifting objects)";
            break;
        case 8:
            descDetail = "Traffic congestion";
            break;
        case 9:
            descDetail = "Marine event";
            break;
        case 10:
            descDetail = "Divers down";
            break;
        case 11:
            descDetail = "Swim area";
            break;
        case 12:
            descDetail = "Dredge operations";
            break;
        case 13:
            descDetail = "Survey operations";
            break;
        case 14:
            descDetail = "Underwater operation";
            break;
        case 15:
            descDetail = "Seaplane operations";
            break;
        case 16:
            descDetail = "Fishery - nets in water";
            break;
        case 17:
            descDetail = "Cluster of fishing vessels";
            break;
        case 18:
            descDetail = "Fairway closed";
            break;
        case 19:
            descDetail = "Harbour closed";
            break;
        case 20:
            descDetail = "Risk";
            break;
        case 21:
            descDetail = "Underwater vehicle operation";
            break;
        case 23:
            descDetail = "Storm Front";
            break;
        case 24:
            descDetail = "Hazardous sea ice";
            break;
        case 25:
            descDetail = "Storm warning";
            break;
        case 26:
            descDetail = "High wind";
            break;
        case 27:
            descDetail = "High waves";
            break;
        case 28:
            descDetail = "Restricted visibilty";
            break;
        case 29:
            descDetail = "Strong currents";
            break;
        case 30:
            descDetail = "Heavy icing";
            break;
        case 32:
            descDetail = "Fishing prohibited";
            break;
        case 33:
            descDetail = "No anchoring";
            break;
        case 34:
            descDetail = "Entry approval required prior to transit";
            break;
        case 35:
            descDetail = "Entry prohibited";
            break;
        case 36:
            descDetail = "Active militaty OPAREA";
            break;
        case 37:
            descDetail = "Firing - danger area";
            break;
        case 38:
            descDetail = "Drifting mines";
            break;
        case 40:
            descDetail = "Anchorage open";
            break;
        case 41:
            descDetail = "Anchorage closed";
            break;
        case 42:
            descDetail = "Anchoring prohibited";
            break;
        case 43:
            descDetail = "Deep draft anchorage";
            break;
        case 44:
            descDetail = "Shallow draft anchorage";
            break;
        case 45:
            descDetail = "Vessel transfer operations";
            break;
        case 56:
            descDetail = "Level 1";
            break;
        case 57:
            descDetail = "Level 2";
            break;
        case 58:
            descDetail = "Level 3";
            break;
        case 64:
            descDetail = "Vessel disabled and drift";
            break;
        case 65:
            descDetail = "Vessel sinking";
            break;
        case 66:
            descDetail = "Vessel abandoning ship";
            break;
        case 67:
            descDetail = "Vessel request medical assistance";
            break;
        case 68:
            descDetail = "Vessel flooding";
            break;
        case 69:
            descDetail = "Vessel fire/explosion";
            break;
        case 70:
            descDetail = "Vessel grounding";
            break;
        case 71:
            descDetail = "Vessel collision";
            break;
        case 72:
            descDetail = "Vessel listing /capsizing";
            break;
        case 73:
            descDetail = "Vessel under assault";
            break;
        case 74:
            descDetail = "Person overboard";
            break;
        case 75:
            descDetail = "SAR area";
            break;
        case 76:
            descDetail = "Pollution response area";
            break;
        case 80:
            descDetail = "Contact VTS at this point/juncture";
            break;
        case 81:
            descDetail = "Contact Port Administration at this point/juncture";
            break;
        case 82:
            descDetail = "Do not proceed beyond this point/juncture";
            break;
        case 83:
            descDetail = "Await instruction prior to proceeding beyond this point/juncture";
            break;
        case 84:
            descDetail = "Proceed to this location - await instructions";
            break;
        case 85:
            descDetail = "Clearance granted - proceed to berth";
            break;
        case 88:
            descDetail = "Pilot boarding position";
            break;
        case 89:
            descDetail = "Icebreaker waiting area";
            break;
        case 90:
            descDetail = "Places of refuge";
            break;
        case 91:
            descDetail = "Position of icebreakers";
            break;
        case 92:
            descDetail = "Location of response units";
            break;
        case 93:
            descDetail = "";
            break;
        case 94:
            descDetail = "";
            break;
        case 95:
            descDetail = "";
            break;
        case 96:
            descDetail = "Sunken vessel";
            break;
        case 97:
            descDetail = "Submerged object";
            break;
        case 98:
            descDetail = "Semi-submerged object";
            break;
        case 99:
            descDetail = "Shoal area";
            break;
        case 100:
            descDetail = "Shoal area due north";
            break;
        case 101:
            descDetail = "Shoal area due east";
            break;
        case 102:
            descDetail = "Shoal area due south";
            break;
        case 103:
            descDetail = "Shoal area due west";
            break;
        case 104:
            descDetail = "Channel obstruction";
            break;
        case 105:
            descDetail = "Reduced vertical clearance";
            break;
        case 106:
            descDetail = "Bridge closed";
            break;
        case 107:
            descDetail = "Bridge partially open";
            break;
        case 108:
            descDetail = "Bridge fully open";
            break;
        case 112:
            descDetail = "Icing info";
            break;
        case 114:
            descDetail = "Miscellaneous";
            break;
        case 120:
            descDetail = "Recommended route";
            break;
        case 121:
            descDetail = "Alternative route";
            break;
        case 122:
            descDetail = "Recommended route through ice";
            break;
        case 125:
            descDetail = "";
            break;
        case 126:
            descDetail = "";
            break;
        default:
            descDetail = "";
            break;
        }

        return descDetail;
    }

    public static String getDescription(int descId) {
        String descType, descDetail;
        switch (descId) {
        case 0:
            descType = "Caution Area";
            descDetail = "Marine mammals habitat";
            break;
        case 1:
            descType = "Caution Area";
            descDetail = "Marine mammals in area - reduce speed";
            break;
        case 2:
            descType = "Caution Area";
            descDetail = "Marine mammals in area - stay clear";
            break;
        case 3:
            descType = "Caution Area";
            descDetail = "Marine mammals in area - report sightings";
            break;
        case 4:
            descType = "Caution Area";
            descDetail = "Protected habitat - reduce speed";
            break;
        case 5:
            descType = "Caution Area";
            descDetail = "Protected habitat - stay clear";
            break;
        case 6:
            descType = "Caution Area";
            descDetail = "Protected habitat - no fishing or anchoring";
            break;
        case 7:
            descType = "Caution Area";
            descDetail = "Derelicts (drifting objects)";
            break;
        case 8:
            descType = "Caution Area";
            descDetail = "Traffic congestion";
            break;
        case 9:
            descType = "Caution Area";
            descDetail = "Marine event";
            break;
        case 10:
            descType = "Caution Area";
            descDetail = "Divers down";
            break;
        case 11:
            descType = "Caution Area";
            descDetail = "Swim area";
            break;
        case 12:
            descType = "Caution Area";
            descDetail = "Dredge operations";
            break;
        case 13:
            descType = "Caution Area";
            descDetail = "Survey operations";
            break;
        case 14:
            descType = "Caution Area";
            descDetail = "Underwater operation";
            break;
        case 15:
            descType = "Caution Area";
            descDetail = "Seaplane operations";
            break;
        case 16:
            descType = "Caution Area";
            descDetail = "Fishery - nets in water";
            break;
        case 17:
            descType = "Caution Area";
            descDetail = "Cluster of fishing vessels";
            break;
        case 18:
            descType = "Caution Area";
            descDetail = "Fairway closed";
            break;
        case 19:
            descType = "Caution Area";
            descDetail = "Harbour closed";
            break;
        case 20:
            descType = "Caution Area";
            descDetail = "Risk";
            break;
        case 21:
            descType = "Caution Area";
            descDetail = "Underwater vehicle operation";
            break;
        case 23:
            descType = "Environmental Caution Area";
            descDetail = "Storm Front";
            break;
        case 24:
            descType = "Environmental Caution Area";
            descDetail = "Hazardous sea ice";
            break;
        case 25:
            descType = "Environmental Caution Area";
            descDetail = "Storm warning";
            break;
        case 26:
            descType = "Environmental Caution Area";
            descDetail = "High wind";
            break;
        case 27:
            descType = "Environmental Caution Area";
            descDetail = "High waves";
            break;
        case 28:
            descType = "Environmental Caution Area";
            descDetail = "Restricted visibilty";
            break;
        case 29:
            descType = "Environmental Caution Area";
            descDetail = "Strong currents";
            break;
        case 30:
            descType = "Environmental Caution Area";
            descDetail = "Heavy icing";
            break;
        case 32:
            descType = "Restricted Area";
            descDetail = "Fishing prohibited";
            break;
        case 33:
            descType = "Restricted Area";
            descDetail = "No anchoring";
            break;
        case 34:
            descType = "Restricted Area";
            descDetail = "Entry approval required prior to transit";
            break;
        case 35:
            descType = "Restricted Area";
            descDetail = "Entry prohibited";
            break;
        case 36:
            descType = "Restricted Area";
            descDetail = "Active militaty OPAREA";
            break;
        case 37:
            descType = "Restricted Area";
            descDetail = "Firing - danger area";
            break;
        case 38:
            descType = "Restricted Area";
            descDetail = "Drifting mines";
            break;
        case 40:
            descType = "Anchorage Area";
            descDetail = "Anchorage open";
            break;
        case 41:
            descType = "Anchorage Area";
            descDetail = "Anchorage closed";
            break;
        case 42:
            descType = "Anchorage Area";
            descDetail = "Anchoring prohibited";
            break;
        case 43:
            descType = "Anchorage Area";
            descDetail = "Deep draft anchorage";
            break;
        case 44:
            descType = "Anchorage Area";
            descDetail = "Shallow draft anchorage";
            break;
        case 45:
            descType = "Anchorage Area";
            descDetail = "Vessel transfer operations";
            break;
        case 56:
            descType = "Security Alert";
            descDetail = "Level 1";
            break;
        case 57:
            descType = "Security Alert";
            descDetail = "Level 2";
            break;
        case 58:
            descType = "Security Alert";
            descDetail = "Level 3";
            break;
        case 64:
            descType = "Distress Area";
            descDetail = "Vessel disabled and drift";
            break;
        case 65:
            descType = "Distress Area";
            descDetail = "Vessel sinking";
            break;
        case 66:
            descType = "Distress Area";
            descDetail = "Vessel abandoning ship";
            break;
        case 67:
            descType = "Distress Area";
            descDetail = "Vessel request medical assistance";
            break;
        case 68:
            descType = "Distress Area";
            descDetail = "Vessel flooding";
            break;
        case 69:
            descType = "Distress Area";
            descDetail = "Vessel fire/explosion";
            break;
        case 70:
            descType = "Distress Area";
            descDetail = "Vessel grounding";
            break;
        case 71:
            descType = "Distress Area";
            descDetail = "Vessel collision";
            break;
        case 72:
            descType = "Distress Area";
            descDetail = "Vessel listing /capsizing";
            break;
        case 73:
            descType = "Distress Area";
            descDetail = "Vessel under assault";
            break;
        case 74:
            descType = "Distress Area";
            descDetail = "Person overboard";
            break;
        case 75:
            descType = "Distress Area";
            descDetail = "SAR area";
            break;
        case 76:
            descType = "Distress Area";
            descDetail = "Pollution response area";
            break;
        case 80:
            descType = "Instruction";
            descDetail = "Contact VTS at this point/juncture";
            break;
        case 81:
            descType = "Instruction";
            descDetail = "Contact Port Administration at this point/juncture";
            break;
        case 82:
            descType = "Instruction";
            descDetail = "Do not proceed beyond this point/juncture";
            break;
        case 83:
            descType = "Instruction";
            descDetail = "Await instruction prior to proceeding beyond this point/juncture";
            break;
        case 84:
            descType = "Instruction";
            descDetail = "Proceed to this location - await instructions";
            break;
        case 85:
            descType = "Instruction";
            descDetail = "Clearance granted - proceed to berth";
            break;
        case 88:
            descType = "Information";
            descDetail = "Pilot boarding position";
            break;
        case 89:
            descType = "Information";
            descDetail = "Icebreaker waiting area";
            break;
        case 90:
            descType = "Information";
            descDetail = "Places of refuge";
            break;
        case 91:
            descType = "Information";
            descDetail = "Position of icebreakers";
            break;
        case 92:
            descType = "Information";
            descDetail = "Location of response units";
            break;
        case 93:
            descType = "VTS active target";
            descDetail = "";
            break;
        case 94:
            descType = "Rouge or suspicious vessel";
            descDetail = "";
            break;
        case 95:
            descType = "Vessel requesting non-distress assistance";
            descDetail = "";
            break;
        case 96:
            descType = "Chart Feature";
            descDetail = "Sunken vessel";
            break;
        case 97:
            descType = "Chart Feature";
            descDetail = "Submerged object";
            break;
        case 98:
            descType = "Chart Feature";
            descDetail = "Semi-submerged object";
            break;
        case 99:
            descType = "Chart Feature";
            descDetail = "Shoal area";
            break;
        case 100:
            descType = "Chart Feature";
            descDetail = "Shoal area due north";
            break;
        case 101:
            descType = "Chart Feature";
            descDetail = "Shoal area due east";
            break;
        case 102:
            descType = "Chart Feature";
            descDetail = "Shoal area due south";
            break;
        case 103:
            descType = "Chart Feature";
            descDetail = "Shoal area due west";
            break;
        case 104:
            descType = "Chart Feature";
            descDetail = "Channel obstruction";
            break;
        case 105:
            descType = "Chart Feature";
            descDetail = "Reduced vertical clearance";
            break;
        case 106:
            descType = "Chart Feature";
            descDetail = "Bridge closed";
            break;
        case 107:
            descType = "Chart Feature";
            descDetail = "Bridge partially open";
            break;
        case 108:
            descType = "Chart Feature";
            descDetail = "Bridge fully open";
            break;
        case 112:
            descType = "Report from ship";
            descDetail = "Icing info";
            break;
        case 114:
            descType = "Report from ship";
            descDetail = "Miscellaneous";
            break;
        case 120:
            descType = "Route";
            descDetail = "Recommended route";
            break;
        case 121:
            descType = "Route";
            descDetail = "Alternative route";
            break;
        case 122:
            descType = "Route";
            descDetail = "Recommended route through ice";
            break;
        case 125:
            descType = "Other";
            descDetail = "";
            break;
        case 126:
            descType = "Cancellation";
            descDetail = "";
            break;
        default:
            descType = "Undefined";
            descDetail = "";
            break;
        }

        return descType + ": " + descDetail;
    }

}
