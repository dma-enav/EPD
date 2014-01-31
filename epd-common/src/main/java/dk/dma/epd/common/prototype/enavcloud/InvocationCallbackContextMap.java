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
package dk.dma.epd.common.prototype.enavcloud;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.maritimecloud.net.service.invocation.InvocationCallback;

/**
 * Pseudo-map used for storing Maritime Cloud {@linkplain InvocationCallback.Context} contexts
 * for a certain amount of time.
 * <p>
 * The key of the map must be a unique identified for the context
 */
public class InvocationCallbackContextMap<K, V extends InvocationCallback.Context<?>> {

    private final long ttl;
    private Map<K, TimedValue<V>> map = new ConcurrentHashMap<>();
    
    /**
     * Constructor
     * @param ttl time to live in seconds
     */
    public InvocationCallbackContextMap(long ttl) {
        super();
        this.ttl = ttl;
    }
    
    /**
     * Puts the {@code value} into the map keyed by {@code key}
     * @param key the key
     * @param value the value
     */
    public synchronized void put(K key, V value) {
        map.put(key, new TimedValue<>(value));
    }

    /**
     * Returns the value associated with {@code key}
     * @param key the key
     * @return the value or null if not present
     */
    public synchronized V get(K key) {
        if (map.get(key) != null) {
            return map.get(key).getValue();
        }
        return null;
    }

    /**
     * Returns if the map contains {@code key}
     * @param key the key
     * @return if the map contains {@code key}
     */
    public synchronized boolean containsKey(K key) {
        return map.get(key) != null;
    }
    
    /**
     * Removes and returns the value associated with {@code key}
     * @param key the key of the value to remove
     * @return the removed value or null if not present
     */
    public synchronized V remove(K key) {
        if (map.get(key) != null) {
            return map.remove(key).getValue();
        }
        return null;
    }
    
    /**
     * Cleans up old values that have timed out according to 
     * the TTL constructor parameter
     */
    public synchronized void cleanup() {
        long now = System.currentTimeMillis();
        
        for (Iterator<Map.Entry<K, TimedValue<V>>> it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<K, TimedValue<V>> entry = it.next();
            if(now > entry.getValue().getTime() + ttl * 1000L) {
                it.remove();
            }
        }
    }
}

/**
 * Helper class that adds a time stamp to the value
 */
class TimedValue<V> {
    V value;
    long time;
    
    public TimedValue(V value) {
        this.value = value;
        this.time = System.currentTimeMillis();
    }

    public V getValue() {
        return value;
    }

    public long getTime() {
        return time;
    }
}
