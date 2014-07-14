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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Properties;

public class S57LayerSettings implements Serializable {

    private static final long serialVersionUID = 1L;
    private String s52mapSettings = "rO0ABXNyABNqYXZhLnV0aWwuQXJyYXlMaXN0eIHSHZnHYZ0DAAFJAARzaXpleHAAAAADdwQAAAAK\r\ndXIALltMZGsubmF2aWNvbi5zNTIucHJlc2VudGF0aW9uLlM1MlZpZXdpbmdHcm91cDvfPiU5sdDr\r\ndgIAAHhwAAAAFXNyACtkay5uYXZpY29uLnM1Mi5wcmVzZW50YXRpb24uUzUyVmlld2luZ0dyb3Vw\r\nSAufCAw34vECAAZJAAJpZEkAAnIxWgAGc3RhdHVzTAAFbGV2ZWx0ABNMamF2YS9sYW5nL0ludGVn\r\nZXI7TAAEbW9kZXQAMkxkay9uYXZpY29uL3M1Mi9wcmVzZW50YXRpb24vUzUyVmlld2luZ0dyb3Vw\r\nJE1PREU7TAAEbmFtZXQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwAAAAAgAAUhIBc3IAEWphdmEubGFu\r\nZy5JbnRlZ2VyEuKgpPeBhzgCAAFJAAV2YWx1ZXhyABBqYXZhLmxhbmcuTnVtYmVyhqyVHQuU4IsC\r\nAAB4cAADDUB+cgAwZGsubmF2aWNvbi5zNTIucHJlc2VudGF0aW9uLlM1MlZpZXdpbmdHcm91cCRN\r\nT0RFAAAAAAAAAAASAAB4cgAOamF2YS5sYW5nLkVudW0AAAAAAAAAABIAAHhwdAAEYXV0b3QADnVu\r\na25vd24gb2JqZWN0c3IAMGRrLm5hdmljb24uczUyLnByZXNlbnRhdGlvbi5TNTJWaWV3aW5nR3Jv\r\ndXBSYW5nZYaw+evLPs3AAgABSQACcjJ4cQB+AAQAAAADAABSHAFzcQB+AAkACSfAcQB+AA50ABNj\r\naGFydCBkYXRhIGNvdmVyYWdlAABSRHNxAH4AEQAAAAQAAFX6AXNxAH4ACQAPQkB+cQB+AAx0AAJv\r\nbnQADWxhbmQgZmVhdHVyZXMAAFb0c3EAfgAEAAAABQAAWeIBc3EAfgAJAAMNQHEAfgAXdAAmYXJl\r\nYSBvZiBkZXB0aCBsZXNzIHRoYW4gc2FmZXR5IGNvbnRvdXJzcQB+ABEAAAAGAABZ7AFzcQB+AAkA\r\nACcQcQB+AA50ABl3YXRlciBhbmQgc2VhYmVkIGZlYXR1cmVzAABdynNxAH4AEQAAAAcAAGGyAXNx\r\nAH4ACQAHoSBxAH4ADnQADnRyYWZmaWMgcm91dGVzAABh0HNxAH4AEQAAAAgAAGWaAXNxAH4ACQAD\r\nDUBxAH4ADnQAEGNhdXRpb25hcnkgYXJlYXMAAGXCc3EAfgARAAAACQAAZmIBc3EAfgAJAAAnEHEA\r\nfgAOdAARaW5mb3JtYXRpb24gYXJlYXMAAGaec3EAfgARAAAACgAAaYIBc3EAfgAJAAMNQHEAfgAO\r\ndAAPYnVveXMgJiBiZWFjb25zAABpqnNxAH4ABAAAAAsAAGm+AXNxAH4ACQADDUBxAH4ADnQABmxp\r\nZ2h0c3NxAH4ABAAAAAwAAGnIAXNxAH4ACQADDUBxAH4ADnQAC2ZvZyBzaWduYWxzc3EAfgARAAAA\r\nDgAAbWoBc3EAfgAJAAMNQHEAfgAOdAAdc2VydmljZXMgKHBpbG90LCBzaWduYWwgc3RucykAAG10\r\nc3EAfgARAAAAEwAAzxIBc3EAfgAJAAMNQHEAfgAOdAASbWFyaW5lcnMnIGZlYXR1cmVzAADPWHNx\r\nAH4AEQAAABYAANrAAXNxAH4ACQADDUBxAH4ADnQAJW1hcmluZXJzJyBhc3NpZ25tZW50cyB0byBz\r\ndGQuIGRpc3BsYXkAAN6nc3EAfgARAAAAFwAAeSIBc3EAfgAJAADDUH5xAH4ADHQAA29mZnQAHGlu\r\nZm9ybWF0aW9uIGFib3V0IGNoYXJ0IGRhdGEAAHlec3EAfgARAAAAGAAAfQoBc3EAfgAJAA9CQHEA\r\nfgAXdAANbGFuZCBmZWF0dXJlcwAAfsxzcQB+AAQAAAAZAACA8gFzcQB+AAkAAw1AcQB+AA50AAlz\r\nb3VuZGluZ3NzcgAyZGsubmF2aWNvbi5zNTIucHJlc2VudGF0aW9uLlM1MlZpZXdpbmdHcm91cFJh\r\nbmdlT3L85Hm/ctpeagIAAUkABW9yVmFseHEAfgARAAAAGgAAgPwBc3EAfgAJAAGGoHEAfgAOdAAj\r\nZGVwdGggY29udG91cnMsIGN1cnJlbnRzLCBtYWduZXRpY3MAAIEkAAB5aHNxAH4AEQAAABsAAITa\r\nAXNxAH4ACQADDUBxAH4ADnQAF3NlYWJlZCBhbmQgb2JzdHJ1Y3Rpb25zAACFFnNxAH4AEQAAABwA\r\nAJR6AXNxAH4ACQADDUBxAH4ADnQAI3NlcnZpY2VzIGFuZCBzbWFsbCBjcmFmdCBmYWNpbGl0aWVz\r\nAACVQnNxAH4AEQAAAB4AAPI6AXNxAH4ACQADDUBxAH4ADnQADnBvc2l0aW9uIGZpeGVzAADyRHVx\r\nAH4AAgAAAAxzcQB+AAQAAAAgAAAACgFzcQB+AAkAAw1AcQB+ABd0AA5pbXBvcnRhbnQgdGV4dHNx\r\nAH4ABAAAACEAAAALAXNxAH4ACQABhqBxAH4ADnQAHXZlcnRpY2FsIGNsZWFyYW5jZSBvZiBicmlk\r\nZ2Vzc3EAfgAEAAAAIgAAABQBc3EAfgAJAAMNQHEAfgAOdAAKb3RoZXIgdGV4dHNxAH4ABAAAACMA\r\nAAAVAXNxAH4ACQABhqBxAH4ADnQAHG5hbWVzIGZvciBwb3NpdGlvbiByZXBvcnRpbmdzcQB+AAQA\r\nAAAkAAAAFwFzcQB+AAkAAST4cQB+AA50ABhsaWdodCBkZXNjcmlwdGlvbiBzdHJpbmdzcQB+AAQA\r\nAAAlAAAAGAFzcQB+AAkAAw1AcQB+AA50ADxub3RlIG9uIGNoYXJ0IGRhdGEgKElORk9STSkgb3Ig\r\nbmF1dGljYWwgcHVibGljYXRpb24gKFRYVERTQylzcQB+AAQAAAAmAAAAGQFzcQB+AAkAAYagcQB+\r\nAA50ACNuYXR1cmUgb2Ygc2VhYmVkIChOQVRTVVIgb2YgU0JEQVJFKXNxAH4ABAAAACcAAAAaAXNx\r\nAH4ACQAJJ8BxAH4ADnQAMGdlb2dyYXBoaWMgbmFtZXMgKE9CSk5BTSBvZiBTRUFBUkUsIExORFJH\r\nTiBldGMuKXNxAH4ABAAAACgAAAAbAXNxAH4ACQADDUBxAH4APXQAT3ZhbHVlIG9mOiBtYWduZXRp\r\nYyB2YXJpYXRpb24gKFZBTE1BRyBvZiBNQUdWQVIpOyBzd2VwdCBkZXB0aCAoRFJWQUwxIG9mIFNX\r\nUEFSRSlzcQB+AAQAAAApAAAAHAFzcQB+AAkAAw1AcQB+ABd0ACBoZWlnaHQgIG9mIGlzbGV0IG9y\r\nIGxhbmQgZmVhdHVyZXNxAH4ABAAAACoAAAAdAXNxAH4ACQAAw1BxAH4ADnQAJ2JlcnRoIG51bWJl\r\nciAoT0JKTkFNIG9mIEJFUlRIUywgQUNIQlJUKXNxAH4ABAAAACsAAAAfAXNxAH4ACQADDUBxAH4A\r\nDnQAL25hdGlvbmFsIGxhbmd1YWdlIHRleHQgKE5PQkpOTSwgTklORk9NLCBOVFhURFMpdXEAfgAC\r\nAAAABnNxAH4AEQAAAGkAAAAAAXNxAH4ACQExLQBxAH4ADnQACE92ZXJ2aWV3AJiWgHNxAH4AEQAA\r\nAGgAAAAAAXNxAH4ACQAW42BxAH4ADnQAB0dlbmVyYWwAHoSAc3EAfgARAAAAZwAAAAABc3EAfgAJ\r\nAAST4HEAfgAOdAAHQ29hc3RhbAAMNQBzcQB+ABEAAABmAAAAAAFzcQB+AAkAATiAcQB+AA50AAhB\r\ncHByb2FjaAAGGoBzcQB+ABEAAABlAAAJxAFzcQB+AAkAAGGocQB+AA50AAdIYXJib3VyAAGGoHNx\r\nAH4AEQAAAGQAAAAAAXNxAH4ACQAAHUxxAH4ADnQACEJlcnRoaW5nAADDUHg\\=";

    public void saveSettings(String settings) {

        Properties props = new Properties();
        props.put("enc.viewGroupSettings", s52mapSettings);

      
        
        File propFile = new File(settings);

        try {
            FileOutputStream fis = new FileOutputStream(propFile);
            props.store(fis, "S57 Settings stored " + new Date());
            fis.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void readSettings(String settings) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(settings));
            
            s52mapSettings = props.getProperty("enc.viewGroupSettings",
                    s52mapSettings);
        } catch (FileNotFoundException e) {
            System.out.println("File not found exception " + e);
            return;
        } catch (IOException e) {
            System.out.println(e);
            return;
        }
    }

    /**
     * @return the s52mapSettings
     */
    public String getS52mapSettings() {
        return s52mapSettings;
    }

    /**
     * @param s52mapSettings
     *            the s52mapSettings to set
     */
    public void setS52mapSettings(String result) {
//        System.out.println(result.equals(s52mapSettings));
//        System.out.println("Settings changed");

        this.s52mapSettings = result;
    }

}
