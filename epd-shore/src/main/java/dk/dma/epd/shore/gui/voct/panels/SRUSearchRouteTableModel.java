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
package dk.dma.epd.shore.gui.voct.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.common.prototype.model.route.RoutesUpdateEvent;
import dk.dma.epd.common.prototype.model.voct.sardata.EffortAllocationData;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.voct.SRU;
import dk.dma.epd.shore.voct.SRUManager;
import dk.dma.epd.shore.voct.VOCTManager;

/**
 * Table model for SRUManagerDialog
 */
public class SRUSearchRouteTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SRUSearchRouteTableModel.class);

    private static final String[] COLUMN_NAMES = { "Name", "SearchPtn", "Visible", "Dynamic" };

    private SRUManager sruManager;
    private VOCTManager voctManager;

    private Map<Integer, JButton> buttons = new HashMap<Integer, JButton>();

    private SRUSearchPAtternButtonHandler handler;

    public SRUSearchRouteTableModel(SRUSearchPAtternButtonHandler handler, SRUManager sruManager, VOCTManager voctManager) {
        super();
        this.handler = handler;
        this.sruManager = sruManager;
        this.voctManager = voctManager;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_NAMES[column];
    }

    @Override
    public int getColumnCount() {
        return COLUMN_NAMES.length;
    }

    @Override
    public int getRowCount() {
        // return sruManager.getSRUCount();
        // return 0;
        return voctManager.getSarData().getEffortAllocationData().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        SRU sru = sruManager.getSRUs().get(rowIndex);
        EffortAllocationData effortAllocationData = voctManager.getSarData().getEffortAllocationData().get(rowIndex);
        switch (columnIndex) {
        case 0:
            return Formatter.formatString(sru.getName());
        case 1:
            return getCellButton(rowIndex);
        case 2:
            // return sru.isVisible();
            // return effortAllocationData.getSearchPatternRoute() !=
            if (effortAllocationData.getSearchPatternRoute() != null) {
                return effortAllocationData.getSearchPatternRoute().isVisible();
            } else {
                return false;
            }
        case 3:
            // return false;
            if (effortAllocationData.getSearchPatternRoute() != null) {
                return effortAllocationData.getSearchPatternRoute().isDynamic();
            } else {
                return false;
            }

        default:
            LOG.error("Unknown column " + columnIndex);
            return new String("");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        System.out.println("Set value at, aValue: " + aValue + " rowIndex: " + rowIndex + " columIndex: " + columnIndex);
        // SRU sru = sruManager.getSRUs().get(rowIndex);

        if (voctManager.getSarData().getEffortAllocationData().size() > rowIndex) {
            EffortAllocationData effortAllocationData = voctManager.getSarData().getEffortAllocationData().get(rowIndex);

            switch (columnIndex) {

            case 2:

                if (effortAllocationData.getSearchPatternRoute() != null) {
                    effortAllocationData.getSearchPatternRoute().setVisible((Boolean) aValue);
                    EPDShore.getInstance().getRouteManager().notifyListeners(RoutesUpdateEvent.ROUTE_VISIBILITY_CHANGED);
                    fireTableCellUpdated(rowIndex, columnIndex);
                } else {
                    break;
                }

                // toggle visibility of route

                // sru.setVisible((Boolean)aValue);
                // sruManager.toggleSRUVisiblity(rowIndex, (Boolean)aValue);
                // fireTableCellUpdated(rowIndex, columnIndex);
                break;
            case 3:
                // toggle dynamic

                if (effortAllocationData.getSearchPatternRoute() != null) {

                    boolean switchDynamic = (boolean) aValue;

                    if (switchDynamic) {
                        effortAllocationData.getSearchPatternRoute().switchToDynamic();
                    } else {
                        effortAllocationData.getSearchPatternRoute().switchToStatic();
                    }

                    EPDShore.getInstance().getRouteManager().notifyListeners(RoutesUpdateEvent.ROUTE_WAYPOINT_MOVED);
                    fireTableCellUpdated(rowIndex, columnIndex);
                } else {
                    break;
                }

                // sru.setVisible((Boolean)aValue);
                // sruManager.toggleSRUVisiblity(rowIndex, (Boolean)aValue);
                fireTableCellUpdated(rowIndex, columnIndex);
                break;
            default:
                break;
            }

        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {

        if (columnIndex == 1) {
            return true;
        }

        if (columnIndex == 2 || columnIndex == 3) {
            EffortAllocationData effortAllocationData = voctManager.getSarData().getEffortAllocationData().get(rowIndex);

            if (effortAllocationData.getSearchPatternRoute() != null) {
                return true;
            }

        }

        return false;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 1) {
            return JButton.class;
        } else {
            return getValueAt(0, columnIndex).getClass();
        }

    }

    private JButton getCellButton(int sruID) {
        JButton button = buttons.get(sruID);
        if (button == null) {
            button = createButton(sruID);
            buttons.put(sruID, button);
        }
        return button;

    }

    private JButton createButton(final int sruID) {
        final JButton button = new JButton("Create");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handler.buttonClicked(sruID);
            }
        });

        return button;
    }

    // @Override
    // public void fireTableChanged(TableModelEvent paramTableModelEvent) {
    //
    // Object[] arrayOfObject = this.listenerList.getListenerList();
    // for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
    // if (arrayOfObject[i] == TableModelListener.class) {
    // ((TableModelListener) arrayOfObject[(i + 1)]).tableChanged(paramTableModelEvent);
    // }
    // }
    // }

    public void fireTableDataChanged() {
//        buttons.clear();
        super.fireTableDataChanged();

    }

    public interface SRUSearchPAtternButtonHandler {
        void buttonClicked(int e);
    }

}
