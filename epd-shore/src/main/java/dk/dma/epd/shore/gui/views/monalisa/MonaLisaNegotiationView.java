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
package dk.dma.epd.shore.gui.views.monalisa;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestMessage;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteRequestReply;
import dk.dma.epd.common.prototype.enavcloud.MonaLisaRouteService.MonaLisaRouteStatus;
import dk.dma.epd.common.text.Formatter;
import dk.dma.epd.shore.gui.settingtabs.GuiStyler;

public class MonaLisaNegotiationView extends JPanel {

    private static final long serialVersionUID = 1L;

    JLabel lblNoReply;
    JPanel replyPanel;

    /**
     * Create the panel.
     * 
     * @param monaLisaRouteRequestMessage
     */
    public MonaLisaNegotiationView(
            MonaLisaRouteRequestMessage monaLisaRouteRequestMessage) {

        this.setBackground(GuiStyler.backgroundColor);
        setLayout(null);

        JPanel requestPanel = new JPanel();
        requestPanel.setBounds(10, 11, 263, 147);

        requestPanel.setBackground(GuiStyler.backgroundColor);
        requestPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
                new Color(70, 70, 70)), "Voyage Request", TitledBorder.LEADING,
                TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        add(requestPanel);
        requestPanel.setLayout(null);

        JLabel lblRequestMessage = new JLabel("Request Message:");
        lblRequestMessage.setBounds(10, 20, 106, 14);
        GuiStyler.styleText(lblRequestMessage);
        requestPanel.add(lblRequestMessage);

        JLabel lblSent = new JLabel("Sent:");
        GuiStyler.styleText(lblSent);
        lblSent.setBounds(10, 80, 46, 14);
        requestPanel.add(lblSent);

        JLabel lblRouteName = new JLabel("Route Name:");
        lblRouteName.setBounds(10, 100, 75, 14);
        GuiStyler.styleText(lblRouteName);
        requestPanel.add(lblRouteName);

        
        
        
        JTextArea requestMessageTxt = new JTextArea(monaLisaRouteRequestMessage.getMessage());
        requestMessageTxt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        requestMessageTxt.setBackground(Color.WHITE);
        requestMessageTxt.setLineWrap(true);
        requestMessageTxt.setBorder(null);
        requestMessageTxt.setEditable(false);
        
        
        GuiStyler.styleArea(requestMessageTxt);
        
        JScrollPane requestMessageSP = new JScrollPane(requestMessageTxt);
        requestMessageSP.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
        requestMessageSP.setBounds(10, 37, 243, 41);
        requestPanel.add(requestMessageSP);
        
        
        
        
//        JLabel requestMessageTxt = new JLabel(
//                monaLisaRouteRequestMessage.getMessage());
//        requestMessageTxt.setBounds(10, 40, 243, 14);
//        GuiStyler.styleText(requestMessageTxt);
//        requestPanel.add(requestMessageTxt);

        JLabel sentDateTxt = new JLabel(
                Formatter.formatLongDateTime(monaLisaRouteRequestMessage
                        .getSent()));
        sentDateTxt.setBounds(85, 80, 168, 14);
        GuiStyler.styleText(sentDateTxt);
        requestPanel.add(sentDateTxt);

        JLabel routeName = new JLabel(monaLisaRouteRequestMessage.getRoute()
                .getName());
        routeName.setBounds(75, 100, 178, 14);
        GuiStyler.styleText(routeName);
        requestPanel.add(routeName);

        replyPanel = new JPanel();
        replyPanel.setBounds(10, 169, 263, 119);
        add(replyPanel);
        replyPanel.setLayout(null);

        replyPanel.setBackground(GuiStyler.backgroundColor);
        replyPanel.setBorder(new TitledBorder(new MatteBorder(1, 1, 1, 1,
                new Color(70, 70, 70)), "STCC Reply", TitledBorder.LEADING,
                TitledBorder.TOP, GuiStyler.defaultFont, GuiStyler.textColor));

        lblNoReply = new JLabel("No reply sent");
        GuiStyler.styleText(lblNoReply);
        GuiStyler.styleText(lblNoReply);
        lblNoReply.setBounds(10, 20, 243, 14);
        replyPanel.add(lblNoReply);

        
//        handleReply(new MonaLisaRouteRequestReply("TEST TEST MSG", -1, -1, 1, MonaLisaRouteStatus.AGREED, new Route()));
        
    }

    public void handleReply(MonaLisaRouteRequestReply monaLisaRouteRequestReply) {
        lblNoReply.setVisible(false);

        JLabel lblReplied = new JLabel("Replied:");
        lblReplied.setBounds(10, 20, 46, 14);
        GuiStyler.styleText(lblReplied);
        replyPanel.add(lblReplied);

        JLabel lblMessage = new JLabel("Message:");
        lblMessage.setBounds(10, 40, 50, 14);
        GuiStyler.styleText(lblMessage);
        replyPanel.add(lblMessage);

        JLabel lblType = new JLabel("Type:");
        lblType.setBounds(10, 94, 46, 14);
        GuiStyler.styleText(lblType);
        replyPanel.add(lblType);

        JLabel repliedTxt = new JLabel(Formatter.formatLongDateTime(new Date(
                monaLisaRouteRequestReply.getSendDate())));
        repliedTxt.setBounds(65, 20, 188, 14);
        GuiStyler.styleText(repliedTxt);
        replyPanel.add(repliedTxt);

        
        JTextArea messageTxt = new JTextArea(monaLisaRouteRequestReply.getMessage());
        messageTxt.setFont(new Font("Monospaced", Font.PLAIN, 12));
        messageTxt.setBackground(Color.WHITE);
        messageTxt.setLineWrap(true);
        messageTxt.setBorder(null);
        messageTxt.setEditable(false);

        GuiStyler.styleArea(messageTxt);
        
        JScrollPane replyMessageSP = new JScrollPane(messageTxt);
        replyMessageSP.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
        replyMessageSP.setBounds(10, 55, 243, 39);
        replyPanel.add(replyMessageSP);
        
        
        
        
//        JLabel messageTxt = new JLabel(monaLisaRouteRequestReply.getMessage());
//        messageTxt.setBounds(65, 40, 188, 14);
//        GuiStyler.styleText(messageTxt);
//        replyPanel.add(messageTxt);

        JLabel typeTxt = new JLabel();
        typeTxt.setBounds(65, 94, 188, 14);
        GuiStyler.styleText(typeTxt);
        replyPanel.add(typeTxt);

        if (monaLisaRouteRequestReply.getStatus() == MonaLisaRouteStatus.AGREED) {
            typeTxt.setText("Route Approved");
        }
        if (monaLisaRouteRequestReply.getStatus() == MonaLisaRouteStatus.NEGOTIATING) {
            typeTxt.setText("Route modified and sent for negotiation");
        }

    }

}
