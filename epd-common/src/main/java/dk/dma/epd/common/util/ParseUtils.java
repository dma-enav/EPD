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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.bbn.openmap.geo.Geo;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.Heading;

/**
 * Utility class different parsing tasks
 */
public class ParseUtils {
    
    public static Double nmToMeters(Double nm) {
        if (nm == null) {
            return null;
        }
        return Converter.nmToMeters(nm);
    }
    
    public static Double metersToNm(Double meters) {
        if (meters == null) {
            return null;
        }
        return Converter.metersToNm(meters);
    }
    
    public static Float parseFloat(String str) throws FormatException {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            throw new FormatException("Could not parse " + str + " as a decimal number");
        }
    }
    
    public static Double parseDouble(String str) throws FormatException {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException e) {
            throw new FormatException("Could not parse " + str + " as a decimal number");
        }
    }
    
    public static Integer parseInt(String str) throws FormatException {
        if (str == null || str.length() == 0) {
            return null;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new FormatException("Could not parse " + str + " as an integer");
        }
    }
    
    public static String parseString(String str) {
        str = str.trim();
        if (str == null || str.length() == 0) {
            return null;
        }
        return str;
    }
    
    public static double parseLatitude(String formattedString) throws FormatException {
        String[] parts = splitFormattedPos(formattedString);
        return parseLatitude(parts[0], parts[1], parts[2]);
    }
    
    public static double parseLongitude(String formattedString) throws FormatException {
        String[] parts = splitFormattedPos(formattedString);
        return parseLongitude(parts[0], parts[1], parts[2]);
    }
    
    private static String[] splitFormattedPos(String posStr) throws FormatException {
        if (posStr.length() < 4) {
            throw new FormatException();
        }
        String[] parts = new String[3];
        parts[2] = posStr.substring(posStr.length() - 1);
        posStr = posStr.substring(0, posStr.length() - 1);
        String[] posParts = posStr.split(" ");
        if (posParts.length != 2) {
            throw new FormatException();
        }
        parts[0] = posParts[0];
        parts[1] = posParts[1];
        
        return parts;
    }
    
    public static double parseLatitude(String hours, String minutes, String northSouth) throws FormatException {
        Integer h = parseInt(hours);
        Double m = parseDouble(minutes);
        String ns = parseString(northSouth);        
        if (h == null || m == null || ns == null) {
            throw new FormatException();
        }
        if (!ns.equals("N") && !ns.equals("S")) {
            throw new FormatException();
        }
        double lat = h + m / 60.0; 
        if (ns.equals("S")) {
            lat *= -1;
        }
        return lat;        
    }
    
    public static double parseLongitude(String hours, String minutes, String eastWest) throws FormatException {
        Integer h = parseInt(hours);
        Double m = parseDouble(minutes);
        String ew = parseString(eastWest);        
        if (h == null || m == null || ew == null) {
            throw new FormatException();
        }
        if (!ew.equals("E") && !ew.equals("W")) {
            throw new FormatException();
        }
        
        System.out.println("H " + h);
        System.out.println("M " + m);
        
        double lon = h + m / 60.0; 
        if (ew.equals("W")) {
            lon *= -1;
        }
        return lon;
    }
    
    public static Heading parseSailHeadingType(String heading) throws FormatException {
        heading = parseString(heading);
        if (heading == null) {
            throw new FormatException("Missing sail field");
        }
        if (heading.equals("RL")) {
            return Heading.RL;
        }
        if (heading.equals("GC")) {
            return Heading.GC;
        }
        throw new FormatException("Unknown sail heading " + heading);
    }
    
    public static String getShortSailHeadingType(Heading st) {
        if (st == Heading.RL) {
            return  "RL";
        }
        return "GC";
    }

    public static TimeZone parseTimeZone(String tz) throws FormatException {
        tz = parseString(tz);
        if (tz == null) {
            return null;
        }
        String[] parts = tz.split(":");
        if (parts.length != 2) {
            throw new FormatException("Error in timezone");
        }
        Integer hours = parseInt(parts[0]);
        Integer mins = parseInt(parts[1]);
        if (hours == null || mins == null) {
            throw new FormatException("Error in timezone");
        }
        String sign = hours < 0 ? "-" : "+";
        hours = Math.abs(hours);        
        String customTzId = String.format("GMT%s%02d%02d", sign, hours, mins);
        return TimeZone.getTimeZone(customTzId);
    }
    
    public static Date parseIso8602(String str) throws FormatException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000"));
        try {
            return dateFormat.parse(str);
        } catch (ParseException e) {
            throw new FormatException(e.getMessage());
        }
    }
    
    public static Date parseVariuosDateTime(String dateStr) throws FormatException {
        // Preparation
        int i = dateStr.indexOf('(');
        if (i >=0) {
            dateStr = dateStr.substring(0, i);
        }
        
        SimpleDateFormat dateFormat;
        List<String> patterns = new ArrayList<>();
        patterns.add("MM/dd HH:mm:ss");
        patterns.add("MM/dd HH:mm");
        patterns.add("MM/dd/yyyy HH:mm:ss");
        patterns.add("MM/dd/yyyy HH:mm");
        patterns.add("dd-MM-yyyy HH:mm");
        patterns.add("dd-MM-yyyy HH:mm:ss");        
        Date res = null;
        for (String pattern : patterns) {
            dateFormat = new SimpleDateFormat(pattern);
            try {
                res = dateFormat.parse(dateStr);
                return res;
            } catch (ParseException e) {
                
            }
        }
        throw new FormatException("Cannot parse datetime");
    }
    
    public static Geo PositionToGeo(Position position){
        return new Geo(position.getLatitude(), position.getLongitude());
    }
    
    public static Position GeoToPosition(Geo geo){
        return Position.create(geo.getLatitude(), geo.getLongitude());
    }
    
    /**
     * Combines the date from the first {@code date} parameter with the 
     * time from the {@code time} parameter
     * 
     * @param date the date
     * @param time the time
     * @return the combined date
     */
    public static Date combineDateTime(Date date, Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);

        cal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, timeCal.get(Calendar.SECOND));
        return cal.getTime();
    }

}    
