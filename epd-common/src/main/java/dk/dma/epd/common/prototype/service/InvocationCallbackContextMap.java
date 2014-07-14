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
package dk.dma.epd.common.prototype.service;

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
