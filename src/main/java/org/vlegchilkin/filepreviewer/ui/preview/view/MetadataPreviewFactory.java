package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewFactory;

import javax.swing.*;
import java.io.File;

/**
 * Preview with Metadata Factory.
 * Shows a common metadata view and an empty content.
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
}
