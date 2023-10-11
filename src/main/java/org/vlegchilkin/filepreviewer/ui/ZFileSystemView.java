package org.vlegchilkin.filepreviewer.ui;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ZFileSystemView extends DecoratingFileSystemView {

    private FileSystem zfs;

    public ZFileSystemView() {
        super(FileSystemView.getFileSystemView());
    }

    void syncZFS(File dir) {
        if (dir != null && dir.toPath().getFileSystem().equals(this.zfs)) {
            return;
        }

        if (this.zfs != null) {
            try {
                this.zfs.close();
            } catch (IOException e) {
                //
            } finally {
                this.zfs = null;
            }
        }

        try {
            if (dir instanceof ZFile && ((ZFile) dir).isZip()) {
                this.zfs = FileSystems.newFileSystem(dir.toPath());
            }
        } catch (IOException e) {
            //
        }
    }

    @Override
    public Boolean isTraversable(File f) {
        return (f instanceof ZFile && ((ZFile) f).isZip()) || f.isDirectory();
    }

    @Override
    public File getParentDirectory(File dir) {
        return dir.getParentFile();
    }

    @Override
    public File getChild(File parent, String fileName) {
        File[] children = getFiles(parent, false);
        for (File child : children) {
            if (child.getName().equals(fileName)) {
                return child;
            }
        }
        return super.getChild(parent, fileName);
    }

    @Override
    public File[] getFiles(File dir, boolean useFileHiding) {
        File[] files;
        if (zfs != null) {
            Path path;
            if (dir instanceof ZFile && ((ZFile) dir).isZip()) {
                path = zfs.getPath("/");
            } else {
                path = dir.toPath();
            }
            try {
                try (Stream<Path> stream = Files.list(path)) {
                    files = stream.map(p ->ZFile.packed(dir, p)).toArray(File[]::new);
                }
            } catch (IOException e) {
                files = new File[0];
            }
        } else {
            List<File> zFiles = new LinkedList<>();
            for (File file : super.getFiles(dir, useFileHiding)) {
                zFiles.add(ZFile.nonPacked(dir, file));
            }
            files = zFiles.toArray(new File[0]);
        }
        return files;
    }
}

abstract class DecoratingFileSystemView extends FileSystemView {

    /** The decorated file system view. */
    protected final FileSystemView fsv;

    /**
     * Creates a new decorating file system view.
     *
     * @param fsv the file system view to decorate.
     */
    protected DecoratingFileSystemView(final FileSystemView fsv) {
        this.fsv = Objects.requireNonNull(fsv);
    }

    @Override
    public boolean isRoot(File f) {
        return fsv.isRoot(f);
    }

    @Override
    public Boolean isTraversable(File f) {
        return fsv.isTraversable(f);
    }

    @Override
    public String getSystemDisplayName(File f) {
        return fsv.getSystemDisplayName(f);
    }

    @Override
    public String getSystemTypeDescription(File f) {
        return fsv.getSystemTypeDescription(f);
    }

    @Override
    public Icon getSystemIcon(File f) {
        return fsv.getSystemIcon(f);
    }

    @Override
    public boolean isParent(File folder, File file) {
        return fsv.isParent(folder, file);
    }

    @Override
    public File getChild(File parent, String fileName) {
        return fsv.getChild(parent, fileName);
    }

    @Override
    public boolean isFileSystem(File f) {
        return fsv.isFileSystem(f);
    }

    @Override
    public File createNewFolder(File containingDir) throws IOException {
        return fsv.createNewFolder(containingDir);
    }

    @Override
    public boolean isHiddenFile(File f) {
        return fsv.isHiddenFile(f);
    }

    @Override
    public boolean isFileSystemRoot(File dir) {
        return fsv.isFileSystemRoot(dir);
    }

    @Override
    public boolean isDrive(File dir) {
        return fsv.isDrive(dir);
    }

    @Override
    public boolean isFloppyDrive(File dir) {
        return fsv.isFloppyDrive(dir);
    }

    @Override
    public boolean isComputerNode(File dir) {
        return fsv.isComputerNode(dir);
    }

    @Override
    public File[] getRoots() {
        return fsv.getRoots();
    }

    @Override
    public File getHomeDirectory() {
        return fsv.getHomeDirectory();
    }

    @Override
    public File getDefaultDirectory() {
        return fsv.getDefaultDirectory();
    }

    @Override
    public File createFileObject(File dir, String filename) {
        return fsv.createFileObject(dir, filename);
    }

    @Override
    public File createFileObject(String path) {
        return fsv.createFileObject(path);
    }

    @Override
    public File[] getFiles(File dir, boolean useFileHiding) {
        return fsv.getFiles(dir, useFileHiding);
    }

    @Override
    public File getParentDirectory(File dir) {
        return fsv.getParentDirectory(dir);
    }
}
