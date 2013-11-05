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
package dk.dma.epd.common.prototype.gui.dialogs;

import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * @author Janus Varmarken
 */
public final class SimpleConfirmDialog extends JDialog {

    /**
     * Default
     */
    private static final long serialVersionUID = 1L;
    /**
     * Contents of the dialog.
     */
    private final JOptionPane optionPane;

    /**
     * Listeners listening for user input.
     */
    private List<ISimpleConfirmDialogListener> yesNoListeners = new ArrayList<ISimpleConfirmDialogListener>();

    private SimpleConfirmDialog(String dialogTitle, String dialogMsg) {
        this.setTitle(dialogTitle);
        this.setModal(true);
        this.setAlwaysOnTop(true);
        this.optionPane = new JOptionPane(dialogMsg,
                JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION);
        this.setContentPane(this.optionPane);
        // Add listener for the content pane in order to be able to respond to
        // yes/no/close
        this.optionPane.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                String propName = e.getPropertyName();
                if (SimpleConfirmDialog.this.isVisible()
                        && e.getSource() == SimpleConfirmDialog.this.optionPane
                        && propName.equals(JOptionPane.VALUE_PROPERTY)) {
                    SimpleConfirmDialog.this.setVisible(false);
                    Object resultValue = SimpleConfirmDialog.this.optionPane
                            .getValue();
                    if (resultValue instanceof Integer) {
                        int resultCode = (Integer) resultValue;
                        if (resultCode == JOptionPane.YES_OPTION) {
                            for (ISimpleConfirmDialogListener listener : SimpleConfirmDialog.this.yesNoListeners) {
                                listener.onYesClicked();
                            }
                        } else if (resultCode == JOptionPane.NO_OPTION) {
                            for (ISimpleConfirmDialogListener listener : SimpleConfirmDialog.this.yesNoListeners) {
                                listener.onNoClicked();
                            }
                        }
                    }
                }
            }
        });

    }

    /**
     * Create and display a simple yes/no confirm dialog.
     * 
     * @param dialogTitle
     *            The title of the dialog.
     * @param dialogMsg
     *            The question to ask the user in the dialog.
     * @param listeners
     *            Initial set of listeners to listen for user input
     *            (yes/no/close)
     * @return
     */
    public static SimpleConfirmDialog showSimpleConfirmDialog(
            String dialogTitle, String dialogMsg,
            List<ISimpleConfirmDialogListener> listeners, Point dialogLocation) {
        SimpleConfirmDialog dialog = new SimpleConfirmDialog(dialogTitle,
                dialogMsg);
        dialog.setLocation(dialogLocation);
        for (ISimpleConfirmDialogListener lis : listeners) {
            dialog.addSimpleConfirmDialogListener(lis);
        }
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    /**
     * Add a listener to listen for user input on this dialog.
     * 
     * @param newListener
     *            The listener to add.
     */
    public void addSimpleConfirmDialogListener(
            ISimpleConfirmDialogListener newListener) {
        this.yesNoListeners.add(newListener);
    }
}
