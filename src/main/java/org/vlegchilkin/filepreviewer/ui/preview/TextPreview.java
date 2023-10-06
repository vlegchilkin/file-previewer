package org.vlegchilkin.filepreviewer.ui.preview;

import net.java.truevfs.access.TFileReader;
import org.vlegchilkin.filepreviewer.Main;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.List;

/**
 * Preview dialog for text files.
 * Supported extensions and preview max length are defined in the 'preview.text.*' properties.
 * uses AWT for loading images with fallback to slow ImageIO in case of awt failure (some rare png crc issues).
 */

public class TextPreview extends PreviewDialog {
    public static final java.util.List<String> EXTENSIONS = List.of(
            Main.PROPERTIES.getString("preview.text.extensions").split(",")
    );
    private static final int MAX_LENGTH = Integer.parseInt(Main.PROPERTIES.getString("preview.text.max.length"));
    private static final String MESSAGE_TRIM = Main.PROPERTIES.getString("preview.text.message.trim");

    public TextPreview(Frame owner, File file) {
        super(owner, file);
    }

    @Override
    void prepareView(File file) throws PreviewException {
        final long fileSize = file.length();

        final int charsRead;
        final boolean complete;
        char[] buffer = new char[(int) Math.min(fileSize, TextPreview.MAX_LENGTH)];
        try (Reader reader = new TFileReader(file)) {
            charsRead = reader.read(buffer);
            complete = reader.read() == -1;
        } catch (IOException e) {
            throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
        }

        String content = String.valueOf(buffer, 0, charsRead);
        JTextArea textArea = new JTextArea(content);
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        if (!complete) {
            setTitle(String.format(TextPreview.MESSAGE_TRIM, getTitle(), charsRead));
        }
    }
}
