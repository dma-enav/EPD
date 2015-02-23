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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.PropertyConsumer;

public class PluginLoader {

    private static final String METAFILE_PREFIX = "META-INF/";

    private static final String COMPONENTS_PROPERTY = "epd.plugin_components";

    private static final String CLASSPATH_PROPERTY = "epd.plugin_classpath";

    private static final Logger LOG = LoggerFactory.getLogger(PluginLoader.class);

    private List<Object> plugins = new ArrayList<>();

    private final Properties epdProperties;
    private final Path homePath;
    private final String propertyFileName;

    public PluginLoader(Properties properties, Path homePath, String propertyFileName) {
        this.epdProperties = properties;
        this.homePath = homePath;
        this.propertyFileName = propertyFileName;
    }

    public Properties getEPDProperties() {
        return epdProperties;
    }

    public Path getHomePath() {
        return homePath;
    }
    
    public String getPropertyFileName() {
        return propertyFileName;
    }

    /**
     * Create the plugin components when possible.
     * 
     * Examines the 'epd.plugin_classpath' to find additional
     * property files, which together with the default
     * properties are being used to instantiate all 
     * 'epd_plugin_components'
     * Each plugin loaded are then passed to the pluginConsumer.
     * Plugins implementing {@link PropertyConsumer} will be given
     * the system properties.
     * Plugins implementing {@link Closeable} will be called during
     * termination (see {@link #closePlugins()}).
     * @param pluginConsumer
     */
    public void createPluginComponents(Consumer<Object> pluginConsumer) throws IOException{
        List<Properties> allMetaFiles = new ArrayList<>();
        allMetaFiles.add(getEPDProperties());
        ClassLoader loader = getPluginClassLoader();
        
        // Scan for additional property files.
        Enumeration<URL> resources = loader.getResources(METAFILE_PREFIX + getPropertyFileName());
        while (resources.hasMoreElements()) {
            Properties props = new Properties();
            props.load(resources.nextElement().openStream());
            allMetaFiles.add(props);
        }

        // Load plugin_components from all property files.
        for (Properties props : allMetaFiles) {
            loadPlugins(loader, props, pluginConsumer);
        }
    }

    private void loadPlugins(ClassLoader loader, Properties props, Consumer<Object> pluginConsumer) {
        String componentsValue = props.getProperty(COMPONENTS_PROPERTY);
        if (componentsValue == null) {
            return;
        }

        String[] componentNames = componentsValue.split(" ");
        for (String compName : componentNames) {
            String classProperty = compName + ".class";
            String className = props.getProperty(classProperty);
            if (className == null) {
                LOG.error("Failed to locate property " + classProperty);
                continue;
            }
            // Create it if you do...
            try {
                Object obj = java.beans.Beans.instantiate(loader, className);
                if (obj instanceof PropertyConsumer) {
                    PropertyConsumer propCons = (PropertyConsumer) obj;
                    propCons.setProperties(compName, props);
                }
                pluginConsumer.accept(obj);
                plugins.add(obj);
            } catch (IOException e) {
                LOG.error("IO Exception instantiating class \"" + className + "\"", e);
            } catch (ClassNotFoundException e) {
                LOG.error("Component class not found: \"" + className + "\"", e);
            }
        }
    }

    /**
     * Get the extended classloader to load extensions.
     * 
     * @return
     */
    private ClassLoader getPluginClassLoader() {
        Properties props = getEPDProperties();
        String pathsValue = props.getProperty(CLASSPATH_PROPERTY);
        if (pathsValue == null || pathsValue.isEmpty()) {
            return null;
        }
        String[] pathNames = pathsValue.split(File.pathSeparator);
        List<URL> paths = new ArrayList<>();
        for (String pathName : pathNames) {
            File pathElement = new File(pathName);

            // Resolve relative file inside homepath
            if (!pathElement.isAbsolute()) {
                pathElement = getHomePath().resolve(pathName).toFile();
            }

            // Ignore non-existing paths
            if (!pathElement.exists()) {
                continue;
            }

            try {
                paths.add(pathElement.toURI().toURL());
            } catch (MalformedURLException e) {
                LOG.debug("Malformed URL from " + pathName, e);
            }
        }

        return new URLClassLoader(paths.toArray(new URL[paths.size()]), PluginLoader.class.getClassLoader());
    }

    /**
     * Close all {@link Closeable} plugins. Exceptions will be logged as pluginDebug.
     */
    public void closePlugins() {
        for (Object pluginHandler : plugins) {
            if (pluginHandler instanceof Closeable) {
                try {
                    ((Closeable) pluginHandler).close();
                } catch (IOException e) {
                    LOG.debug("Error closing extension", e);
                }
            }
        }
    }

}
