package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import java.io.File;

/**
 * Preview Builder for text files.
 * Max preview text length is defined in the 'preview.text.max.length' property.
 */

public class TextPreviewFactory extends MetadataPreviewFactory {
    /**
     * Check if it is possible to show the content as a plain text.
     */
    public static boolean isSupported(Metadata metadata) {
        if (metadata.mimeType() == null) {
            return false;
        }
        return metadata.mimeType().startsWith("text/") || "application/json".equals(metadata.mimeType());
    }

    public TextPreviewFactory(Metadata metadata) throws PreviewException {
        super(metadata);
    }

    @Override
    public TextPreview createContentView(File file) {
        return new TextPreview(file);
    }

}