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
package dk.dma.epd.common.graphics;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class used to fetch resource URL's and image icons (more to come).
 * <p>
 * Examples:
 * <pre>
 *  Resources res = Resources.get(EPD.class, "images/vesselIcons");
 *  ImageIcon vesselWhite = res.getCachedImageIcon("white1_90.png");
 *  ImageIcon vesselBlue = res.getCachedImageIcon("blue1_90.png");
 *  ImageIcon img1 = Resources.get(EPDShip.class).getImageIcon("images/toolbar/zoom_mouse.png");
 *  ImageIcon img2 = res.from(EPDShip.class).folder("images/toolbar").getImageIcon("zoom_mouse.png");
 * </pre>
 */
public final class Resources {

    private static Map<CacheKey, Object> cache = new ConcurrentHashMap<>();
    Class<?> loaderClass;
    String folder;
    
    /**
     * Constructor
     * 
     * @param loaderClass class that defines the class-loader/jar-file to load from
     * @param folder the folder prefix
     */
    private Resources(Class<?> loaderClass, String folder) {
        
        from(loaderClass).folder(folder);
    }
    
      /***********************************/
     /********* Factory methods *********/
    /***********************************/
    
    /**
     * Returns a new instance of the {@code Resources} entity
     * <p>
     * The resources are loaded from the same class-path as the {@code Resources} class
     * 
     * @return a new {@code Resources} entity
     */
    public static Resources get() {
        return new Resources(null, null);
    }
    
    /**
     * Returns a new instance of the {@code Resources} entity with the given folder prefix.
     * <p>
     * The resources are loaded from the same class-path as the {@code Resources} class
     * 
     * @param folder the folder prefix
     * @return a new {@code Resources} entity
     */
    public static Resources get(String folder) {
        return new Resources(null, folder);
    }

    /**
     * Returns a new instance of the {@code Resources} entity with the given folder prefix.
     * <p>
     * The resources are loaded from the class-path of the {@code loaderClass} class
     * 
     * @param loaderClass class that defines the class-loader/jar-file to load from
     * @param folder the folder prefix
     * @return a new {@code Resources} entity
     */
    public static Resources get(Class<?> loaderClass, String folder) {
        return new Resources(loaderClass, folder);
    }

    /**
     * Returns a new instance of the {@code Resources} entity.
     * <p>
     * The resources are loaded from the class-path of the {@code loaderClass} class
     * 
     * @param loaderClass class that defines the class-loader/jar-file to load from
     * @return a new {@code Resources} entity
     */
    public static Resources get(Class<?> loaderClass) {
        return new Resources(loaderClass, null);
    }
    
    /**
     * Method chaining style of setting the loader-class
     * @param loaderClass the loader class
     */
    public Resources from(Class<?> loaderClass) {
        this.loaderClass = loaderClass;
        if (this.loaderClass == null) {
            this.loaderClass = Resources.class;
        }        
        return this;
    }

    /**
     * Method chaining style of setting the loader-class
     * @param loaderClass the loader class
     */
    public Resources folder(String folder) {
        this.folder = StringUtils.defaultString(folder);
        if (!StringUtils.isBlank(this.folder) && !this.folder.endsWith("/")) {
            this.folder = this.folder + "/";
        }
        return this;
    }

      /********************************************/
     /********* Resource loading methods *********/
    /********************************************/
  
    /**
     * Returns a full bath based on the current folder prefix and the
     * given {@code path} parameter
     * 
     * @param path the resource path relative to the current folder
     * @return the full path
     */
    private String getFullPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return folder + path;
    }
    
    /**
     * Returns a URL for the resource given by the {@code path} parameter.
     * 
     * @param path the path to the resource
     * @return the resource
     */
    public URL getResource(String path) {
        Objects.requireNonNull(path);
        return getClass().getClassLoader().getResource(getFullPath(path));
    }
    
    /**
     * Returns a new {@linkplain ImageIcon} for the image given by the {@code path} parameter.
     * 
     * @param path the path to the resource
     * @return the {@linkplain ImageIcon} with the given path
     */
    public ImageIcon getImageIcon(String path) {
        Objects.requireNonNull(path);
        URL url = getResource(path);
        return (url == null) ? null : new ImageIcon(url);
    }
    
    /**
     * Returns a cached {@linkplain ImageIcon} for the image given by the {@code path} parameter.
     * 
     * @param path the path to the resource
     * @return the {@linkplain ImageIcon} with the given path
     */
    public ImageIcon getCachedImageIcon(String path) {
        Objects.requireNonNull(path);
        CacheKey key = new CacheKey(loaderClass, getFullPath(path));
        ImageIcon imageIcon = (ImageIcon)cache.get(key);
        if (cache.containsKey(key)) {
            return imageIcon;
        }
        imageIcon = new ImageIcon(getResource(path));
        cache.put(key, imageIcon);
        return imageIcon;
    }
    
      /**********************************/
     /********* Helper classes *********/
    /**********************************/
  
    /**
     * Defines a cache key used for caching resources by loaderClass, 
     * and a full resource path
     */
    private static class CacheKey implements Serializable {
        private static final long serialVersionUID = 1L;
        private Class<?> loaderClass;
        private String fullPath;
        
        /**
         * Constructor
         * @param loaderClass
         * @param url
         */
        public CacheKey(Class<?> loaderClass, String fullPath) {
            Objects.requireNonNull(loaderClass);
            Objects.requireNonNull(fullPath);
            this.loaderClass = loaderClass;
            this.fullPath = fullPath;
        }

        @Override
        public int hashCode() {
            return Objects.hash(loaderClass, fullPath);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            return loaderClass.equals(other.loaderClass) && fullPath.equals(other.fullPath);
        }

    }
}
