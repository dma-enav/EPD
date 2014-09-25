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
package dk.dma.epd.shore.gui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.joda.time.DateTime;

import dk.dma.epd.common.prototype.nogo.NoGoDataEntry;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.layers.voyage.EmbeddedInfoPanelMoveMouseListener;
import dk.dma.epd.shore.nogo.NogoHandler;
import dk.frv.enav.common.xml.nogo.types.NogoPolygon;

public class NoGoPanel extends JPanel implements MouseListener, ChangeListener {

    private static final long serialVersionUID = 1L;
    private JPanel moveHandler;
    private JPanel masterPanel;

    private JPanel nogoInternalPanel;
    private static int moveHandlerHeight = 18;

    public int width;
    public int height;
    private static int iconWidth = 16;
    private static int iconHeight = 16;
    private Border toolPaddingBorder = BorderFactory.createMatteBorder(3, 3, 3, 3, new Color(83, 83, 83));
    private Border toolInnerEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(37, 37, 37), new Color(
            52, 52, 52));

    JMapFrame parent;

    private JLabel statusTitleLabelSlider = new JLabel("Status");
    private JLabel statusLabelSlider = new JLabel("N/A");
    private final JLabel validToTxtLabelSlider = new JLabel("N/A");
    private final JLabel validFromTxtLabelSlider = new JLabel("N/A");
    private final JLabel draughtTxtLabelSlider = new JLabel("N/A");
    private final JLabel additionalTxtTitleLabelSlider = new JLabel("N/A");
    private final JLabel validFromLabelTitleSlider = new JLabel("Valid From");
    private final JLabel validToLabelTitleSlider = new JLabel("Valid to");
    private final JLabel draughtLabelTitleSlider = new JLabel("Depth Contour");
    private final JLabel additionalTxtTitleLabel2Slider = new JLabel("N/A");

    private JLabel statusTitleLabel = new JLabel("Status");
    private JLabel statusLabel = new JLabel("N/A");
    private final JLabel validToTxtLabel = new JLabel("N/A");
    private final JLabel validFromTxtLabel = new JLabel("N/A");
    private final JLabel draughtTxtLabel = new JLabel("N/A");
    private final JLabel additionalTxtTitleLabel = new JLabel("N/A");
    private final JLabel validFromLabelTitle = new JLabel("Valid From");
    private final JLabel validToLabelTitle = new JLabel("Valid to");
    private final JLabel draughtLabelTitle = new JLabel("Depth Contour");
    private final JLabel additionalTxtTitleLabel2 = new JLabel("N/A");

    private JPanel singleNoGoPanel;
    private JPanel multipleNoGoPanel;

    private JSlider slider;

    private DecimalFormat df = new DecimalFormat("#.#");
    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM , HH:mm");

    private NogoHandler nogoHandler;

    /**
     * Create the panel.
     * 
     * @param voyage
     */
    public NoGoPanel() {
        super();

        initGUI();

        setBounds(10, 50, 210, 150);

        // FlowLayout flowLayout = (FlowLayout) getLayout();
        // flowLayout.setAlignment(FlowLayout.LEFT);

        singleNoGoPanel = new JPanel();
        multipleNoGoPanel = new JPanel();

        createSingleRequestPanel();
        createSliderRequestPanel();

        initLabels();

        activateSingle();
        // activateSliderPanel();

    }

    public void activateSliderPanel() {
        nogoInternalPanel.remove(singleNoGoPanel);
        nogoInternalPanel.add(multipleNoGoPanel);

        repaintPanelSlide();
    }

    public void activateSingle() {
        nogoInternalPanel.add(singleNoGoPanel);
        nogoInternalPanel.remove(multipleNoGoPanel);

        repaintPanelSingle();
    }

    private void createSliderRequestPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 125, 90 };
        gridBagLayout.rowHeights = new int[] { 20, 16, 15, 0, 0, 0, 0, 0, 30 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        multipleNoGoPanel.setLayout(gridBagLayout);

        GridBagConstraints gbc_nogoTitleLabel = new GridBagConstraints();
        gbc_nogoTitleLabel.anchor = GridBagConstraints.NORTH;
        gbc_nogoTitleLabel.fill = GridBagConstraints.HORIZONTAL;
        gbc_nogoTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_nogoTitleLabel.gridwidth = 2;
        gbc_nogoTitleLabel.gridx = 0;
        gbc_nogoTitleLabel.gridy = 0;

        statusTitleLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        statusTitleLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statusTitleLabel = new GridBagConstraints();
        gbc_statusTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_statusTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_statusTitleLabel.gridx = 0;
        gbc_statusTitleLabel.gridy = 1;
        multipleNoGoPanel.add(statusTitleLabelSlider, gbc_statusTitleLabel);

        statusLabelSlider.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statusLabel = new GridBagConstraints();
        gbc_statusLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_statusLabel.insets = new Insets(0, 0, 5, 0);
        gbc_statusLabel.gridx = 1;
        gbc_statusLabel.gridy = 1;
        multipleNoGoPanel.add(statusLabelSlider, gbc_statusLabel);

        validFromLabelTitleSlider.setHorizontalAlignment(SwingConstants.LEFT);
        validFromLabelTitleSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_validFromLabelTitle = new GridBagConstraints();
        gbc_validFromLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_validFromLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_validFromLabelTitle.gridx = 0;
        gbc_validFromLabelTitle.gridy = 2;
        multipleNoGoPanel.add(validFromLabelTitleSlider, gbc_validFromLabelTitle);

        validFromTxtLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        validFromTxtLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel1 = new GridBagConstraints();
        gbc_statLabel1.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel1.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel1.gridx = 1;
        gbc_statLabel1.gridy = 2;
        multipleNoGoPanel.add(validFromTxtLabelSlider, gbc_statLabel1);

        GridBagConstraints gbc_validToLabelTitle = new GridBagConstraints();
        gbc_validToLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_validToLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_validToLabelTitle.gridx = 0;
        gbc_validToLabelTitle.gridy = 3;
        multipleNoGoPanel.add(validToLabelTitleSlider, gbc_validToLabelTitle);
        validToLabelTitleSlider.setHorizontalAlignment(SwingConstants.LEFT);
        validToLabelTitleSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        validToTxtLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        validToTxtLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel2 = new GridBagConstraints();
        gbc_statLabel2.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel2.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel2.gridx = 1;
        gbc_statLabel2.gridy = 3;
        multipleNoGoPanel.add(validToTxtLabelSlider, gbc_statLabel2);

        GridBagConstraints gbc_draughtLabelTitle = new GridBagConstraints();
        gbc_draughtLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_draughtLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_draughtLabelTitle.gridx = 0;
        gbc_draughtLabelTitle.gridy = 4;
        multipleNoGoPanel.add(draughtLabelTitleSlider, gbc_draughtLabelTitle);
        draughtLabelTitleSlider.setHorizontalAlignment(SwingConstants.LEFT);
        draughtLabelTitleSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        draughtTxtLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        draughtTxtLabelSlider.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel3 = new GridBagConstraints();
        gbc_statLabel3.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel3.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel3.gridx = 1;
        gbc_statLabel3.gridy = 4;
        multipleNoGoPanel.add(draughtTxtLabelSlider, gbc_statLabel3);

        additionalTxtTitleLabelSlider.setHorizontalAlignment(SwingConstants.LEFT);
        additionalTxtTitleLabelSlider.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints gbc_additionalTxtTitleLabel = new GridBagConstraints();
        gbc_additionalTxtTitleLabel.gridwidth = 2;
        gbc_additionalTxtTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_additionalTxtTitleLabel.anchor = GridBagConstraints.NORTH;
        gbc_additionalTxtTitleLabel.gridx = 0;
        gbc_additionalTxtTitleLabel.gridy = 5;
        multipleNoGoPanel.add(additionalTxtTitleLabelSlider, gbc_additionalTxtTitleLabel);

        additionalTxtTitleLabel2Slider.setHorizontalAlignment(SwingConstants.LEFT);
        additionalTxtTitleLabel2Slider.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints gbc_additionalTxtTitleLabel2 = new GridBagConstraints();
        gbc_additionalTxtTitleLabel2.anchor = GridBagConstraints.NORTH;
        gbc_additionalTxtTitleLabel2.insets = new Insets(0, 0, 5, 0);
        gbc_additionalTxtTitleLabel2.gridwidth = 2;
        gbc_additionalTxtTitleLabel2.gridx = 0;
        gbc_additionalTxtTitleLabel2.gridy = 6;
        multipleNoGoPanel.add(additionalTxtTitleLabel2Slider, gbc_additionalTxtTitleLabel2);

        slider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        slider.setSnapToTicks(true);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        slider.setMajorTickSpacing(1);
        slider.setMinorTickSpacing(1);
        // slider.addChangeListener(this);
        slider.setEnabled(false);
        //
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.fill = GridBagConstraints.HORIZONTAL;
        gbc_label.insets = new Insets(0, 0, 5, 0);
        gbc_label.gridwidth = 2;
        gbc_label.gridx = 0;
        gbc_label.gridy = 7;
        multipleNoGoPanel.add(slider, gbc_label);

        slider.addChangeListener(this);

    }

    private void createSingleRequestPanel() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 125, 90 };
        gridBagLayout.rowHeights = new int[] { 20, 16, 15, 0, 0, 0, 0, 0, 10 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        singleNoGoPanel.setLayout(gridBagLayout);

        statusTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        statusTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statusTitleLabel = new GridBagConstraints();
        gbc_statusTitleLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_statusTitleLabel.insets = new Insets(0, 0, 5, 5);
        gbc_statusTitleLabel.gridx = 0;
        gbc_statusTitleLabel.gridy = 1;
        singleNoGoPanel.add(statusTitleLabel, gbc_statusTitleLabel);

        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statusLabel = new GridBagConstraints();
        gbc_statusLabel.anchor = GridBagConstraints.NORTHWEST;
        gbc_statusLabel.insets = new Insets(0, 0, 5, 0);
        gbc_statusLabel.gridx = 1;
        gbc_statusLabel.gridy = 1;
        singleNoGoPanel.add(statusLabel, gbc_statusLabel);

        validFromLabelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        validFromLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_validFromLabelTitle = new GridBagConstraints();
        gbc_validFromLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_validFromLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_validFromLabelTitle.gridx = 0;
        gbc_validFromLabelTitle.gridy = 2;
        singleNoGoPanel.add(validFromLabelTitle, gbc_validFromLabelTitle);

        validFromTxtLabel.setHorizontalAlignment(SwingConstants.LEFT);
        validFromTxtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel1 = new GridBagConstraints();
        gbc_statLabel1.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel1.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel1.gridx = 1;
        gbc_statLabel1.gridy = 2;
        singleNoGoPanel.add(validFromTxtLabel, gbc_statLabel1);

        GridBagConstraints gbc_validToLabelTitle = new GridBagConstraints();
        gbc_validToLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_validToLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_validToLabelTitle.gridx = 0;
        gbc_validToLabelTitle.gridy = 3;
        singleNoGoPanel.add(validToLabelTitle, gbc_validToLabelTitle);
        validToLabelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        validToLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        validToTxtLabel.setHorizontalAlignment(SwingConstants.LEFT);
        validToTxtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel2 = new GridBagConstraints();
        gbc_statLabel2.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel2.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel2.gridx = 1;
        gbc_statLabel2.gridy = 3;
        singleNoGoPanel.add(validToTxtLabel, gbc_statLabel2);

        GridBagConstraints gbc_draughtLabelTitle = new GridBagConstraints();
        gbc_draughtLabelTitle.anchor = GridBagConstraints.WEST;
        gbc_draughtLabelTitle.insets = new Insets(0, 0, 5, 5);
        gbc_draughtLabelTitle.gridx = 0;
        gbc_draughtLabelTitle.gridy = 4;
        singleNoGoPanel.add(draughtLabelTitle, gbc_draughtLabelTitle);
        draughtLabelTitle.setHorizontalAlignment(SwingConstants.LEFT);
        draughtLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        draughtTxtLabel.setHorizontalAlignment(SwingConstants.LEFT);
        draughtTxtLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        GridBagConstraints gbc_statLabel3 = new GridBagConstraints();
        gbc_statLabel3.anchor = GridBagConstraints.NORTHWEST;
        gbc_statLabel3.insets = new Insets(0, 0, 5, 0);
        gbc_statLabel3.gridx = 1;
        gbc_statLabel3.gridy = 4;
        singleNoGoPanel.add(draughtTxtLabel, gbc_statLabel3);

        additionalTxtTitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        additionalTxtTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints gbc_additionalTxtTitleLabel = new GridBagConstraints();
        gbc_additionalTxtTitleLabel.gridwidth = 2;
        gbc_additionalTxtTitleLabel.insets = new Insets(0, 0, 5, 0);
        gbc_additionalTxtTitleLabel.anchor = GridBagConstraints.NORTH;
        gbc_additionalTxtTitleLabel.gridx = 0;
        gbc_additionalTxtTitleLabel.gridy = 5;
        singleNoGoPanel.add(additionalTxtTitleLabel, gbc_additionalTxtTitleLabel);

        additionalTxtTitleLabel2.setHorizontalAlignment(SwingConstants.LEFT);
        additionalTxtTitleLabel2.setFont(new Font("Segoe UI", Font.BOLD, 12));
        GridBagConstraints gbc_additionalTxtTitleLabel2 = new GridBagConstraints();
        gbc_additionalTxtTitleLabel2.anchor = GridBagConstraints.NORTH;
        gbc_additionalTxtTitleLabel2.gridwidth = 2;
        gbc_additionalTxtTitleLabel2.gridx = 0;
        gbc_additionalTxtTitleLabel2.gridy = 6;
        singleNoGoPanel.add(additionalTxtTitleLabel2, gbc_additionalTxtTitleLabel2);
    }

    private void initGUI() {
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45, 45)));

        setBackground(new Color(83, 83, 83));
        setLayout(null);

        // Create the top movehandler (for dragging)
        moveHandler = new JPanel(new BorderLayout());
        moveHandler.add(new JLabel("NoGo Panel", SwingConstants.CENTER), BorderLayout.CENTER);
        moveHandler.setForeground(new Color(200, 200, 200));
        moveHandler.setOpaque(true);
        moveHandler.setBackground(Color.DARK_GRAY);
        moveHandler.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(30, 30, 30)));
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setPreferredSize(new Dimension(208, moveHandlerHeight));

        JLabel close = new JLabel(EPDShore.res().getCachedImageIcon("images/window/close.png"));
        close.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setVisible(false);
            }
        });
        close.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        moveHandler.add(close, BorderLayout.EAST);

        // Create the grid for the toolitems
        nogoInternalPanel = new JPanel();
        nogoInternalPanel.setLayout(new BoxLayout(nogoInternalPanel, BoxLayout.PAGE_AXIS));
        nogoInternalPanel.setBackground(new Color(83, 83, 83));

        this.addMouseListener(this);

        // Create the masterpanel for aligning
        masterPanel = new JPanel(new BorderLayout());

        masterPanel.add(moveHandler, BorderLayout.NORTH);
        masterPanel.add(nogoInternalPanel, BorderLayout.SOUTH);
        masterPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45, 45)));

        add(masterPanel);

    }

    public void checkPosition() {
        if (parent != null) {

            if (parent.getWidth() != 0 || parent.getHeight() != 0) {
                if (this.getLocation().getX() > parent.getWidth()) {
                    this.setLocation(parent.getWidth() - this.getWidth() + 2, (int) this.getLocation().getY());
                }

                if (this.getLocation().getY() + this.getHeight() > parent.getHeight()) {
                    int y = (int) parent.getHeight() - this.getHeight() + 2;
                    if (y < 0) {
                        y = 18;
                    }

                    this.setLocation((int) this.getLocation().getX(), y);
                }
            }
        }
    }

    public void setParent(JMapFrame parent) {
        this.parent = parent;

        EmbeddedInfoPanelMoveMouseListener mml = new EmbeddedInfoPanelMoveMouseListener(this, parent);
        moveHandler.addMouseListener(mml);
        moveHandler.addMouseMotionListener(mml);

    }

    @Override
    public void mouseClicked(MouseEvent arg0) {

    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mousePressed(MouseEvent arg0) {

    }

    @Override
    public void mouseReleased(MouseEvent arg0) {
    }

    /**
     * Function for resizing the icons for the toolbar
     * 
     * @param imgpath
     *            path of the image
     * @return newimage the newly created and resized image
     */
    public ImageIcon toolbarIcon(String imgpath) {

        ImageIcon icon = EPDShore.res().getCachedImageIcon(imgpath);
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(iconWidth, iconHeight, java.awt.Image.SCALE_DEFAULT);
        ImageIcon newImage = new ImageIcon(newimg);
        return newImage;
    }

    /**
     * Function for refreshing the toolbar after editing toolitems
     */
    public void repaintPanelSingle() {

        // nogoInternalPanel.removeAll();
        nogoInternalPanel.updateUI();

        width = 210;
        height = 150;

        height = height + 7;

        masterPanel.setSize(width, height);
        // this.setBounds(10, 50, width, height);
        this.setSize(width, height);
        // this.setLocation(0, 50);

        this.revalidate();
        this.repaint();
    }

    public void repaintPanelSlide() {

        // nogoInternalPanel.removeAll();
        nogoInternalPanel.updateUI();

        width = 210;
        height = 200;

        height = height + 7;

        masterPanel.setSize(width, height);
        this.setSize(width, height);
        // this.setBounds(0, 50, width, height);
        // this.setSize(width, height);
        // this.setLocation(0, 50);

        this.revalidate();
        this.repaint();
    }

    public void setInactiveToolItem(JLabel toolItem) {
        toolItem.setBorder(toolPaddingBorder);
        toolItem.setOpaque(false);
    }

    /**
     * Function for setting the active tool item in the toolbar
     * 
     * @param tool
     *            reference to the active tool
     */
    public void setActiveToolItem(JLabel toolItem) {
        // Set active tool
        toolItem.setBackground(new Color(55, 55, 55));
        toolItem.setBorder(BorderFactory.createCompoundBorder(toolPaddingBorder, toolInnerEtchedBorder));
        toolItem.setOpaque(true);
    }

    private void initLabels() {

        statusLabel.setText("Inactive");
        statusLabelSlider.setText("Inactive");

        statusLabel.setEnabled(false);
        statusLabelSlider.setEnabled(false);

        // Valid from
        validFromTxtLabel.setEnabled(false);
        validFromTxtLabelSlider.setEnabled(false);

        // Valid to
        validToTxtLabel.setEnabled(false);
        validToTxtLabelSlider.setEnabled(false);

        // Draught
        draughtTxtLabel.setEnabled(false);
        draughtTxtLabelSlider.setEnabled(false);

        // Additional txt
        additionalTxtTitleLabel.setEnabled(false);
        additionalTxtTitleLabelSlider.setEnabled(false);

        additionalTxtTitleLabel2.setEnabled(false);
        additionalTxtTitleLabel2Slider.setEnabled(false);

        additionalTxtTitleLabel.setText("");
        additionalTxtTitleLabelSlider.setText("");

        additionalTxtTitleLabel2.setText("");
        additionalTxtTitleLabel2Slider.setText("");
    }

    public void newRequestSingle() {
        statusLabel.setEnabled(true);
        validFromTxtLabel.setEnabled(true);
        validToTxtLabel.setEnabled(true);
        draughtTxtLabel.setEnabled(true);
        additionalTxtTitleLabel.setEnabled(true);
        additionalTxtTitleLabel2.setEnabled(true);

        statusLabel.setText("Connecting...");
        statusLabel.setForeground(Color.GREEN);
        validFromTxtLabel.setText("N/A");
        validToTxtLabel.setText("N/A");
        draughtTxtLabel.setText("N/A");

        additionalTxtTitleLabel.setText("Requesting NoGo");
        additionalTxtTitleLabel2.setText("Please standby");

        this.setVisible(true);
    }

    public void newRequestMultiple() {
        statusLabelSlider.setEnabled(true);
        validFromTxtLabelSlider.setEnabled(true);
        validToTxtLabelSlider.setEnabled(true);
        draughtTxtLabelSlider.setEnabled(true);
        additionalTxtTitleLabelSlider.setEnabled(true);
        additionalTxtTitleLabel2Slider.setEnabled(true);

        statusLabelSlider.setText("Connecting...");
        statusLabelSlider.setForeground(Color.GREEN);
        validFromTxtLabelSlider.setText("N/A");
        validToTxtLabelSlider.setText("N/A");
        draughtTxtLabelSlider.setText("N/A");

        additionalTxtTitleLabelSlider.setText("Requesting NoGo");
        additionalTxtTitleLabel2Slider.setText("Please standby");
        slider.setEnabled(false);
        slider.setValue(0);

        this.setVisible(true);
    }

    public void nogoFailedSingle() {
        statusLabel.setText("Failed");
        statusLabel.setForeground(Color.RED);
        additionalTxtTitleLabel.setText("An error occured retrieving NoGo");
        additionalTxtTitleLabel2.setText("Try again in a few minutes");

        validFromTxtLabel.setEnabled(false);
        validToTxtLabel.setEnabled(false);
        draughtTxtLabel.setEnabled(false);
    }

    public void nogoFailedMultiple() {
        statusLabelSlider.setText("Failed");
        statusLabelSlider.setForeground(Color.RED);
        additionalTxtTitleLabelSlider.setText("An error occured retrieving NoGo");
        additionalTxtTitleLabel2Slider.setText("Try again in a few minutes");

        validFromTxtLabelSlider.setEnabled(false);
        validToTxtLabelSlider.setEnabled(false);
        draughtTxtLabelSlider.setEnabled(false);
    }

    public void noConnectionSingle() {
        statusLabel.setText("Failed");
        statusLabel.setForeground(Color.RED);
        additionalTxtTitleLabel.setText("No network connection");
        additionalTxtTitleLabel2.setText("Reestablish network and try again");

        validFromTxtLabel.setEnabled(false);
        validToTxtLabel.setEnabled(false);
        draughtTxtLabel.setEnabled(false);
    }

    public void noConnectionMultiple() {
        statusLabelSlider.setText("Failed");
        statusLabelSlider.setForeground(Color.RED);
        additionalTxtTitleLabelSlider.setText("No network connection");
        additionalTxtTitleLabel2Slider.setText("Reestablish network and try again");

        validFromTxtLabelSlider.setEnabled(false);
        validToTxtLabelSlider.setEnabled(false);
        draughtTxtLabelSlider.setEnabled(false);
    }

    public void requestCompletedSingle(int errorCodeOwn, List<NogoPolygon> polygonsOwn, Date validFrom, Date validTo, Double draught) {
        draught = -draught;

        // int draughtInt = (int) Math.round(draught);

        String validFromStr = "";
        String validToStr = "";

        if (validFrom != null) {
            validFromStr = sdf.format(validFrom);
            validToStr = sdf.format(validTo);
        }

        if (errorCodeOwn == 17) {
            statusLabel.setText("Failed");
            statusLabel.setForeground(Color.RED);
            additionalTxtTitleLabel.setText("No data for region");

            validFromTxtLabel.setEnabled(false);
            validToTxtLabel.setEnabled(false);
            draughtTxtLabel.setEnabled(false);

            additionalTxtTitleLabel2.setText(" ");
            return;
        }

        if (errorCodeOwn == 18) {
            statusLabel.setText("Limited");
            statusLabel.setForeground(Color.ORANGE);
            additionalTxtTitleLabel.setText("No tide data available for region");
            additionalTxtTitleLabel2.setText("");
            validFromTxtLabel.setText("N/A");
            validToTxtLabel.setText("N/A");
            draughtTxtLabel.setText(df.format(draught) + " meters");
            return;
        }
        if (polygonsOwn.size() == 0) {
            statusLabel.setText("Success");
            statusLabel.setForeground(Color.GREEN);
            validFromTxtLabel.setText(validFromStr);
            validToTxtLabel.setText(validToStr);
            draughtTxtLabel.setText(df.format(draught) + " meters");
            additionalTxtTitleLabel.setText("Entire region is Go");
            additionalTxtTitleLabel2.setText(" ");

            validFromTxtLabel.setEnabled(true);
            validToTxtLabel.setEnabled(true);
            draughtTxtLabel.setEnabled(true);
            return;

        }
        if (errorCodeOwn == 0) {
            statusLabel.setText("Success");
            statusLabel.setForeground(Color.GREEN);
            validFromTxtLabel.setText(validFromStr);
            validToTxtLabel.setText(validToStr);

            draughtTxtLabel.setText(df.format(draught) + " meters");
            additionalTxtTitleLabel.setText(" ");
            additionalTxtTitleLabel2.setText(" ");

            validFromTxtLabel.setEnabled(true);
            validToTxtLabel.setEnabled(true);
            draughtTxtLabel.setEnabled(true);
            return;
        }

    }

    private void requestCompletedMultiple(int errorCodeOwn, List<NogoPolygon> polygonsOwn, Date validFrom, Date validTo,
            Double draught, int id) {

        if (id == 0) {

            draught = -draught;

            // int draughtInt = (int) Math.round(draught);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM , HH:mm");

            String validFromStr = "";
            String validToStr = "";

            if (validFrom != null) {
                validFromStr = sdf.format(validFrom);
                validToStr = sdf.format(validTo);
            }

            if (errorCodeOwn == 17) {
                statusLabelSlider.setText("Failed");
                statusLabelSlider.setForeground(Color.RED);
                additionalTxtTitleLabelSlider.setText("No data for region");

                validFromTxtLabelSlider.setEnabled(false);
                validToTxtLabelSlider.setEnabled(false);
                draughtTxtLabelSlider.setEnabled(false);

                additionalTxtTitleLabel2Slider.setText("");
                return;
            }

            if (errorCodeOwn == 18) {
                statusLabelSlider.setText("Limited");
                statusLabelSlider.setForeground(Color.ORANGE);
                additionalTxtTitleLabelSlider.setText("No tide data available for region");
                additionalTxtTitleLabel2Slider.setText("");
                validFromTxtLabelSlider.setText("N/A");
                validToTxtLabelSlider.setText("N/A");
                draughtTxtLabelSlider.setText(df.format(draught) + " meters");
                return;
            }
            if (polygonsOwn.size() == 0) {
                statusLabelSlider.setText("Success");
                statusLabelSlider.setForeground(Color.GREEN);
                validFromTxtLabelSlider.setText(validFromStr);
                validToTxtLabelSlider.setText(validToStr);
                draughtTxtLabelSlider.setText(df.format(draught) + " meters");
                additionalTxtTitleLabelSlider.setText("Entire region is Go");
                additionalTxtTitleLabel2Slider.setText("");

                validFromTxtLabelSlider.setEnabled(true);
                validToTxtLabelSlider.setEnabled(true);
                draughtTxtLabelSlider.setEnabled(true);
                return;

            }
            if (errorCodeOwn == 0) {
                statusLabelSlider.setText("Success");
                statusLabelSlider.setForeground(Color.GREEN);
                validFromTxtLabelSlider.setText(validFromStr);
                validToTxtLabelSlider.setText(validToStr);

                draughtTxtLabelSlider.setText(df.format(draught) + " meters");
                additionalTxtTitleLabelSlider.setText("");
                additionalTxtTitleLabel2Slider.setText("");

                validFromTxtLabelSlider.setEnabled(true);
                validToTxtLabelSlider.setEnabled(true);
                draughtTxtLabelSlider.setEnabled(true);
                return;
            }
        }
    }

    public void initializeSlider(int count) {

        // slider = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);
        slider.setMaximum(count);

        // slider.addChangeListener(this);

    }

    public void setCompletedSlices(int completed, int total) {
        additionalTxtTitleLabel2Slider.setText("Completed: " + completed + " / " + total);

        if (completed == total) {
            slider.setEnabled(true);
            slider.setValue(0);
        } else {
            slider.setEnabled(false);
        }
    }

    /**
     * @return the slider
     */
    public JSlider getSlider() {
        return slider;
    }

    private void setToAndFromSliderOptions(Date validFrom, Date validTo) {

        String validFromStr = "";
        String validToStr = "";

        if (validFrom != null) {
            validFromStr = sdf.format(validFrom);
            validToStr = sdf.format(validTo);
        }

        validFromTxtLabelSlider.setText(validFromStr);
        validToTxtLabelSlider.setText(validToStr);
    }

    // ///HERE

    /**
     * Errorcode -1 means server experinced a timeout Errorcode 0 means everything went ok Errorcode 1 is the standby message
     * Errorcode 17 means no data Errorcode 18 means no tide data
     * 
     * @param nogoFailed
     * @param errorCode
     *            Own
     * @param errorCode
     *            Target
     * @param polygons
     *            own
     * @param polygons
     *            target
     * @param valid
     *            from
     * @param valid
     *            to
     * @param own
     *            draught
     * @param target
     *            draught
     * 
     * @param completed
     */
    public void requestCompletedMultiple(int errorCodeOwn, List<NogoPolygon> polygonsOwn, DateTime dateTime, DateTime dateTime2,
            Double draught, int id) {
        this.requestCompletedMultiple(errorCodeOwn, polygonsOwn, new Date(dateTime.getMillis()), new Date(dateTime2.getMillis()),
                draught, id);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();

        int index = (int) source.getValue();

        NoGoDataEntry entry = nogoHandler.getNogoData().get(index - 1);

        this.setToAndFromSliderOptions(new Date(entry.getValidFrom().getMillis()), new Date(entry.getValidTo().getMillis()));
        nogoHandler.showNoGoIndex(index);

    }

    /**
     * @param nogoHandler
     *            the nogoHandler to set
     */
    public void setNogoHandler(NogoHandler nogoHandler) {
        this.nogoHandler = nogoHandler;
    }

}
