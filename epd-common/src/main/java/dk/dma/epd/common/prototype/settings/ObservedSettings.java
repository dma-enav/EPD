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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * An abstract base class that can be used when writing classes that maintain a
 * set of application settings in a central place. It allows for clients to
 * register for notifications of changes to any setting maintained by this
 * class.
 * 
 * @param <OBSERVER>
 *            A callback interface used to deliver notifications of changes to
 *            settings maintained by the {@code ObservedSettings}.
 * 
 * @author Janus Varmarken
 */
public abstract class ObservedSettings<OBSERVER extends ISettingsObserver> {

    // TODO We may have to do lazy init of this list inside add/remove observer
    // methods if we want to serialize this class.
    /**
     * The list of observers that are to receive notifications when changes
     * occur to this {@code ObservedSettings} instance.
     */
    protected CopyOnWriteArrayList<OBSERVER> observers = new CopyOnWriteArrayList<>();

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
     * Loads settings from a file into memory. If the settings are successfully
     * loaded, {@link #onLoadSuccess(Properties)} is invoked with a
     * {@link Properties} instance containing the loaded settings. Similarly, if
     * an error occurs during load, {@link #onLoadFailure(IOException)} is
     * invoked with an {@link IOException} specifying what went wrong.
     * 
     * @param file
     *            The name of the file containing the settings to be loaded into
     *            memory.
     */
    public final void loadFromFile(String file) {
        // TODO consider if file should be full file path.
        Properties p = new Properties();
        try (FileReader reader = new FileReader(file)) {
            p.load(reader);
            this.onLoadSuccess(p);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            this.onLoadFailure(e);
        } catch (IOException e) {
            e.printStackTrace();
            this.onLoadFailure(e);
        }
    }

    /**
     * Invoked when settings have been successfully read from a file. This
     * allows subclasses to perform initialization of variables based on the
     * settings contained in the provided {@link Properties} instance,
     * {@code settings}.
     * 
     * @param settings
     *            Contains the settings that were read from the file.
     */
    protected abstract void onLoadSuccess(Properties settings);

    /**
     * Invoked if an error occurs while reading settings from a file. This
     * allows subclasses to respond to such an error.
     * 
     * @param error
     *            A {@link FileNotFoundException} if the settings file specified
     *            in {@link #loadFromFile(String)} was not found. An
     *            {@link IOException} if an error occurred while reading the
     *            settings file.
     */
    protected abstract void onLoadFailure(IOException error);
}
