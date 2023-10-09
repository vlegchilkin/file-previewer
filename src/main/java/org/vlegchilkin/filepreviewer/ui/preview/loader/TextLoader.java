package org.vlegchilkin.filepreviewer.ui.preview.loader;

import net.java.truevfs.access.TFileReader;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewException;
import org.vlegchilkin.filepreviewer.ui.preview.view.Preview;
import org.vlegchilkin.filepreviewer.ui.preview.view.ResourceLoader;

import java.io.IOException;
import java.io.Reader;

public class TextLoader extends ResourceLoader<String> {
    private static final int TEXT_MAX_LENGTH = Integer.parseInt(
            Main.PROPERTIES.getString("loader.text.text-max-length")
    );

    public TextLoader(Preview<String> owner) {
        super(owner);
    }

    @Override
    protected String doInBackground() throws Exception {
        final int charsRead;
        char[] buffer = new char[(int) Math.min(owner.getFile().length(), TEXT_MAX_LENGTH)];
        try (Reader reader = new TFileReader(owner.getFile())) {
            charsRead = reader.read(buffer);
        } catch (IOException e) {
            throw new PreviewException(e, PreviewException.ErrorCode.UNABLE_TO_LOAD);
        }

        return String.valueOf(buffer, 0, charsRead);
    }
}
