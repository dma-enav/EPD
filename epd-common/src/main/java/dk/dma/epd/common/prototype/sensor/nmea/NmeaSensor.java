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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapHandlerChild;

import dk.dma.ais.binary.SixbitException;
import dk.dma.ais.message.AisMessage;
import dk.dma.ais.message.AisMessage18;
import dk.dma.ais.message.AisMessageException;
import dk.dma.ais.message.AisPositionMessage;
import dk.dma.ais.packet.AisPacket;
import dk.dma.ais.packet.AisPacketParser;
import dk.dma.ais.proprietary.IProprietarySourceTag;
import dk.dma.ais.proprietary.IProprietaryTag;
import dk.dma.ais.proprietary.ProprietaryFactory;
import dk.dma.ais.reader.SendException;
import dk.dma.ais.reader.SendRequest;
import dk.dma.ais.reader.SendThread;
import dk.dma.ais.reader.SendThreadPool;
import dk.dma.ais.sentence.Abk;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.ais.sentence.SentenceLine;
import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.util.function.Consumer;
import dk.dma.epd.common.prototype.sensor.gps.GnssTimeMessage;
import dk.dma.epd.common.prototype.sensor.gps.IGnssTimeListener;
import dk.dma.epd.common.util.Util;

/**
 * Abstract class for reading and handling NMEA messages
 */
