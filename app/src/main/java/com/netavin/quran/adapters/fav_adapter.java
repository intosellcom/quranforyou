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
import com.netavin.quran.activities.listfav_act;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

public class fav_adapter extends ArrayAdapter<String>
{
    private final Context context;
    private final Vector values;
    font_class mf;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    listfav_act la;
    public fav_adapter(Context context, Vector values)
    {
        super(context, R.layout.row_fav, values);
        this.context = context;
        this.values = values;
        mf = new font_class(context);
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();
        la=(listfav_act)context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.row_fav, null);
        }
        RelativeLayout row = (RelativeLayout) rowView.findViewById(R.id.rowfav_rel);
        TextView title = (TextView) rowView.findViewById(R.id.rowfav_lable);
        TextView info = (TextView) rowView.findViewById(R.id.rowfav_info);
        final ImageView star = (ImageView) rowView.findViewById(R.id.rowfav_img);

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onclick(star);
            }
        });

        title.setTypeface(mf.getSultan());
        info.setTypeface(mf.getSultan());

        Vector temp = new Vector();
        temp = (Vector) values.elementAt(position);

        title.setText(editOnvan(temp.elementAt(1)+""));
        title.setTextColor(context.getResources().getColorStateList(R.color.submit_txt_row));

        String info_txt="";
        int type= (int) temp.elementAt(0);
        if(type==0)
        {
            info_txt="قرآن";
        }
        else if(type==1)
        {
            info_txt="قرآن";
        }
        else if(type==2)
        {
            info_txt="آیات اهل بیت";
        }
        else if(type==3)
        {
            info_txt="پرسمان";
        }
        else if(type==4)
        {
            info_txt="حکایات";
        }
        else if(type==5)
        {
            info_txt="تجوید";
        }
        else if(type==6)
        {
            info_txt="معماهای قرآنی";
        }
        else if(type==7)
        {
            info_txt="مقالات";
        }
        else if(type==8)
        {
            info_txt="احادیث";
        }
        else if(type==70)
        {
            info_txt="درباره ما";
        }
        else if(type==96)
        {
            info_txt="تفسیر";
        }
        info.setText(info_txt+"");

        star.setTag(temp);
        rowView.setTag(temp);
        return rowView;
    }
    private void onclick(ImageView star)
    {
        Vector tag=new Vector();
        tag= (Vector) star.getTag();
        System.out.println("starstarstarstarstarstarstarstarstarstarstarstar");
        la.starClick(tag);
    }
    private String editOnvan(String str)
    {
        int n=str.indexOf("-");
        //System.out.println("str="+str);
        String newStr=str.substring(n+1);
        //System.out.println("newStr="+newStr);


        return newStr;
    }
}

