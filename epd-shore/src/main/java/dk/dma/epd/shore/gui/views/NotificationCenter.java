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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import dk.dma.epd.common.prototype.msi.IMsiUpdateListener;
import dk.dma.epd.common.prototype.msi.MsiHandler;
import dk.dma.epd.common.prototype.msi.MsiMessageExtended;
import dk.dma.epd.shore.EPDShore;
import dk.dma.epd.shore.ais.AisHandler;
import dk.dma.epd.shore.event.ToolbarMoveMouseListener;
import dk.dma.epd.shore.gui.msi.MsiTableModel;
import dk.dma.epd.shore.gui.route.RouteExchangeTableModel;
import dk.dma.epd.shore.gui.utils.ComponentFrame;
import dk.dma.epd.shore.gui.views.strategicRouteExchange.StrategicRouteExchangeNotificationPanel;
import dk.dma.epd.shore.gui.views.strategicRouteExchange.StrategicRouteExchangeTableModel;
import dk.dma.epd.shore.service.EnavServiceHandler;
import dk.dma.epd.shore.service.RouteExchangeListener;
import dk.dma.epd.shore.service.StrategicRouteExchangeHandler;
import dk.dma.epd.shore.service.StrategicRouteExchangeListener;
import dk.dma.epd.shore.service.StrategicRouteNegotiationData;
import dk.dma.epd.shore.voyage.VoyageManager;

