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
package dk.dma.epd.ship.gui.fal;

import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.dma.epd.ship.EPDShip;

/**
 * Table model for RouteManagerDialog
 */
public class FALTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(FALTableModel.class);

    private static final String[] COLUMN_NAMES = { "Name", "Date", "Type" };

    public FALTableModel() {
        super();

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
        return EPDShip.getInstance().getFalManager().getFalReports().size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        switch (columnIndex) {
        case 0:
            return EPDShip.getInstance().getFalManager().getFalReports().get(rowIndex).getFalReportName();
        case 1:
            return EPDShip.getInstance().getFalManager().getFalReports().get(rowIndex).getFalform1()
                    .getDateAndTimeOfArrivalDepature();
        case 2:
            if (EPDShip.getInstance().getFalManager().getFalReports().get(rowIndex).getFalform1().isArrival()) {
                return "Arrival";
            }
            return "Depature";

        default:
            LOG.error("Unknown column " + columnIndex);
            return new String("");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

}
