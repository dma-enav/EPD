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
package dk.dma.epd.shore;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import dk.dma.epd.common.prototype.BootstrapCommon;

/**
 * @author Kasper Nielsen, David Camre
 */
class Bootstrap extends BootstrapCommon{
    
    public void Boostrap() {
        home = Paths.get(System.getProperty("user.home"), ".epd-shore");
    }

    public void run() throws IOException {

        Files.createDirectories(home);

        // Used from log4j to place log files
        System.setProperty("dma.app.home", home.toString());
        
        // Log4j
        unpackToAppHome("log4j.xml");
        //DOMConfigurator.configure(home.resolve("log4j.xml").toUri().toURL());

        // Properties
        unpackToAppHome("epd-shore.properties");
        unpackToAppHome("settings.properties");
        unpackToAppHome("transponder.xml");

        unpackFolderToAppHome("workspaces");
        unpackFolderToAppHome("routes");
        unpackFolderToAppHome("shape/GSHHS_shp");

        // update location of shape files to user.home
        EPDShore.loadProperties();
        String prev = EPDShore.properties.getProperty("background.shapeFile");
        EPDShore.properties.put("background.shapeFile", home.resolve(prev).toString());
        prev = EPDShore.properties.getProperty("background.spatialIndex");
        EPDShore.properties.put("background.spatialIndex", home.resolve(prev).toString());
    }
}
