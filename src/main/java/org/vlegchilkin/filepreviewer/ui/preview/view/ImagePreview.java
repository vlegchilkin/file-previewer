package org.vlegchilkin.filepreviewer.ui.preview.view;

import net.java.truevfs.access.TFileInputStream;
import org.apache.commons.io.FileUtils;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImagePreview extends Preview<Image> {
    private static final int MAX_SIZE = Integer.parseInt(Main.PROPERTIES.getString("preview.image.max.size"));
    private static final int MIN_PIXELS = Integer.parseInt(Main.PROPERTIES.getString("preview.image.min.pixels"));
    private Image image;

    public ImagePreview(File file) {
        super(file);
        this.image = null;
        this.resourceLoader = new ImageLoader();
        this.resourceLoader.execute();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
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

    @Override
    protected JComponent build(Image resource) {
        image = resource;
        //todo find a way to update metadata
//        imagePreviewFactory.getMetadata().information().put(
//                "image.dimensions", "%d x %d".formatted(image.getWidth(this), image.getHeight(this))
//        );
        return new JLabel();
    }

    private class ImageLoader extends ResourceLoader {
        private final MediaTracker tracker = new MediaTracker(ImagePreview.this);

        @Override
        protected Image doInBackground() throws Exception {
            if (file.length() > MAX_SIZE) {
                throw new PreviewException(
                        PreviewException.ErrorCode.SIZE_LIMIT, FileUtils.byteCountToDisplaySize(MAX_SIZE)
                );
            }

            if (file.length() > 0xFFFFF) { // todo move to properties
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
            super.done();
            tracker.removeImage(image, 0);
        }
    }
}
