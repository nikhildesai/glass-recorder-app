package com.iohackathon.classrecorder;

import android.app.Application;

public class ClassRecorderApplication extends Application {

    private static long lectureId;
    private static long lectureStartTime;

    public static synchronized long getLectureId() {
        return lectureId;
    }

    public static synchronized void setLectureId(long lectureId) {
        ClassRecorderApplication.lectureId = lectureId;
    }

    public static synchronized long getLectureStartTime() {
        return lectureStartTime;
    }

    public static synchronized void setLectureStartTime(long lectureStartTime) {
        ClassRecorderApplication.lectureStartTime = lectureStartTime;
    }

}
