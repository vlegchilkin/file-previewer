package org.vlegchilkin.filepreviewer.ui.preview.view.image;

import org.apache.commons.io.FileUtils;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewPane;
import org.vlegchilkin.filepreviewer.ui.preview.view.Preview;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Preview view for Image files.
 * - Uses FOLLOW_LAG_MS/FOLLOW_LAG_FILE_MIN_SIZE pair to skip heavy operations on huge files.
 * - FILE_MAX_SIZE is the file limit
 * - doesn't show a preview if the area size smaller than MIN_PIXELS by any dimension
 */
public class ImagePreview extends Preview<Image> {
    /**
     * prevents heavy (IO / create image) operations in case of fast scrolling.
     * waits for FOLLOW_LAG_MS if the file size exceeds FOLLOW_LAG_FILE_MIN_SIZE.
     * If it is just a scroll-through action then the thread will be cancelled before an actual processing.
     */
    private static final int FOLLOW_LAG_MS = Integer.parseInt(Main.PROPERTIES.getString("preview.image.follow-lag-ms"));
    private static final int FOLLOW_LAG_FILE_MIN_SIZE = Integer.parseInt(Main.PROPERTIES.getString("preview.image.follow-lag-file-min-size"));
    private static final int FILE_MAX_SIZE = Integer.parseInt(Main.PROPERTIES.getString("preview.image.file-max-size"));
    private static final int MIN_PIXELS = Integer.parseInt(Main.PROPERTIES.getString("preview.image.min-pixels"));
    private Image image;

    public ImagePreview(File file) {
        super(file);
    }

    @Override
    protected ResourceLoader createResourceLoader() {
        return new ResourceLoader() {

            private final MediaTracker tracker = new MediaTracker(ImagePreview.this);
            private Image image;

            @Override
            protected Image doInBackground() throws Exception {
                if (getFile().length() > ImagePreview.FILE_MAX_SIZE) {
                    throw new PreviewException(
                            PreviewException.ErrorCode.SIZE_LIMIT,
                            FileUtils.byteCountToDisplaySize(ImagePreview.FILE_MAX_SIZE)
                    );
                }

                if (getFile().length() > ImagePreview.FOLLOW_LAG_FILE_MIN_SIZE) {
                    Thread.sleep(ImagePreview.FOLLOW_LAG_MS);
                }

                byte[] data = readBytes(-1);
                this.image = Toolkit.getDefaultToolkit()
                        .createImage(data)
                        .getScaledInstance(PreviewPane.PANE_WIDTH * 2, -1, Image.SCALE_AREA_AVERAGING);

                tracker.addImage(this.image, 0);

                boolean completed = this.tracker.waitForID(0, 0);
                if (!completed || this.image.getWidth(null) < 0) {
                    throw new PreviewException(PreviewException.ErrorCode.UNABLE_TO_LOAD);
                }
                return this.image;
            }

            @Override
            protected void done() {
                super.done();
                this.tracker.removeImage(this.image, 0);
            }
        };
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (this.image != null) {
            this.image.flush();
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
        int width = this.image.getWidth(null), height = this.image.getHeight(null);

        var scale = (float) Math.max(width / bounds.getWidth(), height / bounds.getHeight());
        if (scale > 1) {
            width = Math.round(width / scale);
            height = Math.round(height / scale);
        }

        int x = (bounds.width - width) / 2;
        int y = (bounds.height - height) / 2;

        g.drawImage(this.image, x, y, width, height, null);
    }

    @Override
    protected JComponent build(Image resource) {
        this.image = resource;
        return new JLabel();
    }

}
