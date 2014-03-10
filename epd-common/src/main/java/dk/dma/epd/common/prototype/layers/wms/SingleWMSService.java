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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.image.ImageServerConstants;
import com.bbn.openmap.omGraphics.OMGraphicList;
import com.bbn.openmap.proj.Projection;

import dk.dma.epd.common.graphics.CenterRaster;
import dk.dma.epd.common.prototype.EPD;
import dk.dma.epd.common.prototype.status.ComponentStatus;
import dk.dma.epd.common.prototype.status.IStatusComponent;

public final class SingleWMSService extends AbstractWMSService implements ImageServerConstants, IStatusComponent,
        Callable<OMGraphicList> {
    private static final Logger LOG = LoggerFactory.getLogger(SingleWMSService.class);
    private Projection projection;

    public SingleWMSService(String wmsQuery, Projection p) {
        super(wmsQuery, p);
        this.projection = p;
    }

    public Projection getProjection() {
        return projection;
    }

    public void setProjection(Projection projection) {
        this.projection = projection;
    }

    public SingleWMSService(String wmsQuery) {
        super(wmsQuery);
    }

    public OMGraphicList getWmsList(Projection p) {
        java.net.URL url = null;

        OMGraphicList wmsList = new OMGraphicList();
        
        try {

            url = new java.net.URL(getQueryString());

            BufferedImage image = ImageIO.read(url);

            if (image == null) {
                Image noImage = EPD.res().getCachedImageIcon("images/noWMSAvailable.png").getImage();
                BufferedImage bi = new BufferedImage(noImage.getWidth(null), noImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bi.createGraphics();
                g.drawImage(noImage, 0, 0, wmsWidth, wmsHeight, null);
                ImageIcon noImageIcon = new ImageIcon(bi);
                wmsList.add(new CenterRaster(getProjection().getCenter().getY(), getProjection().getCenter().getX(), this.wmsWidth,
                        this.wmsHeight, noImageIcon));

            } else {
                status.markContactSuccess();
                
                Image maskedImage = transformWhiteToTransparent(image);
                wmsList.add(new CenterRaster(getProjection().getCenter().getY(), getProjection().getCenter().getX(), this.wmsWidth,
                        this.wmsHeight, new ImageIcon(maskedImage)));
            }

        } catch (IOException ex) {
            status.markContactError(ex);
            LOG.error("Bad URL!");
        }
        // LOG.debug("DONE DOWNLOADING");

        return wmsList;
    }

    private Image transformWhiteToTransparent(BufferedImage image) {

        BufferedImage dest = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        
        image.flush();
        
        // Mask out the white pixels
        final int width = image.getWidth();
        int[] imgData = new int[width];
        
        // The color we want transparent
        final Color color = Color.WHITE;
        // the color we are looking for... Alpha bits are set to opaque
        int markerRGB = color.getRGB() | 0xFF000000;

        for (int y = 0; y < dest.getHeight(); y++) {
            // fetch a line of data from each image
            dest.getRGB(0, y, width, 1, imgData, 0, 1);
            // apply the mask
            for (int x = 0; x < width; x++) {
                if ((imgData[x] | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    imgData[x] = 0x00FFFFFF & imgData[x];
                } 
            }
            // replace the data
            dest.setRGB(0, y, width, 1, imgData, 0, 1);
        }
        return dest;
    }

    @Override
    public ComponentStatus getStatus() {
        return status;
    }

    @Override
    public OMGraphicList call() throws Exception {
        return getWmsList(getProjection());
    }

}
