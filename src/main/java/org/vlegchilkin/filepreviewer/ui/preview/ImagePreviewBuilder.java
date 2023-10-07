package org.vlegchilkin.filepreviewer.ui.preview;

import net.java.truevfs.access.TFileInputStream;
import org.apache.commons.io.FileUtils;
import org.vlegchilkin.filepreviewer.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Preview Builder for images.
 * File max size is defined in the 'preview.image.max.size' property.
 * uses AWT for loading images with fallback to slow ImageIO in case of awt failure (some rare png crc issues).
 */
public class ImagePreviewBuilder extends PreviewBuilder {
    private static final int MAX_SIZE = Integer.parseInt(Main.PROPERTIES.getString("preview.image.max.size"));

    private final ImageIcon imageIcon;

    public ImagePreviewBuilder(File file, Metadata metadata) throws PreviewException {
        super(metadata);
        if (file.length() > ImagePreviewBuilder.MAX_SIZE) {
            throw new PreviewException(
                    PreviewException.ErrorCode.SIZE_LIMIT, FileUtils.byteCountToDisplaySize(MAX_SIZE)
            );
        }
        ImageIcon image = loadImage(file);
        this.getMetadata().information().put(
            "image.dimensions", "%d x %d".formatted(image.getIconWidth(), image.getIconHeight())
        );
        this.imageIcon = image;
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
    public JComponent buildContentView(int areaWidth, int areaHeight) {
        Icon icon = scaleIcon(this.imageIcon, areaWidth, areaHeight);
        return new JLabel("", icon, SwingConstants.CENTER);
    }

    private static ImageIcon scaleIcon(ImageIcon imageIcon, int areaWidth, int areaHeight) {
        if (areaWidth < 16 || areaHeight < 16) {
            areaWidth = areaHeight = 16;
        }
        var scale = Math.max(
                imageIcon.getIconWidth() / (float) (areaWidth),
                imageIcon.getIconHeight() / (float) (areaHeight)
        );

        final ImageIcon result;
        if (scale > 1) {
            int scaledWidth = Math.round(imageIcon.getIconWidth() / scale);
            Image scaledImage = imageIcon.getImage().getScaledInstance(scaledWidth, -1, Image.SCALE_FAST);
            result = new ImageIcon(scaledImage);
        } else {
            result = imageIcon;
        }

        return result;
    }

    private static ImageIcon loadImage(File file) throws PreviewException {
        try (TFileInputStream is = new TFileInputStream(file)) {
            byte[] data = is.readAllBytes();
            return buildImage(data);
        } catch (IOException e) {
            throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
        }
    }

    /**
     * Build an image using AWT first with fallback to javax.imageio
     */
    private static ImageIcon buildImage(byte[] data) throws IOException {
        ImageIcon imageIcon = new ImageIcon(data);
        if (imageIcon.getImageLoadStatus() != MediaTracker.COMPLETE || imageIcon.getIconWidth() < 0) {
            imageIcon = buildImageFallback(data);
        }
        return imageIcon;
    }

    /**
     * sometimes AWT doesn't build an image because of crc corruption or other reasons
     * so this is a fallback via slow javax.imageio
     */
    private static ImageIcon buildImageFallback(byte[] data) throws IOException {
        return new ImageIcon(ImageIO.read(new ByteArrayInputStream(data)));
    }
}
