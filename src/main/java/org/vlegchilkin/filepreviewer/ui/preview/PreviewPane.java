package org.vlegchilkin.filepreviewer.ui.preview;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.view.*;

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
                var previewFactory = makePreviewFactory(file);

                setTopComponent(previewFactory.createContentView(file));
                setBottomComponent(previewFactory.createMetadataView());
            }
        }
    }

    private static PreviewFactory makePreviewFactory(File file) {
        Metadata metadata = Metadata.of(file);

        PreviewFactory preview;
        try {
            if (metadata == null) {
                preview = new MetadataPreviewFactory(null);
            } else if (ImagePreviewFactory.isSupported(metadata)) {
                preview = new ImagePreviewFactory(metadata);
            } else if (TextPreviewFactory.isSupported(metadata)) {
                preview = new TextPreviewFactory(metadata);
            } else {
                preview = new UnsupportedPreviewFactory(file, metadata);
            }
        } catch (PreviewException e) {
            preview = new ErrorPreviewFactory(metadata, e);
        } catch (Exception e) {
            log.error("Can't create a preview builder for the file {}", file, e);
            preview = new ErrorPreviewFactory(
                    metadata,
                    new PreviewException(e, PreviewException.ErrorCode.UNKNOWN_ERROR)
            );
        }
        return preview;
    }
}
