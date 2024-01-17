package com.netavin.quran.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.graphics.Color;
import android.preference.PreferenceManager;
//import android.support.v4.widget.CursorAdapter;
import  android.widget.CursorAdapter;
//import android.support.v4.widget.CursorAdapter;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netavin.quran.R;
import com.netavin.quran.classes.CustomTypefaceSpan;
import com.netavin.quran.classes.DatabaseHelper;
import com.netavin.quran.classes.font_class;

/**
 * Created by mehdi on 13/04/2017.
 */
public class qoranjoz_curser extends CursorAdapter
{
    DatabaseHelper dbHelper = null;
    String base_adr="";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    font_class mf;
    private LayoutInflater inflater;
    float curElementSize=0;
    int tb_size = 0;
    int txt_size = 0;
    SpannableString pr_baz;
    SpannableString pr_baste;
    int num_aye=0;

    int type=0;
    boolean is_first_1=true;

    int num_joz=0;
    //SpannableStringBuilder ar_num;
    private    static  class   ViewHolder   {
        RelativeLayout row ;
        TextView   ar;
        TextView   fa;


    }
    public qoranjoz_curser(Context context, Cursor cursor,int num_aye,int type,int num_joz) {
        super(context, cursor, 0);
        mf = new font_class(context);
        this.num_aye=num_aye;
        this.type=type;
        this.num_joz=num_joz;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pref = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        editor = pref.edit();
        curElementSize =pixelsToSp(context, (int) context.getResources().getDimension(R.dimen.txt_size));
        tb_size = context.getResources().getDimensionPixelSize(R.dimen.tab_size_kuchak);
        txt_size = context.getResources().getDimensionPixelSize(R.dimen.txt_size);
        base_adr = pref.getString("base_adr", "null");

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View   view    =   inflater.inflate(R.layout.rowqoran, null);
        ViewHolder holder  =   new ViewHolder();
        holder.row = (RelativeLayout) view.findViewById(R.id.rowqoran_rel);
        holder.ar    =   (TextView)  view.findViewById(R.id.rowqoran_ar);
        holder.fa    =   (TextView)  view.findViewById(R.id.rowqoran_fa);


        holder.ar.setTypeface(mf.getQoran());
        holder.fa.setTypeface(mf.getAdobe());

        pr_baz = new SpannableString("﴿");
        pr_baste = new SpannableString("﴾");
        //ar_num = new SpannableStringBuilder("");

        pr_baz.setSpan(new CustomTypefaceSpan("", mf.getAdobe()), 0, pr_baz.length(), 0);
        pr_baz.setSpan(new ForegroundColorSpan(Color.parseColor("#bb3c04")), 0, pr_baz.length(), 0);
        pr_baste.setSpan(new CustomTypefaceSpan("", mf.getAdobe()), 0, pr_baste.length(), 0);
        pr_baste.setSpan(new ForegroundColorSpan(Color.parseColor("#bb3c04")), 0, pr_baste.length(), 0);


        //holder.pr_baz.setText("﴿");
        //holder.pr_baste.setText("﴾");

        int mysize=pref.getInt("qoran_txt_size",0);
        holder.ar.setTextSize(curElementSize+mysize);
        holder.fa.setTextSize(curElementSize+mysize);

        view.setTag(holder);



        //System.out.println("newView newView newView newView newView newView newView ");
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor)
    {
        ViewHolder holder  =   (ViewHolder)    view.getTag();

        String text = cursor.getString(cursor.getColumnIndexOrThrow("text"));
        String tr = cursor.getString(cursor.getColumnIndexOrThrow("tr"));
        //////////////////tr = CodeDecode(tr,1);

        int aya =  cursor.getInt(cursor.getColumnIndexOrThrow("aya"));
        int sura = cursor.getInt(cursor.getColumnIndexOrThrow("sura"));
        int _id =  cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        int id =  cursor.getInt(cursor.getColumnIndexOrThrow("id"));

        int cur_row=0;
        SpannableString ar_num;
        CharSequence finalText="";


        /*if(num_sure==1||num_sure==9)
        {
            ar_num = new SpannableString(aya+"");
            cur_row=aya;

            ar_num.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, ar_num.length(), 0);
            ar_num.setSpan(new AbsoluteSizeSpan(tb_size), 0, ar_num.length(), 0);

            finalText = TextUtils.concat(text, pr_baz,ar_num,pr_baste);

            if((aya)==num_aye)
            {
                tr=tr+"\n\n\n\n";
            }
        }
        else
        {
            if(aya==1&&_id==1)
            {
                cur_row=aya;

                finalText = TextUtils.concat(text);
            }
            else
            {
                ar_num = new SpannableString((aya)+"");
                cur_row=aya+1;

                ar_num.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, ar_num.length(), 0);
                ar_num.setSpan(new AbsoluteSizeSpan(tb_size), 0, ar_num.length(), 0);

                finalText = TextUtils.concat(text, pr_baz,ar_num,pr_baste);
            }

            if((aya+1)==num_aye)
            {
                tr=tr+"\n\n\n\n";
            }
        }*/


        ar_num = new SpannableString((aya)+"");
        cur_row=aya+1;

        ar_num.setSpan(new CustomTypefaceSpan("", mf.getYekan()), 0, ar_num.length(), 0);
        ar_num.setSpan(new AbsoluteSizeSpan(tb_size), 0, ar_num.length(), 0);

        if(aya!=0)
        {
            finalText = TextUtils.concat(text, pr_baz,ar_num,pr_baste);
        }
        else
        {
            dbHelper = new DatabaseHelper(context, base_adr + "/parsmagz/qoran","qoran");
            String name_sure=dbHelper.getQoranName(sura);
            SpannableString name_span = new SpannableString("سورة  "+name_sure);
            //name_span.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), 0, name_span.length(), 0);
            name_span.setSpan(new CustomTypefaceSpan("", mf.getQoran()), 0, name_span.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            name_span.setSpan(new AbsoluteSizeSpan(txt_size), 0, name_span.length(), 0);
            name_span.setSpan(new ForegroundColorSpan(Color.parseColor("#bb3c04")), 0, name_span.length(), 0);

            finalText = TextUtils.concat(name_span,"\n",text);
        }




        holder.ar.setText(finalText, TextView.BufferType.SPANNABLE);
        holder.fa.setText(tr);

        if (cur_row % 2 == 0)
        {
            holder.row.setBackgroundColor(Color.parseColor("#ffe2a4"));
        }
        else
        {
            holder.row.setBackgroundColor(Color.parseColor("#feeeca"));
        }

    }
    public void setface()
    {
        //super.
    }
    //mode=0//reshte ro code mikonad
    //mode=1//decode
    /*public static String CodeDecode(String str, Integer mode) {
        String[] code1 = { "P#", "D#", "S#", "@#", "Q@", "F#", "G#", "H#", "J#", "W@", "E@", "-#", "_#", "K#", "=#",
                "L#", "Z#", "O#", "X#", "C#", "V#", "B#", "N#", "R@", "T@", "M#", "Y@", "U@", "I@", "O@", "Q#", "W#",
                "E#", "R#", "T#", "Y#", "U#","I#" };
        String[] code2 = { "م", "ي", "س", "ش", "ط", "ظ", "ر", "ذ", "د", "ع", "ض", "ص", "ث", "ح", "خ", "ه", "ج", "چ",
                "ل", "ا", "ب", "ت", "گ", "ک", "ف", "پ", "ق", "غ", "ن", "ز", "ژ", "و", "آ", "ة", "ئ", "ؤ", "‌أ" ,"ك"};
        int i;
        if (mode == 0) {
            for (i = 0; i < code2.length; i++) {
                str = str.replace(code2[i], code1[i]);
            }
            return str;
        } else {
            for (i = 0; i < code2.length; i++) {
                str = str.replace(code1[i], code2[i]);
            }
            return str;
        }
    }*/
    public static float pixelsToSp(Context context, float px)
    {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }
}

