package com.hegdeapps.quizapp

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * Created by usha on 3/11/17.
 * Draw a bar graph based on scores . code from androidhub4you
 */

class DrawGraph : Activity() {
    private var mTopicArray: Array<String>? = null
    private var mScoreArray: IntArray? = null
    //  private MySqliteHelper mHelper;
    private var mTotalQnsArray: IntArray? = null
    private val mReturnWithNoData: Boolean = false
    private var mNumQuestionArray: IntArray? = null
    private//mHelper.getTotalObjectiveQuestions(mTopicArray[i]);
    val scoreArray: Boolean
        get() {
            mScoreArray = IntArray(mTopicArray!!.size)
            mTotalQnsArray = IntArray(mTopicArray!!.size)
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            var emptyList = true
            for (i in mTopicArray!!.indices) {
                val str = mTopicArray!![i] + "Score"
                val score = pref.getInt(str, 0)
                mScoreArray!![i] = score
                if (mScoreArray!![i] > 0) {
                    emptyList = false
                }
                val numQns = mScoreArray!!.size
                mTotalQnsArray!![i] = numQns
            }
            return emptyList
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bargraph)

        /*here we are getting scores and topics for them from quiz activity*/
        val intent = intent
        mTopicArray = intent.getStringArrayExtra(CConstants.TOPICS_ARRAY)
        mScoreArray = intent.getIntArrayExtra(CConstants.SCORE_ARRAY)
        mNumQuestionArray = intent.getIntArrayExtra(CConstants.TOTAL_IN_TOPIC_ARRAY)
        createGraph()
        //    mHelper = new MySqliteHelper(this);
        /*   mTopicArray = CConstants.topicArray;
     if(!getScoreArray()) {
            createGraph();
        }else{
            mReturnWithNoData = true;
            setResult(CConstants.NO_GRAPH_DATA);
            this.finish();
        }*/
    }

    private fun createGraph() {
        val d = windowManager.defaultDisplay
        val p = Point()
        d.getSize(p)
        var width = p.x
        val height = p.y
        val margin = resources.getDimensionPixelSize(R.dimen.graph_margin).toFloat()
        width = width - 10//hard coded number is wrong.
        val widOfColumn = ((width - 2 * margin) / (mScoreArray!!.size - 1)).toInt()
        val factor = (height * 0.7 / 120).toFloat()

        val layout = findViewById<View>(R.id.parentlayout) as LinearLayout
        //    ViewGroup.LayoutParams params = layout.getLayoutParams();


        /*      LinearLayout layoutTemp = new LinearLayout(this);
        layoutTemp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT ));
        layout.setOrientation(LinearLayout.HORIZONTAL);*/
        for (i in 1 until mTopicArray!!.size) {
            val layoutTemp2 = RelativeLayout(this)
            layoutTemp2.layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // layoutTemp2.setOrientation(LinearLayout.VERTICAL);

            val tv = TextView(this)
            tv.setLines(2)
            tv.text = mTopicArray!![i]
            val relparams = RelativeLayout.LayoutParams(
                widOfColumn,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            relparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            tv.layoutParams = relparams//new LinearLayout.LayoutParams(widOfColumn,
            //ViewGroup.LayoutParams.WRAP_CONTENT ));
            val tvId = (i + 1) * 1000
            tv.id = tvId


            val view = View(this)
            view.setBackgroundColor(Color.BLUE)
            view.layoutParams =
                LinearLayout.LayoutParams(CConstants.BAR_WIDTH, mScoreArray!![i] * 20 + 10)
            //   LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view
            //           .getLayoutParams();
            // params.setMargins(3, 0, 0, 0); // substitute parameters for left,
            // top, right, bottom

            val tvBar = TextView(this)
            val relparamsBar = RelativeLayout.LayoutParams(
                25,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            relparamsBar.addRule(RelativeLayout.ABOVE, tvId)
            tvBar.layoutParams = relparamsBar
            val tvBarid = i * 888 + 888
            tvBar.id = tvBarid
            val percent = (100 * (mScoreArray!![i].toFloat() / mNumQuestionArray!![i])).toInt()

            tvBar.height = (percent * factor).toInt()
            tvBar.width = 20
            tvBar.gravity = Gravity.BOTTOM
            tvBar.setBackgroundColor(Color.MAGENTA)
            val tv2 = TextView(this)
            val relparams2 = RelativeLayout.LayoutParams(
                widOfColumn,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            relparams2.addRule(RelativeLayout.ABOVE, tvBarid)
            tv2.layoutParams = relparams2
            tv2.text = "$percent%"
            //  Drawable dr = createDrawable(percent+5);
            //     tv2.setCompoundDrawablesWithIntrinsicBounds(null,null,null,dr);

            tv2.gravity = Gravity.BOTTOM
            // tv.setLayoutParams(params);
            layoutTemp2.addView(tv2)

            layoutTemp2.addView(tvBar)
            layoutTemp2.addView(tv)
            //     layoutTemp2.setGravity(Gravity.BOTTOM);
            layout.addView(layoutTemp2)

        }
        /*  TextView tv3 = new TextView(this);
        tv3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT ));
        tv3.setText("\n\nYour Performance in Quiz");
        tv3.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);
        layout.addView(tv3);*/
        //    layout.addView(layoutTemp);
    }

    internal fun createDrawable(ht: Int): Drawable {
        val dr = ShapeDrawable()
        dr.paint.color = Color.BLUE
        dr.intrinsicWidth = 30
        dr.intrinsicHeight = ht * 2
        return dr
    }
}
