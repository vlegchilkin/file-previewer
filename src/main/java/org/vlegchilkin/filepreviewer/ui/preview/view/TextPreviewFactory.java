package org.vlegchilkin.filepreviewer.ui.preview.view;

import net.java.truevfs.access.TFileReader;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.concurrent.ExecutionException;

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
    public JComponent createContentView() {
        return new TextPreview();
    }

    class TextPreview extends JPanel {
        private final TextLoader textLoader;

        public TextPreview() {
            super(new GridBagLayout());
            add(new JLabel("", ImagePreviewFactory.ImagePreview.LOADER_ICON, JLabel.CENTER));
            this.textLoader = new TextLoader();
            this.textLoader.execute();
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            textLoader.cancel(true);
        }

        private class TextLoader extends SwingWorker<String, Void> {
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

            @Override
            protected void done() {
                String text = null;
                PreviewException exception = null;
                try {
                    text = get();
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof PreviewException) {
                        exception = (PreviewException) cause;
                    } else {
                        exception = new PreviewException(e.getCause(), PreviewException.ErrorCode.UNKNOWN_ERROR);
                    }
                } catch (Exception e) {
                    exception = new PreviewException(e, PreviewException.ErrorCode.UNKNOWN_ERROR);
                }

                if (exception != null) {
                    JLabel label = (JLabel) getComponent(0);
                    label.setIcon(ImagePreviewFactory.ImagePreview.ERROR_ICON);
                    label.setText(exception.getMessage());
                } else {
                    getComponent(0).setVisible(false);
                    JTextArea textArea = new JTextArea(text);
                    textArea.setEditable(false);
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(getSize());
                    add(scrollPane);
                }
            }
        }
    }
}