package com.netavin.quran.classes;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Created by mehdi on 19/03/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private final static String TAG = "DatabaseHelper";
    private final Context myContext;
    private static  String DATABASE_NAME = "nahj_data.db";
    private static  String DATABASE_NAME_ZIP = "nahj_data.zip";
    private static  int DATABASE_VERSION = 2;
    private String pathToSaveDBFile;
    private String pathToSaveDBFileZip;
    String filePath = "";
    String mytable="";
    ProgressDialog pd_copydata;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public DatabaseHelper(Context context, String filePath,String cur_db)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        pref = PreferenceManager.getDefaultSharedPreferences(myContext);
        editor = pref.edit();
        mytable=cur_db;
        DATABASE_NAME=cur_db+".db";
        DATABASE_NAME_ZIP=cur_db+".zip";
        pathToSaveDBFile = new StringBuffer(filePath).append("/").append(DATABASE_NAME).toString();
        pathToSaveDBFileZip = new StringBuffer(filePath).append("/").append(DATABASE_NAME_ZIP).toString();

        this.filePath = filePath;

        pd_copydata = new ProgressDialog(context);
        Typeface tfs = Typeface.createFromAsset(context.getAssets(), "font/Yekan.ttf");
        //TypefaceSpan ttt=new TypefaceSpan(context,tfs);
        SpannableString text = new SpannableString("انتقال پایگاه داده");
        text.setSpan(new ForegroundColorSpan(Color.parseColor("#6d260e")), 0, text.length(), 0);
        text.setSpan(new CustomTypefaceSpan("", tfs), 0, text.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        pd_copydata.setMessage(text);
        pd_copydata.setIndeterminate(true);
        pd_copydata.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd_copydata.setCancelable(false);
        pd_copydata.setProgressNumberFormat(null);

        try {
            prepareDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "prepareDatabase_err=" + e.getMessage());
        }
    }
    public void prepareDatabase() throws IOException {
        boolean dbExist = checkDataBase();
        System.out.println("dbExist="+dbExist);
        if (dbExist)
        {
            /*int currentDBVersion = getVersionId();
            if (DATABASE_VERSION > currentDBVersion) {
                pd_copydata.show();
                Log.d(TAG, "Database version is higher than old.");
                deleteDb();
                new Thread() {
                    public void run() {
                        try {
                            copyDataBase();

                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        }
                        messageHandler.sendEmptyMessage(5);
                    }
                }.start();

            }*/
        }
        else
        {
            pd_copydata.show();
            new Thread() {
                public void run() {
                    try {
                        copyDataBase();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                    messageHandler.sendEmptyMessage(5);
                }
            }.start();

        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            File file = new File(pathToSaveDBFile);
            checkDB = file.exists();
        } catch (SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;
    }

    private void copyDataBase() throws IOException {
        messageHandler.sendEmptyMessage(1);
        Log.d(TAG, "copy database shoru");
        OutputStream os = new FileOutputStream(pathToSaveDBFileZip);
        InputStream is = myContext.getAssets().open("databases/" + DATABASE_NAME_ZIP);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
        Log.d(TAG, "copy database payan");
        messageHandler.sendEmptyMessage(2);
        extract();
    }
    private int getVersionId() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        int v=0;
        try
        {
            String query = "SELECT version_id FROM dbVersion";
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            v = cursor.getInt(0);
        }
        catch (Exception e)
        {
            System.out.println("eemmmee="+e.getMessage());
            if(e.getMessage().contains("no such table"))
            {
                v=1000;
            }
        }


        db.close();
        return v;
    }
    public void deleteDb() {
        File file = new File(pathToSaveDBFile);
        if (file.exists()) {
            file.delete();
            Log.d(TAG, "Database deleted.");
        }
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //imageView.setImageBitmap(bitmap);


            if (msg.what == 1) {
                pd_copydata.setIndeterminate(false);
                pd_copydata.setMax(100);
                pd_copydata.setProgress(25);
            } else if (msg.what == 2) {
                pd_copydata.setIndeterminate(false);
                pd_copydata.setMax(100);
                pd_copydata.setProgress(50);
            } else if (msg.what == 3) {
                pd_copydata.setIndeterminate(false);
                pd_copydata.setMax(100);
                pd_copydata.setProgress(75);
            } else if (msg.what == 4) {
                pd_copydata.setIndeterminate(false);
                pd_copydata.setMax(100);
                pd_copydata.setProgress(100);
                pd_copydata.dismiss();
            } else if (msg.what == 5) {
                pd_copydata.dismiss();
            }
        }
    };
    public void extract() {

        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(pathToSaveDBFileZip);
        } catch (ZipException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            if (zipFile.isEncrypted()) {
                zipFile.setPassword("2839");
            }
        } catch (ZipException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            messageHandler.sendEmptyMessage(3);
            zipFile.extractAll(filePath);


            File f = new File(pathToSaveDBFileZip);
            if (f.exists()) {
                f.delete();
            }
            messageHandler.sendEmptyMessage(4);

        } catch (ZipException e) {
            Log.d(TAG, e.getMessage());
        }
    }

    public Vector getHadiseList(int parent) {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM list WHERE parent='" + parent + "'";
        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext()) {
            int key = cursor.getInt(cursor.getColumnIndex("key"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String listkey = cursor.getString(cursor.getColumnIndex("listkey"));
            int tedad = cursor.getInt(cursor.getColumnIndex("tedad"));
            //////////////////////////name = CodeDecode(name,1);
            name = name.replace("ي", "ی");
            name=name.replace("ك","ک");
            name=name.replace("گهر","گوهر");
            Vector temp = new Vector();
            temp.add(key);
            temp.add(index + "- " + name);
            temp.add(listkey);
            temp.add(tedad);

            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getHadiseList2(int listkey) {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM listhadith WHERE listkey='" + listkey + "'";
        Cursor cursor = db.rawQuery(query, null);
        System.out.println("cursor.getCount()="+cursor.getCount());
        int index = 1;
        while (cursor.moveToNext()) {
            int key = cursor.getInt(cursor.getColumnIndex("key"));
            String masom = cursor.getString(cursor.getColumnIndex("masom"));
            String arabic = cursor.getString(cursor.getColumnIndex("arabic"));
            String farsi = cursor.getString(cursor.getColumnIndex("farsi"));
            String manbah = cursor.getString(cursor.getColumnIndex("manbah"));

            //////////////////////////masom = CodeDecode(masom,1);
            //////////////////////////farsi = CodeDecode(farsi,1);
            //////////////////////////manbah = CodeDecode(manbah,1);
            //////////////////////////arabic = CodeDecode(arabic,1);

            masom = masom.replace("ي", "ی");
            masom=masom.replace("ك","ک");
            farsi = farsi.replace("ي", "ی");
            farsi=farsi.replace("ك","ک");
            manbah = manbah.replace("ي", "ی");
            manbah=manbah.replace("ك","ک");

            Vector temp = new Vector();
            temp.add(key);
            temp.add(masom);
            temp.add(arabic);
            temp.add(farsi);
            temp.add(manbah);

            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }

    public Vector getQoranList() {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "SELECT * FROM sura";
        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));

            Vector temp = new Vector();
            temp.add(id);
            temp.add(index + "- " + name);
            temp.add(name);

            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getQoranOtherList(int num) {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        if(num==2)
        {
            query = "SELECT * FROM tajvid WHERE parentid=0";
        }
        else if(num==3)
        {
            query = "SELECT * FROM qesas WHERE parentid=0";
        }
        else if(num==4)
        {
            query = "SELECT * FROM moama WHERE parentid=0";
        }
        else if(num==5)
        {
            query = "SELECT * FROM dastan";
        }
        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            if(num==2||num==3||num==4)
            {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String parentid = cursor.getString(cursor.getColumnIndex("parentid"));
                String title = cursor.getString(cursor.getColumnIndex("title"))+"";
                //String content = cursor.getString(cursor.getColumnIndex("content"))+"";

                if(title.equals("null"))
                    title=" ";
                //if(content.equals("null"))
                //content=" ";
                //////////////////////////title = CodeDecode(title,1);
                //content = CodeDecode(content,1);
                title = title.replace("ي", "ی");
                title=title.replace("ك","ک");
                //content = content.replace("ي", "ی");

                Vector temp = new Vector();
                temp.add(_id);
                temp.add(index + "- " + title);
                temp.add(title);

                vec.add(temp);
                index++;
            }
            else if(num==5)
            {
                int _id = cursor.getInt(cursor.getColumnIndex("_id"));
                String parentid = cursor.getString(cursor.getColumnIndex("parentid"));
                String title = cursor.getString(cursor.getColumnIndex("title"))+"";
                String content = cursor.getString(cursor.getColumnIndex("content"))+"";

                if(title.equals("null"))
                    title=" ";
                if(content.equals("null"))
                    content=" ";
                //////////////////////////title = CodeDecode(title,1);
                //////////////////////////content = CodeDecode(content,1);
                title = title.replace("ي", "ی");
                title=title.replace("ك","ک");
                content = content.replace("ي", "ی");
                content=content.replace("ك","ک");

                //Color.LTGRAY
                content=content.replace("\r\n","<br>");
                content=content.replace("\n","<br>");
                content=content.replace("blue","navy");
                content=content.replace("green","blue");

                Vector temp = new Vector();
                temp.add(_id);
                temp.add(_id + "- " + title);
                temp.add(title);
                temp.add(content);

                vec.add(temp);
            }
        }
        db.close();
        return vec;
    }
    public Vector getQoranOtherList2(int num,String table) {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM "+table+" WHERE parentid="+num;
        Cursor cursor = db.rawQuery(query, null);

        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"))+"";
            String content = cursor.getString(cursor.getColumnIndex("content"))+"";

            if(title.equals("null"))
                title=" ";
            if(content.equals("null"))
                content=" ";
            //////////////////////////title = CodeDecode(title,1);
            //////////////////////////content = CodeDecode(content,1);
            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");
            content = content.replace("ي", "ی");
            content=content.replace("ك","ک");

            content=content.replace("\r\n","<br>");
            content=content.replace("\n","<br>");
            content=content.replace("blue","navy");
            content=content.replace("green","blue");

            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);
            temp.add(content);

            vec.add(temp);
            index++;
        }

        db.close();
        return vec;
    }

    public Vector getQoranList2(int sura) {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "SELECT * FROM arabic_text WHERE sura='" + sura + "'";
        Cursor cursor = db.rawQuery(query, null);
        String qr_tr=pref.getString("qoran_tr","ناصر مکارم شیرازی");
        while (cursor.moveToNext()) {
            int index = cursor.getInt(cursor.getColumnIndex("index"));
            String text = cursor.getString(cursor.getColumnIndex("text"));
            //System.out.println("index="+index);

            Vector temp = new Vector();
            String tr=getQoranTarjome(index,qr_tr);
            temp.add(text);
            temp.add(tr);

            vec.add(temp);
        }
        db.close();
        return vec;
    }
    public Vector getQoranInfo(int id) {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "SELECT * FROM sura WHERE id='" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            int count_aya = cursor.getInt(cursor.getColumnIndex("count_aya"));
            int hizb = cursor.getInt(cursor.getColumnIndex("hizb"));
            int juz = cursor.getInt(cursor.getColumnIndex("juz"));
            String location = cursor.getString(cursor.getColumnIndex("location"));

            vec.add(name);
            vec.add(count_aya+"");
            vec.add(hizb+"");
            vec.add(juz+"");
            vec.add(location);
        }
        db.close();
        return vec;
    }
    public Vector getAbout(int key) {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "SELECT * FROM about WHERE key='" + key + "'";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String text = cursor.getString(cursor.getColumnIndex("text"));
            String dsc = cursor.getString(cursor.getColumnIndex("dsc"));
            //System.out.println("index="+index);

            Vector temp = new Vector();
            temp.add(text);
            temp.add(dsc);

            vec.add(temp);
        }
        db.close();
        return vec;
    }
    public Vector getQoranList3(int sura) {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "SELECT * FROM arabic_text WHERE sura='" + sura + "'";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            vec.add(id);
        }
        db.close();
        return vec;
    }
    public String getQoranTarjome(int id,String table)
    {
        String tr="";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM "+table +" WHERE id='" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {

            tr = cursor.getString(cursor.getColumnIndex("tr"));
            //////////////////////////tr = CodeDecode(tr,1);
        }

        db.close();
        return  tr;
    }
    public String getQoranFa(int index) {
        String text="";
        String qr_tr=pref.getString("qoran_tr","ناصر مکارم شیرازی");
        String table="";
        if(qr_tr.equals("ناصر مکارم شیرازی"))
        {
            table="fa_makar";
        }
        else if(qr_tr.equals("حسین انصاریان"))
        {
            table="fa_ansar";
        }
        else if(qr_tr.equals("عبدالمحمد آیتی"))
        {
            table="fa_ayati";
        }
        else if(qr_tr.equals("ابوالفضل بهرامپور"))
        {
            table="fa_bahra";
        }
        else if(qr_tr.equals("محمد مهدی فولادوند"))
        {
            table="fa_foola";
        }
        else if(qr_tr.equals("مصطفی خرمدل"))
        {
            table="fa_kho_1";
        }
        else if(qr_tr.equals("بهاءالدین خرمشاهی"))
        {
            table="fa_khorr";
        }
        else if(qr_tr.equals("محمدکاظم معزی"))
        {
            table="fa_moezz";
        }
        else if(qr_tr.equals("سیدجلال الدین مجتبوی"))
        {
            table="fa_mojta";
        }
        else if(qr_tr.equals("محمد صادقی تهرانی"))
        {
            table="fa_sadeq";
        }
        text=getQoranTarjome(index,table);

        return text;
    }
    public String getQoranAr(int index) {
        String text="";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "SELECT text FROM arabic_text WHERE id='" + index + "'";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            text = cursor.getString(cursor.getColumnIndex("text"));
        }
        db.close();
        return text;
    }
    public String getQoranSound(int id)
    {
        String url="";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM my_audio WHERE id='" + id + "'";
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {

            url = cursor.getString(cursor.getColumnIndex("url"));
        }

        db.close();
        return  url;
    }
    public Cursor getQoranCurserTest(int sura)//1..114
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String qr_tr=pref.getString("qoran_tr","ناصر مکارم شیرازی");
        String table="";
        if(qr_tr.equals("ناصر مکارم شیرازی"))
        {
            table="fa_makar";
        }
        else if(qr_tr.equals("حسین انصاریان"))
        {
            table="fa_ansar";
        }
        else if(qr_tr.equals("عبدالمحمد آیتی"))
        {
            table="fa_ayati";
        }
        else if(qr_tr.equals("ابوالفضل بهرامپور"))
        {
            table="fa_bahra";
        }
        else if(qr_tr.equals("محمد مهدی فولادوند"))
        {
            table="fa_foola";
        }
        else if(qr_tr.equals("مصطفی خرمدل"))
        {
            table="fa_kho_1";
        }
        else if(qr_tr.equals("بهاءالدین خرمشاهی"))
        {
            table="fa_khorr";
        }
        else if(qr_tr.equals("محمدکاظم معزی"))
        {
            table="fa_moezz";
        }
        else if(qr_tr.equals("سیدجلال الدین مجتبوی"))
        {
            table="fa_mojta";
        }
        else if(qr_tr.equals("محمد صادقی تهرانی"))
        {
            table="fa_sadeq";
        }

        //String query = "SELECT _id,text,search_text FROM arabic_text WHERE sura='" + sura + "'"+" OR  _id=1";

        String query="";
        if(sura==1)//hamd
        {
            query = "SELECT _id,text,aya,sura,id,tr,audio FROM arabic_text,"+table+" WHERE (arabic_text.sura='" + sura + "'"+") AND arabic_text._id="+table+".id";
        }
        else if(sura==9)//tobe
        {
            query = "SELECT _id,text,aya,sura,id,tr,audio FROM arabic_text,"+table+" WHERE (arabic_text.sura='" + sura + "'"+") AND arabic_text._id="+table+".id";
        }
        else
        {
            query = "SELECT _id,text,aya,sura,id,tr,audio FROM arabic_text,"+table+" WHERE (arabic_text.sura='" + sura + "'"+" OR  arabic_text._id=1) AND arabic_text._id="+table+".id";
        }
        //String query = "SELECT _id,text,aya,sura,id,tr,audio FROM arabic_text,"+table+" WHERE (arabic_text.sura='" + sura + "'"+" OR  arabic_text._id=1) AND arabic_text._id="+table+".id";
        Cursor cursor = db.rawQuery(query, null);


        //db.close();
        return cursor;
    }
    public Cursor getQoranJozCurserTest(int juz)//1..30
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String qr_tr=pref.getString("qoran_tr","ناصر مکارم شیرازی");
        String table="";
        if(qr_tr.equals("ناصر مکارم شیرازی"))
        {
            table="fa_makar";
        }
        else if(qr_tr.equals("حسین انصاریان"))
        {
            table="fa_ansar";
        }
        else if(qr_tr.equals("عبدالمحمد آیتی"))
        {
            table="fa_ayati";
        }
        else if(qr_tr.equals("ابوالفضل بهرامپور"))
        {
            table="fa_bahra";
        }
        else if(qr_tr.equals("محمد مهدی فولادوند"))
        {
            table="fa_foola";
        }
        else if(qr_tr.equals("مصطفی خرمدل"))
        {
            table="fa_kho_1";
        }
        else if(qr_tr.equals("بهاءالدین خرمشاهی"))
        {
            table="fa_khorr";
        }
        else if(qr_tr.equals("محمدکاظم معزی"))
        {
            table="fa_moezz";
        }
        else if(qr_tr.equals("سیدجلال الدین مجتبوی"))
        {
            table="fa_mojta";
        }
        else if(qr_tr.equals("محمد صادقی تهرانی"))
        {
            table="fa_sadeq";
        }

        //String query = "SELECT _id,text,search_text FROM arabic_text WHERE sura='" + sura + "'"+" OR  _id=1";

        String query="";
        query = "SELECT _id,text,aya,sura,id,tr,audio FROM arabic_joz,"+table+" WHERE (arabic_joz.juz='" + juz + "'"+") AND arabic_joz._id="+table+".id";

        //String query = "SELECT _id,text,aya,sura,id,tr,audio FROM arabic_text,"+table+" WHERE (arabic_text.sura='" + sura + "'"+" OR  arabic_text._id=1) AND arabic_text._id="+table+".id";
        Cursor cursor = db.rawQuery(query, null);


        //db.close();
        return cursor;
    }
    public Cursor getQoranHezbCurserTest(int hizb)//1..120
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String qr_tr=pref.getString("qoran_tr","ناصر مکارم شیرازی");
        String table="";
        if(qr_tr.equals("ناصر مکارم شیرازی"))
        {
            table="fa_makar";
        }
        else if(qr_tr.equals("حسین انصاریان"))
        {
            table="fa_ansar";
        }
        else if(qr_tr.equals("عبدالمحمد آیتی"))
        {
            table="fa_ayati";
        }
        else if(qr_tr.equals("ابوالفضل بهرامپور"))
        {
            table="fa_bahra";
        }
        else if(qr_tr.equals("محمد مهدی فولادوند"))
        {
            table="fa_foola";
        }
        else if(qr_tr.equals("مصطفی خرمدل"))
        {
            table="fa_kho_1";
        }
        else if(qr_tr.equals("بهاءالدین خرمشاهی"))
        {
            table="fa_khorr";
        }
        else if(qr_tr.equals("محمدکاظم معزی"))
        {
            table="fa_moezz";
        }
        else if(qr_tr.equals("سیدجلال الدین مجتبوی"))
        {
            table="fa_mojta";
        }
        else if(qr_tr.equals("محمد صادقی تهرانی"))
        {
            table="fa_sadeq";
        }

        //String query = "SELECT _id,text,search_text FROM arabic_text WHERE sura='" + sura + "'"+" OR  _id=1";

        String query="";
        query = "SELECT _id,text,aya,sura,id,tr,audio FROM arabic_joz,"+table+" WHERE (arabic_joz.hizb='" + hizb + "'"+") AND arabic_joz._id="+table+".id";

        //String query = "SELECT _id,text,aya,sura,id,tr,audio FROM arabic_text,"+table+" WHERE (arabic_text.sura='" + sura + "'"+" OR  arabic_text._id=1) AND arabic_text._id="+table+".id";
        Cursor cursor = db.rawQuery(query, null);


        //db.close();
        return cursor;
    }
    public int getQoranSureCount(int sura)
    {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT count_aya FROM sura WHERE id='" + sura + "'";
        Cursor cursor = db.rawQuery(query, null);
        int count_aya=0;
        while (cursor.moveToNext())
        {
            count_aya = cursor.getInt(cursor.getColumnIndex("count_aya"));

        }
        db.close();
        return count_aya;
    }
    public String getQoranName(int sura)
    {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT arabic_name FROM sura WHERE id='" + sura + "'";
        Cursor cursor = db.rawQuery(query, null);
        String name="";
        while (cursor.moveToNext())
        {
            name = cursor.getString(cursor.getColumnIndex("arabic_name"));

        }
        db.close();
        return name;
    }
    public int getQoranId(int sura,int aya)
    {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT _id FROM arabic_text WHERE sura=" + sura + " And aya="+aya;
        Cursor cursor = db.rawQuery(query, null);
        int _id=0;
        while (cursor.moveToNext())
        {
            _id = cursor.getInt(cursor.getColumnIndex("_id"));

        }
        db.close();
        return _id;
    }
    public String getQoranSharh(int _id,int sharh_code)
    {
        String matn="";
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM explan_1 WHERE id_name=" + sharh_code + " And id_verse LIKE '%["+_id+"]%'";
        //String query = "SELECT * FROM explan_1 WHERE id_name=" + sharh_code + " And CONTAINS(id_verse,["+_id+"])";
        //String query = "SELECT * FROM explan_1 WHERE id_name=" + sharh_code + " And id_verse CONTAINS  %["+_id+"]%";
        //String query = "SELECT * FROM explan_1 WHERE id_name=" + sharh_code + " And id_verse in  (["+_id+"])";
        Cursor cursor = db.rawQuery(query, null);

        int num=cursor.getCount();
        System.out.println("cursor.getCount()="+cursor.getCount());
        System.out.println("_id_id_id_id_id_id_id="+_id);
        if(num==0)
        {
            matn="شرحی برای این آیه وجود ندارد.";
            return matn;
        }

        while (cursor.moveToNext())
        {
            matn += cursor.getString(cursor.getColumnIndex("value"));

        }
        //////////////////////////matn = CodeDecode(matn,1);
        db.close();
        return matn;
    }



    public Vector getData() {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT * FROM " + mytable;
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext())
        {
            try
            {
                int key = cursor.getInt(cursor.getColumnIndex("key"));
                //System.out.println("key="+key);
                int parent = cursor.getInt(cursor.getColumnIndex("parent"));
                String text = cursor.getString(cursor.getColumnIndex("text"))+"";
                if(text.equals("null"))
                    text=" ";

                String dsc = cursor.getString(cursor.getColumnIndex("dsc"))+"";
                if(dsc.equals("null"))
                    dsc=" ";

                String link = cursor.getString(cursor.getColumnIndex("link"))+"";
                if(link.equals("null"))
                    link=" ";

                String showtype = cursor.getString(cursor.getColumnIndex("showtype"));
                String isdownload = cursor.getString(cursor.getColumnIndex("isdownload"));
                int countrec = cursor.getInt(cursor.getColumnIndex("countrec"));
                int sizefile = cursor.getInt(cursor.getColumnIndex("sizefile"));

                Vector temp = new Vector();
                temp.add(key);
                temp.add(parent);
                temp.add(text);
                temp.add(dsc);
                temp.add(link);
                temp.add(showtype);
                temp.add(isdownload);
                temp.add(countrec);
                temp.add(sizefile);
                vec.add(temp);
            }
            catch (Exception e)
            {
                System.out.println("eeeeeeeeeee="+e.getMessage());
            }

        }

        db.close();

        return vec;
    }
    public void updateList(Vector vec,String table_name) {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);

        Vector temp = new Vector();
        try
        {
            for (int i = 0; i < vec.size(); i++) {
                temp = new Vector();
                temp = (Vector) vec.elementAt(i);
                ContentValues values = new ContentValues();
                values.put("key", Integer.parseInt(temp.elementAt(0) + ""));
                values.put("parent", Integer.parseInt(temp.elementAt(1) + ""));
                values.put("text", temp.elementAt(2) + "");
                values.put("dsc", temp.elementAt(3) + "");
                values.put("link", temp.elementAt(4) + "");
                values.put("showtype", "1");
                values.put("isdownload", "1");
                values.put("countrec", Integer.parseInt(temp.elementAt(7) + ""));
                values.put("sizefile", Integer.parseInt(temp.elementAt(8) + ""));
                long chk = db.insert(table_name, null, values);
                if (chk != 0) {
                    System.out.println("key="+temp.elementAt(0)+"    Record added successfully");
                } else {
                    System.out.println("key="+temp.elementAt(0)+"   Record added failed...! ");
                }
            }
            setQuestionUpdate(table_name);
        }
        catch (Exception e)
        {
            System.out.println("e5="+e.getMessage());
        }
        db.close();

    }
    public void setQuestionUpdate(String table_name)
    {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READWRITE);
        String query = "UPDATE question SET isdownload='" + 1 + "' WHERE dsc='" + table_name + "'";
        try
        {
            //String strSQL = "UPDATE question SET isdownload = 1 WHERE dsc = " + table_name;
            db.execSQL(query);
            System.out.println("site main update shod");
        }
        catch (Exception e)
        {
            System.out.println("ee88="+e.getMessage());
        }

        db.close();
    }
    //mode=0//reshte ro code mikonad
    //mode=1//decode
    /*public static String CodeDecode(String str, Integer mode) {
        String[] code1 = { "P#", "D#", "S#", "@#", "Q@", "F#", "G#", "H#", "J#", "W@", "E@", "-#", "_#", "K#", "=#",
                "L#", "Z#", "O#", "X#", "C#", "V#", "B#", "N#", "R@", "T@", "M#", "Y@", "U@", "I@", "O@", "Q#", "W#",
                "E#", "R#", "T#", "Y#", "U#","I#" };
        String[] code2 = { "م", "ي", "س", "ش", "ط", "ظ", "ر", "ذ", "د", "ع", "ض", "ص", "ث", "ح", "خ", "ه", "ج", "چ",
                "ل", "ا", "ب", "ت", "گ", "ک", "ف", "پ", "ق", "غ", "ن", "ز", "ژ", "و", "آ", "ة", "ئ", "ؤ", "‌أ" ,"ك"};
        int i;
        if (mode == 0) {
            for (i = 0; i < code2.length; i++) {
                str = str.replace(code2[i], code1[i]);
            }
            return str;
        } else {
            for (i = 0; i < code2.length; i++) {
                str = str.replace(code1[i], code2[i]);
            }
            return str;
        }
    }*/
    public String getOnvanFav(int type, int code)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        System.out.println("type=" + type + "    code=" + code);

        String str="khali";
        if (type == 0 || type == 1 || type == 2 || type == 3)
        {
            String query = "SELECT * FROM sobhisal WHERE type='" + type + "'" + "AND id='" + code + "'";
            Cursor cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                String onvan = cursor.getString(cursor.getColumnIndex("onvan"));
                onvan = onvan.replace("ي", "ی");
                onvan=onvan.replace("ك","ک");
                str=onvan + "";

            }
        }


        db.close();
        return str;
    }
    public Vector getHekayatList() {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM dastan";

        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"))+"";
            //String content = cursor.getString(cursor.getColumnIndex("content"))+"";

            if(title.equals("null"))
                title=" ";
            //if(content.equals("null"))
                //content=" ";
            //////////////////////////title = CodeDecode(title,1);
            //content = CodeDecode(content,1);
            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");
            //content = content.replace("ي", "ی");
            //content=content.replace("ك","ک");


            //content=content.replace("\r\n","<br>");
            //content=content.replace("\n","<br>");
            //content=content.replace("blue","navy");
            //content=content.replace("green","blue");

            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);
            //temp.add(content);

            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getTajvidList(int parent)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM tajvid where parentid="+parent;

        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"))+"";


            if(title.equals("null"))
                title=" ";
            //////////////////////////title = CodeDecode(title,1);

            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");


            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);


            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getAyatList()
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM quran_5tan";

        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("id"));
            String title = cursor.getString(cursor.getColumnIndex("name"))+"";


            if(title.equals("null"))
                title=" ";


            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");


            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);


            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getAyatDet(int id)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM quran_5tan where id="+id;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String matn = cursor.getString(cursor.getColumnIndex("matn"))+"";
            String tarjomeh = cursor.getString(cursor.getColumnIndex("tarjomeh"))+"";
            String tafsir = cursor.getString(cursor.getColumnIndex("tafsir"))+"";
            String refrance = cursor.getString(cursor.getColumnIndex("refrance"))+"";


            if(matn.equals("null"))
                matn=" ";
            if(tarjomeh.equals("null"))
                tarjomeh=" ";
            if(tafsir.equals("null"))
                tafsir=" ";
            if(refrance.equals("null"))
                refrance=" ";

            //////////////////////////matn = CodeDecode(matn,1);
            //////////////////////////tarjomeh = CodeDecode(tarjomeh,1);
            //////////////////////////tafsir = CodeDecode(tafsir,1);
            //////////////////////////refrance = CodeDecode(refrance,1);

            matn = matn.replace("ي", "ی");
            matn=matn.replace("ك","ک");
            tarjomeh = tarjomeh.replace("ي", "ی");
            tarjomeh=tarjomeh.replace("ك","ک");
            tafsir = tafsir.replace("ي", "ی");
            tafsir=tafsir.replace("ك","ک");
            refrance = refrance.replace("ي", "ی");
            refrance=refrance.replace("ك","ک");

            //tafsir=tafsir.replace("\r\n","<br>");
            //tafsir=tafsir.replace("\n","<br>");
            //refrance=refrance.replace("\r\n","<br>");
            //refrance=refrance.replace("\n","<br>");

            vec.add(matn);
            vec.add(tarjomeh);
            vec.add(tafsir);
            vec.add(refrance);

        }
        db.close();
        return vec;
    }
    public Vector getMaqalatDet(int id)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM quran_articel where code="+id;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String writer = cursor.getString(cursor.getColumnIndex("writer"))+"";
            String name = cursor.getString(cursor.getColumnIndex("name"))+"";
            String dsc = cursor.getString(cursor.getColumnIndex("dsc"))+"";
            String subdsc = cursor.getString(cursor.getColumnIndex("subdsc"))+"";


            if(writer.equals("null"))
                writer=" ";
            if(name.equals("null"))
                name=" ";
            if(dsc.equals("null"))
                dsc=" ";
            if(subdsc.equals("null"))
                subdsc=" ";

            //////////////////////////dsc = CodeDecode(dsc,1);
            //////////////////////////subdsc = CodeDecode(subdsc,1);


            writer = writer.replace("ي", "ی");
            writer=writer.replace("ك","ک");
            name = name.replace("ي", "ی");
            name=name.replace("ك","ک");
            dsc = dsc.replace("ي", "ی");
            dsc=dsc.replace("ك","ک");
            subdsc = subdsc.replace("ي", "ی");
            subdsc=subdsc.replace("ك","ک");

            //tafsir=tafsir.replace("\r\n","<br>");
            //tafsir=tafsir.replace("\n","<br>");
            //refrance=refrance.replace("\r\n","<br>");
            //refrance=refrance.replace("\n","<br>");

            vec.add(writer);
            vec.add(name);
            vec.add(dsc);
            vec.add(subdsc);

        }
        db.close();
        return vec;
    }
    public Vector getPorsemanDet(int id)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM question where _id="+id;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String title = cursor.getString(cursor.getColumnIndex("title"))+"";
            String content = cursor.getString(cursor.getColumnIndex("content"))+"";



            if(title.equals("null"))
                title=" ";
            if(content.equals("null"))
                content=" ";


            //////////////////////////title = CodeDecode(title,1);
            //////////////////////////content = CodeDecode(content,1);


            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");
            content = content.replace("ي", "ی");
            content=content.replace("ك","ک");


            //tafsir=tafsir.replace("\r\n","<br>");
            //tafsir=tafsir.replace("\n","<br>");
            //refrance=refrance.replace("\r\n","<br>");
            //refrance=refrance.replace("\n","<br>");

            vec.add(title);
            vec.add(content);

        }
        db.close();
        return vec;
    }
    public Vector getDastanDet(int id)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM dastan where _id="+id;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String content = cursor.getString(cursor.getColumnIndex("content"))+"";



            if(content.equals("null"))
                content=" ";


            //////////////////////////content = CodeDecode(content,1);


            content = content.replace("ي", "ی");
            content=content.replace("ك","ک");


            content=content.replace("\r\n","<br>");
            content=content.replace("\n","<br>");
            //refrance=refrance.replace("\r\n","<br>");
            //refrance=refrance.replace("\n","<br>");

            vec.add(content);
        }
        db.close();
        return vec;
    }
    public Vector getTajvidDet(int id)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM tajvid where _id="+id;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String content = cursor.getString(cursor.getColumnIndex("content"))+"";



            if(content.equals("null"))
                content=" ";


            //////////////////////////content = CodeDecode(content,1);


            content = content.replace("ي", "ی");
            content=content.replace("ك","ک");


            content=content.replace("\r\n","<br>");
            content=content.replace("\n","<br>");
            //refrance=refrance.replace("\r\n","<br>");
            //refrance=refrance.replace("\n","<br>");

            vec.add(content);
        }
        db.close();
        return vec;
    }
    public Vector getMoamaDet(int id)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM moama where _id="+id;

        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            String content = cursor.getString(cursor.getColumnIndex("content"))+"";



            if(content.equals("null"))
                content=" ";


            //////////////////////////content = CodeDecode(content,1);


            content = content.replace("ي", "ی");
            content=content.replace("ك","ک");


            content=content.replace("\r\n","<br>");
            content=content.replace("\n","<br>");
            //refrance=refrance.replace("\r\n","<br>");
            //refrance=refrance.replace("\n","<br>");

            vec.add(content);
        }
        db.close();
        return vec;
    }
    public Vector getPorsemanList(int parent)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM question where parentid="+parent;

        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"))+"";


            if(title.equals("null"))
                title=" ";
            //////////////////////////title = CodeDecode(title,1);

            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");


            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);


            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getMaqalatList()
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM quran_articel";

        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("code"));
            String title = cursor.getString(cursor.getColumnIndex("name"))+"";


            if(title.equals("null"))
                title=" ";


            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");


            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);


            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getMoamaList(int parent)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM moama where parentid="+parent;

        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            String title = cursor.getString(cursor.getColumnIndex("title"))+"";


            if(title.equals("null"))
                title=" ";
            //////////////////////////title = CodeDecode(title,1);

            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");


            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);


            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getAhadisList()
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM list";

        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("key"));
            String title = cursor.getString(cursor.getColumnIndex("name"))+"";


            if(title.equals("null"))
                title=" ";


            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");


            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);


            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getAhadisList2(int listkey)
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM listhadith where listkey="+listkey;

        Cursor cursor = db.rawQuery(query, null);
        int index = 1;
        while (cursor.moveToNext())
        {
            int _id = cursor.getInt(cursor.getColumnIndex("key"));
            String title = cursor.getString(cursor.getColumnIndex("name"))+"";


            if(title.equals("null"))
                title=" ";


            title = title.replace("ي", "ی");
            title=title.replace("ك","ک");


            Vector temp = new Vector();
            temp.add(_id);
            temp.add(index + "- " + title);
            temp.add(title);


            vec.add(temp);
            index++;
        }
        db.close();
        return vec;
    }
    public Vector getqoran()
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM sura";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"))+"";
            String arabic_name = cursor.getString(cursor.getColumnIndex("arabic_name"))+"";
            String location = cursor.getString(cursor.getColumnIndex("location"))+"";
            int count_aya = cursor.getInt(cursor.getColumnIndex("count_aya"));
            int hizb = cursor.getInt(cursor.getColumnIndex("hizb"));
            int juz = cursor.getInt(cursor.getColumnIndex("juz"));

            name = name.replace("ي", "ی");
            name=name.replace("ك","ک");


            Vector temp = new Vector();
            temp.add(id);
            temp.add(name);
            temp.add(arabic_name);
            temp.add(count_aya);
            temp.add(hizb);
            temp.add(juz);
            temp.add(location);




            vec.add(temp);
        }
        db.close();
        return vec;
    }
    public Vector getQaryList()
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM audio";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"))+"";
            String dir_name = cursor.getString(cursor.getColumnIndex("dir_name"))+"";
            String url = cursor.getString(cursor.getColumnIndex("url"))+"";
            url=url.trim();




            Vector temp = new Vector();
            temp.add(id);
            temp.add(name);
            temp.add(dir_name);
            temp.add(url);

            vec.add(temp);
        }
        db.close();
        return vec;
    }
    public Vector getQaryUrl()
    {
        Vector vec = new Vector();
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);

        String query = "";
        query = "SELECT * FROM audio";

        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"))+"";
            String dir_name = cursor.getString(cursor.getColumnIndex("dir_name"))+"";
            String url = cursor.getString(cursor.getColumnIndex("url"))+"";
            url=url.trim();




            Vector temp = new Vector();
            temp.add(id);
            temp.add(name);
            temp.add(dir_name);
            temp.add(url);

            vec.add(temp);
        }
        db.close();
        return vec;
    }
}
