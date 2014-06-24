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
package dk.dma.epd.common.prototype.model.voct;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dk.dma.enav.model.geometry.Position;
import dk.dma.enav.model.voct.SARAreaData;
import dk.dma.enav.model.voct.SARISTarget;
import dk.dma.epd.common.prototype.model.voct.sardata.DatumPointDataSARIS;
import dk.dma.epd.common.prototype.model.voct.sardata.SARWeatherData;

public class SARISXMLParser {
    private Document document;
    private XPath xPath;
    private DatumPointDataSARIS sarData;

    public SARISXMLParser(String path) throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;

        try {
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            document = builder.parse(new FileInputStream(path));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        xPath = XPathFactory.newInstance().newXPath();

        DateTime LKPDate;

        String lkpExpression = "/saris-report/search-plan-information/search-plan-locations/dsp-information[@location='1']/time[@name='Drift Start']/*";

        LKPDate = getDateFromNodeList(lkpExpression);

        if (LKPDate == null) {
            throw new Exception("fail");
        }

        String DSPExpression = "/saris-report/search-plan-information/search-plan-locations/dsp-information[@location='1']/position[@name='D.S.P.']/angle/*";
        Position DSP = getPositionFromNodeList(DSPExpression);

         System.out.println(DSP);
        DateTime CSP;

        String CSPExpression = "/saris-report/search-plan-information/time[@name='Datum']/*";
        CSP = getDateFromNodeList(CSPExpression);

        String weatherDataExpression = "/saris-report/search-plan-information/wind-input/";
        List<SARWeatherData> weatherData = getSARWeatherData(weatherDataExpression);

        // No current info?

        // Leeway info about targets
        // SARISTarget
        String sarisTargetsExpression = "/saris-report/search-plan-information/leeway-info/";
        List<SARISTarget> sarisTargets = getSARISTargets(sarisTargetsExpression);

        // Ignore errors info

        // Get search areas
        String searchAreaCoordinates = "/saris-report/search-area-coordinates";
        List<SARAreaData> sarAreas = getSearchAreas(searchAreaCoordinates);

        sarData = new DatumPointDataSARIS("N/A", LKPDate, CSP, DSP, 0.0, 0.0, 0.0, -1);

        sarData.setWeatherPoints(weatherData);

        sarData.setSarisTarget(sarisTargets);

        sarData.setSarAreaData(sarAreas);
        
        

    }

    private List<SARAreaData> getSearchAreas(String expression) throws XPathExpressionException {
        // System.out.println("Expression is " + expression);
        List<SARAreaData> sarAreas = new ArrayList<SARAreaData>();
        String targetDataExpression = expression;

        int i = 1;
        while (true) {

            SARAreaData entry = getSearchArea(targetDataExpression + "/search-area-target[@name='Target " + i + "']");

            i = i + 1;
            if (entry == null) {
                break;
            } else {

                sarAreas.add(entry);
            }

        }

        return sarAreas;

    }

    private SARAreaData getSearchArea(String expression) throws XPathExpressionException {
        // System.out.println("Expression is " + expression);
        expression = expression + "/search-area[@name='Area']";

        System.out.println("Expression is " + expression);
        
        // We need A, B C, D and center.

        String areaExpressionA = expression + "/position[@name='A']/angle/*";
        String areaExpressionB = expression + "/position[@name='B']/angle/*";
        String areaExpressionC = expression + "/position[@name='C']/angle/*";
        String areaExpressionD = expression + "/position[@name='D']/angle/*";
        String areaExpressionCentre = expression + "/position[@name='centre']/angle/*";

        Position A = getPositionFromNodeList(areaExpressionA);
        Position B = getPositionFromNodeList(areaExpressionB);
        Position C = getPositionFromNodeList(areaExpressionC);
        Position D = getPositionFromNodeList(areaExpressionD);
        Position centre = getPositionFromNodeList(areaExpressionCentre);

        if (A == null || B == null || C == null || D == null) {
            return null;
        }
        
        System.out.println("A is " + A);
        System.out.println("B is " + B);
        System.out.println("C is " + C);
        System.out.println("D is " + D);

        // Breadth or width
        String breadthExpression = expression + "/distance[@name='Breadth']";
        Node node = (Node) xPath.compile(breadthExpression).evaluate(document, XPathConstants.NODE);
        Double breadth = Double.parseDouble(node.getFirstChild().getNodeValue());

        System.out.println("Breadth is " + breadth);
        // Length
        String lengthExpression = expression + "/distance[@name='Length']";
        node = (Node) xPath.compile(lengthExpression).evaluate(document, XPathConstants.NODE);
        Double length = Double.parseDouble(node.getFirstChild().getNodeValue());

        System.out.println("Length is " + length);
        
        SARAreaData sarArea = new SARAreaData(A, B, C, D, centre, breadth, length);

        return sarArea;

    }

    private List<SARISTarget> getSARISTargets(String expression) throws XPathExpressionException {

        String targetDataExpression = expression;
        List<SARISTarget> sarisTargets = new ArrayList<SARISTarget>();
        int i = 1;
        while (true) {

            SARISTarget entry = getSingleSARISData(targetDataExpression + "/target[@name='Target " + i + "']");

            i = i + 1;
            if (entry == null) {
                break;
            } else {
                sarisTargets.add(entry);
            }
        }

        return sarisTargets;
    }

