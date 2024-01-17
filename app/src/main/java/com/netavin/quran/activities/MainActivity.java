package com.netavin.quran.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
/////////////////////import android.support.v4.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.*;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szugyi.circlemenu.view.CircleImageView;
import com.szugyi.circlemenu.view.CircleLayout;
import com.netavin.quran.BuildConfig;
import com.netavin.quran.R;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.baseClass;
import com.netavin.quran.classes.font_class;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    Intent myint;
    font_class mf;
    ObjectAnimator anim;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    DatabaseHelper dbHelper = null;
    String base_adr="";
    boolean is_run=false;
    Dialog dialog;
    RecyclerView.Adapter mAdapter;
    public static int num_qofl=3;
    int num_click=0;

    int verCode=1;
    int ver_update=0;
    String link_update="";
    DownloadTask downloadTask;
    ProgressBar prog;
    TextView txt_darsad;
    Dialog dialog_down;
    boolean sout_is_down=false;


    HttpURLConnection connection;
    OutputStreamWriter request = null;
    URL url = null;
    String response = null;

    public static final int REQUEST_CODE_READ_SMS = 156;
    private static final int REQUEST_READ_PHONE_STATE =157 ;

    boolean is_database_ok=false;
    int row_path_selected=0;

    SQLiteDatabase db1 = null;
    String DBNAME = "qoran_valiasr.db";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();

        boolean screen_on=pref.getBoolean("screen_on",true);
        findViewById(R.id.dashbord_child1).setKeepScreenOn(screen_on);

        mf = new font_class(getApplicationContext());


        //dbHelper = new DatabaseHelper(MainActivity.this, base_adr + "/ahrar/golbargha","nahj");
        checkDB();



        findViewById(R.id.dashbord_tanzimat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myint = new Intent(getApplicationContext(), tanzimat.class);
                startActivity(myint);
            }
        });
        /*findViewById(R.id.dashbord_darbarema).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),list_act.class);
                myint.putExtra("parent","about");
                myint.putExtra("onvan","درباره ما");
                startActivity(myint);
            }
        });*/
        findViewById(R.id.dashbord_alaqemandiha).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),listfav_act.class);
                startActivity(myint);
            }
        });
        findViewById(R.id.dashbord_akarinmotale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLastSeen();
            }
        });
        findViewById(R.id.dashbord_komak).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myint = new Intent(getApplicationContext(), wv_act.class);
                myint.putExtra("url","http://netavin.com/form/quran");
                startActivity(myint);
            }
        });

        base_adr=pref.getString("base_adr","null");

        /*else if(name.equals("galery"))
        {
            myint=new Intent(getApplicationContext(),galery_act.class);
            startActivity(myint);
        }
        else if(name.equals("hamyan"))
        {
            myint=new Intent(getApplicationContext(),wv_act.class);
            myint.putExtra("url","http://netavin.com/form/hamiyanquran/");
            startActivity(myint);
        }
        else if(name.equals("mahsulat"))
        {
            Toast.makeText(MainActivity.this, "این آیتم در نسخه بعد تکمیل میشود.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String tit="";
            if(name.equals("porseman"))
                tit="پرسمان";
            if(name.equals("tajvid"))
                tit="تجوید";
            if(name.equals("maqalat"))
                tit="مقالات";
            if(name.equals("hekayat"))
                tit="حکایات";
            if(name.equals("qoran"))
                tit="قرآن";
            if(name.equals("ahadis"))
                tit="احادیث";
            if(name.equals("ayat"))
                tit="آیات اهل بیت";
            if(name.equals("moama"))
                tit="معما";
            myint=new Intent(getApplicationContext(),list_act.class);
            myint.putExtra("parent",name);
            myint.putExtra("onvan",tit);
            startActivity(myint);
        }*/

        findViewById(R.id.dashboard_qoran).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),list_qoran.class);
                startActivity(myint);
            }
        });
        findViewById(R.id.dashboard_galery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),galery_act.class);
                startActivity(myint);
            }
        });
        findViewById(R.id.dashboard_hamiyan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),wv_act.class);
                myint.putExtra("url","http://netavin.com/form/hamiyanquran/");
                startActivity(myint);
            }
        });
        findViewById(R.id.dashboard_porseman).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),list_act.class);
                myint.putExtra("parent","porseman");
                myint.putExtra("onvan","پرسمان");
                startActivity(myint);
            }
        });
        findViewById(R.id.dashboard_tajvid).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),list_act.class);
                myint.putExtra("parent","tajvid");
                myint.putExtra("onvan","تجوید");
                startActivity(myint);
            }
        });
        findViewById(R.id.dashboard_hekayat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),list_act.class);
                myint.putExtra("parent","hekayat");
                myint.putExtra("onvan","حکایات");
                startActivity(myint);
            }
        });
        findViewById(R.id.dashboard_moama).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myint=new Intent(getApplicationContext(),list_act.class);
                myint.putExtra("parent","moama");
                myint.putExtra("onvan","معما");
                startActivity(myint);
            }
        });


        ImageView dashboard_qoran=findViewById(R.id.dashboard_qoran);
        ImageView dashboard_galery=findViewById(R.id.dashboard_galery);
        ImageView dashboard_hamiyan=findViewById(R.id.dashboard_hamiyan);
        ImageView dashboard_porseman=findViewById(R.id.dashboard_porseman);
        ImageView dashboard_tajvid=findViewById(R.id.dashboard_tajvid);
        ImageView dashboard_hekayat=findViewById(R.id.dashboard_hekayat);
        ImageView dashboard_moama=findViewById(R.id.dashboard_moama);

        ObjectAnimator anim1x = ObjectAnimator.ofFloat(dashboard_qoran, "scaleX", 0f, 1.0f);
        ObjectAnimator anim1y = ObjectAnimator.ofFloat(dashboard_qoran, "scaleY", 0f, 1.0f);

        ObjectAnimator anim2x = ObjectAnimator.ofFloat(dashboard_tajvid, "scaleX", 0f, 1.0f);
        ObjectAnimator anim2y = ObjectAnimator.ofFloat(dashboard_tajvid, "scaleY", 0f, 1.0f);

        ObjectAnimator anim3x = ObjectAnimator.ofFloat(dashboard_porseman, "scaleX", 0f, 1.0f);
        ObjectAnimator anim3y = ObjectAnimator.ofFloat(dashboard_porseman, "scaleY", 0f, 1.0f);

        ObjectAnimator anim4x = ObjectAnimator.ofFloat(dashboard_moama, "scaleX", 0f, 1.0f);
        ObjectAnimator anim4y = ObjectAnimator.ofFloat(dashboard_moama, "scaleY", 0f, 1.0f);

        ObjectAnimator anim5x = ObjectAnimator.ofFloat(dashboard_hekayat, "scaleX", 0f, 1.0f);
        ObjectAnimator anim5y = ObjectAnimator.ofFloat(dashboard_hekayat, "scaleY", 0f, 1.0f);

        ObjectAnimator anim6x = ObjectAnimator.ofFloat(dashboard_galery, "scaleX", 0f, 1.0f);
        ObjectAnimator anim6y = ObjectAnimator.ofFloat(dashboard_galery, "scaleY", 0f, 1.0f);

        ObjectAnimator anim7x = ObjectAnimator.ofFloat(dashboard_hamiyan, "scaleX", 0f, 1.0f);
        ObjectAnimator anim7y = ObjectAnimator.ofFloat(dashboard_hamiyan, "scaleY", 0f, 1.0f);

        AnimatorSet animSet = new AnimatorSet();
        //animSet.play(anim1).after(50);
        animSet.play(anim1x).with(anim1y).after(50);
        animSet.play(anim2x).with(anim2y).after(anim1x);
        animSet.play(anim3x).with(anim3y).after(anim2x);
        animSet.play(anim4x).with(anim4y).after(anim3x);
        animSet.play(anim5x).with(anim5y).after(anim4x);
        animSet.play(anim6x).with(anim6y).after(anim5x);
        animSet.play(anim7x).with(anim7y).after(anim6x);


        animSet.setDuration(1000);
        animSet.setInterpolator(new LinearInterpolator());
        animSet.start();


    }
    private void checkDB()
    {

        base_adr=pref.getString("base_adr","null");
        System.out.println("base_adr1="+base_adr);
        if(base_adr.equals("null"))
        {
            baseClass baseclass=new baseClass(MainActivity.this);
            Vector vec_path=new Vector();
            vec_path=baseclass.prepare();


            if(vec_path.size()==1)//1 adr
            {
                Vector temp=new Vector();
                temp= (Vector) vec_path.elementAt(0);
                String path=temp.elementAt(0)+"";
                editor.putString("base_adr",path).commit();
                createDir();
                base_adr=pref.getString("base_adr","null");
                System.out.println("base_adr2="+base_adr);
                dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
            }
            else if(vec_path.size()>1)
            {
                System.out.println("base_adr3="+base_adr);
                path_dialog(vec_path);
            }

        }
        is_database_ok=true;
    }
    public void path_dialog(final Vector vec_path)
    {
        dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_path);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);

        TextView title= (TextView) dialog.findViewById(R.id.alert_path_title_txt);
        title.setTypeface(mf.getYekan());
        title.setText("محل ذخیره سازی داده ها");
        Button zakhire= (Button) dialog.findViewById(R.id.alert_path_zakhire);
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
                //int selected_pos=pref.getInt("path_select",0);
                int selected_pos= row_path_selected;
                setPath(vec_path,selected_pos);
            }
        });

        dialog.show();
    }
    private void setPath(Vector vec_path,int pos)
    {
        Vector temp=new Vector();
        temp= (Vector) vec_path.elementAt(pos);
        String path=temp.elementAt(0)+"";
        /*File f=new File(path, "alaki10/nahj");
        f.mkdirs();
        boolean is_writable=f.canWrite();
        if(f.exists())
        {
            f.delete();
        }
        if(is_writable==false)
        {
            path=path+ "/Android/data/" + getApplication().getPackageName() + "/Files";
            getExternalFilesDir("MyFileStorage");
        }*/

        editor.putInt("path_select",pos).commit();
        editor.putString("base_adr",path).commit();
        createDir();
        String base_adr=pref.getString("base_adr","null");
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
    }
    /*@Override
    public void onItemClick(View view)
    {
        if(is_database_ok==false)
        {
            return;
        }
        if(is_run==true)
        {
            return;
        }
        is_run=true;
        String name = null;
        View view1 = circleLayout.getSelectedItem();
        if (view1 instanceof CircleImageView) {
            name = ((CircleImageView) view).getName();
        }
        System.out.println("name="+name);//porseman,tajvid,maqalat,hekayat,qoran,ahadis,ayat,moama
        if(name.equals("qoran"))
        {
            myint=new Intent(getApplicationContext(),list_qoran.class);
            startActivity(myint);
        }
        else if(name.equals("galery"))
        {
            myint=new Intent(getApplicationContext(),galery_act.class);
            startActivity(myint);
        }
        else if(name.equals("hamyan"))
        {
            myint=new Intent(getApplicationContext(),wv_act.class);
            myint.putExtra("url","http://netavin.com/form/hamiyanquran/");
            startActivity(myint);
        }
        else if(name.equals("mahsulat"))
        {
            Toast.makeText(MainActivity.this, "این آیتم در نسخه بعد تکمیل میشود.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String tit="";
            if(name.equals("porseman"))
                tit="پرسمان";
            if(name.equals("tajvid"))
                tit="تجوید";
            if(name.equals("maqalat"))
                tit="مقالات";
            if(name.equals("hekayat"))
                tit="حکایات";
            if(name.equals("qoran"))
                tit="قرآن";
            if(name.equals("ahadis"))
                tit="احادیث";
            if(name.equals("ayat"))
                tit="آیات اهل بیت";
            if(name.equals("moama"))
                tit="معما";
            myint=new Intent(getApplicationContext(),list_act.class);
            myint.putExtra("parent",name);
            myint.putExtra("onvan",tit);
            startActivity(myint);
        }

    }

    @Override
    public void onRotationFinished(View view)
    {
        if(is_run==true)
        {
            return;
        }
        is_run=true;
        String name = null;
        View view1 = circleLayout.getSelectedItem();
        if (view1 instanceof CircleImageView) {
            name = ((CircleImageView) view).getName();

        }
        if(name.equals("qoran"))
        {
            myint=new Intent(getApplicationContext(),list_qoran.class);
            startActivity(myint);
        }
        else if(name.equals("galery"))
        {
            myint=new Intent(getApplicationContext(),galery_act.class);
            startActivity(myint);
        }
        else if(name.equals("hamyan"))
        {
            myint=new Intent(getApplicationContext(),wv_act.class);
            myint.putExtra("url","http://netavin.com/form/hamiyanquran/");
            startActivity(myint);
        }
        else if(name.equals("mahsulat"))
        {
            Toast.makeText(MainActivity.this, "این آیتم در نسخه بعد تکمیل میشود.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            System.out.println("name="+name);
            String tit="";
            if(name.equals("porseman"))
                tit="پرسمان";
            if(name.equals("tajvid"))
                tit="تجوید";
            if(name.equals("maqalat"))
                tit="مقالات";
            if(name.equals("hekayat"))
                tit="حکایات";
            if(name.equals("qoran"))
                tit="قرآن";
            if(name.equals("ahadis"))
                tit="احادیث";
            if(name.equals("ayat"))
                tit="آیات اهل بیت";
            if(name.equals("moama"))
                tit="معما";
            myint=new Intent(getApplicationContext(),list_act.class);
            myint.putExtra("parent",name);
            myint.putExtra("onvan",tit);
            new Handler().postDelayed(new Thread(){
                @Override
                public void run(){

                    startActivity(myint);
                }
            }, 500);
        }

    }*/


    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if(msg.what==0)
            {
                System.out.println("verCode="+verCode);
                System.out.println("ver_update="+ver_update);
                if(ver_update>verCode)
                {
                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
                    String base_adr=pref.getString("base_adr","null");
                    String local_adr=base_adr + "/ahrar/golbargha/";
                    System.out.println("local_adr="+local_adr);
                    System.out.println("link_update="+link_update);
                    System.out.println("verCode="+verCode);
                    System.out.println("ver_update="+ver_update);
                    showDialogDownload(local_adr,link_update);
                }
            }
            if(msg.what==1)
            {
                //
            }
        }
    };
    public void showDialogDownload(final String loc_adr, final String ser_adr)
    {
        //System.out.println("adr="+adr);
        //System.out.println("name="+name);
        dialog_down = new Dialog(MainActivity.this);
        dialog_down.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_down.setContentView(R.layout.dialog_yesno);

        dialog_down.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_down.setCancelable(false);
        TextView title= (TextView) dialog_down.findViewById(R.id.dialog_yesno_title_txt);
        TextView matn= (TextView) dialog_down.findViewById(R.id.dialog_yesno_matn_txt);
        final Button bale= (Button) dialog_down.findViewById(R.id.dialog_yesno_btn_bale);
        Button kheyr= (Button) dialog_down.findViewById(R.id.dialog_yesno_btn_kheyr);
        prog=(ProgressBar) dialog_down.findViewById(R.id.dialog_yesno_prog);
        txt_darsad= (TextView) dialog_down.findViewById(R.id.dialog_yesno_txt);



        title.setTypeface(mf.getYekan());
        matn.setTypeface(mf.getYekan());
        bale.setTypeface(mf.getYekan());
        kheyr.setTypeface(mf.getYekan());
        txt_darsad.setTypeface(mf.getYekan());

        txt_darsad.setText("0%");
        title.setText("بروزرسانی");
        matn.setText("آیا مایل به دریافت بروزرسانی نرم افزار هستید؟");
        bale.setText("دانلود");
        kheyr.setText("خیر");

        bale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()==false)
                {
                    Toast.makeText(MainActivity.this, "برای دریافت فایل به اینترنت متصل شوید...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    downloadTask = new DownloadTask(MainActivity.this);
                    bale.setEnabled(false);
                    sout_is_down=true;
                    downloadTask.execute(loc_adr,ser_adr);
                }

            }
        });

        kheyr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(sout_is_down==true)
                {
                    downloadTask.cancel(true);
                    sout_is_down=false;
                    File f=new File(loc_adr);
                    if(f.exists())
                    {
                        f.delete();
                    }
                }

                dialog_down.dismiss();

            }
        });

        dialog_down.show();
    }
    private void createDir()
    {
        String base_adr=pref.getString("base_adr","null");
        System.out.println("base_adr2="+base_adr);
        File myDirectory = new File(base_adr, "parsmagz/qoran");

        if(!myDirectory.exists())
        {
            myDirectory.mkdirs();
        }

        for(int i=1;i<=10;i++)
        {
            myDirectory = new File(base_adr, "parsmagz/qoran/sound"+i);
            if(!myDirectory.exists())
            {
                myDirectory.mkdirs();
            }
        }
    }
    public static float pixelsToSp(Context context, float px)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String>
    {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        String loc_adr="";
        public DownloadTask(Context context) {
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
            ///////////////////////////////mProgressDialog.show();
            System.out.println("111111111111111111111");
        }

        @Override
        protected String doInBackground(String... f_url)
        {
            loc_adr=f_url[0];
            String ser_adr=f_url[1];
            loc_adr=loc_adr+"qoran.apk";
            System.out.println("loc_adr="+loc_adr);
            System.out.println("ser_adr="+ser_adr);
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(ser_adr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Accept-Encoding", "identity");
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                {
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();
                }
                //connection.setChunkedStreamingMode(100);
                //connection.setDoInput(true);
                // this will be useful to display download percentage
                // might be -1: server did not report the length

                int fileLength = connection.getContentLength();
                System.out.println("fileLength="+fileLength);
                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(loc_adr);

                byte data[] = new byte[40960];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e)
            {
                System.out.println("eee1="+e.getMessage());
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        try {
                            output.close();
                        }
                        catch (IOException e) {
                            System.out.println("eee2="+e.getMessage());
                            e.printStackTrace();
                        }
                    if (input != null)
                        input.close();
                } catch (IOException ignored)
                {
                    System.out.println("eee3="+ignored.getMessage());
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            //prog.setIndeterminate(false);
            prog.setMax(100);
            prog.setProgress(progress[0]);
            txt_darsad.setText(progress[0]+"%");
            System.out.println("aaaa progress[0]="+progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            sout_is_down=false;
            //////////////////////mProgressDialog.dismiss();

            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
            {
                dialog_down.dismiss();
                /*Intent intent = new Intent(Intent.ACTION_VIEW);
                File file = new File(loc_adr);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);*/
                File file = new File(loc_adr);

                //File toInstall = new File(appDirectory, appName + ".apk");
                //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                if (Build.VERSION.SDK_INT >= 24)
                {
                    /*Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider", file);
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);*/
                }
                else {
                    Uri apkUri = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }

        }
    }//down
    @Override
    public void onPause() {
        super.onPause();
        //anim.cancel();
    }
    @Override
    public void onResume() {
        super.onResume();
        is_run=false;
    }
    private void setLastSeen()
    {
        Vector myvec=new Vector();
        db1 = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE,null);
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_lastseen(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//1
        try
        {
            Cursor c = db1.rawQuery("SELECT * FROM tb_lastseen ", null);

            if(c!= null)
            {
                if(c.getCount()==0)//item dar db vojud nadarad pas insert mikonim
                {
                    Toast.makeText(MainActivity.this, "شما هنوز مطالعه ای انجام نداده اید.", Toast.LENGTH_SHORT).show();
                }
                if(c.getCount()==1)//item dar db vojud darad pas delete mikonim
                {
                    try
                    {
                        if(c!= null)
                        {
                            if (c.moveToFirst())
                            {
                                do
                                {
                                    int type =c.getInt(c.getColumnIndex("TYPE"));
                                    String onvan=c.getString(c.getColumnIndex("ONVAN"));
                                    int key =c.getInt(c.getColumnIndex("KEY"));
                                    int pos =c.getInt(c.getColumnIndex("POS"));
                                    int page =c.getInt(c.getColumnIndex("PAGE"));


                                    myvec.add(type);
                                    myvec.add(onvan);
                                    myvec.add(key);
                                    myvec.add(pos);
                                    myvec.add(page);

                                    //System.out.println(code+"   nn   "+type);
                                }while(c.moveToNext());
                            }
                            onclicklastseen(myvec);
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("eevvveee="+e.getMessage());
                    }
                }
                if(c.getCount()>1)
                {
                    Toast.makeText(MainActivity.this, "err", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("eeeeeeeeeeeerrrrrrrrr2"+e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage()+"  bb",Toast.LENGTH_SHORT).show();
        }
    }
    private void onclicklastseen(Vector tag)
    {
        int type= (int) tag.elementAt(0);
        String onvan=tag.elementAt(1)+"";
        int key= (int) tag.elementAt(2);
        int pos= (int) tag.elementAt(3);
        int page=(int) tag.elementAt(4);//1=matn_template    2=parent_act    3=qoran_template  4=ahadis_matn
        System.out.println("page="+page);
        if(page==1)
        {
            Intent myint = new Intent(getApplicationContext(), matn_template.class);
            myint.putExtra("type",type);
            myint.putExtra("key",key);
            myint.putExtra("onvan",onvan);
            myint.putExtra("pos",pos);

            startActivity(myint);
        }
        else if(page==3)
        {
            Intent myint = new Intent(getApplicationContext(), qoran_template.class);
            myint.putExtra("type",type);
            myint.putExtra("id",pos);
            myint.putExtra("onvan",onvan);

            startActivity(myint);
        }
        else if(page==4)
        {
            Intent myint = new Intent(getApplicationContext(), ahadis_matn.class);
            myint.putExtra("key",key);
            myint.putExtra("onvan",onvan);

            startActivity(myint);
        }
    }
}
