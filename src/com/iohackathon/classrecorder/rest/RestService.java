package com.iohackathon.classrecorder.rest;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;

import com.iohackathon.classrecorder.ClassRecorderApplication;

public class RestService implements RestServiceIF {
    private final String baseUrl = "http://192.168.43.109:1337/";
    private final String boundary = "===***===";
    private static final String LINE_FEED = "\r\n";
    private static RestService restService;

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

            URL url = null;
            try {
                url = new URL(baseUrl + "/lecture/create");
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = null;
                try {
                    out = new BufferedOutputStream(urlConnection.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writeStream(out, params[0]);

                InputStream in = null;
                try {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                readStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        private void readStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        }

        private void writeStream(OutputStream out, String audioFileUrl) {
            try {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
                addFilePart(writer, new File(audioFileUrl), out);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void addFilePart(PrintWriter writer, File uploadFile, OutputStream outputStream) throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; \"; filename=\"" + fileName + "\"").append(LINE_FEED);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }

    }

    class AddPhotoTask extends AsyncTask<Object, String, String> {

        @Override
        protected String doInBackground(Object... params) {

            URL url = null;
            try {
                url = new URL(baseUrl + "/lecture/add_photo/" + ClassRecorderApplication.getLectureId());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }
            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);

                OutputStream out = null;
                try {
                    out = new BufferedOutputStream(urlConnection.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writeStream(out, (String) params[0], (Long) params[1]);

                InputStream in = null;
                try {
                    in = new BufferedInputStream(urlConnection.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                readStream(in);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        private void readStream(InputStream in) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        }

        private void writeStream(OutputStream out, String audioFileUrl, long offset) {
            try {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, "UTF-8"), true);
                addFilePart(writer, new File(audioFileUrl), out, offset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void addFilePart(PrintWriter writer, File uploadFile, OutputStream outputStream, long offset)
                throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; timeStamp=\"" + offset + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }

    }

}
