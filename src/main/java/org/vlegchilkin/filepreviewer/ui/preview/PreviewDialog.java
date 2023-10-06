package org.vlegchilkin.filepreviewer.ui.preview;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlegchilkin.filepreviewer.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Optional;

/**
 * Base class for preview dialogs, responsible for a common window/buttons/actions part.
 * Handles exception cases.
 */
public abstract class PreviewDialog extends JDialog implements ActionListener {
    final static Logger log = LoggerFactory.getLogger(PreviewDialog.class);
    private static final int MIN_WIDTH = Integer.parseInt(Main.PROPERTIES.getString("preview.min.width"));
    private static final int MIN_HEIGHT = Integer.parseInt(Main.PROPERTIES.getString("preview.min.height"));
    private static final String CLOSE_BUTTON_TEXT = Main.PROPERTIES.getString("preview.close.button.text");

    public PreviewDialog(Frame owner, File file) {
        super(owner, buildDefaultTitle(file), true);
        setMinimumSize(new Dimension(PreviewDialog.MIN_WIDTH, PreviewDialog.MIN_HEIGHT));
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        Container dialogContainer = getContentPane();
        dialogContainer.setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        dialogContainer.add(buttonPanel, BorderLayout.SOUTH);

        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        this.rootPane.registerKeyboardAction(this, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        JButton closeButton = new JButton(PreviewDialog.CLOSE_BUTTON_TEXT);
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);
        this.rootPane.setDefaultButton(closeButton);

        Optional<String> error;
        try {
            prepareView(file);
            error = Optional.empty();
        } catch (PreviewException e) {
            log.warn("Unable to show a preview for {}", file, e);
            error = Optional.of(e.getMessage());
        } catch (Exception e) {
            log.error("Something critical happened on a a preview for {}", file, e);
            error = Optional.of(PreviewException.ErrorCode.UNKNOWN_ERROR.getMessage());
        }
        error.ifPresent(message -> add(new JLabel(message, null, SwingConstants.CENTER)));

        pack();
        setLocationRelativeTo(getOwner());
    }

    abstract void prepareView(File file) throws PreviewException;

    static String buildDefaultTitle(File file) {
        return String.format("%s (%s)", file.getName(), FileUtils.byteCountToDisplaySize(file.length()));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        dispose();
    }
}