@ThreadSafe
public abstract class NmeaSensor extends MapHandlerChild implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NmeaSensor.class);

    public enum Status {
        CONNECTED, DISCONNECTED
    };

    @GuardedBy("this")
    private boolean replay;
    @GuardedBy("this")
    private Date replayStartDate;
    @GuardedBy("this")
    private Date dataStart;
    @GuardedBy("this")
    private Date dataEnd;
    @GuardedBy("this")
    private Date replayStart;
    @GuardedBy("this")
    private Date replayEnd;
    @GuardedBy("this")
    private Date replayTime = new Date(0);
    @GuardedBy("this")
    private int replaySpeedup = 1;

    private final AisPacketParser packetReader = new AisPacketParser();

    protected final SendThreadPool sendThreadPool = new SendThreadPool();
    protected final Set<SensorType> sensorTypes = Collections.newSetFromMap(new ConcurrentHashMap<SensorType, Boolean>());
    private final CopyOnWriteArrayList<IGpsListener> gpsListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<IAisListener> aisListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<IGnssTimeListener> gnssTimeListeners = new CopyOnWriteArrayList<>();

    public NmeaSensor() {

    }

    /**
     * Main method to read NMEA messages from stream
     * 
     * @param stream
     * @throws IOException
     */
    protected void readLoop(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String msg;

        while ((msg = reader.readLine()) != null) {
            if (isReplay()) {
                handleReplay(msg);
            }

            handleSentence(msg);
        }

    }

    /**
     * Method to send addressed or broadcast AIS messages (ABM or BBM).
     * 
     * @param sendRequest
     * @param resultListener
     *            A class to handle the result when it is ready.
     */
    public abstract void send(SendRequest sendRequest, Consumer<Abk> resultListener) throws SendException;

    /**
     * The method to do the actual sending
     * 
     * @param sendRequest
     * @param resultListener
     * @param out
     * @throws SendException
     */
    protected void doSend(SendRequest sendRequest, Consumer<Abk> resultListener, OutputStream out) throws SendException {
        if (out == null) {
            throw new SendException("Not connected");
        }

        // Get sentences
        String[] sentences = sendRequest.createSentences();

        // Create and start thread
        SendThread sendThread = sendThreadPool.createSendThread(sendRequest, resultListener);

        // Write to out
        String str = StringUtils.join(sentences, "\r\n") + "\r\n";
        LOG.debug("Sending:\n" + str);
        try {
            out.write(str.getBytes());
        } catch (IOException e) {
            throw new SendException("Could not send AIS message: " + e.getMessage());
        }

        // Start send thread
        sendThread.start();
    }

    protected void handleAbk(String msg) {
        Abk abk = new Abk();
        try {
            abk.parse(new SentenceLine(msg));
            sendThreadPool.handleAbk(abk);
        } catch (Exception e) {
            LOG.error("Failed to parse ABK: " + msg + ": " + e.getMessage());
        }
    }

    protected void handleReplay(String msg) {
        // Check if proprietary sentence
        if (!ProprietaryFactory.isProprietaryTag(msg)) {
            return;
        }
        IProprietaryTag tag = ProprietaryFactory.parseTag(new SentenceLine(msg));

        if (!(tag instanceof IProprietarySourceTag)) {
            return;
        }
        IProprietarySourceTag sourceTag = (IProprietarySourceTag) tag;
        if (sourceTag == null || sourceTag.getTimestamp() == null) {
            return;
        }

        Date timestamp = sourceTag.getTimestamp();
        // TODO if timestamp before some starttime, then just return

        // Set replay time to current timestamp
        setReplayTime(timestamp);

        if (getDataStart() == null && getReplayStartDate() != null) {
            if (timestamp.before(getReplayStartDate())) {
                return;
            }
        }

        Date now = new Date();

        setDataEnd(timestamp);

        if (getDataStart() == null) {
            setDataStart(timestamp);
        }
        if (getReplayStart() == null) {
            setReplayStart(now);
        }

        long elapsedData = timestamp.getTime() - getDataStart().getTime();
        long elapsedReal = (now.getTime() - getReplayStart().getTime()) * getReplaySpeedup();
        long diff = elapsedData - elapsedReal;
        if (diff > 500) {
            Util.sleep(diff / getReplaySpeedup());
        }

        setReplayEnd(now);

    }

    protected void handleProprietary(String msg) {
        if (msg.indexOf("$PSTT,10A") >= 0) {
            handlePstt(msg);
        }

    }

    protected void handleSentence(String msg) {
        if (gpsListeners.size() > 0 && msg.indexOf("$GPRMC") >= 0) {
            handleGpRmc(msg);
        } else if (aisListeners.size() > 0 && isVdm(msg)) {
            handleAis(msg);
        } else if (Abk.isAbk(msg)) {
            handleAbk(msg);
        } else if (msg.indexOf("$P") >= 0) {
            handleProprietary(msg);
        }
    }

    protected boolean isVdm(String msg) {
        return msg.indexOf("!AIVDM") >= 0 || msg.indexOf("!AIVDO") >= 0 || msg.indexOf("!BSVDM") >= 0;
    }

    protected void handleAis(String msg) {
        AisPacket packet;
        try {
            packet = packetReader.readLine(msg);
        } catch (SentenceException se) {
            LOG.info("Sentence error: " + se.getMessage() + " msg: " + msg);
            return;
        }

        // No complete packet yet
        if (packet == null) {
            return;
        }

        // Get AIS message
        AisMessage message = null;
        // Parse AIS message
        try {
            message = AisMessage.getInstance(packet.getVdm());
        } catch (AisMessageException me) {
            LOG.info("AIS message exception: " + me.getMessage() + " vdm: " + packet.getVdm().getOrgLinesJoined());
        } catch (SixbitException se) {
            LOG.info("Sixbit error: " + se.getMessage() + " vdm: " + packet.getVdm().getOrgLinesJoined());
        }
        if (message == null) {
            return;
        }

        // Check if simulated own ship
        boolean ownMessage = packet.getVdm().isOwnMessage();;
        
        // Distribute GPS from own mesasge
        if (ownMessage) {
            handleGpsFromOwnMessage(message);
        }

        // Distribute message
        for (IAisListener aisListener : aisListeners) {
            if (ownMessage) {
                aisListener.receiveOwnMessage(message);
            } else {
                aisListener.receive(message);
            }
        }
    }

    protected void handleGpsFromOwnMessage(AisMessage aisMessage) {
        boolean foundPos = false;

        Position pos = null;
        Double sog = null;
        Double cog = null;
        if (aisMessage instanceof AisPositionMessage) {
            AisPositionMessage posMessage = (AisPositionMessage) aisMessage;
            sog = posMessage.getSog() / 10.0d;
            cog = posMessage.getCog() / 10.0d;
            pos = posMessage.getPos().getGeoLocation();
            foundPos = true;
        } else if (aisMessage instanceof AisMessage18) {
            AisMessage18 msg18 = (AisMessage18) aisMessage;
            sog = msg18.getSog() / 10.0d;
            cog = msg18.getCog() / 10.0d;
            pos = msg18.getPos().getGeoLocation();
            foundPos = true;
        }
        
        GpsMessage gpsMessage = new GpsMessage(pos, sog, cog);

        if (!foundPos) {
            return;
        }


        if (isReplay()) {
            GnssTimeMessage gnssTimeMessage = new GnssTimeMessage(getReplayTime());
            for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
                gnssTimeListener.receive(gnssTimeMessage);
            }
        }

        for (IGpsListener gpsListener : gpsListeners) {
            gpsListener.receive(gpsMessage);
        }
    }

    protected void handleGpRmc(String msg) {
        GpRmcSentence sentence = new GpRmcSentence();
        try {
            sentence.parse(msg);
        } catch (Exception e) {
            LOG.error("Failed to parse GPRMC sentence: " + msg + " : " + e.getMessage());
            return;
        }
        // Only AIS own messages will be used for positioning
        // for (IGpsListener gpsListener : gpsListeners) {
        // gpsListener.receive(sentence.getGpsMessage());
        // }
        for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
            gnssTimeListener.receive(sentence.getGnssTimeMessage());
        }
    }

    private void handlePstt(String msg) {
        PsttSentence psttSentence = new PsttSentence();
        try {
            if (psttSentence.parse(msg)) {
                for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
                    gnssTimeListener.receive(psttSentence.getGnssTimeMessage());
                }
            }
        } catch (SentenceException e) {
            LOG.error("Failed to handle $PSTT,10A: " + e.getMessage());
        }
    }

    public void addGpsListener(IGpsListener gpsListener) {
        gpsListeners.add(gpsListener);
    }

    public void removeGpsListener(IGpsListener gpsListener) {
        gpsListeners.remove(gpsListener);
    }

    public void addAisListener(IAisListener aisListener) {
        aisListeners.add(aisListener);
    }

    public void removeAisListener(IAisListener aisListener) {
        aisListeners.remove(aisListener);
    }

    public void addGnssTimeListener(IGnssTimeListener gnssTimeListener) {
        gnssTimeListeners.add(gnssTimeListener);
    }

    public void removeGnssTimeListener(IGnssTimeListener gnssTimeListener) {
        gnssTimeListeners.remove(gnssTimeListener);
    }

    public void addSensorType(SensorType type) {
        sensorTypes.add(type);
    }

    public boolean isSensorType(SensorType type) {
        return sensorTypes.contains(type);
    }

    public void start() {
        new Thread(this).start();
    }

    private synchronized boolean isReplay() {
        return replay;
    }

    protected synchronized void setReplay(boolean replay) {
        this.replay = replay;
    }

    protected synchronized int getReplaySpeedup() {
        return replaySpeedup;
    }

    protected synchronized void setReplaySpeedup(int replaySpeedup) {
        this.replaySpeedup = replaySpeedup;
    }

    protected synchronized Date getReplayStart() {
        return replayStart;
    }

    private synchronized void setReplayStart(Date replayStart) {
        this.replayStart = replayStart;
    }

    protected synchronized Date getReplayEnd() {
        return replayEnd;
    }

    private synchronized void setReplayEnd(Date replayEnd) {
        this.replayEnd = replayEnd;
    }

    protected synchronized Date getDataStart() {
        return dataStart;
    }

    private synchronized void setDataStart(Date dataStart) {
        this.dataStart = dataStart;
    }

    protected synchronized Date getDataEnd() {
        return dataEnd;
    }

    private synchronized void setDataEnd(Date dataEnd) {
        this.dataEnd = dataEnd;
    }

    private synchronized Date getReplayTime() {
        return replayTime;
    }

    private synchronized void setReplayTime(Date replayTime) {
        this.replayTime = replayTime;
    }

    private synchronized Date getReplayStartDate() {
        return replayStartDate;
    }

    protected synchronized void setReplayStartDate(Date replayStartDate) {
        this.replayStartDate = replayStartDate;
    }

}
