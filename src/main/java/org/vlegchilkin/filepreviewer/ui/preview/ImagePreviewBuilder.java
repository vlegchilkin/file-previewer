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
    private final ImageIcon image;

    public ImagePreviewBuilder(File file, Metadata metadata) throws PreviewException {
        super(metadata);
        if (file.length() > ImagePreviewBuilder.MAX_SIZE) {
            throw new PreviewException(
                    PreviewException.ErrorCode.SIZE_LIMIT, FileUtils.byteCountToDisplaySize(MAX_SIZE)
            );
        }
        this.image = loadImage(file);
        this.getMetadata().information().put(
                "image.dimensions", "%d x %d".formatted(image.getIconWidth(), image.getIconHeight())
        );
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
    public JComponent buildContentView() {
        return new ImagePanel(this.image);
    }

    static class ImagePanel extends JPanel {
        private static final int MIN_PIXELS = Integer.parseInt(Main.PROPERTIES.getString("preview.image.min.pixels"));

        private final ImageIcon image;

        public ImagePanel(ImageIcon image) {
            this.image = image;

        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Rectangle bounds = g.getClipBounds();
            if (bounds.height < ImagePanel.MIN_PIXELS || bounds.width < ImagePanel.MIN_PIXELS) {
                return;
            }

            var scale = (float) Math.max(
                    image.getIconWidth() / bounds.getWidth(),
                    image.getIconHeight() / bounds.getHeight()
            );

            final int width, height;
            if (scale > 1) {
                width = Math.round(image.getIconWidth() / scale);
                height = Math.round(image.getIconHeight() / scale);
            } else {
                width = image.getIconWidth();
                height = image.getIconHeight();
            }

            int x = (bounds.width - width) / 2;
            int y = (bounds.height - height) / 2;
            g.drawImage(image.getImage(), x, y, width, height, null);
        }
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
