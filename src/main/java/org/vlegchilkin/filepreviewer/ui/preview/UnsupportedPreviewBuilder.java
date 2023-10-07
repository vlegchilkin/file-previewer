package org.vlegchilkin.filepreviewer.ui.preview;

import org.vlegchilkin.filepreviewer.Main;

import javax.swing.*;
import java.io.File;
import java.util.Optional;

/**
 * Default Preview Builder for files with unsupported content types.
 */
public class UnsupportedPreviewBuilder extends PreviewBuilder {
    private static final String TEXT = Main.PROPERTIES.getString("preview.unsupported.text");

    private final String text;

    public UnsupportedPreviewBuilder(File file, Metadata metadata) {
        super(metadata);
        Boolean isFile = Optional.ofNullable(file).map(File::isFile).orElse(false);
        if (isFile) {
            this.text = UnsupportedPreviewBuilder.TEXT;
        } else {
            this.text = "";
        }
    }

    @Override
    public JComponent buildContentView(int areaWidth, int areaHeight) {
        return new JLabel(this.text, null, SwingConstants.CENTER);
    }

}
