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
package dk.dma.epd.shore.gui.voct;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;
import dk.dma.epd.shore.gui.utils.ComponentFrame;
import dk.dma.epd.shore.gui.views.MainFrame;
import dk.dma.epd.shore.voct.SRU;
import dk.dma.epd.shore.voct.SRUManager;

/**
 * Route manager dialog
 */
public class SRUManagerDialog extends ComponentFrame implements ActionListener,
        ListSelectionListener, TableModelListener, MouseListener {

    private static final long serialVersionUID = 1L;

    protected SRUManager sruManager;

    // private JButton propertiesBtn;
    // private JButton zoomToBtn;
    // private JButton reverseCopyBtn;
    // private JButton deleteBtn;
    // private JButton exportBtn;
    // private JButton importBtn;
    // private JButton closeBtn;
    private JLabel propertiesBtn;
    private JLabel zoomToBtn;
    private JLabel deleteBtn;
    private JLabel importBtn;
    private JLabel closeBtn;
    private JLabel exportAllBtn;
    private JLabel copyBtn;
    
    
    private JLabel addNewSRUBtn;

    private JScrollPane sruScrollPane;
    private JTable sruTable;
    private SRUTableModel sruTableModel;
    private ListSelectionModel sruSelectionModel;

    JFrame parent;

    private JPanel topBar;
    private static int moveHandlerHeight = 18;
    private JLabel moveHandler;
    private JPanel masterPanel;
    private JPanel contentPanel;
    private Color backgroundColor = new Color(83, 83, 83);
    private MainFrame mainFrame;

    Border paddingLeft = BorderFactory.createMatteBorder(0, 8, 0, 0, new Color(
            65, 65, 65));
    Border paddingBottom = BorderFactory.createMatteBorder(0, 0, 5, 0,
            new Color(83, 83, 83));
    Border notificationPadding = BorderFactory.createCompoundBorder(
            paddingBottom, paddingLeft);
    Border notificationsIndicatorImportant = BorderFactory.createMatteBorder(0,
            0, 0, 10, new Color(206, 120, 120));
    Border paddingLeftPressed = BorderFactory.createMatteBorder(0, 8, 0, 0,
            new Color(45, 45, 45));
    Border notificationPaddingPressed = BorderFactory.createCompoundBorder(
            paddingBottom, paddingLeftPressed);

    public SRUManagerDialog(JFrame parent) {
        super("SRU Manager", false, true, false, false);
        this.parent = parent;
        sruManager = EPDShore.getSRUManager();

        // Strip off window looks
        setRootPaneCheckingEnabled(false);
        ((javax.swing.plaf.basic.BasicInternalFrameUI) this.getUI())
                .setNorthPane(null);
        this.setBorder(null);

        // Map tools
        topBar = new JPanel(new GridLayout(1, 3));
        topBar.setPreferredSize(new Dimension(500, moveHandlerHeight));
        topBar.setOpaque(true);
        topBar.setBackground(Color.DARK_GRAY);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(
                30, 30, 30)));

        // Placeholder - for now
        topBar.add(new JLabel());

        // Movehandler/Title dragable)
        moveHandler = new JLabel("Search Rescue Units", SwingConstants.CENTER);
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setForeground(new Color(200, 200, 200));
        // actions = moveHandler.getListeners(MouseMotionListener.class);
        topBar.add(moveHandler);

        // The tools (minimize, maximize and close)
        JPanel windowToolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
                0, 0));
        windowToolsPanel.setOpaque(false);
        windowToolsPanel.setPreferredSize(new Dimension(60, 50));

        JLabel close = new JLabel(new ImageIcon("images/window/close.png"));
        close.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                toggleVisibility();
            }
        });

        close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
        windowToolsPanel.add(close);
        topBar.add(windowToolsPanel);

        contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(900,
                600 - moveHandlerHeight));
        contentPanel.setSize(new Dimension(900, 600 - moveHandlerHeight));
        contentPanel.setBackground(backgroundColor);

        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(topBar, BorderLayout.NORTH);
        masterPanel.add(contentPanel, BorderLayout.CENTER);

        masterPanel.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45,
                        45)));

        getContentPane().add(masterPanel);

        setSize(600, 400);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        setLocation(10, 10);

        
        propertiesBtn = new JLabel("Properties");
        GuiStyler.styleButton(propertiesBtn);
         propertiesBtn.addMouseListener(this);
         
         
        zoomToBtn = new JLabel("Zoom to");
        GuiStyler.styleButton(zoomToBtn);
        zoomToBtn.setEnabled(false);
        
        
        deleteBtn = new JLabel("Remove Selected");
        GuiStyler.styleButton(deleteBtn);
        // exportBtn.addActionListener(this);
        exportAllBtn = new JLabel("Export All");
        GuiStyler.styleButton(exportAllBtn);
        // exportAllBtn.addActionListener(this);
        importBtn = new JLabel("Import");
        GuiStyler.styleButton(importBtn);
        // importBtn.addActionListener(this);
        closeBtn = new JLabel("Close");
        GuiStyler.styleButton(closeBtn);
        // metocBtn.addActionListener(this);
        copyBtn = new JLabel("Generate Search Pattern");
        GuiStyler.styleButton(copyBtn);
        // copyBtn.addActionListener(this);

        
        JLabel lblEffortAllocation = new JLabel("Effort Allocation");
        GuiStyler.styleButton(lblEffortAllocation);
        lblEffortAllocation.setEnabled(false);
        
        addNewSRUBtn = new JLabel("Add new SRU");
        GuiStyler.styleButton(addNewSRUBtn);
