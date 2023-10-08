package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;

/**
 * Preview Builder for failures.
 */
public class ErrorPreviewFactory extends MetadataPreviewFactory {
    private final PreviewException previewException;

    public ErrorPreviewFactory(Metadata metadata, PreviewException previewException) {
        super(metadata);
        this.previewException = previewException;
    }

    @Override
    public JComponent createContentView() {
        return new JLabel(this.previewException.getMessage(), null, SwingConstants.CENTER);
    }

}