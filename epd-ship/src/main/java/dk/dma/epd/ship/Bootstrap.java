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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.google.common.io.Resources;

import dk.dma.epd.common.prototype.EPD;


public class Bootstrap {
    Path home;

    public void run(EPD<?> epd) throws IOException {
        
        home = epd.getHomePath();

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
        Properties properties = epd.loadProperties();
        String prev = properties.getProperty("background.WorldOutLine.shapeFile");
        properties.put("background.WorldOutLine.shapeFile", home.resolve(prev).toString());
        
        prev = properties.getProperty("background.InternalWaters.shapeFile");
        properties.put("background.InternalWaters.shapeFile", home.resolve(prev).toString());
        
        prev = properties.getProperty("background.InternalArea.shapeFile");
        properties.put("background.InternalArea.shapeFile", home.resolve(prev).toString());
        
        
        prev = properties.getProperty("background.WorldOutLine.spatialIndex");
        properties.put("background.WorldOutLine.spatialIndex", home.resolve(prev).toString());
        
        prev = properties.getProperty("background.InternalWaters.spatialIndex");
        properties.put("background.InternalWaters.spatialIndex", home.resolve(prev).toString());
        
        prev = properties.getProperty("background.InternalArea.spatialIndex");
        properties.put("background.InternalArea.spatialIndex", home.resolve(prev).toString());

    }
    protected void unpackFolderToAppHome(String folder) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext();
        // we do not support recursive folders
        Resource[] xmlResources = context.getResources("classpath:/" + folder + "/*.*");
        Path f = home.resolve(folder);
        if (!Files.exists(f)) {
            Files.createDirectories(f);
        }
        for (Resource r : xmlResources) {
            Path destination = f.resolve(r.getFilename());
            if (!Files.exists(destination)) {
                Resources.copy(r.getURL(), Files.newOutputStream(destination));
            }
        }

    }

    protected void unpackToAppHome(String filename) throws IOException {
        Path destination = home.resolve(filename);
        if (!Files.exists(destination)) {
            URL url = getClass().getResource("/" + filename);
            if (url == null) {
                throw new Error("Missing file src/resources/" + filename);
            }
            Resources.copy(url, Files.newOutputStream(destination));
        }
    }
}
