package com.netavin.quran.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import androidx.appcompat.widget.*;

import com.netavin.quran.R;
import com.netavin.quran.adapters.font_adapter;
import com.netavin.quran.adapters.qaran_adapter;
import com.netavin.quran.adapters.qoran_curser;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.MyLinearLayoutManager;
import com.netavin.quran.classes.font_class;


import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by mehdi on 07/04/2017.
 */
public class qoran_template extends AppCompatActivity
{
    DatabaseHelper dbHelper = null;
    Vector vec;

    SQLiteDatabase db1 = null;
    String DBNAME = "qoran_valiasr.db";

    int type = 0;
    ListView lst;
    font_class mf;
    /*int pos=0;
    String sound_name="";
    String dsctarjome="";*/

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String base_adr="";

    TextView logo_txt;
    String onvan="";
    int id=0;
    int ayat_count,joz;

    ProgressDialog progressBar_circle;
    qaran_adapter adapter;

    float curElementSize1=0;
    float curElementSize2=0;

    public Dialog dialog;
    ImageView fav_btn,play_btn,info_btn;


    MediaPlayer mp;
    VideoView video;
    MediaController mediaController;
    boolean is_pause=false;
    ProgressDialog prog_down;
    DownloadTask downloadTask;
    ProgressBar prog;
    TextView txt_darsad;
    Dialog dialog_down;
    boolean tafsir_is_down=false;


    ExtractTask extractTask;

    public static boolean is_sharh=false;

    DownloadSoundTask downloadsoundtask;
    boolean sound_is_down=false;
    int kol_aya=0;
    public static int cur_aye=1;

    boolean is_besm_down=false;

    public static int pos_sound_play=1;
    public static boolean is_besm_played=false;

    String cur_sound_isdownloading="";

