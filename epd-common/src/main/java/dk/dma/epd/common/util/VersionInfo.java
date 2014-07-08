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
    
    public static String getBuildId() {
        return getInstance().props.getProperty("git.commit.id.describe", "N/A");        
    }
    
    public static String getBuildDate() {
        return getInstance().props.getProperty("build.date", "N/A");        
    }
    
    public static String getBuildUser() {
        return getInstance().props.getProperty("git.build.user.email", "N/A");        
    }
    
    public static String getVersionAndBuild() {
        return getVersion() + " (" + getBuildId() + ")";
    }

}
