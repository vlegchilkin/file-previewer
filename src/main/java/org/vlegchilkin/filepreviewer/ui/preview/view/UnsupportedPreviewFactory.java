package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;

import javax.swing.*;
import java.io.File;
import java.util.Optional;

/**
 * Preview Builder for files with unsupported content types.
 */
public class UnsupportedPreviewFactory extends MetadataPreviewFactory {
    private static final String TEXT = Main.PROPERTIES.getString("preview.unsupported.text");

    private final String text;

    public UnsupportedPreviewFactory(File file, Metadata metadata) {
        super(metadata);
        Boolean isFile = Optional.ofNullable(file).map(File::isFile).orElse(false);
        if (isFile) {
            this.text = UnsupportedPreviewFactory.TEXT;
        } else {
            this.text = "";
        }
    }

    @Override
    public JComponent createContentView(File file) {
        return new JLabel(this.text, null, SwingConstants.CENTER);
    }

}
