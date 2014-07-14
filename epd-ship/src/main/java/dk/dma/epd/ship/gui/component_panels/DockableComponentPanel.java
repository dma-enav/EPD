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
package dk.dma.epd.ship.gui.component_panels;

/**
 * Interface that must be implemented by all dockable component panels
 */
public interface DockableComponentPanel {

    /**
     * Used as title and ID for the dockable component
     * @return the title and ID for the dockable component
     */
    String getDockableComponentName();
    
    /**
     * Returns whether or not to include the panel in a default layout
     * @return whether or not to include the panel in a default layout
     */
    boolean includeInDefaultLayout();
    
    /**
     * Returns whether or not to include the panel in the "Panels" menu
     * @return whether or not to include the panel in the "Panels" menu
     */
    boolean includeInPanelsMenu();
}
