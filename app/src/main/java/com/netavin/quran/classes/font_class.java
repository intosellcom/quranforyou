package com.netavin.quran.classes;


/**
 * Created by mehdi on 29/08/2016.
 */

import android.content.Context;
import android.graphics.Typeface;

public class font_class {
    String font_adobe = "adobe.ttf";
    String font_adobebold = "AdobeArabic-Bold.ttf";
    String font_qalam = "Al Qalam New.ttf";
    String font_nabi = "arabicNabi.ttf";
    String font_neyrizi = "arabicNeirizi.ttf";
    String font_nazaninbold = "B Nazanin Bold habl.ttf";
    String font_badr = "BBadr.ttf";
    String font_nazanin_habl = "BNAZANIN habl.TTF";
    String font_naskh = "DroidNaskh.ttf";
    String font_entezar = "Entezar.TTF";
    String font_nazanin_bayan = "Nazanin_bayan.ttf";
    String font_qoran = "QuranFont.ttf";
    String font_sultan = "sultan.ttf";
    String font_tahrir = "Tahrir.ttf";
    String font_uthman2 = "Uthman2.otf";
    String font_uthmanic = "Uthmanic.otf";
    String font_yekan = "Yekan.ttf";
    String font_zar = "ZAR New.ttf";


    Typeface face;
    Context context;

    public font_class(Context context) {
        this.context = context;
    }

    public Typeface getAdobe() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_adobe);
        return face;
    }

    public Typeface getAdobeBold() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_adobebold);
        return face;
    }

    public Typeface getQalam() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_qalam);
        return face;
    }

    public Typeface getNabi() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_nabi);
        return face;
    }

    public Typeface getNeyrizi() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_neyrizi);
        return face;
    }

    public Typeface getNazaninBold() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_nazaninbold);
        return face;
    }

    public Typeface getBadr() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_badr);
        return face;
    }

    public Typeface getNazaninHabl() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_nazanin_habl);
        return face;
    }

    public Typeface getNaskh() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_naskh);
        return face;
    }

    public Typeface getEntezar() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_entezar);
        return face;
    }

    public Typeface getNazaninBayan() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_nazanin_bayan);
        return face;
    }

    public Typeface getQoran() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_qoran);
        return face;
    }

    public Typeface getSultan() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_sultan);
        return face;
    }

    public Typeface getTahrir() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_tahrir);
        return face;
    }

    public Typeface getUthman2() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_uthman2);
        return face;
    }

    public Typeface getUthmanic() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_uthmanic);
        return face;
    }

    public Typeface getYekan() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_yekan);
        return face;
    }

    public Typeface getZar() {
        face = Typeface.createFromAsset(context.getAssets(), "font/" + font_zar);
        return face;
    }

    public Typeface getFace(String item) {
        if (item.equals("قلم")) {
            face = getQalam();
        } else if (item.equals("قرآن")) {
            face = getQoran();
        } else if (item.equals("نازنین")) {
            face = getNazaninHabl();
        } else if (item.equals("سلطان")) {
            face = getSultan();
        } else if (item.equals("زر")) {
            face = getZar();
        } else if (item.equals("یکان")) {
            face = getYekan();
        } else if (item.equals("بدر")) {
            face = getBadr();
        }

        return face;
    }
    /*public int getSize()
    {
        int size=0;
        SharedPreferences pref;
        SharedPreferences.Editor editor;

        pref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = pref.edit();

        //1=kam    2=motevaset   3=zyad
        int curSize=pref.getInt("size",2);
        if(curSize==1)
        {
            size=-2;
        }
        else if(curSize==2)
        {
            size=0;
        }
        else if(curSize==3)
        {
            size=2;
        }
        return size;
    }*/
}