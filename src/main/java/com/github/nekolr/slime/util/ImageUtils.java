package com.github.nekolr.slime.util;

import net.coobird.thumbnailator.Thumbnails;

import java.io.*;

public class ImageUtils {

    public static void compressImage(String image, int ltLimit, int gtLimit) throws IOException {
        File file = new File(image);
        compressImage(file, ltLimit, gtLimit);
    }

    public static void compressImage(File file, int ltLimit, int gtLimit) throws IOException {
        if (file.length() > ltLimit && file.length() < gtLimit) {
            compressImage(new FileInputStream(file), file, 0.95f, 1.0f);
        } else if (file.length() > gtLimit) {
            compressImage(new FileInputStream(file), file, 0.8f, 1.0f);
        }
    }

    public static void compressImage(InputStream input, File destination, float scale, float quality) throws IOException {
        Thumbnails.of(input)
                .scale(scale)
                .outputQuality(quality)
                .toFile(destination);
    }

}
