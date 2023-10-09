package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

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

    protected ResourceLoader resourceLoader;
    protected final File file;
    private final JLabel status;

    public Preview(File file) {
        super(new GridBagLayout());
        this.file = file;
        this.resourceLoader = null;
        this.status = new JLabel(null, StatusIcon.LOADER.getIcon(), JLabel.CENTER);
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
        status.setIcon(StatusIcon.ERROR.getIcon());
        status.setText(previewException.getMessage());
        status.setVisible(true);
    }

    public abstract class ResourceLoader extends SwingWorker<T, Void> {
        final static Logger log = LoggerFactory.getLogger(Preview.class);

        @Override
        protected void done() { // todo looks like too heavy
            PreviewException exception;
            try {
                T result = get();
                render(build(result));
                return;
            } catch (ExecutionException e) {
                log.warn("Error during resource processing", e);
                Throwable cause = e.getCause();
                if (cause instanceof PreviewException) {
                    exception = (PreviewException) cause;
                } else {
                    exception = new PreviewException(e.getCause(), PreviewException.ErrorCode.UNKNOWN_ERROR);
                }
            } catch (CancellationException | InterruptedException e) {
                exception = null;
            } catch (Exception e) {
                log.error("Critical error during resource processing", e);
                exception = new PreviewException(e, PreviewException.ErrorCode.UNKNOWN_ERROR);
            }
            if (exception != null) {
                render(exception);
            }
        }
    }

}
