package com.hegdeapps.quizapp

import android.content.Context
import android.database.Cursor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cursoradapter.widget.CursorAdapter

/**
 * Created by usha on 11/9/17.
 */

class ObjectiveCursorListAdapter(internal var mCtx: Context, //  Typeface mTypeface;
                                 internal var mCursor: Cursor) : CursorAdapter(mCtx, mCursor) {
    private val mTextSize: String

    init {
        //  mTypeface = tp;
        mTextSize = MyMethods.getFontSizeFromPreferences(mCtx).toString()
    }

    @Override
    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(mCtx)
        return inflater.inflate(R.layout.obj_qn_list_row_new, null)
    }

    @Override
    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val tvQuestion = view.findViewById(R.id.question) as TextView
        val tvAns1 = view.findViewById(R.id.ans1) as TextView
        val tvAns2 = view.findViewById(R.id.ans2) as TextView
        val tvAns3 = view.findViewById(R.id.ans3) as TextView
        val tvAns4 = view.findViewById(R.id.ans4) as TextView
        val tvQuestionCode = view.findViewById<TextView>(R.id.questionocde)
        val question = cursor.getString(cursor.getColumnIndex("question"))
        val questionCode = cursor.getString(cursor.getColumnIndex("questioncode"))
        val answer1 = cursor.getString(cursor.getColumnIndex("answer1"))
        val answer2 = cursor.getString(cursor.getColumnIndex("answer2"))
        val answer3 = cursor.getString(cursor.getColumnIndex("answer3"))
        val answer4 = cursor.getString(cursor.getColumnIndex("answer4"))
        //    final  int id = cursor.getInt(cursor.getColumnIndex("_id"));
        /*   if(null != mTypeface){
            tvQuestion.setTypeface(mTypeface);
            tvAns1.setTypeface(mTypeface);
            tvAns2.setTypeface(mTypeface);
            tvAns3.setTypeface(mTypeface);
            tvAns4.setTypeface(mTypeface);
        }
        int textSize ;
        if (mTextSize.equals(CConstants.LARGE_TEXT)){
            textSize = 22;//android.R.style.TextAppearance_Large;
        } else if (mTextSize.equals(CConstants.SMALL_TEXT)){
            textSize = 14;//android.R.style.TextAppearance_Medium;
        }else{
            textSize =18;// android.R.style.TextAppearance_Small;
        }

            tvQuestion.setTextSize(textSize);
            tvAns1.setTextSize(textSize);
            tvAns2.setTextSize(textSize);
            tvAns3.setTextSize(textSize);
            tvAns4.setTextSize(textSize);*/

        val p = cursor.getPosition() + 1
        if (null != question) {
            tvQuestion.setText(p.toString() + "    " + question)
        }
        if (questionCode != null) {
            tvQuestionCode.setVisibility(View.VISIBLE)
            tvQuestionCode.setText(questionCode)
        } else {
            tvQuestionCode.setVisibility(View.GONE)
        }
        if (null != answer1) {
            tvAns1.setText("1)" + answer1!!)
        }
        if (null != answer2) {
            tvAns2.setText("2)" + answer2!!)
        }
        if (null != answer3) {
            tvAns3.setText("3)" + answer3!!)
        }
        if (null != answer4) {
            tvAns4.setText("4)" + answer4!!)
        }
        //let us remove favorites from here and put it only in details screen - uh 27/3/18
        /*       final ImageButton btnBookMarked = (ImageButton)view.findViewById(R.id.favorites);
        final int isRowBookmarked = cursor.getInt(cursor.getColumnIndex("Bookmarked"));
        if(1 == isRowBookmarked){
            btnBookMarked.setImageDrawable(context.getResources().getDrawable(R.drawable.favon));
        }else{
            btnBookMarked.setImageDrawable(context.getResources().getDrawable(R.drawable.favoff));
        }
        btnBookMarked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (1 != isRowBookmarked){
                    addQnToBooks(id,context);
                    btnBookMarked.setImageDrawable(context.getResources().getDrawable(R.drawable.favon));
                } else {
                    removeQnToBooks(id,context);
                    btnBookMarked.setImageDrawable(context.getResources().getDrawable(R.drawable.favoff));
                }
            }
        });*/
    }

    private fun numberLinesIn(str: String): Int {
        var num = 0
        for (i in 0 until str.length )
            if (str[i] === '\n')
                num++
        return num
    }

    /*   private void removeQnToBooks(int id,Context c) {
        MySqliteHelper helper = new MySqliteHelper(c);
        helper.removeObjQnsFromFavs(id);
        Toast.makeText(c,"Question removed from favorites....",Toast.LENGTH_SHORT).show();

    }

    private void addQnToBooks(int id,Context c) {
        MySqliteHelper helper = new MySqliteHelper(c);
        helper.addObjQnsToFavs(id);
        Toast.makeText(c,"Question bookmarked....",Toast.LENGTH_SHORT).show();
    }*/


}
