/* Copyright (c) 2011 Danish Maritime Authority.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
