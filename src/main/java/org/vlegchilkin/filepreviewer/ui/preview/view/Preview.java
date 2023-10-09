package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;

public abstract class Preview<T> extends JPanel {
    protected final ResourceLoader<T> resourceLoader;
    protected final File file;
    private final JLabel status;

    public Preview(File file) {
        super(new GridBagLayout());
        this.file = file;
        this.status = new JLabel(null, Status.LOADING.getIcon(), JLabel.CENTER);
        add(status);
        this.resourceLoader = createResourceLoader();
        if (this.resourceLoader != null) {
            this.resourceLoader.execute();
        }
    }

    protected ResourceLoader<T> createResourceLoader() {
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

    public void show(T resource) {
        this.status.setVisible(false);
        JComponent view = build(resource);
        add(view);
        view.setPreferredSize(getSize());
    }

    public void show(PreviewException previewException) {
        this.status.setIcon(Status.ERROR.getIcon());
        this.status.setText(previewException.getMessage());
        this.status.setVisible(true);
    }

    public File getFile() {
        return file;
    }

    enum Status {
        LOADING("status.loading.icon-file"),
        ERROR("status.error.icon-file");
        private final ImageIcon icon;

        Status(String iconFile) {
            this.icon = new ImageIcon(
                    Objects.requireNonNull(
                            Status.class.getClassLoader().getResource(Main.PROPERTIES.getString(iconFile))
                    )
            );
        }

        public ImageIcon getIcon() {
            return icon;
        }
    }
}
