package com.tomclaw.filepicker.util;

import java.io.File;
import java.util.Comparator;

/**
 * Created by solki on 18.06.2017.
 */
public class LastModifiedComparator implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {
        long lm1 = file1.lastModified();
        long lm2 = file2.lastModified();
        if (lm1 < lm2) {
            return 1;
        } else if (lm1 > lm2) {
            return -1;
        } else {
            return 0;
        }
    }
}
