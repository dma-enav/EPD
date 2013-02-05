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
package dk.dma.epd.shore.gui.fileselection;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Workspace filter used to filter what files are displayed in the JFileChooser,
 * we only display directors or .workspace files
 *
 *
 */
public class WorkspaceFileFilter extends FileFilter {

    /**
     * Set what files to display
     */
    @Override
    public boolean accept(File f) {
        return f.isDirectory() || f.getName().toLowerCase().endsWith(".workspace");
    }

    /**
     * Set description for the type of file that should be display
     */
    public String getDescription() {
        return "Workspace files";
    }

}
