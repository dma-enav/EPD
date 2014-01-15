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
package dk.dma.epd.common.prototype.sensor.rpnt;

/**
 * Interface to implement for classes wanting to receive PNT updates 
 * from the {@linkplain MultiSourcePntHandler}
 */
public interface IResilientPntDataListener {

    /**
     * Called upon receiving a new {@code ResilientPntData} update
     * @param rpntData the updated {@code ResilientPntData} data
     */
    void rpntDataUpdate(ResilientPntData rpntData);
}
