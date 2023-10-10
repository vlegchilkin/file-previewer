package org.vlegchilkin.filepreviewer.ui.preview.view.text;

import net.java.truevfs.access.TFileReader;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;
import org.vlegchilkin.filepreviewer.ui.preview.view.Preview;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class TextPreview extends Preview<String> {
    private static final int TEXT_MAX_LENGTH = Integer.parseInt(
            Main.PROPERTIES.getString("preview.text.text-max-length")
    );

    public TextPreview(File file) {
        super(file);
    }

    @Override
    protected ResourceLoader createResourceLoader() {
        return new ResourceLoader() {
            @Override
            protected String doInBackground() throws Exception {
                final int charsRead;
                char[] buffer = new char[(int) Math.min(getFile().length(), TextPreview.TEXT_MAX_LENGTH)];
                try (Reader reader = new TFileReader(getFile())) {
                    charsRead = reader.read(buffer);
                } catch (IOException e) {
                    throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
                }

                return String.valueOf(buffer, 0, charsRead);
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
