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
package dk.dma.epd.ship.service.voct;

public class LeewayValues {
    /**
     * Leway Vales as defined by SAR Danmark Figure 3-8 in section 3-8.
     * @param LWknots
     * @return
     */

    
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
    
    public static double smallFishingVessel(double LWknots){
        return 0.040 * LWknots;
    }
    
    public static int smallFishingVessel(){
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
