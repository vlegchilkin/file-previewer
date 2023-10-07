package org.vlegchilkin.filepreviewer.ui.preview;

import javax.swing.*;

/**
 * Preview Builder for failures.
 */
public class ErrorPreviewBuilder extends PreviewBuilder {
    private final PreviewException previewException;

    public ErrorPreviewBuilder(Metadata metadata, PreviewException previewException) {
        super(metadata);
        this.previewException = previewException;
    }

    @Override
    public JComponent buildContentView(int areaWidth, int areaHeight) {
        return new JLabel(this.previewException.getMessage(), null, SwingConstants.CENTER);
    }

}
