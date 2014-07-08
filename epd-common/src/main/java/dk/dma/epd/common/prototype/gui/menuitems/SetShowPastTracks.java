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

import dk.dma.epd.common.prototype.ais.AisHandlerCommon;
import dk.dma.epd.common.prototype.gui.menuitems.event.IMapMenuAction;

/**
 * Menu item that will either show or hide all past-tracks
 */
public class SetShowPastTracks extends JMenuItem implements IMapMenuAction {
   
   private static final long serialVersionUID = 1L;
   private AisHandlerCommon aisHandler;
   private boolean showPastTracks;

   /**
    * Constructor
    * @param text the menu text
    * @param showPastTracks whether to show or hide all past-tracks
    */
   public SetShowPastTracks(String text, boolean showPastTracks) {
       super();
       setText(text);
       this.showPastTracks = showPastTracks;
   }
   
   /**
    * Called when the menu item is enacted
    */
   @Override
   public void doAction() {
       aisHandler.setShowAllPastTracks(showPastTracks);
   }
   
   /**
    * Sets the current {@linkplain AisHandlerCommon} entity
    * @param aisHandler
    */
   public void setAisHandler(AisHandlerCommon aisHandler) {
       this.aisHandler = aisHandler;
   }
}
