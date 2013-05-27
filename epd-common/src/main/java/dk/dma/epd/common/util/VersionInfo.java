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
package dk.dma.epd.common.util;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tries to read version information from version.properties
 */
public final class VersionInfo {

    private static final Logger LOG = LoggerFactory.getLogger(VersionInfo.class);

    private static VersionInfo instance;

    private final Properties props = new Properties();

    private VersionInfo() {

        try {
            props.load(getClass().getResourceAsStream("/version.properties"));
        } catch (Exception e) {
            LOG.error("Failed to load version.properties", e);
        }
    }

    public static VersionInfo getInstance() {
        synchronized (VersionInfo.class) {
            if (instance == null) {
                instance = new VersionInfo();
            }
            return instance;
        }
    }

    public static String getVersion() {
        return getInstance().props.getProperty("version", "<unknown version>");
    }

}
