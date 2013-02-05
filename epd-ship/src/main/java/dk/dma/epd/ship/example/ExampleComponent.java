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
package dk.dma.epd.ship.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

/**
 * An example component to show how plugin components could be added in
 * eeins.properties. 
 */
public class ExampleComponent extends MapHandlerChild {
    
    private static final Logger LOG =LoggerFactory.getLogger(ExampleComponent.class);
    
    public ExampleComponent() {
        // Called when class is created
    }
    
    /**
     * Find other components
     */
    @Override
    public void findAndInit(Object obj) {
        LOG.info("findAndInit obj.getClass(): " + obj.getClass()); 
    }
    
    @Override
    public void findAndUndo(Object obj) {
        // Unregister other components
    }
    
}
