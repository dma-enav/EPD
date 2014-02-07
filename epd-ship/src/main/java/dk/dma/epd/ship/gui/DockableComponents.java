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
package dk.dma.epd.ship.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.MissingCDockableStrategy;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.action.predefined.CBlank;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.theme.ThemeMap;
import bibliothek.util.filter.PresetFilter;
import bibliothek.util.xml.XElement;
import dk.dma.epd.ship.EPDShip;
import dk.dma.epd.ship.gui.component_panels.ActiveWaypointComponentPanel;
import dk.dma.epd.ship.gui.component_panels.AisComponentPanel;
import dk.dma.epd.ship.gui.component_panels.CursorComponentPanel;
import dk.dma.epd.ship.gui.component_panels.DynamicNoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.MultiSourcePntComponentPanel;
import dk.dma.epd.ship.gui.component_panels.PntComponentPanel;
import dk.dma.epd.ship.gui.component_panels.MSIComponentPanel;
import dk.dma.epd.ship.gui.component_panels.NoGoComponentPanel;
import dk.dma.epd.ship.gui.component_panels.OwnShipComponentPanel;
import dk.dma.epd.ship.gui.component_panels.ScaleComponentPanel;

public class DockableComponents {

    private static final String[] PANEL_NAMES = { "Chart", "Scale", "Own Ship",
            "GPS", "Cursor", "Active Waypoint", "MSI", "AIS Target",
            "Dynamic NoGo", "NoGo", "Mona Lisa Communication", "Resilient PNT" };
    Map<String, PanelDockable> dmap;
    private CControl control;
    DockableFactory factory;

    private ChartPanel chartPanel;

    private ScaleComponentPanel scalePanel;
    private OwnShipComponentPanel ownShipPanel;
    private PntComponentPanel gpsPanel;
    private CursorComponentPanel cursorPanel;
    private ActiveWaypointComponentPanel activeWaypointPanel;
    private MSIComponentPanel msiPanel;
    private AisComponentPanel aisPanel;
    private DynamicNoGoComponentPanel dynamicNoGoPanel;
    private NoGoComponentPanel nogoPanel;
    private MultiSourcePntComponentPanel msPntPanel;

    
    private boolean locked;

    public DockableComponents(MainFrame mainFrame) {
        // Docks
        control = new CControl(mainFrame);
        control.setMissingStrategy(MissingCDockableStrategy.STORE);

        chartPanel = mainFrame.getChartPanel();
        scalePanel = mainFrame.getScalePanel();
        ownShipPanel = mainFrame.getOwnShipPanel();
        gpsPanel = mainFrame.getGpsPanel();
        cursorPanel = mainFrame.getCursorPanel();
        activeWaypointPanel = mainFrame.getActiveWaypointPanel();
        msiPanel = mainFrame.getMsiComponentPanel();
        aisPanel = mainFrame.getAisComponentPanel();
        dynamicNoGoPanel = mainFrame.getDynamicNoGoPanel();
        nogoPanel = mainFrame.getNogoPanel();
        msPntPanel = mainFrame.getMsPntComponentPanel();

        factory = new DockableFactory(chartPanel, scalePanel, ownShipPanel, gpsPanel, cursorPanel, activeWaypointPanel, msiPanel,
                aisPanel, dynamicNoGoPanel, nogoPanel, msPntPanel);

        CContentArea contentArea = control.getContentArea();
        mainFrame.getContentPane().add(contentArea);

        control.addSingleDockableFactory(new PresetFilter<>(PANEL_NAMES),
                factory);

        mainFrame.add(control.getContentArea());

        // Load a layout
        Path home = EPDShip.getInstance().getHomePath();
        File layoutFile = home.resolve(EPDShip.class.getSimpleName() + ".xml")
                .toFile();
        if (layoutFile.exists()) {
            try {
                control.readXML(layoutFile);
            } catch (Exception ex) {
                System.out
                        .println("Error occured while loading layout, reverting to default");
                loadLayout(home.resolve("layout/static/default.xml").toString());
            }
        } else {
            loadLayout(home.resolve("layout/static/default.xml").toString());
            // control.readXML(createLayout());
        }

        control.intern().getController().getRelocator().setDragOnlyTitel(true);
//
        List<SingleCDockable> mdlist = control.getRegister()
                .getSingleDockables();
//
        for (int i = 0; i < mdlist.size(); i++) {
            PanelDockable dockable = (PanelDockable) mdlist.get(i);
            dockable.setStackable(false);
            dockable.setMinimizable(false);
            dockable.setMaximizable(false);
            dockable.setTitleIcon(new ImageIcon());
            dockable.setExternalizable(false);
            // dockable.putAction(CDockable.ACTION_KEY_MAXIMIZE, CBlank.BLANK);
            // dockable.putAction(CDockable.ACTION_KEY_MINIMIZE, CBlank.BLANK);
            dockable.putAction(CDockable.ACTION_KEY_EXTERNALIZE, CBlank.BLANK);
            // dockable.putAction(CDockable.ACTION_KEY_CLOSE, CBlank.BLANK);
            dockable.getContentPane().getComponent(0).setVisible(true);
        }
//
        control.getContentArea().setMinimumAreaSize(new Dimension(0, 0));
        control.setTheme( ThemeMap.KEY_FLAT_THEME );
    }

