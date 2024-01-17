package com.netavin.quran.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netavin.quran.R;
import com.netavin.quran.activities.matn_template;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 07/04/2017.
 */
public class qaran_adapter extends ArrayAdapter<String>
{
    DatabaseHelper dbHelper = null;
    private final Context context;
    private final Vector values;
    font_class mf;
    int type = 0;//27=qoran
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    TextView ar;
    TextView fa;
    int id=0;
    public qaran_adapter(Context context, Vector values, int type,int id)
    {
        super(context, R.layout.rowqoran, values);
        this.context = context;
        this.values = values;
        mf = new font_class(context);
        this.id=id;

        this.type = type;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
        String base_adr = pref.getString("base_adr", "null");
        dbHelper = new DatabaseHelper(context, base_adr + "/parsmagz/qoran","qoran");

    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowqoran, null);
        }
        RelativeLayout row = (RelativeLayout) rowView.findViewById(R.id.rowqoran_rel);
        ar = (TextView) rowView.findViewById(R.id.rowqoran_ar);
        fa = (TextView) rowView.findViewById(R.id.rowqoran_fa);

        ar.setTypeface(mf.getQoran());
        fa.setTypeface(mf.getAdobe());


        //Vector temp = new Vector();
        //temp = (Vector) values.elementAt(position);
        //ar.setText(temp.elementAt(0)+"");
        //fa.setText(temp.elementAt(1)+"");

        if(type==27)
        {
            int id= (int) values.elementAt(position);
            ar.setText(dbHelper.getQoranAr(id));
            fa.setText(dbHelper.getQoranFa(id));
        }


        if (position % 2 == 0)
        {
            row.setBackgroundColor(Color.parseColor("#f3f5d7"));
        }
        else
        {
            row.setBackgroundColor(Color.parseColor("#e6f4d3"));
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick(position);
            }
        });
        return rowView;
    }
    public void onclick(int pos)
    {
        if(type==21)
        {
            if(pos==0)
            {
                return;
            }
            Intent myint = new Intent(context, matn_template.class);
            myint.putExtra("type", 95);
            myint.putExtra("pos", pos);
            myint.putExtra("key",id);
            myint.putExtra("onvan","شرح صحیفه");
            myint.putExtra("dsc", pos+"");

            context.startActivity(myint);
        }
    }

}

