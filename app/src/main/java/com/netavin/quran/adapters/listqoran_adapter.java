package com.netavin.quran.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netavin.quran.R;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 26/06/2017.
 */
public class listqoran_adapter extends ArrayAdapter<String>
{
    private final Context context;
    private final Vector values;
    font_class mf;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public listqoran_adapter(Context context, Vector values) {
        super(context, R.layout.row_qoran, values);
        this.context = context;
        this.values = values;
        mf = new font_class(context);

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_qoran, null);
        }
        RelativeLayout row = (RelativeLayout) rowView.findViewById(R.id.row3_rel);
        TextView title = (TextView) rowView.findViewById(R.id.row3_lable);
        ImageView name= (ImageView) rowView.findViewById(R.id.row3_name);
        ImageView maki= (ImageView) rowView.findViewById(R.id.row3_maki);
        TextView info = (TextView) rowView.findViewById(R.id.row3_info);

        title.setTypeface(mf.getSultan());
        info.setTypeface(mf.getBadr());

        Vector temp = new Vector();
        temp = (Vector) values.elementAt(position);
        String loc=temp.elementAt(6)+"";
        title.setText(temp.elementAt(0)+"");
        title.setTextColor(context.getResources().getColorStateList(R.color.submit_txt_row));

        int num_aya= (int) temp.elementAt(3);
        int hezb= (int) temp.elementAt(4);
        int joz= (int) temp.elementAt(5);
        //String info_str="جزء "+joz+"-"+"حزب "+hezb+"-"+num_aya+" آیه";
        System.out.println("hezb="+hezb+"   num_aya="+num_aya);
        String info_str="جزء "+joz+"،"+"حزب "+hezb+"،"+" تعداد آیات "+num_aya;
        info.setText(info_str);

        if(loc.equals("مكه"))
        {
            maki.setImageResource(R.drawable.makki);
        }
        else
        {
            maki.setImageResource(R.drawable.madani);
        }
        name.setImageResource(context.getResources().getIdentifier("sname_" + temp.elementAt(0), "drawable", context.getPackageName()));
        //name.setImageResource(context.getResources().getIdentifier("" + temp.elementAt(0), "drawable", context.getPackageName()));
        rowView.setTag(temp);
        if (position % 2 == 0)
        {
            row.setBackgroundResource(R.drawable.row3_80);
        }
        else
        {
            row.setBackgroundResource(R.drawable.row3_50);
        }



        return rowView;
    }
}