//        lblAddNewSru.setEnabled(false);
        
        
        DefaultTableModel model = new DefaultTableModel(30, 3);

        sruTable = new JTable(model) {
            private static final long serialVersionUID = 1L;

            public Component prepareRenderer(TableCellRenderer renderer,
                    int Index_row, int Index_col) {
                Component comp = super.prepareRenderer(renderer, Index_row,
                        Index_col);
                if (Index_row % 2 == 0) {
                    comp.setBackground(new Color(49, 49, 49));
                } else {
                    comp.setBackground(new Color(65, 65, 65));
                }

                if (isCellSelected(Index_row, Index_col)) {
                    comp.setForeground(Color.white);
                    comp.setBackground(new Color(85, 85, 85));
                }

                return comp;
            }
        };

        // routeTable.setTableHeader(null);

        sruTable.setBorder(new EmptyBorder(0, 0, 0, 0));
        // routeTable.setIntercellSpacing(new Dimension(0, 0));
        sruTable.setBackground(new Color(49, 49, 49));
        sruTable.setShowVerticalLines(false);
        sruTable.setShowHorizontalLines(false);
        sruTable.setShowGrid(false);
        sruTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        sruTable.setForeground(Color.white);
        sruTable.setSelectionForeground(Color.gray);
        // routeTable.setRowHeight(20);
        sruTable.setFocusable(false);
        // routeTable.setAutoResizeMode(0);

        sruTableModel = new SRUTableModel(sruManager);
        sruTableModel.addTableModelListener(this);

        sruTable.setShowHorizontalLines(false);
        sruTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sruScrollPane = new JScrollPane(sruTable);
        sruScrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        sruScrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sruTable.setFillsViewportHeight(true);

        sruScrollPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
                new Color(30, 30, 30)));

        // TODO: Comment this line when using WindowBuilder
        sruTable.setModel(sruTableModel);
        for (int i = 0; i < 4; i++) {
            
            if (i == 0){
                sruTable.getColumnModel().getColumn(i).setPreferredWidth(75);
            }
            if (i == 1){
                sruTable.getColumnModel().getColumn(i).setPreferredWidth(50);
            }
            if (i == 2){
                sruTable.getColumnModel().getColumn(i).setPreferredWidth(50);
            }
            if (i == 3){
                sruTable.getColumnModel().getColumn(i).setPreferredWidth(25);
            }
            
        }
        sruSelectionModel = sruTable.getSelectionModel();
        sruSelectionModel.addListSelectionListener(this);
        sruTable.setSelectionModel(sruSelectionModel);
        sruTable.addMouseListener(this);
        


        GroupLayout groupLayout = new GroupLayout(contentPanel);
        groupLayout.setHorizontalGroup(
            groupLayout.createParallelGroup(Alignment.TRAILING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(sruScrollPane, GroupLayout.DEFAULT_SIZE, 441, Short.MAX_VALUE)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(closeBtn, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(zoomToBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(propertiesBtn, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                    .addComponent(copyBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
                                    .addComponent(deleteBtn, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(exportAllBtn, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                    .addComponent(importBtn, GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                                .addComponent(addNewSRUBtn, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE)))
                        .addComponent(lblEffortAllocation, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 131, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap())
        );
        groupLayout.setVerticalGroup(
            groupLayout.createParallelGroup(Alignment.LEADING)
                .addGroup(groupLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                            .addComponent(propertiesBtn)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(zoomToBtn)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(copyBtn)
                            .addGap(26)
                            .addComponent(deleteBtn)
                            .addGap(47)
                            .addComponent(exportAllBtn)
                            .addPreferredGap(ComponentPlacement.RELATED)
                            .addComponent(importBtn)
                            .addPreferredGap(ComponentPlacement.RELATED, 89, Short.MAX_VALUE)
                            .addComponent(addNewSRUBtn)
                            .addGap(10))
                        .addComponent(sruScrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE))
                    .addGap(8)
                    .addComponent(lblEffortAllocation)
                    .addPreferredGap(ComponentPlacement.RELATED)
                    .addComponent(closeBtn)
                    .addContainerGap())
        );

        contentPanel.setLayout(groupLayout);

        sruSelectionModel.setSelectionInterval(0, 0);

        updateTable();
        updateButtons();

        addMouseListeners();

        
    }

    private void updateButtons() {
        boolean routeSelected = sruTable.getSelectedRow() >= 0;

        // LOG.info("---------------------------------------");
        // LOG.info("routeSelected: " + routeSelected);
        // LOG.info("routeTable.getSelectedRow(): " +
        // routeTable.getSelectedRow());
        // LOG.info("activeSelected: " + activeSelected);
        // LOG.info("routeManager.isRouteActive(): " +
        // routeManager.isRouteActive());
        // LOG.info("activeRoute: " + routeManager.getActiveRouteIndex());
        // LOG.info("\n\n");

        propertiesBtn.setEnabled(routeSelected);
        zoomToBtn.setEnabled(routeSelected);
        copyBtn.setEnabled(routeSelected);
    }

    private void updateTable() {
        int selectedRow = sruTable.getSelectedRow();
        // Update routeTable
        sruTableModel.fireTableDataChanged();
        // routeTable.doLayout();
        updateButtons();
        if (selectedRow >= 0 && selectedRow < sruTable.getRowCount()) {
            sruSelectionModel.setSelectionInterval(selectedRow, selectedRow);
        }
    }

    private void close() {
        this.setVisible(false);
    }

    private void zoomTo() {

        SRU selectedroute = sruManager.getSRUs(sruTable.getSelectedRow());

//        if (EPDShore.getMainFrame().getActiveMapWindow() != null) {
//            EPDShore.getMainFrame()
//                    .getActiveMapWindow()
//                    .getChartPanel()
//                    .zoomToPoint(
//                            selectedroute.getWaypoints().getFirst().getPos());
//        } else if (EPDShore.getMainFrame().getMapWindows().size() > 0) {
//            EPDShore.getMainFrame()
//                    .getMapWindows()
//                    .get(0)
//                    .getChartPanel()
//                    .zoomToPoint(
//                            selectedroute.getWaypoints().getFirst().getPos());
//        }
        
        
        
    }

    private void copy() {
//        if (routeTable.getSelectedRow() >= 0) {
//            routeManager.routeCopy(routeTable.getSelectedRow());
//            updateTable();
//        }
    }



    // private void metocProperties() {
    // int i = routeTable.getSelectedRow();
    // if (i >= 0) {
    // RouteMetocDialog routeMetocDialog = new RouteMetocDialog((Window) parent,
    // routeManager, i);
    // routeMetocDialog.setVisible(true);
    // routeManager.notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
    // }
    // }

    private void delete() {

        int i = sruTable.getSelectedRow();
        
        if (i >= 0){
            sruManager.removeSRU(sruTable.getSelectedRow());    
        }
        
        
        
    }

    private void exportToFile(int routeId) {
//        if (routeId < 0) {
//            return;
//        }
//
//        Route route = routeManager.getRoute(routeId);
//
//        JFileChooser fc = new JFileChooser(System.getProperty("user.dir")
//                + "/routes/");
//        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        fc.setMultiSelectionEnabled(false);
//
//        fc.addChoosableFileFilter(new FileNameExtensionFilter(
//                "Simple route text format", "txt", "TXT"));
//        fc.setAcceptAllFileFilterUsed(true);
//        File f = new File(route.getName() + ".txt");
//        fc.setSelectedFile(f);
//
//        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
//            return;
//        }
//        File file = fc.getSelectedFile();
//
//        if (!fc.getSelectedFile().toString().contains(".txt")) {
//            file = new File(fc.getSelectedFile().getPath() + ".txt");
//        }
//
//        if (file.exists()) {
//            if (JOptionPane.showConfirmDialog(this, "File exists. Overwrite?",
//                    "Overwrite?", JOptionPane.YES_NO_OPTION) != 0) {
//                exportToFile(routeId);
//                return;
//            }
//        }
//
//        if (!RouteLoader.saveSimple(route, file)) {
//            JOptionPane.showMessageDialog(EPDShore.getMainFrame(),
//                    "Route save error", "Route not saved",
//                    JOptionPane.ERROR_MESSAGE);
//        }

    }

    private void importFromFile() {
//        // Get filename from dialog
//        JFileChooser fc = new JFileChooser(System.getProperty("user.dir")
//                + "/routes");
//        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        fc.setMultiSelectionEnabled(true);
//        fc.addChoosableFileFilter(new FileNameExtensionFilter(
//                "Simple route text format", "txt", "TXT"));
//        fc.addChoosableFileFilter(new FileNameExtensionFilter(
//                "ECDIS900 V3 route", "rou", "ROU"));
//        fc.addChoosableFileFilter(new FileNameExtensionFilter(
//                "Navisailor 3000 route", "rt3", "RT3"));
//        fc.setAcceptAllFileFilterUsed(true);
//
//        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
//            return;
//        }
//
//        for (File file : fc.getSelectedFiles()) {
//            try {
//                routeManager.loadFromFile(file);
//            } catch (RouteLoadException e) {
//                JOptionPane.showMessageDialog(this, e.getMessage() + ": "
//                        + file.getName(), "Route load error",
//                        JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//        }
//
//        updateTable();
//        routeSelectionModel.setSelectionInterval(routeTable.getRowCount() - 1,
//                routeTable.getRowCount() - 1);
    }



    private void exportAllToFile() {
        for (int i = 0; i < sruTable.getRowCount(); i++) {
            exportToFile(i);
        }
    }

    public void addMouseListeners() {

        closeBtn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                close();
            }
        });

        propertiesBtn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                properties();
            }
        });

        zoomToBtn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                zoomTo();
            }
        });

        copyBtn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                copy();
            }
        });

        deleteBtn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                delete();
            }
        });

        exportAllBtn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                exportAllToFile();
            }
        });

        importBtn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                importFromFile();
            }
        });
        
        
        addNewSRUBtn.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                addNewSRU();
            }
        });
    }

    
    public void properties(){
        
        int i = sruTable.getSelectedRow();
        
        if (i >=0){
            SRUAddEditDialog dialog = new SRUAddEditDialog(sruManager, i);
            dialog.setVisible(true);
        }
        
//        SRUAddEditDialog dialog = new SRUAddEditDialog(sruManager);
        
        
        
     // int i = routeTable.getSelectedRow();
        // if (i >= 0) {
        // RouteMetocDialog routeMetocDialog = new RouteMetocDialog((Window) parent,
        // routeManager, i);
        // routeMetocDialog.setVisible(true);
        // routeManager.notifyListeners(RoutesUpdateEvent.METOC_SETTINGS_CHANGED);
        
    }
    
    private void addNewSRU(){
        SRUAddEditDialog dialog = new SRUAddEditDialog(sruManager);
        dialog.setVisible(true);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            properties();
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        updateButtons();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (e.getColumn() == 2) {
            // Visibility has changed
//            routeManager
//                    .notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
            ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this,
                    mainFrame);
            topBar.addMouseListener(mml);
            topBar.addMouseMotionListener(mml);
        }
    }

    /**
     * Change the visiblity
     */
    public void toggleVisibility() {
        setVisible(!this.isVisible());
    }
}
