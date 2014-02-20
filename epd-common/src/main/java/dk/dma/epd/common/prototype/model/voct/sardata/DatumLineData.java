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
package dk.dma.epd.common.prototype.model.voct.sardata;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import dk.dma.enav.model.geometry.Position;
import dk.dma.epd.common.prototype.model.voct.LeewayValues;

public class DatumLineData extends SARData {

    private static final long serialVersionUID = 1L;

    List<DatumPointData> datumPointDataSets = new ArrayList<DatumPointData>();
    
    List<Position> datumLinePolygon = new ArrayList<Position>();
    

    
    // Init data
    public DatumLineData(String sarID, DateTime TLKP, DateTime CSS,
            Position LKP, double x, double y, double SF, int searchObject) {
        super(sarID, TLKP, CSS, LKP, x, y, SF, searchObject);
    }
    
    
    public void addDatumData(DatumPointData data){
        datumPointDataSets.add(data);
    }
    

    /**
     * @return the datumPointDataSets
     */
    public List<DatumPointData> getDatumPointDataSets() {
        return datumPointDataSets;
    }

    

    /**
     * @return the datumLinePolygon
     */
    public List<Position> getDatumLinePolygon() {
        return datumLinePolygon;
    }


    /**
     * @param datumLinePolygon the datumLinePolygon to set
     */
    public void setDatumLinePolygon(List<Position> datumLinePolygon) {
        this.datumLinePolygon = datumLinePolygon;
    }


    /**
     * @param datumPointDataSets the datumPointDataSets to set
     */
    public void setDatumPointDataSets(List<DatumPointData> datumPointDataSets) {
        this.datumPointDataSets = datumPointDataSets;
    }


    public void setBox(Position A, Position B, Position C, Position D) {
        
    }
    
  
    @Override
    public String generateHTML() {

        DateTimeFormatter fmt = DateTimeFormat
                .forPattern("dd MMMM, yyyy, 'at.' HH':'mm");

        // // Generate a html sheet of rapid response calculations
        StringBuilder str = new StringBuilder();
        // String name = "How does this look";
        //
        str.append("<html>");
        str.append("<table >");
        str.append("<tr>");
        str.append("<td align=\"left\" style=\"vertical-align: top;\">");
        str.append("<h1>Search and Rescue - Datum Point</h1>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Time of Last Known Position: "
                + fmt.print(this.getLKPDate()) + "");
        str.append("<br>Last Known Position: " + this.getLKP().toString()
                + "</br>");
        str.append("<br>Commence Search Start time: "
                + fmt.print(this.getCSSDate()) + "</br>");

        str.append(this.getWeatherPoints().get(0).generateHTML());

        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Initial Position Error, X in nautical miles: "
                + this.getX() + "");
        str.append("<br>SRU Navigational Error, Y in nautical miles: "
                + this.getY() + "</br>");
        str.append("<br>Safety Factor, Fs: " + this.getSafetyFactor() + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Object: "
                + LeewayValues.getLeeWayTypes().get(this.getSearchObject())
                + "");
        str.append("<br>With value: "
                + LeewayValues.getLeeWayContent().get(this.getSearchObject())
                + "</br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
//        str.append("Time Elapsed: " + Formatter.formatHours(timeElasped) + "");
//        str.append("<br>Applying Leeway and TWC gives a datum of  "
//                + datum.toString() + "</br>");
//        str.append("<br>With the following Residual Drift Vector</br>");
//        str.append("<br>RDV Direction: " + rdvDirection + "°</br>");
//        str.append("<br>RDV Distance: "
//                + Formatter.formatDouble(rdvDistance, 2) + " nm</br>");
//        str.append("<br>RDV Speed: " + Formatter.formatDouble(rdvSpeed, 2)
//                + " kn/h</br>");
//        str.append("<br>With radius: " + Formatter.formatDouble(radius, 2)
//                + "nm </br>");
        str.append("<hr>");
        str.append("<font size=\"4\">");
        str.append("Search Area:");
//        str.append("<br>A: " + A.toString() + "</br>");
//        str.append("<br>B: " + B.toString() + "</br>");
//        str.append("<br>C: " + C.toString() + "</br>");
//        str.append("<br>D: " + D.toString() + "</br>");
//        str.append("<br>Total Size: "
//                + Formatter.formatDouble(radius * 2 * radius * 2, 2)
//                + " nm2</br>");

        str.append("</font>");
        str.append("</td>");
        str.append("</tr>");
        str.append("</table>");
        str.append("</html>");
        //
        // calculationsText.setText(str.toString());
        //
        //

        return str.toString();
    }

}
