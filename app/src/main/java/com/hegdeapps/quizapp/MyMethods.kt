package com.hegdeapps.quizapp

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.preference.PreferenceManager

/**
 * Created by usha on 17-11-2015.
 * Common methods used in the project
 * Methods are getFontSizeFromPreferences(context)
 */
internal object MyMethods {
    fun getFontSizeFromPreferences(ctx: Context): String? {
        val pref = PreferenceManager.getDefaultSharedPreferences(ctx)
        return pref.getString("font_size", CConstants.MEDIUM_TEXT)
    }

    fun showTextInDialog(ctx: Context, dialog_text: String, title: String) {


        val builder = AlertDialog.Builder(ctx)
        builder.setTitle(title)
        builder.setMessage(dialog_text)
        builder.setPositiveButton("OK", object : DialogInterface.OnClickListener  {
            override
            fun onClick(dialog: DialogInterface, which: Int) {
                dialog.dismiss()
            }
        })

        val d = builder.create()
        d.show()


    }

    fun saveFontSizeToPreference(ctx: Context, fontsize: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(ctx)
        val _editor = pref.edit()
        val value = fontsize.toString()
        _editor.putString("font_size", value)
        _editor.commit()
    }


    /*public static Typeface getTypefaceFromPref(Context ctx) {
          SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
          String str = pref.getString(CConstants.TYPEFACE,null);
         Typeface tp = null;
          if(null == str)
              tp = null;

          else  if( str.equals(CConstants.SERIF))
                tp = Typeface.SERIF;
             // return tp;
          else
             tp = Typeface.createFromAsset(ctx.getAssets(),str);
     //   if(null != tp) {
            return tp;
      //  }

    }*/
}