    TextView tedad_header,joz_header;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qoran_template);

        cur_aye=1;
        is_besm_played=false;
        pos_sound_play=1;

        mf = new font_class(getApplicationContext());

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        base_adr = pref.getString("base_adr", "null");

        db1 = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE,null);
        //db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,DSC TEXT,KEY INTEGER,POS INTEGER,SOUND_NAME TEXT,DSCTARJOME TEXT,PAGE INTEGER); ");//3
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//3

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.qorantemp_child1).setKeepScreenOn(screen_on);

        /*tedad_header= (TextView) findViewById(R.id.qorantemp_tedadayat);
        joz_header= (TextView) findViewById(R.id.qorantemp_joz);

        tedad_header.setTypeface(mf.getYekan());
        joz_header.setTypeface(mf.getYekan());*/




        logo_txt= (TextView) findViewById(R.id.qorantemp_title);
        lst= (ListView) findViewById(R.id.qorantemp_list);
        //lst.setLongClickable(true);

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long bb)
            {
                System.out.println("click   "+position+"  id="+id +"    kol_aya="+kol_aya);
                int sound_actived=pref.getInt("qoran_qary_code",1);
                boolean qr_isdown=pref.getBoolean("sura_"+id+"_"+sound_actived,false);
                if(qr_isdown==true)
                {
                    if(mp.isPlaying())
                    {
                        mp.stop();
                        mp=null;
                    }
                    System.out.println("pakhsh shavad..................");
                    if(id==1)
                    {
                        pos_sound_play=position+1;
                    }
                    else if(id==9)
                    {
                        pos_sound_play=position+1;
                        is_besm_played=true;
                    }
                    else
                    {
                        if(position==0)
                        {
                            pos_sound_play=position+1;
                            is_besm_played=false;
                        }
                        else
                        {
                            pos_sound_play=position;
                            is_besm_played=true;
                        }
                    }

                    playAya();
                }
                else
                {
                    showDialogDownloadSound();
                }
            }
        });
        /*lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long bb)
            {

                File f=new File(base_adr + "/parsmagz/qoran/qoran_sharh.db");
                if(f.exists())
                {
                    Intent myint = new Intent(getApplicationContext(), matn_template.class);
                    myint.putExtra("type", 96);
                    if(id==1)
                    {
                        myint.putExtra("pos", (position));
                    }
                    else
                    {
                        myint.putExtra("pos", position);
                    }
                    myint.putExtra("pos", position);
                    myint.putExtra("key",id);
                    myint.putExtra("onvan","تفسیر آیه"+(position+1)+" از سوره "+onvan);
                    myint.putExtra("dsc", "");

                    startActivity(myint);
                }
                else
                {
                    String mydb="qoran_sharh";
                    showDialogDownload(mydb);// qoran_sharh
                }
                return true;
            }
        });*/


        logo_txt.setTypeface(mf.getYekan());

        Intent myint=getIntent();
        type=myint.getIntExtra("type",0);
        id=myint.getIntExtra("id",0);
        ayat_count=myint.getIntExtra("ayat_count",0);
        joz=myint.getIntExtra("joz",0);
        onvan=myint.getStringExtra("onvan");


        new Handler().postDelayed(new Thread(){
            @Override
            public void run(){

                //joz_header.setText(joz+"");
                //tedad_header.setText(ayat_count+"");
            }
        }, 5000);

        final ImageView tarjome= (ImageView) findViewById(R.id.qorantemp_tarjome);
        tarjome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tarjome_click();
            }
        });
        /*final ImageView sharh= (ImageView) findViewById(R.id.qorantemp_sharh);
        sharh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sharh_click();
            }
        });*/


        fav_btn = (ImageView) findViewById(R.id.qorantemp_alaqemandiha);
        fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fav_click();
            }
        });


        final ImageView kuchak= (ImageView) findViewById(R.id.qorantemp_kuchak);
        kuchak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kuchak_click();
            }
        });
        final ImageView bozorg= (ImageView) findViewById(R.id.qorantemp_bozorg);
        bozorg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bozorg_click();
            }
        });

        play_btn = (ImageView) findViewById(R.id.qorantemp_play);
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play_click();
            }
        });

        info_btn = (ImageView) findViewById(R.id.qorantemp_info);
        info_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //info_click();
                stopMedia();
                qary_dialog();
            }
        });


        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mp=new MediaPlayer();
        setBoard();
        setFav();
        setLastSeen();


        boolean chekshowe=pref.getBoolean("qoran_rahnema",true);
        if(chekshowe)
        {
            //rahnema_click();
        }

    }
    public void setBoard()
    {
        logo_txt.setText(onvan);
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");


        //id=1..114
        //int count_aya=dbHelper.getQoranSureCount(id);
        Cursor cursor=dbHelper.getQoranCurserTest(id);
        kol_aya=cursor.getCount();

        //cursor.getCount()=num_aye==daqiq ba besmelah
        qoran_curser todoAdapter = new qoran_curser(qoran_template.this, cursor,cursor.getCount(),type,id);
        lst.setAdapter(todoAdapter);
    }
    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            dissProg();

            if(msg.what==0)
            {
                lst.setAdapter(new qaran_adapter(qoran_template.this, vec,type,0));
            }
            if(msg.what==1)
            {
                //
            }
        }
    };
    public void showProg(String str)
    {
        progressBar_circle = ProgressDialog.show(qoran_template.this, str, "کمی صبر کنید . . .");
        progressBar_circle.setCancelable(false);
    }
    public void dissProg()
    {
        progressBar_circle.dismiss();
    }
    public void tarjome_click()
    {
        is_sharh=false;
        qalam_dialog();
    }
    public static float pixelsToSp(Context context, float px)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }
    public void qalam_dialog()
    {
        dialog = new Dialog(qoran_template.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_font);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title= (TextView) dialog.findViewById(R.id.alert_font_title_txt);
        title.setTypeface(mf.getYekan());
        if(is_sharh==false)
        {
            title.setText("ترجمه را انتخاب کنید :");
        }
        if(is_sharh==true)
        {
            title.setText("شرح را انتخاب کنید :");
        }

        RecyclerView mRecyclerView= (RecyclerView) dialog.findViewById(R.id.alert_font_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(getApplicationContext(), Configuration.ORIENTATION_PORTRAIT,false));

        Vector values=new Vector();
        if(is_sharh==false)
        {
            values.add("ناصر مکارم شیرازی");
            values.add("حسین انصاریان");
            values.add("عبدالمحمد آیتی");
            values.add("ابوالفضل بهرامپور");
            values.add("محمد مهدی فولادوند");
            values.add("مصطفی خرمدل");
            values.add("بهاءالدین خرمشاهی");
            values.add("محمدکاظم معزی");
            values.add("سیدجلال الدین مجتبوی");
            values.add("محمد صادقی تهرانی");
        }
        if(is_sharh==true)
        {
            values.add("تفسیر المیزان");
            values.add("تفسیر نمونه");
        }

        RecyclerView.Adapter mAdapter=new font_adapter(qoran_template.this,values,1027);
        mRecyclerView.setAdapter(mAdapter);


        dialog.show();
    }
    public void qary_dialog()
    {

        dialog = new Dialog(qoran_template.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_font);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title= (TextView) dialog.findViewById(R.id.alert_font_title_txt);
        title.setTypeface(mf.getYekan());
        title.setText("قاری را انتخاب کنید :");

        RecyclerView mRecyclerView= (RecyclerView) dialog.findViewById(R.id.alert_font_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(getApplicationContext(), Configuration.ORIENTATION_PORTRAIT,false));

        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        Vector values=new Vector();

        values.add("منشاوي");
        values.add("سعدالغامدي");
        values.add("کريم منصوري");
        values.add("محمد ايوب");
        values.add("ماهر المعيقلي");
        values.add("محمد الطبلاوي");
        values.add("هاني الرفاعي");
        values.add("ابراهيم الاخضر");
        values.add("پرهيزگار");
        values.add("عبدالباسط");

        RecyclerView.Adapter mAdapter=new font_adapter(qoran_template.this,values,200);
        mRecyclerView.setAdapter(mAdapter);


        dialog.show();
    }
    private void setFav()
    {
        //db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,DSC TEXT,KEY INTEGER,POS INTEGER,SOUND_NAME TEXT,DSCTARJOME TEXT,PAGE INTEGER); ");
        String dsc="";
        int key=0;
        String sound_name="";
        String dsctarjome="";
        try
        {
            //Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan+"'"+"AND TYPE='"+type+"'"+"AND DSC='"+dsc+"'"+"AND KEY='"+key+"'"+"AND PAGE='"+3+"'", null);
            Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan+"'"+" AND TYPE='"+type+"'"+" AND KEY='"+key+"'"+" AND POS='"+id+"'"+" AND PAGE='"+3+"'", null);
            if(c!= null)
            {
                if(c.getCount()==0)
                {
                    fav_btn.setImageResource(R.drawable.submit_alaqemandiha2);
                }
                if(c.getCount()==1)
                {
                    fav_btn.setImageResource(R.drawable.submit_alaqemandiha2fav);
                }
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage()+"  aa",Toast.LENGTH_SHORT).show();
        }

    }
    private void fav_click()
    {
        String dsc="";
        int key=0;
        String sound_name="";
        String dsctarjome="";
        try
        {
            //Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan+"'"+"AND TYPE='"+type+"'"+"AND DSC='"+dsc+"'"+"AND KEY='"+key+"'"+"AND PAGE='"+3+"'", null);
            Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan+"'"+" AND TYPE='"+type+"'"+" AND KEY='"+key+"'"+" AND POS='"+id+"'"+" AND PAGE='"+3+"'", null);
            if(c!= null)
            {
                if(c.getCount()==0)//item dar db vojud nadarad pas insert mikonim
                {
                    //db1.execSQL("INSERT INTO tb_fav (TYPE,ONVAN,DSC,KEY,PAGE) VALUES ('" + type + "','" + onvan+"','" + dsc+"','"+key +"','"+3 +"');");
                    db1.execSQL("INSERT INTO tb_fav (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan+"','"+key +"','"+id  +"','"+3 +"');");
                    Toast.makeText(qoran_template.this," "+"به لیست علاقه مندیها اضافه شد.", Toast.LENGTH_SHORT).show();

                }
                if(c.getCount()==1)//item dar db vojud darad pas delete mikonim
                {
                    //db1.execSQL("DELETE  FROM tb_fav WHERE ONVAN='" +onvan+"'"+"AND TYPE='"+type+"'"+"AND DSC='"+dsc+"'"+"AND KEY='"+key+"'"+"AND PAGE='"+3+"'");
                    db1.execSQL("DELETE  FROM tb_fav WHERE ONVAN='" +onvan+"'"+" AND TYPE='"+type+"'"+" AND KEY='"+key+"'"+" AND POS='"+id+"'"+" AND PAGE='"+3+"'");
                    Toast.makeText(qoran_template.this," "+"از لیست علاقه مندیها حذف شد.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage()+"  bb",Toast.LENGTH_SHORT).show();
        }
        setFav();

    }//
    public void sharh_click()
    {
        String sharh_db_adr=base_adr + "/parsmagz/qoran/qoran_sharh.db";

        if(checkDb(sharh_db_adr))
        {
            is_sharh=true;
            qalam_dialog();
        }
        else
        {
            //String local_adr=base_adr + "/ahrar/golbargha/qoran/qoran_sharh.db";
            //String server_adr="http://www.valiasr-aj.com/_mobile/golbarg/qoran_sharh.zip";

            String mydb="qoran_sharh";
            showDialogDownload(mydb);// qoran_sharh
        }

    }
    public void ShowProg()
    {
        progressBar_circle = ProgressDialog.show(qoran_template.this, "", "کمی صبر کنید . . .");
        progressBar_circle.setCancelable(false);

    }
    public void hideProg()
    {
        progressBar_circle.dismiss();
    }
    public void play_click()
    {
        //boolean qr_isdown=pref.getBoolean("sura_"+id,false);
        int sound_actived=pref.getInt("qoran_qary_code",1);
        boolean qr_isdown=pref.getBoolean("sura_"+id+"_"+sound_actived,false);
        if(qr_isdown==true)
        {
            System.out.println("pakhsh shavad..................");
            if(mp.isPlaying())
            {
                mp.stop();
                play_btn.setImageResource(R.drawable.play_ico);
            }
            else
            {
                if(id==9)
                {
                    //pos_sound_play=position+1;
                    is_besm_played=true;
                }
                //pos_sound_play=1;
                playAya();
            }

        }
        else
        {
            showDialogDownloadSound();
        }
    }
    public void info_click()
    {
        final Dialog dialog_info = new Dialog(qoran_template.this);
        dialog_info.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_info.setContentView(R.layout.dialog_1_btn);

        dialog_info.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_info.setCancelable(true);
        TextView title= (TextView) dialog_info.findViewById(R.id.dialog1_title_txt);
        TextView matn= (TextView) dialog_info.findViewById(R.id.dialog1_matn_txt);
        final Button khob= (Button) dialog_info.findViewById(R.id.dialog1_btn_khob);

        dialog_info.findViewById(R.id.dialog1_chb).setVisibility(View.GONE);

        title.setTypeface(mf.getYekan());
        matn.setTypeface(mf.getYekan());
        khob.setTypeface(mf.getYekan());


        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        Vector v=dbHelper.getQoranInfo(id);
        String name=v.elementAt(0)+"";
        String tedad=v.elementAt(1)+"";
        tedad=tedad.trim();
        String hezb=v.elementAt(2)+"";
        hezb=hezb.trim();
        String joz=v.elementAt(3)+"";
        joz=joz.trim();
        String maki=v.elementAt(4)+"";


        SpannableString name_tit = new SpannableString("نام سوره "+":");
        name_tit.setSpan(new ForegroundColorSpan(Color.parseColor("#1a896c")), 0, name_tit.length(), 0);
        SpannableString tedad_tit = new SpannableString("تعداد آیات"+":");
        tedad_tit.setSpan(new ForegroundColorSpan(Color.parseColor("#1a896c")), 0, tedad_tit.length(), 0);
        SpannableString joz_tit = new SpannableString("جزء "+":");
        joz_tit.setSpan(new ForegroundColorSpan(Color.parseColor("#1a896c")), 0, joz_tit.length(), 0);
        SpannableString hezb_tit = new SpannableString("حزب "+":");
        hezb_tit.setSpan(new ForegroundColorSpan(Color.parseColor("#1a896c")), 0, hezb_tit.length(), 0);


        SpannableString name_matn = new SpannableString(name+"-"+maki);
        name_matn.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, name_matn.length(), 0);
        SpannableString tedad_matn = new SpannableString(tedad);
        tedad_matn.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, tedad_matn.length(), 0);
        SpannableString joz_matn= new SpannableString(joz);
        joz_matn.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, joz_matn.length(), 0);
        SpannableString hezb_matn = new SpannableString(hezb);
        hezb_matn.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, hezb_matn.length(), 0);

        CharSequence finalText = TextUtils.concat(name_tit,name_matn,"\n",tedad_tit,tedad_matn,"\n",joz_tit,joz_matn,"\n",hezb_tit,hezb_matn);

        title.setText("اطلاعات سوره");
        khob.setText("خب");
        matn.setText(finalText, TextView.BufferType.SPANNABLE);

        khob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog_info.dismiss();
            }
        });

        dialog_info.show();
    }
    private boolean checkDb(String adr)
    {
        boolean check = false;
        File file = new File(adr);
        check = file.exists();
        return check;
    }
    public void showDialogDownload(final String mydb)//qoran_sharh
    {
        //System.out.println("adr="+adr);
        //System.out.println("name="+name);
        dialog_down = new Dialog(qoran_template.this);
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
        title.setText("دریافت تفسیر");
        matn.setText("آیا مایل به دریافت تفسیر نمونه و المیزان هستید؟");
        bale.setText("دانلود");
        kheyr.setText("خیر");

        bale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog_down.dismiss();
                //Toast.makeText(parent_act.this, "bale", Toast.LENGTH_SHORT).show();
                //new DownloadFileFromURL().execute(loc_adr,ser_adr);
                if(isNetworkAvailable()==false)
                {
                    Toast.makeText(qoran_template.this, "برای دریافت فایل به اینترنت متصل شوید...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    downloadTask = new DownloadTask(qoran_template.this);
                    bale.setEnabled(false);
                    tafsir_is_down=true;
                    downloadTask.execute(mydb);
                }

            }
        });

        kheyr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(tafsir_is_down==true)
                {
                    downloadTask.cancel(true);
                    tafsir_is_down=false;
                    String loc_adr=base_adr + "/parsmagz/qoran/"+mydb+".zip";
                    File f=new File(loc_adr);
                    if(f.exists())
                    {
                        f.delete();
                    }
                }

                dialog_down.dismiss();
                //Toast.makeText(parent_act.this, "kheyr", Toast.LENGTH_SHORT).show();
            }
        });

        dialog_down.show();
    }
    private class DownloadTask extends AsyncTask<String, Integer, String>
    {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        String down_str="";
        String cur_db="";

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
        }

        @Override
        protected String doInBackground(String... f_url)
        {
            down_str=f_url[0]+".zip";//qoran_sharh.zip
            cur_db=f_url[0];//qoran_sharh

            String loc_adr=base_adr + "/parsmagz/qoran/"+down_str;
            String ser_adr="http://www.valiasr-aj.com/_mobile/golbarg/"+down_str;
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

                byte data[] = new byte[4096];
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
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
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
            tafsir_is_down=false;
            //////////////////////mProgressDialog.dismiss();

            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
            {
                dialog_down.dismiss();
                //

                ShowProg();

                extractTask = new ExtractTask(getApplicationContext());
                extractTask.execute(down_str,cur_db);//(qoran_sharh.zip,qoran_sharh)
            }
            //Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }
    }//down
    private class ExtractTask extends AsyncTask<String, Integer, String>
    {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        String down_str="";
        String cur_db="";

        public ExtractTask(Context context) {
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
        }

        @Override
        protected String doInBackground(String... f_url)
        {
            down_str=f_url[0];//qoran_sharh.zip
            cur_db=f_url[1];//qoran_sharh
            extract(down_str,cur_db);
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
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
            {

                System.out.println("extract tamam shodddddddddddddddddddddddd");

                File f = new File(base_adr + "/parsmagz/qoran/" + cur_db + ".zip");
                if (f.exists()) {
                    f.delete();
                }
                hideProg();
                //startActivity(tempint);
                sharh_click();

            }
        }
    }//extract
    public void extract(String down_str,String cur_db)//question1.zip,question1
    {

        ZipFile zipFile = null;
        try {
            String ss=base_adr + "/parsmagz/qoran/"+down_str;
            System.out.println("ss="+ss);
            zipFile = new ZipFile(base_adr + "/parsmagz/qoran/"+down_str);
        } catch (ZipException e) {
            System.out.println("e1=" + e.getMessage());
        }
        try
        {
            if (zipFile.isEncrypted())
            {
                zipFile.setPassword("2839");
            }
        }
        catch (ZipException e)
        {
            System.out.println("e2=" + e.getMessage());
        }
        try
        {
            zipFile.extractAll(base_adr + "/parsmagz/qoran");


        }
        catch (ZipException e) {
            System.out.println("e3=" + e.getMessage());
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    /*public void setSharh()
    {
        Intent myint = new Intent(getApplicationContext(), matn_template.class);
        myint.putExtra("type", type);
        myint.putExtra("key",-1);
        myint.putExtra("onvan","شرح");
        myint.putExtra("dsc","");
        startActivity(myint);
    }*/
    public void kuchak_click()
    {
        int mysize = pref.getInt("qoran_txt_size", 0);
        mysize -= 2;
        editor.putInt("qoran_txt_size", mysize);
        editor.commit();
        //drawSize();
        setBoard();
    }
    public void bozorg_click()
    {
        int mysize = pref.getInt("qoran_txt_size", 0);
        mysize += 2;
        editor.putInt("qoran_txt_size", mysize);
        editor.commit();
        //drawSize();
        setBoard();
    }
    public void showDialogDownloadSound()
    {
        dialog_down = new Dialog(qoran_template.this);
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

        prog.setIndeterminate(false);
        prog.setMax(kol_aya);


        title.setTypeface(mf.getYekan());
        matn.setTypeface(mf.getYekan());
        bale.setTypeface(mf.getYekan());
        kheyr.setTypeface(mf.getYekan());
        txt_darsad.setTypeface(mf.getYekan());

        txt_darsad.setText("0/"+kol_aya);
        title.setText("دریافت صوت");
        matn.setText("آیا مایل به دریافت صوت این سوره هستید؟");

        bale.setText("دانلود");
        kheyr.setText("خیر");


        bale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()==false)
                {
                    Toast.makeText(qoran_template.this, "برای دریافت فایل به اینترنت متصل شوید...", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    /////////////////cur_aye=1;
                    downloadsoundtask = new DownloadSoundTask(qoran_template.this);
                    bale.setEnabled(false);
                    sound_is_down=true;
                    downloadsoundtask.execute("");
                }

            }
        });
        kheyr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(sound_is_down==true)
                {
                    downloadsoundtask.cancel(true);
                    sound_is_down=false;
                    System.out.println("cur_sound_isdownloading="+cur_sound_isdownloading);

                    if(cur_sound_isdownloading.equals("")==false && cur_sound_isdownloading.equals(" ")==false)
                    {
                        int sound_actived=pref.getInt("qoran_qary_code",1);
                        String cur_adr=base_adr + "/parsmagz/qoran/sound"+sound_actived+"/"+cur_sound_isdownloading;
                        //String cur_adr=base_adr + "/valiasr/qoran/sound/"+cur_sound_isdownloading;
                        System.out.println("cur_adr="+cur_adr);
                        File f=new File(cur_adr);
                        if(f.exists())
                        {
                            System.out.println("cur_adr deleted");
                            f.delete();
                        }
                    }
                }

                dialog_down.dismiss();
            }
        });

        dialog_down.show();
    }
    private class DownloadSoundTask extends AsyncTask<String, Integer, String>
    {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        String down_str="";
        String sound_name="";
        boolean besm_is_downloading=false;
        public DownloadSoundTask(Context context) {
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
        }

        @Override
        protected String doInBackground(String... f_url)
        {
            String loc_adr="";
            String ser_adr="";

            if(id==1)
            {
                String part1=id+"";
                int diff=3-part1.length();

                for(int i=0;i<diff;i++)
                {
                    part1="0"+part1;
                }
                String part2=cur_aye+"";
                diff=3-part2.length();

                for(int i=0;i<diff;i++)
                {
                    part2="0"+part2;
                }
                sound_name=part1+part2;
            }
            else
            {
                if(is_besm_down==false)
                {
                    sound_name="001001";
                    besm_is_downloading=true;
                }
                else
                {
                    String part1=id+"";
                    int diff=3-part1.length();

                    for(int i=0;i<diff;i++)
                    {
                        part1="0"+part1;
                    }
                    String part2=cur_aye+"";
                    diff=3-part2.length();

                    for(int i=0;i<diff;i++)
                    {
                        part2="0"+part2;
                    }
                    sound_name=part1+part2;
                }
            }

            cur_sound_isdownloading=sound_name+".mp3";
            int sound_actived=pref.getInt("qoran_qary_code",1);
            loc_adr=base_adr + "/parsmagz/qoran/sound"+sound_actived+"/"+sound_name+".mp3";
            //loc_adr=base_adr + "/valiasr/qoran/sound/"+sound_name+".mp3";
            ///////////////////////////////////String qoran_qary_url=pref.getString("qoran_qary_url","http://www.valiasr-aj.com/_mobile/quran/sound/Menshawi_16kbps/");
            String qoran_qary_url=pref.getString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Menshawi_16kbps/");
            ser_adr=qoran_qary_url+sound_name+".mp3";
            //ser_adr="http://everyayah.com/data/Menshawi_16kbps/"+sound_name+".mp3";


            System.out.println("loc_adr="+loc_adr);
            System.out.println("ser_adr="+ser_adr);

            File f=new File(loc_adr);
            if(f.exists()==false)
            {
                System.out.println("hhhhaaaaaaaaaaaaaaaaaaahhhaaaaaaaaaaaaaaaa");
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(ser_adr);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("Accept-Encoding", "identity");
                    connection.setInstanceFollowRedirects(false);



                    //true=khata 301
                    //System.out.println("connection.getInstanceFollowRedirects()="+connection.getInstanceFollowRedirects());

                    connection.connect();


                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    {
                        return "Server returned HTTP mehdi" + connection.getResponseCode() + " " + connection.getResponseMessage();
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

                    byte data[] = new byte[4096];
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
                    System.out.println("e99="+e.getMessage());
                    return e.toString();
                } finally {
                    try {
                        if (output != null)
                            try {
                                output.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }
            }


            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            //prog.setIndeterminate(false);
            //prog.setMax(kol_aya);
            //prog.setProgress(progress[0]);
            //txt_darsad.setText(progress[0]+"%");
            //System.out.println("aaaa progress[0]="+progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            //tafsir_is_down=false;
            int sound_actived=pref.getInt("qoran_qary_code",1);
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
            {
                if(id==1)
                {
                    cur_aye++;
                    prog.setProgress(cur_aye);
                    txt_darsad.setText(cur_aye+"/"+kol_aya);
                    if(cur_aye>kol_aya)
                    {
                        sound_is_down=false;
                        dialog_down.dismiss();
                        editor.putBoolean("sura_"+id+"_"+sound_actived,true).commit();
                    }
                    else
                    {
                        downloadsoundtask = new DownloadSoundTask(qoran_template.this);
                        downloadsoundtask.execute("");
                    }
                }
                else
                {
                    prog.setProgress(cur_aye);
                    txt_darsad.setText(cur_aye+"/"+kol_aya);
                    if(besm_is_downloading)
                    {
                        is_besm_down=true;
                        downloadsoundtask = new DownloadSoundTask(qoran_template.this);
                        downloadsoundtask.execute("");
                    }
                    else
                    {
                        cur_aye++;
                        prog.setProgress(cur_aye);
                        txt_darsad.setText(cur_aye+"/"+kol_aya);
                        if(cur_aye>=kol_aya)
                        {
                            sound_is_down=false;
                            dialog_down.dismiss();
                            editor.putBoolean("sura_"+id+"_"+sound_actived,true).commit();
                        }
                        else
                        {
                            downloadsoundtask = new DownloadSoundTask(qoran_template.this);
                            downloadsoundtask.execute("");
                        }
                    }
                }//
            }
        }
    }//downsound
    private void playAya()//pos=1
    {
        System.out.println("playAya playAya  pos_sound_play="+pos_sound_play);
        play_btn.setImageResource(R.drawable.pause_ico);
        mp=new MediaPlayer();
        String sound_name="";
        String loc_adr="";
        int sound_actived=pref.getInt("qoran_qary_code",1);

        System.out.println("pos_sound_play="+pos_sound_play);
        System.out.println("is_besm_played="+is_besm_played);
        if(pos_sound_play==1&&is_besm_played==false)//besm
        {
            sound_name="001001";
            loc_adr=base_adr + "/parsmagz/qoran/sound"+sound_actived+"/"+sound_name+".mp3";

            try
            {
                mp.setDataSource(loc_adr);
                mp.prepare();
                mp.setVolume(1f, 1f);
                mp.start();

                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        System.out.println("setOnCompletionListener setOnCompletionListener setOnCompletionListener  111");
                        mp.reset();
                        mp.release();
                        mp=null;
                        if(id==1)
                        {
                            is_besm_played=true;
                            pos_sound_play=pos_sound_play+1;
                        }
                        else
                        {
                            is_besm_played=true;
                        }

                        playAya();
                    }

                });

            }
            catch (IOException e)
            {
                System.out.println("eeeeeeeeeee1="+e.getMessage());
                //editor.putBoolean("sura_"+id,false).commit();
            }


        }//besm
        else
        {
            if(id==1)
            {
                System.out.println("11111111111111111111111");
                System.out.println("pos_sound_play="+pos_sound_play+"  kol_aya="+kol_aya);
                if(pos_sound_play<=kol_aya)
                {
                    System.out.println("222222222222222222222222222222");
                    String part1=id+"";
                    int diff=3-part1.length();

                    for(int i=0;i<diff;i++)
                    {
                        part1="0"+part1;
                    }
                    String part2=pos_sound_play+"";
                    diff=3-part2.length();

                    for(int i=0;i<diff;i++)
                    {
                        part2="0"+part2;
                    }
                    sound_name=part1+part2;
                    loc_adr=base_adr + "/parsmagz/qoran/sound"+sound_actived+"/"+sound_name+".mp3";
                    //loc_adr=base_adr + "/valiasr/qoran/sound/"+sound_name+".mp3";

                    try
                    {
                        mp.setDataSource(loc_adr);
                        mp.prepare();
                        mp.setVolume(1f, 1f);
                        mp.start();

                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp)
                            {
                                System.out.println("setOnCompletionListener setOnCompletionListener setOnCompletionListener 2222");
                                // TODO Auto-generated method stub
                                mp.reset();
                                mp.release();
                                mp=null;
                                pos_sound_play=pos_sound_play+1;
                                playAya();
                            }

                        });

                    }
                    catch (IOException e)
                    {
                        System.out.println("eeeeeeeeeee2="+e.getMessage());
                        //editor.putBoolean("sura_"+id,false).commit();
                    }
                }
                else
                {
                    play_btn.setImageResource(R.drawable.play_ico);
                    System.out.println("sure tamam shod.......................");
                }
            }
            else//id!=1
            {
                System.out.println("6666666666666666666666666");
                if(pos_sound_play<kol_aya)
                {
                    System.out.println("777777777777777777777777777777");
                    String part1=id+"";
                    int diff=3-part1.length();

                    for(int i=0;i<diff;i++)
                    {
                        part1="0"+part1;
                    }
                    String part2=pos_sound_play+"";
                    diff=3-part2.length();

                    for(int i=0;i<diff;i++)
                    {
                        part2="0"+part2;
                    }
                    sound_name=part1+part2;
                    loc_adr=base_adr + "/parsmagz/qoran/sound"+sound_actived+"/"+sound_name+".mp3";
                    //loc_adr=base_adr + "/valiasr/qoran/sound/"+sound_name+".mp3";

                    try
                    {
                        mp.setDataSource(loc_adr);
                        mp.prepare();
                        mp.setVolume(1f, 1f);
                        mp.start();

                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                // TODO Auto-generated method stub
                                mp.reset();
                                mp.release();
                                mp=null;
                                pos_sound_play=pos_sound_play+1;
                                playAya();
                            }

                        });

                    }
                    catch (IOException e)
                    {
                        System.out.println("eeeeeeeeeee3="+e.getMessage());
                        //editor.putBoolean("sura_"+id,false).commit();
                    }
                }
                else
                {
                    play_btn.setImageResource(R.drawable.play_ico);
                    System.out.println("sure tamam shod.......................");
                }
            }

        }//////


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            mp.stop();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    private void setLastSeen()
    {
        String dsc="";
        int key=0;
        String sound_name="";
        String dsctarjome="";
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_lastseen(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//4
        try
        {
            Cursor c = db1.rawQuery("SELECT * FROM tb_lastseen ", null);
            System.out.println("c.getCount()1="+c.getCount());
            if(c!= null)
            {
                if(c.getCount()==0)//item dar db vojud nadarad pas insert mikonim
                {
                    db1.execSQL("INSERT INTO tb_lastseen (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan+"','"+key +"','"+id  +"','"+3 +"');");
                }
                if(c.getCount()>0)//item dar db vojud darad pas delete mikonim
                {
                    System.out.println("c.getCount()4="+c.getCount());
                    db1.execSQL("DELETE  FROM tb_lastseen ");
                    db1.execSQL("INSERT INTO tb_lastseen (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan+"','"+key +"','"+id  +"','"+3 +"');");
                    System.out.println("c.getCount()5="+c.getCount());
                }
            }
            System.out.println("c.getCount()3="+c.getCount());
        }
        catch(Exception e)
        {
            System.out.println("eeeeeeeeeeeerrrrrrrrr2"+e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage()+"  bb",Toast.LENGTH_SHORT).show();
        }
    }
    public void rahnema_click()
    {
        final Dialog dialog_info = new Dialog(qoran_template.this);
        dialog_info.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_info.setContentView(R.layout.dialog_1_btn);

        dialog_info.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_info.setCancelable(true);
        TextView title= (TextView) dialog_info.findViewById(R.id.dialog1_title_txt);
        TextView matn= (TextView) dialog_info.findViewById(R.id.dialog1_matn_txt);
        final Button khob= (Button) dialog_info.findViewById(R.id.dialog1_btn_khob);
        final AppCompatCheckBox chek=(AppCompatCheckBox) dialog_info.findViewById(R.id.dialog1_chb);

        title.setTypeface(mf.getYekan());
        matn.setTypeface(mf.getYekan());
        khob.setTypeface(mf.getYekan());
        chek.setTypeface(mf.getYekan());


        String str="";
        str="با لمس هر آیه صوت آن پخش میشود و با نگه داشتن انگشت روی هر آیه، وارد تفسیر آن آیه میشوید.";


        SpannableString name_tit = new SpannableString(str);
        name_tit.setSpan(new ForegroundColorSpan(Color.parseColor("#000000")), 0, name_tit.length(), 0);



        title.setText("راهنما");
        khob.setText("خب");
        chek.setText("عدم نمایش مجدد");
        matn.setText(name_tit, TextView.BufferType.SPANNABLE);

        khob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dialog_info.dismiss();
            }
        });
        chek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                System.out.println("chek.isChecked()="+chek.isChecked());
                if(chek.isChecked()==true)
                {
                    editor.putBoolean("qoran_rahnema",false).commit();
                }
                else if(chek.isChecked()==true)
                {
                    editor.putBoolean("qoran_rahnema",true).commit();
                }
            }
        });

        dialog_info.show();
    }
    public   void stopMedia()
    {
        //mp=new MediaPlayer();
        mp.stop();
        //mp=null;
        play_btn.setImageResource(R.drawable.play_ico);
    }
    public   void stopQaryMedia()
    {
        mp=new MediaPlayer();
        //mp.stop();
        //mp=null;
        play_btn.setImageResource(R.drawable.play_ico);
    }
}
