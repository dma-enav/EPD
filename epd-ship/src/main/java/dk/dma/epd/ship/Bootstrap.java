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
package dk.dma.epd.ship;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.log4j.xml.DOMConfigurator;

import dk.dma.epd.common.prototype.BootstrapCommon;


public class Bootstrap extends BootstrapCommon {
    Bootstrap() {
        home = Paths.get(System.getProperty("user.home"), ".epd-ship");
    }


    @Override
    public void run() throws IOException {

        Files.createDirectories(home);

        // Used from log4j to place log files
        System.setProperty("dma.app.home", home.toString());

        // Log4j
        unpackToAppHome("log4j.xml");
        // actually use the definitions
        DOMConfigurator.configure(home.resolve("log4j.xml").toUri().toURL());

        // Properties
        unpackToAppHome("epd-ship.properties");
        unpackToAppHome("enc_navicon.properties");
        unpackToAppHome("settings.properties");
        unpackToAppHome("transponder.xml");

        unpackFolderToAppHome("routes");
        unpackFolderToAppHome("layout/static");
        unpackFolderToAppHome("shape/GSHHS_shp");

        // update location of shape files to user.home
        EPDShip.loadProperties();
        String prev = EPDShip.properties.getProperty("background.shapeFile");
        EPDShip.properties.put("background.shapeFile", home.resolve(prev).toString());
        prev = EPDShip.properties.getProperty("background.spatialIndex");
        EPDShip.properties.put("background.spatialIndex", home.resolve(prev).toString());

    }
    

    
}
