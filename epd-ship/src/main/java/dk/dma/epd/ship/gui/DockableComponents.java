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
import dk.dma.epd.ship.gui.component_panels.DockableComponentPanel;

public class DockableComponents {

    Map<String, PanelDockable> dmap;
    private CControl control;
    DockableFactory factory;
    
    private List<DockableComponentPanel> panels;

    private boolean locked;

    public DockableComponents(MainFrame mainFrame) {
        // Docks
        control = new CControl(mainFrame);
        control.setMissingStrategy(MissingCDockableStrategy.STORE);

        panels = mainFrame.getDockableComponentPanels();

        factory = new DockableFactory(panels);

        CContentArea contentArea = control.getContentArea();
        mainFrame.getContentPane().add(contentArea);

        String[] panelNames = new String[panels.size()];
        for (int x = 0; x < panels.size(); x++) {
            panelNames[x] = panels.get(x).getDockableComponentName();
        }
        control.addSingleDockableFactory(new PresetFilter<>(panelNames), factory);

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
        control.setTheme(ThemeMap.KEY_FLAT_THEME);
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

        for (DockableComponentPanel panel : panels) {
            if (panel.includeInPanelsMenu()) {
                JMenuItem m = createDockableMenuItem(panel.getDockableComponentName(), dmap.get(panel.getDockableComponentName()));
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
        // System.out.println(
        dockable.getContentPane().getComponent(0).setVisible(false);

        dockable.setVisible(false);
        control.removeDockable(dockable);
    }

    // If no layout file is present, create the basic layout!
    @SuppressWarnings("unused")
    private XElement createLayout() {
        // System.out.println("Create layout?");
        CControl aControl = new CControl();

        CGrid grid = new CGrid(aControl);
        int x = 0;
        for (DockableComponentPanel panel : panels) {
            if (panel.includeInDefaultLayout()) {
                PanelDockable pd = new PanelDockable(panel.getDockableComponentName(), (JPanel)panel);
                grid.add(x == 0 ? 0 : 90, x * 10, 90, 90, pd);
            }
        }        

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

        // control.intern().getController().getRelocator().setDragOnlyTitel(true);
        //
        // List<SingleCDockable> mdlist = control.getRegister()
        // .getSingleDockables();
        //
        // for (int i = 0; i < mdlist.size(); i++) {
        // PanelDockable dockable = (PanelDockable) mdlist.get(i);
        // dockable.setStackable(false);
        // dockable.setMinimizable(false);
        // dockable.setMaximizable(false);
        // }
        //
        // control.getContentArea().setMinimumAreaSize(new Dimension(0, 0));

        // Frames
        // BorderMod bridge = new BorderMod();
        // control.getController()
        // .getThemeManager()
        // .publish(Priority.CLIENT, DisplayerDockBorder.KIND,
        // ThemeManager.BORDER_MODIFIER_TYPE, bridge);

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
        control.setTheme(ThemeMap.KEY_FLAT_THEME);

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

        Map<String, DockableComponentPanel> panelLookup = new HashMap<>();

        /**
         * Constructor
         * @param panels
         */
        public DockableFactory(List<DockableComponentPanel> panels) {

            super();
            
            for (DockableComponentPanel panel : panels) {
                panelLookup.put(panel.getDockableComponentName(), panel);
            }
        }

        @Override
        public SingleCDockable createBackup(String id) {
            
            if (panelLookup.containsKey(id)) {
                return new PanelDockable(id, (JPanel)panelLookup.get(id));
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
