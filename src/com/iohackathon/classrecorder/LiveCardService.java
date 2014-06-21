package com.iohackathon.classrecorder;

import java.io.File;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;
import com.iohackathon.classrecorder.rest.RestService;

public class LiveCardService extends Service {

    private static final String LIVE_CARD_TAG = "recorder";

    private RemoteViews mLiveCardView;
    private final String TAG = "LiveCardService";

    private LiveCard mLiveCard;

    private Worker worker;

    private MediaRecorder mRecorder = null;

    private final int STOP = 1;
    private final int START = 0;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("LiveCardService", "onBind()");

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (mLiveCard == null) {
            ClassRecorderApplication.setLectureStartTime(System.currentTimeMillis());

            Log.d(TAG, "creating new live card");
            mLiveCard = new LiveCard(this, LIVE_CARD_TAG);

            mLiveCardView = new RemoteViews(getPackageName(), R.layout.activity_main);
            mLiveCardView.setTextViewText(R.id.app_state_text, getString(R.string.lecture_started_text));

            Intent menuIntent = new Intent(this, MenuActivity.class);
            menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
            mLiveCard.attach(this);
            mLiveCard.publish(PublishMode.REVEAL);

            mLiveCard.setViews(mLiveCardView);

            worker = new Worker();
            Message msg = new Message();
            msg.arg1 = START;
            worker.runMessage(msg);
        } else {
            Log.d(TAG, "navigate to live card");
            mLiveCard.navigate();
        }

        if (intent != null && intent.getExtras() != null && intent.getExtras().getBoolean("stop") == true) {
            if (mLiveCard != null && mLiveCard.isPublished()) {
                mLiveCard.unpublish();
                mLiveCard = null;
            }

            Message msg = new Message();
            msg.arg1 = STOP;
            worker.runMessage(msg);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d("LiveCardService", "onDestroy()");

        super.onDestroy();
    }

    protected class Worker {
        private Handler mHandler;

        private final Object mHandlerLock = new Object();

        public Worker() {
            mHandler = startWorkerThread();
        }

        public void runMessage(Message msg) {
            synchronized (mHandlerLock) {
                if (mHandler != null) {
                    mHandler.sendMessage(msg);
                }
            }
        }

        private Handler startWorkerThread() {
            final HandlerThread thread = new HandlerThread(this.getClass().getCanonicalName(), Thread.MIN_PRIORITY);
            thread.start();
            final Handler ret = new MessageHandler(thread.getLooper());
            return ret;
        }

        private class MessageHandler extends Handler {
            public MessageHandler(Looper looper) {
                super(looper);
            }

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.arg1 == STOP) {
                    stopRecording();
                    RestService.getInstance().createLecture(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/audio.3gp");
                    LiveCardService.this.stopSelf();
                } else if (msg.arg1 == START) { // TODO: add cases for start
                    startRecording();
                }
            }

            private void stopRecording() {
                mRecorder.stop();
                Log.d("LiveCardService",
                        "Saving audio file to: "
                                + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                                + "/audio.3gp");

                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                File audioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                        "audio.3gp");
                Log.d(TAG, "file size=" + audioFile.length());

            }

            private void startRecording() {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                File audioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                        "audio.3gp");
                audioFile.setWritable(true, false);
                mRecorder.setOutputFile(audioFile.getAbsolutePath());
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

                try {
                    mRecorder.prepare();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("LiveCardService", "prepare failed");
                }
                mRecorder.start();
            }
        }
    }

}
