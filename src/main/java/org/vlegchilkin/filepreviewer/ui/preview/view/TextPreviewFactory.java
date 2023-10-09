package org.vlegchilkin.filepreviewer.ui.preview.view;

import net.java.truevfs.access.TFileReader;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

/**
 * Preview Builder for text files.
 * Max preview text length is defined in the 'preview.text.max.length' property.
 */

public class TextPreviewFactory extends MetadataPreviewFactory {
    private static final int MAX_LENGTH = Integer.parseInt(Main.PROPERTIES.getString("preview.text.max.length"));

    private final File file;

    /**
     * Check if it is possible to show the content as a plain text.
     */
    public static boolean isSupported(Metadata metadata) {
        if (metadata.mimeType() == null) {
            return false;
        }
        return metadata.mimeType().startsWith("text/") || "application/json".equals(metadata.mimeType());
    }

    public TextPreviewFactory(File file, Metadata metadata) throws PreviewException {
        super(metadata);
        this.file = file;
    }

    @Override
    public TextPreview createContentView() {
        return new TextPreview();
    }

    public class TextPreview extends Preview<String> {
        public TextPreview() {
            super();
            this.resourceLoader = new TextLoader();
            this.resourceLoader.execute();
        }

        @Override
        protected JComponent build(String resource) {
            JTextArea textArea = new JTextArea(resource);
            textArea.setEditable(false);
            return new JScrollPane(textArea);
        }

        private class TextLoader extends ResourceLoader {
            @Override
            protected String doInBackground() throws Exception {
                final int charsRead;
                char[] buffer = new char[(int) Math.min(file.length(), TextPreviewFactory.MAX_LENGTH)];
                try (Reader reader = new TFileReader(file)) {
                    charsRead = reader.read(buffer);
                } catch (IOException e) {
                    throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
                }

                return String.valueOf(buffer, 0, charsRead);
            }
        }
    }
}