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
package dk.dma.epd.shore.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("hiding")
public class RouteSuggestionDataStructure<RouteSuggestionKey, RouteSuggestionData> extends HashMap<RouteSuggestionKey, RouteSuggestionData>{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean containsKey( Object key){

        @SuppressWarnings("unchecked")
        RouteSuggestionKey suggestionKey = (RouteSuggestionKey) key;

         Iterator<RouteSuggestionKey> iterator = this.keySet().iterator();
            while (iterator.hasNext()) {
                RouteSuggestionKey currentKey = iterator.next();
              if (currentKey.equals(suggestionKey)){
                  return true;
              }
            }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RouteSuggestionData get(Object key){


        RouteSuggestionKey suggestionKey = (RouteSuggestionKey) key;

        for (java.util.Map.Entry<RouteSuggestionKey, RouteSuggestionData> entry2 : this.entrySet()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = entry2;
            RouteSuggestionKey currentKey = (RouteSuggestionKey) entry.getKey();
            RouteSuggestionData value = (RouteSuggestionData) entry.getValue();
            if (currentKey.equals(suggestionKey)){
                return value;
            }
          }
        return null;
    }


    @SuppressWarnings("unchecked")
    @Override
    public RouteSuggestionData remove(Object key){
        RouteSuggestionKey suggestionKey = (RouteSuggestionKey) key;

        for (java.util.Map.Entry<RouteSuggestionKey, RouteSuggestionData> entry2 : this.entrySet()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = entry2;
            RouteSuggestionKey currentKey = (RouteSuggestionKey) entry.getKey();
            RouteSuggestionData value = (RouteSuggestionData) entry.getValue();
            if (currentKey.equals(suggestionKey)){
                super.remove(currentKey);
                return value;
            }
          }
        return null;
    }
}
