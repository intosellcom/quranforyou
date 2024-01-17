package com.netavin.quran.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.netavin.quran.R;
import com.netavin.quran.adapters.fav_adapter;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 01/07/2017.
 */
public class listfav_act extends AppCompatActivity
{
    ListView lst;
    EditText inputSearch;
    font_class mf;
    Vector vec;


    SQLiteDatabase db1 = null;
    String DBNAME = "qoran_valiasr.db";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String base_adr="";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listfav_act);


        db1 = openOrCreateDatabase(DBNAME, Context.MODE_PRIVATE,null);
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,POS INTEGER,PAGE INTEGER); ");//1

        mf = new font_class(getApplicationContext());
        //inputSearch = (EditText) findViewById(R.id.listfav_inputSearch);
        //inputSearch.setTypeface(mf.getYekan());

        TextView title = (TextView) findViewById(R.id.listfav_title);
        title.setTypeface(mf.getAdobeBold());



        lst= (ListView) findViewById(R.id.listfav_list);
        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vector tag=new Vector();
                tag= (Vector) view.getTag();
                System.out.println("listlistlistlistlistlist "+position);
                onclick(tag);
            }
        });

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        base_adr = pref.getString("base_adr", "null");

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.listfav_child1).setKeepScreenOn(screen_on);

        initData();
        System.out.println("vec.size()="+vec.size());


        /*ImageView tanzimat= (ImageView) findViewById(R.id.listfav_tanzimat);
        ImageView about= (ImageView) findViewById(R.id.listfav_about);
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
    public void initData()
    {
        db1.execSQL("CREATE TABLE IF NOT EXISTS tb_fav(TYPE INTEGER,ONVAN TEXT,KEY INTEGER,PAGE INTEGER); ");
        vec=new Vector();
        try{
            Cursor c = db1.rawQuery("SELECT * FROM tb_fav", null);
            //Cursor c = db1.rawQuery("SELECT * FROM tabq34 WHERE FIRSTNAME='" +edtname.getText()+"'", null);
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


                        Vector temp=new Vector();

                        temp.add(type);
                        temp.add(onvan);
                        temp.add(key);
                        temp.add(pos);
                        temp.add(page);
                        vec.add(temp);
                        //System.out.println(code+"   nn   "+type);
                    }while(c.moveToNext());
                }

                lst.setAdapter(new fav_adapter(listfav_act.this, vec));
                //db1.close();
            }
        } catch(Exception e)
        {
            System.out.println(e);
        }
    }
    public void starClick(Vector tag)
    {
        System.out.println("qqqqqqqqqqqqqqqqqqqqqqqqqqqqqq");
        int type= (int) tag.elementAt(0);
        String onvan=tag.elementAt(1)+"";
        int key= (int) tag.elementAt(2);
        int pos= (int) tag.elementAt(3);
        int page= (int) tag.elementAt(4);
        try
        {
            db1.execSQL("DELETE  FROM tb_fav WHERE ONVAN='" +onvan+"'"+" AND TYPE='"+type+"'"+" AND KEY='"+key+"'"+" AND POS='"+pos+"'"+" AND PAGE='"+page+"'");
            initData();
        }
        catch(Exception e)
        {
            System.out.println("eeeeeeeeeeeerrrrrrrrr2"+e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage()+"  bb",Toast.LENGTH_SHORT).show();
        }
    }
    private void onclick(Vector tag)
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
