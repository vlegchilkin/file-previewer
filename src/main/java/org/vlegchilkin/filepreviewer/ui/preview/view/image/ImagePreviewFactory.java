package org.vlegchilkin.filepreviewer.ui.preview.view.image;

import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.view.MetadataPreviewFactory;

import javax.swing.*;
import java.io.File;

/**
 * Preview Builder for images.
 * File max size is defined in the 'preview.image.max.size' property.
 */
public class ImagePreviewFactory extends MetadataPreviewFactory {
    public ImagePreviewFactory(Metadata metadata) {
        super(metadata);
    }

    /**
     * Check if it is possible to show the content as a scaled image.
     */
    public static boolean isSupported(Metadata metadata) {
        if (metadata.mimeType() == null) {
            return false;
        }
        return metadata.mimeType().startsWith("image/") && !"image/heic".equals(metadata.mimeType());
    }

    @Override
    public JComponent createContentView(File file) {
        return new ImagePreview(file);
    }
}
