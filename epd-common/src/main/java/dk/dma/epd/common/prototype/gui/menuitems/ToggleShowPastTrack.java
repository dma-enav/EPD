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
package dk.dma.epd.common.prototype.gui.menuitems;

import javax.swing.JMenuItem;

import dk.dma.epd.common.prototype.ais.IAisTargetListener;
import dk.dma.epd.common.prototype.ais.MobileTarget;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

/**
 * Toggle the visibility of the past track for a specific vessel
 */
public class ToggleShowPastTrack extends JMenuItem implements IMapMenuAction {

   private static final long serialVersionUID = 1L;
   
   private MobileTarget mobileTarget;
   private IAisTargetListener aisLayer;

   /**
    * Constructor
    */
   public ToggleShowPastTrack() {
       super();
   }
   
   /**
    * Called when the menu item is enacted
    */
   @Override
   public void doAction() {
       // Toggle past-track visibility
       mobileTarget.getSettings().setShowPastTrack(!mobileTarget.getSettings().isShowPastTrack());
       if (aisLayer != null) {
           aisLayer.targetUpdated(mobileTarget);
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
