package org.vlegchilkin.filepreviewer.ui;

import org.vlegchilkin.filepreviewer.Main;
import org.vlegchilkin.filepreviewer.ui.preview.PreviewPane;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
 * The main app component written based on the JFileChooser.
 * Uses read-only mode and hides the file-name text field.
 * Uses <a href="http://truevfs.net">TrueVFS</a> library to support walk through archive files (zip)
 */
public class FileBrowser extends JFileChooser {
    private static final String ARCHIVE_ICON_FILE = Main.PROPERTIES.getString("filebrowser.archive.icon.file");
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
        super(new ZFileSystemView());
        setFileView(new ZipFoldersFileView());
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

    @Override
    public void setCurrentDirectory(File dir) {
        ((ZFileSystemView)getFileSystemView()).syncZFS(dir);
        super.setCurrentDirectory(dir);
    }

    static class ZipFoldersFileView extends FileView {

        public final static Icon archiveIcon = new ImageIcon(
                Objects.requireNonNull(
                        ZipFoldersFileView.class.getClassLoader().getResource(FileBrowser.ARCHIVE_ICON_FILE)
                )
        );

        @Override
        public Icon getIcon(File f) {
            if (f instanceof ZFile && ((ZFile) f).isZip()) {
                return ZipFoldersFileView.archiveIcon;
            }
            return super.getIcon(f);
        }
    }
}
