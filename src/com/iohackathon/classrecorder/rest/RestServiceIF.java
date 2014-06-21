package com.iohackathon.classrecorder.rest;

public interface RestServiceIF {
    public static final String AUDIO_FILE_EXTENSION = ".3gp";
    public static final String PHOTO_FILE_EXTENSION = ".jpg";
    public static final String AUDIO_FILE_PARAM_NAME = "audio";
    public static final String PHOTO_FILE_PARAM_NAME = "photo";
    public static final String TIME_OFFSET_PARAM_NAME = "timeStamp";

    public void createLecture(String audioFileUrl);

    void addPhoto(String photoFileUrl, long offsetMilliSeconds);

}