    public JMenu createDockableMenu() {
        JMenu menu = new JMenu("Panels");
        List<SingleCDockable> mdlist = control.getRegister()
                .getSingleDockables();
        dmap = new HashMap<>();
        for (int i = 0; i < mdlist.size(); i++) {
            PanelDockable dockable = (PanelDockable) mdlist.get(i);
            dmap.put(dockable.getName(), dockable);
        }

        for (String name : PANEL_NAMES) {
            if (!name.equals("Top") && !name.equals("Chart")) {

                JMenuItem m = createDockableMenuItem(name, dmap.get(name));
                menu.add(m);

            }
        }

        return menu;
    }

    public void toggleFrameLock() {

        // System.out.println("Toggle frame lock");

        List<SingleCDockable> mdlist = control.getRegister()
                .getSingleDockables();

        if (!locked) {

            for (int i = 0; i < mdlist.size(); i++) {
                PanelDockable dockable = (PanelDockable) mdlist.get(i);
                dockable.setTitleShown(false);
            }
            control.getContentArea().getCenter().setResizingEnabled(false);
            control.getContentArea().getCenter().setDividerSize(0);

            locked = true;

        } else {
            for (int i = 0; i < mdlist.size(); i++) {
                PanelDockable dockable = (PanelDockable) mdlist.get(i);
                dockable.setTitleShown(true);
            }

            control.getContentArea().getCenter().setResizingEnabled(true);
            control.getContentArea().getCenter().setDividerSize(2);

            locked = false;
        }

    }

    public void lock() {

        // System.out.println("Locking!");

        List<SingleCDockable> mdlist = control.getRegister()
                .getSingleDockables();

        for (int i = 0; i < mdlist.size(); i++) {
            PanelDockable dockable = (PanelDockable) mdlist.get(i);
            dockable.setTitleShown(false);
        }
        control.getContentArea().getCenter().setResizingEnabled(false);
        control.getContentArea().getCenter().setDividerSize(0);

        locked = true;
        // System.out.println("Locked");
    }

