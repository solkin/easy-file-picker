package com.tomclaw.filepicker.util;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.tomclaw.filepicker.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created by Solkin on 18.10.2014.
 */
public class FileHelper {

    public static int getMimeTypeResPicture(String mimeType) {
        if (mimeType.startsWith("image")) {
            return R.drawable.files_img;
        } else if (mimeType.contains("compressed") ||
                mimeType.contains("zip") ||
                mimeType.contains("7z") ||
                mimeType.contains("rar")) {
            return R.drawable.files_zip;
        } else if (mimeType.contains("android") && mimeType.contains("package")) {
            return R.drawable.files_apk;
        } else if (mimeType.contains("text") || mimeType.contains("document") ||
                mimeType.contains("pdf") || mimeType.contains("html") || mimeType.contains("latex")) {
            return R.drawable.files_text;
        } else if (mimeType.contains("audio")) {
            return R.drawable.files_music;
        } else if (mimeType.contains("video") || mimeType.contains("flash")) {
            return R.drawable.files_video;
        } else {
            return R.drawable.files_unknown;
        }
    }

    public static String getMimeType(String path) {
        String type = null;
        String extension = getFileExtensionFromPath(path);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (TextUtils.isEmpty(type)) {
            type = "application/octet-stream";
        }
        return type;
    }

    public static String getFileBaseFromName(String name) {
        String base = name;
        if (!TextUtils.isEmpty(name)) {
            int index = name.lastIndexOf(".");
            if (index != -1) {
                base = name.substring(0, index);
            }
        }
        return base;
    }

    public static String getFileExtensionFromPath(String path) {
        String suffix = "";
        if (!TextUtils.isEmpty(path)) {
            int index = path.lastIndexOf(".");
            if (index != -1) {
                suffix = path.substring(index + 1);
            }
        }
        return suffix;
    }

    public static List<File> findRecentFiles(int limit, List<File> dirs) {
        List<File> files = new ArrayList<>();
        for (File dir : dirs) {
            File[] filesList = dir.listFiles();
            for (File file : filesList) {
                if (file.isFile() && !file.isHidden()) {
                    files.add(file);
                }
            }
        }
        Collections.sort(files, new LastModifiedComparator());
        return files.subList(0, limit);
    }

    public static HashSet<String> getExternalMounts() {
        final HashSet<String> out = new HashSet<String>();
        String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
        BufferedReader is = null;
        try {
            final Process process = new ProcessBuilder().command("mount")
                    .redirectErrorStream(true).start();
            process.waitFor();
            is = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = is.readLine()) != null) {
                if (!line.toLowerCase(Locale.US).contains("asec")) {
                    if (line.matches(reg)) {
                        String[] parts = line.split(" ");
                        for (String part : parts) {
                            if (part.startsWith("/")) {
                                if (!part.toLowerCase(Locale.US).contains("vold")) {
                                    out.add(part);
                                }
                            }
                        }
                    }
                }
            }
            is.close();
        } catch (final Exception ex) {
            Log.d("mounts scanner", ex.toString());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignored) {
                }
            }
        }
        return out;
    }
}
