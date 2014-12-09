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

    private Map<Long, JButton> buttons = new HashMap<Long, JButton>();

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
        return voctManager.getSarData().getEffortAllocationData().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        EffortAllocationData effortAllocationData = voctManager.getSarData().getEffortAllocationAsArray()[rowIndex];
        SRU sru = sruManager.getSRUs().get(effortAllocationData.getMmsi());
        
        switch (columnIndex) {
        case 0:
            return Formatter.formatString(sru.getName());
        case 1:
            return getCellButton(sru.getMmsi());
        case 2:
            if (effortAllocationData != null) {

                if (effortAllocationData.getSearchPatternRoute() != null) {
                    return effortAllocationData.getSearchPatternRoute().isVisible();
                }
            }
            return false;
        case 3:
            if (effortAllocationData != null) {
                if (effortAllocationData.getSearchPatternRoute() != null) {
                    return effortAllocationData.getSearchPatternRoute().isDynamic();
                } else {
                    return false;
                }
            }

        default:
            LOG.error("Unknown column " + columnIndex);
            return new String("");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//        System.out.println("Set value at, aValue: " + aValue + " rowIndex: " + rowIndex + " columIndex: " + columnIndex);
        // SRU sru = sruManager.getSRUs().get(rowIndex);

        if (voctManager.getSarData().getEffortAllocationData().size() > rowIndex) {
            EffortAllocationData effortAllocationData = voctManager.getSarData().getEffortAllocationAsArray()[rowIndex];

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
        SRU sru = sruManager.getSRUs().get(voctManager.getSarData().getEffortAllocationAsArray()[rowIndex].getMmsi());
        if (columnIndex == 1) {
            return true;
        }

        if (columnIndex == 2 || columnIndex == 3) {
            EffortAllocationData effortAllocationData = voctManager.getSarData().getEffortAllocationData().get(sru.getMmsi());

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

    private JButton getCellButton(long l) {
        JButton button = buttons.get(l);
        if (button == null) {
            button = createButton(l);
            buttons.put(l, button);
        }
        return button;

    }

    private JButton createButton(final long l) {
        final JButton button = new JButton("Create");

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handler.buttonClicked(l);
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
        // buttons.clear();
        super.fireTableDataChanged();

    }

    public interface SRUSearchPAtternButtonHandler {
        void buttonClicked(long e);
    }

}
