package org.vlegchilkin.filepreviewer.ui.preview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlegchilkin.filepreviewer.Main;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;


/**
 * Preview Pane, contains of two vertical-aligned sections - content and file metadata.
 */
public class PreviewPane extends JSplitPane implements PropertyChangeListener {
    final static Logger log = LoggerFactory.getLogger(PreviewPane.class);
    private static final int PANE_WIDTH = Integer.parseInt(Main.PROPERTIES.getString("preview.pane.width"));

    public PreviewPane(JFileChooser fileChooser) {
        super(VERTICAL_SPLIT);
        fileChooser.addPropertyChangeListener(this);
        setPreferredSize(new Dimension(PreviewPane.PANE_WIDTH, -1));
        setTopComponent(new JLabel(""));
        setBottomComponent(new JLabel(""));
        setResizeWeight(1);
        setDividerSize(0);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case JFileChooser.SELECTED_FILE_CHANGED_PROPERTY, JFileChooser.DIRECTORY_CHANGED_PROPERTY -> {
                File file = (File) evt.getNewValue();
                var previewBuilder = makeBuilder(file);

                int width = getTopComponent().getWidth();
                int height = getTopComponent().getHeight();
                setTopComponent(previewBuilder.buildContentView(width, height));
                setBottomComponent(previewBuilder.buildMetadataView());
            }
        }
    }

    private static PreviewBuilder makeBuilder(File file) {
        Metadata metadata = Metadata.of(file);
        if (metadata == null) {
            return new PreviewBuilder(null);
        }

        PreviewBuilder preview;
        try {
            if (ImagePreviewBuilder.isSupported(metadata)) {
                preview = new ImagePreviewBuilder(file, metadata);
            } else if (TextPreviewBuilder.isSupported(metadata)) {
                preview = new TextPreviewBuilder(file, metadata);
            } else {
                preview = new UnsupportedPreviewBuilder(file, metadata);
            }
        } catch (PreviewException e) {
            preview = new ErrorPreviewBuilder(metadata, e);
        } catch (Exception e) {
            log.error("Can't create a preview builder for the file {}", file, e);
            preview = new ErrorPreviewBuilder(
                    metadata,
                    new PreviewException(e, PreviewException.ErrorCode.UNKNOWN_ERROR)
            );
        }
        return preview;
    }
}
