package com.netavin.quran.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.netavin.quran.R;
import com.netavin.quran.adapters.joz_adapter;
import com.netavin.quran.adapters.listqoran_adapter;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 25/06/2017.
 */
public class list_qoran extends AppCompatActivity
{
    DatabaseHelper dbHelper = null;
    Vector vec;
    String parent = "";

    String onvan="";
    int num=0;
    ListView lst;
    ListView lst_joz,lst_hezb;
    EditText inputSearch;
    font_class mf;


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String base_adr="";

    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_qoran);


        mf = new font_class(getApplicationContext());
        inputSearch = (EditText) findViewById(R.id.listqoran_inputSearch);
        inputSearch.setTypeface(mf.getYekan());

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        base_adr = pref.getString("base_adr", "null");

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.listqoran_list).setKeepScreenOn(screen_on);

        lst = (ListView) findViewById(R.id.listqoran_list);
        lst_joz = (ListView) findViewById(R.id.listqoran_listjoz);
        lst_hezb = (ListView) findViewById(R.id.listqoran_listhezb);
        title = (TextView) findViewById(R.id.listqoran_title);

        title.setTypeface(mf.getAdobeBold());


        TextView joz_txt=(TextView) findViewById(R.id.listqoran_joz);
        joz_txt.setTypeface(mf.getYekan());
        TextView hezb_txt=(TextView) findViewById(R.id.listqoran_hezb);
        hezb_txt.setTypeface(mf.getYekan());

        onvan = "فهرست سوره ها";
        title.setText(Html.fromHtml("<center>" + onvan + "</center>"));

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vector tag=new Vector();
                tag= (Vector) view.getTag();
                onclick(tag,position);
            }
        });

        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getqoran();
        lst.setAdapter(new listqoran_adapter(list_qoran.this, vec));


        Vector vec_joz=new Vector();
        for(int i=1;i<=30;i++)
        {
            vec_joz.add(i);
        }
        lst_joz.setAdapter(new joz_adapter(list_qoran.this, vec_joz,1));
        lst_joz.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vector tag=new Vector();
                String str= (String) view.getTag();
                //System.out.println("str="+str);
                Intent myint = new Intent(getApplicationContext(), qoranjoz_template.class);
                myint.putExtra("id",Integer.parseInt(str));
                myint.putExtra("key",1);

                startActivity(myint);
                //System.out.println("jozzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
            }
        });


        Vector vec_hezb=new Vector();
        for(int i=1;i<=120;i++)
        {
            vec_hezb.add(i);
        }
        lst_hezb.setAdapter(new joz_adapter(list_qoran.this, vec_hezb,2));
        lst_hezb.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vector tag=new Vector();
                String str= (String) view.getTag();
                //System.out.println("str="+str);
                Intent myint = new Intent(getApplicationContext(), qoranjoz_template.class);
                myint.putExtra("id",Integer.parseInt(str));
                myint.putExtra("key",2);

                startActivity(myint);
                //System.out.println("hezbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //System.out.println(cs+"");
                Vector vv=filterVec(cs+"");
                lst.setAdapter(new listqoran_adapter(list_qoran.this, vv));
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

        ImageView hazf= (ImageView) findViewById(R.id.listqoran_hazf);

        hazf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearch.setText("");
            }
        });


        /*ImageView tanzimat= (ImageView) findViewById(R.id.listqoran_tanzimat);
        ImageView about= (ImageView) findViewById(R.id.listqoran_about);
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
    private Vector filterVec(String str)
    {
        Vector myvec=new Vector();
        for (int i = 0; i < vec.size(); i++)
        {
            Vector temp = new Vector();
            temp = (Vector) vec.elementAt(i);
            String id= temp.elementAt(0)+"";
            String onvan = temp.elementAt(1) + "";
            if (onvan.contains(str)||id.contains(str))
            {
                myvec.add(temp);
            }
        }


        return myvec;
    }
    private void onclick(Vector tag,int pos)
    {
        //System.out.println(tag.elementAt(0)+"");//1..114
        int id=(int)tag.elementAt(0);
        String name=tag.elementAt(2)+"";
        int joz= (int) tag.elementAt(5);
        int ayat_count= (int) tag.elementAt(3);
        //System.out.println("id="+id+"   name="+name);//3  alomran(farsi)

        Intent myint = new Intent(getApplicationContext(), qoran_template.class);
        myint.putExtra("type",27);
        myint.putExtra("id",id);
        myint.putExtra("onvan",name);
        myint.putExtra("joz",joz);
        myint.putExtra("ayat_count",ayat_count);

        startActivity(myint);
    }
}
