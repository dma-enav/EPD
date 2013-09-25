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
package dk.dma.epd.common.prototype.layers.wms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.prototype.status.WMSStatus;

public abstract class AbstractWMSService {
    protected Logger LOG;

    protected String wmsQuery = "";
    protected String width;
    protected String height;
    protected Double upperLeftLon;
    protected Double upperLeftLat;
    protected Double lowerRightLon;
    protected Double lowerRightLat;

    protected int wmsWidth;
    protected int wmsHeight;
    protected Double wmsullon;
    protected Double wmsullat;
    // protected Double deltaX = 0.0013;
    // protected Double deltaY = 0.00058;
    // protected Double deltaY = 0.00068;
    private Double deltaX = 0.000000;
    private Double deltaY = 0.000000;
    protected WMSStatus status = new WMSStatus();
    protected float zoomLevel = -1;

    public AbstractWMSService(String wmsQuery) {
        this.LOG = LoggerFactory.getLogger(this.getClass());
        this.wmsQuery = wmsQuery;
    }

    public AbstractWMSService(String wmsQuery, Projection p) {
        this(wmsQuery);
        this.setWMSPosition(p);
        this.setZoomLevel(p);

    }

    protected void setZoomLevel(Projection p) {
        setZoomLevel(p.getScale());
    }

    protected void setWMSPosition(Projection p) {
        setWMSPosition(p.getUpperLeft().getX(), p.getUpperLeft().getY(), p
                .getUpperLeft().getX(), p.getUpperLeft().getY(), p
                .getLowerRight().getX(), p.getLowerRight().getY(),
                p.getWidth(), p.getHeight());
    }

    protected void setZoomLevel(float zoom) {
        zoomLevel = zoom;
    }

    /**
     * Set the position of the WMS image and what area we wish to display
     * 
     * @param ullon
     * @param ullat
     * @param upperLeftLon
     * @param upperLeftLat
     * @param lowerRightLon
     * @param lowerRightLat
     * @param w
     * @param h
     */
    protected void setWMSPosition(Double ullon, Double ullat,
            Double upperLeftLon, Double upperLeftLat, Double lowerRightLon,
            Double lowerRightLat, int w, int h) {

        this.wmsWidth = w;
        this.wmsHeight = h;
        this.wmsullon = ullon;
        this.wmsullat = ullat;
        this.width = Integer.toString(w);
        this.height = Integer.toString(h);

        this.upperLeftLon = upperLeftLon;
        this.upperLeftLat = upperLeftLat;
        this.lowerRightLon = lowerRightLon;
        this.lowerRightLat = lowerRightLat;

    }

    /**
     * Get the generated WMS query
     * 
     * @author David A. Camre (davidcamre@gmail.com)
     * @return
     */
    protected String getQueryString() {
        String queryString = "";

        if (wmsQuery.indexOf("kortforsyningen.kms.dk/soe_enc_primar") > 0) {

            String[] splittedUrl = wmsQuery.split("&");
            String newUrl = "";

            String styleReplacer = "";

            // 3428460
            // Do style 244
            if (zoomLevel > 727875) {
                styleReplacer = "STYLES=style-id-244";
            }

            // Do style 200
            if (zoomLevel <= 727875) {
                styleReplacer = "STYLES=style-id-200";
            }

            // Do style 246
            if (zoomLevel <= 363937) {
                styleReplacer = "STYLES=style-id-246";
            }

            // //Do style 245
            // if (zoomLevel <= 181968){
            // styleReplacer = "STYLES=style-id-245";
            // }

            for (int i = 0; i < splittedUrl.length; i++) {

                if (splittedUrl[i].startsWith("STYLES=")) {
                    splittedUrl[i] = styleReplacer;
                }

                if (i != splittedUrl.length - 1) {

                    newUrl = newUrl + splittedUrl[i] + "&";
                } else {
                    newUrl = newUrl + splittedUrl[i];
                }

            }
            queryString = newUrl + "&BBOX=" + getBbox() + "&WIDTH=" + width
                    + "&HEIGHT=" + height;
        } else {
            queryString = wmsQuery + "&BBOX=" + getBbox() + "&WIDTH=" + width
                    + "&HEIGHT=" + height;
        }

        return queryString;
    }

    public void setWMSString(String wmsString) {
        this.wmsQuery = wmsString;
    }

    /**
     * After the query has been generated this completes it and returns a
     * OMGraphiclist of the graphics
     * 
     * @return
     */
    public abstract OMGraphicList getWmsList(Projection p);

    public String getBbox() {
        // @author Renoud Because finished education and 10 years of experince
        // we know to add the delta values
        return Double.toString(upperLeftLon + deltaX) + ","
                + Double.toString(lowerRightLat + deltaY) + ","
                + Double.toString(lowerRightLon + deltaX) + ","
                + Double.toString(upperLeftLat + deltaY);

    }

    public static Projection normalizeProjection(Projection p) {
        //TODO: implement
        return (Projection) p.makeClone();

    }

}
