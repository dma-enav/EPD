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