    public void saveLayout() {
        try {
            try {
                Path home = EPDShip.getInstance().getHomePath();
                File f = home.resolve(EPDShip.class.getSimpleName() + ".xml")
                        .toFile();
                control.writeXML(f);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            control.destroy();
        } catch (Exception e) {
            System.out.println("Error occured due to corrupt layout");
        }
    }

    private JMenuItem createDockableMenuItem(final String name,
            PanelDockable dockable) {
        JCheckBoxMenuItem m = new JCheckBoxMenuItem(name);
        m.setSelected(dockable != null && dockable.isVisible());
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBoxMenuItem m = (JCheckBoxMenuItem) e.getSource();
                if (m.isSelected()) {
                    PanelDockable dockable = dmap.get(name);
                    // System.out.println(dockable);
                    if (dockable != null) {
                        doOpen(dockable);
                    } else {
                        PanelDockable newDockable = (PanelDockable) factory
                                .createBackup(name);
                        dmap.put(newDockable.getName(), newDockable);
                        newDockable.setStackable(false);
                        newDockable.setMinimizable(false);
                        newDockable.setMaximizable(false);
                        doOpen(newDockable);
                    }

                } else {
                    PanelDockable dockable = getMyDockableByName(name);
                    if (dockable != null) {
                        doClose(dockable);
                    }
                }
            }
        });
        return m;
    }

    public boolean isDockVisible(String name) {
        PanelDockable dockable = dmap.get(name);

        if (dockable != null) {

            return dockable.isVisible();

        } else {
            return false;
        }
    }

    public void openDock(String name) {

        PanelDockable dockable = dmap.get(name);
        // System.out.println(dockable);
        if (dockable != null) {
            doOpen(dockable);
        } else {
            PanelDockable newDockable = (PanelDockable) factory
                    .createBackup(name);
            dmap.put(newDockable.getName(), newDockable);
            newDockable.setStackable(false);
            newDockable.setMinimizable(false);
            newDockable.setMaximizable(false);
            doOpen(newDockable);
        }

    }

    PanelDockable getMyDockableByName(String name) {
        return (PanelDockable) control.getSingleDockable(name);
    }

    void doOpen(PanelDockable dockable) {

        if (locked) {
            dockable.setTitleShown(false);
        } else {
            dockable.setTitleShown(true);
        }

        control.addDockable(dockable);

        dockable.setDefaultLocation(ExtendedMode.NORMALIZED, CLocation.base()
                .normalEast(0.5));

        dockable.setVisible(true);
        
        dockable.getContentPane().getComponent(0).setVisible(true);
    }

    void doClose(PanelDockable dockable) {
//        System.out.println(
                dockable.getContentPane().getComponent(0).setVisible(false);
        
        dockable.setVisible(false);
        control.removeDockable(dockable);
    }

    // If no layout file is present, create the basic layout!
    @SuppressWarnings("unused")
    private XElement createLayout() {
        // System.out.println("Create layout?");
        CControl aControl = new CControl();

        PanelDockable chartDock = new PanelDockable("Chart", chartPanel);
        // PanelDockable topDock = new PanelDockable("Top", topPanel);
        PanelDockable scaleDock = new PanelDockable("Scale", scalePanel);
        PanelDockable ownShipDock = new PanelDockable("Own Ship", ownShipPanel);
        PanelDockable gpsDock = new PanelDockable("GPS", gpsPanel);
        PanelDockable cursorDock = new PanelDockable("Cursor", cursorPanel);
        PanelDockable activeWaypointDock = new PanelDockable("Active Waypoint",
                activeWaypointPanel);
        PanelDockable msiDock = new PanelDockable("MSI", msiPanel);
        PanelDockable msPntDock = new PanelDockable("Resilient PNT", msPntPanel);

        // PanelDockable aisDock = new PanelDockable("AIS Target", aisPanel);

        CGrid grid = new CGrid(aControl);
        // grid.add(0, 0, 100, 3, topDock);
        grid.add(0, 3, 90, 97, chartDock);
        grid.add(90, 3, 10, 10, scaleDock);
        grid.add(90, 13, 10, 10, ownShipDock);
        grid.add(90, 23, 10, 10, gpsDock);
        grid.add(90, 33, 10, 10, cursorDock);
        grid.add(90, 43, 10, 10, activeWaypointDock);
        grid.add(90, 53, 10, 10, msiDock);
        grid.add(90, 25, 10, 10, msPntDock);
        // grid.add(90, 63, 10, 10, aisDock);

        aControl.getContentArea().setMinimumAreaSize(new Dimension(0, 0));

        // Deploy the grid content
        aControl.getContentArea().deploy(grid);

        XElement root = new XElement("root");
        aControl.writeXML(root);
        aControl.destroy();
        return root;
    }

    public void loadLayout(String path) {
        // System.out.println("Loading " + path);
        // locked = false;

        // Load a layout
        File layoutFile = new File(path);
        if (layoutFile.exists()) {
            try {
                control.readXML(layoutFile);
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }

        // System.out.println(locked);
        // toggleFrameLock();

//        control.intern().getController().getRelocator().setDragOnlyTitel(true);
//
//        List<SingleCDockable> mdlist = control.getRegister()
//                .getSingleDockables();
//
//        for (int i = 0; i < mdlist.size(); i++) {
//            PanelDockable dockable = (PanelDockable) mdlist.get(i);
//            dockable.setStackable(false);
//            dockable.setMinimizable(false);
//            dockable.setMaximizable(false);
//        }
//
//        control.getContentArea().setMinimumAreaSize(new Dimension(0, 0));

        // Frames
//        BorderMod bridge = new BorderMod();
//        control.getController()
//                .getThemeManager()
//                .publish(Priority.CLIENT, DisplayerDockBorder.KIND,
//                        ThemeManager.BORDER_MODIFIER_TYPE, bridge);
        
        
        control.intern().getController().getRelocator().setDragOnlyTitel(true);
        //
                List<SingleCDockable> mdlist = control.getRegister()
                        .getSingleDockables();
        
                for (int i = 0; i < mdlist.size(); i++) {
                    PanelDockable dockable = (PanelDockable) mdlist.get(i);
                    dockable.setStackable(false);
                    dockable.setMinimizable(false);
                    dockable.setMaximizable(false);
                    dockable.setTitleIcon(new ImageIcon());
                    dockable.setExternalizable(false);
                    // dockable.putAction(CDockable.ACTION_KEY_MAXIMIZE, CBlank.BLANK);
                    // dockable.putAction(CDockable.ACTION_KEY_MINIMIZE, CBlank.BLANK);
                    dockable.putAction(CDockable.ACTION_KEY_EXTERNALIZE, CBlank.BLANK);
                    // dockable.putAction(CDockable.ACTION_KEY_CLOSE, CBlank.BLANK);
                    dockable.getContentPane().getComponent(0).setVisible(true);
                }
        
                control.getContentArea().setMinimumAreaSize(new Dimension(0, 0));
                control.setTheme( ThemeMap.KEY_FLAT_THEME );

        lock();
        // System.out.println(locked);
    }

    public void saveLayout(String name) {

        try {
            Path home = EPDShip.getInstance().getHomePath();
            Path layoutFolder = home.resolve("layout");
            Files.createDirectories(layoutFolder);
            File f = layoutFolder.resolve(name + ".xml").toFile();
            control.writeXML(f);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isLocked() {
        return locked;
    }

    // Create the dockables from a xml file
    private static class DockableFactory implements SingleCDockableFactory {

        ChartPanel chartPanel;
        // TopPanel topPanel;
        ScaleComponentPanel scalePanel;
        OwnShipComponentPanel ownShipPanel;
        PntComponentPanel gpsPanel;
        CursorComponentPanel cursorPanel;
        ActiveWaypointComponentPanel activeWaypointPanel;
        MSIComponentPanel msiPanel;
        AisComponentPanel aisPanel;
        DynamicNoGoComponentPanel dynamicNoGoPanel;
        NoGoComponentPanel nogoPanel;
        MultiSourcePntComponentPanel msPntPanel;

        public DockableFactory(ChartPanel chartPanel,
                ScaleComponentPanel scalePanel,
                OwnShipComponentPanel ownShipPanel, PntComponentPanel gpsPanel,
                CursorComponentPanel cursorPanel,
                ActiveWaypointComponentPanel activeWaypointPanel,
                MSIComponentPanel msiPanel,
                AisComponentPanel aisPanel,
                DynamicNoGoComponentPanel dynamicNoGoPanel,
                NoGoComponentPanel nogoPanel,
                MultiSourcePntComponentPanel msPntPanel) {

            super();

            this.chartPanel = chartPanel;
            // this.topPanel = topPanel;
            this.scalePanel = scalePanel;
            this.ownShipPanel = ownShipPanel;
            this.gpsPanel = gpsPanel;
            this.cursorPanel = cursorPanel;
            this.activeWaypointPanel = activeWaypointPanel;
            this.msiPanel = msiPanel;
            this.aisPanel = aisPanel;
            this.dynamicNoGoPanel = dynamicNoGoPanel;
            this.nogoPanel = nogoPanel;
            this.msPntPanel = msPntPanel;

        }

        @Override
        public SingleCDockable createBackup(String id) {
            if (id.equals("Chart")) {
                return new PanelDockable(id, chartPanel);
            }

            // if (id.equals("Top")) {
            // return new PanelDockable(id, topPanel);
            // }
            if (id.equals("Scale")) {
                return new PanelDockable(id, scalePanel);
            }

            if (id.equals("Own Ship")) {
                return new PanelDockable(id, ownShipPanel);
            }

            if (id.equals("GPS")) {
                return new PanelDockable(id, gpsPanel);
            }

            if (id.equals("Cursor")) {
                return new PanelDockable(id, cursorPanel);
            }
            if (id.equals("Active Waypoint")) {
                return new PanelDockable(id, activeWaypointPanel);
            }
            if (id.equals("MSI")) {
                return new PanelDockable(id, msiPanel);
            }
            if (id.equals("AIS Target")) {
                return new PanelDockable(id, aisPanel);
            }

            if (id.equals("Dynamic NoGo")) {
                return new PanelDockable(id, dynamicNoGoPanel);
            }
            if (id.equals("NoGo")) {
                return new PanelDockable(id, nogoPanel);
            }
            if (id.equals("Resilient PNT")) {
                return new PanelDockable(id, msPntPanel);
            }

            return new PanelDockable(id, new JPanel());

        }
    }

    private static class PanelDockable extends DefaultSingleCDockable {

        private final String name;

        public PanelDockable(String name, JPanel panel) {
            super(name);
            this.name = name;
            setTitleText(name);

            add(panel);

        }

        public String getName() {
            return name;
        }
    }
}
