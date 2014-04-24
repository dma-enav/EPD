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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

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

    /**
     * The list of observers that are to receive notifications when changes
     * occur to this {@code ObservedSettings} instance.
     */
    protected CopyOnWriteArrayList<OBSERVER> observers;

    /**
     * Provides a read lock and a write lock for accessing and modifying the
     * settings values maintained by this {@link ObservedSettings} instance. The
     * read lock allows for simultaneous read access from many threads while the
     * write lock is exclusive, i.e. only one thread can write at any given time
     * and no threads can read when one thread is writing.
     */
    protected ReentrantReadWriteLock settingLock;
    
    /**
     * Creates a new {@link ObservedSettings}.
     */
    public ObservedSettings() {
        this.settingLock = new ReentrantReadWriteLock(true);
        this.observers = new CopyOnWriteArrayList<>();
    }

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
     * Save this settings instance to a file. If an error occurs, this method calls {@link #onSaveFailure(IOException)} with the error. Subclasses may override {@link #onSaveFailure(IOException)} to perform recovery or logging.
     * 
     * @param file
     *            The file where the settings are to be stored.
     */
    public void saveToYamlFile(File file) {
        try {
            /*
             * Lock in order to take a snapshot of all individual settings at a
             * single point in time. Release the lock in finally block.
             */
            this.settingLock.readLock().lock();
            Yaml yaml = new Yaml();
            FileWriter writer = new FileWriter(file);
            yaml.dump(this, writer);
        } catch (IOException e) {
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
     *         to the data stored in the given file.
     * @throws FileNotFoundException
     *             If the given {@code file} cannot be found.
     */
    public static <T extends ObservedSettings<?>> T loadFromFile(
            Class<T> typeToLoad, File file) throws FileNotFoundException {
        InputStream is = new FileInputStream(file);
        Yaml yaml = new Yaml();
        return yaml.loadAs(is, typeToLoad);
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
}
