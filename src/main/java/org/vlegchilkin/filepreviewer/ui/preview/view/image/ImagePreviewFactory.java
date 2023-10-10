package org.vlegchilkin.filepreviewer.ui.preview.view.image;

import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.view.MetadataPreviewFactory;

import javax.swing.*;
import java.io.File;

/**
 * Preview Factory for images.
 *
 * @see ImagePreview as a content
 * @see org.vlegchilkin.filepreviewer.ui.preview.view.MetadataView as metadata view .
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
