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
package dk.dma.epd.common.prototype.settings.gui;

import java.awt.Dimension;
import java.awt.Point;

import dk.dma.epd.common.prototype.settings.ObservedSettings;

/**
 * @author Janus Varmarken
 */
public class GUICommonSettings extends ObservedSettings<OBSERVER> {
    private boolean maximized;
    private Point appLocation = new Point(10, 10);
    private Dimension appDimensions = new Dimension(1280, 800);

    private boolean fullscreen;
}
