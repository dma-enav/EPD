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
import java.util.Date;
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
import dk.dma.epd.common.prototype.sensor.rpnt.ResilientPntData;
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
    @GuardedBy("this")
    private boolean stopped;
    @GuardedBy("this")
    private boolean terminated;

    private final AisPacketParser packetReader = new AisPacketParser();

    protected final SendThreadPool sendThreadPool = new SendThreadPool();
    private final CopyOnWriteArrayList<IPntSensorListener> pntListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<IResilientPntSensorListener> msPntListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<IAisSensorListener> aisListeners = new CopyOnWriteArrayList<>();

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
        String line;

        while (!isStopped() && (line = reader.readLine()) != null) {
            handleLine(line);
        }
    }
    
    protected void handleLine(String line) {
        if (isReplay()) {
            handleReplay(line);
        }
        handleSentence(line);
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
        } else if (msg.indexOf("$PRPNT") >= 0) {
            handlePrpnt(msg);
        }
    }

    /**
     * Handles the given sentence
     * @param msg the sentence to handle
     */
    protected void handleSentence(String msg) {
        if (pntListeners.size() > 0 && RmcSentence.getParser(msg) != null) {
            handleRmc(msg);
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
        boolean ownMessage = packet.getVdm().isOwnMessage();

        // Distribute message
        for (IAisSensorListener aisListener : aisListeners) {
            if (ownMessage) {
                aisListener.receiveOwnMessage(message);
            } else {
                aisListener.receive(message);
            }
        }
        
        // Distribute PNT from own mesasge
        if (ownMessage) {
            handlePntFromOwnMessage(message);
        }

        
    }

    protected void handlePntFromOwnMessage(AisMessage aisMessage) {
        if (pntListeners.size() == 0) {
            return;
        }        
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

        if (!foundPos) {
            return;
        }

        Long time = (isReplay()) ? getReplayTime().getTime() : null;
        PntMessage pntMessage = new PntMessage(PntSource.AIS, pos, sog, cog, time);
        publishPntMessage(pntMessage);
    }

    /**
     * Handle NMEA RMC sentences such as $GPRMC (GPS), $ELRMC (eLoran) and $RDRMC (radar)
     * @param msg the message to handle
     */
    protected void handleRmc(String msg) {
        RmcSentence sentence = RmcSentence.getParser(msg);
        try {
            sentence.parse(msg);
        } catch (Exception e) {
            LOG.error("Failed to parse GPRMC sentence: " + msg + " : " + e.getMessage());
            return;
        }
        publishPntMessage(sentence.getPntMessage());
    }

    /**
     * Handle $PSTT sentences
     * @param msg the message to handle
     */
    private void handlePstt(String msg) {
        PsttSentence psttSentence = new PsttSentence();
        try {
            if (psttSentence.parse(msg)) {
                publishPntMessage(psttSentence.getPntMessage());
            }
        } catch (SentenceException e) {
            LOG.error("Failed to handle $PSTT,10A: " + e.getMessage());
        }
    }

    /**
     * The $PRPNT is a proprietary sentence introduced to 
     * flag which PNT source to use
     * 
     * @param msg the message to parse
     */
    private void handlePrpnt(String msg) {
        try {
            // Parse the $PRPNT message
            PrpntSentence sentence = new PrpntSentence();
            sentence.parse(msg);
            
            // Broadcast the parsed data to the listeners
            publishRpntData(sentence.getRpntData());
        } catch (SentenceException e) {
            LOG.error("Failed to handle $PRPNT '" + msg + "': " + e.getMessage());
        }
    }
    

    /**
     * Publishes the given PNT message to all listeners
     * @param pntMessage the message to publish
     */
    private void publishPntMessage(PntMessage pntMessage) {
        for (IPntSensorListener pntListener : pntListeners) {
            pntListener.receive(pntMessage);
        }
    }
    
    /**
     * Publishes the given PNT message to all PNT Time listeners
     * @param pntMessage the message to publish
     */
    private void publishRpntData(ResilientPntData rpntData) {
        for (IResilientPntSensorListener msPntListener : msPntListeners) {
            msPntListener.receive(rpntData);
        }
    }

    public void addPntListener(IPntSensorListener pntListener) {
        pntListeners.add(pntListener);
    }
    
    public void removePntListener(IPntSensorListener pntListener) {
        pntListeners.remove(pntListener);
    }

    public void addMsPntListener(IResilientPntSensorListener msPntListener) {
        msPntListeners.add(msPntListener);
    }

    public void removeMsPntListener(IResilientPntSensorListener msPntListener) {
        msPntListeners.remove(msPntListener);
    }

    public void addAisListener(IAisSensorListener aisListener) {
        aisListeners.add(aisListener);
    }

    public void removeAisListener(IAisSensorListener aisListener) {
        aisListeners.remove(aisListener);
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

    /**
     * Returns if {@linkplain #stop()} has been called to request that the sensor stops.<br>
     * The sensor will not have completed until {@linkplain #hasTerminated()} returns true
     * 
     * @return if {@linkplain #stop()} has been called to request that the sensor stops
     */
    public synchronized boolean isStopped() {
        return stopped;
    }
    
    /**
     * Call this method to stop the sensor.<br>
     * The sensor will not have completed until {@linkplain #hasTerminated()} returns true
     */
    public synchronized void stop() {
        this.stopped = true;
    }

    /**
     * Returns if the sensor has terminated
     * 
     * @return if the sensor has terminated
     */
    public synchronized boolean hasTerminated() {
        return terminated;
    }
    
    /**
     * Used internally to flag that the sensor has terminated
     */
    protected void flagTerminated() {
        this.terminated = true;
    }
}
