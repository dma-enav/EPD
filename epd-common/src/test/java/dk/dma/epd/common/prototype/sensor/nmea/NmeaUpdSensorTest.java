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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.google.common.base.Charsets;

public class NmeaUpdSensorTest {

    //@Test
    public void listenForUpd() throws IOException {
        final DatagramSocket serverSocket = new DatagramSocket(9091);

        try {
            while (true) {
                byte[] receiveData = new byte[256];
                final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                final String sentence = new String(receivePacket.getData(), Charsets.US_ASCII);
//                if (sentence.contains("RMC")) {
//                    System.out.println(sentence);
//                } else if (sentence.contains("PRPNT")) {
//                    System.out.println(sentence);                    
//                }
                System.out.println("Sentence: " + sentence);
            }
        } finally {
            serverSocket.close();
        }

    }

}
