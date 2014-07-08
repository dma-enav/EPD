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
package dk.dma.epd.common.prototype.gui;

import java.awt.Component;

import javax.swing.JPanel;

/**
 * Interface that should be implemented by the map frame component,
 * i.e. the widget that holds the maps.
 * <p>
 * In EPDShip that will be the {@code MainFrame} class and in EPDShore 
 * that will be the {@code JMapFrame} class.
 */
public interface IMapFrame {

    
    /**
     * Function for getting the glassPanel of the map frame
     * @return glassPanel the glassPanel of the map frame
     */
    JPanel getGlassPanel();
    
    /**
     * Returns a reference to the map frame cast as a component
     * @return a reference to the map frame cast as a component
     */
    Component asComponent();
    
    /**
     * Disposes the component
     */
    void dispose();
}
