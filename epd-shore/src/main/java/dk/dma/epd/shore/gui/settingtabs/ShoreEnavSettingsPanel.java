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
                        TimeUnit.MINUTES
                                .toMillis(this.intendedRouteFilterSettingsPanel
                                        .getTimeToLive()))
                || changed(enavSettings.getNotificationDistance(),
                        this.intendedRouteFilterSettingsPanel
                                .getNotificationDistance())
                || changed(enavSettings.getAlertDistance(),
                        this.intendedRouteFilterSettingsPanel
                                .getAlertDistance())
                || changed(enavSettings.getFilterDistance(),
                        this.intendedRouteFilterSettingsPanel
                                .getFilterDistance());
    }
    
    @Override
    protected void doLoadSettings() {
        super.doLoadSettings();
        // Load intended route filter settings.
        EnavSettings enavSettings = this.getSettings().getEnavSettings();
        this.intendedRouteFilterSettingsPanel.setFilterDistance(enavSettings.getFilterDistance());
        this.intendedRouteFilterSettingsPanel.setTimeToLive(enavSettings.getRouteTimeToLive());
        this.intendedRouteFilterSettingsPanel.setAlertDistance(enavSettings.getAlertDistance());
        this.intendedRouteFilterSettingsPanel.setNotificationDistance(enavSettings.getNotificationDistance());
    }
    
    @Override
    protected void doSaveSettings() {
        super.doSaveSettings();
        // Save intended route filter settings.
        EnavSettings enavSettings = this.getSettings().getEnavSettings();
        enavSettings.setFilterDistance(this.intendedRouteFilterSettingsPanel.getFilterDistance());
        enavSettings.setAlertDistance(this.intendedRouteFilterSettingsPanel.getAlertDistance());
        enavSettings.setNotificationDistance(this.intendedRouteFilterSettingsPanel.getNotificationDistance());
        enavSettings.setRouteTimeToLive(TimeUnit.MINUTES.toMillis(this.intendedRouteFilterSettingsPanel.getTimeToLive()));
    }
}
