package com.netavin.quran.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netavin.quran.R;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 24/06/2017.
 */
public class list_adapter extends ArrayAdapter<String>
{
    private final Context context;
    private final Vector values;
    font_class mf;
    int type = 0;//1=qoran   2=ayat   3=porseman   4=hekayat   5=tajvid   6=moama   7=maqalat   8=ahadis
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public list_adapter(Context context, Vector values, int type) {
        super(context, R.layout.row, values);
        this.context = context;
        this.values = values;
        mf = new font_class(context);

        this.type = type;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row, null);
        }
        RelativeLayout row = (RelativeLayout) rowView.findViewById(R.id.row1_rel);
        TextView title = (TextView) rowView.findViewById(R.id.row1_lable);

        title.setTypeface(mf.getSultan());

        if(type==4||type==5||type==9||type==2||type==3||type==10||type==7||type==6||type==8||type==11||type==70)
        {
            Vector temp = new Vector();
            temp = (Vector) values.elementAt(position);
            title.setText(temp.elementAt(1)+"");
            title.setTextColor(context.getResources().getColorStateList(R.color.submit_txt_row));
            rowView.setTag(temp);
            if (position % 2 == 0)
            {
                row.setBackgroundResource(R.drawable.row_80);
            }
            else
            {
                row.setBackgroundResource(R.drawable.row_50);
            }
        }

        return rowView;
    }
}
