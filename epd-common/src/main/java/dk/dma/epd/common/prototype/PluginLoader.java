package dk.dma.epd.common.prototype;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.PropertyConsumer;

public class PluginLoader {
	
	private static final Logger LOG = LoggerFactory.getLogger(PluginLoader.class);

	
    private List<Object> plugins = new ArrayList<>();
    
    private final Properties properties;
    private final Path homePath;
    
    

    public PluginLoader(Properties properties, Path homePath) {
		this.properties = properties;
		this.homePath = homePath;
	}
    
    public Properties getProperties() {
		return properties;
	}
    
    public Path getHomePath() {
		return homePath;
	}

	/**
     * Create the plugin components when possible.
     */
    public void createPluginComponents(Consumer<Object> pluginConsumer) {
    	Properties props = getProperties();
        String componentsValue = props.getProperty("epd.plugin_components");
        if (componentsValue == null) {
            return;
        }
        ClassLoader loader = getClassLoader();
        
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
    private ClassLoader getClassLoader() {
        Properties props = getProperties();
        String pathsValue = props.getProperty("epd.plugin_classpath");
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
     * Close all {@link Closeable} plugins.
     * Exceptions will be logged as pluginDebug.
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
