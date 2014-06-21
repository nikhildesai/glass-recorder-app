package com.iohackathon.classrecorder;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.iohackathon.classrecorder.rest.RestService;

public class MenuActivity extends Activity {

    private Handler mHandler = new Handler();
    private Uri imageUri;
    private final int TAKE_PICTURE = 0;
    private String TAG = MenuActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_stop) {
            post(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(MenuActivity.this, LiveCardService.class);
                    intent.putExtra("stop", true);
                    startService(intent);
                }
            });
            return true;
        } else if (id == R.id.action_take_picture) {
            Log.d(TAG, "Take picture");
            takePhoto();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            openOptionsMenu();
            return true;
        }
        return false;
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the activity.
        finish();
    }

    protected void post(Runnable runnable) {
        mHandler.post(runnable);
    }

    public void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(), "picture" + System.currentTimeMillis()
                + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case TAKE_PICTURE:
            if (resultCode == Activity.RESULT_OK) {
                long timePhotoTaken = System.currentTimeMillis();
                Log.d(TAG, "File saved at : " + imageUri.toString() + " at " + timePhotoTaken);
                RestService.getInstance().addPhoto(imageUri.toString(),
                        timePhotoTaken - ClassRecorderApplication.getLectureStartTime());
            }
        }
    }
}