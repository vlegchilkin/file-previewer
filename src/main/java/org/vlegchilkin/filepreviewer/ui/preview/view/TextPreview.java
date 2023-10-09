package org.vlegchilkin.filepreviewer.ui.preview.view;

import net.java.truevfs.access.TFileReader;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

public class TextPreview extends Preview<String> {
    private static final int MAX_LENGTH = Integer.parseInt(Main.PROPERTIES.getString("preview.text.max.length"));

    public TextPreview(File file) {
        super(file);
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
            char[] buffer = new char[(int) Math.min(file.length(), MAX_LENGTH)];
            try (Reader reader = new TFileReader(file)) {
                charsRead = reader.read(buffer);
            } catch (IOException e) {
                throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
            }

            return String.valueOf(buffer, 0, charsRead);
        }
    }
}
