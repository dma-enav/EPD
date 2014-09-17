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
package dk.dma.epd.shore.gui.settingtabs;

import java.util.concurrent.TimeUnit;

import dk.dma.epd.common.prototype.gui.settings.CommonENavSettingsPanel;
import dk.dma.epd.common.prototype.gui.settings.IntendedRouteFilterSettingsPanel;
import dk.dma.epd.common.prototype.settings.EnavSettings;

/**
 * This class is a hacky hotfix introduced in order to allow for dynamic changes
 * to intended route filter settings in EPD 3.0. It is not intended for future
 * use.
 * 
 * @author Janus Varmarken
 */
public class ShoreEnavSettingsPanel extends CommonENavSettingsPanel {

    /**
     * Default.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Provides a GUI for managing intended route filter settings.
     */
    private IntendedRouteFilterSettingsPanel intendedRouteFilterSettingsPanel;

    public ShoreEnavSettingsPanel() {
        super();
        this.intendedRouteFilterSettingsPanel = new IntendedRouteFilterSettingsPanel();
        this.add(this.intendedRouteFilterSettingsPanel);
    }

    @Override
    protected boolean checkSettingsChanged() {
        EnavSettings enavSettings = this.getSettings().getEnavSettings();
        return super.checkSettingsChanged()
                || changed(enavSettings.getRouteTimeToLive(),
                        TimeUnit.MINUTES.toMillis(this.intendedRouteFilterSettingsPanel.getTimeToLive()))
                || changed(enavSettings.getMarkerDistance(), this.intendedRouteFilterSettingsPanel.getMarkerDistance())
                || changed(enavSettings.getAlertDistance(), this.intendedRouteFilterSettingsPanel.getAlertDistance())
                || changed(enavSettings.getFilterDistance(), this.intendedRouteFilterSettingsPanel.getFilterDistance());
    }

    @Override
    protected void doLoadSettings() {
        super.doLoadSettings();
        // Load intended route filter settings.
        EnavSettings enavSettings = this.getSettings().getEnavSettings();
        this.intendedRouteFilterSettingsPanel.setMarkerDistance(enavSettings.getMarkerDistance());
        this.intendedRouteFilterSettingsPanel.setTimeToLive(enavSettings.getRouteTimeToLive());
        this.intendedRouteFilterSettingsPanel.setAlertDistance(enavSettings.getAlertDistance());
        this.intendedRouteFilterSettingsPanel.setFilterDistance(enavSettings.getFilterDistance());
    }
    
    @Override
    protected void doSaveSettings() {
        super.doSaveSettings();
        // Save intended route filter settings.
        EnavSettings enavSettings = this.getSettings().getEnavSettings();
        enavSettings.setFilterDistance(this.intendedRouteFilterSettingsPanel.getFilterDistance());
        enavSettings.setAlertDistance(this.intendedRouteFilterSettingsPanel.getAlertDistance());
        enavSettings.setMarkerDistance(this.intendedRouteFilterSettingsPanel.getMarkerDistance());
        enavSettings.setRouteTimeToLive(TimeUnit.MINUTES.toMillis(this.intendedRouteFilterSettingsPanel.getTimeToLive()));
    }
}
