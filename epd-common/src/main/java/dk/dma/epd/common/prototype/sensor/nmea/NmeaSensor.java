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
import java.util.HashSet;
import java.util.Set;

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
import dk.dma.ais.proprietary.IProprietarySourceTag;
import dk.dma.ais.proprietary.IProprietaryTag;
import dk.dma.ais.proprietary.ProprietaryFactory;
import dk.dma.ais.reader.AisPacketReader;
import dk.dma.ais.reader.SendException;
import dk.dma.ais.reader.SendRequest;
import dk.dma.ais.reader.SendThread;
import dk.dma.ais.reader.SendThreadPool;
import dk.dma.ais.sentence.Abk;
import dk.dma.ais.sentence.SentenceException;
import dk.dma.enav.util.function.Consumer;
import dk.dma.epd.common.prototype.sensor.gps.GnssTimeMessage;
import dk.dma.epd.common.prototype.sensor.gps.IGnssTimeListener;
import dk.dma.epd.common.util.Util;

/**
 * Abstract class for reading and handling NMEA messages
 */
public abstract class NmeaSensor extends MapHandlerChild implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(NmeaSensor.class);

    public enum Status {
        CONNECTED, DISCONNECTED
    };

    private boolean replay;
    
    private Date replayStartDate;
    private Date dataStart;
    private Date dataEnd;
    private Date replayStart;
    private Date replayEnd;
    private Date replayTime = new Date(0);
    
    protected SendThreadPool sendThreadPool = new SendThreadPool();
    private int replaySpeedup = 1;
    protected Set<SensorType> sensorTypes = new HashSet<>();
    private boolean simulateGps;
    private long simulatedOwnShip;
    private Set<IGpsListener> gpsListeners = new HashSet<>();
    private Set<IAisListener> aisListeners = new HashSet<>();
    private Set<IGnssTimeListener> gnssTimeListeners = new HashSet<>(); 
    private AisPacketReader packetReader = new AisPacketReader();
    
    
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
            if (replay) {
                handleReplay(msg);
            }
            
            handleSentence(msg);
        }        
        
    }
    
    /**
     * Method to send addressed or broadcast AIS messages (ABM or BBM). 
     * @param sendRequest
     * @param resultListener A class to handle the result when it is ready. 
     */
    public abstract void send(SendRequest sendRequest,  Consumer<Abk> resultListener) throws SendException;
    
    /**
     * The method to do the actual sending
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
            abk.parse(msg);
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
        IProprietaryTag tag = ProprietaryFactory.parseTag(msg);
        
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
        
        if (dataStart == null && getReplayStartDate() != null) {
            if (timestamp.before(getReplayStartDate())) {
                return;
            }
        }
        
        Date now = new Date();
            
        dataEnd = timestamp;
        if (dataStart == null) {
            dataStart = timestamp;
        }
        if (replayStart == null) {
            replayStart = now;
        }
        
        long elapsedData = timestamp.getTime() - dataStart.getTime();
        long elapsedReal = (now.getTime() - replayStart.getTime()) * replaySpeedup;
        long diff = elapsedData - elapsedReal;
        if (diff > 500) {
            Util.sleep(diff / replaySpeedup);            
        }
        
        replayEnd = now;
        
    }
    
    protected void handleProprietary(String msg) {
        if (msg.indexOf("$PSTT,10A") >= 0) {
            handlePstt(msg);
        }
        
    }
    
    protected void handleSentence(String msg) {
        if (!isSimulateGps() && gpsListeners.size() > 0 && msg.indexOf("$GPRMC") >= 0) {
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
                
        boolean ownMessage = false;
        // Check if simulated own ship
        if (isSimulateGps()) {
            ownMessage = message.getUserId() == simulatedOwnShip;
        } else {
            ownMessage = packet.getVdm().isOwnMessage();
        }
        
        // Distribute GPS from own mesasge
        if (ownMessage) {
            handleGpsFromOwnMessage(message);
        }
        
        // Distribute message
        synchronized (aisListeners) {
            for (IAisListener aisListener : aisListeners) {
                if (ownMessage) {
                    aisListener.receiveOwnMessage(message);
                } else {
                    aisListener.receive(message);
                }
            }
        }                
        
//        try {
//            int result = vdm.parse(msg);
//            if (result == 0) {
//                // Complete message
//                AisMessage aisMessage = AisMessage.getInstance(vdm);
//                if (aisMessage == null) {
//                    vdm = new Vdm();
//                    return;
//                }
//                
//                boolean ownMessage = false;
//                // Check if simulated own ship
//                if (isSimulateGps()) {
//                    ownMessage = (aisMessage.getUserId() == simulatedOwnShip);
//                } else {
//                    ownMessage = vdm.isOwnMessage();
//                }
//                
//                // Distribute GPS from own mesasge
//                if (ownMessage) {
//                    handleGpsFromOwnMessage(aisMessage);
//                }
//                
//                // Distribute message
//                synchronized (aisListeners) {
//                    for (IAisListener aisListener : aisListeners) {
//                        if (ownMessage) {                            
//                            aisListener.receiveOwnMessage(aisMessage);
//                        } else {
//                            aisListener.receive(aisMessage);
//                        }
//                    }
//                }                
//
//                vdm = new Vdm();
//                return;
//
//            } else {
//                // result = 1: Wait for more data
//                return;
//            }
//        } catch (AisMessageException e) {
//            LOG.error("AisMessageException: " + e.getMessage() + " msg: " + msg);
//        } catch (SentenceException e) {
//            LOG.error("SentenceException: " + e.getMessage() + " msg: " + msg);
//        } catch (SixbitException e) {
//            LOG.error("SixbitException: " + e.getMessage() + " msg: " + msg);
//        }

    }

    protected void handleGpsFromOwnMessage(AisMessage aisMessage) {
        GpsMessage gpsMessage = new GpsMessage();
        boolean foundPos = false;

        if (aisMessage instanceof AisPositionMessage) {
            AisPositionMessage posMessage = (AisPositionMessage) aisMessage;
            gpsMessage.setSog( posMessage.getSog() / 10.0d);
            gpsMessage.setCog( posMessage.getCog() / 10.0d);
            gpsMessage.setPos(posMessage.getPos().getGeoLocation());
            foundPos = true;
        } else if (aisMessage instanceof AisMessage18) {
            AisMessage18 msg18 = (AisMessage18) aisMessage;
            gpsMessage.setSog(msg18.getSog() / 10.0d);
            gpsMessage.setCog( msg18.getCog() / 10.0d);
            gpsMessage.setPos(msg18.getPos().getGeoLocation());
            foundPos = true;
        }

        if (!foundPos) {
            return;
        }
        
        gpsMessage.validateFields();

        if (replay) {
            GnssTimeMessage gnssTimeMessage = new GnssTimeMessage(getReplayTime());
            synchronized (gnssTimeListeners) {
                for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
                    gnssTimeListener.receive(gnssTimeMessage);
                }
            }
        }
        
        synchronized (gpsListeners) {
            for (IGpsListener gpsListener : gpsListeners) {
                gpsListener.receive(gpsMessage);
            }
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
//        synchronized (gpsListeners) {
//            for (IGpsListener gpsListener : gpsListeners) {
//                gpsListener.receive(sentence.getGpsMessage());
//            }
//        }
        synchronized (gnssTimeListeners) {
            for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
                gnssTimeListener.receive(sentence.getGnssTimeMessage());
            }
        }
    }
    
    private void handlePstt(String msg) {
        PsttSentence psttSentence = new PsttSentence();
        try {
            if (psttSentence.parse(msg)) {
                synchronized (gnssTimeListeners) {
                    for (IGnssTimeListener gnssTimeListener : gnssTimeListeners) {
                        gnssTimeListener.receive(psttSentence.getGnssTimeMessage());
                    }
                }
            }
        } catch (SentenceException e) {
            LOG.error("Failed to handle $PSTT,10A: " + e.getMessage());
        }
    }


    public void addGpsListener(IGpsListener gpsListener) {
        synchronized (gpsListeners) {
            gpsListeners.add(gpsListener);
        }
    }
    
    public void removeGpsListener(IGpsListener gpsListener) {
        synchronized (gpsListeners) {
            gpsListeners.remove(gpsListener);
        }
    }

    public void addAisListener(IAisListener aisListener) {
        synchronized (aisListeners) {
            aisListeners.add(aisListener);
        }
    }
    
    public void removeAisListener(IAisListener aisListener) {
        synchronized (aisListeners) {
            aisListeners.remove(aisListener);
        }
    }
    
    public void addGnssTimeListener(IGnssTimeListener gnssTimeListener) {
        synchronized (gnssTimeListeners) {
            gnssTimeListeners.add(gnssTimeListener);
        }
    }
    
    public void removeGnssTimeListener(IGnssTimeListener gnssTimeListener) {
        synchronized (gnssTimeListeners) {
            gnssTimeListeners.remove(gnssTimeListener);
        }
    }

    public boolean isSimulateGps() {
        return simulateGps;
    }

    public void setSimulateGps(boolean simulateGps) {
        this.simulateGps = simulateGps;
    }

    public long getSimulatedOwnShip() {
        return simulatedOwnShip;
    }

    public void setSimulatedOwnShip(long simulatedOwnShip) {
        this.simulatedOwnShip = simulatedOwnShip;
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
    
    public boolean isReplay() {
        return replay;
    }
    
    public void setReplay(boolean replay) {
        this.replay = replay;
    }
    
    public int getReplaySpeedup() {
        return replaySpeedup;
    }
    
    public void setReplaySpeedup(int replaySpeedup) {
        this.replaySpeedup = replaySpeedup;
    }
    
    public Date getReplayStart() {
        return replayStart;
    }

    public Date getReplayEnd() {
        return replayEnd;
    }
    
    public Date getDataStart() {
        return dataStart;
    }
    
    public Date getDataEnd() {
        return dataEnd;
    }
    
    public Date getReplayTime() {
        synchronized (replayTime) {
            return replayTime;
        }        
    }
    
    public void setReplayTime(Date replayTime) {
        synchronized (replayTime) {
            this.replayTime = replayTime;
        }
    }
    
    public Date getReplayStartDate() {
        return replayStartDate;
    }
    
    public void setReplayStartDate(Date replayStartDate) {
        this.replayStartDate = replayStartDate;
    }

}
