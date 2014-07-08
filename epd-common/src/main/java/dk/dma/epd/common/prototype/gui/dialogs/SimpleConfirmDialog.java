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
