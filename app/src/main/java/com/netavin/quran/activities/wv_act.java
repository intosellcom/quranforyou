package com.netavin.quran.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.netavin.quran.R;

/**
 * Created by mehdi on 02/12/2018.
 */
public class wv_act extends AppCompatActivity
{
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wv_act);


        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.wv).setKeepScreenOn(screen_on);

        Intent myint = getIntent();
        String url = myint.getStringExtra("url");

        webView = (WebView) findViewById(R.id.wv);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            /*webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.setWebChromeClient(new WebChromeClient());
            webView.loadUrl(url);*/
        webView.setWebViewClient(new MyBrowser());//2
        //String url = "http://www.golbargha.com/";//3
        webView.getSettings().setLoadsImagesAutomatically(true);//4
        webView.getSettings().setJavaScriptEnabled(true);//5
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);//6
        webView.loadUrl(url);//7



        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadWithOverviewMode(true);
    }
    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
