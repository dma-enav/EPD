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
package dk.dma.epd.common.prototype.msi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.Heading;
import dk.dma.epd.common.prototype.model.route.ActiveRoute;
import dk.dma.epd.common.prototype.model.route.Route;
import dk.dma.epd.common.prototype.sensor.gps.GnssTime;
import dk.dma.epd.common.prototype.settings.EnavSettings;
import dk.dma.epd.common.util.Calculator;
import dk.frv.enav.common.xml.msi.MsiMessage;
import dk.frv.enav.common.xml.msi.MsiPoint;

/**
 * Serializable class to store MSI information
 */
public class MsiStore implements Serializable {
    // TODO: If class changed, generate a new serial version ID
    private static final long serialVersionUID = -5653288769636767014L;
    private static final Logger LOG = LoggerFactory.getLogger(MsiStore.class);
    // private static final String msiFile = ".msi";
    private static String msiFile;

    private Map<Integer, MsiMessage> messages = new TreeMap<>();
    private int lastMessage;
    private Set<Integer> acknowledged = new HashSet<>();
    private Set<Integer> visibleGPS = new HashSet<>();
    private Set<Integer> visibleRoute = new HashSet<>();
    private Set<Integer> relevant = new HashSet<>();
    private Set<Integer> allVisible = new HashSet<>();

    private EnavSettings eNavSettings;

    public MsiStore(Path homePath, EnavSettings eNavSettings) {
        msiFile = homePath.resolve(".msi").toString();
        this.eNavSettings = eNavSettings;
    }

    public synchronized boolean hasValidUnacknowledged() {
        Date now = GnssTime.getInstance().getDate();
        for (Integer msgId : messages.keySet()) {
            MsiMessage msg = messages.get(msgId);
            if (msg.getValidFrom() != null && msg.getValidFrom().after(now)) {
                continue;
            }
            if (!acknowledged.contains(msgId)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean hasValidVisibleUnacknowledged() {
        Date now = GnssTime.getInstance().getDate();
        for (Integer msgId : messages.keySet()) {
            MsiMessage msg = messages.get(msgId);
            if (msg.getValidFrom() != null && msg.getValidFrom().after(now)) {
                continue;
            }

            if (!acknowledged.contains(msgId)
                    && (visibleGPS.contains(msgId) || visibleRoute
                            .contains(msgId))) {
                return true;
            }
        }
        return false;
    }

    public synchronized void update(List<MsiMessage> newMessages,
            Position calculationPosition, List<Route> routes) {
        for (MsiMessage newMessage : newMessages) {
            // Update lastMessage
            if (newMessage.getId() > lastMessage) {
                lastMessage = newMessage.getId();
            }
            // Remove acknowledge if existing message
            acknowledged.remove(newMessage.getMessageId());
            if (newMessage.getDeleted() != null) {
                // Remove message
                messages.remove(newMessage.getMessageId());
            } else {
                // Insert/update message
                messages.put(newMessage.getMessageId(), newMessage);
            }
        }
        visibleGPS.clear();
        if (calculationPosition != null) {
            setVisibility(calculationPosition);
        }
        if (routes != null) {
            setVisibility(routes);
        }
        saveToFile();
    }

    /**
     * Sets msi warnings visible if they are in the radius of the given location
     * (ship location)
     * 
     * @param calculationPosition
     *            Current location of own ship
     */
    public synchronized void setVisibility(Position calculationPosition) {
        visibleGPS.clear();
        Iterator<Map.Entry<Integer, MsiMessage>> it = messages.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, MsiMessage> entry = it.next();
            MsiMessage msiMessage = entry.getValue();

            // TODO Handle general area. For now they are show in list
            if (!msiMessage.hasLocation()) {
                visibleGPS.add(msiMessage.getMessageId());
                continue;
            }

            List<MsiPoint> msiPoints = msiMessage.getLocation().getPoints();
            Double distance = Double.MAX_VALUE;
            for (MsiPoint msiPoint : msiPoints) {
                Position msiLocation = Position.create(msiPoint.getLatitude(),
                        msiPoint.getLongitude());
                double currentDistance = Calculator.range(calculationPosition,
                        msiLocation, Heading.GC);
                distance = Math.min(currentDistance, distance);
            }
            if (distance <= eNavSettings.getMsiRelevanceFromOwnShipRange()) {
                visibleGPS.add(msiMessage.getMessageId());
            }
        }
        LOG.debug("Relevance calculation performed at:"
                + calculationPosition.getLatitude() + ", "
                + calculationPosition.getLongitude() + " yielded "
                + visibleGPS.size() + " visible warnings");
    }

    /**
     * Sets msi warnings visible if they are within a rectangle given by the
     * routes' waypoints.
     * 
     * @param routes
     *            List of routes for which to enable msi warnings at
     */
    public synchronized void setVisibility(List<Route> routes) {
        visibleRoute.clear();
        if (routes == null || routes.size() == 0) {
            return;
        }
        Iterator<Map.Entry<Integer, MsiMessage>> it = messages.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, MsiMessage> entry = it.next();
            MsiMessage msiMessage = entry.getValue();

            // TODO Handle general area. For now they are show in list
            if (!msiMessage.hasLocation()) {
                visibleRoute.add(msiMessage.getMessageId());
                continue;
            }

            boolean contained = false;
            List<MsiPoint> msiPoints = msiMessage.getLocation().getPoints();
            for (MsiPoint msiPoint : msiPoints) {
                for (Route route : routes) {
                    Position msiLocation = Position.create(
                            msiPoint.getLatitude(), msiPoint.getLongitude());
                    if (route.isVisible()
                            && route.isPointWithingBBox(msiLocation)) {
                        contained = true;
                    }
                }
            }
            if (contained) {
                visibleRoute.add(msiMessage.getMessageId());
            }
        }
        LOG.debug("Relevance calculation performed for routes yielded "
                + visibleRoute.size() + " visible warnings");
    }

    /**
     * Sets relevance for MSI warnings in proximity of an active route.
     * Currently implemented with a bounding box method, but later should be
     * implemented with calculation of cross track distance from route to point
     * 
     * @param route
     *            Active route
     */
    public synchronized void setRelevance(ActiveRoute route) {
        relevant.clear();
        Iterator<Map.Entry<Integer, MsiMessage>> it = messages.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, MsiMessage> entry = it.next();
            MsiMessage msiMessage = entry.getValue();

            // TODO Handle general area. For now they are show in list
            if (!msiMessage.hasLocation()) {
                relevant.add(msiMessage.getMessageId());
                continue;
            }

            boolean contained = false;
            List<MsiPoint> msiPoints = msiMessage.getLocation().getPoints();
            for (MsiPoint msiPoint : msiPoints) {
                Position msiLocation = Position.create(msiPoint.getLatitude(),
                        msiPoint.getLongitude());
                if (route.isPointWithingBBox(msiLocation)) {
                    contained = true;
                }
            }
            if (contained) {
                relevant.add(msiMessage.getMessageId());
            }
        }
    }

