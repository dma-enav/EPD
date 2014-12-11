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
package dk.dma.epd.common.prototype.enavcloud;

import net.maritimecloud.core.id.MaritimeId;
import net.maritimecloud.core.id.MmsiId;

/**
 * In the process of migrating to maritime cloud 0.2, which has completely changed it's API's,
 * various old MC class have been stubbed out with sub-classes from this class.
 */
public class TODO {

    public static class ServiceInitiationPoint<T> {
        public ServiceInitiationPoint(Class<T> clz) {
        }
    }

    public static class ServiceMessage<U> {
    }

    public static class ServiceInvocationFuture<T> {
    }

    public static class ServiceEndpoint<M, R> {
        public MaritimeId getId() { return new MmsiId(0); }
    }

    public static class BroadcastMessageHeader {
        public MaritimeId getId() { return new MmsiId(0); }
    }

    public static class BroadcastMessage {
    }

    public static class BroadcastListener<T>{
        public void onMessage(BroadcastMessageHeader header, T msg){

        }
    }

    public static class BroadcastOptions {
        public void setBroadcastRadius(int r) {
        }
    }
}
