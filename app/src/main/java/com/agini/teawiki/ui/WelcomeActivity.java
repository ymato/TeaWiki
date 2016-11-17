package com.agini.teawiki.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.agini.teawiki.R;

public class WelcomeActivity extends AppCompatActivity {

    private ImageView splash;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://第一次使用
                    WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, GuideActivity.class));
                    WelcomeActivity.this.finish();
                    break;
                case 1://不是第一次使用
                    WelcomeActivity.this.startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    WelcomeActivity.this.finish();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        splash = (ImageView) findViewById(R.id.welcome);
        SharedPreferences sp = getSharedPreferences("appConfig", MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("isFirst", true);
        if (isFirst) {
            handler.sendEmptyMessageDelayed(0, 2500);
        } else {
            handler.sendEmptyMessageDelayed(1, 2500);
        }


    }
}


