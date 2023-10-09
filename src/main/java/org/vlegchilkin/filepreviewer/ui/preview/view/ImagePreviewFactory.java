package org.vlegchilkin.filepreviewer.ui.preview.view;

import net.java.truevfs.access.TFileInputStream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Preview Builder for images.
 * File max size is defined in the 'preview.image.max.size' property.
 */
public class ImagePreviewFactory extends MetadataPreviewFactory {
    private static final int MAX_SIZE = Integer.parseInt(Main.PROPERTIES.getString("preview.image.max.size"));
    private final File file;

    public ImagePreviewFactory(File file, Metadata metadata) throws PreviewException {
        super(metadata);
        this.file = file;
    }

    /**
     * Check if it is possible to show the content as a scaled image.
     */
    public static boolean isSupported(Metadata metadata) throws PreviewException {
        if (metadata.mimeType() == null) {
            return false;
        }
        if (!metadata.mimeType().startsWith("image/") || "image/heic".equals(metadata.mimeType())) {
            return false;
        }
        if (metadata.fileSize() > ImagePreviewFactory.MAX_SIZE) {
            throw new PreviewException(
                    PreviewException.ErrorCode.SIZE_LIMIT, FileUtils.byteCountToDisplaySize(MAX_SIZE)
            );
        }
        return true;
    }

    @Override
    public JComponent createContentView() {
        return new ImagePreview();
    }

    public class ImagePreview extends JLabel {
        private static final int MIN_PIXELS = Integer.parseInt(Main.PROPERTIES.getString("preview.image.min.pixels"));
        public final static ImageIcon LOADER_ICON = new ImageIcon(
                Objects.requireNonNull(
                        ImagePreview.class.getClassLoader().getResource(
                                Main.PROPERTIES.getString("preview.image.loader.icon.file")
                        )
                )
        );
        public final static ImageIcon ERROR_ICON = new ImageIcon(
                Objects.requireNonNull(
                        ImagePreview.class.getClassLoader().getResource(
                                Main.PROPERTIES.getString("preview.image.error.icon.file")
                        )
                )
        );
        private final ImageLoader imageLoader;
        private Image image;

        public ImagePreview() {
            super(null, LOADER_ICON, CENTER);
            this.image = null;
            this.imageLoader = new ImageLoader();
            imageLoader.execute();
        }

        @Override
        public void removeNotify() {
            super.removeNotify();
            imageLoader.cancel(true);
            if (image != null) {
                image.flush();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (this.image == null) {
                return;
            }

            Rectangle bounds = g.getClipBounds();
            if (bounds.height < ImagePreview.MIN_PIXELS || bounds.width < ImagePreview.MIN_PIXELS) {
                return;
            }
            int width = image.getWidth(null), height = image.getHeight(null);

            var scale = (float) Math.max(width / bounds.getWidth(), height / bounds.getHeight());
            if (scale > 1) {
                width = Math.round(width / scale);
                height = Math.round(height / scale);
            }

            int x = (bounds.width - width) / 2;
            int y = (bounds.height - height) / 2;

            g.drawImage(image, x, y, width, height, null);
        }

        private class ImageLoader extends SwingWorker<Image, Void> {
            private final MediaTracker tracker = new MediaTracker(ImagePreview.this);

            @Override
            protected Image doInBackground() throws Exception {
                if (getMetadata().fileSize() > 0xFFFFF) {
                    Thread.sleep(100); // prevents heavy (IO / create image) operations in case of fast scrolling
                }

                Image image;
                try (TFileInputStream is = new TFileInputStream(file)) {
                    byte[] data = is.readAllBytes();
                    image = Toolkit.getDefaultToolkit().createImage(data);
                }
                tracker.addImage(image, 0);
                boolean completed = tracker.waitForID(0, 0);
                if (!completed || image.getWidth(null) < 0) {
                    throw new PreviewException(PreviewException.ErrorCode.UNABLE_TO_LOAD);
                }
                return image;
            }

            @Override
            protected void done() {
                PreviewException exception = null;
                try {
                    image = get();
                    getMetadata().information().put(
                            "image.dimensions", "%d x %d".formatted(
                                    image.getWidth(null),
                                    image.getHeight(null)
                            )
                    );
                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof PreviewException) {
                        exception = (PreviewException) cause;
                    } else {
                        exception = new PreviewException(e.getCause(), PreviewException.ErrorCode.UNKNOWN_ERROR);
                    }
                } catch (Exception e) {
                    exception = new PreviewException(e, PreviewException.ErrorCode.UNKNOWN_ERROR);
                }
                tracker.removeImage(image, 0);

                if (exception != null) {
                    setIcon(ERROR_ICON);
                    setText(exception.getMessage());
                } else {
                    setIcon(null);
                    setText("");
                }
            }
        }
    }
}
