package org.vlegchilkin.filepreviewer.ui.preview;

import org.vlegchilkin.filepreviewer.Main;

public class PreviewException extends Exception {
    public enum ErrorCode {
        SIZE_LIMIT("preview.image.message.size.limit"),
        UNABLE_TO_LOAD("preview.image.message.unable.to.load"),
        UNKNOWN_ERROR("preview.image.message.unknown.error");
        private final String messageTemplate;

        ErrorCode(String alias) {
            this.messageTemplate = Main.PROPERTIES.getString(alias);
        }

        public String getMessage(Object... args) {
            return String.format(messageTemplate, args);
        }
    }

    public PreviewException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
    }

    public PreviewException(Exception cause, ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args), cause);
    }
}
