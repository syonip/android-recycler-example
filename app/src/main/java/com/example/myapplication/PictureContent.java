package com.example.myapplication;

import android.net.Uri;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class PictureContent {
    static final List<PictureItem> ITEMS = new ArrayList<>();

    public static void loadSavedImages(File dir) {
        ITEMS.clear();
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                String absolutePath = file.getAbsolutePath();
                String extension = absolutePath.substring(absolutePath.lastIndexOf("."));
                if (extension.equals(".jpg")) {
                    loadImage(file);
                }
            }
        }
    }

    private static void loadImage(File file) {
        PictureItem newItem = new PictureItem();
        newItem.uri = Uri.fromFile(file);
        addItem(newItem);
    }

    private static void addItem(PictureItem item) {
        ITEMS.add(item);
    }
}
