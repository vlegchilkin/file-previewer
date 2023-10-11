package org.vlegchilkin.filepreviewer.ui.preview.zfile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

abstract class DecoratingFileSystemView extends FileSystemView {

    /**
     * The decorated file system view.
     */
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
        return this.fsv.isRoot(f);
    }

    @Override
    public Boolean isTraversable(File f) {
        return this.fsv.isTraversable(f);
    }

    @Override
    public String getSystemDisplayName(File f) {
        return this.fsv.getSystemDisplayName(f);
    }

    @Override
    public String getSystemTypeDescription(File f) {
        return this.fsv.getSystemTypeDescription(f);
    }

    @Override
    public Icon getSystemIcon(File f) {
        return this.fsv.getSystemIcon(f);
    }

    @Override
    public boolean isParent(File folder, File file) {
        return this.fsv.isParent(folder, file);
    }

    @Override
    public File getChild(File parent, String fileName) {
        return this.fsv.getChild(parent, fileName);
    }

    @Override
    public boolean isFileSystem(File f) {
        return this.fsv.isFileSystem(f);
    }

    @Override
    public File createNewFolder(File containingDir) throws IOException {
        return this.fsv.createNewFolder(containingDir);
    }

    @Override
    public boolean isHiddenFile(File f) {
        return this.fsv.isHiddenFile(f);
    }

    @Override
    public boolean isFileSystemRoot(File dir) {
        return this.fsv.isFileSystemRoot(dir);
    }

    @Override
    public boolean isDrive(File dir) {
        return this.fsv.isDrive(dir);
    }

    @Override
    public boolean isFloppyDrive(File dir) {
        return this.fsv.isFloppyDrive(dir);
    }

    @Override
    public boolean isComputerNode(File dir) {
        return this.fsv.isComputerNode(dir);
    }

    @Override
    public File[] getRoots() {
        return this.fsv.getRoots();
    }

    @Override
    public File getHomeDirectory() {
        return this.fsv.getHomeDirectory();
    }

    @Override
    public File getDefaultDirectory() {
        return this.fsv.getDefaultDirectory();
    }

    @Override
    public File createFileObject(File dir, String filename) {
        return this.fsv.createFileObject(dir, filename);
    }

    @Override
    public File createFileObject(String path) {
        return this.fsv.createFileObject(path);
    }

    @Override
    public File[] getFiles(File dir, boolean useFileHiding) {
        return this.fsv.getFiles(dir, useFileHiding);
    }

    @Override
    public File getParentDirectory(File dir) {
        return this.fsv.getParentDirectory(dir);
    }
}
