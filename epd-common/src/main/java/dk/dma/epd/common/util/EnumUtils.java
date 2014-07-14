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

/**
 * Often, you want to associate enumerations with a key different from
 * then enum value itself.
 * <p>
 * This class provides support for these enumerations by defining an
 * interface, {@code KeyedEnum}, which the enumeration must implement and a
 * utility method, {@code findByKey}, for looking up the enumeraion value
 * associated with a specific key.
 * <p>
 * Example usage:
 * <pre>
 * enum PntSource implements EnumUtils.KeyedEnum<String> {
 *       DO_NOT_USE("0"),
 *       GPS("1"),
 *       ELORAN("2"),
 *       RADAR("3");
 *    private String key;
 *    private PntSource(String key) { this.key = key; }
 *    &#064;Override public String getKey() { return key; }
 * }
 * 
 * System.out.println(EnumUtils.findByKey(PntSource.class, "0"));
 * </pre>
 */
public class EnumUtils {
    
    /**
     * Returns the enumeration value associated with the given key
     * 
     * @param key the key to find a enumeration value for
     * @return the associated enumeration value or {@code null} if none is found
     */
    public static <T, E extends Enum<E> & KeyedEnum<T>> E findByKey(Class<E> enumClass, T key) {
        for (E v : enumClass.getEnumConstants()) {
            if (v.getKey().equals(key)) {
                return v;
            }
        }
        return null;
    }

    /**
     * Can be implemented by enumerations where the values
     * are associated with a key
     *  
     * @param <T> the type of the key
     */
    public interface KeyedEnum<T> {
        T getKey();
    }
}
