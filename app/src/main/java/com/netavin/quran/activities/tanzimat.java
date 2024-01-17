package com.netavin.quran.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.*;

import com.netavin.quran.R;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.baseClass;
import com.netavin.quran.classes.font_class;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * Created by mehdi on 29/06/2017.
 */
public class tanzimat extends AppCompatActivity
{
    DatabaseHelper dbHelper = null;
    font_class mf;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ProgressDialog progressBar_circle;

    AppCompatCheckBox chb,chb_erab;
    Dialog dialog;
    RelativeLayout rel1;

    RelativeLayout rel4;
    RecyclerView.Adapter mAdapter;


    CopyTask copytask;
    boolean is_taqyir = false;
    int row_path_selected=0;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //card_view:cardBackgroundColor="#fce8c8"
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tanzimat);


        mf = new font_class(getApplicationContext());
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        TextView title= (TextView) findViewById(R.id.tanzimat_title);
        title.setTypeface(mf.getAdobeBold());

        dialog = new Dialog(tanzimat.this);


        chb = (AppCompatCheckBox) findViewById(R.id.tanzimat_screen_chb);
        chb.setTypeface(mf.getYekan());
        chb.setText("روشن ماندن صفحه نمایش ");

        chb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    editor.putBoolean("screen_on", true).commit();
                } else if (isChecked == false) {
                    editor.putBoolean("screen_on", false).commit();
                }
            }
        });

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.tanzimat_screen_chb).setKeepScreenOn(screen_on);
        if (screen_on == true) {
            chb.setChecked(true);
        }

        rel1 = (RelativeLayout) findViewById(R.id.tanzimat_rel1);
        rel4 = (RelativeLayout) findViewById(R.id.tanzimat_rel4);


        TextView path_titr = (TextView) findViewById(R.id.tanzimat_path_titr);
        TextView path_matn = (TextView) findViewById(R.id.tanzimat_path_matn);
        path_titr.setTypeface(mf.getYekan());
        path_matn.setTypeface(mf.getYekan());
        path_titr.setText("تغییر محل ذخیره داده ها");
        String s = "با انتخاب این گزینه میتوانید محل ذخیره داده ها که شامل پایگاه داده و فایل های صوتی میشود را تغییر دهید .";
        path_matn.setText(s);


        AppCompatButton taqyir_btn = (AppCompatButton) findViewById(R.id.tanzimat_taqyir_btn);
        taqyir_btn.setTypeface(mf.getYekan());
        taqyir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseClass baseclass = new baseClass(tanzimat.this);
                Vector vec_path = new Vector();
                vec_path = baseclass.prepare();
                path_dialog(vec_path);
            }
        });

        copytask = new CopyTask(tanzimat.this);


        /*ImageView about= (ImageView) findViewById(R.id.tanzimat_about);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                Intent myint=new Intent(getApplicationContext(),list_act.class);
                myint.putExtra("parent","about");
                myint.putExtra("onvan","درباره ما");
                startActivity(myint);
            }
        });*/
    }
    public void path_dialog(final Vector vec_path) {
        dialog = new Dialog(tanzimat.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_path);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);
        is_taqyir = true;
        TextView title = (TextView) dialog.findViewById(R.id.alert_path_title_txt);
        title.setTypeface(mf.getYekan());
        title.setText("محل ذخیره سازی داده ها");
        Button zakhire = (Button) dialog.findViewById(R.id.alert_path_zakhire);
        zakhire.setTypeface(mf.getYekan());
        zakhire.setText("ذخیره");

        row_path_selected=0;
        final Vector radio_vec=new Vector();
        int last_id=1000;
        LinearLayout mainRel=(LinearLayout) dialog.findViewById(R.id.azmun_mainRel);
        int text_size=(int) getResources().getDimension(R.dimen.txt_size);
        int tab_size=(int) getResources().getDimension(R.dimen.tab_size);
        int tab_size_kuchak=(int) getResources().getDimension(R.dimen.tab_size_kuchak);
        for(int i=0;i<vec_path.size();i++)
        {
            Vector temp = new Vector();
            temp = (Vector) vec_path.elementAt(i);
            String path = temp.elementAt(0) + "";
            String free = temp.elementAt(1) + "";
            String total = temp.elementAt(2) + "";

            RelativeLayout temp_rel=new RelativeLayout(this);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            temp_rel.setLayoutParams(param);
            temp_rel.setBackgroundColor(Color.parseColor("#ffffff"));
            mainRel.addView(temp_rel);

            AppCompatRadioButton radio=new AppCompatRadioButton(this);
            RelativeLayout.LayoutParams param_rel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param_rel.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            param_rel.addRule(RelativeLayout.CENTER_VERTICAL);
            param_rel.setMargins(5,0,0,0);
            radio.setLayoutParams(param_rel);
            radio.setGravity(Gravity.LEFT);
            radio.setText(path);
            radio.setTextColor(Color.parseColor("#516f7a"));
            radio.setTextSize(pixelsToSp(this,tab_size));
            radio.setId(last_id);
            temp_rel.addView(radio);

            TextView total_txt=new TextView(this);
            param_rel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param_rel.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            param_rel.addRule(RelativeLayout.BELOW, last_id);
            param_rel.setMargins(0,15,15,0);
            total_txt.setLayoutParams(param_rel);
            total_txt.setGravity(Gravity.RIGHT);
            total_txt.setTextSize(pixelsToSp(this,tab_size_kuchak));
            total_txt.setText("فضای کل" + ":" + total);
            total_txt.setTypeface(mf.getYekan());
            last_id++;
            total_txt.setId(last_id);
            temp_rel.addView(total_txt);

            TextView free_txt=new TextView(this);
            param_rel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            param_rel.addRule(RelativeLayout.BELOW, radio.getId());
            param_rel.addRule(RelativeLayout.LEFT_OF, total_txt.getId());
            param_rel.setMargins(0,15,25,0);
            free_txt.setLayoutParams(param_rel);
            free_txt.setGravity(Gravity.RIGHT);
            free_txt.setTextSize(pixelsToSp(this,tab_size_kuchak));
            free_txt.setText("فضای خالی" + ":" + free);
            free_txt.setTypeface(mf.getYekan());
            temp_rel.addView(free_txt);

            View line=new View(this);
            param_rel = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
            param_rel.addRule(RelativeLayout.BELOW, total_txt.getId());
            param_rel.setMargins(15,3,15,0);
            line.setLayoutParams(param_rel);
            line.setBackgroundColor(Color.parseColor("#c4c4c4"));
            temp_rel.addView(line);

            int sel_pos = pref.getInt("path_select", 0);
            if(sel_pos==i)
            {
                radio.setChecked(true);
            }

            radio_vec.add(radio);
            final int finalI = i;
            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("i="+ finalI+"  "+radio_vec.size());
                    row_path_selected=finalI;
                    for(int j=0;j<radio_vec.size();j++)
                    {
                        if(j!=finalI)
                        {
                            AppCompatRadioButton radio_temp= (AppCompatRadioButton) radio_vec.elementAt(j);
                            radio_temp.setChecked(false);
                        }
                    }
                }
            });
        }

        zakhire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                copy(vec_path);
            }
        });

        dialog.show();
    }

    private void copy(Vector vec_path) {
        //int selected_pos=pref.getInt("path_select",0);
        int selected_pos = row_path_selected;
        Vector temp = new Vector();
        temp = (Vector) vec_path.elementAt(selected_pos);

        String new_path = temp.elementAt(0) + "";
        String cur_path = pref.getString("base_adr", "null");




        if (cur_path.equals(new_path))
        {
            //
        }
        else
        {
            Log.d("aaaa", "copy database shoru");

            System.out.println("cur_path=" + cur_path);
            System.out.println("new_path=" + new_path);

            /*File sourceLocation=new File(cur_path+"/valiasr");
            File targetLocation=new File(new_path+"/valiasr");



            try {
                FileUtils.copyDirectory(sourceLocation, targetLocation);
                if(sourceLocation.exists())
                {
                    FileUtils.deleteDirectory(sourceLocation);
                    System.out.println("dir pak shod");
                    editor.putString("base_adr",new_path).commit();
                }
            } catch (IOException e) {
                System.out.println("e10.getMessage()="+e.getMessage());
            }


            Log.d("aaaa", "copy database payan");*/
            copytask = new CopyTask(tanzimat.this);
            copytask.execute(cur_path, new_path);
        }
    }

    public void showProg(String str) {
        progressBar_circle = ProgressDialog.show(tanzimat.this, str, "کمی صبر کنید . . .");
        progressBar_circle.setCancelable(false);
    }
    public void dissProg() {
        progressBar_circle.dismiss();
    }
    private class CopyTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        String loc_adr = "";

        public CopyTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
            mWakeLock.acquire();
            showProg("");
            ///////////////////////////////mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... f_url) {
            String cur_path = f_url[0];
            String new_path = f_url[1];

            File sourceLocation = new File(cur_path + "/parsmagz/qoran");
            File targetLocation = new File(new_path + "/parsmagz/qoran");

            /*try {
                FileUtils.copyDirectory(sourceLocation, targetLocation);
                if (sourceLocation.exists()) {
                    //FileUtils.deleteDirectory(sourceLocation);
                    deleteRecursive(sourceLocation);
                    System.out.println("dir pak shod");
                    editor.putString("base_adr", new_path).commit();
                }
            } catch (IOException e) {
                System.out.println("e10.getMessage()=" + e.getMessage());
            }*/

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();


            if (result != null)
                Toast.makeText(context, "Copy error: " + result, Toast.LENGTH_LONG).show();
            else {
                Log.d("aaaa", "copy database payan");
                editor.putInt("path_select", row_path_selected).commit();
                dissProg();
            }
            //Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }
    }//copy

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.out.println("dialog.isShowing()=" + dialog.isShowing());
        if (dialog.isShowing()) {
            dialog.dismiss();
        } else {
            finish();
        }
        //finish();
    }
    public static float pixelsToSp(Context context, float px)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
}
