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
import dk.dma.epd.common.prototype.settings.handlers.IntendedRouteHandlerCommonSettings;
import dk.dma.epd.shore.EPDShore;

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
        super(EPDShore.getInstance().getSettings().getEnavServicesHttpSettings(),
                EPDShore.getInstance().getSettings().getMetocHandlerSettings(),
                EPDShore.getInstance().getSettings().getMsiHandlerSettings(),
                EPDShore.getInstance().getSettings().getPrimaryMsiLayerSettings());
        this.intendedRouteFilterSettingsPanel = new IntendedRouteFilterSettingsPanel();
        this.add(this.intendedRouteFilterSettingsPanel);
    }

    @Override
    protected boolean checkSettingsChanged() {
        IntendedRouteHandlerCommonSettings<?> irhSettings = EPDShore.getInstance().getSettings().getIntendedRouteHandlerSettings();
        return super.checkSettingsChanged()
                || changed(irhSettings.getRouteTimeToLive(),
                        TimeUnit.MINUTES
                                .toMillis(this.intendedRouteFilterSettingsPanel
                                        .getTimeToLive()))
                || changed(irhSettings.getNotificationDistance(),
                        this.intendedRouteFilterSettingsPanel
                                .getNotificationDistance())
                || changed(irhSettings.getAlertDistance(),
                        this.intendedRouteFilterSettingsPanel
                                .getAlertDistance())
                || changed(irhSettings.getFilterDistance(),
                        this.intendedRouteFilterSettingsPanel
                                .getFilterDistance());
    }
    
    @Override
    protected void doLoadSettings() {
        super.doLoadSettings();
        // Load intended route filter settings.
        IntendedRouteHandlerCommonSettings<?> irhSettings = EPDShore.getInstance().getSettings().getIntendedRouteHandlerSettings();
        this.intendedRouteFilterSettingsPanel.setFilterDistance(irhSettings.getFilterDistance());
        this.intendedRouteFilterSettingsPanel.setTimeToLive(irhSettings.getRouteTimeToLive());
        this.intendedRouteFilterSettingsPanel.setAlertDistance(irhSettings.getAlertDistance());
        this.intendedRouteFilterSettingsPanel.setNotificationDistance(irhSettings.getNotificationDistance());
    }
    
    @Override
    protected void doSaveSettings() {
        super.doSaveSettings();
        // Save intended route filter settings.
        IntendedRouteHandlerCommonSettings<?> irhSettings = EPDShore.getInstance().getSettings().getIntendedRouteHandlerSettings();
        irhSettings.setFilterDistance(this.intendedRouteFilterSettingsPanel.getFilterDistance());
        irhSettings.setAlertDistance(this.intendedRouteFilterSettingsPanel.getAlertDistance());
        irhSettings.setNotificationDistance(this.intendedRouteFilterSettingsPanel.getNotificationDistance());
        irhSettings.setRouteTimeToLive(TimeUnit.MINUTES.toMillis(this.intendedRouteFilterSettingsPanel.getTimeToLive()));
    }
}
