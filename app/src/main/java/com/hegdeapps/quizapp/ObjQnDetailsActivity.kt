package com.hegdeapps.quizapp

import android.app.AlertDialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/*
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
*/
/*

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
*/

import java.util.ArrayList

/**
 * Created by usha on 12/9/17.
 */

class ObjQnDetailsActivity : AppCompatActivity(), View.OnClickListener {
    private val mTextMessage: TextView? = null
    private var mCursor: Cursor? = null
    private var tvQuestion: TextView? = null
    private val tvAnswer: TextView? = null
    private val scAnswer: ScrollView? = null
    private val btnShowAnswer: Button? = null
    private var tvQuestionCode: TextView? = null
    private var btnAnswer1: TextView? = null
    private var btnAnswer2: TextView? = null
    private var btnAnswer3: TextView? = null
    private var btnAnswer4: TextView? = null
    private var mCorrectAnswerNum: Int = 0
    private var mExplanation: String? = null
    private val mFirstAttempt: Boolean = false
    private var mScore: Int = 0
    // private Typeface mTypeface;
    //  private boolean mReviewMode;
    private val mNewQuestionStarted: Boolean = false
    private var mAnswersList: MutableList<AnswerObject>? = null
    private var mNextActionStarted: Boolean = false
    //  private CountDownTimer mTimer;
    private var mTopic: String? = null
    private var mGreenDrawable: Drawable? = null
    private var mRedDrawable: ColorDrawable? = null
    private var btnExplain: Button? = null
    private var mDisableMode: Boolean = false
    // private HashMap<Integer, Integer> mHashMap;
    private var mAnswerBackground: Int = 0
    private var mSqliteHelper: MySqliteHelper? = null
    private var mIsFavorites: Boolean = false
    private var mFilterText: String? = null
    private var mWrong: Int = 0
    //private AdView mAdView;
    private var mCorrect: Int = 0
    private val mbackgroundResource: Int = 0
   /* private var mAdView: AdView? = null
*/
    val currentRow: Int
        get() = if (null != mCursor) {
            mCursor!!.position
        } else {
            -1
        }


      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_obj_question_details)
        val intent = getIntent()
        mTopic = intent.getStringExtra(CConstants.TOPIC)
        val pos = intent.getIntExtra(CConstants.CURSOR_POSITION, -1)
        if (-1 == pos) {
            this.finish()
        }
        val str = intent.getStringExtra(CConstants.FILTER_TEXT)
        if (str != null) {
            mFilterText = str
        }
        val tb = findViewById(R.id.toolbar) as Toolbar
        if (null != tb) {
            tb!!.setTitle(mTopic)
            setSupportActionBar(tb)
            val ab = getSupportActionBar()
            ab?.setDisplayHomeAsUpEnabled(true)

        }
        mSqliteHelper = MySqliteHelper(this)
        mIsFavorites = intent.getBooleanExtra(CConstants.FAVORITES, false)
        if (mFilterText != null && !TextUtils.isEmpty(mFilterText)) {
            mCursor = mSqliteHelper!!.getFilteredObjQuestions(mTopic!!, mFilterText!!)
        } else {
            if (mIsFavorites) {
                mCursor = mSqliteHelper!!.favoriteObjQns
            } else {
                mCursor = mSqliteHelper!!.getObjQuestionOfTopic(mTopic!!)
            }
        }
        if (!mCursor!!.moveToFirst()) {
            this.finish()
        }
        if (!mCursor!!.moveToPosition(pos)) {
            finish()
        }
        //  mTypeface =MyMethods.getTypefaceFromPref(this);
        mGreenDrawable = ColorDrawable(Color.GREEN)
        mRedDrawable = ColorDrawable(Color.RED)
        val btnFav = findViewById(R.id.favorites) as ImageButton
        btnFav.setOnClickListener {
            val id = mCursor!!.getInt(mCursor!!.getColumnIndex("_id"))
            val isRowBookmarked = isBookmarked(id)
            if (isRowBookmarked) {
                removeQnToBooks(id)
                btnFav.setImageDrawable(getResources().getDrawable(R.drawable.favoff))
            } else {
                addQnToBooks(id)
                btnFav.setImageDrawable(getResources().getDrawable(R.drawable.favon))
            }
        }
        /*  int []attrs=new int[]{R.attr.selectableItemBackgroundBorderless};
        TypedArray typedArray = this.obtainStyledAttributes(attrs);
        mbackgroundResource = typedArray.getResourceId(0,0);*/
        /*  Button btnResults = (Button)findViewById(R.id.result);
        btnResults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEndDialog();
            }
        });
*/

       /* mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)*/
        initQns()
        showQuestion()

    }


    private fun showQuestion() {
        setTitle(mTopic)
        /* tvQuestion.setTypeface(mTypeface);
        btnAnswer1.setTypeface(mTypeface);
        btnAnswer2.setTypeface(mTypeface);
        btnAnswer3.setTypeface(mTypeface);
        btnAnswer4.setTypeface(mTypeface);*/

        val btnPrevious = findViewById(R.id.prev) as Button
        btnPrevious.setOnClickListener { gotoPreviousQuestion() }
        val nextButton = findViewById(R.id.next) as Button
        nextButton.setOnClickListener {
            gotoNextQuestion()
            mDisableMode = false
        }
        showCurrentQuestion()
    }

    private fun gotoPreviousQuestion() {
        mCursor!!.moveToPrevious()
        if (!mCursor!!.isBeforeFirst) {
            showCurrentQuestion()
        }
    }

    /* private boolean isVisitedQuestion() {
        int pos = mCursor.getPosition();
        return mHashMap.containsKey(pos);
    }*/

    private fun showAnswerScroll(answer: String) {
        scAnswer!!.visibility = View.VISIBLE
        tvAnswer!!.text = answer
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            /*   case R.id.action_solution :showExplanation(mExplanation);
                   break;*/
            R.id.action_share -> showShareDialog()
        }
        return false
    }

    private fun showShareDialog() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
        val question = mCursor!!.getString(mCursor!!.getColumnIndex("question"))
        val answer1 = mCursor!!.getString(mCursor!!.getColumnIndex("answer1"))
        val answer2 = mCursor!!.getString(mCursor!!.getColumnIndex("answer2"))
        val answer3 = mCursor!!.getString(mCursor!!.getColumnIndex("answer3"))
        val answer4 = mCursor!!.getString(mCursor!!.getColumnIndex("answer4"))
        val shareString = question + "\n1)" +
                answer1 + "\n2)" + answer2 + "\n3)" + answer3 + "\n4)" + answer4 + "\n\n" +
                "For such solved questions download the app " +
                CConstants.LINK_TO_APP
        intent.putExtra(Intent.EXTRA_TEXT, shareString)
        startActivity(Intent.createChooser(intent, "Share via"))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    /*   void disableAnswers()
        {
            btnAnswer1.setEnabled(false);
            btnAnswer2.setEnabled(false);
            btnAnswer3.setEnabled(false);
            btnAnswer4.setEnabled(false);
        }
    */
    private fun showCurrentQuestion() {
        mNextActionStarted = true
        val question = mCursor!!.getString(mCursor!!.getColumnIndex("question"))
        val questionCode = mCursor!!.getString(mCursor!!.getColumnIndex("questioncode"))
        val answer1 = mCursor!!.getString(mCursor!!.getColumnIndex("answer1"))
        val answer2 = mCursor!!.getString(mCursor!!.getColumnIndex("answer2"))
        var answer3: String? = mCursor!!.getString(mCursor!!.getColumnIndex("answer3"))
        var answer4: String? = mCursor!!.getString(mCursor!!.getColumnIndex("answer4"))
        mCorrectAnswerNum = mCursor!!.getInt(mCursor!!.getColumnIndex("correctanswer"))

        //TextView tvQn = (TextView)getView().findViewById(R.id.question);
        val questionNumber = mCursor!!.position + 1
        val totQns = mCursor!!.count
        val tvQnNum = findViewById(R.id.qnnum) as TextView
        val qnNum = "$questionNumber/$totQns"
        tvQnNum.text = qnNum
        tvQuestion!!.text = question

        if (questionCode != null && !TextUtils.isEmpty(questionCode)) {
            tvQuestionCode!!.visibility = View.VISIBLE
            tvQuestionCode!!.text = questionCode
        } else {
            tvQuestionCode!!.visibility = View.GONE
        }

        if (answer3 != null)
            answer3 = answer3.trim { it <= ' ' }
        if (answer4 != null)
            answer4 = answer4.trim { it <= ' ' }

        btnAnswer1!!.text = answer1
        btnAnswer2!!.text = answer2

        if (TextUtils.isEmpty(answer3)) {
            btnAnswer3!!.visibility = View.INVISIBLE
        } else {
            btnAnswer3!!.visibility = View.VISIBLE
            btnAnswer3!!.text = answer3
        }
        if (TextUtils.isEmpty(answer4)) {
            btnAnswer4!!.visibility = View.INVISIBLE
        } else {
            btnAnswer4!!.visibility = View.VISIBLE
            btnAnswer4!!.text = answer4
        }
        /*  btnAnswer1.setBackgroundDrawable(null);
        btnAnswer2.setBackgroundDrawable(null);
        btnAnswer3.setBackgroundDrawable(null);
        btnAnswer4.setBackgroundDrawable(null);*/
        //btnAnswer3.setBackgroundResource(mbackgroundResource);
        btnAnswer1!!.setTextColor(Color.WHITE)
        btnAnswer2!!.setTextColor(Color.WHITE)
        btnAnswer3!!.setTextColor(Color.WHITE)
        btnAnswer4!!.setTextColor(Color.WHITE)
        val btnPrev = findViewById(R.id.prev) as Button
        val btnNext = findViewById(R.id.next) as Button
        val pos = mCursor!!.position
        val count = mCursor!!.count
        if (pos == 0) {
            btnPrev.isEnabled = false
        } else {
            btnPrev.isEnabled = true
        }
        //    Button btnResult = (Button)findViewById(R.id.result);
        if (pos >= count - 1) {
            //     btnResult.setVisibility(View.VISIBLE);
            btnNext.isEnabled = false
        } else {
            //    btnResult.setVisibility(View.GONE);
            btnNext.isEnabled = true
        }
        /*is it bookmarked?*/
        val btnFav = findViewById(R.id.favorites) as ImageButton
        //   final int isRowBookmarked = mCursor.getInt(mCursor.getColumnIndex("Bookmarked"));
        val id = mCursor!!.getInt(mCursor!!.getColumnIndex("_id"))
        if (isBookmarked(id)) {
            btnFav.setImageDrawable(getResources().getDrawable(R.drawable.favon))
        } else {
            btnFav.setImageDrawable(getResources().getDrawable(R.drawable.favoff))
        }
        /** */

        val strExplain = mCursor!!.getString(mCursor!!.getColumnIndex(CConstants.EXPLAIN))
        btnExplain = findViewById(R.id.explain) as Button
        if (TextUtils.isEmpty(strExplain)) {
            btnExplain!!.visibility = View.GONE
        } else {
            btnExplain!!.visibility = View.VISIBLE
            btnExplain!!.setOnClickListener(this)
            mExplanation = strExplain
        }
        val a = mAnswersList!![pos]
        if (a != null) {
            val answerNum = a.yourAnswerNum
            if (answerNum == mCorrectAnswerNum)
                showCorrectInGreen(answerNum)
            else if (answerNum != 0)
                showWrongAnswerInRed(answerNum)
        }
        // mFirstAttempt = true;
    }

    /*  private void enableAnswers() {
        btnAnswer1.setEnabled(true);
        btnAnswer2.setEnabled(true);
        btnAnswer3.setEnabled(true);
        btnAnswer4.setEnabled(true);
    }*/

    /*@Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = getActivity();*/
    internal fun initQns() {
        mAnswerBackground = R.drawable.answerbg
        // ObjQnListActivity act = (ObjQnListActivity) mActivity;
        //   mCursor = act.mCursor;
        val numQuestions = mCursor!!.count
        mAnswersList = ArrayList(numQuestions)
        /*clear all answers */
        for (i in 0 until numQuestions) {
            val obj = AnswerObject()
            obj.correctAnswerNum = 0
            obj.yourAnswerNum = 0
            mAnswersList!!.add(obj)
        }
        //     mHashMap = new HashMap<Integer, Integer>(numQuestions);
        /*initialize hashmap to all zeroes*/
        //    for(int i=0;i<numQuestions;i++)
        //       mHashMap.put(i,0);
        //    Bundle b = getArguments();
        //        int pos = b.getInt(CConstants.CURSOR_POSITION);
        //   if (pos<0)
        //        return;
        tvQuestion = findViewById(R.id.question) as TextView
        tvQuestionCode = findViewById(R.id.questioncode) as TextView
        //    tvQuestionCode = (TextView)   findViewById(R.id.questioncode);
        btnAnswer1 = findViewById(R.id.answer1) as TextView
        btnAnswer2 = findViewById(R.id.answer2) as TextView
        btnAnswer3 = findViewById(R.id.answer3) as TextView
        btnAnswer4 = findViewById(R.id.answer4) as TextView
        btnExplain = findViewById(R.id.explain) as Button
        btnAnswer1!!.setOnClickListener(this)
        btnAnswer2!!.setOnClickListener(this)
        btnAnswer3!!.setOnClickListener(this)
        btnAnswer4!!.setOnClickListener(this)
        btnExplain!!.setOnClickListener(this)
        btnAnswer1!!.setBackgroundResource(mAnswerBackground)
        btnAnswer2!!.setBackgroundResource(mAnswerBackground)
        btnAnswer3!!.setBackgroundResource(mAnswerBackground)
        btnAnswer4!!.setBackgroundResource(mAnswerBackground)
        /* if(!mCursor.isClosed()) {
        //    mCursor.moveToPosition(pos);
       //     mTopic = mCursor.getString(mCursor.getColumnIndex("topic"));
            showQuestion();
        }*/
        mScore = 0
    }

    /*   @Override
    public void onDetach() {
        super.onDetach();
        storeScoreInPref();
    }*/

    override fun onStop() {
        super.onStop()
        /*   if(mTimer!=null ){
            mTimer.cancel();
        }*/

    }

    override fun onClick(v: View) {
        val id = v.id
        val answerNum: Int
        val correctAnswer: Boolean
        when (id) {
            R.id.answer1 -> {
                answerNum = 1
                correctAnswer = 1 == mCorrectAnswerNum
                processAnswer(correctAnswer, answerNum)
            }
            R.id.answer2 -> {
                answerNum = 2
                correctAnswer = 2 == mCorrectAnswerNum
                processAnswer(correctAnswer, answerNum)
            }
            R.id.answer3 -> {
                answerNum = 3
                correctAnswer = 3 == mCorrectAnswerNum
                processAnswer(correctAnswer, answerNum)
            }
            R.id.answer4 -> {
                answerNum = 4
                correctAnswer = 4 == mCorrectAnswerNum
                processAnswer(correctAnswer, answerNum)
            }
            R.id.explain -> showExplanation(mExplanation)
        }

    }

    private fun processAnswer(correctAnswer: Boolean, answerNum: Int) {
        showCorrectInGreen(mCorrectAnswerNum)
        showToast(correctAnswer)
        if (!correctAnswer) {
            //  mWrong++;
            showWrongAnswerInRed(answerNum)
        }/*else{
           // mScore++;
        }*/
        val pos = mCursor!!.position
        val obj = AnswerObject()
        obj.yourAnswerNum = answerNum
        obj.correctAnswerNum = mCorrectAnswerNum
        mAnswersList!![pos] = obj
        /*  AnswerObject answerObj = new AnswerObject();
        answerObj.correctAnswerNum = mCorrectAnswerNum;
        answerObj.yourAnswerNum = answerNum;*/

        /*AnswerObject obj = new AnswerObject();
        obj.correctAnswerNum = mCorrectAnswerNum;
        obj.yourAnswerNum = answerNum;
        mAnswersList.set(pos,obj);*/
        //    mHashMap.put(pos,answerNum);
        /*  answerObj.cursorPosition = pos;//TODO may be we dont need this fiedls after using hashmap
        mAnswersList.add(answerObj);
        mHashMap.put(pos, answerObj);*/
        //   disableAnswers();
        //enableExplain();
    }

    /* private void enableExplain() {
        if(btnExplain.getVisibility()==View.VISIBLE){
            btnExplain.setEnabled(true);
        }
    }
    private void disableExplain() {
        if(btnExplain.getVisibility()==View.VISIBLE){
            btnExplain.setEnabled(false);
        }
    }*/

    private fun showToast(correctAnswer: Boolean) {
        if (correctAnswer) {
            Toast.makeText(this, "Correct Answer!!", Toast.LENGTH_SHORT).show()
        }// Snackbar.make(tvQuestion,"Correct Answer!!",Snackbar.LENGTH_SHORT).show();
        else {
            Toast.makeText(this, "Wrong Answer!!", Toast.LENGTH_SHORT).show()
        }
        //  Snackbar.make(tvQuestion,"Wrong Answer. :(",Snackbar.LENGTH_SHORT).show();

    }

    /* private void showScore() {
        TextView tvScore = (TextView) findViewById(R.id.score);
        String str = "Score :"+mScore;
        tvScore.setText(str);
    }*/

    /* private void showCountDownTimer() {
        CountDownTimer timer = new CountDownTimer(3000,3000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }
            @Override
            public void onFinish() {
                if(!mNextActionStarted)//if another question started because of user click dont start again
                gotoNextQuestion();
            }
        };
        mNextActionStarted = false;
        timer.start();
        mTimer = timer;
    }*/

    /**
     *
     * shows next question.
     */
    private fun gotoNextQuestion() {
        mCursor!!.moveToNext()
        if (!mCursor!!.isAfterLast) {
            showCurrentQuestion()
        }/*else{
          //  if(!mReviewMode)
            showEndDialog();
        }
*/
    }

    /* private void showEndDialog() {
        mNextActionStarted = true;
        processAnswerList();
        storeScoreInPref();
       *//* if(mAnswersList.size()<mCursor.getCount()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You have not attempted some questions. Do you want to answer them?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    gotoFirstQuestion();
                }
            });
        *//**//*    builder.setNeutralButton("Review Answers", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reviewAnswers();
                }
            });*//**//*
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return;
        }*//*
        //also store score in sharedpref
        int unAttempted = mCursor.getCount()  - (mCorrect+mWrong);
        AlertDialog.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog);
        }else{
            builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_Light_Dialog);
        }

        builder.setMessage("Correct answers: "+mCorrect
           +"\nWrong answers: "+mWrong+
        "\nUnattempted questions:"+unAttempted).setTitle("Result");
     *//*   builder.setPositiveButton("Review Answers", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reviewAnswers();
            }
        });*//*
        builder.setNeutralButton("Repeat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gotoFirstQuestion();
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finish();
            }
        });
        builder.show();
    }
*/
    private fun processAnswerList() {
        mCorrect = 0
        mWrong = 0
        var unatt = 0
        for (i in 0 until mCursor!!.count) {
            val obj = mAnswersList!![i]
            val correctAnswerNum = obj.correctAnswerNum
            val answerNum = obj.yourAnswerNum
            if (answerNum == 0) {
                unatt++
            } else if (correctAnswerNum == answerNum) {
                mCorrect++
            } else {
                mWrong++
            }
        }
    }

    private fun storeScoreInPref() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        val numQuestionsAttempted = mCorrect + mWrong
        val percent = (mCorrect.toFloat() / numQuestionsAttempted * 100).toInt()
        val scoreString = mTopic!! + "Score"
        editor.putInt(scoreString, percent)
        editor.commit()
    }


    private fun gotoFirstQuestion() {
        mCursor!!.moveToFirst()
        showCurrentQuestion()
    }

    private fun showWrongAnswerInRed(answerNum: Int) {
        var tv: TextView? = null
        when (answerNum) {
            1 -> tv = btnAnswer1
            2 -> tv = btnAnswer2
            3 -> tv = btnAnswer3
            4 -> tv = btnAnswer4
        }
        tv?.setTextColor(Color.RED)
        //tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.redbutton));
    }

    private fun showCorrectInGreen(num: Int) {
        var tv: TextView? = null
        when (num) {
            1 -> tv = btnAnswer1
            2 -> tv = btnAnswer2
            3 -> tv = btnAnswer3
            4 -> tv = btnAnswer4
        }
        tv?.setTextColor(Color.GREEN)
        // tv.setBackgroundDrawable(getResources().getDrawable(R.drawable.greenbutton));
    }

    private fun showExplanation(explanation: String?) {
        val bldr = AlertDialog.Builder(this, R.style.DialogTheme)
        bldr.setMessage(explanation)
        bldr.setPositiveButton("OK", null)
        bldr.show()
        /*     if(mTimer!=null){
            mTimer.cancel();
        }*/
    }

    private fun removeQnToBooks(id: Int) {
        val helper = MySqliteHelper(this)
        helper.removeObjQnsFromFavs(id)
        Toast.makeText(this, "Question removed from favorites....", Toast.LENGTH_SHORT).show()

    }

    private fun addQnToBooks(id: Int) {
        val helper = MySqliteHelper(this)
        helper.addObjQnsToFavs(id)
        Toast.makeText(this, "Question bookmarked....", Toast.LENGTH_SHORT).show()
    }

    private fun isBookmarked(id: Int): Boolean {
        val helper = MySqliteHelper(this)
        return helper.getObjectiveQnBookMarkStatus(id)
    }
}
