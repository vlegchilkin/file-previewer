package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Default Preview Builder with common metadata builder and an empty content builder.
 */
public class MetadataPreviewFactory implements PreviewFactory {
    private final Metadata metadata;

    public MetadataPreviewFactory(Metadata metadata) {
        this.metadata = metadata;
    }

    protected Metadata getMetadata() {
        return metadata;
    }

    public JComponent createContentView() {
        return new JLabel();
    }
    public JComponent createMetadataView() {
        return new MetadataView();
    }

    /**
     * JLabel that passes the text field via translation.
     */
    static class TranslatedJLabel extends JLabel {
        public TranslatedJLabel(String text) {
            this(text, LEADING);
        }

        public TranslatedJLabel(String text, int horizontalAlignment) {
            super(Main.PROPERTIES.getString(text), horizontalAlignment);
        }
    }

    /**
     * A common view for metadata section.
     */
    class MetadataView extends JPanel {
        public MetadataView() {
            super();
            if (metadata == null) {
                return;
            }
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            Font headerFont = new Font("Courier", Font.BOLD, 12);

            JPanel header = new JPanel(new GridLayout(0, 1));
            JLabel filename = new JLabel(metadata.fileName(), JLabel.LEFT);
            filename.setFont(headerFont);
            header.add(filename);

            String type = StringUtils.isEmpty(metadata.mimeType()) ? "?" : metadata.mimeType();
            String description = "%s - %s".formatted(type, FileUtils.byteCountToDisplaySize(metadata.fileSize()));
            header.add(new JLabel(description));

            add(header);

            if (!metadata.information().isEmpty()) {
                JPanel info = new JPanel(new GridLayout(0, 2));
                JLabel infoLabel = new TranslatedJLabel("metadata.information");
                infoLabel.setFont(headerFont);
                info.add(infoLabel);
                info.add(new JLabel(""));

                for (var row : metadata.information().entrySet()) {
                    info.add(new TranslatedJLabel(row.getKey(), JLabel.LEFT));
                    info.add(new JLabel(row.getValue().toString(), JLabel.RIGHT));
                }

                while (info.getComponentCount() < 10) {
                    info.add(new JLabel(""));
                    info.add(new JLabel(""));
                }

                add(info);
            }
        }
    }
}