    private SARISTarget getSingleSARISData(String expression) throws XPathExpressionException {
        // System.out.println("Expression is " + expression);

        Node node = (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);
        // System.out.println(node.getAttributes().item(0).getNodeValue());

        if (node == null) {
            return null;
        }

        String targetName = node.getAttributes().item(0).getNodeValue();

        String leewayFormulaExpression = expression + "/*";
        node = (Node) xPath.compile(leewayFormulaExpression).evaluate(document, XPathConstants.NODE);
        String leewayFormula = node.getFirstChild().getNodeValue();
        // System.out.println(leewayFormula);

        NodeList nodeList = createNodeListFromExpression(leewayFormulaExpression);

        double divergence = Double.parseDouble(nodeList.item(1).getChildNodes().item(1).getFirstChild().getNodeValue());

        String type = nodeList.item(2).getFirstChild().getNodeValue();

        SARISTarget target = new SARISTarget(targetName, leewayFormula, divergence, type);

        return target;
    }

    private List<SARWeatherData> getSARWeatherData(String expression) throws XPathExpressionException {

        String weatherDataExpression = expression;
        List<SARWeatherData> weatherData = new ArrayList<SARWeatherData>();
        int i = 1;
        while (true) {

            SARWeatherData entry = getSingleSARWeatherData(weatherDataExpression + "/wind-entry[@index='" + i + "']");

            i = i + 1;
            if (entry == null) {
                // System.out.println("Breaking");
                break;
            } else {
                weatherData.add(entry);
            }
        }

        // String test = "/saris-report/search-plan-information/wind-input/wind-entry[@index='0']/time/*";
        //
        // System.out.println(getDateFromNodeList(test));

        // printExpressionResult(expression);

        // NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

        // for (int i = 0; i < nodeList.getLength(); i++) {
        // getSingleSARWeatherData(nodeList.item(i));
        // break;
        // }

        return weatherData;
    }

    private SARWeatherData getSingleSARWeatherData(String expression) throws XPathExpressionException {

        String timeEntry = expression + "/time/*";
        DateTime weatherTime = getDateFromNodeList(timeEntry);

        // Leeway
        String windValueEntry = expression + "/wind-value/*";
        List<Double> windValues = getWindValues(windValueEntry);

        if (weatherTime == null || windValues == null) {
            return null;
        }

        SARWeatherData sarEntry = new SARWeatherData(-1, -1, windValues.get(0), windValues.get(1), weatherTime);

        return sarEntry;
    }

    private List<Double> getWindValues(String expression) throws XPathExpressionException {
        NodeList nodeList = createNodeListFromExpression(expression);
        // System.out.println(expression);
        ArrayList<Double> values = new ArrayList<Double>();

        if (nodeList.getLength() == 0) {
            return null;
        }

        if (nodeList.item(0).getNodeName().equals("speed")) {
            // Speed
            // System.out.println(nodeList.item(0).getNodeName());
            // System.out.println(nodeList.item(0).getFirstChild().getNodeValue());
            values.add(Double.parseDouble(nodeList.item(0).getFirstChild().getNodeValue()));
        } else {
            return null;
        }

        if (nodeList.item(1).getNodeName().equals("angle")) {
            // Speed
            // System.out.println(nodeList.item(1).getNodeName());
            // System.out.println(nodeList.item(1).getChildNodes().item(1).getFirstChild().getNodeValue());
            values.add(Double.parseDouble(nodeList.item(1).getChildNodes().item(1).getFirstChild().getNodeValue()));

            // values.add(Double.parseDouble(nodeList.item(1).getFirstChild().getNodeValue()));
        } else {
            return null;
        }

        // for (int i = 0; i < nodeList.getLength(); i++) {
        // System.out.println(nodeList.item(i) + " i " + i);
        // }

        return values;
    }

