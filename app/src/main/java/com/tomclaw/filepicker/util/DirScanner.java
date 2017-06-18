package com.tomclaw.filepicker.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by solkin on 18.06.2017.
 */
public class DirScanner {

    private Callback callback;
    private ExecutorService executor;
    private List<File> result;
    private AtomicLong dirsRemain = new AtomicLong();

    public DirScanner(int threads) {
        executor = Executors.newFixedThreadPool(threads);
        result = new ArrayList<>();
    }

    public void scan(Callback callback, File... dir) {
        if (dirsRemain.get() != 0) {
            throw new IllegalStateException("DirScanner is busy");
        }
        this.callback = callback;
        result.clear();
        scan(Arrays.asList(dir));
    }

    private void scan(List<File> dirs) {
        for (File dir : dirs) {
            if (dir.isDirectory() && !dir.isHidden()) {
                result.add(dir);
                ScanTask scanTask = new ScanTask(dir);
                executor.submit(scanTask);
            }
        }
        if (dirsRemain.get() == 0) {
            callback.onCompleted(result);
        }
    }

    private class ScanTask implements Runnable {

        private File dir;

        public ScanTask(File dir) {
            this.dir = dir;
            dirsRemain.incrementAndGet();
        }

        @Override
        public void run() {
            File[] files = dir.listFiles();
            List<File> nextDirs = Collections.emptyList();
            if (files.length > 0) {
                 nextDirs = new ArrayList<>();
                for (File file : files) {
                    if (file.isDirectory()) {
                        nextDirs.add(file);
                    }
                }
            }
            dirsRemain.decrementAndGet();
            scan(nextDirs);
        }
    }

    public interface Callback {

        void onCompleted(List<File> dirs);

    }

}
