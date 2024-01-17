package com.netavin.quran.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.netavin.quran.R;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 06/07/2017.
 */
public class joz_adapter extends ArrayAdapter<String>
{
    private final Context context;
    private final Vector values;
    font_class mf;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int type=0;//1=joz   2=hezb
    public joz_adapter(Context context, Vector values,int type) {
        super(context, R.layout.row_joz, values);
        this.context = context;
        this.values = values;
        mf = new font_class(context);
        this.type=type;

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_joz, null);
        }
        TextView title = (TextView) rowView.findViewById(R.id.rowjoz_lable);

        title.setTypeface(mf.getSultan());

        title.setText(values.elementAt(position)+"");
        title.setTextColor(context.getResources().getColorStateList(R.color.submit_txt_row));

        Vector tag=new Vector();
        tag.add(type);
        tag.add(values.elementAt(position)+"");
        rowView.setTag(values.elementAt(position)+"");

        return rowView;
    }
}
