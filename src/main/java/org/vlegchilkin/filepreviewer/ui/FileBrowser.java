package org.vlegchilkin.filepreviewer.ui;

import net.java.truevfs.access.TArchiveDetector;
import net.java.truevfs.access.TFile;
import net.java.truevfs.access.swing.TFileSystemView;
import net.java.truevfs.access.swing.TFileView;
import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewPane;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The main app component written based on the JFileChooser.
 * Uses read-only mode and hides the file-name text field.
 * Uses <a href="http://truevfs.net">TrueVFS</a> library to support walk through archive files (zip)
 */
class FileBrowser extends JFileChooser {
    private static final String ARCHIVE_ICON_FILE = Main.PROPERTIES.getString("filebrowser.archive.icon.file");
    private static final String ARCHIVES_SUPPORTED = Main.PROPERTIES.getString("filebrowser.archives.supported");
    public static final java.util.List<String> FILTER_IMAGE_EXTENSIONS = List.of(
            Main.PROPERTIES.getString("filebrowser.filter.image.extensions").split(",")
    );
    public static final java.util.List<String> FILTER_TEXT_EXTENSIONS = List.of(
            Main.PROPERTIES.getString("filebrowser.filter.text.extensions").split(",")
    );

    static {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);
    }

    private static final SortedMap<String, String[]> CUSTOM_FILE_FILTERS = new TreeMap<>() {{
        put("Pictures", FILTER_IMAGE_EXTENSIONS.toArray(new String[0]));
        put("Text files", FILTER_TEXT_EXTENSIONS.toArray(new String[0]));
    }};

    public FileBrowser() {
        super(new TFileSystemView(FileSystemView.getFileSystemView(), new TArchiveDetector(ARCHIVES_SUPPORTED)));
        setFileView(new ArchiveFileView());
        setAccessory(new PreviewPane(this));
        setControlButtonsAreShown(false);
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