    public synchronized void clearRelevance() {
        relevant.clear();
    }

    public synchronized boolean cleanup() {
        List<Integer> doDelete = new ArrayList<>();
        Date now = GnssTime.getInstance().getDate();
        for (MsiMessage message : messages.values()) {
            // Check if validTo has been passed
            if (message.getValidTo() != null
                    && message.getValidTo().before(now)) {
                doDelete.add(message.getMessageId());
            }
        }
        for (Integer msgId : doDelete) {
            messages.remove(msgId);
            acknowledged.remove(msgId);
        }
        return doDelete.size() > 0;
    }

    public synchronized void deleteMessage(MsiMessage msiMessage) {
        acknowledged.remove(msiMessage.getMessageId());
        messages.remove(msiMessage.getMessageId());
    }

    public synchronized int getLastMessage() {
        return lastMessage;
    }

    public synchronized void saveToFile() {

        try (FileOutputStream fileOut = new FileOutputStream(msiFile);
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);) {
            objectOut.writeObject(this);
        } catch (IOException e) {
            LOG.error("Failed to save MSI file: " + e.getMessage());
        }
    }

    public static MsiStore loadFromFile(Path homePath, EnavSettings eNavSettings) {

        msiFile = homePath.resolve(".msi").toString();

        try (FileInputStream fileIn = new FileInputStream(msiFile);
                ObjectInputStream objectIn = new ObjectInputStream(fileIn);) {
            MsiStore msiStore = (MsiStore) objectIn.readObject();
            return msiStore;
        } catch (FileNotFoundException e) {
            // Not an error
        } catch (Exception e) {
            LOG.error("Failed to load MSI file: " + e.getMessage());
            // Delete possible corrupted or old file
            new File(msiFile).delete();
        }
        return new MsiStore(homePath, eNavSettings);
    }

    public synchronized Set<Integer> getAcknowledged() {
        return acknowledged;
    }

    public synchronized Set<Integer> getVisible() {
        allVisible.clear();
        allVisible.addAll(visibleGPS);
        allVisible.addAll(visibleRoute);
        return allVisible;
    }

    public synchronized Set<Integer> getRelevant() {
        return relevant;
    }

    public synchronized Map<Integer, MsiMessage> getMessages() {
        return messages;
    }

}
