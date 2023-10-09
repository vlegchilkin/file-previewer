package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public abstract class Preview<T> extends JPanel {
    public final static ImageIcon LOADER_ICON = new ImageIcon(
            Objects.requireNonNull(
                    Preview.class.getClassLoader().getResource(
                            Main.PROPERTIES.getString("preview.image.loader.icon.file")
                    )
            )
    );
    public final static ImageIcon ERROR_ICON = new ImageIcon(
            Objects.requireNonNull(
                    Preview.class.getClassLoader().getResource(
                            Main.PROPERTIES.getString("preview.image.error.icon.file")
                    )
            )
    );
    protected ResourceLoader resourceLoader;

    private final JLabel status;
    public Preview() {
        super(new GridBagLayout());
        this.resourceLoader = null;
        this.status = new JLabel(null, LOADER_ICON, JLabel.CENTER);
        add(status);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (resourceLoader != null && !resourceLoader.isDone()) {
            resourceLoader.cancel(true);
        }
    }

    protected abstract JComponent build(T resource);

    private void render(JComponent view) {
        status.setVisible(false);
        add(view);
        view.setPreferredSize(getSize());
    }

    private void render(PreviewException previewException) {
        status.setIcon(ERROR_ICON);
        status.setText(previewException.getMessage());
        status.setVisible(true);
    }

    public abstract class ResourceLoader extends SwingWorker<T, Void> {
        @Override
        protected void done() {
            PreviewException exception = null;
            try {
                T result = get();
                render(build(result));
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
            if (exception != null) {
                render(exception);
            }
        }
    }

}
