package com.iohackathon.classrecorder;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;

public class GlassRecorderApplication extends Application {

    private static String lectureId;
    private static long lectureStartTime;
    private static List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();

    public synchronized static void reset() {
        lectureId = "";
        lectureStartTime = 0;
        imageInfoList.clear();
    }

    public static synchronized String getLectureId() {
        return lectureId;
    }

    public static synchronized void setLectureId(String lectureId) {
        GlassRecorderApplication.lectureId = lectureId;
    }

    public static synchronized long getLectureStartTime() {
        return lectureStartTime;
    }

    public static synchronized void setLectureStartTime(long lectureStartTime) {
        GlassRecorderApplication.lectureStartTime = lectureStartTime;
    }

    public synchronized static void addImageInfo(String path, long offset) {
        ImageInfo imageInfo = new ImageInfo(path, offset);
        imageInfoList.add(imageInfo);
    }

    public synchronized static List<ImageInfo> getAllImages() {
        List<ImageInfo> imageInfos = new ArrayList<ImageInfo>();
        imageInfos.addAll(imageInfoList);
        return imageInfos;
    }

    public static class ImageInfo {
        private String path;
        private long offset;

        public ImageInfo(String path, long offset) {
            this.path = path;
            this.offset = offset;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }
    }

}
