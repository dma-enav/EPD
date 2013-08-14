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
import java.util.concurrent.Callable;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.image.ImageServerConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.omGraphics.OMRaster;

import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;



public class SingleWMSService extends AbstractWMSService implements ImageServerConstants, IStatusComponent, Callable<OMGraphicList> {
    private static final Logger LOG = LoggerFactory
            .getLogger(SingleWMSService.class);
    
    /**
     * Initialize a SingleWMSService instance that uses wmsQuery as base string
     * @param wmsQuery base string for web service address
     */
    public SingleWMSService(String wmsQuery) {
        super(wmsQuery);
    }


    public OMGraphicList getWmsList() {
        java.net.URL url = null;
        try {
            
            url = new java.net.URL(getQueryString());
            /*
            java.net.HttpURLConnection urlc = (java.net.HttpURLConnection) url.openConnection();
            urlc.setRequestProperty("User-Agent","Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.8.1.14) Gecko/20080404 Firefox/2.0.0.14");
            urlc.setDoInput(true);
            urlc.setDoOutput(true);
            urlc.setRequestMethod("GET");
            urlc.disconnect();*/
            wmsList.clear();

            ImageIcon wmsImg = new ImageIcon(url);
            wmsImg.getImage();

            if (wmsImg.getIconHeight() == -1 || wmsImg.getIconWidth() ==-1){
                Image noImage = new ImageIcon(EPD.class.getClassLoader().getResource("images/noWMSAvailable.png")).getImage();
                BufferedImage bi = new BufferedImage(noImage.getWidth(null), noImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bi.createGraphics();
                g.drawImage(noImage, 0, 0, wmsWidth, wmsHeight, null);
                ImageIcon noImageIcon = new ImageIcon(bi);
                wmsList.add(new CenterRaster(this.wmsullat, this.wmsullon, this.wmsWidth, this.wmsHeight, noImageIcon));
                wmsImage = false;
            }else{
                status.markContactSuccess();
                wmsList.add(new CenterRaster(this.wmsullat, this.wmsullon, this.wmsWidth, this.wmsHeight, wmsImg));
                wmsImage = true;
            }

        } catch (java.net.MalformedURLException murle) {
            status.markContactError(murle);
            System.out.println("Bad URL!");
        }
        System.out.println("thread done");
        return wmsList;
    }


    @Override
    public ComponentStatus getStatus() {
        return status;
    }


    @Override
    public OMGraphicList call() throws Exception {
        return getWmsList();
    }

}
