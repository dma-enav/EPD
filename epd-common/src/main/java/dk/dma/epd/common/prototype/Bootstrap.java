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
package dk.dma.epd.common.prototype;

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


public class Bootstrap {
    Path home;

    public void run(EPD epd, String[] appHomeFiles, String[] appHomeFolders) throws IOException {
        
        home = epd.getHomePath();

        Files.createDirectories(home);

        // Used from log4j to place log files
        System.setProperty("dma.app.home", home.toString());

        // Log4j
        unpackToAppHome("log4j.xml");
        // actually use the definitions
        DOMConfigurator.configure(home.resolve("log4j.xml").toUri().toURL());

        // Properties
        for (String appHomeFile : appHomeFiles) {
            unpackToAppHome(appHomeFile);
        }
        for (String appHomeFolder : appHomeFolders) {
            unpackFolderToAppHome(appHomeFolder);
        }

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
