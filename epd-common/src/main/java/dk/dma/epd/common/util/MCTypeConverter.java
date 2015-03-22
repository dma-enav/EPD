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

import java.util.Date;

import net.maritimecloud.util.Timestamp;

import org.joda.time.DateTime;

import dk.dma.enav.model.geometry.Position;

public class MCTypeConverter {

    public static net.maritimecloud.util.geometry.Position getMaritimeCloudPositin(
            Position position) {
        return net.maritimecloud.util.geometry.Position.create(
                position.getLatitude(), position.getLongitude());
    }

    public static Timestamp getMaritimeCloudTimeStamp(Date date) {
        return net.maritimecloud.util.Timestamp.create(date.getTime());
    }

    public static Timestamp getMaritimeCloudTimeStamp(DateTime date) {
        return net.maritimecloud.util.Timestamp.create(date.getMillis());
    }
}
