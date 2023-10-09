package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;

import javax.swing.*;
import java.io.File;

/**
 * Preview Builder for files with unsupported content types.
 */
public class UnsupportedPreviewFactory extends MetadataPreviewFactory {
    private static final String TEXT = Main.PROPERTIES.getString("preview.unsupported.text");
    private final String text;

    public UnsupportedPreviewFactory(Metadata metadata) {
        super(metadata);
        this.text = metadata != null ? UnsupportedPreviewFactory.TEXT : "";
    }

    @Override
    public JComponent createContentView(File file) {
        return new JLabel(this.text, null, SwingConstants.CENTER);
    }

}
