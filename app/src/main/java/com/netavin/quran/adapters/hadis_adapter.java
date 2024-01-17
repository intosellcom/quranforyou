package com.netavin.quran.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netavin.quran.R;
import com.netavin.quran.activities.ahadis_matn;
import com.netavin.quran.classes.font_class;


import java.util.Vector;

/**
 * Created by mehdi on 03/04/2017.
 */
public class hadis_adapter extends ArrayAdapter<String>
{
    private final Context context;
    private final Vector values;
    font_class mf;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ahadis_matn am;
    TextView txt;
    float curElementSize=0;
    public hadis_adapter(Context context, Vector values) {
        super(context, R.layout.row_hadis, values);
        this.context = context;
        this.values = values;
        mf = new font_class(context);

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();

        this.am=(ahadis_matn)context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_hadis, null);
        }
        RelativeLayout rel = (RelativeLayout) rowView.findViewById(R.id.rowhadis_rel);
        txt = (TextView) rowView.findViewById(R.id.rowhadis_txt);
        curElementSize =pixelsToSp(context.getApplicationContext(), (int) context.getResources().getDimension(R.dimen.tab_size));
        setfont();
        drawSize();

        Vector temp=new Vector();
        temp= (Vector) values.elementAt(position);
        SpannableString masom = new SpannableString(temp.elementAt(1)+"");
        masom.setSpan(new ForegroundColorSpan(Color.parseColor("#cc6600")), 0, masom.length(), 0);
        SpannableString arabic = new SpannableString(temp.elementAt(2)+"");
        arabic.setSpan(new ForegroundColorSpan(Color.parseColor("#339966")), 0, arabic.length(), 0);
        SpannableString farsi = new SpannableString(temp.elementAt(3)+"");
        farsi.setSpan(new ForegroundColorSpan(Color.parseColor("#0000cc")), 0, farsi.length(), 0);
        SpannableString manbah = new SpannableString(temp.elementAt(4)+"");
        manbah.setSpan(new ForegroundColorSpan(Color.BLACK), 0, manbah.length(), 0);

        CharSequence finalText="";
        if(position==(values.size()-1))
        {
            finalText = TextUtils.concat(masom,"\n",arabic,"\n",farsi,"\n",manbah,"\n");
        }
        else
        {
            finalText = TextUtils.concat(masom,"\n",arabic,"\n",farsi,"\n",manbah);
        }

        txt.setText(finalText, TextView.BufferType.SPANNABLE);

        if (position % 2 == 0)
        {
            rel.setBackgroundResource(R.drawable.row_bg);
        }
        else
        {
            rel.setBackgroundResource(R.drawable.row_bg2);
        }


        rowView.setTag(temp);
        return rowView;
    }
    public void setfont()
    {
        String item=pref.getString("hadis_font","یکان");
        txt.setTypeface(mf.getFace(item));
    }
    public static float pixelsToSp(Context context, float px)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }
    public void drawSize()
    {
        int mysize=0;
        mysize = pref.getInt("hadis_txt_size", 0);
        txt.setTextSize(curElementSize+mysize);
    }
    public void kuchak_click()
    {
        int mysize = pref.getInt("hadis_txt_size", 0);
        mysize -= 2;
        editor.putInt("hadis_txt_size", mysize);
        editor.commit();
        //drawSize();
    }
    public void bozorg_click()
    {
        int mysize = pref.getInt("hadis_txt_size", 0);
        mysize += 2;
        editor.putInt("hadis_txt_size", mysize);
        editor.commit();
        //drawSize();
    }

}

