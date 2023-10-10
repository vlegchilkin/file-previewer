package org.vlegchilkin.filepreviewer.ui.preview;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * File metadata.
 *
 * @param fileName    - file name
 * @param fileSize    - file size
 * @param mimeType    - media type (MIME) of the file
 * @param information - additional info, sorted map that metadata view uses for information section.
 */
public record Metadata(String fileName, long fileSize, String mimeType, Information information) {
    public static Metadata of(File file) {
        if (file == null || !file.isFile()) {
            return null;
        }
        String mimeType = null;
        try {
            mimeType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            PreviewPane.log.warn("Can't recognize content type for file {}", file, e);
        }

        return new Metadata(file.getName(), file.length(), mimeType, Information.of(file));
    }


    /**
     * Additional information, sorted map that metadata view uses for information section.
     */
    public static class Information extends LinkedHashMap<String, Object> {
        private static final DateTimeFormatter FILETIME_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss");

        private static Information of(File file) {
            final Information result = new Information();

            BasicFileAttributes attributes = null;
            try {
                attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            } catch (IOException e) {
                PreviewPane.log.warn("Can't get file attributes for file {}", file, e);
            }

            Optional.ofNullable(attributes).ifPresent(attr -> {
                result.putIfDefined("file.creation.time", attr.creationTime());
                result.putIfDefined("file.last.modified.time", attr.lastModifiedTime());
                result.putIfDefined("file.last.access.time", attr.lastAccessTime());
            });

            return result;
        }

        public void putIfDefined(String key, FileTime fileTime) {
            if (fileTime.toMillis() > 0) {
                LocalDateTime localDateTime = fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                put(key, localDateTime.format(Information.FILETIME_FORMATTER));
            }
        }
    }
}
