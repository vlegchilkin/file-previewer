package org.vlegchilkin.filepreviewer.ui.preview.view.text;

import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.view.MetadataPreviewFactory;

import java.io.File;

/**
 * Preview Builder for text files.
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

    public TextPreviewFactory(Metadata metadata) {
        super(metadata);
    }

    @Override
    public TextPreview createContentView(File file) {
        return new TextPreview(file);
    }

}