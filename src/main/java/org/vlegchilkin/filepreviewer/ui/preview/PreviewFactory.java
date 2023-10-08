package org.vlegchilkin.filepreviewer.ui.preview;

import javax.swing.*;

/**
 * Preview Factory, create views for a Preview Pane.
 */
public interface PreviewFactory {
    /**
     * View area with a content
     */
    JComponent createContentView();

    /**
     * View area with a metadata information
     */
    JComponent createMetadataView();
}
