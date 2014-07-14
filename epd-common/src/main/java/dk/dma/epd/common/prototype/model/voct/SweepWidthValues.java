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

public class SweepWidthValues {

    private static HashMap<Integer, String> SweepWidthTypes = new HashMap<Integer, String>();

    // Table 4-7 from SAR Denmark
    // Usage is as follows, first select one of two hashmaps, smaller and larger
    // vessels
    // Then withdraw the value of search target, ie. PIW is 0
    // This returns a new HashMap with the keys 1, 3, 5, 10, 15, 20 for
    // Visibility
    // The value returned from this is the final value.

    private static HashMap<Integer, HashMap<Integer, Double>> smallerVessels = new HashMap<Integer, HashMap<Integer, Double>>();

    private static HashMap<Integer, Double> smallerVesselsPIW = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsRaftOne = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsRaftFour = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsRaftSix = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsRaftEight = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsRaftTen = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsRaftFifteen = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsRaftTwenty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsRaftTwentyFive = new HashMap<Integer, Double>();

    private static HashMap<Integer, Double> smallerVesselsMotorboatFifteen = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsMotorboatTwenty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsMotorboatThirtyThree = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsMotorboatFiftyThree = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsMotorboatSeventyEight = new HashMap<Integer, Double>();

    private static HashMap<Integer, Double> smallerVesselsSailboatFifteen = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsSailboatTwenty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsSailboatTwentyFive = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsSailboatThirty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsSailboatFourty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsSailboatFifty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsSailboatSeventy = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsSailEightyThree = new HashMap<Integer, Double>();

    private static HashMap<Integer, Double> smallerVesselsShipOneTwenty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsShipTwoTwentyFive = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> smallerVesselsShipThree = new HashMap<Integer, Double>();

    
    
    private static HashMap<Integer, HashMap<Integer, Double>> largerVessels = new HashMap<Integer, HashMap<Integer, Double>>();

    private static HashMap<Integer, Double> largerVesselsPIW = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsRaftOne = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsRaftFour = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsRaftSix = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsRaftEight = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsRaftTen = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsRaftFifteen = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsRaftTwenty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsRaftTwentyFive = new HashMap<Integer, Double>();

    private static HashMap<Integer, Double> largerVesselsMotorboatFifteen = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsMotorboatTwenty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsMotorboatThirtyThree = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsMotorboatFiftyThree = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsMotorboatSeventyEight = new HashMap<Integer, Double>();

    private static HashMap<Integer, Double> largerVesselsSailboatFifteen = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsSailboatTwenty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsSailboatTwentyFive = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsSailboatThirty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsSailboatFourty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsSailboatFifty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsSailboatSeventy = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsSailEightyThree = new HashMap<Integer, Double>();

    private static HashMap<Integer, Double> largerVesselsShipOneTwenty = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsShipTwoTwentyFive = new HashMap<Integer, Double>();
    private static HashMap<Integer, Double> largerVesselsShipThree = new HashMap<Integer, Double>();
    
    
    
