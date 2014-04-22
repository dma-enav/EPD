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
package dk.dma.epd.common.prototype.settings;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;

/**
 * <p>
 * An abstract base class that can be used when writing classes that maintain a
 * set of application settings in a central place. It allows for clients to
 * register for notifications of changes to any setting maintained by a concrete
 * subclass class.
 * </p>
 * <p>
 * Furthermore, this class provides a {@link ReentrantReadWriteLock} (see
 * instance field {@link #settingLock}) that subclasses can utilize when
 * synchronizing access to the settings values they maintain.
 * </p>
 * 
 * @param <OBSERVER>
 *            The type of the observers observing the {@code ObservedSettings}
 *            for changes.
 * 
 * @author Janus Varmarken
 */
public abstract class ObservedSettings<OBSERVER> {

    /**
     * Logger that subclasses may use, e.g. when receiving error callbacks such
     * as {@link #onSaveFailure(IOException)}.
     */
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    // TODO We may have to do lazy init of this list inside add/remove observer
    // methods if we want to serialize this class.
    /**
     * The list of observers that are to receive notifications when changes
     * occur to this {@code ObservedSettings} instance.
     */
    protected CopyOnWriteArrayList<OBSERVER> observers = new CopyOnWriteArrayList<>();

    /**
     * Provides a read lock and a write lock for accessing and modifying the
     * settings values maintained by this {@link ObservedSettings} instance. The
     * read lock allows for simultaneous read access from many threads while the
     * write lock is exclusive, i.e. only one thread can write at any given time
     * and no threads can read when one thread is writing.
     */
    protected ReentrantReadWriteLock settingLock = new ReentrantReadWriteLock(
            true);

    /**
     * Add a new observer that is to be notified when any setting is changed. An
     * observer can only be registered once.
     * 
     * @param obs
     *            The observer that is to be notified.
     * @return {@code true} if the observer was successfully added,
     *         {@code false} if the observer was not added (i.e. it was already
     *         registered).
     */
    public boolean addObserver(OBSERVER obs) {
        return this.observers.addIfAbsent(obs);
    }

    /**
     * Removes an observer such that it will no longer be notified when any
     * setting is changed.
     * 
     * @param obs
     *            The observer to remove.
     * @return {@code true} if the observer was successfully removed,
     *         {@code false} if the observer was not registered with this
     *         {@code ObservedSettings}.
     */
    public boolean removeObserver(OBSERVER obs) {
        return this.observers.remove(obs);
    }

    /**
     * Save this settings instance to a file.
     * 
     * @param file
     *            The file where the settings are to be stored.
     */
    public void saveToYamlFile(File file) {
        /*
         * Use a YamlConfig to tell the YamlWriter to also write default (and
         * unchanged) values.
         */
        YamlConfig yCfg = new YamlConfig();
        yCfg.writeConfig.setWriteDefaultValues(true);
        try {
            /*
             * Lock in order to take a snapshot of all individual settings at a
             * single point in time. Release the lock in finally block.
             */
            this.settingLock.readLock().lock();
            YamlWriter yWriter = new YamlWriter(new FileWriter(file), yCfg);
            // Write bean properties of this settings instance.
            yWriter.write(this);
            // Flush and close output stream.
            yWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO consider how write errors should be handled.
            this.onSaveFailure(e);
        } finally {
            this.settingLock.readLock().unlock();
        }
    }

    /**
     * Loads settings from a file.
     * 
     * @param typeToLoad
     *            Specifies the type of the settings instance serialized in the
     *            given file.
     * @param file
     *            The file from where the settings are loaded.
     * @return An instance of {@code typeToLoad} with properties set according
     *         to the data stored in the given file or null if the data in the
     *         given file cannot be parsed to an instance of {@code typeToLoad}.
     * @throws FileNotFoundException
     *             If the given {@code file} cannot be found.
     */
    public static <T extends ObservedSettings<?>> T loadFromFile(
            Class<T> typeToLoad, File file) throws FileNotFoundException {
        YamlReader reader = null;
        try {
            reader = new YamlReader(new FileReader(file));
            return reader.read(typeToLoad);
        } catch (YamlException e) {
            // Could not parse given file to given type.
            onLoadFailure(e);
            return null;
        } finally {
            // Free resources.
            try {
                if (reader != null) {
                    /*
                     * TODO this may need to be moved up to the main part as it
                     * might also flush the underlying stream.
                     */
                    reader.close();
                }
            } catch (IOException ioe) {
                // Too bad.
            }
        }
    }

    /**
     * Invoked if an error occurs while saving settings to a file.
     * 
     * @param error
     *            The exception that occurred while saving the settings.
     */
    protected void onSaveFailure(IOException error) {
        // TODO add logging or similar.
    }

    /**
     * Invoked if an error occurs while reading settings from a file.
     * 
     * @param error
     *            The exception that occurred while loading the settings.
     */
    private static void onLoadFailure(IOException error) {
        // TODO add logging or similar.
    }
}
