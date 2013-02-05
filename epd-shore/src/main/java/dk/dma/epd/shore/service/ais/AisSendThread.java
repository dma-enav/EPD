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
package dk.dma.epd.shore.service.ais;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.ais.reader.SendException;
import dk.dma.ais.reader.SendRequest;
import dk.dma.ais.sentence.Abk;
import dk.dma.enav.util.function.Consumer;

/**
 * Thread for sending AIS messages
 */
public class AisSendThread extends Thread implements Consumer<Abk>  {

    private static final Logger LOG = LoggerFactory.getLogger(AisSendThread.class);

    protected SendRequest sendRequest;
    protected AisServices aisServices;
    protected Abk abk;
    protected Boolean abkReceived = false;

    public AisSendThread(SendRequest sendRequest, AisServices aisServices) {
        this.sendRequest = sendRequest;
        this.aisServices = aisServices;
    }

    @Override
    public void run() {
        // Send message
        try {
            aisServices.getNmeaSensor().send(sendRequest, this);
        } catch (SendException e) {
            LOG.error("Failed to send AIS message: " + sendRequest + ": " + e.getMessage());
            aisServices.sendResult(false);
            return;
        }

        // Busy wait
        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (abkReceived) {
                if (abkReceived) {
                    break;
                }
            }
        }

        if (abk != null && abk.isSuccess()) {
            LOG.info("AIS SEND SUCCESS");
            aisServices.sendResult(true);
        } else {
            LOG.info("AIS SEND ERROR");
            aisServices.sendResult(false);
        }

        LOG.debug("abk: " + abk);


    }

    @Override
    public void accept(Abk abk) {
        synchronized (abkReceived) {
            this.abk = abk;
            this.abkReceived = true;
        }
    }

}
