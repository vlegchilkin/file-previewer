package org.vlegchilkin.filepreviewer.ui.preview;

import net.java.truevfs.access.TFileInputStream;
import org.apache.commons.io.FileUtils;
import org.vlegchilkin.filepreviewer.Main;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Preview Builder for images.
 * File max size is defined in the 'preview.image.max.size' property.
 */
public class ImagePreviewBuilder extends PreviewBuilder {
    private static final int MAX_SIZE = Integer.parseInt(Main.PROPERTIES.getString("preview.image.max.size"));
    private final BufferedImage image;

    public ImagePreviewBuilder(File file, Metadata metadata) throws PreviewException {
        super(metadata);
        if (file.length() > ImagePreviewBuilder.MAX_SIZE) {
            throw new PreviewException(
                    PreviewException.ErrorCode.SIZE_LIMIT, FileUtils.byteCountToDisplaySize(MAX_SIZE)
            );
        }
        this.image = loadImage(file);
        this.getMetadata().information().put(
                "image.dimensions", "%d x %d".formatted(image.getWidth(), image.getHeight())
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
    public JComponent buildContentView(int areaWidth, int areaHeight) {
        return new ImagePanel(this.image);
    }

    static class ImagePanel extends JPanel {
        private static final int MIN_PIXELS = Integer.parseInt(Main.PROPERTIES.getString("preview.image.min.pixels"));

        private final BufferedImage image;

        public ImagePanel(BufferedImage image) {
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
                    image.getWidth() / bounds.getWidth(),
                    image.getHeight() / bounds.getHeight()
            );

            final int width, height;
            if (scale > 1) {
                width = Math.round(image.getWidth() / scale);
                height = Math.round(image.getHeight() / scale);
            } else {
                width = image.getWidth();
                height = image.getHeight();
            }

            int x = (bounds.width - width) / 2;
            int y = (bounds.height - height) / 2;
            g.drawImage(image, x, y, width, height, null);
        }
    }


    private static BufferedImage loadImage(File file) throws PreviewException {
        try (TFileInputStream is = new TFileInputStream(file)) {
            return ImageIO.read(is);
        } catch (Exception e) {
            throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
        }
    }
}
