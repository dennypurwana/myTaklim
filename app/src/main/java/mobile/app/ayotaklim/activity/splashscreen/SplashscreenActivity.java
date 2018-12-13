package mobile.app.ayotaklim.activity.splashscreen;

import android.Manifest;
import android.content.Intent;
import android.media.session.MediaSessionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import mobile.app.ayotaklim.MainActivity;
import mobile.app.ayotaklim.R;
import mobile.app.ayotaklim.activity.event.EventActivity;

public class SplashscreenActivity extends AppCompatActivity {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        handler=new Handler();


        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent intent = new Intent(SplashscreenActivity.this, EventActivity.class);
                    startActivity(intent);
                    finish();
            }
        },3000);

    }
}