public class NotificationCenter extends ComponentFrame implements
        ListSelectionListener, ActionListener, IMsiUpdateListener,
        RouteExchangeListener, StrategicRouteExchangeListener {

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

    private MainFrame mainFrame;
    private HashMap<String, Integer> unreadMessages = new HashMap<String, Integer>();
    private HashMap<String, JLabel> unreadMessagesLabels = new HashMap<String, JLabel>();
    private HashMap<String, JLabel> indicatorLabels = new HashMap<String, JLabel>();

    private static int notificationHeight = 25;
    private static int notificationWidth = 125;
    private JTable msiTable;
    private JTable routeTable;
    private JTable strategicRouteTable;

    private MsiHandler msiHandler;
    private MsiTableModel msiTableModel;

    private RouteExchangeTableModel routeTableModel;
    private StrategicRouteExchangeTableModel strategicRouteTableModel = new StrategicRouteExchangeTableModel();

    private JPanel msiPanelLeft;
    private JPanel routePanelLeft;
    private JPanel strategicRoutePanelLeft;

    private Color leftButtonColor = Color.DARK_GRAY;
    private Color leftButtonColorClicked = new Color(45, 45, 45);
    private Color backgroundColor = new Color(83, 83, 83);

    private JLabel moveHandler;
    private JPanel masterPanel;
    private static int moveHandlerHeight = 18;
    private JPanel topBar;
    private JPanel notificationContentPanel;

    private MSINotificationPanel msiPanel;
    private RouteExchangeNotificationPanel routePanel;
    private StrategicRouteExchangeNotificationPanel strategicRoutePanel;

    private EnavServiceHandler enavServiceHandler;
    
    private StrategicRouteExchangeHandler strategicRouteExchangeHandler;
    
    private AisHandler aisHandler;
    
    public NotificationCenter() {

        super("NotificationCenter", false, true, false, false);

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
        moveHandler = new JLabel("Notification Center", SwingConstants.CENTER);
        moveHandler.setFont(new Font("Arial", Font.BOLD, 9));
        moveHandler.setForeground(new Color(200, 200, 200));
        // actions = moveHandler.getListeners(MouseMotionListener.class);
        topBar.add(moveHandler);

        // The tools (minimize, maximize and close)
        JPanel windowToolsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,
                0, 0));
        windowToolsPanel.setOpaque(false);
        windowToolsPanel.setPreferredSize(new Dimension(60, 50));

        JLabel close = new JLabel(EPDShore.res().getCachedImageIcon("images/window/close.png"));
        close.addMouseListener(new MouseAdapter() {

            public void mouseReleased(MouseEvent e) {
                toggleVisibility();
            }
        });

        close.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 2));
        windowToolsPanel.add(close);
        topBar.add(windowToolsPanel);

        JPanel outerPanel = new JPanel();

        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 900, 600);
        outerPanel
                .setPreferredSize(new Dimension(900, 600 - moveHandlerHeight));
        outerPanel.setSize(new Dimension(900, 600 - moveHandlerHeight));
        outerPanel.setBackground(backgroundColor);

        Integer messageCountMSI = unreadMessages.get("MSI");
        if (messageCountMSI == null) {
            messageCountMSI = 0;
        }

        Integer messageCountRoute = unreadMessages.get("Route");
        if (messageCountRoute == null) {
            messageCountRoute = 0;
        }

        Integer messageCountStrategicRoute = unreadMessages.get("StrategicRoute");
        if (messageCountStrategicRoute == null) {
            messageCountStrategicRoute = 0;
        }

        String[] colHeadings = { "ID", "Title" };
        DefaultTableModel model = new DefaultTableModel(30, colHeadings.length);
        model.setColumnIdentifiers(colHeadings);

        masterPanel = new JPanel(new BorderLayout());
        masterPanel.add(topBar, BorderLayout.NORTH);
        masterPanel.add(outerPanel, BorderLayout.CENTER);
        outerPanel.setLayout(new BorderLayout(0, 0));

        JPanel LeftLabelPane = new JPanel();
        LeftLabelPane.setLayout(new FlowLayout());

        // LeftLabelPane.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
        // new Color(30, 30, 30), new Color(
        // 45, 45, 45)));
        LeftLabelPane.setBorder(new MatteBorder(0, 0, 0, 1, new Color(30, 30,
                30)));

        // LeftLabelPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        LeftLabelPane.setBackground(backgroundColor);
        outerPanel.add(LeftLabelPane, BorderLayout.WEST);

        JPanel labelContainer = new JPanel();
        labelContainer.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelContainer.setPreferredSize(new Dimension(notificationWidth,
                notificationHeight * 4));
        ((FlowLayout) labelContainer.getLayout()).setVgap(3);
        labelContainer.setBackground(backgroundColor);
        LeftLabelPane.add(labelContainer);

        // Style the MSI notification panel
        msiPanelLeft = new JPanel();
        msiPanelLeft.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        msiPanelLeft.setBackground(new Color(65, 65, 65));
        msiPanelLeft.setPreferredSize(new Dimension(notificationWidth,
                notificationHeight));
        msiPanelLeft.setSize(new Dimension(notificationWidth,
                notificationHeight));

        // Style the Route notification panel
        routePanelLeft = new JPanel();
        routePanelLeft.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        routePanelLeft.setBackground(new Color(65, 65, 65));
        routePanelLeft.setPreferredSize(new Dimension(notificationWidth,
                notificationHeight));
        routePanelLeft.setSize(new Dimension(notificationWidth,
                notificationHeight));

        // Style the Stratic Route Exchange notification panel
        strategicRoutePanelLeft = new JPanel();
        strategicRoutePanelLeft.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        strategicRoutePanelLeft.setBackground(new Color(65, 65, 65));
        strategicRoutePanelLeft.setPreferredSize(new Dimension(
                notificationWidth, notificationHeight));
        strategicRoutePanelLeft.setSize(new Dimension(notificationWidth,
                notificationHeight));

        // Create labels for each service

        // Route Exchange
        JLabel notificationRoute = new JLabel("  Tactical Route");
        notificationRoute
                .setPreferredSize(new Dimension(98, notificationHeight));
        notificationRoute.setSize(new Dimension(76, notificationHeight));
        notificationRoute.setFont(new Font("Arial", Font.PLAIN, 11));
        notificationRoute.setForeground(new Color(237, 237, 237));
        routePanelLeft.add(notificationRoute);

        // Unread messages
        JLabel routeMessages = new JLabel(messageCountRoute.toString(),
                SwingConstants.RIGHT);
        routeMessages.setPreferredSize(new Dimension(20, notificationHeight));
        routeMessages.setSize(new Dimension(20, notificationHeight));
        routeMessages.setFont(new Font("Arial", Font.PLAIN, 9));
        routeMessages.setForeground(new Color(100, 100, 100));
        routeMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        routePanelLeft.add(routeMessages);

        // The unread indicator
        JLabel routeUnreadIndicator = new JLabel();
        routeUnreadIndicator.setPreferredSize(new Dimension(7,
                notificationHeight));
        routeUnreadIndicator.setSize(new Dimension(7, notificationHeight));
        routePanelLeft.add(routeUnreadIndicator);

        labelContainer.add(routePanelLeft);

        
        
        // Stratic Route Exchange
        JLabel strategicRouteExchangeLisanotificationRoute = new JLabel("  Strategic Route");
        strategicRouteExchangeLisanotificationRoute.setPreferredSize(new Dimension(98,
                notificationHeight));
        strategicRouteExchangeLisanotificationRoute
                .setSize(new Dimension(76, notificationHeight));
        strategicRouteExchangeLisanotificationRoute.setFont(new Font("Arial", Font.PLAIN, 11));
        strategicRouteExchangeLisanotificationRoute.setForeground(new Color(237, 237, 237));
        strategicRoutePanelLeft.add(strategicRouteExchangeLisanotificationRoute);

        // Unread messages
        JLabel strategicRouteMessages = new JLabel(
                messageCountStrategicRoute.toString(), SwingConstants.RIGHT);
        strategicRouteMessages.setPreferredSize(new Dimension(20,
                notificationHeight));
        strategicRouteMessages.setSize(new Dimension(20, notificationHeight));
        strategicRouteMessages.setFont(new Font("Arial", Font.PLAIN, 9));
        strategicRouteMessages.setForeground(new Color(100, 100, 100));
        strategicRouteMessages.setBorder(BorderFactory.createEmptyBorder(0, 0,
                0, 5));
        strategicRoutePanelLeft.add(strategicRouteMessages);

        // The unread indicator
        JLabel strategicRouteUnreadIndicator = new JLabel();
        strategicRouteUnreadIndicator.setPreferredSize(new Dimension(7,
                notificationHeight));
        strategicRouteUnreadIndicator.setSize(new Dimension(7,
                notificationHeight));
        strategicRoutePanelLeft.add(strategicRouteUnreadIndicator);

        labelContainer.add(strategicRoutePanelLeft);

        // MSI
        JLabel notification = new JLabel("  MSI");
        notification.setPreferredSize(new Dimension(98, notificationHeight));
        notification.setSize(new Dimension(76, notificationHeight));
        notification.setFont(new Font("Arial", Font.PLAIN, 11));
        notification.setForeground(new Color(237, 237, 237));
        msiPanelLeft.add(notification);

        // Unread messages
        JLabel msiMessages = new JLabel(messageCountMSI.toString(),
                SwingConstants.RIGHT);
        msiMessages.setPreferredSize(new Dimension(20, notificationHeight));
        msiMessages.setSize(new Dimension(20, notificationHeight));
        msiMessages.setFont(new Font("Arial", Font.PLAIN, 9));
        msiMessages.setForeground(new Color(100, 100, 100));
        msiMessages.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        msiPanelLeft.add(msiMessages);
        // The unread indicator
        JLabel msiUnreadIndicator = new JLabel();
        msiUnreadIndicator
                .setPreferredSize(new Dimension(7, notificationHeight));
        msiUnreadIndicator.setSize(new Dimension(7, notificationHeight));
        msiPanelLeft.add(msiUnreadIndicator);

        labelContainer.add(msiPanelLeft);

        // Make list of labels to use when updating service
        indicatorLabels.put("MSI", msiUnreadIndicator);
        unreadMessagesLabels.put("MSI", msiMessages);

        // Make list of labels to use when updating service
        indicatorLabels.put("Route", routeUnreadIndicator);
        unreadMessagesLabels.put("Route", routeMessages);

        // Make list of labels to use when updating service
        indicatorLabels.put("StrategicRoute", strategicRouteUnreadIndicator);
        unreadMessagesLabels.put("StrategicRoute", strategicRouteMessages);

        notificationContentPanel = new JPanel();
        outerPanel.add(notificationContentPanel, BorderLayout.EAST);
        GridBagLayout gbl_notificationContentPanel = new GridBagLayout();
        gbl_notificationContentPanel.columnWidths = new int[] { 752, 0 };
        gbl_notificationContentPanel.rowHeights = new int[] { 900, 0 };
        gbl_notificationContentPanel.columnWeights = new double[] { 0.0,
                Double.MIN_VALUE };
        gbl_notificationContentPanel.rowWeights = new double[] { 0.0, 0.0 };
        notificationContentPanel.setLayout(gbl_notificationContentPanel);
        masterPanel.setBorder(BorderFactory.createEtchedBorder(
                EtchedBorder.LOWERED, new Color(30, 30, 30), new Color(45, 45,
                        45)));
        this.getContentPane().add(masterPanel);

        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 0;
        gbc_panel.gridy = 0;

        // Add notification panels
        msiPanel = new MSINotificationPanel();
        msiTable = msiPanel.getMsiTable();

        notificationContentPanel.add(msiPanel, gbc_panel);
        msiPanel.setVisible(true);

        // Add notification panel
        routePanel = new RouteExchangeNotificationPanel();
        routeTable = routePanel.getRouteTable();

        notificationContentPanel.add(routePanel, gbc_panel);
        routePanel.setVisible(false);

        // Add notification panel
        strategicRoutePanel = new StrategicRouteExchangeNotificationPanel(this);
        strategicRouteTable = strategicRoutePanel.getRouteTable();

        notificationContentPanel.add(strategicRoutePanel, gbc_panel);
        strategicRoutePanel.setVisible(false);

        addMouseListeners();

    }

    public void newMessage(final String service) throws InterruptedException {
        final int blinks = 20;
        final Runnable doChangeIndicator = new Runnable() {

            JLabel unreadIndicator = indicatorLabels.get(service);
            boolean changeColor;

            public void run() {

                if (changeColor = !changeColor) {
                    unreadIndicator.setBackground(new Color(165, 80, 80));
                } else {
                    unreadIndicator.setBackground(new Color(206, 120, 120));
                }

            }
        };

        Runnable doBlinkIndicator = new Runnable() {

            public void run() {
                for (int i = 0; i < blinks; i++) {
                    try {
                        EventQueue.invokeLater(doChangeIndicator);
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }

        };

        new Thread(doBlinkIndicator).start();

    }

    public void setMessages(String service, int messageCount)
            throws InterruptedException {

        JLabel unread = unreadMessagesLabels.get(service);
        JLabel unreadIndicator = indicatorLabels.get(service);
        Integer currentCount = unreadMessages.get(service);

        if (currentCount == null) {
            currentCount = 0;
        }

        // If no unread messages, remove the red indicator for the service
        if (messageCount == 0) {
            unreadIndicator.setOpaque(false);
        }

        // Update the unread messages label if it differs
        if (messageCount != currentCount) {
            unread.setText(Integer.toString(messageCount));
        }

        // If new unread messages are received, start the blinking indicator
        if (messageCount > currentCount) {
            unreadIndicator.setOpaque(true);
            newMessage(service);
        }

        unreadMessages.put(service, messageCount);
    }

    public void showMSIMessage(int service, int msgId) {
        
        setNotificationView(service);

        int index = -1;

        List<MsiMessageExtended> messages = msiTableModel.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            MsiMessageExtended message = messages.get(i);
            if (message.msiMessage.getMessageId() == msgId) {

                index = i;
                break;
            }
        }

        msiPanel.readMessage(index, index);
    }
    
    
    public void showStrategicRouteExchangeMsg(int service, long msgId) {
        
        setNotificationView(service);

        int index = -1;

        
        
        List<StrategicRouteNegotiationData> messages = strategicRouteTableModel.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            StrategicRouteNegotiationData message = messages.get(i);
            if (message.getId() == msgId) {
                index = i;
                break;
            }
        }

        strategicRoutePanel.readMessage(index, index);
        
        this.setVisible(true);
        

    }
    

    @Override
    public void findAndInit(Object obj) {
        if (obj instanceof MsiHandler) {
            msiHandler = (MsiHandler) obj;
            msiHandler.addListener(this);
            msiTableModel = new MsiTableModel(msiHandler);
            msiTable.setModel(msiTableModel);
            msiPanel.setMsiHandler(msiHandler);
            msiPanel.initTable();
        } else
        if (obj instanceof AisHandler) {
            aisHandler = (AisHandler) obj;
            strategicRouteTableModel.setAisHandler(aisHandler);
            strategicRoutePanel.setAisHandler(aisHandler);
        } else
        if (obj instanceof MainFrame) {
            mainFrame = (MainFrame) obj;
            ToolbarMoveMouseListener mml = new ToolbarMoveMouseListener(this,
                    mainFrame);
            topBar.addMouseListener(mml);
            topBar.addMouseMotionListener(mml);
        } else

        if (obj instanceof EnavServiceHandler) {
            enavServiceHandler = (EnavServiceHandler) obj;
            enavServiceHandler.addRouteExchangeListener(this);
            
            routePanel.setEnavServiceHandler(enavServiceHandler);
            routeTableModel = new RouteExchangeTableModel(enavServiceHandler);
            routeTable.setModel(routeTableModel);
            routePanel.initTable();
            

        }
        else
        if (obj instanceof VoyageManager){
            strategicRoutePanel.setVoyageManager((VoyageManager) obj);
        }
        
        if (obj instanceof StrategicRouteExchangeHandler){
            strategicRouteExchangeHandler = (StrategicRouteExchangeHandler) obj;
            strategicRouteExchangeHandler.addStrategicRouteExchangeListener(this);
            strategicRoutePanel.setStrategicRouteExchangeHandler(strategicRouteExchangeHandler);
//            monaLisaRoutePanel.setEnavServiceHandler(enavServiceHandler);
//            monaLisaRouteTableModel.setEnavServiceHandler(enavServiceHandler);
            
            
            strategicRouteTableModel.setStrategicRouteExchangeHandler(strategicRouteExchangeHandler);
            strategicRouteTable.setModel(strategicRouteTableModel);
            strategicRoutePanel.initTable();
            
        }
    }

    @Override
    public void msiUpdate() {
        try {
            setMessages("MSI", msiHandler.getUnAcknowledgedMSI());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        msiPanel.updateTable();

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void valueChanged(ListSelectionEvent arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * Change the visiblity
     */
    public void toggleVisibility() {
        setVisible(!this.isVisible());
    }

    public void addMouseListeners() {

        msiPanelLeft.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                msiPanelLeft.setBackground(leftButtonColorClicked);
            }

            public void mouseReleased(MouseEvent e) {
                msiPanelLeft.setBackground(leftButtonColor);
            }

            public void mouseClicked(MouseEvent e) {
                // Activate and update table

                strategicRoutePanel.setVisible(false);
                routePanel.setVisible(false);
                msiPanel.setVisible(true);

            }
        });

        routePanelLeft.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                routePanelLeft.setBackground(leftButtonColorClicked);
            }

            public void mouseReleased(MouseEvent e) {
                routePanelLeft.setBackground(leftButtonColor);
            }

            public void mouseClicked(MouseEvent e) {
                strategicRoutePanel.setVisible(false);
                msiPanel.setVisible(false);
                routePanel.setVisible(true);
            }
        });

        strategicRoutePanelLeft.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                strategicRoutePanelLeft.setBackground(leftButtonColorClicked);
            }

            public void mouseReleased(MouseEvent e) {
                strategicRoutePanelLeft.setBackground(leftButtonColor);
            }

            public void mouseClicked(MouseEvent e) {
                msiPanel.setVisible(false);
                routePanel.setVisible(false);
                strategicRoutePanel.setVisible(true);
            }
        });
    }

    public void toggleVisibility(int service) {

        // setVisible(!this.isVisible());
        setVisible(true);
        setNotificationView(service);

    }

    private void setNotificationView(int service) {
        switch (service) {
        case 0:
            // Activate MSI
            routePanel.setVisible(false);
            strategicRoutePanel.setVisible(false);
            msiPanel.setVisible(true);
            
            break;

        case 1:

            // Activate route exchange
            msiPanel.setVisible(false);
            strategicRoutePanel.setVisible(false);
            routePanel.setVisible(true);

            break;

        case 2:
            // Activate Strategic Route Exchange
            msiPanel.setVisible(false);
            routePanel.setVisible(false);
            strategicRoutePanel.setVisible(true);

            break;

        default:
            break;
        }
    }

    // public void cloudUpdate(){
    // routePanel.updateTable();
    //
    // if (routeTable.getSelectedRow() != -1){
    // routePanel.readMessage(routeTable.getSelectedRow());
    // }
    // }
    //
    @Override
    public void routeUpdate() {
        routePanel.updateTable();

        if (routeTable.getSelectedRow() != -1) {
            routePanel.readMessage(routeTable.getSelectedRow());
        }
        try {
            setMessages("Route", enavServiceHandler.getUnkAck());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void strategicRouteUpdate() {
        strategicRoutePanel.updateTable();

        if (strategicRouteTable.getSelectedRow() != -1) {
            strategicRoutePanel.readMessage(strategicRouteTable.getSelectedRow());
        }
        try {
            setMessages("StrategicRoute", strategicRouteExchangeHandler.getUnHandled());

        } catch (InterruptedException e) {
            e.printStackTrace();
        }        
    }
}
