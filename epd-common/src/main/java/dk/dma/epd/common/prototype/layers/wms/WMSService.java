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

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import com.bbn.openmap.image.ImageServerConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.plugin.wms.WMSPlugIn;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.status.WMSStatus;



public class WMSService extends WMSPlugIn implements ImageServerConstants, IStatusComponent {

    private OMGraphicList wmsList = new OMGraphicList();
    private String wmsQuery = "";
    private String bbox;
    private String width;
    private String height;
    private int wmsWidth;
    private int wmsHeight;
    private Double wmsullon;
    private Double wmsullat;
    private Double deltaX = 0.0013;
    private Double deltaY = 0.00058;
//    private Double deltaX = 0.00;
//    private Double deltaY = 0.00;
    private boolean wmsImage;
    private WMSStatus status = new WMSStatus();
    private float zoomLevel = -1;

    /**
     * Constructor for the WMS Service - loads the WMS server from the settings file
     */
    public WMSService(String wmsQuery) {
        super();
        this.wmsQuery = wmsQuery;
    }

    public void setWMSString(String wmsString){
        this.wmsQuery = wmsString;
    }

    public void setZoomLevel(float zoom){
        zoomLevel = zoom;
    }
    /**
     * Set the position of the WMS image and what area we wish to display
     * @param ullon
     * @param ullat
     * @param upperLeftLon
     * @param upperLeftLat
     * @param lowerRightLon
     * @param lowerRightLat
     * @param w
     * @param h
     */
    public void setWMSPosition(Double ullon, Double ullat, Double upperLeftLon, Double upperLeftLat, Double lowerRightLon, Double lowerRightLat, int w, int h){
        this.wmsWidth = w;
        this.wmsHeight = h;
        this.wmsullon = ullon;
        this.wmsullat = ullat;
        //Because finished education and 10 years of experince we know to add the delta values
        this.bbox = Double.toString(upperLeftLon + deltaX) + "," +
                  Double.toString(lowerRightLat + deltaY) + "," +
                  Double.toString(lowerRightLon + deltaX) + "," +
                  Double.toString(upperLeftLat + deltaY);
        this.width = Integer.toString(w);
        this.height = Integer.toString(h);
    }


    /**
     * Get the generated WMS query
     * @return
     */
    public String getQueryString(){
        String queryString = "";


        if (wmsQuery.indexOf("kortforsyningen.kms.dk/soe_enc_primar") > 0){

            String[] splittedUrl = wmsQuery.split("&");
            String newUrl = "";

            String styleReplacer = "";

            //3428460
            //Do style 244
            if (zoomLevel > 727875){
                styleReplacer = "STYLES=style-id-244";
            }

            //Do style 200
            if (zoomLevel <= 727875){
                styleReplacer = "STYLES=style-id-200";
            }

            //Do style 246
            if (zoomLevel <= 363937){
                styleReplacer = "STYLES=style-id-246";
            }

//            //Do style 245
//            if (zoomLevel <= 181968){
//                styleReplacer = "STYLES=style-id-245";
//            }



            for (int i = 0; i < splittedUrl.length; i++) {

                if (splittedUrl[i].startsWith("STYLES=")){
                    splittedUrl[i] = styleReplacer;
                }

                if (i != splittedUrl.length-1){

                newUrl = newUrl + splittedUrl[i] + "&";
                }else{
                    newUrl = newUrl + splittedUrl[i];
                }

            }
            queryString = newUrl
                    + "&BBOX="+bbox
                    + "&WIDTH=" + width
                    + "&HEIGHT=" + height;
        }else{
            queryString = wmsQuery
                    + "&BBOX="+bbox
                    + "&WIDTH=" + width
                    + "&HEIGHT=" + height;
        }


//        System.out.println(queryString);

        return queryString;
    }


    /**
     * After the query has been generated this completes it and returns a OMGraphiclist of the graphics
     * @return
     */
    public OMGraphicList getWmsList() {

        java.net.URL url = null;
        try {
            url = new java.net.URL(getQueryString());
            java.net.HttpURLConnection urlc = (java.net.HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");
            urlc.setDoInput(true);
            urlc.setDoOutput(true);
            urlc.setRequestMethod("GET");
            urlc.disconnect();
            wmsList.clear();
            //wmsList.add(new CenterRaster(55.6760968, 12.568337, 445, 472, new ImageIcon(url)));
            ImageIcon wmsImg = new ImageIcon(url);

            if (wmsImg.getIconHeight() == -1 || wmsImg.getIconWidth() ==-1){
//                System.out.println("no WMS");
                Image noImage = new ImageIcon(EPD.class.getClassLoader().getResource("images/noWMSAvailable.png")).getImage();
                BufferedImage bi = new BufferedImage(noImage.getWidth(null), noImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bi.createGraphics();
                g.drawImage(noImage, 0, 0, wmsWidth, wmsHeight, null, null);
                ImageIcon noImageIcon = new ImageIcon(bi);
                wmsList.add(new CenterRaster(this.wmsullat, this.wmsullon, this.wmsWidth, this.wmsHeight, noImageIcon));
                wmsImage = false;
            }else{
                status.markContactSuccess();
                wmsList.add(new CenterRaster(this.wmsullat, this.wmsullon, this.wmsWidth, this.wmsHeight, wmsImg));
                wmsImage = true;
            }



//            System.out.println(wmsImg.getIconHeight());
//            System.out.println(wmsImg.getIconWidth());
            //If iconHeight or width == -1 no WMS available

        } catch (java.net.MalformedURLException murle) {
            status.markContactError(murle);
            System.out.println("Bad URL!");
        } catch (java.io.IOException ioe) {
            System.out.println("IO Exception");
            status.markContactError(ioe);
        }
        return wmsList;
    }

    @Override
    public String createQueryString(Projection arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isWmsImage() {
        return wmsImage;
    }

    @Override
    public String getServerName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ComponentStatus getStatus() {
        return status;
    }

}
