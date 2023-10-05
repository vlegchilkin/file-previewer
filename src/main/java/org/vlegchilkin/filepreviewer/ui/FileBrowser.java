package org.vlegchilkin.filepreviewer.ui;

import net.java.truevfs.access.TArchiveDetector;
import net.java.truevfs.access.TFile;
import net.java.truevfs.access.swing.TFileSystemView;
import net.java.truevfs.access.swing.TFileView;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.ImagePreview;
import org.vlegchilkin.filepreviewer.ui.preview.TextPreview;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

class FileBrowser extends JFileChooser {
    private static final String APPROVE_BUTTON_TEXT = Main.PROPERTIES.getString("filebrowser.approve.button.text");
    private static final String CANCEL_BUTTON_TEXT = Main.PROPERTIES.getString("filebrowser.cancel.button.text");
    private static final String ARCHIVE_ICON_FILE = Main.PROPERTIES.getString("filebrowser.archive.icon.file");
    private static final String ARCHIVES_SUPPORTED = Main.PROPERTIES.getString("filebrowser.archives.supported");

    static {
        UIManager.put("FileChooser.cancelButtonText", CANCEL_BUTTON_TEXT);
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
    }

    private static final SortedMap<String, String[]> CUSTOM_FILE_FILTERS = new TreeMap<>() {{
        put("Pictures", ImagePreview.EXTENSIONS.toArray(new String[0]));
        put("Text files", TextPreview.EXTENSIONS.toArray(new String[0]));
    }};

    public FileBrowser() {
        super(new TFileSystemView(FileSystemView.getFileSystemView(), new TArchiveDetector(ARCHIVES_SUPPORTED)));
        setFileView(new ArchiveFileView());

        setDialogType(JFileChooser.CUSTOM_DIALOG);
        setApproveButtonText(APPROVE_BUTTON_TEXT);
        setFileSelectionMode(JFileChooser.FILES_ONLY);

        resetChoosableFileFilters();

        for (Map.Entry<String, String[]> entry : CUSTOM_FILE_FILTERS.entrySet()) {
            addChoosableFileFilter(new FileNameExtensionFilter(entry.getKey(), entry.getValue()));
        }
        removeTextFieldWithLabel(this);
    }

    private boolean removeTextFieldWithLabel(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextField) {
                container.getParent().remove(container);
                return true;
            }
            if (comp instanceof Container) {
                if (removeTextFieldWithLabel((Container) comp)) return true;
            }
        }
        return false;
    }

    static class ArchiveFileView extends TFileView {
        private final static Icon archiveIcon = new ImageIcon(
                Objects.requireNonNull(
                        ArchiveFileView.class.getClassLoader().getResource(FileBrowser.ARCHIVE_ICON_FILE)
                )
        );

        public ArchiveFileView() {
            super(null);
        }

        @Override
        public Icon getIcon(@Nonnull File f) {
            if (f instanceof TFile && ((TFile) f).isArchive()) {
                return ArchiveFileView.archiveIcon;
            }
            return super.getIcon(f);
        }
    }
}
