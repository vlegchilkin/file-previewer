package org.vlegchilkin.filepreviewer.ui.preview.zfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class ZFile extends File {
    private final Path path;
    private final boolean directory;
    private final boolean file;
    private final long length;
    private final boolean zip;
    private final File parent;
    private final boolean exists;

    private ZFile(Path path, boolean directory, boolean file, long length, boolean zip, File parent, boolean exists) {
        super(path.toString());
        this.path = path;
        this.directory = directory;
        this.file = file;
        this.length = length;
        this.zip = zip;
        this.parent = parent;
        this.exists = exists;
    }


    public static ZFile packed(File parent, Path zipPath) {
        long length;
        try {
            length = Files.size(zipPath);
        } catch (IOException e) {
            length = 0;
        }
        return new ZFile(zipPath, Files.isDirectory(zipPath), Files.isRegularFile(zipPath), length, false, parent, true);
    }

    public static ZFile nonPacked(File parent, File file) {
        boolean zip = file.isFile() && file.getName().endsWith(".zip");
        return new ZFile(file.toPath(), zip || file.isDirectory(), !zip && file.isFile(), file.length(), zip, parent, file.exists());
    }

    @Override
    public boolean exists() {
        return exists;
    }

    @Override
    public String getPath() {
        return this.path.toString();
    }

    public Path toPath() {
        return this.path;
    }

    public boolean isZip() {
        return this.zip;
    }

    @Override
    public boolean isDirectory() {
        return this.directory;
    }

    @Override
    public boolean isFile() {
        return this.file;
    }

    @Override
    public long length() {
        return this.length;
    }

    @Override
    public File getParentFile() {
        return this.parent;
    }

    @Override
    public String getParent() {
        return Optional.ofNullable(parent).map(File::getPath).orElse(null);
    }

    @Override
    public File getCanonicalFile() {
        return this;
    }

    @Override
    public String toString() {
        return "%s [isZip:%b, size %d]".formatted(getPath(), isZip(), length());
    }
}
