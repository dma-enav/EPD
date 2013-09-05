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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bbn.openmap.MapBean;
import com.bbn.openmap.event.ProjectionEvent;
import com.bbn.openmap.event.ProjectionListener;
import com.bbn.openmap.proj.Projection;

/**
 * @author jtj-sfs
 *
 */
public class SimpleOffScreenMapRenderer extends Thread implements
        ProjectionListener, Runnable {

    private static final int SCREEN_BOUND_X = 10000;
    private static final int SCREEN_BOUND_Y = 10000;
    
    protected MapBean sourceBean;
    protected MapBean targetBean;
    private final Object imgLock = new Object();
    private BufferedImage img;
    private volatile BufferedImage outImg;
    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    private JFrame frame;
    private LinkedBlockingDeque<Projection> events = new LinkedBlockingDeque<Projection>();  

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
        //frame.setVisible(true);

        sourceBean.addProjectionListener(this);

    }

    private void drawGrid(BufferedImage image) {
        int i = 100;
        while(i < image.getHeight()) {
            int j = 100;
            while (j < image.getWidth()) {
                image.getGraphics().drawLine(j,0,j,image.getHeight());
                j+=100;
            }
            image.getGraphics().drawLine(0,i,image.getWidth(),i);
            i+=100;
        }
    }

    public void updateOutImg() {

        new Thread(new Runnable() {
            
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                
                //frame.repaint();
                targetBean.paint(img.getGraphics());
                long end = System.currentTimeMillis();
                LOG.debug("To Paint: " + (end - start));
                
                
                
                /*AffineTransform at = new AffineTransform();
                at.scale(1.0, 1.0);
                AffineTransformOp scaleOp = new AffineTransformOp(at,
                        AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                scaleOp.filter(img, outImg);*/
                
                drawGrid(img);
                setOutImg(img);
                
                
            }
        }).start();

    }
    
    public BufferedImage getOutImg() {
        return outImg;
    }
    
    public void setOutImg(BufferedImage i) {
        outImg = i;
        setImg(new BufferedImage(i.getWidth(), i.getHeight(), BufferedImage.TYPE_INT_RGB));
    }
    

    public MapBean getTargetBean() {
        return this.targetBean;
    }

    @Override
    public void projectionChanged(ProjectionEvent arg0) {
        this.events.offerLast(arg0.getProjection().makeClone());
    }

    @Override
    public void run() {
        while (true) {
            try {
                final LinkedList<Projection> l = new LinkedList<Projection>();
                updateTargetMap(events.takeLast());
                this.events.drainTo(l);                
            } catch (InterruptedException e) {
                
            }
        }
    }

    public void saveScreenShot(File out) {
        try {
            ImageIO.write(this.getOutImg(), "PNG", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateTargetMap(final Projection p) {
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {

                frame.setVisible(true);
                int w = (int) p.getWidth()*3;
                int h = (int) p.getHeight()*3;

                float scaleDiff = targetBean.getScale() / p.getScale();
                
                if (Math.abs(scaleDiff - 1.0) > 0.01) {
                    targetBean.setScale((float) (p.getScale() * 1));
                }
                if (!targetBean.getCenter().equals(p.getCenter())) {
                    targetBean.setCenter(p.getCenter());
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
                        outImg = new BufferedImage(w, h,
                                BufferedImage.TYPE_INT_RGB);
                    }
                }
                
                frame.repaint();
                updateOutImg(); //background paint
                frame.setVisible(false);

                
            }
        });
    }
    

}
