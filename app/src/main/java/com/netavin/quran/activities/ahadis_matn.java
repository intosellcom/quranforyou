package com.netavin.quran.activities;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import androidx.appcompat.app.LinearLayoutManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netavin.quran.R;
import com.netavin.quran.adapters.font_adapter;
import com.netavin.quran.adapters.hadis_adapter;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.MyLinearLayoutManager;
import com.netavin.quran.classes.font_class;


import java.util.Vector;

/**
 * Created by mehdi on 03/04/2017.
 */
public class ahadis_matn extends AppCompatActivity {
    DatabaseHelper dbHelper = null;
    String base_adr = "";
    font_class mf;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    SQLiteDatabase db1 = null;
    String DBNAME = "qoran_valiasr.db";

    ImageView qalam_btn, fav_btn, kuchak_btn, bozorh_btn, eshterak_btn;

    ListView lst;
    hadis_adapter adapter;
    EditText inputSearch;
    Vector vec;

    public Dialog dialog;

    int key = 0;
    String onvan = "";
    int type = 0;
    //String dsc="";


    String eshterak_txt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ahadis_matn);

        mf = new font_class(this);
        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        base_adr = pref.getString("base_adr", "null");

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.hadismatn_qalam).setKeepScreenOn(screen_on);

        db1 = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE, null);
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//4

        inputSearch = (EditText) findViewById(R.id.hadismatn_inputSearch);
        inputSearch.setTypeface(mf.getYekan());

        qalam_btn = (ImageView) findViewById(R.id.hadismatn_qalam);
        fav_btn = (ImageView) findViewById(R.id.hadismatn_alaqemandiha);
        kuchak_btn = (ImageView) findViewById(R.id.hadismatn_kuchak);
        bozorh_btn = (ImageView) findViewById(R.id.hadismatn_bozorg);
        eshterak_btn = (ImageView) findViewById(R.id.hadismatn_eshterak);

        TextView title = (TextView) findViewById(R.id.hadismatn_title);
        title.setTypeface(mf.getAdobeBold());
        lst = (ListView) findViewById(R.id.hadismatn_list);

        Intent myint = getIntent();
        key = myint.getIntExtra("key", 0);
        onvan = myint.getStringExtra("onvan");
        //String listkey=myint.getStringExtra("listkey");
        type = 100;
        //dsc="";

        title.setText(Html.fromHtml("<center>" + onvan + "</center>"));
        initData(key);


        qalam_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qalam_dialog();
            }
        });
        fav_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fav_click();
            }
        });
        kuchak_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showProg("");
                kuchak_click();
            }
        });
        bozorh_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showProg("");
                bozorg_click();
            }
        });
        eshterak_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eshterak_click();
            }
        });

        setFav();
        setLastSeen();


        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //System.out.println(cs+"");
                Vector vv = filterVec(cs + "");
                adapter = new hadis_adapter(ahadis_matn.this, vv);
                lst.setAdapter(adapter);
                //lst.setAdapter(new list_adapter(ahadis_matn.this, vv,type));


            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        ImageView hazf= (ImageView) findViewById(R.id.hadismatn_hazf);

        hazf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearch.setText("");
            }
        });


        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vector tag = new Vector();
                tag = (Vector) view.getTag();
                onclick(tag, position);
            }
        });

        ImageView tanzimat= (ImageView) findViewById(R.id.hadismatn_tanzimat);
        ImageView about= (ImageView) findViewById(R.id.hadismatn_about);
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
        });
    }

    private Vector filterVec(String str) {
        /*temp.add(key);
        temp.add(masom);
        temp.add(arabic);
        temp.add(farsi);
        temp.add(manbah);*/
        Vector myvec = new Vector();
        for (int i = 0; i < vec.size(); i++) {
            Vector temp = new Vector();
            temp = (Vector) vec.elementAt(i);
            String masom = temp.elementAt(1) + "";
            String arabic = temp.elementAt(2) + "";
            String farsi = temp.elementAt(3) + "";
            String manbah = temp.elementAt(4) + "";
            if (masom.contains(str) || arabic.contains(str) || farsi.contains(str) || manbah.contains(str)) {
                myvec.add(temp);
            }
        }


        return myvec;
    }

    private void initData(int key) {
        //dbHelper = new DatabaseHelper(this, base_adr + "/ahrar/golbargha", "hadis");
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran", "qoran");
        vec = new Vector();
        vec = dbHelper.getHadiseList2(key);


        adapter = new hadis_adapter(ahadis_matn.this, vec);
        lst.setAdapter(adapter);
    }

    public void qalam_dialog() {
        dialog = new Dialog(ahadis_matn.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_font);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView title = (TextView) dialog.findViewById(R.id.alert_font_title_txt);
        title.setTypeface(mf.getYekan());
        title.setText("فونت را انتخاب کنید :");
        RecyclerView mRecyclerView = (RecyclerView) dialog.findViewById(R.id.alert_font_list);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(getApplicationContext(), Configuration.ORIENTATION_PORTRAIT, false));

        Vector values = new Vector();

        values.add("یکان");
        values.add("بدر");
        values.add("نازنین");
        values.add("سلطان");
        values.add("زر");

        RecyclerView.Adapter mAdapter = new font_adapter(ahadis_matn.this, values, 100);
        mRecyclerView.setAdapter(mAdapter);
        dialog.show();
    }

    public void setfont() {
        //adapter.setfont();
        lst.setAdapter(adapter);
    }

    public void kuchak_click() {
        adapter.kuchak_click();
        lst.setAdapter(adapter);
    }

    public void bozorg_click() {
        adapter.bozorg_click();
        lst.setAdapter(adapter);
    }

    private void setFav() {
        int pos = 0;
        String dsc = "";
        String sound_name = "";
        String dsctarjome = "";
        //db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,DSC TEXT,KEY INTEGER); ");
        try {
            //Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan+"'"+"AND TYPE='"+type+"'"+"AND DSC='"+dsc+"'"+"AND KEY='"+key+"'", null);
            Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" + onvan + "'" + " AND TYPE='" + type + "'" + " AND KEY='" + key + "'" + " AND POS='" + pos  + "'" + " AND PAGE='" + 4 + "'", null);
            if (c != null) {
                if (c.getCount() == 0) {
                    fav_btn.setImageResource(R.drawable.submit_alaqemandiha2);
                }
                if (c.getCount() == 1) {
                    fav_btn.setImageResource(R.drawable.submit_alaqemandiha2fav);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage() + "  aa", Toast.LENGTH_SHORT).show();
        }

    }

    private void fav_click() {
        int pos = 0;
        String dsc = "";
        String sound_name = "";
        String dsctarjome = "";
        try {
            //Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" +onvan+"'"+"AND TYPE='"+type+"'"+"AND DSC='"+dsc+"'"+"AND KEY='"+key+"'", null);
            Cursor c = db1.rawQuery("SELECT * FROM tb_fav WHERE ONVAN='" + onvan + "'" + " AND TYPE='" + type  + "'" + " AND KEY='" + key + "'" + " AND POS='" + pos + "'" + " AND PAGE='" + 4 + "'", null);
            if (c != null) {
                if (c.getCount() == 0)//item dar db vojud nadarad pas insert mikonim
                {

                    //db1.execSQL("INSERT INTO tb_fav (TYPE,ONVAN,DSC,KEY) VALUES ('" + type + "','" + onvan+"','" + dsc+"','"+key + "');");
                    db1.execSQL("INSERT INTO tb_fav (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan  + "','" + key + "','" + pos  + "','" + 4 + "');");
                    Toast.makeText(ahadis_matn.this, " " + "به لیست علاقه مندیها اضافه شد.", Toast.LENGTH_SHORT).show();

                }
                if (c.getCount() == 1)//item dar db vojud darad pas delete mikonim
                {
                    //db1.execSQL("DELETE  FROM tb_fav WHERE ONVAN='" +onvan+"'"+"AND TYPE='"+type+"'"+"AND DSC='"+dsc+"'"+"AND KEY='"+key+"'");
                    db1.execSQL("DELETE  FROM tb_fav WHERE ONVAN='" + onvan + "'" + " AND TYPE='" + type + "'" + " AND KEY='" + key + "'" + " AND POS='" + pos  + "'" + " AND PAGE='" + 4 + "'");
                    Toast.makeText(ahadis_matn.this, " " + "از لیست علاقه مندیها حذف شد.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage() + "  bb", Toast.LENGTH_SHORT).show();
        }
        setFav();

    }

    private void eshterak_click() {
        for (int i = 0; i < vec.size(); i++) {
            Vector temp = new Vector();
            temp = (Vector) vec.elementAt(i);
            eshterak_txt += temp.elementAt(1) + "\n" + temp.elementAt(2) + "\n" + temp.elementAt(3) + "\n" + temp.elementAt(4) + "\n" + "************************" + "\n";
        }
        //eshterak_txt += "\n" + "برگرفته از نرم افزار جامع گلبرگها";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, eshterak_txt);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "اشتراک گذاری"));

    }

    private void onclick(Vector tag, int pos) {
        eshterak_txt = tag.elementAt(1) + "\n" + tag.elementAt(2) + "\n" + tag.elementAt(3) + "\n" + tag.elementAt(4) ;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, eshterak_txt);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "اشتراک گذاری"));
    }

    private void setLastSeen() {
        int pos = 0;
        String dsc = "";
        String sound_name = "";
        String dsctarjome = "";
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_lastseen(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//4
        try {
            Cursor c = db1.rawQuery("SELECT * FROM tb_lastseen ", null);
            System.out.println("c.getCount()1=" + c.getCount());
            if (c != null) {
                if (c.getCount() == 0)//item dar db vojud nadarad pas insert mikonim
                {
                    db1.execSQL("INSERT INTO tb_lastseen (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan + "','" + key + "','" + pos + "','" + 4 + "');");
                }
                if (c.getCount() > 0)//item dar db vojud darad pas delete mikonim
                {
                    System.out.println("c.getCount()4=" + c.getCount());
                    db1.execSQL("DELETE  FROM tb_lastseen ");
                    db1.execSQL("INSERT INTO tb_lastseen (TYPE,ONVAN,KEY,POS,PAGE) VALUES ('" + type + "','" + onvan  + "','" + key + "','" + pos + "','" + 4 + "');");
                    System.out.println("c.getCount()5=" + c.getCount());
                }
            }
            System.out.println("c.getCount()3=" + c.getCount());
        } catch (Exception e) {
            System.out.println("eeeeeeeeeeeerrrrrrrrr2" + e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage() + "  bb", Toast.LENGTH_SHORT).show();
        }
    }
}

