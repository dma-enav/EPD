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
package dk.dma.epd.common.text;

import org.apache.commons.lang.StringUtils;

/**
 * Class with different common text utilities
 */
public class TextUtils {

    /**
     * Return class name without package
     * @param cls
     * @return
     */
    public static String className(Class<?> cls) {
        String[] nameParts = StringUtils.split(cls.getName(), '.');
        return nameParts[nameParts.length - 1];
    }

    /**
     * Returns true if string is not null and non-empty
     * @param str
     * @return
     */
    public static boolean exists(String str) {
        return str != null && str.length() > 0;
    }
    
}
