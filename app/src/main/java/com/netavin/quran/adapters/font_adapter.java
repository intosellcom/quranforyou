package com.netavin.quran.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netavin.quran.R;
import com.netavin.quran.activities.ahadis_matn;
import com.netavin.quran.activities.matn_template;
import com.netavin.quran.activities.qoran_template;
import com.netavin.quran.activities.qoranjoz_template;
import com.netavin.quran.classes.font_class;

import java.util.Vector;

/**
 * Created by mehdi on 24/06/2017.
 */
public class font_adapter extends RecyclerView.Adapter<font_adapter.ViewHolder>
{
    private Vector mDataset;
    Context con;

    int page=0;//
    font_class mf;
    SharedPreferences pref;
    SharedPreferences.Editor editor;


    matn_template me;
    ahadis_matn am;
    qoran_template qt;
    qoranjoz_template qzt;
    Vector radio_vec=new Vector();
    public font_adapter(Context con,Vector mDataset,int page)
    {
        this.con = con;
        this.mDataset=mDataset;
        this.page=page;
        mf=new font_class(con);
        pref = PreferenceManager.getDefaultSharedPreferences(con);
        editor = pref.edit();

        System.out.println("page="+page+"     ddddd");
        if(page==4||page==7||page==5||page==3||page==6||page==70||page==96)
        {
            this.me=(matn_template)con;
        }
        else if(page==100)
        {
            this.am=(ahadis_matn)con;
        }
        else if(page==1027||page==1021||page==200)
        {
            this.qt=(qoran_template)con;
        }
        else if(page==1030)
        {
            this.qzt=(qoranjoz_template)con;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        public TextView txtHeader;
        public ImageView radio_btn;
        public RelativeLayout rel;
        public View line;
        public ViewHolder(View v)
        {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.row_font_txt);
            radio_btn = (ImageView) v.findViewById(R.id.row_font_radio);
            rel = (RelativeLayout) v.findViewById(R.id.row_font_rel);
            line = (View) v.findViewById(R.id.row_font_line);
        }
    }
    @Override
    public font_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_font, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position)
    {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final String name = (String) mDataset.elementAt(position);
        holder.txtHeader.setText(mDataset.elementAt(position)+"");
        holder.txtHeader.setTypeface(mf.getYekan());


        holder.rel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(page==4)
                {
                    editor.putString("hekayat_font",name).commit();
                }
                else if(page==7)
                {
                    editor.putString("maqalat_font",name).commit();
                }
                else if(page==5)
                {
                    editor.putString("tajvid_font",name).commit();
                }
                else if(page==3)
                {
                    editor.putString("porseman_font",name).commit();
                }
                else if(page==6)
                {
                    editor.putString("moama_font",name).commit();
                }
                else if(page==100)
                {
                    editor.putString("hadis_font",name).commit();
                }
                else if(page==1027)
                {
                    if(qoran_template.is_sharh==false)
                    {
                        editor.putString("qoran_tr",name).commit();
                    }
                    else
                    {
                        editor.putString("qoran_tf",name).commit();
                        if(name.equals("تفسیر المیزان"))
                        {
                            editor.putInt("qoran_tf_code",1).commit();
                        }
                        else
                        {
                            editor.putInt("qoran_tf_code",2).commit();
                        }
                    }
                }
                else if(page==1030)
                {
                    if(qoranjoz_template.is_sharh==false)
                    {
                        editor.putString("qoran_tr",name).commit();
                    }
                    else
                    {
                        editor.putString("qoran_tf",name).commit();
                        if(name.equals("تفسیر المیزان"))
                        {
                            editor.putInt("qoran_tf_code",1).commit();
                        }
                        else
                        {
                            editor.putInt("qoran_tf_code",2).commit();
                        }
                    }
                }
                else if(page==200)
                {
                    String font_name=pref.getString("qoran_qary","منشاوي");
                    if(font_name.equals(name))
                    {
                        System.out.println("equals equals equals equals equals equals equals equals equals ");
                    }
                    else
                    {
                        System.out.println("not equal not equal not equal not equal not equal not equal not equal ");
                        qoran_template.is_besm_played=false;
                        qoran_template.cur_aye=1;
                        qoran_template.pos_sound_play=1;
                        qt.stopQaryMedia();
                    }
                    editor.putString("qoran_qary",name).commit();
                    if(name.equals("منشاوي"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"منشاوي");
                        editor.putInt("qoran_qary_code",1).commit();
                        ///////////////////////////editor.putString("qoran_qary_url","http://www.valiasr-aj.com/_mobile/quran/sound/Menshawi_16kbps/").commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Menshawi_16kbps/").commit();
                    }
                    else if(name.equals("سعدالغامدي"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"سعدالغامدي");
                        editor.putInt("qoran_qary_code",2).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Ghamadi_40kbps/").commit();
                    }
                    else if(name.equals("کريم منصوري"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"کريم منصوري");
                        editor.putInt("qoran_qary_code",3).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Karim_Mansoori_40kbps/").commit();
                    }
                    else if(name.equals("محمد ايوب"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"محمد ايوب");
                        editor.putInt("qoran_qary_code",4).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Muhammad_Ayyoub_128kbps/").commit();
                    }
                    else if(name.equals("ماهر المعيقلي"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"ماهر المعيقلي");
                        editor.putInt("qoran_qary_code",5).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/MaherAlMuaiqly128kbps/").commit();
                    }
                    else if(name.equals("محمد الطبلاوي"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"محمد الطبلاوي");
                        editor.putInt("qoran_qary_code",6).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Mohammad_al_Tablaway_128kbps/").commit();
                    }
                    else if(name.equals("هاني الرفاعي"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"هاني الرفاعي");
                        editor.putInt("qoran_qary_code",7).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Hani_Rifai_192kbps/").commit();
                    }
                    else if(name.equals("ابراهيم الاخضر"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"ابراهيم الاخضر");
                        editor.putInt("qoran_qary_code",8).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Ibrahim_Akhdar_32kbps/").commit();
                    }
                    else if(name.equals("پرهيزگار"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"پرهيزگار");
                        editor.putInt("qoran_qary_code",9).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Parhizgar_48kbps/").commit();
                    }
                    else if(name.equals("عبدالباسط"))
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"عبدالباسط");
                        editor.putInt("qoran_qary_code",10).commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Abdul_Basit_Mujawwad_128kbps/").commit();
                    }
                    else
                    {
                        System.out.println("namenamenamenamenamenamenamenamenamenamenamename="+"else");
                        editor.putInt("qoran_qary_code",1).commit();
                        ///////////////////////////////editor.putString("qoran_qary_url","http://www.valiasr-aj.com/_mobile/quran/sound/Menshawi_16kbps/").commit();
                        editor.putString("qoran_qary_url","http://dsfgr5qquran44s3ryzalq.netavin.com/quran/Menshawi_16kbps/").commit();
                    }
                }

                else if(page==96)//sharh_qoran
                {
                    editor.putString("sharh_qoran_font",name).commit();
                }
                else if(page==70)//about
                {
                    editor.putString("about_font",name).commit();
                }


                new Handler().postDelayed(new Thread(){
                    @Override
                    public void run()
                    {
                        if(page==4||page==7||page==5||page==3||page==6||page==70)
                        {
                            me.dialog.dismiss();
                            me.setfont();
                        }
                        else if(page==100)
                        {
                            am.dialog.dismiss();
                            am.setfont();
                        }
                        else if(page==1027)
                        {
                            qt.dialog.dismiss();
                            if(qoran_template.is_sharh==false)
                            {
                                qt.setBoard();
                            }
                        }
                        else if(page==1030)
                        {
                            qzt.dialog.dismiss();
                            if(qoranjoz_template.is_sharh==false)
                            {
                                qzt.setBoard();
                            }
                        }
                        else if(page==200)
                        {
                            qt.dialog.dismiss();
                            //
                        }
                        else if(page==96)
                        {
                            me.dialog.dismiss();
                            me.setfont();
                        }

                    }
                }, 500);

            }
        });


        if(page==4)
        {
            String font_name=pref.getString("hekayat_font","یکان");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }
        else if(page==7)
        {
            String font_name=pref.getString("maqalat_font","یکان");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }
        else if(page==5)
        {
            String font_name=pref.getString("tajvid_font","یکان");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }
        else if(page==3)
        {
            String font_name=pref.getString("porseman_font","یکان");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }
        else if(page==6)
        {
            String font_name=pref.getString("moama_font","یکان");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }
        else if(page==100)
        {
            String font_name=pref.getString("hadis_font","یکان");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }
        else if(page==1027)
        {
            if(qoran_template.is_sharh==false)
            {
                String font_name=pref.getString("qoran_tr","ناصر مکارم شیرازی");
                if(font_name.equals(name))
                {
                    holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                }
                else
                {
                    holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                }
            }
            else
            {
                String font_name=pref.getString("qoran_tf","تفسیر نمونه");
                if(font_name.equals(name))
                {
                    holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                }
                else
                {
                    holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                }
            }
        }
        else if(page==1030)
        {
            if(qoranjoz_template.is_sharh==false)
            {
                String font_name=pref.getString("qoran_tr","ناصر مکارم شیرازی");
                if(font_name.equals(name))
                {
                    holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                }
                else
                {
                    holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                }
            }
            else
            {
                String font_name=pref.getString("qoran_tf","تفسیر نمونه");
                if(font_name.equals(name))
                {
                    holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                }
                else
                {
                    holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
                }
            }
        }
        else if(page==200)
        {
            String font_name=pref.getString("qoran_qary","منشاوي");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }

        else if(page==96)
        {
            String font_name=pref.getString("sharh_qoran_font","سلطان");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }
        else if(page==70)
        {
            String font_name=pref.getString("about_font","یکان");
            if(font_name.equals(name))
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }
            else
            {
                holder.radio_btn.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);
            }
        }

        radio_vec.add(holder.radio_btn);

        if(position==(getItemCount()-1))
        {
            holder.line.setVisibility(View.INVISIBLE);
        }
        else
        {
            holder.line.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
