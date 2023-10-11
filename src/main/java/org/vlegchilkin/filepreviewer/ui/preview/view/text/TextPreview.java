package org.vlegchilkin.filepreviewer.ui.preview.view.text;

import org.apache.tika.parser.txt.CharsetDetector;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.view.Preview;

import javax.swing.*;
import java.io.File;

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
                byte[] data = readBytes(BUFFER_MAX_LENGTH);
                var detector = new CharsetDetector();
                return detector.setText(data).detect().getString();
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
