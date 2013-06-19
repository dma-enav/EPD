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
package dk.dma.epd.common.prototype.sensor.nmea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.List;

import com.google.common.io.Resources;

/***
 * Inject required serial communication dll file into path and load native library
 * @author jtj-sfs
 *
 * TODO: make work for linux/mac
 */
public class NmeaSerialSensorFactory {
    
    private static final String EPDNATIVEPATH = 
            Paths.get(System.getProperty("java.io.tmpdir"),"epdNative").toAbsolutePath().toString();
    
    public static NmeaSerialSensor create(String comPort) {
        
        unpackLibs();
        
        try {
            addToLibraryPath(EPDNATIVEPATH);
            System.loadLibrary("rxtxSerial");
        } catch (NoSuchFieldException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        return new NmeaSerialSensor(comPort);
        
    }
    
    private static void addToLibraryPath(String path)
            throws NoSuchFieldException, 
             IllegalAccessException {
        System.setProperty("java.library.path", path);
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }
    
    private static List<String> findLibs() {
        /*Path mainDir = Paths.get(NmeaSerialSensorFactory.class.getResource("/gun/io").toString());
        
        
        Files.walkFileTree(mainDir, new SimpleFil) {
        })
        */
        
        return null;
    }
    
    
    public static void unpackLibs() {
        findLibs();
        String filename = "";
        String libDir = "";
        if (System.getProperty("os.name").startsWith("Windows")) {
            filename = "rxtxSerial.dll";
            libDir = "Windows/i368-mingw32/";
        } else if ("Linux".equals(System.getProperty("os.name"))) {
            //TODO: implement for linux/mac/etc 
        } else {
            return;
        }
                
        
        try {
            File dest = Paths.get(EPDNATIVEPATH,filename).toAbsolutePath().toFile();      
            dest.createNewFile();
            
            FileOutputStream destOut = new FileOutputStream(dest);

            String resource = "/gnu/io/"+libDir+filename;
            System.out.println(resource);            
            
            
            Resources.copy(NmeaSerialSensorFactory.class.getResource(resource), destOut);
            
            destOut.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        

       
    }

}
