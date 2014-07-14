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
package dk.dma.epd.common.prototype.model.voct;

import java.util.HashMap;

public class LeewayValues {
    
    
    
    private static HashMap<Integer, String> leeWayTypes = new HashMap<Integer, String>();
    private static HashMap<Integer, String> leeWayContent = new HashMap<Integer, String>();
    
    /**
     * Leway Vales as defined by SAR Danmark Figure 3-8 in section 3-8.
     * @param LWknots
     * @return
     */

    
    static
    {
        leeWayTypes.put(0, "Person in water (PIW)");
        leeWayContent.put(0, "0.011 x U + 0.068, Divergence: 30");
        
        leeWayTypes.put(1, "Raft (4-6 person), unknown drift anker status");
        leeWayContent.put(1, "0.029 x U + 0.039, Divergence: 20");
        
        leeWayTypes.put(2, "Raft (4-6 person) with drift anker");
        leeWayContent.put(2, "0.018 x U + 0.027, Divergence: 16");
        
        leeWayTypes.put(3, "Raft (4-6 person) without drift anker");
        leeWayContent.put(3, "0.038 x U - 0.041, Divergence: 20");
        
        leeWayTypes.put(4, "Raft (15-25 person), unknown drift anker status");
        leeWayContent.put(4, "0.036 x U - 0.086, Divergence: 14");
        
        leeWayTypes.put(5, "Raft (15-25 person) with drift anker");
        leeWayContent.put(5, "0.031 x U - 0.070, Divergence: 12");
        
        leeWayTypes.put(6, "Raft (15-25 person) without drift anker");
        leeWayContent.put(6, "0.039 x U - 0.060, Divergence: 12");
        
        leeWayTypes.put(7, "Dinghy (Flat buttom)");
        leeWayContent.put(7, "0.034 x U + 0.040, Divergence: 22");
        
        leeWayTypes.put(8, "Dinghy (Keel)");
        leeWayContent.put(8, "0.030 x U + 0.080, Divergence: 15");
        
        leeWayTypes.put(9, "Dinghy (Capsized)");
        leeWayContent.put(9, "0.017 x U , Divergence: 15");
        
        leeWayTypes.put(10, "Kayak with Person");
        leeWayContent.put(10, "0.011 x U + 0.240, Divergence: 15");
        
        leeWayTypes.put(11, "Surfboard with Person");
        leeWayContent.put(11, "0.020 x U, Divergence: 15");
        
        leeWayTypes.put(12, "Windsurfer with Person. Mast and sail in water.");
        leeWayContent.put(12, "0.023 x U + 0.100, Divergence: 12");
        
        leeWayTypes.put(13, "Sailboat (Long keel)");
        leeWayContent.put(13, "0.030 x U, Divergence: 48");
        
        leeWayTypes.put(14, "Sailboat (Fin keel)");
        leeWayContent.put(14, "0.040 x U, Divergence: 48");
        
        leeWayTypes.put(15, "Motorboat");
        leeWayContent.put(15, "0.069 x U - 0.080, Divergence: 19");
        
        leeWayTypes.put(16, "Fishing Vessel");
        leeWayContent.put(16, "0.042 x U, Divergence: 48");
        
        leeWayTypes.put(17, "Trawler");
        leeWayContent.put(17, "0.040 x U, Divergence: 33");
        
        leeWayTypes.put(18, "Coaster");
        leeWayContent.put(18, "0.028 x U, Divergence: 48");
        
        leeWayTypes.put(19, "Wreckage");
        leeWayContent.put(19, "0.020 x U, Divergence: 10");
    }
    
    
    /**
     * @return the leeWayTypes
     */
    public static HashMap<Integer, String> getLeeWayTypes() {
        return leeWayTypes;
    }

    /**
     * @return the leeWayContent
     */
    public static HashMap<Integer, String> getLeeWayContent() {
        return leeWayContent;
    }

    public static double personInWater(double LWknots){
        return 0.011 * LWknots + 0.068;
    }
    
    public static int personInWater(){
        return 30;
    }
    
    public static double raftFourToSix(double LWknots){
        return 0.029 * LWknots + 0.039;
    }
    
    public static int raftFourToSix(){
        return 20;
    }
        
    public static double raftFourToSixWithDriftAnker(double LWknots){
        return 0.018 * LWknots + 0.027;
    }
    
    public static int raftFourToSixWithDriftAnker(){
        return 16;
    }
    
    
    public static double raftFourToSixWithoutDriftAnker(double LWknots){
        return 0.038 * LWknots - 0.041;
    }
    
    public static int raftFourToSixWithoutDriftAnker(){
        return 20;
    }
    
    public static double raftFifteenToTwentyFive(double LWknots){
        return 0.036 * LWknots - 0.086;
    }
    
    
    public static int raftFifteenToTwentyFive(){
        return 14;
    }
    
    public static double raftFifteenToTwentyFiveWithDriftAnker(double LWknots){
        return 0.0031 * LWknots - 0.070;
    }
    
    public static int raftFifteenToTwentyFiveWithDriftAnker(){
        return 12;
    }
    
    public static double raftFifteenToTwentyFiveWitouthDriftAnker(double LWknots){
        return 0.0039 * LWknots - 0.060;
    }
    
    public static int raftFifteenToTwentyFiveWitouthDriftAnker(){
        return 12;
    }
    
    public static double dinghyFlatBottom(double LWknots){
        return 0.034 * LWknots + 0.040;
    }
    
    public static int dinghyFlatBottom(){
        return 22;
    }
    
    public static double dinghyWithKeel(double LWknots){
        return 0.030 * LWknots + 0.080;
    }
    
    public static int dinghyWithKeel(){
        return 15;
    }
    
    public static double dinghyCapsized(double LWknots){
        return 0.017 * LWknots;
    }
    
    public static int dinghyCapsized(){
        return 15;
    }
    
    public static double kayakWithPerson(double LWknots){
        return 0.011 * LWknots + 0.240;
    }
    
    public static int kayakWithPerson(){
        return 15;
    }
    
    public static double surfboardWithPerson(double LWknots){
        return 0.020 * LWknots;
    }
    
    public static int surfboardWithPerson(){
        return 15;
    }
    
    public static double windsurferWithPersonMastAndSailInWater(double LWknots){
        return 0.023 * LWknots + 0.100;
    }
    
    public static int windsurferWithPersonMastAndSailInWater(){
        return 12;
    }
    
    public static double sailboatLongKeel(double LWknots){
        return 0.030 * LWknots;
    }
    
    public static int sailboatLongKeel(){
        return 48;
    }
    
    public static double sailboatFinKeel(double LWknots){
        return 0.040 * LWknots;
    }
    
    public static int sailboatFinKeel(){
        return 48;
    }
    
    public static double motorboat(double LWknots){
        return 0.069 * LWknots - 0.080;
    }
    
    public static int motorboat(){
        return 19;
    }
    
    public static double fishingVessel(double LWknots){
        return 0.042 * LWknots;
    }
    
    public static int fishingVessel(){
        return 48;
    }
    
    public static double trawler(double LWknots){
        return 0.040 * LWknots;
    }
    
    public static int trawler(){
        return 33;
    }
    
    public static double coaster(double LWknots){
        return 0.028 * LWknots;
    }
    
    public static int coaster(){
        return 48;
    }
    
    public static double wreckage(double LWknots){
        return 0.020 * LWknots;
    }
    
    public static int wreckage(){
        return 10;
    }
    
    
}
