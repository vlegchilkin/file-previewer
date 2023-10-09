package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewFactory;

import javax.swing.*;
import java.io.File;

/**
 * Default Preview Builder with common metadata builder and an empty content builder.
 */
public class MetadataPreviewFactory implements PreviewFactory {
    private final Metadata metadata;

    public MetadataPreviewFactory(Metadata metadata) {
        this.metadata = metadata;
    }

    public JComponent createContentView(File file) {
        return new JLabel();
    }

    public JComponent createMetadataView() {
        return new MetadataView(this.metadata);
    }

    /**
     * JLabel that passes the text field via translation.
     */
    static class TranslatedJLabel extends JLabel {
        public TranslatedJLabel(String text) {
            this(text, LEADING);
        }

        public TranslatedJLabel(String text, int horizontalAlignment) {
            super(Main.PROPERTIES.getString(text), horizontalAlignment);
        }
    }

}
