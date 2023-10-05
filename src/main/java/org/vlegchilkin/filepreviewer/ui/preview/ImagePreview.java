package org.vlegchilkin.filepreviewer.ui.preview;

import net.java.truevfs.access.TFileInputStream;
import org.apache.commons.io.FileUtils;
import org.vlegchilkin.filepreviewer.Main;

import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class ImagePreview extends PreviewDialog {
    public static final List<String> EXTENSIONS = List.of(Main.PROPERTIES.getString("preview.image.extensions").split(","));
    private static final int AREA_WIDTH = Integer.parseInt(Main.PROPERTIES.getString("preview.image.area.width"));
    private static final int AREA_HEIGHT = Integer.parseInt(Main.PROPERTIES.getString("preview.image.area.height"));
    private static final int MAX_SIZE = Integer.parseInt(Main.PROPERTIES.getString("preview.image.max.size"));

    static {
        if (AREA_WIDTH * AREA_HEIGHT == 0) {
            throw new IllegalStateException(
                    String.format("Wrong configuration, image preview area is zero (%d x %d)", AREA_WIDTH, AREA_HEIGHT)
            );
        }
    }

    public ImagePreview(Frame owner, File file) {
        super(owner, file);
    }

    @Override
    void prepareView(File file) throws PreviewException {
        if (file.length() > ImagePreview.MAX_SIZE) {
            throw new PreviewException(
                    PreviewException.ErrorCode.SIZE_LIMIT, FileUtils.byteCountToDisplaySize(MAX_SIZE)
            );
        }

        Icon icon = buildScaledIcon(file);
        add(new JLabel("", icon, SwingConstants.CENTER), BorderLayout.CENTER);
        setResizable(false);
    }

    public static Icon buildScaledIcon(File file) throws PreviewException {
        ImageIcon preview = loadImage(file);

        var scale = Math.max(
                preview.getIconWidth() / (float) AREA_WIDTH,
                preview.getIconHeight() / (float) AREA_HEIGHT
        );

        if (scale > 1) {
            int scaledWidth = Math.round(preview.getIconWidth() / scale);
            Image scaledImage = preview.getImage().getScaledInstance(scaledWidth, -1, Image.SCALE_FAST);
            preview = new ImageIcon(scaledImage);
        }

        return preview;
    }

    public static ImageIcon loadImage(File file) throws PreviewException {
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
    public static ImageIcon buildImage(byte[] data) throws IOException {
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
    public static ImageIcon buildImageFallback(byte[] data) throws IOException {
        return new ImageIcon(ImageIO.read(new ByteArrayInputStream(data)));
    }
}
