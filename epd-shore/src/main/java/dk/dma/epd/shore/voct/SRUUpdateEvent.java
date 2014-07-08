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
package dk.dma.epd.shore.voct;

import java.util.Arrays;
import java.util.EnumSet;

/**
 * Different events for SRUs
 */
public enum SRUUpdateEvent {
    SRU_ADDED, SRU_REMOVED, SRU_VISIBILITY_CHANGED, SRU_CHANGED, SRU_STATUS_CHANGED, BROADCAST_MESSAGE, SRU_ACCEPT, SRU_REJECT;
    
    public boolean is(SRUUpdateEvent... events) {
        return EnumSet.copyOf(Arrays.asList(events)).contains(this);
    }
};
