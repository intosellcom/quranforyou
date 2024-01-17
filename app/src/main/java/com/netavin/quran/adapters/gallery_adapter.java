package com.netavin.quran.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.netavin.quran.R;

import java.util.Vector;
import androidx.appcompat.widget.*;

/**
 * Created by mehdi on 06/11/2018.
 */
public class gallery_adapter extends RecyclerView.Adapter<gallery_adapter.ViewHolder>
{
    private Vector mDataset;
    Context con;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    public gallery_adapter(Vector myDataset,Context con) {
        mDataset = myDataset;
        this.con=con;
        pref = PreferenceManager.getDefaultSharedPreferences(con);
        editor = pref.edit();

    }
    @Override
    public gallery_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_gallery, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Vector temp=new Vector();
        String link= mDataset.elementAt(position)+"";

        //holder.img.setImageResource(id);
        /*Ion.with(holder.img)
                .load(link);*/

        holder.card.setTag(temp);


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onclick(holder.card);
            }
        });
    }


    private void onclick(CardView currel)
    {
        int is_bought= pref.getInt("is_bought", 0);//0=no  1=yes

        Vector temp=new Vector();





    }
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public RelativeLayout rel;
        public CardView card;

        public ViewHolder(View v) {
            super(v);
            img = (ImageView) v.findViewById(R.id.row_gallery_img);
            rel = (RelativeLayout) v.findViewById(R.id.row_gallery_rel);
            card = (CardView) v.findViewById(R.id.row_gallery_card);
            card=v.findViewById(R.id.row_gallery_card);
        }
    }
}
