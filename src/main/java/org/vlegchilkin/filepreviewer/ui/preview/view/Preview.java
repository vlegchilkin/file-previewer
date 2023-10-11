package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Abstract View for a Preview section.
 * - runs a SwingWorker background job for loading resource data from file specified.
 * - calls abstract build method to build a real view for the retrieved data.
 * - handles all exceptions cases and shows 'an exception view' in case of error.
 *
 * @param <T> the resource type that will be loaded via a background task.
 */
public abstract class Preview<T> extends JPanel {
    private final ResourceLoader resourceLoader;
    private final File file;
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

    protected ResourceLoader createResourceLoader() {
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

    protected abstract class ResourceLoader extends SwingWorker<T, Void> {
        final static Logger log = LoggerFactory.getLogger(Preview.class);

        /**
         * creates a new FileSystem object Zip archives, cause interruption of the thread kills original
         * FileChannel object.
         * So in case of a thread interruption the ZipFileSystem becomes closed.
         */
        protected byte[] readBytes(int len) throws IOException {
            Path path = getFile().toPath();
            if (path.getFileSystem().equals(FileSystems.getDefault())) {
                return readBytes(path, len);
            }

            try (FileSystem fs = FileSystems.newFileSystem(Path.of(path.getFileSystem().toString()))) {
                return readBytes(fs.getPath(path.toString()), len);
            }
        }

        private byte[] readBytes(Path path, int len) throws IOException {
            try (InputStream is = Files.newInputStream(path)) {
                if (len < 0) {
                    return is.readAllBytes();
                }

                byte[] buffer = new byte[len];
                int read = is.read(buffer);
                final byte[] result;
                if (read == -1) {
                    result = new byte[0];
                } else {
                    result = Arrays.copyOf(buffer, read);
                }
                return result;
            }
        }

        @Override
        protected void done() {
            PreviewException exception;
            try {
                T result = get();
                show(result);
                exception = null;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                log.warn("Error during resource processing", cause);
                if (cause instanceof PreviewException) {
                    exception = (PreviewException) cause;
                } else {
                    exception = new PreviewException(cause, PreviewException.ErrorCode.UNKNOWN_ERROR);
                }
            } catch (CancellationException | InterruptedException e) {
                exception = null;
            } catch (Exception e) {
                log.error("Critical error during resource processing", e);
                exception = new PreviewException(e, PreviewException.ErrorCode.UNKNOWN_ERROR);
            }

            if (exception != null) {
                show(exception);
            }
        }
    }
}
