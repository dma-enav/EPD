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

package dk.dma.epd.common.prototype.gui.util;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;

/**
 * @author jtj-sfs
 *
 */
public class SimpleOffScreenMapRenderer extends Thread implements
        ProjectionListener, Runnable {

    private static final int SCREEN_BOUND_X = -5000;
    private static final int SCREEN_BOUND_Y = -5000;
    
    protected MapBean sourceBean;
    protected MapBean targetBean;
    private final Object imgLock = new Object();
    private BufferedImage img;
    private BufferedImage outImg;
    private JFrame frame;

    private Logger LOG;

    public SimpleOffScreenMapRenderer(MapBean sourceBean, MapBean targetBean,
            int sizeFactor) {
        super();

        this.LOG = LoggerFactory.getLogger(SimpleOffScreenMapRenderer.class);

        this.sourceBean = sourceBean;
        this.targetBean = targetBean;

        this.targetBean.setScale(sourceBean.getScale());
        this.targetBean.setCenter(sourceBean.getCenter());

        targetBean.setSize(256 * sizeFactor, 256 * sizeFactor);

        int w = targetBean.getWidth();
        int h = targetBean.getHeight();

        img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        this.frame = new JFrame();
        frame.setVisible(false);
        frame.setSize(new Dimension(targetBean.getWidth(), targetBean
                .getHeight()));
        frame.setBounds(SCREEN_BOUND_X, SCREEN_BOUND_Y, targetBean.getWidth(), targetBean.getHeight());
        frame.add(targetBean);
        frame.setVisible(true);

        sourceBean.addProjectionListener(this);

    }

    public BufferedImage call() {
        BufferedImage i = getScreenshot();
        return i;
    }

    public BufferedImage getScreenshot() {
        synchronized (imgLock) {
            long start = System.currentTimeMillis();
            
            //frame.repaint();
            this.targetBean.paint(img.getGraphics());
            long end = System.currentTimeMillis();
            LOG.debug("To Paint: " + (end - start));
            
            
            
            AffineTransform at = new AffineTransform();
            at.scale(3.0, 3.0);
            AffineTransformOp scaleOp = new AffineTransformOp(at,
                    AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            scaleOp.filter(img, outImg);
            
            
        }

        return outImg;
    }

    public MapBean getTargetBean() {
        return this.targetBean;
    }

    @Override
    public void projectionChanged(ProjectionEvent arg0) {
        updateTargetMap();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                this.frame.repaint();
            }
        }
    }

    public void saveScreenShot(File out) {
        try {
            ImageIO.write(this.getScreenshot(), "PNG", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateTargetMap() {
        frame.setVisible(true);
        int w = (int) sourceBean.getSize().getWidth();
        int h = (int) sourceBean.getSize().getHeight();

        float scaleDiff = targetBean.getScale() / sourceBean.getScale();
        if (Math.abs(scaleDiff - 3.0) > 0.01) {
            targetBean.setScale((float) (sourceBean.getScale() * 3));
        }
        
        if (!targetBean.getCenter().equals(sourceBean)) {
            targetBean.setCenter(sourceBean.getCenter());
        }
        
        if ((int) frame.getSize().getWidth() != w
                || (int) frame.getSize().getHeight() != h) {
            frame.setSize(w, h);
            frame.setBounds(SCREEN_BOUND_X, SCREEN_BOUND_Y, w, h);
        }
        
        targetBean.setSize(w, h);

        if (img.getWidth() != w || img.getHeight() != h) {
            synchronized (imgLock) {
                img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                outImg = new BufferedImage(w * 3, h * 3,
                        BufferedImage.TYPE_INT_RGB);
            }
        }
        
        this.frame.repaint();
        frame.setVisible(false);
    }
    

}
