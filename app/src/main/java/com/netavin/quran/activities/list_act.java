package com.netavin.quran.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
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
import com.netavin.quran.adapters.list_adapter;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 23/06/2017.
 */
public class list_act extends AppCompatActivity
{
    DatabaseHelper dbHelper = null;
    Vector vec;
    String parent = "";

    //1=qoran   2=ayat   3=porseman   4=hekayat   5=tajvid   6=moama   7=maqalat   8=ahadis   9=tajvid_click   10=porseman_click
    //11=moama_click     70=about
    int type = 0;
    String onvan="";
    int num=0;
    ListView lst;
    EditText inputSearch;
    font_class mf;


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String base_adr="";

    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_act);


        mf = new font_class(getApplicationContext());
        inputSearch = (EditText) findViewById(R.id.listact_inputSearch);
        inputSearch.setTypeface(mf.getYekan());

        pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = pref.edit();
        base_adr = pref.getString("base_adr", "null");

        boolean screen_on = pref.getBoolean("screen_on", true);
        findViewById(R.id.listact_list).setKeepScreenOn(screen_on);

        lst= (ListView) findViewById(R.id.listact_list);
        title= (TextView) findViewById(R.id.listact_title);

        title.setTypeface(mf.getAdobeBold());

        Intent myint=getIntent();
        parent=myint.getStringExtra("parent");
        onvan=myint.getStringExtra("onvan");
        num=myint.getIntExtra("num",0);

        title.setText(Html.fromHtml("<center>"+onvan+"</center>"));

        if(parent.equals("qoran"))
        {
            //setQoran();
        }
        else if(parent.equals("ayat"))
        {
            setAyat();
        }
        else if(parent.equals("porseman"))
        {
            setPorseman();
        }
        else if(parent.equals("hekayat"))
        {
            setHekayat();
        }
        else if(parent.equals("tajvid"))
        {
            setTajvid();
        }
        else if(parent.equals("moama"))
        {
            setMoama();
        }
        else if(parent.equals("maqalat"))
        {
            setMaqalat();
        }
        else if(parent.equals("ahadis"))
        {
            setAhadis();
        }
        else if(parent.equals("tajvid_click"))
        {
            setTajvidClick();
        }
        else if(parent.equals("porseman_click"))
        {
            setPorsemanClick();
        }
        else if(parent.equals("moama_click"))
        {
            setMoamaClick();
        }
        else if(parent.equals("about"))
        {
            setAbout();
        }

        lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Vector tag=new Vector();
                tag= (Vector) view.getTag();
                onclick(tag,position);
            }
        });


        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                //System.out.println(cs+"");
                Vector vv=filterVec(cs+"");
                lst.setAdapter(new list_adapter(list_act.this, vv,type));
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

        ImageView hazf= (ImageView) findViewById(R.id.listact_hazf);

        hazf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearch.setText("");
            }
        });


        /*ImageView tanzimat= (ImageView) findViewById(R.id.listact_tanzimat);
        ImageView about= (ImageView) findViewById(R.id.listact_about);
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

        if(type==70)
        {
            //about.setVisibility(View.GONE);
        }
    }
    private Vector filterVec(String str)
    {
        Vector myvec=new Vector();
        for (int i = 0; i < vec.size(); i++)
        {
            Vector temp = new Vector();
            temp = (Vector) vec.elementAt(i);
            String onvan = temp.elementAt(1) + "";
            if (onvan.contains(str))
            {
                myvec.add(temp);
            }
        }


        return myvec;
    }
    private void setAbout()
    {
        type=70;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        Vector temp=new Vector();
        temp.add(0);
        temp.add("تأسيس");
        vec.add(temp);

        temp=new Vector();
        temp.add(1);
        temp.add("بخش حديث ");
        vec.add(temp);

        temp=new Vector();
        temp.add(2);
        temp.add("بخش رجال");
        vec.add(temp);

        temp=new Vector();
        temp.add(3);
        temp.add("بخش فقه ");
        vec.add(temp);

        temp=new Vector();
        temp.add(4);
        temp.add("اطلاع رساني");
        vec.add(temp);

        temp=new Vector();
        temp.add(5);
        temp.add("زندگينامه دكتر حسيني قزويني ");
        vec.add(temp);


        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setHekayat()
    {
        type=4;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getHekayatList();
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setTajvid()
    {
        type=5;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getTajvidList(0);
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setTajvidClick()
    {
        type=9;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getTajvidList(num);
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setAyat()
    {
        type=2;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getAyatList();
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setPorseman()
    {
        type=3;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getPorsemanList(0);
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setPorsemanClick()
    {
        type=10;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getPorsemanList(num);
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));
    }
    private void setMaqalat()
    {
        type=7;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getMaqalatList();
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setMoama()
    {
        type=6;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getMoamaList(0);
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setMoamaClick()
    {
        type=11;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getMoamaList(num);
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void setAhadis()
    {
        type=8;
        dbHelper = new DatabaseHelper(this, base_adr + "/parsmagz/qoran","qoran");
        vec=new Vector();
        vec=dbHelper.getAhadisList();
        System.out.println("vec.size()="+vec.size());
        lst.setAdapter(new list_adapter(list_act.this, vec,type));

    }
    private void onclick(Vector tag,int pos)
    {
        //1=qoran   2=ayat   3=porseman   4=hekayat   5=tajvid   6=moama   7=maqalat   8=ahadis   9=tajvid_click   10=porseman_click
        //11=moama_click     70=about
        if(type==4)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),matn_template.class);
            myint.putExtra("type", 4);
            myint.putExtra("key", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==5)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),list_act.class);
            myint.putExtra("parent","tajvid_click");
            myint.putExtra("num", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==9)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),matn_template.class);
            myint.putExtra("type", 5);
            myint.putExtra("key", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==2)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),matn_template.class);
            myint.putExtra("type", 2);
            myint.putExtra("key", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==3)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),list_act.class);
            myint.putExtra("parent","porseman_click");
            myint.putExtra("num", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==10)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),matn_template.class);
            myint.putExtra("type", 3);
            myint.putExtra("key", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==7)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),matn_template.class);
            myint.putExtra("type", 7);
            myint.putExtra("key", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==6)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),list_act.class);
            myint.putExtra("parent","moama_click");
            myint.putExtra("num", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==11)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),matn_template.class);
            myint.putExtra("type", 6);
            myint.putExtra("key", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==8)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(2)+"";
            Intent myint=new Intent(getApplicationContext(),ahadis_matn.class);
            myint.putExtra("key", id);
            myint.putExtra("onvan",title);
            startActivity(myint);
        }
        else if(type==70)
        {
            int id= (int) tag.elementAt(0);
            String title=tag.elementAt(1)+"";
            Intent myint=new Intent(getApplicationContext(),matn_template.class);
            myint.putExtra("type", 70);
            myint.putExtra("key", id);
            myint.putExtra("onvan","درباره ما");
            startActivity(myint);
        }
    }
}
