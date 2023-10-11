package org.vlegchilkin.filepreviewer.ui.preview.view.text;

import org.apache.tika.parser.txt.CharsetDetector;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;
import org.vlegchilkin.filepreviewer.ui.preview.view.Preview;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Preview view for Text files. Shows the content in the read-only JTextArea.
 * - TEXT_MAX_LENGTH is the preview content limit
 */
public class TextPreview extends Preview<String> {
    private static final int BUFFER_MAX_LENGTH = Integer.parseInt(
            Main.PROPERTIES.getString("preview.text.buffer-max-length")
    );

    public TextPreview(File file) {
        super(file);
    }

    @Override
    protected ResourceLoader createResourceLoader() {
        return new ResourceLoader() {
            @Override
            protected String doInBackground() throws Exception {
                byte[] buffer = new byte[BUFFER_MAX_LENGTH];
                final String result;
                try (InputStream stream = Files.newInputStream(getFile().toPath())) {
                    int read = stream.read(buffer);
                    if (read == -1) {
                        result = "";
                    } else {
                        var detector = new CharsetDetector();
                        byte[] data = Arrays.copyOf(buffer, read);
                        result = detector.setText(data).detect().getString();
                    }
                } catch (IOException e) {
                    throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
                }
                return result;
            }
        };
    }

    @Override
    protected JComponent build(String resource) {
        JTextArea textArea = new JTextArea(resource);
        textArea.setEditable(false);
        return new JScrollPane(textArea);
    }
}
