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
package dk.dma.epd.common.prototype.gui.notification;

import java.util.Stack;

public class VoiceAlertTimers {

    private Stack<VoiceAlertTimer> inactiveVoiceAlerts = new Stack<VoiceAlertTimer>();
    private VoiceAlertTimer activeVoiceAlert;

    public VoiceAlertTimers() {

    }

    public void addVoiceAlert(VoiceAlertTimer alert) {

//        System.out.println("Adding new voice alert");

        if (activeVoiceAlert == null) {
            activeVoiceAlert = alert;
//            System.out.println("New timer added and started");
        } else {
            activeVoiceAlert.stopTimer();
            inactiveVoiceAlerts.push(activeVoiceAlert);
            activeVoiceAlert = alert;
//            System.out.println("New timer added as active");
        }

        activeVoiceAlert.startTimer();

    }

    public void removeActiveVoiceAlert() {
//        System.out.println("Removing active voice alert");
        activeVoiceAlert.stopTimer();
        if (!inactiveVoiceAlerts.isEmpty()) {
            activeVoiceAlert = inactiveVoiceAlerts.pop();
            activeVoiceAlert.startTimer();

        }

    }

}
