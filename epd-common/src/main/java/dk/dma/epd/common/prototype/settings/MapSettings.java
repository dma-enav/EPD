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
package dk.dma.epd.common.prototype.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

import com.bbn.openmap.proj.coords.LatLonPoint;
import com.bbn.openmap.util.PropUtils;

/**
 * Map/chart settings
 */
public class MapSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String PREFIX = "map.";

    private LatLonPoint center = new LatLonPoint.Double(56, 11);
    private float scale = 10000000;
    private boolean useEnc = true;
    private boolean encVisible = true;
    private int maxScale = 5000;
    
    private boolean s52ShowText;
    private boolean s52ShallowPattern;
    private int s52ShallowContour = 6;
    private int s52SafetyDepth = 8;
    private int s52SafetyContour = 8;
    private int s52DeepContour = 10;
    private boolean useSimplePointSymbols = true;
    private boolean usePlainAreas;
    private boolean s52TwoShades;
    private String color = "Day";
    private String s52mapSettings = "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAADdwQAAAAK\r\ndXIALltMZGsubmF2aWNvbi5zNTIucHJlc2VudGF0aW9uLlM1MlZpZXdpbmdHcm91cDvfPiU5sdDr\r\ndgIAAHhwAAAAFXNyACtkay5uYXZpY29uLnM1Mi5wcmVzZW50YXRpb24uUzUyVmlld2luZ0dyb3Vw\r\nSAufCAw34vECAAZJAAJpZEkAAnIxWgAGc3RhdHVzTAAFbGV2ZWx0ABNMamF2YS9sYW5nL0ludGVn\r\nZXI7TAAEbW9kZXQAMkxkay9uYXZpY29uL3M1Mi9wcmVzZW50YXRpb24vUzUyVmlld2luZ0dyb3Vw\r\nJE1PREU7TAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwAAAAAgAAUhIBc3IAEWphdmEubGFu\r\nZy5JbnRlZ2VyEuKgpPeBhzgCAAFJAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsC\r\nAAB4cAADDUB+cgAwZGsubmF2aWNvbi5zNTIucHJlc2VudGF0aW9uLlM1MlZpZXdpbmdHcm91cCRN\r\nT0RFAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAADb2ZmdAAOdW5r\r\nbm93biBvYmplY3RzcgAwZGsubmF2aWNvbi5zNTIucHJlc2VudGF0aW9uLlM1MlZpZXdpbmdHcm91\r\ncFJhbmdlhrD568s+zcACAAFJAAJyMnhxAH4ABAAAAAMAAFIcAXNxAH4ACQAJJ8B+cQB+AAx0AARh\r\ndXRvdAATY2hhcnQgZGF0YSBjb3ZlcmFnZQAAUkRzcQB+ABEAAAAEAABV+gFzcQB+AAkAD0JAfnEA\r\nfgAMdAACb250AA1sYW5kIGZlYXR1cmVzAABW9HNxAH4ABAAAAAUAAFniAXNxAH4ACQADDUBxAH4A\r\nDnQAJmFyZWEgb2YgZGVwdGggbGVzcyB0aGFuIHNhZmV0eSBjb250b3Vyc3EAfgARAAAABgAAWewB\r\nc3EAfgAJAAAnEHEAfgAOdAAZd2F0ZXIgYW5kIHNlYWJlZCBmZWF0dXJlcwAAXcpzcQB+ABEAAAAH\r\nAABhsgFzcQB+AAkAB6EgcQB+ABR0AA50cmFmZmljIHJvdXRlcwAAYdBzcQB+ABEAAAAIAABlmgFz\r\ncQB+AAkAAw1AcQB+ABR0ABBjYXV0aW9uYXJ5IGFyZWFzAABlwnNxAH4AEQAAAAkAAGZiAXNxAH4A\r\nCQAAJxBxAH4AFHQAEWluZm9ybWF0aW9uIGFyZWFzAABmnnNxAH4AEQAAAAoAAGmCAXNxAH4ACQAD\r\nDUBxAH4AFHQAD2J1b3lzICYgYmVhY29ucwAAaapzcQB+AAQAAAALAABpvgFzcQB+AAkAAw1AcQB+\r\nABR0AAZsaWdodHNzcQB+AAQAAAAMAABpyAFzcQB+AAkAAw1AcQB+ABR0AAtmb2cgc2lnbmFsc3Nx\r\nAH4AEQAAAA4AAG1qAXNxAH4ACQADDUBxAH4AFHQAHXNlcnZpY2VzIChwaWxvdCwgc2lnbmFsIHN0\r\nbnMpAABtdHNxAH4AEQAAABMAAM8SAXNxAH4ACQADDUBxAH4AFHQAEm1hcmluZXJzJyBmZWF0dXJl\r\ncwAAz1hzcQB+ABEAAAAWAADawAFzcQB+AAkAAw1AcQB+ABR0ACVtYXJpbmVycycgYXNzaWdubWVu\r\ndHMgdG8gc3RkLiBkaXNwbGF5AADep3NxAH4AEQAAABcAAHkiAXNxAH4ACQAAw1BxAH4ADnQAHGlu\r\nZm9ybWF0aW9uIGFib3V0IGNoYXJ0IGRhdGEAAHlec3EAfgARAAAAGAAAfQoBc3EAfgAJAA9CQHEA\r\nfgAZdAANbGFuZCBmZWF0dXJlcwAAfsxzcQB+AAQAAAAZAACA8gFzcQB+AAkAAw1AcQB+AA50AAlz\r\nb3VuZGluZ3NzcgAyZGsubmF2aWNvbi5zNTIucHJlc2VudGF0aW9uLlM1MlZpZXdpbmdHcm91cFJh\r\nbmdlT3L85Hm/ctpeagIAAUkABW9yVmFseHEAfgARAAAAGgAAgPwBc3EAfgAJAAGGoHEAfgAOdAAj\r\nZGVwdGggY29udG91cnMsIGN1cnJlbnRzLCBtYWduZXRpY3MAAIEkAAB5aHNxAH4AEQAAABsAAITa\r\nAXNxAH4ACQADDUBxAH4ADnQAF3NlYWJlZCBhbmQgb2JzdHJ1Y3Rpb25zAACFFnNxAH4AEQAAABwA\r\nAJR6AXNxAH4ACQADDUBxAH4AFHQAI3NlcnZpY2VzIGFuZCBzbWFsbCBjcmFmdCBmYWNpbGl0aWVz\r\nAACVQnNxAH4AEQAAAB4AAPI6AXNxAH4ACQADDUBxAH4AFHQADnBvc2l0aW9uIGZpeGVzAADyRHVx\r\nAH4AAgAAAAxzcQB+AAQAAAAgAAAACgFzcQB+AAkAAw1AcQB+AA50AA5pbXBvcnRhbnQgdGV4dHNx\r\nAH4ABAAAACEAAAALAXNxAH4ACQABhqBxAH4ADnQAHXZlcnRpY2FsIGNsZWFyYW5jZSBvZiBicmlk\r\nZ2Vzc3EAfgAEAAAAIgAAABQBc3EAfgAJAAMNQHEAfgAOdAAKb3RoZXIgdGV4dHNxAH4ABAAAACMA\r\nAAAVAXNxAH4ACQABhqBxAH4ADnQAHG5hbWVzIGZvciBwb3NpdGlvbiByZXBvcnRpbmdzcQB+AAQA\r\nAAAkAAAAFwFzcQB+AAkAAST4cQB+AA50ABhsaWdodCBkZXNjcmlwdGlvbiBzdHJpbmdzcQB+AAQA\r\nAAAlAAAAGAFzcQB+AAkAAw1AcQB+AA50ADxub3RlIG9uIGNoYXJ0IGRhdGEgKElORk9STSkgb3Ig\r\nbmF1dGljYWwgcHVibGljYXRpb24gKFRYVERTQylzcQB+AAQAAAAmAAAAGQFzcQB+AAkAAYagcQB+\r\nAA50ACNuYXR1cmUgb2Ygc2VhYmVkIChOQVRTVVIgb2YgU0JEQVJFKXNxAH4ABAAAACcAAAAaAXNx\r\nAH4ACQAJJ8BxAH4ADnQAMGdlb2dyYXBoaWMgbmFtZXMgKE9CSk5BTSBvZiBTRUFBUkUsIExORFJH\r\nTiBldGMuKXNxAH4ABAAAACgAAAAbAXNxAH4ACQADDUBxAH4ADnQAT3ZhbHVlIG9mOiBtYWduZXRp\r\nYyB2YXJpYXRpb24gKFZBTE1BRyBvZiBNQUdWQVIpOyBzd2VwdCBkZXB0aCAoRFJWQUwxIG9mIFNX\r\nUEFSRSlzcQB+AAQAAAApAAAAHAFzcQB+AAkAAw1AcQB+AA50ACBoZWlnaHQgIG9mIGlzbGV0IG9y\r\nIGxhbmQgZmVhdHVyZXNxAH4ABAAAACoAAAAdAXNxAH4ACQAAw1BxAH4ADnQAJ2JlcnRoIG51bWJl\r\nciAoT0JKTkFNIG9mIEJFUlRIUywgQUNIQlJUKXNxAH4ABAAAACsAAAAfAXNxAH4ACQADDUBxAH4A\r\nDnQAL25hdGlvbmFsIGxhbmd1YWdlIHRleHQgKE5PQkpOTSwgTklORk9NLCBOVFhURFMpdXEAfgAC\r\nAAAABnNxAH4AEQAAAGkAAAAAAXNxAH4ACQExLQBxAH4AFHQACE92ZXJ2aWV3AJiWgHNxAH4AEQAA\r\nAGgAAAAAAXNxAH4ACQAW42BxAH4AFHQAB0dlbmVyYWwAHoSAc3EAfgARAAAAZwAAAAABc3EAfgAJ\r\nAAST4HEAfgAUdAAHQ29hc3RhbAAMNQBzcQB+ABEAAABmAAAAAAFzcQB+AAkAATiAcQB+ABR0AAhB\r\ncHByb2FjaAAGGoBzcQB+ABEAAABlAAAJxAFzcQB+AAkAAGGocQB+ABR0AAdIYXJib3VyAAGGoHNx\r\nAH4AEQAAAGQAAAAAAXNxAH4ACQAAHUxxAH4AFHQACEJlcnRoaW5nAADDUHg\\=";

    public MapSettings() {
    }

    public void readProperties(Properties props) {
        center.setLatitude(PropUtils.doubleFromProperties(props, PREFIX + "center_lat", center.getLatitude()));
        center.setLongitude(PropUtils.doubleFromProperties(props, PREFIX + "center_lon", center.getLongitude()));
        scale = PropUtils.floatFromProperties(props, PREFIX + "scale", scale);
        useEnc = PropUtils.booleanFromProperties(props, PREFIX + "useEnc", useEnc);
        encVisible = PropUtils.booleanFromProperties(props, PREFIX + "encVisible", encVisible);
        maxScale = PropUtils.intFromProperties(props, PREFIX + "maxScale", maxScale);
        
        // settings for S52 layer
        s52ShowText = PropUtils.booleanFromProperties(props, PREFIX + "s52ShowText", s52ShowText);
        s52ShallowPattern = PropUtils.booleanFromProperties(props, PREFIX + "s52ShallowPattern", s52ShallowPattern);
        s52ShallowContour = PropUtils.intFromProperties(props, PREFIX + "s52ShallowContour", s52ShallowContour);
        s52SafetyDepth = PropUtils.intFromProperties(props, PREFIX + "s52SafetyDepth", s52SafetyDepth);
        s52SafetyContour = PropUtils.intFromProperties(props, PREFIX + "s52SafetyContour", s52SafetyContour);
        s52DeepContour = PropUtils.intFromProperties(props, PREFIX + "s52DeepContour", s52DeepContour);
        useSimplePointSymbols = PropUtils.booleanFromProperties(props, PREFIX + "useSimplePointSymbols", useSimplePointSymbols);
        usePlainAreas = PropUtils.booleanFromProperties(props, PREFIX + "usePlainAreas", usePlainAreas);
        s52TwoShades = PropUtils.booleanFromProperties(props, PREFIX + "s52TwoShades", s52TwoShades);
        color = props.getProperty(PREFIX + "color", color);
        s52mapSettings = props.getProperty(PREFIX + "s52mapSettings", s52mapSettings);
            }

    public void setProperties(Properties props) {
        props.put(PREFIX + "center_lat", Double.toString(center.getLatitude()));
        props.put(PREFIX + "center_lon", Double.toString(center.getLongitude()));
        props.put(PREFIX + "scale", Double.toString(scale));
        props.put(PREFIX + "useEnc", Boolean.toString(useEnc));
        props.put(PREFIX + "encVisible", Boolean.toString(encVisible));
        props.put(PREFIX + "maxScale", Integer.toString(maxScale));
        
        // settings for S52 layer
        props.put(PREFIX + "s52ShowText", Boolean.toString(s52ShowText));
        props.put(PREFIX + "s52ShallowPattern", Boolean.toString(s52ShallowPattern));
        props.put(PREFIX + "s52ShallowContour", Integer.toString(s52ShallowContour));
        props.put(PREFIX + "s52SafetyDepth", Integer.toString(s52SafetyDepth));
        props.put(PREFIX + "s52SafetyContour", Integer.toString(s52SafetyContour));
        props.put(PREFIX + "s52DeepContour", Integer.toString(s52DeepContour));
        props.put(PREFIX + "useSimplePointSymbols", Boolean.toString(useSimplePointSymbols));
        props.put(PREFIX + "usePlainAreas", Boolean.toString(usePlainAreas));
        props.put(PREFIX + "s52TwoShades", Boolean.toString(s52TwoShades));
        props.put(PREFIX + "color", color);
//        props.put(PREFIX + "s52mapSettings", s52mapSettings);
        
    }
    
    


    public LatLonPoint getCenter() {
        return center;
    }

    public void setCenter(LatLonPoint center) {
        this.center = center;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public boolean isUseEnc() {
        return useEnc;
    }

    public void setUseEnc(boolean useEnc) {
        this.useEnc = useEnc;
    }
    
    public boolean isEncVisible() {
        return encVisible;
    }
    
    public void setEncVisible(boolean encVisible) {
        this.encVisible = encVisible;
    }
    
    public int getMaxScale() {
        return maxScale;
    }
    
    public void setMaxScale(int maxScale) {
        this.maxScale = maxScale;
    }

    public boolean isS52ShowText() {
        return s52ShowText;
    }

    public void setS52ShowText(boolean s52ShowText) {
        this.s52ShowText = s52ShowText;
    }

    public boolean isS52ShallowPattern() {
        return s52ShallowPattern;
    }

    public void setS52ShallowPattern(boolean s52ShallowPattern) {
        this.s52ShallowPattern = s52ShallowPattern;
    }

    public int getS52ShallowContour() {
        return s52ShallowContour;
    }

    public void setS52ShallowContour(int s52ShallowContour) {
        this.s52ShallowContour = s52ShallowContour;
    }

    public int getS52SafetyDepth() {
        return s52SafetyDepth;
    }

    public void setS52SafetyDepth(int s52SafetyDepth) {
        this.s52SafetyDepth = s52SafetyDepth;
    }

    public int getS52SafetyContour() {
        return s52SafetyContour;
    }

    public void setS52SafetyContour(int s52SafetyContour) {
        this.s52SafetyContour = s52SafetyContour;
    }

    public int getS52DeepContour() {
        return s52DeepContour;
    }

    public void setS52DeepContour(int s52DeepContour) {
        this.s52DeepContour = s52DeepContour;
    }

    public boolean isUseSimplePointSymbols() {
        return useSimplePointSymbols;
    }

    public void setUseSimplePointSymbols(boolean useSimplePointSymbols) {
        this.useSimplePointSymbols = useSimplePointSymbols;
    }

    public boolean isUsePlainAreas() {
        return usePlainAreas;
    }

    public void setUsePlainAreas(boolean usePlainAreas) {
        this.usePlainAreas = usePlainAreas;
    }

    public boolean isS52TwoShades() {
        return s52TwoShades;
    }

    public void setS52TwoShades(boolean s52TwoShades) {
        this.s52TwoShades = s52TwoShades;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the s52mapSettings
     */
    public String getS52mapSettings() {
        return s52mapSettings;
    }

    /**
     * @param s52mapSettings the s52mapSettings to set
     */
    public void setS52mapSettings(String s52mapSettings) {
        this.s52mapSettings = s52mapSettings;
    }
    
    
    
    
}
