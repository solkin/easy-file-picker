package com.tomclaw.filepicker.files;

/**
 * Created by solkin 18.06.2017.
 */
public class FileItem {

    private String title;
    private String info;
    private int icon;
    private String path;

    public FileItem(String title, String info, int icon, String path) {
        this.title = title;
        this.info = info;
        this.icon = icon;
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public String getInfo() {
        return info;
    }

    public int getIcon() {
        return icon;
    }

    public String getPath() {
        return path;
    }
}
