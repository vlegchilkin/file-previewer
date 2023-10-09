package org.vlegchilkin.filepreviewer.ui.preview.view.image;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.view.Preview;
import org.vlegchilkin.filepreviewer.ui.preview.view.ResourceLoader;
import org.vlegchilkin.filepreviewer.ui.preview.loader.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ImagePreview extends Preview<Image> {
    private static final int MIN_PIXELS = Integer.parseInt(Main.PROPERTIES.getString("preview.image.min.pixels"));
    private Image image;

    public ImagePreview(File file) {
        super(file);
    }

    @Override
    public ResourceLoader<Image> getResourceLoader() {
        return new ImageLoader(this);
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
        this.image = resource;
        //todo find a way to update metadata
//        imagePreviewFactory.getMetadata().information().put(
//                "image.dimensions", "%d x %d".formatted(image.getWidth(this), image.getHeight(this))
//        );
        return new JLabel();
    }

}
