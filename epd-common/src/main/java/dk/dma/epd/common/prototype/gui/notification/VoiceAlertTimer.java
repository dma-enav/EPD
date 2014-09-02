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

import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.notification.ChatNotification;
import dk.dma.epd.common.prototype.notification.GeneralNotification;
import dk.dma.epd.common.prototype.notification.Notification;
import dk.dma.epd.common.prototype.notification.RouteSuggestionNotificationCommon;
import dk.dma.epd.common.prototype.notification.StrategicRouteNotificationCommon;

public class VoiceAlertTimer {

    Timer warningTimer;
    ContinousVoiceAlerts voiceAlert;
    Notification<?, ?> notification;
    VoiceAlertTimers voiceAlertTimers;

    public VoiceAlertTimer(Notification<?, ?> notification, VoiceAlertTimers voiceAlertTimers) {
        this.notification = notification;
        this.voiceAlertTimers = voiceAlertTimers;
    }

    public void stopTimer() {
        warningTimer.cancel();
        voiceAlert.cancel();
    }

    public void startTimer() {
        warningTimer = new java.util.Timer();
        voiceAlert = new ContinousVoiceAlerts(notification, warningTimer, voiceAlertTimers);

        warningTimer.scheduleAtFixedRate(voiceAlert, 0, // initial delay
                10 * 1000);
    }
}

class ContinousVoiceAlerts extends TimerTask {
    private Notification<?, ?> notification;
    private java.util.Timer warningTimer;
    private Clip clip;
    private VoiceAlertTimers voiceAlertTimers;

    public ContinousVoiceAlerts(Notification<?, ?> notification, java.util.Timer warningTimer, VoiceAlertTimers voiceAlertTimers) {
        this.notification = notification;
        this.warningTimer = warningTimer;
        this.voiceAlertTimers = voiceAlertTimers;
    }

    public void run() {

        if (!notification.isRead()) {

            // toolkit.beep();

            URL audioClip = EPD.res().folder("audio/").getResource("warning.wav");

            if (notification instanceof ChatNotification) {

                ChatNotification chatNotification = (ChatNotification) notification;
                if (!chatNotification.get().isRead()) {
                    audioClip = EPD.res().folder("audio/").getResource("messagewarning.wav");
                } else {
                    killTimer();
                    return;
                }

            }
            if (notification instanceof GeneralNotification) {
                if (notification.getTitle().contains("CPA Warning")) {

                    // System.out.println("Active intended route index is " +
                    // EPD.getInstance().getRouteManager().getActiveRouteIndex());
                    if (EPD.getInstance().getRouteManager().getActiveRouteIndex() < 0) {
                        notification.setRead(true);
                        killTimer();
                        return;
                    } else {
                        audioClip = EPD.res().folder("audio/").getResource("tcpawarning.wav");
                    }

                }
            }

            if (notification instanceof RouteSuggestionNotificationCommon) {
                audioClip = EPD.res().folder("audio/").getResource("tacticalroute.wav");

            }

            if (notification instanceof StrategicRouteNotificationCommon) {

                audioClip = EPD.res().folder("audio/").getResource("strategicroute.wav");

            }

            try {
                clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(audioClip));
                clip.start();

                if (notification.getTitle().contains("Intended")) {
                    // Only play once
                    killTimer();
                }

            } catch (Exception exc) {
                exc.printStackTrace(System.out);
            }
        } else {
            killTimer();
        }

    }

    private void killTimer() {
        warningTimer.cancel();
        if (clip != null) {
            clip.drain();
            clip.stop();
            clip.flush();
            clip.close();
        }
        voiceAlertTimers.removeActiveVoiceAlert();
    }
}
