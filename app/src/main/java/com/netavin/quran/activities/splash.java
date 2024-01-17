package com.netavin.quran.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.netavin.quran.R;


/**
 * Created by mehdi on 20/08/2016.
 */
public class splash extends Activity
{
    private int _splashTime = 3000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);





        new Handler().postDelayed(new Thread(){
            @Override
            public void run(){
                Intent sp2 = new Intent(splash.this, MainActivity.class);
                splash.this.startActivity(sp2);
                splash.this.finish();
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        }, _splashTime);
    }
}
