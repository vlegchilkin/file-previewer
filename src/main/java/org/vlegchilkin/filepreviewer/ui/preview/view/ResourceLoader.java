package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;

import javax.swing.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

public abstract class ResourceLoader<T> extends SwingWorker<T, Void> {
    final static Logger log = LoggerFactory.getLogger(Preview.class);
    protected final Preview<T> owner;

    public ResourceLoader(Preview<T> owner) {
        this.owner = owner;
    }

    @Override
    protected void done() {
        PreviewException exception;
        try {
            T result = get();
            owner.show(result);
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
            owner.show(exception);
        }
    }
}
