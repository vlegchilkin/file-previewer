package org.vlegchilkin.filepreviewer.ui.preview.view.text;

import org.vlegchilkin.filepreviewer.ui.preview.loader.TextLoader;
import org.vlegchilkin.filepreviewer.ui.preview.view.Preview;
import org.vlegchilkin.filepreviewer.ui.preview.view.ResourceLoader;

import javax.swing.*;
import java.io.File;

public class TextPreview extends Preview<String> {

    public TextPreview(File file) {
        super(file);
    }

    @Override
    protected ResourceLoader<String> createResourceLoader() {
        return new TextLoader(this);
    }

    @Override
    protected JComponent build(String resource) {
        JTextArea textArea = new JTextArea(resource);
        textArea.setEditable(false);
        return new JScrollPane(textArea);
    }
}
