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
package dk.dma.epd.common.prototype.model.voct;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.FormatException;
import dk.dma.epd.common.util.ParseUtils;

public class SARFileParser {

    
    Properties prop;

    Position A;
    Position B;
    Position C;
    Position D;
    Position datum;
    int searchObject;
    String sarNumber = "";

    public SARFileParser(String path) throws Exception {

        // READ DATA
        // A
        // B
        // C
        // D
        // DATUM
        readProperties(path);

    }


    /**
     * @return the prop
     */
    public Properties getProp() {
        return prop;
    }



    /**
     * @return the a
     */
    public Position getA() {
        return A;
    }



    /**
     * @return the b
     */
    public Position getB() {
        return B;
    }



    /**
     * @return the c
     */
    public Position getC() {
        return C;
    }



    /**
     * @return the d
     */
    public Position getD() {
        return D;
    }



    /**
     * @return the datum
     */
    public Position getDatum() {
        return datum;
    }



    /**
     * @return the searchObject
     */
    public int getSearchObject() {
        return searchObject;
    }



    /**
     * @return the sarNumber
     */
    public String getSarNumber() {
        return sarNumber;
    }



    private void readProperties(String path) throws Exception {
        prop = new Properties();

        
            InputStream inputStream = new FileInputStream(path);

            prop.load(inputStream);
            setLoadedValues();


    }

    private void setLoadedValues() throws Exception {

        String datum = prop.getProperty("datum");
        this.datum = getPosition(datum);
        String a = prop.getProperty("A");
        this.A = getPosition(a);

        String b = prop.getProperty("B");
        this.B = getPosition(b);

        String c = prop.getProperty("C");
        this.C = getPosition(c);

        String d = prop.getProperty("D");
        this.D = getPosition(d);

        this.searchObject = Integer.parseInt(prop.getProperty("searchobject"));
        this.sarNumber = prop.getProperty("sarnumber");

    }

    private Position getPosition(String inputString) throws FormatException {
//        System.out.println("Input is " + inputString);
        String latitude = inputString.split("\\|")[0].trim();
        String longitude = inputString.split("\\|")[1].trim();


        return Position.create(ParseUtils.parseLatitude(latitude),
                ParseUtils.parseLongitude(longitude));
        // String test = "55 00 1 N";
        // test = "56 40.672N";
        // System.out.println(ParseUtils.parseLatitude(inputString));
        // double latitude = ParseUtils.parseLatitude(
        // inputString.split("\\|")[0] );
        // double longitude = ParseUtils.parseLatitude(
        // inputString.split("\\|")[1] );
        // System.out.println("Latitude is " + latitude);
        // System.out.println("Longtude is " + longitude);
    }

    public static void main(String[] args) {

        // SAXParserExample parser = new SAXParserExample();
        // parser.runExample();

        // XPathParser parser = new
        // XPathParser("000084-13052014/000084-13052014/Sarex Fn.xml");
        try {
            @SuppressWarnings("unused")
            SARFileParser parser = new SARFileParser("C://scenario1.sar");
            // SARISXMLParser parser = new SARISXMLParser("E://Sarex Fn.xml");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
