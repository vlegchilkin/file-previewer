package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public abstract class Preview<T> extends JPanel {

    enum StatusIcon {
        LOADER("preview.image.loader.icon.file"),
        ERROR("preview.image.error.icon.file");
        private final ImageIcon icon;

        StatusIcon(String key) {
            this.icon = new ImageIcon(
                    Objects.requireNonNull(
                            StatusIcon.class.getClassLoader().getResource(Main.PROPERTIES.getString(key))
                    )
            );
        }

        public ImageIcon getIcon() {
            return icon;
        }
    }

    protected final ResourceLoader<T> resourceLoader;
    protected final File file;
    private final JLabel status;

    public Preview(File file) {
        super(new GridBagLayout());
        this.file = file;
        this.status = new JLabel(null, StatusIcon.LOADER.getIcon(), JLabel.CENTER);
        add(status);
        this.resourceLoader = getResourceLoader();
        if (this.resourceLoader != null) {
            this.resourceLoader.execute();
        }
    }

    public ResourceLoader<T> getResourceLoader() {
        return null;
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (resourceLoader != null && !resourceLoader.isDone()) {
            resourceLoader.cancel(true);
        }
    }

    protected abstract JComponent build(T resource);

    public void render(JComponent view) {
        status.setVisible(false);
        add(view);
        view.setPreferredSize(getSize());
    }

    public void render(PreviewException previewException) {
        status.setIcon(StatusIcon.ERROR.getIcon());
        status.setText(previewException.getMessage());
        status.setVisible(true);
    }

    public File getFile() {
        return file;
    }
}
