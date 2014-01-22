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
package dk.dma.epd.common.prototype;

import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.WEST;
import static java.awt.GridBagConstraints.CENTER;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.graphics.GraphicsUtil;

/**
 * Can be used to select an alternative home path for 
 * the EPD instance, i.e. either EPDShip or EPDShore.
 */
public final class HomePathDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(HomePathDialog.class);
    private Preferences prefs = Preferences.userNodeForPackage(getClass());
    private Path selectedPath;

    JComboBox<HomePath> pathSelector;
    JButton deleteButton = new JButton(EPD.res().getCachedImageIcon("images/trash.png"));
    JTextField txtName = new JTextField();
    JTextField txtPath = new JTextField();
    
    /**
     * Constructor
     */
    private HomePathDialog(Path defaultHomePath) {
        super((JFrame)null, "Select Home Path", true);
        
        selectedPath = defaultHomePath;
        Insets insets5  = new Insets(5, 5, 5, 5);
        int gridY = 0;
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        JPanel content = new JPanel(new GridBagLayout());
        setContentPane(content);
        
        pathSelector = loadPathSelector();
        pathSelector.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                updatePathSelection();
            }});
        deleteButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                deletePathSelection();
            }});
        updatePathSelection();
        content.add(pathSelector, new GridBagConstraints(0, gridY, 2, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        content.add(deleteButton, new GridBagConstraints(2, gridY++, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        GraphicsUtil.fixSize(txtName, 200);
        GraphicsUtil.fixSize(txtPath, 200);
        content.add(new JLabel("Name"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        content.add(txtName, new GridBagConstraints(1, gridY++, 2, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        content.add(new JLabel("Path"), new GridBagConstraints(0, gridY, 1, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        content.add(txtPath, new GridBagConstraints(1, gridY++, 2, 1, 0.0, 0.0, WEST, NONE, insets5, 0, 0));
        
        JButton okButton = new JButton("Select");
        okButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                HomePath path = addCurrentPath();
                selectedPath = path.path;
                savePathSelector();
                dispose();
            }});
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                dispose();
            }});
        
        JPanel btnPanel = new JPanel();
        btnPanel.add(okButton);
        btnPanel.add(cancelButton);
        content.add(btnPanel, new GridBagConstraints(0, gridY, 4, 1, 0.0, 0.0, CENTER, NONE, insets5, 0, 0));        
        
        pack();
        GraphicsUtil.centerWindow(this);
    }
    
    /**
     * The current path, given by the values of the
     * name and path fields, is added if it does 
     * not already exist
     */
    private HomePath addCurrentPath() {
        HomePath curPath = new HomePath(txtName.getText(), Paths.get(txtPath.getText()));
        boolean addPath = true;
        for (int index = 0; index < pathSelector.getItemCount(); index++) {
            HomePath path = (HomePath)pathSelector.getItemAt(index);
            
            // Update paths with same name unless it is the default path
            if (path.name.equals(curPath.name) && index == 0) {
                // Same name as the default path
                if (path.path.equals(curPath.path)) {
                    addPath = false;
                    curPath = path;
                    break;
                }
                
            } else if (path.name.equals(curPath.name)) {
                path.path = curPath.path;
                curPath = path;
                addPath = false;
                break;
            }
        }
        
        if (addPath) {
            pathSelector.addItem(curPath);
        }
        
        return curPath;
    }
    
    /**
     * Updates the UI based on the path selection
     */
    private void updatePathSelection() {
        boolean defaultPath = pathSelector.getSelectedIndex() == 0;
        deleteButton.setEnabled(!defaultPath);
        
        HomePath path = (HomePath) pathSelector.getSelectedItem();
        txtName.setText(path.name);
        txtPath.setText(path.path.toString());
    }
    
    /**
     * Deletes the currently selected path
     */
    private void deletePathSelection() {
        // Never delete the default path 
        if (pathSelector.getSelectedIndex() == 0) {
            return;
        }
        pathSelector.removeItemAt(pathSelector.getSelectedIndex());
        pathSelector.setSelectedIndex(0);
    }
    
    /**
     * Instantiates the path selector from the preferences
     * @return the path selector
     */
    private JComboBox<HomePath> loadPathSelector() {
        JComboBox<HomePath> paths = new JComboBox<>();
        paths.setEditable(false);
        
        // Add default path
        paths.addItem(new HomePath("Default", selectedPath));
        
        // Add custom paths
        for (int index = 1; true; index++) {
            String name = prefs.get("path-name-" + index, null);
            String path = prefs.get("path-path-" + index, null);
            if (name == null || path == null) {
                break;
            }
            paths.addItem(new HomePath(name, Paths.get(path)));
        }
        
        return paths;
    }
    
    /**
     * Saves the paths
     */
    private void savePathSelector() {
        // Remove old settings
        for (int index = 1; true; index++) {
            String name = prefs.get("path-name-" + index, null);
            if (name == null) {
                break;
            }
            prefs.remove("path-name-" + index);
            prefs.remove("path-path-" + index);
        }
        
        // Save new
        for (int i = 1; i < pathSelector.getItemCount(); i++) {
            HomePath path = (HomePath)pathSelector.getItemAt(i);
            prefs.put("path-name-" + i, path.name);
            prefs.put("path-path-" + i, path.path.toString());
        }
        
        try {
            prefs.sync();
        } catch (BackingStoreException e) {
            LOG.error("Failed saving paths", e);
        }
    }
    
    /**
     * Factory method for creating the dialog and 
     * returning the selected home path 
     * 
     * @param defaultHomePath the default home path
     * @return the chosen home path
     */
    public static Path determineHomePath(Path defaultHomePath) {
        HomePathDialog dialog = new HomePathDialog(defaultHomePath);
        dialog.setVisible(true);
        LOG.info("Selected home path: " + dialog.selectedPath);
        return dialog.selectedPath;
    }

    /**
     * Test method
     */
    public static void main(String... args) {
        determineHomePath(Paths.get(System.getProperty("user.home"), ".epd-ship"));
    }
    
    /**
     * Helper class that associates a name with a path
     */
    static class HomePath {
        String name;
        Path path;
        
        public HomePath(String name, Path path) {
            this.name = name;
            this.path = path;
        }
        
        @Override
        public String toString() {
            return String.format("<html>%s <small color=gray>(%s)</small></html>", name, path);
        }
    }
}
