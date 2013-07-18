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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.io.Resources;

/***
 * Inject required serial communication native library file into path and load native library
 * @author jtj-sfs
 *
 * TODO: Doing more clean "runtime libraries" injection is difficult but it should be possible. Could move this to the boostrap routines.
 */
public class NmeaSerialSensorFactory {
    
    private static final Path EPDNATIVEPATH = Paths.get(System.getProperty("java.io.tmpdir"),"/epdNative/").toAbsolutePath();

    public static NmeaSerialSensor create(String comPort) {
        try {
            Files.createDirectories(EPDNATIVEPATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        } catch (UnsatisfiedLinkError e1) {
            System.out.println(System.getProperty("java.library.path"));
            e1.printStackTrace(); 
        }

        return new NmeaSerialSensor(comPort);
        
    }
    
    private static void addToLibraryPath(Path path)
            throws NoSuchFieldException, 
             IllegalAccessException {
        System.setProperty("java.library.path", path.toAbsolutePath().toString());
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }
    
    public static void unpackLibs() {
        String filename = "";
        String libDir = "";
        
        final String osArch =  System.getProperty("os.arch");
        final String osName = System.getProperty("os.name");
        
        if (osName.startsWith("Windows")) {
            filename = "rxtxSerial.dll";
            
            if (osArch.indexOf("64") != -1) {
                libDir = "Windows/mfz-rxtx-2.2-20081207-win-x64/";
            } else {
                libDir = "Windows/i368-mingw32/";
            }
            
            
        } else if (osName.equals("Linux")) {
            filename = "librxtxSerial.so";
            if (osArch.equals("amd64")) {
                libDir = "Linux/x86_64-unknown-linu-gnu/";
            } else {
                libDir = "Linux/i686-unknown-linux-gnu/";
            }
                    
        } else if (osName.startsWith("Mac")) {
            filename = "rxtxSerial.jnilib";
            libDir = "Mac_OS_X/";
            
        } else {
            return;
        }
                
        
        try {
            File dest = Paths.get(EPDNATIVEPATH.toAbsolutePath().toString(),filename).toAbsolutePath().toFile();      
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
