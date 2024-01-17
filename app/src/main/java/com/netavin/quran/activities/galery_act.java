package com.netavin.quran.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netavin.quran.R;
import com.netavin.quran.adapters.gallery_adapter;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 06/11/2018.
 */
public class galery_act extends AppCompatActivity
{
    DatabaseHelper dbHelper = null;
    font_class mf;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ProgressDialog progressBar_circle;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Vector vec;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //card_view:cardBackgroundColor="#fce8c8"
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galery_act);


        mf = new font_class(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        TextView title = (TextView) findViewById(R.id.galery_title);
        title.setTypeface(mf.getAdobeBold());

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.galery_title).setKeepScreenOn(screen_on);

        mRecyclerView = (RecyclerView) findViewById(R.id.gallery_recycler);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        /*vec=new Vector();
        for(int i=1;i<=100;i++)
        {
            vec.add("http://dsfgr5qquran44s3ryzalq.netavin.com/quranposter1/faraed-ir-netavin-com-poster-quran-"+i+".jpg");
        }
        mAdapter = new gallery_adapter(vec,galery_act.this);
        mRecyclerView.setAdapter(mAdapter);*/

        getCategory();
    }

    public void getCategory()
    {
        /*Ion.with(getApplicationContext())
                .load("https://intosell-backend.e60.top/api/v1/quranforyou.intosell.com/categories/")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        System.out.println("result="+result);
                    }
                });*/
        Ion.with(getApplicationContext())
                .load("https://intosell-backend.e60.top/api/v1/quranforyou.intosell.com/categories/get_default_site_categories/?format=api")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        System.out.println("result="+result);
                    }
                });
    }
}
