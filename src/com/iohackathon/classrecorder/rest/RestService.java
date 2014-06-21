package com.iohackathon.classrecorder.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.iohackathon.classrecorder.ClassRecorderApplication;

public class RestService implements RestServiceIF {
    private final String baseUrl = "http://107.170.247.99:1337";
    private final String boundary = "===***===";
    private static final String LINE_FEED = "\r\n";
    private static RestService restService;
    private final String TAG = "RestService";

    private RestService() {

    }

    public static RestService getInstance() {
        if (restService == null) {
            restService = new RestService();
        }
        return restService;
    }

    @Override
    public void createLecture(String audioFileUrl) {
        CreateLectureTask task = new CreateLectureTask();
        task.execute(audioFileUrl);
    }

    @Override
    public void addPhoto(String photoFileUrl, long offsetMilliSeconds) {
        AddPhotoTask addPhotoTask = new AddPhotoTask();
        addPhotoTask.execute(photoFileUrl, offsetMilliSeconds);
    }

    class CreateLectureTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpClient cli = new DefaultHttpClient();
            HttpPost post = new HttpPost(baseUrl + "/lecture/create");
            MultipartEntity entity = new MultipartEntity();
            try {
                RandomAccessFile f = new RandomAccessFile(new File(params[0]), "r");
                byte[] b = new byte[(int) f.length()];
                f.read(b);
                ByteArrayBody byteArrayBody = new ByteArrayBody(b, "audio");
                entity.addPart("audio", byteArrayBody);
                post.setEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            post.setEntity(entity);
            HttpResponse httpResponse = null;
            try {
                httpResponse = cli.execute(post);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            JSONObject jsonObject;

            try {
                String inputLine = null;
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
                } catch (IllegalStateException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                StringBuffer buffer = new StringBuffer();
                try {
                    while ((inputLine = in.readLine()) != null) {
                        buffer.append(inputLine);
                    }
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String responseString = buffer.toString();
                Log.d(TAG, responseString);
                jsonObject = new JSONObject(responseString);
                ClassRecorderApplication.setLectureId((String) jsonObject.get("id"));
                Log.d(TAG, "lecture id=" + ClassRecorderApplication.getLectureId());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    class AddPhotoTask extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {
            HttpClient cli = new DefaultHttpClient();
            HttpPost post = new HttpPost(baseUrl + "/lecture/add_photo/" + ClassRecorderApplication.getLectureId());
            MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            try {
                MultipartEntity entity = new MultipartEntity();
                entity.addPart("photo", new FileBody(new File((String) params[0])));
                post.setEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            post.setEntity(reqEntity);
            HttpResponse httpResponse = null;
            try {
                httpResponse = cli.execute(post);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

}
