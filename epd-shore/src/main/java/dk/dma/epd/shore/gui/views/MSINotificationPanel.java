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
package dk.dma.epd.shore.gui.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.gui.msi.MsiTableModel;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;
import dk.frv.enav.common.xml.msi.MsiMessage;

public class MSINotificationPanel extends JPanel {

    private static final long serialVersionUID = 1L;
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

    private JTable msiTable;

    private JPanel pane_3;
    private JScrollPane scrollPane_1;

    private Color backgroundColor = new Color(83, 83, 83);
    private JTextPane area = new JTextPane();
    private StringBuilder doc = new StringBuilder();
    private JLabel but_read;
    private JLabel but_goto;
    private JLabel but_delete;

    private JPanel masterPanel;

    private DefaultListSelectionModel values;
    private JPanel headerPanel;

    private JScrollPane leftScrollPane;

    private MsiTableModel msiTableModel;
    private int currentSelection = -1;

    private JPanel rightPanel;
    private JPanel leftPanel;
    private MsiHandler msiHandler;

    public MSINotificationPanel() {
        GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
        gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_2.gridx = 1;
        gbc_scrollPane_2.gridy = 1;
        gbc_scrollPane_2.gridheight = 1;
        gbc_scrollPane_2.insets = new Insets(-10, 0, 0, 0);
        String[] colHeadings = { "ID", "Title" };
        DefaultTableModel model = new DefaultTableModel(30, colHeadings.length);
        model.setColumnIdentifiers(colHeadings);

        // Right
        GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
        gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_3.gridx = 2;
        gbc_scrollPane_3.gridy = 0;
        gbc_scrollPane_3.insets = new Insets(10, 0, 0, 0);

        GridBagConstraints gbc_scrollPane_1_1 = new GridBagConstraints();
        gbc_scrollPane_1_1.fill = GridBagConstraints.BOTH;
        gbc_scrollPane_1_1.gridx = 2;
        gbc_scrollPane_1_1.gridy = 1;
        setLayout(null);

        masterPanel = new JPanel();
        masterPanel.setBounds(0, 0, 800, 600);
        masterPanel.setBackground(backgroundColor);

        // masterPanel.add(mapPanel, BorderLayout.NORTH);

        masterPanel.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45,
                        45)));

        masterPanel.setLayout(null);
        this.add(masterPanel);

        leftPanel = new JPanel();
        leftPanel.setBounds(0, 0, 345, 600);
        leftPanel.setBackground(backgroundColor);
        masterPanel.add(leftPanel);
        leftPanel.setLayout(null);

        leftScrollPane = new JScrollPane();
        leftScrollPane.setBounds(0, 20, 345, 560);
        leftPanel.add(leftScrollPane);
        leftScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        leftScrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Center
        // MARKER GOES HERE
        // headerPanel = new JPanel();
        // headerPanel.setBounds(0, 0, 300, 10);
        // leftPanel.add(headerPanel);
        // headerPanel.setBackground(new Color(39, 39, 39));
        // headerPanel.setPreferredSize(new Dimension(300, 10));
        // headerPanel.setLayout(null);
        //

        headerPanel = new JPanel(new FlowLayout(0));
        headerPanel.setBackground(new Color(39, 39, 39));
        // headerPanel.setPreferredSize(new Dimension(400, 15));
        headerPanel.setBounds(0, 0, 400, 20);
        // headerPanel.setSize(new Dimension(300, 10));
        ((FlowLayout) headerPanel.getLayout()).setHgap(0);
        leftPanel.add(headerPanel);

        // msiTable = new JTable();

        msiTable = new JTable(model) {
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

                // Paint based on awk
                if (msiTableModel != null) {
                    if (msiTableModel.isAwk(Index_row) && Index_col == 0) {
                        comp.setForeground(new Color(130, 165, 80));
                    } else if (!msiTableModel.isAwk(Index_row)
                            && Index_col == 0) {
                        comp.setForeground(new Color(165, 80, 80));
                    }
                }

                return comp;
            }

        };

        msiTable.setTableHeader(null);
        msiTable.setBorder(new EmptyBorder(0, 0, 0, 0));
        msiTable.setIntercellSpacing(new Dimension(0, 0));
        msiTable.setBackground(new Color(49, 49, 49));
        msiTable.setShowVerticalLines(false);
        msiTable.setShowHorizontalLines(false);
        msiTable.setShowGrid(false);
        msiTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        msiTable.setForeground(Color.white);
        msiTable.setSelectionForeground(Color.gray);
        msiTable.setRowHeight(20);
        msiTable.setFocusable(false);
        msiTable.setAutoResizeMode(0);
        // msiTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        // msiTable.getColumnModel().getColumn(1).setPreferredWidth(300);

        // initialize by setting msiTable as main table
        leftScrollPane.getViewport().setBackground(backgroundColor);

        leftScrollPane.setViewportView(msiTable);
        // scrollPane_2.setViewportView(routeTable);

        leftScrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        leftScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1,
                new Color(30, 30, 30)));
        // ((FlowLayout) headerPanel.getLayout()).setHgap(0);
        GridBagConstraints gbc_test = new GridBagConstraints();
        gbc_test.fill = GridBagConstraints.HORIZONTAL;
        gbc_test.gridx = 1;
        gbc_test.gridy = 0;
        gbc_test.gridheight = 1;
        gbc_test.insets = new Insets(0, 0, 0, 5);

        rightPanel = new JPanel();
        rightPanel.setBounds(345, 0, 410, 600);
        rightPanel.setBackground(backgroundColor);
        masterPanel.add(rightPanel);
        rightPanel.setLayout(null);
        pane_3 = new JPanel();
        pane_3.setBounds(85, 11, 245, 30);
        rightPanel.add(pane_3);
        pane_3.setBackground(backgroundColor);
        pane_3.setLayout(new FlowLayout());
        pane_3.setVisible(true);

        but_read = new JLabel("Read", new ImageIcon(EPDShore.class
                .getClassLoader().getResource(
                        "images/notificationcenter/tick.png")),
                SwingConstants.CENTER);
        GuiStyler.styleButton(but_read);
        but_read.setPreferredSize(new Dimension(75, 20));
        pane_3.add(but_read);

        but_goto = new JLabel("Goto", new ImageIcon(EPDShore.class
                .getClassLoader().getResource(
                        "images/notificationcenter/map-pin.png")),
                SwingConstants.CENTER);
        GuiStyler.styleButton(but_goto);
        but_goto.setPreferredSize(new Dimension(75, 20));

        pane_3.add(but_goto);

        but_delete = new JLabel("Delete", new ImageIcon(EPDShore.class
                .getClassLoader().getResource(
                        "images/notificationcenter/cross.png")),
                SwingConstants.CENTER);
        GuiStyler.styleButton(but_delete);
        but_delete.setPreferredSize(new Dimension(75, 20));
        pane_3.add(but_delete);

        but_read.setEnabled(false);
        but_goto.setEnabled(false);
        but_delete.setEnabled(false);

        scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(0, 41, 408, 541);
        rightPanel.add(scrollPane_1);
        scrollPane_1.setViewportView(area);

        area.setEditable(false);
        area.setContentType("text/html");
        // area.setPreferredSize(new Dimension(2000, 1000));
        area.setLayout(null);
        area.setBackground(backgroundColor);
        area.setMargin(new Insets(10, 10, 10, 10));
        scrollPane_1.setVisible(true);

        area.setText("");

        addMouseListeners();

        scrollPane_1.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(30, 30, 30)));

    }

    public JTable getMsiTable() {
        return msiTable;
    }

    public void initTable() {
        msiTableModel = (MsiTableModel) msiTable.getModel();
        msiTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        msiTable.getColumnModel().getColumn(1).setPreferredWidth(60);
        msiTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        msiTable.getColumnModel().getColumn(3).setPreferredWidth(137);
        msiTable.getSelectionModel().addListSelectionListener(
                new MSIRowListener());

        headerPanel.add(createHeaderColumn(msiTableModel.getColumnName(0), 40));
        headerPanel.add(createHeaderColumn(msiTableModel.getColumnName(1), 60));
        headerPanel.add(createHeaderColumn(msiTableModel.getColumnName(2), 90));
        headerPanel
                .add(createHeaderColumn(msiTableModel.getColumnName(3), 155));
        msiTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        msiTable.setRowSelectionAllowed(true);
        msiTable.setColumnSelectionAllowed(false);

        if (msiTable.getRowCount() > 0) {
            msiTable.setRowSelectionInterval(0, 0);
            readMessage(0);
        }

    }

    public void readMessage(int selection, int msgindex) {
        msiTable.setRowSelectionInterval(selection, selection);
        readMessage(selection);
    }

    private JPanel createHeaderColumn(String name, int width) {
        JPanel container = new JPanel();
        container.setSize(width, 10);
        container.setPreferredSize(new Dimension(width, 10));
        container.setBackground(new Color(39, 39, 39));
        container.setBounds(0, 0, width, 15);
        ((FlowLayout) container.getLayout()).setVgap(0);
        ((FlowLayout) container.getLayout()).setHgap(0);

        JLabel col = new JLabel(name);
        col.setSize(width, 10);
        col.setPreferredSize(new Dimension(width, 10));
        col.setForeground(Color.white);
        col.setFont(new Font("Arial", Font.BOLD, 9));
        if ("ID".equals(name)) {
            col.setHorizontalAlignment(0);
        }
        container.add(col);
        return container;
    }

    public void readMessage(int selectedRow) {
        
//        System.out.println("Reading msi message " + selectedRow);
        
        this.currentSelection = selectedRow;
        
        if (selectedRow < msiHandler.getMessageList().size() && selectedRow > -1){
            
        
        

        but_read.setEnabled(true);
        but_goto.setEnabled(true);
        but_delete.setEnabled(true);

        // System.out.println(msiTableModel.isAwk(selectedRow));

        if (msiTableModel.isAwk(selectedRow)) {
            but_read.setEnabled(false);
        } else {
            but_read.setEnabled(true);
        }

        // Update area

        doc.delete(0, doc.length());
        doc.append("<font size=\"2\" face=\"times, serif\" color=\"white\">");
        for (int i = 0; i < ((MsiTableModel) msiTable.getModel())
                .areaGetColumnCount(); i++) {

            doc.append("<u><b>"
                    + ((MsiTableModel) msiTable.getModel())
                            .areaGetColumnName(i)
                    + ":</b></u><br />"
                    + ((MsiTableModel) msiTable.getModel()).areaGetValueAt(
                            selectedRow, i) + "<br /><br />");
        }

        doc.append("</font>");
        area.setText(doc.toString());
        }else{
            area.setText("");
            but_read.setEnabled(false);
            but_goto.setEnabled(false);
            but_delete.setEnabled(false);
        }
    }

    public void addMouseListeners() {

        but_read.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                if (but_read.isEnabled()) {

                    MsiMessage msiMessage = msiHandler.getMessageList().get(
                            currentSelection).msiMessage;
                    msiHandler.setAcknowledged(msiMessage);
                    msiTableModel.updateMessages();
                    but_read.setEnabled(false);
                }
            }
        });

        but_goto.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (but_goto.isEnabled()) {
                    if (EPDShore.getInstance().getMainFrame().getActiveMapWindow() != null) {
                        EPDShore.getInstance().getMainFrame()
                                .getActiveMapWindow()
                                .getChartPanel()
                                .zoomToPoint(
                                        msiTableModel
                                                .getMessageLatLon(currentSelection));
                    } else if (EPDShore.getInstance().getMainFrame().getMapWindows().size() > 0) {
                        EPDShore.getInstance().getMainFrame()
                                .getMapWindows()
                                .get(0)
                                .getChartPanel()
                                .zoomToPoint(
                                        msiTableModel
                                                .getMessageLatLon(currentSelection));
                    }

                }
            }
        });

        but_delete.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {

                if (but_delete.isEnabled()) {

                    if (currentSelection < msiHandler.getMessageList().size()) {

                        MsiMessage msiMessage = msiHandler.getMessageList()
                                .get(currentSelection).msiMessage;
                        msiHandler.deleteMessage(msiMessage);

                        but_read.setEnabled(false);
                        but_goto.setEnabled(false);
                        but_delete.setEnabled(false);

                        msiTable.updateUI();
                        
                        
                        if (msiHandler.getMessageList().size() > currentSelection-1){
                            readMessage(currentSelection - 1);
                        }else{
                            readMessage(msiHandler.getMessageList().size() + 1);
                        }
                        

                    }
                }
            }
        });
    }

    public void setMsiHandler(MsiHandler msiHandler) {
        this.msiHandler = msiHandler;
    }

    public void updateTable() {
        msiTableModel.updateMessages();
        msiTable.updateUI();
    }

    private class MSIRowListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent event) {

            if (event.getValueIsAdjusting()) {
                return;
            }

            values = (DefaultListSelectionModel) event.getSource();

            currentSelection = values.getAnchorSelectionIndex();

            // Enable buttons?

            if (values.getAnchorSelectionIndex() == -1) {
                // removeArea();
                return;
            } else {
                readMessage(values.getAnchorSelectionIndex());
            }
            // selectedRow = values.getAnchorSelectionIndex();
            // if (msiTableModel.isAwk(selectedRow)) {
            // but_read.setEnabled(false);
            // } else {
            // but_read.setEnabled(true);
            // }
            //
            // // Update area
            //
            // doc.delete(0, doc.length());
            // doc.append("<font size=\"2\" face=\"times, serif\" color=\"white\">");
            // for (int i = 0; i < ((MsiTableModel)
            // msiTable.getModel()).areaGetColumnCount(); i++) {
            // if (values.getAnchorSelectionIndex() == -1) {
            // // removeArea();
            // return;
            // }
            // doc.append("<u><b>" + ((MsiTableModel)
            // msiTable.getModel()).areaGetColumnName(i) + ":</b></u><br />"
            // + ((MsiTableModel)
            // msiTable.getModel()).areaGetValueAt(values.getAnchorSelectionIndex(),
            // i)
            // + "<br /><br />");
            // }
            //
            //
            // doc.append("</font>");
            // area.setText(doc.toString());
            //

        }
    }

}
