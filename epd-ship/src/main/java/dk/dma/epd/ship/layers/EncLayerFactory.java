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
package dk.dma.epd.ship.layers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.layer.OMGraphicHandlerLayer;
import com.bbn.openmap.util.PropUtils;

import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.settings.EPDMapSettings;

/**
 * Factory class for creating ENC layer. If ENC is enabled is uses the file
 * enc.properties to define class and settings.
 * 
 */
public class EncLayerFactory {

    private static final Logger LOG = LoggerFactory
            .getLogger(EncLayerFactory.class);
    private Properties encProps = new Properties();
    private EPDMapSettings mapSettings;
    private OMGraphicHandlerLayer encLayer;

    private static void addSoftwareLibrary(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL",
                new Class[] { URL.class });
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { file
                .toURI().toURL() });
    }

    public static void addToLibraryPath(String path)
            throws NoSuchFieldException, IllegalAccessException {
        System.setProperty("java.library.path", path);
        Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
        fieldSysPath.setAccessible(true);
        fieldSysPath.set(null, null);
    }

    public EncLayerFactory(EPDMapSettings mapSettings) {
        this.mapSettings = mapSettings;
        // Use ENC?
        if (!mapSettings.isUseEnc()) {
            return;
        }

        // // Try to load ENC props
        // if (!PropUtils.loadProperties(encProps, "..\\..\\.epd-ship",
        // "enc.properties")) {
        if (!PropUtils.loadProperties(encProps, EPDShip.getInstance().getHomePath()
                .toString(), "enc.properties")) {

            LOG.error("No enc.properties file found");
            return;
        }

        // Add external jars to runpath
        try {
            addSoftwareLibrary(new File(EPDShip.getInstance().getHomePath()
                    + "\\lib\\s52.jar"));
            addSoftwareLibrary(new File(EPDShip.getInstance().getHomePath()
                    + "\\lib\\s57csv.jar"));
            addSoftwareLibrary(new File(EPDShip.getInstance().getHomePath()
                    + "\\lib\\jts-1.8.jar"));
            addSoftwareLibrary(new File(EPDShip.getInstance().getHomePath()
                    + "\\lib\\dongle-1.10-SNAPSHOT.jar"));
            addSoftwareLibrary(new File(EPDShip.getInstance().getHomePath()
                    + "\\lib\\forms-1.2.1.jar"));
            addSoftwareLibrary(new File(EPDShip.getInstance().getHomePath()
                    + "\\lib\\binding-2.0.1.jar"));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        encProps.put("enc.certLocation", EPDShip.getInstance().getHomePath().toString()
                + "\\" + encProps.get("enc.certLocation"));

        try {
            addToLibraryPath(EPDShip.getInstance().getHomePath().toString()
                    + "\\navicon\\native");
        } catch (Exception e) {
            // TODO: handle exception
        }

        // Make layer instance
        String classProperty = "enc.class";
        String className = encProps.getProperty(classProperty);
        if (className == null) {
            LOG.error("Failed to locate property " + classProperty);
            return;
        }
        try {
            Object obj = java.beans.Beans.instantiate(null, className);
            OMGraphicHandlerLayer layer = (OMGraphicHandlerLayer) obj;
            layer.setProperties("enc", encProps);
            layer.setAddAsBackground(true);
            layer.setVisible(true);
            encLayer = layer;
        } catch (NullPointerException e) {
            LOG.error("Could not set up layer instance of class: \""
                    + className + "\"");
        } catch (ClassNotFoundException e) {
            LOG.error("Layer class not found: \"" + className + "\"");
        } catch (IOException e) {
            LOG.error("IO Exception instantiating class \"" + className + "\"");
        }

    }

    public OMGraphicHandlerLayer getEncLayer() {
        return encLayer;
    }

    /**
     * Try to set map settings for different ENC layer implementations.
     */
    public void setMapSettings() {
        if (encLayer == null) {
            return;
        }

        // Try to set Navicon settings
        if (setNaviconSettings()) {
            return;
        }

    }

    /**
     * Set Navicon settings if Navicon layer
     * 
     * @return if settings could be set
     */
    private boolean setNaviconSettings() {
        Properties marinerSettings = new Properties();

        // Determine if Navicon layer
        if (!encLayer.getClass().getName().contains("navicon")) {
            return false;
        }

        Class<?>[] argTypes = new Class<?>[0];
        Object[] arguments = new Object[0];
        try {
            

            
            
            
            // Get settings
            Method method = encLayer.getClass().getDeclaredMethod(
                    "getS52MarinerSettings", argTypes);
            Object obj = method.invoke(encLayer, arguments);
            marinerSettings = (Properties) obj;

            // Set settings from configuration
            marinerSettings.setProperty("MARINER_PARAM.S52_MAR_SHOW_TEXT",
                    Boolean.toString(mapSettings.isS52ShowText()));
            marinerSettings.setProperty(
                    "MARINER_PARAM.S52_MAR_SHALLOW_PATTERN",
                    Boolean.toString(mapSettings.isS52ShallowPattern()));
            marinerSettings.setProperty(
                    "MARINER_PARAM.S52_MAR_SHALLOW_CONTOUR",
                    Integer.toString(mapSettings.getS52ShallowContour()));
            marinerSettings.setProperty("MARINER_PARAM.S52_MAR_SAFETY_DEPTH",
                    Integer.toString(mapSettings.getS52SafetyDepth()));
            marinerSettings.setProperty("MARINER_PARAM.S52_MAR_SAFETY_CONTOUR",
                    Integer.toString(mapSettings.getS52SafetyContour()));
            marinerSettings.setProperty("MARINER_PARAM.S52_MAR_DEEP_CONTOUR",
                    Integer.toString(mapSettings.getS52DeepContour()));
            marinerSettings.setProperty("MARINER_PARAM.useSimplePointSymbols",
                    Boolean.toString(mapSettings.isUseSimplePointSymbols()));
            marinerSettings.setProperty("MARINER_PARAM.usePlainAreas",
                    Boolean.toString(mapSettings.isUsePlainAreas()));
            marinerSettings.setProperty("MARINER_PARAM.S52_MAR_TWO_SHADES",
                    Boolean.toString(mapSettings.isS52TwoShades()));
            marinerSettings.setProperty("MARINER_PARAM.color", mapSettings
                    .getColor().toUpperCase());

            // Set settings on layer
            argTypes = new Class<?>[1];
            argTypes[0] = Properties.class;
            arguments = new Object[1];
            arguments[0] = marinerSettings;
            method = encLayer.getClass().getDeclaredMethod(
                    "setS52MarinerSettings", argTypes);
            method.invoke(encLayer, arguments);


            
            
            // Set s57 settings
            Properties s57Props = new Properties();
            s57Props.setProperty("enc.viewGroupSettings", EPDShip.getInstance().getSettings()
                    .getS57Settings().getS52mapSettings());

            encLayer.setProperties(s57Props);
            
            

            return true;
        } catch (Exception e) {
            LOG.error("Failed to set mariner settings on Navicon ENC layer: "
                    + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    public void reapplySettings() {
        
        if (encLayer == null) {
            return;
        } else {

            
            try {
                Class<?>[] argTypes = new Class<?>[0];
                Object[] arguments = new Object[0];
                // Get settings
                Method method = encLayer.getClass().getDeclaredMethod(
                        "getS52MarinerSettings", argTypes);
                Object obj = method.invoke(encLayer, arguments);
                Properties marinerSettings = (Properties) obj;

                argTypes = new Class<?>[1];
                argTypes[0] = Properties.class;
                arguments = new Object[1];
                arguments[0] = marinerSettings;
                
               method = encLayer.getClass().getDeclaredMethod(
                        "setS52MarinerSettings", argTypes);
               
                    method.invoke(encLayer, arguments);
            
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            
            
//            Object obj = method.invoke(encLayer, arguments);
//            marinerSettings = (Properties) obj;
            
            
            
            
            
            
            
            
            
            
            
//            
//            Class<?> c;
//            try {
//                c = Class.forName("dk.navicon.s52.pure.presentation.S52Layer");
//                
//                
//                
////                
////                Class<?> s52layer = (c) encLayer;
//                
//                
//                Method m = c.getMethod("emptyCellCache");
//                m.invoke(null);
//
//                Method m2 = c.getMethod("doPrepare");
//                m2.invoke(null);
//
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
        }
    }

}
