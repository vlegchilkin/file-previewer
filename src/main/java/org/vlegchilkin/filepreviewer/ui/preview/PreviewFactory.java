package org.vlegchilkin.filepreviewer.ui.preview;

import javax.swing.*;

public interface PreviewFactory {
    JComponent createContentView();

    JComponent createMetadataView();
}