    static {

        // Content Type
        SweepWidthTypes.put(0, "Person In Water (PIW)");
        SweepWidthTypes.put(1, "Raft 1 person");
        SweepWidthTypes.put(2, "Raft 4 persons");
        SweepWidthTypes.put(3, "Raft 6 persons");
        SweepWidthTypes.put(4, "Raft 8 persons");
        SweepWidthTypes.put(5, "Raft 10 persons");
        SweepWidthTypes.put(6, "Raft 15 persons");
        SweepWidthTypes.put(7, "Raft 20 persons");
        SweepWidthTypes.put(8, "Raft 25 persons");

        SweepWidthTypes.put(9, "Motorboat =< 15 feet");
        SweepWidthTypes.put(10, "Motorboat 20 feet");
        SweepWidthTypes.put(11, "Motorboat 33 feet");
        SweepWidthTypes.put(12, "Motorboat 53 feet");
        SweepWidthTypes.put(13, "Motorboat 78 feet");

        SweepWidthTypes.put(14, "Sailboat 15 feet");
        SweepWidthTypes.put(15, "Sailboat 20 feet");
        SweepWidthTypes.put(16, "Sailboat 25 feet");
        SweepWidthTypes.put(17, "Sailboat 30 feet");
        SweepWidthTypes.put(18, "Sailboat 40 feet");
        SweepWidthTypes.put(19, "Sailboat 50 feet");
        SweepWidthTypes.put(20, "Sailboat 70 feet");
        SweepWidthTypes.put(21, "Sailboat 83 feet");

        SweepWidthTypes.put(22, "Ship 120 feet");
        SweepWidthTypes.put(23, "Ship 225 feet");
        SweepWidthTypes.put(24, "Ship >= 300 feet");

        smallerVessels.put(0, smallerVesselsPIW);
        smallerVessels.put(1, smallerVesselsRaftOne);
        smallerVessels.put(2, smallerVesselsRaftFour);
        smallerVessels.put(3, smallerVesselsRaftSix);
        smallerVessels.put(4, smallerVesselsRaftEight);
        smallerVessels.put(5, smallerVesselsRaftTen);
        smallerVessels.put(6, smallerVesselsRaftFifteen);
        smallerVessels.put(7, smallerVesselsRaftTwenty);
        smallerVessels.put(8, smallerVesselsRaftTwentyFive);

        smallerVessels.put(9, smallerVesselsMotorboatFifteen);
        smallerVessels.put(10, smallerVesselsMotorboatTwenty);
        smallerVessels.put(11, smallerVesselsMotorboatThirtyThree);
        smallerVessels.put(12, smallerVesselsMotorboatFiftyThree);
        smallerVessels.put(13, smallerVesselsMotorboatSeventyEight);

        smallerVessels.put(14, smallerVesselsSailboatFifteen);
        smallerVessels.put(15, smallerVesselsSailboatTwenty);
        smallerVessels.put(16, smallerVesselsSailboatTwentyFive);
        smallerVessels.put(17, smallerVesselsSailboatThirty);
        smallerVessels.put(18, smallerVesselsSailboatFourty);
        smallerVessels.put(19, smallerVesselsSailboatFifty);
        smallerVessels.put(20, smallerVesselsSailboatSeventy);
        smallerVessels.put(21, smallerVesselsSailEightyThree);

        smallerVessels.put(22, smallerVesselsShipOneTwenty);
        smallerVessels.put(23, smallerVesselsShipTwoTwentyFive);
        smallerVessels.put(24, smallerVesselsShipThree);

        
        
        largerVessels.put(0, largerVesselsPIW);
        largerVessels.put(1, largerVesselsRaftOne);
        largerVessels.put(2, largerVesselsRaftFour);
        largerVessels.put(3, largerVesselsRaftSix);
        largerVessels.put(4, largerVesselsRaftEight);
        largerVessels.put(5, largerVesselsRaftTen);
        largerVessels.put(6, largerVesselsRaftFifteen);
        largerVessels.put(7, largerVesselsRaftTwenty);
        largerVessels.put(8, largerVesselsRaftTwentyFive);

        largerVessels.put(9, largerVesselsMotorboatFifteen);
        largerVessels.put(10, largerVesselsMotorboatTwenty);
        largerVessels.put(11, largerVesselsMotorboatThirtyThree);
        largerVessels.put(12, largerVesselsMotorboatFiftyThree);
        largerVessels.put(13, largerVesselsMotorboatSeventyEight);

        largerVessels.put(14, largerVesselsSailboatFifteen);
        largerVessels.put(15, largerVesselsSailboatTwenty);
        largerVessels.put(16, largerVesselsSailboatTwentyFive);
        largerVessels.put(17, largerVesselsSailboatThirty);
        largerVessels.put(18, largerVesselsSailboatFourty);
        largerVessels.put(19, largerVesselsSailboatFifty);
        largerVessels.put(20, largerVesselsSailboatSeventy);
        largerVessels.put(21, largerVesselsSailEightyThree);

        largerVessels.put(22, largerVesselsShipOneTwenty);
        largerVessels.put(23, largerVesselsShipTwoTwentyFive);
        largerVessels.put(24, largerVesselsShipThree);
        
        
        // Smaller vessels Person in Water values
        smallerVesselsPIW.put(1, 0.2);
        smallerVesselsPIW.put(3, 0.2);
        smallerVesselsPIW.put(5, 0.3);
        smallerVesselsPIW.put(10, 0.3);
        smallerVesselsPIW.put(15, 0.3);
        smallerVesselsPIW.put(20, 0.3);

        // Smaller vessels Raft 1 person values
        smallerVesselsRaftOne.put(1, 0.7);
        smallerVesselsRaftOne.put(3, 1.3);
        smallerVesselsRaftOne.put(5, 1.7);
        smallerVesselsRaftOne.put(10, 2.3);
        smallerVesselsRaftOne.put(15, 2.6);
        smallerVesselsRaftOne.put(20, 2.7);

        // Smaller vessels Raft 4 persons values
        smallerVesselsRaftFour.put(1, 0.7);
        smallerVesselsRaftFour.put(3, 1.7);
        smallerVesselsRaftFour.put(5, 2.2);
        smallerVesselsRaftFour.put(10, 3.1);
        smallerVesselsRaftFour.put(15, 3.5);
        smallerVesselsRaftFour.put(20, 3.9);

        // Smaller vessels Raft 6 persons values
        smallerVesselsRaftSix.put(1, 0.8);
        smallerVesselsRaftSix.put(3, 1.9);
        smallerVesselsRaftSix.put(5, 2.6);
        smallerVesselsRaftSix.put(10, 3.6);
        smallerVesselsRaftSix.put(15, 4.3);
        smallerVesselsRaftSix.put(20, 4.7);

        // Smaller vessels Raft 8 persons values
        smallerVesselsRaftEight.put(1, 0.8);
        smallerVesselsRaftEight.put(3, 2.0);
        smallerVesselsRaftEight.put(5, 2.7);
        smallerVesselsRaftEight.put(10, 3.8);
        smallerVesselsRaftEight.put(15, 4.4);
        smallerVesselsRaftEight.put(20, 4.9);
        
        // Smaller vessels Raft 10 persons values
        smallerVesselsRaftTen.put(1, 0.8);
        smallerVesselsRaftTen.put(3, 2.0);
        smallerVesselsRaftTen.put(5, 2.8);
        smallerVesselsRaftTen.put(10, 4.0);
        smallerVesselsRaftTen.put(15, 4.8);
        smallerVesselsRaftTen.put(20, 5.3);
        
        // Smaller vessels Raft 15 persons values
        smallerVesselsRaftFifteen.put(1, 0.9);
        smallerVesselsRaftFifteen.put(3, 2.2);
        smallerVesselsRaftFifteen.put(5, 3.0);
        smallerVesselsRaftFifteen.put(10, 4.3);
        smallerVesselsRaftFifteen.put(15, 5.1);
        smallerVesselsRaftFifteen.put(20, 5.7);
        
        // Smaller vessels Raft 20 persons values
        smallerVesselsRaftTwenty.put(1, 0.9);
        smallerVesselsRaftTwenty.put(3, 2.3);
        smallerVesselsRaftTwenty.put(5, 3.3);
        smallerVesselsRaftTwenty.put(10, 4.9);
        smallerVesselsRaftTwenty.put(15, 5.8);
        smallerVesselsRaftTwenty.put(20, 6.5);

        // Smaller vessels Raft 25 persons values
        smallerVesselsRaftTwentyFive.put(1, 0.9);
        smallerVesselsRaftTwentyFive.put(3, 2.4);
        smallerVesselsRaftTwentyFive.put(5, 3.5);
        smallerVesselsRaftTwentyFive.put(10, 5.2);
        smallerVesselsRaftTwentyFive.put(15, 6.3);
        smallerVesselsRaftTwentyFive.put(20, 7.0);
        
        // Smaller vessels Motorboat less than 15 feet values
        smallerVesselsMotorboatFifteen.put(1, 0.4);
        smallerVesselsMotorboatFifteen.put(3, 0.8);
        smallerVesselsMotorboatFifteen.put(5, 1.1);
        smallerVesselsMotorboatFifteen.put(10, 1.5);
        smallerVesselsMotorboatFifteen.put(15, 1.6);
        smallerVesselsMotorboatFifteen.put(20, 1.8);
        
        // Smaller vessels Motorboat 20 feet values
        smallerVesselsMotorboatTwenty.put(1, 0.8);
        smallerVesselsMotorboatTwenty.put(3, 1.5);
        smallerVesselsMotorboatTwenty.put(5, 2.2);
        smallerVesselsMotorboatTwenty.put(10, 3.3);
        smallerVesselsMotorboatTwenty.put(15, 4.0);
        smallerVesselsMotorboatTwenty.put(20, 4.5);

        // Smaller vessels Motorboat 33 feet values
        smallerVesselsMotorboatThirtyThree.put(1, 0.8);
        smallerVesselsMotorboatThirtyThree.put(3, 1.9);
        smallerVesselsMotorboatThirtyThree.put(5, 2.9);
        smallerVesselsMotorboatThirtyThree.put(10, 4.7);
        smallerVesselsMotorboatThirtyThree.put(15, 5.9);
        smallerVesselsMotorboatThirtyThree.put(20, 6.8);

        // Smaller vessels Motorboat 53 feet values
        smallerVesselsMotorboatFiftyThree.put(1, 0.9);
        smallerVesselsMotorboatFiftyThree.put(3, 2.4);
        smallerVesselsMotorboatFiftyThree.put(5, 3.9);
        smallerVesselsMotorboatFiftyThree.put(10, 7.0);
        smallerVesselsMotorboatFiftyThree.put(15, 9.3);
        smallerVesselsMotorboatFiftyThree.put(20, 11.1);
        
        // Smaller vessels Motorboat 78 feet values
        smallerVesselsMotorboatSeventyEight.put(1, 0.9);
        smallerVesselsMotorboatSeventyEight.put(3, 2.5);
        smallerVesselsMotorboatSeventyEight.put(5, 4.3);
        smallerVesselsMotorboatSeventyEight.put(10, 8.3);
        smallerVesselsMotorboatSeventyEight.put(15, 11.4);
        smallerVesselsMotorboatSeventyEight.put(20, 14.0);

        // Smaller vessels Sailboat 15 feet values
        smallerVesselsSailboatFifteen.put(1, 0.8);
        smallerVesselsSailboatFifteen.put(3, 1.5);
        smallerVesselsSailboatFifteen.put(5, 2.1);
        smallerVesselsSailboatFifteen.put(10, 3.0);
        smallerVesselsSailboatFifteen.put(15, 3.6);
        smallerVesselsSailboatFifteen.put(20, 4.0);

        // Smaller vessels Sailboat 20 feet values
        smallerVesselsSailboatTwenty.put(1, 0.8);
        smallerVesselsSailboatTwenty.put(3, 1.7);
        smallerVesselsSailboatTwenty.put(5, 2.5);
        smallerVesselsSailboatTwenty.put(10, 3.7);
        smallerVesselsSailboatTwenty.put(15, 4.6);
        smallerVesselsSailboatTwenty.put(20, 5.1);
        
        // Smaller vessels Sailboat 25 feet values
        smallerVesselsSailboatTwentyFive.put(1, 0.9);
        smallerVesselsSailboatTwentyFive.put(3, 1.9);
        smallerVesselsSailboatTwentyFive.put(5, 2.8);
        smallerVesselsSailboatTwentyFive.put(10, 4.4);
        smallerVesselsSailboatTwentyFive.put(15, 5.4);
        smallerVesselsSailboatTwentyFive.put(20, 6.3);
        
        // Smaller vessels Sailboat 30 feet values
        smallerVesselsSailboatThirty.put(1, 0.9);
        smallerVesselsSailboatThirty.put(3, 2.1);
        smallerVesselsSailboatThirty.put(5, 3.2);
        smallerVesselsSailboatThirty.put(10, 5.3);
        smallerVesselsSailboatThirty.put(15, 6.6);
        smallerVesselsSailboatThirty.put(20, 7.7);
        
        // Smaller vessels Sailboat 40 feet values
        smallerVesselsSailboatFourty.put(1, 0.9);
        smallerVesselsSailboatFourty.put(3, 2.3);
        smallerVesselsSailboatFourty.put(5, 3.8);
        smallerVesselsSailboatFourty.put(10, 6.6);
        smallerVesselsSailboatFourty.put(15, 8.6);
        smallerVesselsSailboatFourty.put(20, 10.3);
        
        // Smaller vessels Sailboat 50 feet values
        smallerVesselsSailboatFifty.put(1, 0.9);
        smallerVesselsSailboatFifty.put(3, 2.4);
        smallerVesselsSailboatFifty.put(5, 4.0);
        smallerVesselsSailboatFifty.put(10, 7.3);
        smallerVesselsSailboatFifty.put(15, 9.7);
        smallerVesselsSailboatFifty.put(20, 11.6);

        // Smaller vessels Sailboat 70 feet values
        smallerVesselsSailboatSeventy.put(1, 0.9);
        smallerVesselsSailboatSeventy.put(3, 2.5);
        smallerVesselsSailboatSeventy.put(5, 4.2);
        smallerVesselsSailboatSeventy.put(10, 7.9);
        smallerVesselsSailboatSeventy.put(15, 10.7);
        smallerVesselsSailboatSeventy.put(20, 13.1);

        // Smaller vessels Sailboat 83 feet values
        smallerVesselsSailEightyThree.put(1, 0.9);
        smallerVesselsSailEightyThree.put(3, 2.5);
        smallerVesselsSailEightyThree.put(5, 4.4);
        smallerVesselsSailEightyThree.put(10, 8.3);
        smallerVesselsSailEightyThree.put(15, 11.6);
        smallerVesselsSailEightyThree.put(20, 14.2);     
        
        // Smaller vessels Ship 120 feet values
        smallerVesselsShipOneTwenty.put(1, 1.4);
        smallerVesselsShipOneTwenty.put(3, 2.5);
        smallerVesselsShipOneTwenty.put(5, 4.6);
        smallerVesselsShipOneTwenty.put(10, 9.3);
        smallerVesselsShipOneTwenty.put(15, 13.2);
        smallerVesselsShipOneTwenty.put(20, 16.6);     
        
        // Smaller vessels Ship 225 feet values
        smallerVesselsShipTwoTwentyFive.put(1, 1.4);
        smallerVesselsShipTwoTwentyFive.put(3, 2.6);
        smallerVesselsShipTwoTwentyFive.put(5, 4.9);
        smallerVesselsShipTwoTwentyFive.put(10, 10.3);
        smallerVesselsShipTwoTwentyFive.put(15, 15.5);
        smallerVesselsShipTwoTwentyFive.put(20, 20.2);     
        
        // Smaller vessels Ship larger than 300 feet values
        smallerVesselsShipThree.put(1, 1.4);
        smallerVesselsShipThree.put(3, 2.6);
        smallerVesselsShipThree.put(5, 4.9);
        smallerVesselsShipThree.put(10, 10.9);
        smallerVesselsShipThree.put(15, 16.8);
        smallerVesselsShipThree.put(20, 22.5);    

        
        
        // Larger vessels values
        
        // Smaller vessels Person in Water values
        largerVesselsPIW.put(1, 0.3);
        largerVesselsPIW.put(3, 0.4);
        largerVesselsPIW.put(5, 0.5);
        largerVesselsPIW.put(10, 0.5);
        largerVesselsPIW.put(15, 0.5);
        largerVesselsPIW.put(20, 0.5);

        // Smaller vessels Raft 1 person values
        largerVesselsRaftOne.put(1, 0.9);
        largerVesselsRaftOne.put(3, 1.8);
        largerVesselsRaftOne.put(5, 2.3);
        largerVesselsRaftOne.put(10, 3.1);
        largerVesselsRaftOne.put(15, 3.4);
        largerVesselsRaftOne.put(20, 3.7);

        // Smaller vessels Raft 4 persons values
        largerVesselsRaftFour.put(1, 1.0);
        largerVesselsRaftFour.put(3, 2.2);
        largerVesselsRaftFour.put(5, 3.0);
        largerVesselsRaftFour.put(10, 4.0);
        largerVesselsRaftFour.put(15, 4.6);
        largerVesselsRaftFour.put(20, 5.0);

        // Smaller vessels Raft 6 persons values
        largerVesselsRaftSix.put(1, 1.1);
        largerVesselsRaftSix.put(3, 2.5);
        largerVesselsRaftSix.put(5, 3.4);
        largerVesselsRaftSix.put(10, 4.7);
        largerVesselsRaftSix.put(15, 5.5);
        largerVesselsRaftSix.put(20, 6.0);

        // Smaller vessels Raft 8 persons values
        largerVesselsRaftEight.put(1, 1.1);
        largerVesselsRaftEight.put(3, 2.5);
        largerVesselsRaftEight.put(5, 3.5);
        largerVesselsRaftEight.put(10, 4.8);
        largerVesselsRaftEight.put(15, 5.7);
        largerVesselsRaftEight.put(20, 6.2);
        
        // Smaller vessels Raft 10 persons values
        largerVesselsRaftTen.put(1, 1.1);
        largerVesselsRaftTen.put(3, 2.6);
        largerVesselsRaftTen.put(5, 3.6);
        largerVesselsRaftTen.put(10, 5.1);
        largerVesselsRaftTen.put(15, 6.1);
        largerVesselsRaftTen.put(20, 6.7);
        
        // Smaller vessels Raft 15 persons values
        largerVesselsRaftFifteen.put(1, 1.1);
        largerVesselsRaftFifteen.put(3, 2.8);
        largerVesselsRaftFifteen.put(5, 3.8);
        largerVesselsRaftFifteen.put(10, 5.5);
        largerVesselsRaftFifteen.put(15, 6.5);
        largerVesselsRaftFifteen.put(20, 7.2);
        
        // Smaller vessels Raft 20 persons values
        largerVesselsRaftTwenty.put(1, 1.2);
        largerVesselsRaftTwenty.put(3, 3.0);
        largerVesselsRaftTwenty.put(5, 4.1);
        largerVesselsRaftTwenty.put(10, 6.1);
        largerVesselsRaftTwenty.put(15, 7.3);
        largerVesselsRaftTwenty.put(20, 8.1);

        // Smaller vessels Raft 25 persons values
        largerVesselsRaftTwentyFive.put(1, 1.2);
        largerVesselsRaftTwentyFive.put(3, 3.1);
        largerVesselsRaftTwentyFive.put(5, 4.3);
        largerVesselsRaftTwentyFive.put(10, 6.4);
        largerVesselsRaftTwentyFive.put(15, 7.8);
        largerVesselsRaftTwentyFive.put(20, 8.7);
        
        // Smaller vessels Motorboat less than 15 feet values
        largerVesselsMotorboatFifteen.put(1, 0.5);
        largerVesselsMotorboatFifteen.put(3, 1.1);
        largerVesselsMotorboatFifteen.put(5, 1.4);
        largerVesselsMotorboatFifteen.put(10, 1.9);
        largerVesselsMotorboatFifteen.put(15, 2.1);
        largerVesselsMotorboatFifteen.put(20, 2.3);
        
        // Smaller vessels Motorboat 20 feet values
        largerVesselsMotorboatTwenty.put(1, 1.0);
        largerVesselsMotorboatTwenty.put(3, 2.0);
        largerVesselsMotorboatTwenty.put(5, 2.9);
        largerVesselsMotorboatTwenty.put(10, 4.3);
        largerVesselsMotorboatTwenty.put(15, 5.2);
        largerVesselsMotorboatTwenty.put(20, 5.8);

        // Smaller vessels Motorboat 33 feet values
        largerVesselsMotorboatThirtyThree.put(1, 1.1);
        largerVesselsMotorboatThirtyThree.put(3, 2.5);
        largerVesselsMotorboatThirtyThree.put(5, 3.8);
        largerVesselsMotorboatThirtyThree.put(10, 6.1);
        largerVesselsMotorboatThirtyThree.put(15, 7.7);
        largerVesselsMotorboatThirtyThree.put(20, 8.8);

        // Smaller vessels Motorboat 53 feet values
        largerVesselsMotorboatFiftyThree.put(1, 1.2);
        largerVesselsMotorboatFiftyThree.put(3, 3.1);
        largerVesselsMotorboatFiftyThree.put(5, 5.1);
        largerVesselsMotorboatFiftyThree.put(10, 9.1);
        largerVesselsMotorboatFiftyThree.put(15, 12.1);
        largerVesselsMotorboatFiftyThree.put(20, 14.4);
        
        // Smaller vessels Motorboat 78 feet values
        largerVesselsMotorboatSeventyEight.put(1, 1.2);
        largerVesselsMotorboatSeventyEight.put(3, 3.2);
        largerVesselsMotorboatSeventyEight.put(5, 5.6);
        largerVesselsMotorboatSeventyEight.put(10, 10.7);
        largerVesselsMotorboatSeventyEight.put(15, 14.7);
        largerVesselsMotorboatSeventyEight.put(20, 18.1);

        // Smaller vessels Sailboat 15 feet values
        largerVesselsSailboatFifteen.put(1, 1.0);
        largerVesselsSailboatFifteen.put(3, 1.9);
        largerVesselsSailboatFifteen.put(5, 2.7);
        largerVesselsSailboatFifteen.put(10, 3.9);
        largerVesselsSailboatFifteen.put(15, 4.7);
        largerVesselsSailboatFifteen.put(20, 5.2);

        // Smaller vessels Sailboat 20 feet values
        largerVesselsSailboatTwenty.put(1, 1.0);
        largerVesselsSailboatTwenty.put(3, 2.2);
        largerVesselsSailboatTwenty.put(5, 3.2);
        largerVesselsSailboatTwenty.put(10, 4.8);
        largerVesselsSailboatTwenty.put(15, 5.9);
        largerVesselsSailboatTwenty.put(20, 6.6);
        
        // Smaller vessels Sailboat 25 feet values
        largerVesselsSailboatTwentyFive.put(1, 1.1);
        largerVesselsSailboatTwentyFive.put(3, 2.4);
        largerVesselsSailboatTwentyFive.put(5, 3.6);
        largerVesselsSailboatTwentyFive.put(10, 5.7);
        largerVesselsSailboatTwentyFive.put(15, 7.0);
        largerVesselsSailboatTwentyFive.put(20, 8.1);
        
        // Smaller vessels Sailboat 30 feet values
        largerVesselsSailboatThirty.put(1, 1.1);
        largerVesselsSailboatThirty.put(3, 2.7);
        largerVesselsSailboatThirty.put(5, 4.1);
        largerVesselsSailboatThirty.put(10, 6.8);
        largerVesselsSailboatThirty.put(15, 8.6);
        largerVesselsSailboatThirty.put(20, 10.0);
        
        // Smaller vessels Sailboat 40 feet values
        largerVesselsSailboatFourty.put(1, 1.2);
        largerVesselsSailboatFourty.put(3, 3.0);
        largerVesselsSailboatFourty.put(5, 4.9);
        largerVesselsSailboatFourty.put(10, 8.5);
        largerVesselsSailboatFourty.put(15, 11.2);
        largerVesselsSailboatFourty.put(20, 13.3);
        
        // Smaller vessels Sailboat 50 feet values
        largerVesselsSailboatFifty.put(1, 1.2);
        largerVesselsSailboatFifty.put(3, 3.1);
        largerVesselsSailboatFifty.put(5, 5.2);
        largerVesselsSailboatFifty.put(10, 9.4);
        largerVesselsSailboatFifty.put(15, 12.5);
        largerVesselsSailboatFifty.put(20, 15.0);

        // Smaller vessels Sailboat 70 feet values
        largerVesselsSailboatSeventy.put(1, 1.2);
        largerVesselsSailboatSeventy.put(3, 3.2);
        largerVesselsSailboatSeventy.put(5, 5.5);
        largerVesselsSailboatSeventy.put(10, 10.2);
        largerVesselsSailboatSeventy.put(15, 13.9);
        largerVesselsSailboatSeventy.put(20, 16.9);

        // Smaller vessels Sailboat 83 feet values
        largerVesselsSailEightyThree.put(1, 1.2);
        largerVesselsSailEightyThree.put(3, 3.3);
        largerVesselsSailEightyThree.put(5, 5.7);
        largerVesselsSailEightyThree.put(10, 10.8);
        largerVesselsSailEightyThree.put(15, 15.0);
        largerVesselsSailEightyThree.put(20, 18.4);     
        
        // Smaller vessels Ship 120 feet values
        largerVesselsShipOneTwenty.put(1, 1.8);
        largerVesselsShipOneTwenty.put(3, 3.3);
        largerVesselsShipOneTwenty.put(5, 6.0);
        largerVesselsShipOneTwenty.put(10, 12.0);
        largerVesselsShipOneTwenty.put(15, 17.1);
        largerVesselsShipOneTwenty.put(20, 21.5);     
        
        // Smaller vessels Ship 225 feet values
        largerVesselsShipTwoTwentyFive.put(1, 1.8);
        largerVesselsShipTwoTwentyFive.put(3, 3.4);
        largerVesselsShipTwoTwentyFive.put(5, 6.3);
        largerVesselsShipTwoTwentyFive.put(10, 13.4);
        largerVesselsShipTwoTwentyFive.put(15, 20.1);
        largerVesselsShipTwoTwentyFive.put(20, 26.0);     
        
        // Smaller vessels Ship larger than 300 feet values
        largerVesselsShipThree.put(1, 1.8);
        largerVesselsShipThree.put(3, 3.4);
        largerVesselsShipThree.put(5, 6.4);
        largerVesselsShipThree.put(10, 14.1);
        largerVesselsShipThree.put(15, 21.8);
        largerVesselsShipThree.put(20, 29.2);    
        
    }



    /**
     * @return the sweepWidthTypes
     */
    public static HashMap<Integer, String> getSweepWidthTypes() {
        return SweepWidthTypes;
    }



    /**
     * @param sweepWidthTypes the sweepWidthTypes to set
     */
    public static void setSweepWidthTypes(HashMap<Integer, String> sweepWidthTypes) {
        SweepWidthTypes = sweepWidthTypes;
    }



    /**
     * @return the smallerVessels
     */
    public static HashMap<Integer, HashMap<Integer, Double>> getSmallerVessels() {
        return smallerVessels;
    }



    /**
     * @param smallerVessels the smallerVessels to set
     */
    public static void setSmallerVessels(
            HashMap<Integer, HashMap<Integer, Double>> smallerVessels) {
        SweepWidthValues.smallerVessels = smallerVessels;
    }



    /**
     * @return the largerVessels
     */
    public static HashMap<Integer, HashMap<Integer, Double>> getLargerVessels() {
        return largerVessels;
    }



    /**
     * @param largerVessels the largerVessels to set
     */
    public static void setLargerVessels(
            HashMap<Integer, HashMap<Integer, Double>> largerVessels) {
        SweepWidthValues.largerVessels = largerVessels;
    }
    
    
    
}
