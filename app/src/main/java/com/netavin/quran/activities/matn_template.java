package com.netavin.quran.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.netavin.quran.R;
import com.netavin.quran.adapters.font_adapter;
import com.netavin.quran.classes.CustomTypefaceSpan;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.MyLinearLayoutManager;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 24/06/2017.
 */
public class matn_template extends AppCompatActivity
{
    DatabaseHelper dbHelper = null;
    String base_adr="";

    TextView title,matn;
    font_class mf;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    SQLiteDatabase db1 = null;
    String DBNAME = "qoran_valiasr.db";

    ImageView qalam_btn, fav_btn, kuchak_btn, bozorh_btn,eshterak_btn,play_btn;

    //1=qoran   2=ayat   3=porseman   4=hekayat   5=tajvid   6=moama   7=maqalat   8=ahadis   9=tajvid_click   10=porseman_click
    //11=moama_click
    int type=0;
    String onvan="";
    //String dsc="";
    int key=0;
    int pos=0;
    //String sound_name="";
    //String dsctarjome="";

    public Dialog dialog;
    float curElementSize=0;
    String eshterak_txt="";

    FrameLayout board;


    MediaPlayer mp;
    VideoView video;
    MediaController mediaController;
    boolean is_pause=false;

    String onvan_asl="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matn_template);

