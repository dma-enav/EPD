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
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.util.concurrent.Callable;

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

            // System.out.println("Query string is: " + getQueryString());

            ImageIcon wmsImg = new ImageIcon(url);

            wmsImg = new ImageIcon(transformWhiteToTRansparent(wmsImg));

            wmsImg.getImage();

            if (wmsImg.getIconHeight() == -1 || wmsImg.getIconWidth() == -1) {
                Image noImage = EPD.res().getCachedImageIcon("images/noWMSAvailable.png").getImage();
                BufferedImage bi = new BufferedImage(noImage.getWidth(null), noImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics g = bi.createGraphics();
                g.drawImage(noImage, 0, 0, wmsWidth, wmsHeight, null);
                ImageIcon noImageIcon = new ImageIcon(bi);
                wmsList.add(new CenterRaster(getProjection().getCenter().getY(), getProjection().getCenter().getX(), this.wmsWidth,
                        this.wmsHeight, noImageIcon));

            } else {
                status.markContactSuccess();
                // wmsList.add(new CenterRaster(this.wmsullat, this.wmsullon, this.wmsWidth, this.wmsHeight, wmsImg));
                // wmsList.add(new
                // OMRaster(this.projection.getUpperLeft().getY(),this.projection.getUpperLeft().getX(),this.wmsWidth,this.wmsHeight,wmsImg));
                wmsList.add(new CenterRaster(getProjection().getCenter().getY(), getProjection().getCenter().getX(), this.wmsWidth,
                        this.wmsHeight, wmsImg));
            }

        } catch (java.net.MalformedURLException murle) {
            status.markContactError(murle);
            LOG.error("Bad URL!");
        }
        // LOG.debug("DONE DOWNLOADING");

        return wmsList;
    }

    private Image transformWhiteToTRansparent(ImageIcon wmsImg) {

        BufferedImage dest = new BufferedImage(wmsImg.getIconWidth(), wmsImg.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = dest.createGraphics();
        g2.drawImage(wmsImg.getImage(), 0, 0, null);
        g2.dispose();

        // The color we want transparent
        final Color color = Color.WHITE;

        ImageFilter filter = new RGBImageFilter() {

            // the color we are looking for... Alpha bits are set to opaque
            public int markerRGB = color.getRGB() | 0xFF000000;

            public final int filterRGB(int x, int y, int rgb) {
                if ((rgb | 0xFF000000) == markerRGB) {
                    // Mark the alpha bits as zero - transparent
                    return 0x00FFFFFF & rgb;
                } else {
                    // nothing to do
                    return rgb;
                }
            }
        };

        ImageProducer ip = new FilteredImageSource(dest.getSource(), filter);
        return Toolkit.getDefaultToolkit().createImage(ip);

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
