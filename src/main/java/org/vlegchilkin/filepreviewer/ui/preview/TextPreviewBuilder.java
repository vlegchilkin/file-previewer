package org.vlegchilkin.filepreviewer.ui.preview;

import net.java.truevfs.access.TFileReader;
import org.vlegchilkin.filepreviewer.Main;

import javax.swing.*;
import java.io.*;

/**
 * Preview Builder for text files.
 * Max preview text length is defined in the 'preview.text.max.length' property.
 */

public class TextPreviewBuilder extends PreviewBuilder {
    private static final int MAX_LENGTH = Integer.parseInt(Main.PROPERTIES.getString("preview.text.max.length"));

    private final String text;
    private final boolean complete;

    /**
     * Check if it is possible to show the content as a plain text.
     */
    public static boolean isSupported(Metadata metadata) {
        if (metadata.mimeType() == null) {
            return false;
        }
        return metadata.mimeType().startsWith("text/") || "application/json".equals(metadata.mimeType());
    }

    public TextPreviewBuilder(File file, Metadata metadata) throws PreviewException {
        super(metadata);

        final int charsRead;
        char[] buffer = new char[(int) Math.min(file.length(), TextPreviewBuilder.MAX_LENGTH)];
        try (Reader reader = new TFileReader(file)) {
            charsRead = reader.read(buffer);
            this.complete = reader.read() == -1;
        } catch (IOException e) {
            throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
        }

        this.text = String.valueOf(buffer, 0, charsRead);
    }

    /**
     * @deprecated looks like it is useless here, will be removed.
     */
    @Deprecated
    public boolean isComplete() {
        return complete;
    }

    @Override
    public JComponent buildContentView() {
        JTextArea textArea = new JTextArea(this.text);
        textArea.setEditable(false);
        return new JScrollPane(textArea);
    }
}