        mf = new font_class(this);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        base_adr = pref.getString("base_adr", "null");

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.matntemp_scroll).setKeepScreenOn(screen_on);

        db1 = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
        //db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,DSC TEXT,KEY INTEGER,POS INTEGER,SOUND_NAME TEXT,DSCTARJOME TEXT,PAGE INTEGER); ");//1
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//1

        board = (FrameLayout) findViewById(R.id.matntemp_board);

        title = (TextView) findViewById(R.id.matntemp_title);
        matn = (TextView) findViewById(R.id.matntemp_txt);

        title.setTypeface(mf.getAdobeBold());

        qalam_btn = (ImageView) findViewById(R.id.matntemp_qalam);
        fav_btn = (ImageView) findViewById(R.id.matntemp_alaqemandiha);
        kuchak_btn = (ImageView) findViewById(R.id.matntemp_kuchak);
        bozorh_btn = (ImageView) findViewById(R.id.matntemp_bozorg);
        eshterak_btn = (ImageView) findViewById(R.id.matntemp_eshterak);


        Intent myint = getIntent();
        type = myint.getIntExtra("type", 0);
        key = myint.getIntExtra("key", 0);
        onvan = myint.getStringExtra("onvan");
        //dsc = myint.getStringExtra("dsc");
        pos = myint.getIntExtra("pos", 0);
        //sound_name = myint.getStringExtra("sound_name");
        //dsctarjome = myint.getStringExtra("dsctarjome");


        System.out.println("typeeeeeeeeeeeeeeeeeeeeee="+type);

        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mp = new MediaPlayer();

        setBoard();
        setfont();
        setFav();
        setLastSeen();

        curElementSize =pixelsToSp(getApplicationContext(), (int) getResources().getDimension(R.dimen.txt_size));
        drawSize();

        qalam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                qalam_dialog();
            }
        });
        fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                fav_click();
            }
        });
        kuchak_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //showProg("");
                kuchak_click();
            }
        });
        bozorh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                //showProg("");
                bozorg_click();
            }
        });

        eshterak_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                eshterak_click();
            }
        });


        /*ImageView tanzimat= (ImageView) findViewById(R.id.matntemp_tanzimat);
        ImageView about= (ImageView) findViewById(R.id.matntemp_about);
        tanzimat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish();
                Intent myint=new Intent(getApplicationContext(),tanzimat.class);
                startActivity(myint);
            }
        });
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
    private void setBoard()
    {
        if(type==2)//ayat
        {
            qalam_btn.setVisibility(View.GONE);
            title.setText(Html.fromHtml("<center>"+onvan+"</center>"));
            onvan_asl=onvan;
            Vector vec=new Vector();
            dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
            vec=dbHelper.getAyatDet(key);

            SpannableString matn_txt = new SpannableString(vec.elementAt(0)+"");
            matn_txt.setSpan(new ForegroundColorSpan(Color.parseColor("#993300")), 0, matn_txt.length(), 0);
            matn_txt.setSpan(new CustomTypefaceSpan("", mf.getNabi()), 0, matn_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            SpannableString tarjome_txt = new SpannableString(vec.elementAt(1)+"");
            tarjome_txt.setSpan(new ForegroundColorSpan(Color.BLACK), 0, tarjome_txt.length(), 0);
            tarjome_txt.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, tarjome_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            SpannableString tafsir_txt = new SpannableString(Html.fromHtml(vec.elementAt(2)+""));
            tafsir_txt.setSpan(new ForegroundColorSpan(Color.parseColor("#030793")), 0, tafsir_txt.length(), 0);
            tafsir_txt.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, tafsir_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            SpannableString ref_txt = new SpannableString(Html.fromHtml(vec.elementAt(3)+""));
            ref_txt.setSpan(new ForegroundColorSpan(Color.parseColor("#006600")), 0, ref_txt.length(), 0);
            ref_txt.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, ref_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            CharSequence finalText = TextUtils.concat(matn_txt,"\n",tarjome_txt,"\n",tafsir_txt,"\n",ref_txt,"\n\n");
            eshterak_txt=matn_txt+"\n"+tarjome_txt+"\n"+tafsir_txt+"\n"+ref_txt;
            matn.setText(finalText, TextView.BufferType.SPANNABLE);
        }
        else if(type==7)//maqalat
        {
            onvan_asl=onvan;
            onvan="مقاله";
            title.setText(Html.fromHtml("<center>"+onvan+"</center>"));
            Vector vec=new Vector();
            dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
            vec=dbHelper.getMaqalatDet(key);
            SpannableString matn_txt = new SpannableString("عنوان : "+vec.elementAt(1)+"");
            matn_txt.setSpan(new ForegroundColorSpan(Color.parseColor("#993300")), 0, matn_txt.length(), 0);
            //matn_txt.setSpan(new CustomTypefaceSpan("", mf.getNabi()), 0, matn_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            SpannableString tarjome_txt = new SpannableString("نویسنده : "+vec.elementAt(0)+"");
            tarjome_txt.setSpan(new ForegroundColorSpan(Color.BLACK), 0, tarjome_txt.length(), 0);
            //tarjome_txt.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, tarjome_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            SpannableString tafsir_txt = new SpannableString(Html.fromHtml(vec.elementAt(2)+""));
            tafsir_txt.setSpan(new ForegroundColorSpan(Color.parseColor("#030793")), 0, tafsir_txt.length(), 0);
            //tafsir_txt.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, tafsir_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            SpannableString ref_txt = new SpannableString(Html.fromHtml(vec.elementAt(3)+""));
            ref_txt.setSpan(new ForegroundColorSpan(Color.parseColor("#006600")), 0, ref_txt.length(), 0);
            //ref_txt.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, ref_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            String tr=vec.elementAt(0)+"";
            CharSequence finalText = "";
            if(tr.equals("")||tr.equals(" ")||tr.equals("null"))
            {
                finalText = TextUtils.concat(matn_txt,"\n",tafsir_txt,"\n",ref_txt,"\n\n");
            }
            else
            {
                finalText = TextUtils.concat(matn_txt,"\n",tarjome_txt,"\n",tafsir_txt,"\n",ref_txt,"\n\n");
            }
            eshterak_txt=matn_txt+"\n"+tarjome_txt+"\n"+tafsir_txt+"\n"+ref_txt;
            matn.setText(finalText, TextView.BufferType.SPANNABLE);
        }
        else if(type==3)//porseman
        {
            onvan_asl=onvan;
            onvan="پرسمان";
            title.setText(Html.fromHtml("<center>"+onvan+"</center>"));
            Vector vec=new Vector();
            dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
            vec=dbHelper.getPorsemanDet(key);
            SpannableString soal_txt = new SpannableString(vec.elementAt(0)+"");
            soal_txt.setSpan(new ForegroundColorSpan(Color.parseColor("#993300")), 0, soal_txt.length(), 0);
            //soal_txt.setSpan(new CustomTypefaceSpan("", mf.getNabi()), 0, soal_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            SpannableString javab_txt = new SpannableString(vec.elementAt(1)+"");
            javab_txt.setSpan(new ForegroundColorSpan(Color.BLACK), 0, javab_txt.length(), 0);
            //javab_txt.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, javab_txt.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            CharSequence finalText = TextUtils.concat(javab_txt,"\n\n");
            eshterak_txt=javab_txt+"";
            matn.setText(finalText, TextView.BufferType.SPANNABLE);
        }
        else if(type==4)//dastan
        {
            onvan_asl=onvan;
            onvan="حکایت";
            title.setText(Html.fromHtml("<center>"+onvan+"</center>"));
            Vector vec=new Vector();
            dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
            vec=dbHelper.getDastanDet(key);
            SpannableString text = new SpannableString(Html.fromHtml(vec.elementAt(0)+""));
            //text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), 0);
            //text.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            CharSequence finalText = TextUtils.concat(text,"\n\n\n");
            matn.setText(finalText, TextView.BufferType.SPANNABLE);
            eshterak_txt=text+"";
        }
        else if(type==5)//tajvid
        {
            onvan_asl=onvan;
            onvan="آموزش تجوید";
            title.setText(Html.fromHtml("<center>"+onvan+"</center>"));
            Vector vec=new Vector();
            dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
            vec=dbHelper.getTajvidDet(key);
            SpannableString text = new SpannableString(Html.fromHtml(vec.elementAt(0)+""));
            //text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), 0);
            //text.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            CharSequence finalText = TextUtils.concat(text,"\n\n\n");
            matn.setText(finalText, TextView.BufferType.SPANNABLE);
            eshterak_txt=text+"";
        }
        else if(type==6)//moama
        {
            onvan_asl=onvan;
            Vector vec=new Vector();
            dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
            vec=dbHelper.getMoamaDet(key);

            SpannableString text_soal = new SpannableString(onvan);
            text_soal.setSpan(new ForegroundColorSpan(Color.parseColor("#993300")), 0, text_soal.length(), 0);
            onvan="معما";
            title.setText(Html.fromHtml("<center>"+onvan+"</center>"));

            SpannableString text_javab = new SpannableString(Html.fromHtml(vec.elementAt(0)+""));
            text_javab.setSpan(new ForegroundColorSpan(Color.parseColor("#006600")), 0, text_javab.length(), 0);
            //text.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            CharSequence finalText = TextUtils.concat(text_soal,"\n",text_javab,"\n\n\n");
            matn.setText(finalText, TextView.BufferType.SPANNABLE);
            eshterak_txt=text_soal+"\n"+text_javab+"";
        }
        else if(type==70)//about
        {
            fav_btn.setVisibility(View.GONE);
            title.setText(Html.fromHtml("<center>"+onvan+"</center>"));
            String str="";
            if(key==0)
            {
                str="<font color=blue size=+2 >  تاسيس \t  </font><BR>\n" +
                        "\n" +
                        "مؤسسه تحقيقاتي حضرت ولي عصر عليه السلام در سال 1370 (هجري شمسي) زير نظر حضرت آيت اللّه خزعلي با مسئوليت حجة الاسلام والمسلمين سيد محمد حسيني قزويني در راستاي تحقيقات معارف اسلامي بويژه بخش فقه ، رجال وحديث تأسيس گرديد\n" +
                        "<BR>\n" +
                        "آدرس: قم - خيابان صفائيه - كوچه ممتاز- كوچه 7- پلاك 39\n" +
                        "<BR>\n" +
                        "تلفن:37840413\n" +
                        "<BR>";
            }
            else if(key==1)
            {
                str="<font color=blue size=+2 >  بخش حديث \t </font><BR>\n" +
                        "\n" +
                        "در اين بخش كتب روايي شيعه و احاديث معصومين: به شرح ذيل كار شده است:\n" +
                        "<BR>\n" +
                        "1 - تعيين معصومي كه روايت از او صادر شده چه آنكه نام آن معصوم بصورت صريح ذكر شده باشد و يا بصورت كنيه مانند: ابو الحسن، ابو جعفر، و يا بصورت كنايه مانند: الماضي، العالم، الرجل، و يا بصورت مضمر مانند عنه، سألته و كتبت اليه آمده باشد.\n" +
                        "<BR>\n" +
                        "2 - تفكيك رواياتي كه از ائمه و انبياء و ملائكه (ع) نقل شده است.\n" +
                        "<BR>\n" +
                        "3 - تفكيك روايات قدسي از غير قدسي.\n" +
                        "<BR>\n" +
                        "4 - تفكيك احاديث و سيره.\n" +
                        "<BR>\n" +
                        "5 -تفكيك احاديث مضمر، مرسل، مقطوع ، مرفوع و مجهول.\n" +
                        "<BR>\n" +
                        "6 - تعيين آغاز و پايان حديث.\n" +
                        "<BR>\n" +
                        "7 - تعيين احاديثي كه در جواب سؤال سائل آمده و به تنهايي مفهوم ندارند مانند: لابأس، نعم، لا و ... و ارتباط آنها به مورد سؤال. لازم به توضيح است كه كليّه موارد فوق در داخل كتاب با رمز ويژه مشخص گرديده است.\n" +
                        "<BR>\n" +
                        "<font color=red> هدف نهايي: </font>\n" +
                        "<BR>\n" +
                        "هدف اصلي از اين كار به شرح ذيل مي باشد:\n" +
                        "<BR>\n" +
                        "1 - تأليف كتاب موسوعة اطراف الحديث كه آغاز احاديث بصورت الفبائي خواهد آمد.\n" +
                        "<BR>\n" +
                        "2 - تأليف موسوعه هر يك از معصومين (ع) با جمع آوري تمام احاديثي كه آن معصوم مستقلاً روايت فرموده و يا از معصوم قبل از خود نقل نموده و يا معصوم بعد، از او نقل نموده است. البته در اين قسمت تمام احاديث هر معصوم موضوع بندي شده و احاديث احكام ، تفسير ، اخلاق ، عقائد، تاريخ و ... از هم تفكيك خواهد شد.\n" +
                        "<BR>\n" +
                        "3 - تأليف موسوعه انبياء عظام و ملائكه صلوات الله عليهم اجمعين.\n" +
                        "<BR>\n" +
                        "4 - تأليف احاديث مقطوعه و مرفوعه كه نام معصومي كه آن را نقل نموده مشخص نشده.\n" +
                        "<BR>\n" +
                        "5 - ارائه نرم افزاري تحت عنوان موسوعة المعصومين كه حاوي تمام احاديث معصومين (ع) با انواع جستجوهاي لفظي وموضوعي\n"+
                        "<BR>\n";
            }
            else if(key==2)
            {
                str="<font color=blue size=+2 >  بخش رجال \t  </font><BR>\n" +
                        "<font color=red> الف) هوية الراوي</font>\n" +
                        "<BR>\n" +
                        "در اين بخش براي هريك از رواتي كه در سند كتب اربعه و وسائل الشيعة و مستدرك الوسائل قرار گرفته پرونده اي تشكيل شده و به شرح ذيل تحقيقات انجام گرفته است:\n" +
                        "<BR>\n" +
                        "الف: هوية الراوي كه شامل نام، كنيه، لقب، نسبت، محل اقامت، ولادت، وفات، طبقه و مذهب راوي مي باشد.\n" +
                        "<BR>\n" +
                        "ب: مترادفات الراوي كه شامل تمام عناويني است كه راوي مورد بحث با آن عناوين در سند روايات قرار گرفته است؛ چه بسا بعضي از روات با بيش از 20 عنوان مختلف در سند روايات و كتب رجالي قرار گرفته است.\n" +
                        "<BR>\n" +
                        "ج: مكانة الراوي كه شامل خلاصه اقوال علماي رجال در باره اعتبار وعدم اعتبار راوي مورد بحث مي شود.\n" +
                        "<BR>\n" +
                        "د: صحبة الراوي كه راوي مورد بحث، زمان كداميك از معصومين (ع) را درك كرده و از آن نقل روايت نموده است.\n" +
                        "<BR>\n" +
                        "ه : مصادر الترجمة كه شامل آدرس و عناوين كتب رجالي و روايي است كه راوي مورد نظر در آنجا ذكر شده و ترجمه گرديده است. البته در موارد مورد نياز نسبت به ضبط كلمات و تصحيفاتي كه در نام وي به وقوع پيوسته، تحقيق لازم انجام گرفته است\n" +
                        "<BR>\n" +
                        "<font color=red> ب ) تصحيفات</font>\n" +
                        "<BR>\n" +
                        "در اين قسمت، موضوع مورد بحث، تصحيفاتي است كه در كتب اربعه بوقوع پيوسته است.\n" +
                        "<BR>\n" +
                        "ابتدا كليّه مطالبي را كه فقهاي بزرگ و اعاظم رضوان الله تعالي عليهم، مانند: صاحب معالم در منتقي الجمان و كاظمي در مشتركات و مجلسي در روضة المتقين و بروجردي در الموسوعة الرجاليّة و خوئي در معجم رجال الحديث آورده اند جمع آوري شده است.\n" +
                        "<BR>\n" +
                        "و در ضمن تحقيق و يا با استفاده از كامپيوتر مواردي را كه از نظر مبارك آنان پوشيده مانده، ثبت شده است.\n" +
                        "<BR>\n" +
                        "سپس در بعضي از موارد نسبت به فرمايشات آنان مطالبي افزوده شده و چه بسا نقد و بررسي هم انجام گرفته است.\n" +
                        "<BR>\n" +
                        "و اين بخش، از اهميت ويژه اي برخوردار است و اثر سازنده در فتاوي فقهاء دارد زيرا تصحيف و تحريف در سند چه بسا موجب مي شود كه فقيه يك روايت را صحيح بداند و حال آنكه در واقع ضعيف است و يا بعكس.\n" +
                        "<BR>\n" +
                        "بعنوان مثال: عبد الله بن سنان در سند حديث مقدار كرّ ثلاثة أشبار في ثلاثة أشبار قرار گرفته كه تصحيف است و در واقع محمد بن سنان صحيح است كه باتوجه به اينكه عبد الله مورد وثوق و محمد ضعيف است. و از اين روايت، از زمان شيخ طوسي تا مقدس اردبيلي به صحيحه تعبير شده ولي پس از طرح اشكال توسط صاحب معالم درمنتقي الجمان نظر فقهاء برگشته و از اين روايت به خبر و ضعيف تعبير شده است.\n" +
                        "<BR>\n" +
                        "و يا مانند: روايت حسن بن محبوب از ابو حمزه ثمالي كه حدود 38 مورد در كتب اربعه وارد شده كه آيت الله العظمي بروجردي مي فرمايد: روايت حسن بن محبوب از ابوحمزه قطعاً مرسل است زيرا حسن بن محبوب سال 148 هجري به دنيا آمده و ابوحمزه سال 150 هجري از دنيا رفته است و نمي تواند بدون واسطه از وي نقل حديث كند.\n" +
                        "<BR>\n" +
                        "حضرت آيت اللّه مؤمن هنگام بازديد از مؤسسه فرمودند: اگر اين بخش از تحقيقات وارد حوزه علميه شود تحول ايجاد مي كند \n" +
                        "<BR>\n" +
                        "<font color=red> ج ) ضبط اسماء الرواة </font>\n" +
                        "<BR>\n" +
                        "در اين قسمت ضبط و اعراب اسماء و القاب رواتي كه در سند روايات و يا در كتب رجالي قرار گرفته ماننددرّاج آيا به فتح دال و يا به ضمّ دال است و يا حريز بفتح حاء است و يا بضمّ آن كه فهم اين، در تشخيص وتعيين روات مشترك، نقش فعّال دارد.\n" +
                        "<BR>\n" +
                        "بعنوان مثال نام اسيد كه در سند روايات كتب اربعه و وسائل و مستدرك آمده، آيااَسيد بخوانيم و يااُسيد؟ كه با مراجعه به كتب ضبط مي بينيم ابن حجر در تبصير المنتبة 33 نفر از روات را ذكر مي كند كه نام خود و يا پدر آنان اُسيد بضمّ همزه است، و ابن ناصر الدين در توضيح المشتبه، نام 20 نفر را مي برد كه نام خود و يا پدر آنان اَسيد بفتح همزه است.\n" +
                        "<BR>\n" +
                        "در اين بخش كوشش شده در آغاز آنچه در كتب رجالي شيعه مانند: رجال ابن داود و ايضاح الاشتباه علامه حلّي و توضيح الاشتباه ساروي  و نضد الايضاح علم الهدي  وتنقيح المقال مامقاني  وضبط الاسماء طريحي و مانند آنها آمده استفاده شده و آنگاه در بعضي از موارد از كتب عامه مانند: توضيح المشتبه ابن  ناصرالدين، الانسابسمعاني المؤتلف و المختلف دارقطني والاكمال ابن ماكولا وتكملة الاكمال ابن نقطه، و امثال آنها نيز مطالبي افزوده شده است.\n" +
                        "<BR>\n" +
                        "و بعضي از بزرگان مانند: حضرات آيات خزعلي، آقاعزيز طباطبايي، شيخ محمد رضا جعفري ملاحظه نموده اند\n" +
                        "<BR>\n" +
                        "<font color=red> د) مشتركات</font>\n" +
                        "<BR>\n" +
                        "در اين قسمت اسامي رواتي كه بصورت مطلق در سند روايات قرار گرفته و مشترك ميان چند راوي است مانند: ابان واحمد و محمد مورد بحث قرار گرفته و با توجه به اساتيد و تلاميذ (راوي و مروي عنه) كه در اسانيد ساير روايات آمده، مشخص و معيّن مي گردد\n" +
                        "<BR>\n";
            }
            else if(key==3)
            {
                str="<FONT COLOR=BLUE>  بخش فقه \t </FONT>" +
                        "<BR>\n" +
                        "تحقيقات انجام گرفته در اين بخش شامل :\n" +
                        "<BR>\n" +
                        "<FONT COLOR=red>ابواب فقهي\n" +
                        "</FONT><BR>\n" +
                        "فهرست كليه ابواب فقهي از طهارت تا ديات بصورت منظم از 80 دوره كتب فقهي شيعه و 150 دوره كتب فقهي اهل سنّت، شامل حنبلي ، حنفي، شافعي، مالكي، زيدي و ظاهري، فهرست شده است.\n" +
                        "<BR>\n" +
                        "<FONT COLOR=red>رسائل فقهي</FONT>\n" +
                        "<BR>\n" +
                        "وفهرست كليّه رسائل فقهي موجود در كتابخانه هاي قم و بعضي از شهرستانها را كه مفيد بود استخراج شده.\n" +
                        "<BR>\n" +
                        "<FONT COLOR=red>شناسنامه كتب\n" +
                        "</FONT><BR>\n" +
                        "در بخش براي هر يك از كتب فقهي مورد بحث، شناسنامه كاملي تهيه گرديده كه شامل: موضوع، مؤلف، شمول (جميع ابواب فقهي، اكثر ابواب فقهي يا بعضي از ابواب فقهي) عبارت (متن، شرح يا حاشيه) منهج (روائي، فتوائي يا استدلالي) تاريخ تأليف، تاريخ طبع، تعداد اجزاء و مجلدات، مواضع ترجمه و ذكر كتاب، و شروح و حواشي كتاب و ...\n" +
                        "<BR>\n" +
                        "<FONT COLOR=red>شناسنامه مؤلفين\n" +
                        "</FONT><BR>\n" +
                        "در اين بخش براي هريك از مؤلّفان مورد بحث، شناسنامه اي تهيه شده كه شامل: اسم مشهور مؤلف، كنيه، لقب، مذهب، تاريخ ولادت، تاريخ وفات، محل دفن، و محل حيات، تأليفات، مشايخ، تلاميذ و مواضع ترجمه اوست\n" +
                        "<BR>\n";
            }
            else if(key==4)
            {
                str= "<font color=blue >  اطلاع رساني \t </font> \n" +
                        "<BR>\n" +
                        "واحد اطلاع رساني مؤسسه تحقيقاتي حضرت ولي عصر (عج) تير ماه 1376 با هدف ارائه خدمات تخصصي فناوري اطلاعات وارتباطات فعاليت خويش را آغاز كرد.\n" +
                        "<BR>\n" +
                        "از فعاليت هاي اين واحد مي توان به طراحي و افتتاح سايت دربيستم جمادي الثاني سال 1424 هجري قمري به دو زبان عربي و فارسي اشاره كرد (البته لازم به ذكر است كه اطلاعات اين سايت بصورت دايناميك دائما در حال تغيير و افزايش مي باشد)\n" +
                        "<BR>\n" +
                        "از ديگر محصولات اين واحد مي تواند به فيلمبرداري و تبديل و تكثير سخنراني استاد حسيني قزويني و ارائه و پخش آن به صورت سي دي هاي چندرسانه اي و همچنين برنامه نويسي و طراحي پكيچ هاي نرم افزارهاي پاسخگو، الرسول، ثار الله، كريم اهل بيت، شميم گل نرگس و ....اشاره نمود\n" +
                        "<BR>\n";
            }
            else if(key==5)
            {
                str="<BR>\n" +
                        "<font color=blue >  زندگينامه دكتر حسيني قزويني \t </font><BR>\n" +
                        "<BR>\n" +
                        "استاد دكتر سيد محمد حسيني قزويني فرزند سيد سليمان در سال 1331 در شهرستان قزوين به دنيا آمدند و قبل از پايان دوره دبيرستان، سال 1345 وارد حوزه علميه ابراهيميه، و سپس سرداران قزوين شدند و در طي مدت دو سال در آنجا مقدمات را فرا گرفتند و در سال 1347 براي ادامه تحصيل به حوزه علميه قم آمدند و در سال 1356 دروس سطح را به پايان برده و در درس خارج:\n" +
                        "<BR>\n" +
                        "1 - حضرت آيت الله العظمي گلپايگاني.\n" +
                        "<BR>\n" +
                        "2 - حضرت آيت الله العظمي اراكي .\n" +
                        "<BR>\n" +
                        "3 - حضرت آيت الله العظمي وحيد خراساني.\n" +
                        "<BR>\n" +
                        "4 - حضرت آيت الله العظمي شبيري زنجاني.\n" +
                        "<BR>\n" +
                        "5 - حضرت آيت الله العظمي سبحاني.\n" +
                        "<BR>\n" +
                        "شركت نموده و در سال 1368 موفق به اجازه اجتهاد از بعضي از مراجع عظام حوزه علميه قم گرديدند.\n" +
                        "<BR>\n" +
                        "از طرف مركز مديريت حوزه علميه قم مدرك سطح چهارم (دكترا) دريافت نمودند\n" +
                        "<BR>\n" +
                        "و همچنين موفق به دريافت دكترا در علم حديث از دانشگاه اسلامي (الحرة) هلند شدند.\n" +
                        "<BR>\n" +
                        "<font color=red> تدريس :</font>\n" +
                        "<BR>\n" +
                        "* تدريس دروس سطح عالي حوزه شامل 5 دوره كفايه الاصول و يك دوره رسائل و مكاسب\n" +
                        "<BR>\n" +
                        "* تدريس 20 سال رجال و درايه\n" +
                        "<BR>\n" +
                        "* تدريس درس خارج فقه (فقه مقارن) از سال1385\n" +
                        "<BR>\n" +
                        "* تدريس دوره هاي متعدد شيعه شناسي در مركز جهاني علوم اسلامي ؛ مجمع جهاني اهل البيت (ع) مركز تخصصي قضاء و مركز تخصصي مذاهب مركز تخصصي كلام و همچنين در برخي از دانشگاه هاي كشور\n" +
                        "<BR>\n" +
                        "<font color=red>پاسخ به شبهات ، مناظرات و ديدار ها:</font>\n" +
                        "<BR>\n" +
                        "*پاسخ به جديد ترين شبهات وهابيت از سال 80 روز پنج شنبه هر هفته در مدرسه مباركه فيضه قم\n" +
                        "<BR>\n" +
                        "* و هم چنين پاسخ به شبهات وهابيت روزهاي پنج شنبه يك هفته در ميان، در دانشگاه فردوسي و حوزه علميه مشهد مقدس.\n" +
                        "<BR>\n" +
                        "*برگزاري اردوهاي امامت و ولايت براي آشنايي اساتيد و روحانيون معزز استان هاي سني نشين با پاسخ به شبهات وهابيت مانند ( بوشهر ، سيستان و بلوچستان؛ خوزستان ، خراسان جنوبي و رضوي؛ تهران و...)\n" +
                        "<BR>\n" +
                        "*حدود 200 مورد مباحثات و مناظرات با علماء بزرگ اهل سنت و مفتيان و اساتيد دانشگاهاي عربستان سعودي مانند مناظره ايشان با شيخ محمد بن جميل بن زينو از مفتيان و علماي بزرگ مكه و دكتر حمدان از اساتيد بزرگ دانشگاه ام القري و شيخ عبد العزيز بن عبد اللّه آل شيخ مفتي اعظم سعودي و ...\n" +
                        "<BR>\n" +
                        "* مناظره با علماء اهل سنت در شبكه ماهواره اي سلام تي وي مانند ملازداه و مولوي عبد المجيد مرادزهي از اساتيد حوزه علميه زاهدان.\n" +
                        "<BR>\n" +
                        "* مناظره با بزرگان وهابي در شبكه جهاني (المستقله) پيرامون شهادت حضرت زهرا سلام الله عليها.\n" +
                        "<BR>\n" +
                        "<font color=red> برنامه  هاي راديويي ، تلويزيوني و ماهواره اي</font>\n" +
                        "<BR>\n" +
                        "* برنامه متعدد علمي تلويزيوني در شبكه اول، سوم و چهارم سيما ، و شبكه تهران و شبكه جهاني جام جم ، و برنامه مستقيم متعدد در راديوي معارف قم و ... پيرامون شبهات و پاسخ به آنها ، مسايل تربيتي ، علمي و ديني .\n" +
                        "<BR>\n" +
                        "* پاسخ به شبهات در شبكه ماهواره اي سلام تي وي روزهاي پنج شنبه هر هفته به صورت زنده به مدت چهار سال.\n" +
                        "<BR>\n" +
                        "<font color=red> تأليفات :</font>\n" +
                        "<BR>\n" +
                        "تاكنون 33 جلد كتاب از تأليفات ايشان چاپ و منتشر شده\n" +
                        "<BR>\n" +
                        "و 35 جلد كتاب آماده چاپ مي باشد\n" +
                        "<BR>\n" +
                        "29 جلد در دست تأليف دارند كه تا يك سال ديگر آماده چاپ خواهد شد ان شاء الله\n" +
                        "<BR>\n" +
                        "<font color=red> ليست كتابهايي كه تا كنون چاپ و منتشر شده است</font>\n" +
                        "<BR>\n" +
                        "موسوعة الإمام الجواد (ع) 2 جلد (كتاب برگزيده سال حوزه علميه قم)\n" +
                        "<BR>\n" +
                        "1- موسوعة الإمام الهادي (ع) 4 جلد\n" +
                        "<BR>\n" +
                        "2- موسوعة الإمام العسكري (ع) 6 جلد\n" +
                        "<BR>\n" +
                        "3- موسوعة الإمام الرضا (ع) 8 جلد\n" +
                        "<BR>\n" +
                        "4- نقد كتاب أصول مذهب الشيعة دكتر قفاري 2 جلد\n" +
                        "<BR>\n" +
                        "5- تحقيق الفضائل شاذان بن جبرئيل 1 جلد\n" +
                        "<BR>\n" +
                        "6- قصة الحوار الهادئ (جلد اول) 1 جلد\n" +
                        "<BR>\n" +
                        "7- المدخل إلي علم الرجال والدراية 1 جلد\n" +
                        "<BR>\n" +
                        "8- حديث الغدير وشبهة شكوي جيش اليمن(توسط سازمان حج چاپ شده) 1 جلد\n" +
                        "<BR>\n" +
                        "9- دراسات في أسانيد الكتب الروائية (بررسي تصيحفات و اشكالات سندي كتب اربعه) 2 جلد\n" +
                        "<BR>\n" +
                        "10- در حريم طوس 1 جلد\n" +
                        "<BR>\n" +
                        "11- ويژگيهاي امام حسين (ع) (تحقيق و ترجمه الخصائص الحسينية شيخ جعفر شوشتري) 1 جلد\n" +
                        "<BR>\n" +
                        "12- امام مهدي از ولادت تا ظهور (تحقيق و ترجمه الامام المهدي (ع) من الولادة إلي الظهور سيد كاظم قزويني توسط نشر الهادي چاپ شده) 1 جلد\n" +
                        "<BR>\n" +
                        "13- وهابيت از منظر عقل و شرع (جلد اول) 1 جلد\n" +
                        "<BR>\n" +
                        "14- چهل سؤال پيرامون خلافت و امامت 1 جلد.\n" +
                        "<BR>\n" +
                        "<font color=red> كتابهايي كه آماده چاپ مي باشد</font>\n" +
                        "<BR>\n" +
                        "15- موسوعة الإمام الكاظم (ع) 8 جلد\n" +
                        "<BR>\n" +
                        "16- ترجمه نقد كتاب أصول مذهب الشيعة 3 جلد\n" +
                        "<BR>\n" +
                        "17- واقعــة غدير خم دراسة توثيقية ، شبهات وردود 1 جلد\n" +
                        "<BR>\n" +
                        "18- پاسخ به شبهات غدير 1 جلد\n" +
                        "<BR>\n" +
                        "19- پاسخ به شبهات شهادت حضرت زهرا (س) فارسي 2جلد\n" +
                        "<BR>\n" +
                        "20- پاسخ به شبهات شهادت حضرت زهرا (س) عربي 1جلد\n" +
                        "<BR>\n" +
                        "21- پاسخ به شبهات عزاداري 1 جلد\n" +
                        "<BR>\n" +
                        "22- پاسخ به شبهات مهدويت 1 جلد\n" +
                        "<BR>\n" +
                        "23- رجال علامه حلي با تحقيق كامل و مفصل 1 جلد\n" +
                        "<BR>\n" +
                        "24- وفيات الروات (سال وفات روات شيعه) 1 جلد\n" +
                        "<BR>\n" +
                        "25- ضبط أسماء الروات والقابهم 1 جلد\n" +
                        "<BR>\n" +
                        "26- تحقيق الجامع في الرجال 10 جلد\n" +
                        "<BR>\n" +
                        "27- لعن وسب از منظر قرآن و سنت 1 جلد\n" +
                        "<BR>\n" +
                        "28- نگاهي گذرا به عدالت صحابه ازمنظر قرآن وسنت و گذر تاريخ 1 جلد\n" +
                        "<BR>\n" +
                        "29- حوارات مع قناة المستقلة حول شهادة فاطمة (س) 1 جلد.\n" +
                        "<BR>\n" +
                        "30- مناظره با مولوي مراد زهي از اساتيد حوزه علميه زاهدان پيرامون شهادت حضرت زهرا (س) 1 جلد.\n" +
                        "<BR>\n" +
                        "<font color=red> كتابهايي كه در دست تأليف و اقدام مي باشد</font>\n" +
                        "<BR>\n" +
                        "31- موسوعة الامام المهدي (ع) حدود 10 جلد\n" +
                        "<BR>\n" +
                        "32- قصة الحوار الهادئ (جلد دوم) 1 جلد\n" +
                        "<BR>\n" +
                        "33- ترجمه قصة الحوار الهادئ (داستان گفتگوي آرام) 2 جلد\n" +
                        "<BR>\n" +
                        "34- وهابيت از منظر عقل و شرع (جلد دوم) 1 جلد\n" +
                        "<BR>\n" +
                        "35- پاسخ به شبهات وهابيت در شبكه جهاني سلام 4 جلد\n" +
                        "<BR>\n" +
                        "36- پاسخ به شبهات وهابيت در حوزه علميه مشهد مقدس 1 جلد\n" +
                        "<BR>\n" +
                        "37- پاسخ به شبهات وهابيت در دانشگاه فردوسي مشهد مقدس 1 جلد\n" +
                        "<BR>\n" +
                        "38- دراسات في أسانيد الكتب الروائية (بررسي تصحيفات سندي كتب اربعه) 4 جلد\n" +
                        "<BR>\n" +
                        "39- امام علي عليه السلام وبيعت با خلفا 1 جلد\n" +
                        "<BR>\n" +
                        "40- پاسخ به شبهاتي پيرامون نهج البلاغه 1 جلد\n" +
                        "<BR>\n" +
                        "41- الافتراء علي الشيعة في تحريف القرآن 1 جلد\n" +
                        "<BR>\n" +
                        "42- ملاحظات عابرة علي تفسير الأثري 1 جلد\n" +
                        "<BR>\n" +
                        "43- سخني با واعظ زاده خراساني 1 جلد\n" +
                        "<BR>\n" +
                        "<font color=red> كتابخانه شخصي:</font>\n" +
                        "<BR>\n" +
                        "از آنجاي كه ايشان از اوائل ورود به حوزه علاقه خاصي به تهيه و جمع آوري كتاب داشتند و معتقد بودند كه كار پژوهشي و تحقيق بدون داشتن ابزار آن كه كتابخانه خصوصي است امكان پذير نمي باشد و لذا كتابخانه شخصي ايشان بيش 30000 جلد كتاب از كتب شيعه و ديگر فرق و مذاهب و اديان دارد.\n" +
                        "<BR>\n" +
                        "<font color=red> مسئولتيها :</font>\n" +
                        "<BR>\n" +
                        "1ـ مؤسس ومدير مؤسسه تحقيقاتي حضرت ولي عصر عليه السلام از سال 1371.\n" +
                        "<BR>\n" +
                        "2ـ مدير سايت valiasr-aj.com كه پاسخگوي جديد ترين شبهات وهابيت مي باشد و شامل تمام بحثها و سخنراني وتاليفات استاد قزويني بوده به طوري كه ماهيانه بيين 200.000 تا 300.000 نفر مراجعه كننده از داخل و خارج كشور دارد.\n" +
                        "<BR>\n" +
                        "3 - مسئول دروس عمومي حوزه علميه قم به مدت 14 سال.\n" +
                        "<BR>\n" +
                        "4 - عضو هيأت امناي بنياد بين المللي غدير.\n" +
                        "<BR>\n" +
                        "5 - عضو هيئت علمي دانشگاه بين الملي آل البيت (جامعة آل البيت العالمية) ورئيس بخش حديث.\n" +
                        "<BR>\n" +
                        "6 - عضو شوراي علمي مركز تخصصي مذاهب اسلامي\n" +
                        "<BR>\n" +
                        "7 - بيش از 50 مورد استاد راهنما و مشاور و استاد داور در رساله هاي علمي سطح كارشناسي ارشد و دكترا.\n" +
                        "<BR>\n" +
                        "\n" +
                        "<BR>\n" +
                        "\n" +
                        "<BR>";
            }
            else if(key==6)
            {
                str="سيّد على نقى بن محمّد بن مهدى حسينى ديباجى مشهور به فيض الاسلام اصفهانى به سال 1284ش در خمينى شهر اصفهان به دنيا آمد و در 24 / 2 / 1364ش مطابق با 1405ق در تهران چشم از جهان فرو بست و در بهشت زهرا (س) به خاك سپرده شد. وى صاحب اجازاتى از مراجع و بزرگان معاصر بود, ازجمله اجازه اجتهاد از سيّدابوالحسن اصفهانى (م1365ق) به تاريخ رجب 1352ق, اجازه اجتهاد از محمّدكاظم شيرازى (م1367ق) به تاريخ رجب 1352ق, اجازه اجتهاد از آقا ضياءالدّين عراقى (م1361ق), اجازه روايت از شيخ عبّاس قمى (م1359ق) به تاريخ شوّال 1356ق, اجازه روايت از شيخ على اكبر نهاوندى (م1369ق) به تاريخ صفر 1355ق.\n" +
                        "<BR>\n" +
                        "اين عالم فرزانه, صاحب آثارى گرانسنگ است: اشارات الرّضويّه; الافاضات الغرويّة فى اصول الفقهيّة كه در هنگام سكونت در نجف اشرف تأليف شده; بنادر البحار كه تلخيص بحارالانوار علاّمه محمّدباقر مجلسى است; پاسخ نامه ازگلى رساله اى است در اثبات رجعت كه در پاسخ نامه عيسى ازگلى شميرانى تهرانى, كه به زبان ساده و فارسى نگاشته آمده و به چاپ رسيده است; ترجمه خاتون دو سرا سيّدتنا المعصومة زينب الكبرى ارواح العالمين لتراب اقدمها الفداء يا احوالات حضرت زينب كبرى سلام اللّه عليها كه تاريخ زندگى حضرت زينب (س) به زبان فارسى است و چندين بار چاپ شده; ترجمه صحيفه علويّه; الثّقلان فى تفسير القرآن كه تفسير سوره مباركه فاتحة الكتاب است; چراغ راه; ره بر گم شدگان فى اثبات الرّجعة كه رديه اى است به زبان فارسى در برابر كتاب اسلام و رجعت اثر عبدالوهاب فريد تنكابنى (م1360ق) و دو بار به چاپ رسيده; صحيفه كامله سجّاديّه ترجمه تفسيرى يا ترجمه و شرح فارسيِ پنجاه و چهار دعاى امام زين العابدين (ع) كه داراى چاپ هاى مكرّر است; قرآن عظيم كه ترجمه فارسى و تفسير كوتاهى از آيات قرآن است و با كتابت طاهر خوش نويس و هم با كتابت عثمان طه به چاپ رسيده; ترجمه و شرح نهج البلاغه, خطبه ها و نامه ها و سخنان كوتاه اميرالمؤمنين عليه السّلام كه ترجمه تفسيرى نهج البلاغه است و با كتابت طاهر خوش نويس به صورت مكرّر چاپ شده است.\n" +
                        "<BR>\n" +
                        "با توجه به مقام علمي اين عالم والامقام ما بر آن شديم متن کامل ترجمه و شرح نهج البلاغه اين عالم گرانقدر  را براي شما در اين نرم افزار ارائه دهيم\n" +
                        "<BR>\n" +
                        "<BR>\n ";
            }
            else if(key==7)
            {
                str="<FONT COLOR=BLUE>مقدمه</FONT> <BR>\n" +
                        "شكر و سپاس بى منتها، خداى بزرگ را، كه ما را از شيعيان و پيروان اهل بيت قرار داد؛ و به صراط مستقيم ، ولايت مولاى متّقيان ، امير مؤ منان ، علىّ ابن ابى طالب عليه السلام هدايت نمود.\n" +
                        "و بهترين تحيّت و درود بر روان پاك پيامبر عالى قدر اسلام ؛ و بر اهل بيت عصمت و طهارت ، خصوصا اوّلين خليفه بر حقّش امام اميرالمؤ منين ، صلوات اللّه عليهم .\n" +
                        "بخشي كه در اختيار شما خواننده محترم قرار دارد، برگرفته شده است از زندگى سراسر آموزنده اوّلين ايمان آورنده به خدا و رسول ؛ و اوّلين مظلوم عالم ، آن انسان كامل و تمام عيار حقّ و حقيقت ، كه طبق روايت مخالف و موافق ، پيامبر اسلام صلّى اللّه عليه و آله در عظمت او فرمود:\n" +
                        "((عليّ مع الحقّ، و الحقّ مع عليّ))؛ هر كجا علىّ عليه السلام باشد، حقّ همان جا است و هر كجا حقّ باشد، علىّ آن جا است يعنى ؛ آن دو از يكديگر جدائى ناپذيرند .\n" +
                        "با توجه به توسعه فناوري اطلاعات و ارتباطات، برماست که همگام با عصر حاضر به دفاع از کيان ديني خويش قيام نماييم و با استفاده از اين ابزارهاي کارآمد به احياي فرهنگ ولايت و امامت برآييم.\n" +
                        "متناسبا ما نيز سعي کرديم که در اين امر فعاليتي داشته باشيم به نحويي که خصوصا از لحاظ محتويي مشمول تمامي مواردي که متناسب با محوريت امام علي (ع) در موضوعات ولايت، امامت ، غدير و نهج البلاغه است در اين نرم افزار بگنجانيم تا بتوان يک مرجع جامع نرم افزاري نسبت به اين مقوله ايجاد کرده باشيم.\n" +
                        "باشد كه اين ذرّه دلنشين و لذّت بخش مورد استفاده  عموم علاقه مندان ، خصوصا جوانان عزيز قرار گيرد.\n" +
                        "و ذخيره اى باشد:\n" +
                        "<FONT COLOR=white>((لِيَوْمٍ لايَنْفَعُ مالٌ وَ لابَنُون إِلاّ مَنْ اءَتَى اللّه بِقَلْبٍ سَليم لى وَ لِوالِدَىّ، وَ لِمَنْ لَهُ عَلَيَّ حَقّ))\n" +
                        "</FONT><BR>\n" +
                        "<center> <FONT COLOR=BLUE>ويژگي ها</FONT></center><BR>\n" +
                        "حكمت ها: نمايش كامل حكمت ها به صورت متن عربي، ترجمه و شرح و فايل صوتي مربوطه<BR>\n" +
                        "خطبه ها: نمايش كامل خطبه ها به صورت متن عربي، ترجمه و شرح و فايل صوتي مربوطه<BR>\n" +
                        "نامه ها: نمايش كامل نامه ها به صورت متن عربي، ترجمه و شرح و فايل صوتي مربوطه<BR>\n" +
                        "غرائب: نمايش كامل متون غرائب به صورت متن عربي، ترجمه و شرح و فايل صوتي مربوطه<BR>\n" +
                        "آيات: نمايش بيش از يکصد وبيست مورد از آيات بصورت تفكيك شده از ميان متن كامل نهج البلاغه با آدرس دهي آيه و سوره مربوطه<BR>\n" +
                        "فرهنگ موضوعي: نمايش بيش از 2200 موضوع دسته بندي شده از نهج البلاغه با ترجمه و ذکر ماخذ<BR>\n" +
                        "مقالات:  116 عنوان مقاله  پيرامون نهج البلاغه<BR>\n" +
                        "كتابشناسي: معرفى حدود ششصد كتاب درباره نهج البلاغه به زبان فارسى و عربى به همراه امکان دسترسي به معرفي مختصر و فهرست بيش از دويست عنوان از آنها<BR>\n" +
                        "\n" +
                        "<FONT COLOR=BLUE>امکانات</FONT><BR>\n" +
                        "1 - امکان مشاهده ترجمه و شرح به صورت تطبقي با امکان انتخاب از بين 27 شرح و 32 ترجمه<BR>\n" +
                        "2 ـ انواع جستجو در تمامى بخش ها و ليست ها <BR>\n" +
                        "3 ـ امکان انتخاب قلم اختصاصي براي بخش عربي (متن)، فارسي (ترجمه) و شرح به صورت جداگانه<BR>\n" +
                        "4 - امکان اشتراک مطالب <BR>\n" +
                        "5 - امکان تغيير سايز متن<BR>\n" +
                        "6 - امکان انتخاب محيط برنامه جهت مشاهده محتويات در طول روز و شب<BR>\n" +
                        "7 - امکان تغيير محل ذخيره اطلاعات بين حافظه داخلي و حافظه خارجي (sdcard) جهت سهولت استفاده کاربران<BR>\n" +
                        "8 - امکان استفاده از انيميشن در صفحه اول جهت زيبايي محيط کاربري<BR>\n" +
                        "\n" +
                        "<FONT COLOR=BLUE>تهيه کننده:</FONT> <BR>\n" +
                        "مؤسسه تحقيقاتي حضرت ولي عصر (عج)<BR>\n" +
                        "<FONT COLOR=red>رياست و اشراف مؤسسه:</FONT><BR>\n" +
                        "آيت الله دکتر سيد محمد حسيني قزويني<BR>\n" +
                        "<FONT COLOR=red>مدير پروژه:</FONT><BR>\n" +
                        "سيد محمد جواد حسيني<BR>\n" +
                        "<FONT COLOR=red>برنامه نويس:</FONT><BR>\n" +
                        "مهدي حسينعلي گل<BR>\n" +
                        "<FONT COLOR=red>گرافيست:</FONT><BR>\n" +
                        "محسن داوري<BR>\n" +
                        "<FONT COLOR=red>کارشناس ارزياب:</FONT><BR>\n" +
                        "حجت الاسلام محسن ميرمهدي<BR>\n" +
                        "<FONT COLOR=red>با تشکر از آقايان:</FONT><BR>\n" +
                        "شهرزاد رفیعی<BR>\n" +
                        "محمد قدوسي منش<BR>\n" +
                        "منصور ايزدجو<BR>\n" +
                        "مهدي جبرائيلي<BR>\n" +
                        "محمد علي قاسمي<BR>\n" +
                        "ميثم مؤمني<BR>\n" +
                        "<FONT COLOR=red>سايت مؤسسه:</FONT><BR>\n" +
                        "http://www.valiasr-aj.com                                  ";
            }
            SpannableString text = new SpannableString(Html.fromHtml(str));
            //text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), 0);
            //text.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            CharSequence finalText = TextUtils.concat(text,"\n\n\n");
            matn.setText(finalText, TextView.BufferType.SPANNABLE);
            matn.setMovementMethod(LinkMovementMethod.getInstance());
            eshterak_txt=Html.fromHtml(str)+"";
        }
        else if(type==96)
        {
            onvan_asl=onvan;
            title.setText(onvan);
            dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
            int _id=dbHelper.getQoranId(key,pos);
            int sharh_id=pref.getInt("qoran_tf_code",2);//1=almizan    2=nemune
            dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran_sharh");
            String str=dbHelper.getQoranSharh(_id,sharh_id);
            SpannableString text = new SpannableString(str+"\n\n");
            text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), 0);
            matn.setText(text, TextView.BufferType.SPANNABLE);
            //System.out.println("_id="+_id+"   sharh_id="+sharh_id+"   num="+num);
            //sure=key   aya=pos
            //System.out.println("key="+key+"     pos="+pos+"   _id="+_id);
            //eshterak_txt=eshterak_txt+"\n"+str;
            eshterak_txt=text+"";
        }
    }
    public void qalam_dialog()
    {
        dialog = new Dialog(matn_template.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_font);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title= (TextView) dialog.findViewById(R.id.alert_font_title_txt);
        title.setTypeface(mf.getYekan());
        title.setText("فونت را انتخاب کنید :");
        RecyclerView mRecyclerView= (RecyclerView) dialog.findViewById(R.id.alert_font_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(getApplicationContext(), Configuration.ORIENTATION_PORTRAIT,false));

        Vector values=new Vector();
        if(type==44)
        {
            values.add("قرآن");
            values.add("نازنین");
            values.add("سلطان");
            values.add("زر");
        }
        else if(type==4||type==7||type==5||type==3||type==6||type==70||type==96)
        {
            values.add("یکان");
            values.add("بدر");
            values.add("نازنین");
            values.add("سلطان");
            values.add("زر");
        }
        RecyclerView.Adapter mAdapter=new font_adapter(matn_template.this,values,type);
        mRecyclerView.setAdapter(mAdapter);
        dialog.show();
    }
    public void setfont()
    {
        if(type==4)//hekayat
        {
            String item=pref.getString("hekayat_font","یکان");
            matn.setTypeface(mf.getFace(item));
        }
        else if(type==7)//maqalat
        {
            String item=pref.getString("maqalat_font","یکان");
            matn.setTypeface(mf.getFace(item));
        }
        else if(type==5)//tajvid
        {
            String item=pref.getString("tajvid_font","یکان");
            matn.setTypeface(mf.getFace(item));
        }
        else if(type==3)//porseman
        {
            String item=pref.getString("porseman_font","یکان");
            matn.setTypeface(mf.getFace(item));
        }
        else if(type==6)//moama
        {
            String item=pref.getString("moama_font","یکان");
            matn.setTypeface(mf.getFace(item));
        }
        else if(type==96)
        {
            String item=pref.getString("sharh_qoran_font","سلطان");
            matn.setTypeface(mf.getFace(item));
        }
        else if(type==70)
        {
            String item=pref.getString("about_font","یکان");
            matn.setTypeface(mf.getFace(item));
        }
    }
    public void drawSize()
    {
        int mysize=0;

        if(type==4)
        {
            mysize = pref.getInt("hekayat_txt_size", 0);
            matn.setTextSize(curElementSize+mysize);
        }
        else if(type==7)
        {
            mysize = pref.getInt("maqalat_txt_size", 0);
            matn.setTextSize(curElementSize+mysize);
        }
        else if(type==5)
        {
            mysize = pref.getInt("tajvid_txt_size", 0);
            matn.setTextSize(curElementSize+mysize);
        }
        else if(type==3)
        {
            mysize = pref.getInt("porseman_txt_size", 0);
            matn.setTextSize(curElementSize+mysize);
        }
        else if(type==6)
        {
            mysize = pref.getInt("moama_txt_size", 0);
            matn.setTextSize(curElementSize+mysize);
        }
        else if(type==2)
        {
            mysize = pref.getInt("ayat_txt_size", 0);
            matn.setTextSize(curElementSize+mysize);
        }
        else if(type==96)
        {
            mysize = pref.getInt("sharh_qoran_txt_size", 0);
            matn.setTextSize(curElementSize+mysize);
        }
        else if(type==70)
        {
            mysize = pref.getInt("about_txt_size", 0);
            matn.setTextSize(curElementSize+mysize);
        }
    }
    public void kuchak_click()
    {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if (type==4)
                        {
                            int mysize = pref.getInt("hekayat_txt_size", 0);
                            mysize -= 2;
                            editor.putInt("hekayat_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==7)
                        {
                            int mysize = pref.getInt("maqalat_txt_size", 0);
                            mysize -= 2;
                            editor.putInt("maqalat_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==5)
                        {
                            int mysize = pref.getInt("tajvid_txt_size", 0);
                            mysize -= 2;
                            editor.putInt("tajvid_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==3)
                        {
                            int mysize = pref.getInt("porseman_txt_size", 0);
                            mysize -= 2;
                            editor.putInt("porseman_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==6)
                        {
                            int mysize = pref.getInt("moama_txt_size", 0);
                            mysize -= 2;
                            editor.putInt("moama_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==2)
                        {
                            int mysize = pref.getInt("ayat_txt_size", 0);
                            mysize -= 2;
                            editor.putInt("ayat_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==96)
                        {
                            int mysize = pref.getInt("sharh_qoran_txt_size", 0);
                            mysize -= 2;
                            editor.putInt("sharh_qoran_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==70)
                        {
                            int mysize = pref.getInt("about_txt_size", 0);
                            mysize -= 2;
                            editor.putInt("about_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                    }
                });
            }
        }.start();
    }
    public void bozorg_click()
    {
        new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                        if (type==4)
                        {
                            int mysize = pref.getInt("hekayat_txt_size", 0);
                            mysize += 2;
                            editor.putInt("hekayat_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==7)
                        {
                            int mysize = pref.getInt("maqalat_txt_size", 0);
                            mysize += 2;
                            editor.putInt("maqalat_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==5)
                        {
                            int mysize = pref.getInt("tajvid_txt_size", 0);
                            mysize += 2;
                            editor.putInt("tajvid_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==3)
                        {
                            int mysize = pref.getInt("porseman_txt_size", 0);
                            mysize += 2;
                            editor.putInt("porseman_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==6)
                        {
                            int mysize = pref.getInt("moama_txt_size", 0);
                            mysize += 2;
                            editor.putInt("moama_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==2)
                        {
                            int mysize = pref.getInt("ayat_txt_size", 0);
                            mysize += 2;
                            editor.putInt("ayat_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==96)
                        {
                            int mysize = pref.getInt("sharh_qoran_txt_size", 0);
                            mysize += 2;
                            editor.putInt("sharh_qoran_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                        else if (type==70)
                        {
                            int mysize = pref.getInt("about_txt_size", 0);
                            mysize += 2;
                            editor.putInt("about_txt_size", mysize);
                            editor.commit();
                            drawSize();
                        }
                    }
                });
            }
        }.start();
    }
    private void eshterak_click()
    {
        if(type==4||type==2||type==7||type==5||type==6||type==70)
        {
            //eshterak_txt=dsc+"\n";
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/html");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(eshterak_txt));
            startActivity(Intent.createChooser(sharingIntent,"اشتراک گذاری"));

        }
        else if(type==96||type==3)
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, eshterak_txt+"\n");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "اشتراک گذاری"));
        }

    }
    private void setFav()
    {
        //db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,DSC TEXT,KEY INTEGER,POS INTEGER,SOUND_NAME TEXT,DSCTARJOME TEXT,PAGE INTEGER); ");//1
        //db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//1
        try
        {
            //Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan+"'"+" AND TYPE='"+type+"'"+" AND DSC='"+dsc+"'"+" AND KEY='"+key+"'"+" AND POS='"+pos+"'"+" AND SOUND_NAME='"+sound_name+"'"+" AND DSCTARJOME='"+dsctarjome+"'"+" AND PAGE='"+1+"'", null);
            Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan_asl+"'"+" AND TYPE='"+type+"'"+" AND KEY='"+key+"'"+" AND POS='"+pos+"'"+" AND PAGE='"+1+"'", null);
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
            System.out.println("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");
            System.out.println("eeeeeeeeeeeerrrrrrrrr1"+e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage()+"  aa",Toast.LENGTH_SHORT).show();
        }

    }
    private void fav_click()
    {
        try
        {
            Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan_asl+"'"+" AND TYPE='"+type+"'"+" AND KEY='"+key+"'"+" AND POS='"+pos+"'"+" AND PAGE='"+1+"'", null);
            if(c!= null)
            {
                if(c.getCount()==0)//item dar db vojud nadarad pas insert mikonim
                {

                    db1.execSQL("INSERT INTO tb_fav (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan_asl+"','" + key +"','"+pos +"','"+1 +"');");
                    Toast.makeText(matn_template.this," "+"به لیست علاقه مندیها اضافه شد.", Toast.LENGTH_SHORT).show();

                }
                if(c.getCount()==1)//item dar db vojud darad pas delete mikonim
                {
                    //db1.execSQL("DELETE  FROM tb_fav WHERE ONVAN='" +onvan+"'"+" AND TYPE='"+type+"'"+" AND DSC='"+dsc+"'"+" AND KEY='"+key+" AND POS='"+pos+" AND SOUND_NAME='"+sound_name+" AND DSCTARJOME='"+dsctarjome+"'"+" AND PAGE='"+1+"'");
                    db1.execSQL("DELETE  FROM tb_fav WHERE ONVAN='" +onvan_asl+"'"+" AND TYPE='"+type+"'"+" AND KEY='"+key+"'"+" AND POS='"+pos+"'"+" AND PAGE='"+1+"'");
                    Toast.makeText(matn_template.this," "+"از لیست علاقه مندیها حذف شد.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        catch(Exception e)
        {
            System.out.println("eeeeeeeeeeeerrrrrrrrr2"+e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage()+"  bb",Toast.LENGTH_SHORT).show();
        }
        setFav();

    }
    public static float pixelsToSp(Context context, float px)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_lastseen(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//1
        try
        {
            Cursor c = db1.rawQuery("SELECT * FROM tb_lastseen ", null);
            System.out.println("c.getCount()1="+c.getCount());
            if(c!= null)
            {
                if(c.getCount()==0)//item dar db vojud nadarad pas insert mikonim
                {
                    db1.execSQL("INSERT INTO tb_lastseen (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan_asl+"','"+key +"','"+pos +"','"+1 +"');");
                }
                if(c.getCount()>0)//item dar db vojud darad pas delete mikonim
                {
                    System.out.println("c.getCount()4="+c.getCount());
                    db1.execSQL("DELETE  FROM tb_lastseen ");
                    db1.execSQL("INSERT INTO tb_lastseen (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan_asl+"','"+key +"','"+pos  +"','"+1 +"');");
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
}
