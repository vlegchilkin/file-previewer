package org.vlegchilkin.filepreviewer.ui.preview.loader;

import net.java.truevfs.access.TFileInputStream;
import org.apache.commons.io.FileUtils;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;
import org.vlegchilkin.filepreviewer.ui.preview.view.Preview;
import org.vlegchilkin.filepreviewer.ui.preview.view.ResourceLoader;

import java.awt.*;

public class ImageLoader extends ResourceLoader<Image> {
    /**
     * prevents heavy (IO / create image) operations in case of fast scrolling.
     * waits for FOLLOW_LAG_MS if the file size exceeds FOLLOW_LAG_FILE_MIN_SIZE.
     * If it is just a scroll-through action then the thread will be cancelled before an actual processing.
     */
    private static final int FOLLOW_LAG_MS = Integer.parseInt(
            Main.PROPERTIES.getString("loader.image.follow-lag-ms")
    );
    private static final int FOLLOW_LAG_FILE_MIN_SIZE = Integer.parseInt(
            Main.PROPERTIES.getString("loader.image.follow-lag-file-min-size")
    );
    private static final int FILE_MAX_SIZE = Integer.parseInt(
            Main.PROPERTIES.getString("loader.image.file-max-size")
    );

    private final MediaTracker tracker;
    private Image image;

    public ImageLoader(Preview<Image> owner) {
        super(owner);
        tracker = new MediaTracker(owner);
    }

    @Override
    protected Image doInBackground() throws Exception {
        if (owner.getFile().length() > FILE_MAX_SIZE) {
            throw new PreviewException(
                    PreviewException.ErrorCode.SIZE_LIMIT, FileUtils.byteCountToDisplaySize(FILE_MAX_SIZE)
            );
        }

        if (owner.getFile().length() > FOLLOW_LAG_FILE_MIN_SIZE) {
            Thread.sleep(FOLLOW_LAG_MS);
        }

        try (TFileInputStream is = new TFileInputStream(owner.getFile())) {
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
