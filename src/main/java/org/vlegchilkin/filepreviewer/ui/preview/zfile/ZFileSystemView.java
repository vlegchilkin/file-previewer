package org.vlegchilkin.filepreviewer.ui.preview.zfile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

public class ZFileSystemView extends DecoratingFileSystemView {
    final static Logger log = LoggerFactory.getLogger(ZFileSystemView.class);
    private FileSystem zipFileSystem;
    private final int maxFiles;

    public ZFileSystemView(int maxFiles) {
        super(FileSystemView.getFileSystemView());
        this.maxFiles = maxFiles;
    }

    public void syncZFS(File dir) {
        if (dir != null && dir.toPath().getFileSystem().equals(this.zipFileSystem)) {
            return;
        }

        if (this.zipFileSystem != null) {
            try {
                this.zipFileSystem.close();
            } catch (IOException e) {
                log.debug("ZipFileSystem closed with an error", e);
            } finally {
                this.zipFileSystem = null;
            }
        }

        try {
            if (dir instanceof ZFile && ((ZFile) dir).isZip()) {
                this.zipFileSystem = FileSystems.newFileSystem(dir.toPath());
            }
        } catch (IOException e) {
            log.debug("can't open ZipFileSystem", e);
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
    public ZFile getChild(File parent, String fileName) {
        for (ZFile child : getFiles(parent, false)) {
            if (child.getName().equals(fileName)) {
                return child;
            }
        }
        return null;
    }

    @Override
    public ZFile[] getFiles(File dir, boolean useFileHiding) {
        final ZFile[] files;
        if (this.zipFileSystem != null) {
            files = getZFSFiles(dir);
        } else {
            files = getSystemFSFiles(dir, useFileHiding);
        }
        return files;
    }

    /**
     * original system get files method is not limited by 'maxFiles' limit
     */
    private ZFile[] getSystemFSFiles(File dir, boolean useFileHiding) {
        File[] files = super.getFiles(dir, useFileHiding);
        return Arrays.stream(files).limit(maxFiles).map(file -> ZFile.nonPacked(dir, file)).toArray(ZFile[]::new);
    }

    private ZFile[] getZFSFiles(File dir) {
        final Path path;
        if (dir instanceof ZFile && ((ZFile) dir).isZip()) {
            path = this.zipFileSystem.getPath("/");
        } else {
            path = dir.toPath();
        }

        ZFile[] files;
        try (Stream<Path> stream = Files.list(path)) {
            files = stream.limit(this.maxFiles).map(p -> ZFile.packed(dir, p)).toArray(ZFile[]::new);
        } catch (IOException e) {
            files = new ZFile[0];
        }
        return files;
    }
}

