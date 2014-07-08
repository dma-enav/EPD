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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.ais.PastTrackSortedSet;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

/**
 * Clears the list of past track points for a specific vessel
 */
public class ClearPastTrack extends JMenuItem implements IMapMenuAction {

   private static final long serialVersionUID = 1L;
   
   private MobileTarget mobileTarget;
   private IAisTargetListener aisLayer;

   /**
    * Constructor
    */
   public ClearPastTrack() {
       super();
   }
   
   /**
    * Called when the menu item is enacted
    */
   @Override
   public void doAction() {
       if (JOptionPane.showConfirmDialog(
               this, 
               "Delete current past-track?", 
               "Delete Past-Track", 
               JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
           // Clear the past track data
           mobileTarget.setPastTrackData(new PastTrackSortedSet());
           if (aisLayer != null) {
               aisLayer.targetUpdated(mobileTarget);
           }
       }
   }
   
   /**
    * Sets the mobile target
    * @param mobileTarget the mobile target
    */
   public void setMobileTarget(MobileTarget mobileTarget) {
       this.mobileTarget = mobileTarget;
   }   

   /**
    * Sets the AIS layer
    * @param aisLayer the AIS layer
    */
   public void setAisLayer(IAisTargetListener aisLayer) {
       this.aisLayer = aisLayer;
   }
}