    private SARWeatherData getSingleSARWeatherData(Node node) throws XPathExpressionException {

        Element el = (Element) node;
        NodeList children = el.getChildNodes();
        Node timeEntry = children.item(1);

        Element timeElement = (Element) timeEntry;

        NodeList timeNodes = timeElement.getChildNodes();

        DateTime windEntryDate = new DateTime();

        // Day 0
        windEntryDate = windEntryDate.withDayOfMonth(Integer.parseInt(timeNodes.item(0).getFirstChild().getNodeValue()));

        // Month 2
        windEntryDate = windEntryDate.withMonthOfYear(Integer.parseInt(timeNodes.item(2).getFirstChild().getNodeValue()));

        // Year 4
        windEntryDate = windEntryDate.withYear(Integer.parseInt(timeNodes.item(4).getFirstChild().getNodeValue()));

        // hour 6
        windEntryDate = windEntryDate.withHourOfDay(Integer.parseInt(timeNodes.item(6).getFirstChild().getNodeValue()));

        // minute 8
        windEntryDate = windEntryDate.withMinuteOfHour(Integer.parseInt(timeNodes.item(8).getFirstChild().getNodeValue()));

        // second 10
        windEntryDate = windEntryDate.withSecondOfMinute(Integer.parseInt(timeNodes.item(10).getFirstChild().getNodeValue()));

        Node windEntry = children.item(3);
        Element windElements = (Element) windEntry;
        NodeList windNodes = windElements.getChildNodes();

        System.out.println(windElements.getFirstChild().getNodeValue());

        for (int k = 0; k < windNodes.getLength(); k++) {
            System.out.println("k is " + k);
            Node child2 = windNodes.item(k);
            System.out.println("Child2 name is " + child2.getNodeName());
            if (child2.getNodeType() != Node.TEXT_NODE) {
                System.out.println("child tag: " + child2.getNodeName());
                if (child2.getFirstChild().getNodeType() == Node.TEXT_NODE) {
                    System.out.println("inner child value:" + child2.getFirstChild().getNodeValue());
                }

            }
        }

        // System.out.println(node.getChildNodes());

        // Get time
        // System.out.println(getDateFromNodeList(node.getChildNodes().item(1).getChildNodes()));
        // System.out.println(node.getChildNodes().item(1).getChildNodes().item(0).getChildNodes().item(0).getNodeValue());

        // Wind values
        // System.out.println(node.getChildNodes().item(3));

        // for (int i = 0; i < node.getChildNodes().getLength(); i++) {
        // System.out.println(node.getChildNodes().item(i).getChildNodes().item(1));
        // }

        return null;
    }

    private NodeList createNodeListFromExpression(String expression) throws XPathExpressionException {

        // String email = xPath.compile(expression).evaluate(document);

        // System.out.println(email);

        // read an xml node using xpath
        // Node node = (Node) xPath.compile(expression).evaluate(document, XPathConstants.NODE);
        // System.out.println(node);
        // read a nodelist using xpath
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);

        return nodeList;
    }

    private Position getPositionFromNodeList(String expression) throws XPathExpressionException {
        NodeList nodeList = createNodeListFromExpression(expression);

        if (nodeList.getLength() == 0) {
            return null;
        }

        // Latitude
        double latitude = Double.parseDouble(nodeList.item(0).getFirstChild().getNodeValue());
        double longitude = Double.parseDouble(nodeList.item(4).getFirstChild().getNodeValue());

        // Index 0 is Latitude
        // Index 4 is Longitude

        return Position.create(latitude, longitude);
    }

    private void printExpressionResult(String expression) throws XPathExpressionException {
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
        System.out.println("Node list size is " + nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); i++) {
            System.out.println(" uhm " + nodeList.item(i));
        }
    }

    private DateTime getDateFromNodeList(String expression) throws XPathExpressionException {
        NodeList nodeList = createNodeListFromExpression(expression);
        return getDateFromNodeList(nodeList);
    }

    private DateTime getDateFromNodeList(NodeList nodeList) {
        DateTime returnDate = new DateTime();

        if (nodeList.getLength() == 0) {
            return null;
        }

        if (nodeList.item(0).getNodeName().equals("day")) {
            // Day
            returnDate = returnDate.withDayOfMonth(Integer.parseInt(nodeList.item(0).getFirstChild().getNodeValue()));
            // System.out.println("DAY");
        }

        if (nodeList.item(1).getNodeName().equals("month")) {
            // Month
            returnDate = returnDate.withMonthOfYear(Integer.parseInt(nodeList.item(1).getFirstChild().getNodeValue()));
            // System.out.println("MONTH");
        }

        if (nodeList.item(2).getNodeName().equals("year")) {
            // Year
            returnDate = returnDate.withYear(Integer.parseInt(nodeList.item(2).getFirstChild().getNodeValue()));
            // System.out.println("YEAR");
        }

        if (nodeList.item(3).getNodeName().equals("hour")) {
            // Hour
            returnDate = returnDate.withHourOfDay(Integer.parseInt(nodeList.item(3).getFirstChild().getNodeValue()));
            // System.out.println("HOUR");
        }

        if (nodeList.item(4).getNodeName().equals("minute")) {
            // Minute
            returnDate = returnDate.withMinuteOfHour(Integer.parseInt(nodeList.item(4).getFirstChild().getNodeValue()));
            // System.out.println("MINUTE");
        }

        if (nodeList.item(5).getNodeName().equals("second")) {
            // Second
            returnDate = returnDate.withSecondOfMinute(Integer.parseInt(nodeList.item(5).getFirstChild().getNodeValue()));
            // System.out.println("SECOND");
        }

        return returnDate;

    }

    /**
     * @return the sarData
     */
    public DatumPointDataSARIS getSarData() {
        return sarData;
    }

    public static void main(String[] args) {

        // SAXParserExample parser = new SAXParserExample();
        // parser.runExample();

        // XPathParser parser = new XPathParser("000084-13052014/000084-13052014/Sarex Fn.xml");
        try {
            SARISXMLParser parser = new SARISXMLParser("E://mfi abri.xml");
            // SARISXMLParser parser = new SARISXMLParser("E://Sarex Fn.xml");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
