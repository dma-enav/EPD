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
package dk.dma.epd.common.prototype.ais;

import java.util.Date;

import net.jcip.annotations.Immutable;

import dk.dma.ais.message.binary.RouteInformation;

/**
 * Class representing a broadcast route suggestion
 */
@Immutable
public class AisBroadcastRouteSuggestion extends AisRouteData {
    private static final long serialVersionUID = 1L;

    private final Date validFrom;
    private final Date validTo;

    /**
     * Copy constructor
     * 
     * @param broadcastRouteSuggestion
     */
    public AisBroadcastRouteSuggestion(AisBroadcastRouteSuggestion broadcastRouteSuggestion) {
        super(broadcastRouteSuggestion);
        validFrom = null;
        validTo = null;
    }

    /**
     * Constructor given AIS route information
     * 
     * @param routeInformation
     */
    public AisBroadcastRouteSuggestion(RouteInformation routeInformation) {
        super(routeInformation);
        validFrom = getEtaFirst();
        validTo = getEtaLast();
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

}
