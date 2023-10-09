package org.vlegchilkin.filepreviewer.ui.preview.view;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.vlegchilkin.filepreviewer.ui.preview.Metadata;

import javax.swing.*;
import java.awt.*;

/**
 * A common view for metadata section.
 */
class MetadataView extends JPanel {
    public MetadataView(Metadata metadata) {
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
            JLabel infoLabel = new MetadataPreviewFactory.TranslatedJLabel("metadata.information");
            infoLabel.setFont(headerFont);
            info.add(infoLabel);
            info.add(new JLabel(""));

            for (var row : metadata.information().entrySet()) {
                info.add(new MetadataPreviewFactory.TranslatedJLabel(row.getKey(), JLabel.LEFT));
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
