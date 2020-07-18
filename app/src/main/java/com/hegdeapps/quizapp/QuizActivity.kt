package com.hegdeapps.quizapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.Point
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.CompoundButton
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/*

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
*/

import java.util.ArrayList
import java.util.HashMap
import java.util.Random

/**
 * Show a 20 question quiz
 *
 */
class QuizActivity : AppCompatActivity(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    // private DatabaseHelper mDatabaseHelper;
    private var tvQuestion: TextView? = null
    /* private TextView tvAnswer1;
     private TextView tvAnswer2;
     private TextView rbAnswer[2];
     private TextView rbAnswer[3];*/
    private val rbAnswer = arrayOfNulls<Button>(4)
    private var mCursor: Cursor? = null

    private var mRandom: Random? = null
    private var mQuestionNum: Int = 0
    //  private CObjQuestion mCurrentQuestion;
    private var mAnswer1Correct: Boolean = false
    private var mAnswer2Correct: Boolean = false
    private var mAnswer3Correct: Boolean = false
    private var mAnswer4Correct: Boolean = false
    private var mScore: Int = 0
    private var mCorrect: Int = 0
    private var mWrong: Int = 0
    private var tvCodeForQuestion: TextView? = null

    private var mQnNumList: ArrayList<Int>? = null
    //    private MyDialog mExplanationDialog;
    private val mAnsLayoutPoint: Point? = null
    private val mAnsLayoutWidth: Int = 0
    private val mAnsLayoutHeight: Int = 0
    private val mCurrentIndex: Int = 0

    private var tvQuestionNumber: TextView? = null
    // private TextView tvScore;
    private var mYourAnswersList: ArrayList<Int>? = null
    private var mAnswerNum: Int = 0
    //   private RadioGroup rgAnswerGroup;
    private var mSqliteHelper: MySqliteHelper? = null
    private val mFontSize: Int = 0
   // private val mTopicScoreList: List? = null
    private var mTopicScoreMap: HashMap<String, Int>? = null
    private var mTopic: String? = null
    private var mTopicsTotalQuestionsMap: HashMap<String, Int>? = null
    private var mQuestionObj: CObjQuestion? = null

    private val quizTopicsArray: Array<String?>
        get() {
            val l = mTopicScoreMap!!.size
            val arr = arrayOfNulls<String>(l)
            val set = mTopicScoreMap!!.keys
            var i = 0
            for (str in set) {
                arr[i++] = str
            }
            return arr

        }

    private val highScore: Int
        get() {
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            val highScoreString = pref.getString(HIGHSCORES, null) ?: return -1
            val arr = highScoreString.split(SPACE)
            if (arr.size < 1) return 0
            var highScore = Integer.valueOf(arr[0])
            for (str in arr) {
                val scoreNum = Integer.valueOf(str)
                if (scoreNum > highScore)
                    highScore = scoreNum
            }
            return highScore

        }


    override
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSqliteHelper = MySqliteHelper(this)

        setContentView(R.layout.quiz2)
        val toolbar:Toolbar = findViewById(R.id.toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            val ab: ActionBar? = getSupportActionBar();
            if(ab!=null){
                ab.setDisplayShowHomeEnabled(false);
                ab.setDisplayShowTitleEnabled(true);
                ab.setTitle(resources.getString(R.string.quiz));
            }


        }

        // mFontSize = MyMethods.getFontSizeFromPreferences(this);

        tvQuestion = findViewById(R.id.question) as TextView
        rbAnswer[0] = findViewById(R.id.answer1) as Button
        rbAnswer[1] = findViewById(R.id.answer2) as Button
        rbAnswer[2] = findViewById(R.id.answer3) as Button
        rbAnswer[3] = findViewById(R.id.answer4) as Button

        tvQuestionNumber = findViewById(R.id.qnnum) as TextView
        //   tvScore = (TextView) findViewById(R.id.score);


        /*   for(int i=0;i<4;i++){
            rbAnswer[i].setTextSize(mFontSize);
        }*/

        val btnContinue = findViewById(R.id.nextbutton) as Button
        btnContinue.setOnClickListener(this)

        tvCodeForQuestion = findViewById(R.id.questioncode) as TextView
        //   tvQuestion.setTextSize(mFontSize);
        //   tvCodeForQuestion.setTextSize(mFontSize);


        for (i in 0..3) {
            rbAnswer[i]?.setOnClickListener(this)
        }
        enableAnswers(true)

        //rgAnswerGroup = (RadioGroup)findViewById(R.id.answerlayout);






        mRandom = Random()
        mQnNumList = ArrayList<Int>()
        mYourAnswersList = ArrayList<Int>()

        mTopicScoreMap = HashMap<String, Int>()
        mTopicsTotalQuestionsMap = HashMap<String, Int>()


        if (savedInstanceState != null) {
            if (loadCursor()) {
                mQnNumList = savedInstanceState!!.getIntegerArrayList(MyConstants.QUESTION_NUM_LIST)
                mYourAnswersList = savedInstanceState!!.getIntegerArrayList(MyConstants.YOUR_ANSWER_LIST)
                mScore = savedInstanceState!!.getInt(MyConstants.QUIZ_SCORE)
                mCorrect = savedInstanceState!!.getInt(MyConstants.QUIZ_CORRECT_ANSWERED)
                mWrong = savedInstanceState!!.getInt(MyConstants.QUIZ_WRONG_ANSWERED)
                if (!mQnNumList!!.isEmpty()) {
                    val num:Int = mQnNumList!!.get(mQnNumList!!.size  - 1).toInt()
                    mCursor!!.moveToPosition(num)
                    mQuestionNum = mQnNumList!!.size  - 1
                    showQuestion()
                }
            }
        } else {
            if (loadCursor()) {
                initializeCounters()
                createAQuestion()
                showQuestion()
            } else {
                Toast.makeText(this, "Error loading cursor", Toast.LENGTH_SHORT).show()
                this.finish()
            }
        }
    }

    private fun initializeCounters() {
        mQuestionNum = 0
        mWrong = mQuestionNum
        mCorrect = mWrong
        mScore = mCorrect
        mQnNumList!!.clear()
    }

    override
    protected fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putIntegerArrayList(MyConstants.QUESTION_NUM_LIST, mQnNumList)
        outState.putIntegerArrayList(MyConstants.YOUR_ANSWER_LIST, mYourAnswersList)
        outState.putInt(MyConstants.QUIZ_SCORE, mScore)
        outState.putInt(MyConstants.QUIZ_CORRECT_ANSWERED, mCorrect)
        outState.putInt(MyConstants.QUIZ_WRONG_ANSWERED, mWrong)

    }

    override fun onDestroy() {
        if (mCursor != null) {
            mCursor!!.close()
        }
        super.onDestroy()
    }


    private fun showQuestion() {
        /*mQuestionObj =*/ getQuestion()
        mQuestionObj = mQuestionObj
        //    Toast.makeText(this,"explanation"+mQuestionObj.explanation,Toast.LENGTH_SHORT).show();
        if (mQuestionObj == null) {
            return
        }
        mAnswerNum = 0
        tvQuestionNumber!!.setText( (mQuestionNum + 1 ).toString() + "/" + NUM_QUESTIONS)
        //  tvScore.setText(String.valueOf(mScore));

        tvQuestion!!.setText(mQuestionObj!!.question)
        rbAnswer[0]?.setVisibility(View.VISIBLE)
        rbAnswer[1]?.setVisibility(View.VISIBLE)
        rbAnswer[2]?.setVisibility(View.VISIBLE)
        rbAnswer[3]?.setVisibility(View.VISIBLE)
        for (i in 0..3) {
            rbAnswer[i]?.setTextColor(Color.WHITE)
        }
        /*for (int i = 0; i < 4; i++) {
            rbAnswer[i].setChecked(false);

        }*/
        //    rgAnswerGroup.clearCheck();

        if (mQuestionObj!!.answer1 != null) {
            rbAnswer[0]?.setText(mQuestionObj!!.answer1)
        }
        if (mQuestionObj!!.answer2 != null) {
            rbAnswer[1]?.setText(mQuestionObj!!.answer2)
        }
        if (mQuestionObj!!.answer3 != null) {
            rbAnswer[2]?.setText(mQuestionObj!!.answer3)
        } else {
            rbAnswer[2]?.setVisibility(View.INVISIBLE)
        }
        if (mQuestionObj!!.answer4 != null) {
            rbAnswer[3]?.setText(mQuestionObj!!.answer4)
        } else {
            rbAnswer[3]?.setVisibility(View.INVISIBLE)
        }
        if (mQuestionObj!!.qncode == null) {
            //   tvCodeForQuestion.setText(" ");
            tvCodeForQuestion!!.setVisibility(View.GONE)
        } else {
            tvCodeForQuestion!!.setVisibility(View.VISIBLE)
            tvCodeForQuestion!!.setText(mQuestionObj!!.qncode)
            //  Typeface courier = Typeface.createFromAsset(getAssets(),"courier.ttf");
            tvCodeForQuestion!!.setTypeface(Typeface.MONOSPACE)
            //  tvCodeForQuestion.setLineSpacing(1.5f, 1.5f);
        }


        //Set all answers to wrong
        mAnswer4Correct = false
        mAnswer3Correct = mAnswer4Correct
        mAnswer2Correct = mAnswer3Correct
        mAnswer1Correct = mAnswer2Correct
        val m = mQuestionObj!!.correctAnswer
        //      int m = correctAnswer;
        setCorrectAnswer(m)
        enableAnswers(true)

        /*  if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            ScrollView scrlView = (ScrollView) findViewById(R.id.scrollView2);
            scrlView.scrollTo(0, 0);
        }*/
    }

    private fun setCorrectAnswer(m: Int) {
        mAnswer4Correct = false
        mAnswer3Correct = mAnswer4Correct
        mAnswer2Correct = mAnswer3Correct
        mAnswer1Correct = mAnswer2Correct
        when (m) {
            1 -> mAnswer1Correct = true
            2 -> mAnswer2Correct = true
            3 -> mAnswer3Correct = true
            4 -> mAnswer4Correct = true
        }
    }


    private fun createAQuestion() {
        val totalQuestions = mCursor!!.getCount()
        var num: Int
        do {
            num = mRandom!!.nextInt(totalQuestions)
        } while (mQnNumList!!.contains(num))
        mQnNumList!!.add(num)
        mCursor!!.moveToPosition(num)
        storeInTopicList()
    }

    private fun storeInTopicList() {
        mTopic = mCursor!!.getString(mCursor!!.getColumnIndex(CConstants.TOPIC))
        if (!mTopicScoreMap!!.containsKey(mTopic.toString()) ) {
            mTopicScoreMap!!.put(mTopic.toString(),  0)
            mTopicsTotalQuestionsMap!!.put(mTopic.toString(), 1)
        } else {
            val num = mTopicsTotalQuestionsMap!!.get(mTopic.toString())
            mTopicsTotalQuestionsMap!!.put(mTopic.toString(), num!! + 1)
        }
    }

    private fun showQuizEndDialog() {
        var titleString = "Quiz Completed"
        val highscore = highScore
        if (highscore != -1 && mScore > highscore) {
            titleString = "Congratulations. You have scored higher than previous tests!!!"
        }
        val maxScore = NUM_QUESTIONS * 10
        val percent = mScore / maxScore.toFloat() * 100
        var str0 = ""
        if (percent > GRADE_A) {
            str0 = "Excellent Work!!!"
        } else if (percent > GRADE_B) {
            str0 = "Very Good!!"
        } else if (percent > GRADE_C) {
            str0 = "Good!"
        }
        val str1 = "Your score is $mScore\n"
        val str2 = "Correct answers: $mCorrect\n"
        val str3 = "Wrong answers : $mWrong\n"
        val str4 = "Unattempted questions:  " + (10 - mCorrect - mWrong) + "\n"
        val d = Dialog(this)//,R.style.Theme_AppCompat_Dialog_Alert);//TODO set theme here?
        // View v = getLayoutInflater().inflate(R.layout.quizendlayout,null);
        //   builder.setView(v);

        d.setContentView(R.layout.quizendlayout)
        val tv = d.findViewById<TextView>(R.id.message)
        val title = d.findViewById<TextView>(R.id.title)
        val btnShowAns = d.findViewById(R.id.btnShowAnswers) as Button
        btnShowAns.setOnClickListener(object : View.OnClickListener  {
            override
            fun onClick(v: View) {
                d.dismiss()
                showAllAnswers()
            }
        })
        val btnRepeatTest = d.findViewById(R.id.btnRepeatTest) as Button
        btnRepeatTest.setOnClickListener(object : View.OnClickListener  {
            override
            fun onClick(v: View) {
                d.dismiss()
                startQuizAgain()
            }
        })
        val btnShowStats = d.findViewById(R.id.btnShowStat) as Button
        btnShowStats.setOnClickListener(object : View.OnClickListener  {
            override
            fun onClick(v: View) {
                d.dismiss()
                showStatistics()
            }
        })
        title.setText(titleString)
        tv.setText("$str0\n$str1$str2$str3$str4\n\n  ")

        d.show()
        saveScores()
    }

    /*override
    protected fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_GRAPH)
        //if (resultCode == RESULT_OK) {
            finish()
        //  }
    }*/


    private fun showStatistics() {

        val intent = Intent(this, DrawGraph::class.java)

        val quizTopics = quizTopicsArray
        val quizScores = getScoresInTopic(quizTopics)
        val numQuestionsInTopics = getTotalInTopic(quizTopics)
        intent.putExtra(CConstants.TOPICS_ARRAY, quizTopics)
        intent.putExtra(CConstants.SCORE_ARRAY, quizScores)
        intent.putExtra(CConstants.TOTAL_IN_TOPIC_ARRAY, numQuestionsInTopics)

        startActivityForResult(intent, REQ_GRAPH)
    }

    private fun getTotalInTopic(quizTopics: Array<String?>): IntArray {
        val totals = IntArray(quizTopics.size)
        var i = 0
        for (str in quizTopics) {
            val value:Int = mTopicsTotalQuestionsMap!!.get(str)!!
            totals[i++] = value
        }
        return totals
    }

    private fun getScoresInTopic(quizTopics: Array<String?>): IntArray {
        val score = IntArray(quizTopics.size)
        var i = 0
        for (str in quizTopics) {
            val value = mTopicScoreMap!!.get(str)
            score[i++] = value!!
        }
        return score
    }

    /*private void viewQuizAnswers() {
        disableAnswers();
        enablePrevNextButtons();
        for(int i=0;i<10;i++){
            int num = mQnNumList.get(i);
            mCursor.moveToPosition(num);
            showQuestion();

            showCorrectAnswerInGreen();

        }

    }
*/
    /*  private void enablePrevNextButtons() {
        Button prevButton,nextButton,explButton;
        prevButton = (Button)findViewById(R.id.prevbutton);
        nextButton = (Button)findViewById(R.id.nextbutton);
        explButton = (Button)findViewById(R.id.explnbutton);
        prevButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        explButton.setVisibility(View.VISIBLE);
        prevButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);
        explButton.setOnClickListener(this);



    }

 */
    /*private void showCorrectAnswerInGreen(*//*boolean border*//*) {
        int m   = mCurrentQuestion.correctAnswer;
     //   int m = Integer.valueOf(corrAns);
        switch (m) {
            case 1:
                rbAnswer[0].setTextColor(getResources().getColor(R.color.darkgreen));
                   *//* if(border) {
                        rbAnswer[0].setBackgroundDrawable(getResources().getDrawable(R.drawable.borderbg));
                    }*//*
                break;
            case 2:
                rbAnswer[1].setTextColor(getResources().getColor(R.color.darkgreen));
                *//*if(border) {
                    rbAnswer[1].setBackgroundDrawable(getResources().getDrawable(R.drawable.borderbg));
                }*//*
                break;
            case 3:
                rbAnswer[2].setTextColor(getResources().getColor(R.color.darkgreen));
               *//* if(border) {
                    rbAnswer[2].setBackgroundDrawable(getResources().getDrawable(R.drawable.borderbg));
                }*//*
                break;
            case 4:
                rbAnswer[3].setTextColor(getResources().getColor(R.color.darkgreen));
               *//* if(border) {
                    rbAnswer[3].setBackgroundDrawable(getResources().getDrawable(R.drawable.borderbg));
                }*//*
                break;
        }

    }
*/
    private fun enableAnswers(b: Boolean) {
        rbAnswer[0]?.setClickable(b)
        rbAnswer[1]?.setClickable(b)
        rbAnswer[2]?.setClickable(b)
        rbAnswer[3]?.setClickable(b)
    }

    private fun saveScores() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        var highScore = pref.getString(HIGHSCORES, null)
        if (highScore != null) {
            highScore = highScore + SPACE + mScore.toString()
        } else {
            highScore =  mScore.toString()
        }

        val edtr = pref.edit()
        edtr.putString(HIGHSCORES, highScore)
        edtr.commit()

    }

    private fun endQuizActivity() {
        this.finish()
    }

    private fun startQuizAgain() {
        initializeCounters()
        if (mCursor!!.isClosed()) {
            mCursor = mSqliteHelper!!.allObjectiveQustions
        }
        createAQuestion()
        showQuestion()

    }

    private fun loadCursor(): Boolean {

        mCursor = mSqliteHelper!!.allObjectiveQustions
        if (mCursor!!.getCount() <= 0) {
            showIntroDialog()
            return false
        }
        return true


    }

    private fun showIntroDialog() {

        val d = Dialog(this)
        d.requestWindowFeature(Window.FEATURE_NO_TITLE)
        d.setContentView(R.layout.titlelessdialog)

        val tv = d.findViewById(R.id.dialogmessage) as TextView
        val b = d.findViewById(R.id.okbutton) as Button

        tv.setText(getResources().getString(R.string.INTRO_TEXT))
        b.setOnClickListener(object : View.OnClickListener  {
            override
            fun onClick(v: View) {
                reloadCursor()
                d.dismiss()
            }
        })
        d.show()
        /*  AlertDialog.Builder builder  = new AlertDialog.Builder(this,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar);//TODO set theme here?

            builder.setMessage( getResources().getString(R.string.INTRO_TEXT));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    reloadCursor();
                }
            });

            Dialog d = builder.create();
            d.show();*/


    }

    private fun reloadCursor() {
        mCursor = mSqliteHelper!!.allObjectiveQustions
        createAQuestion()
        showQuestion()
    }

    override
    fun onClick(v: View) {
        val id = v.getId()
        val checked: Boolean
        if (id == R.id.nextbutton) {
            goToNextQuestion()
            return
        }
        //  if (checked) {
        when (id) {
            R.id.answer1 -> {
                mAnswerNum = 1
                if (mAnswer1Correct) {
                    correctAnswerClicked(1)
                } else {
                    wrongAnswerClicked(1)
                }
            }
            R.id.answer2 -> {
                mAnswerNum = 2
                if (mAnswer2Correct) {
                    correctAnswerClicked(2)
                } else {
                    wrongAnswerClicked(2)
                }
            }
            R.id.answer3 -> {
                mAnswerNum = 3
                if (mAnswer3Correct) {
                    correctAnswerClicked(3)
                } else {
                    wrongAnswerClicked(3)
                }
            }
            R.id.answer4 -> {
                mAnswerNum = 4
                if (mAnswer4Correct) {
                    correctAnswerClicked(4)
                } else {
                    wrongAnswerClicked(4)
                }
            }
        }
        //   }
    }

    /*private void goToReviewNextQuestion() {

        mCurrentIndex++;

        int num = mQnNumList.get(mCurrentIndex);
        mCursor.moveToPosition(num);
        showQuestion();
    }

    private void goToReviewPrevQuestion() {

    }*/

    private fun correctAnswerClicked(answerNum: Int) {
        val colorGreen = Color.GREEN
        when (answerNum) {
            1 -> rbAnswer[0]?.setTextColor(colorGreen)
            2 -> rbAnswer[1]?.setTextColor(colorGreen)
            3 -> rbAnswer[2]?.setTextColor(colorGreen)
            4 -> rbAnswer[3]?.setTextColor(colorGreen)
        }
        enableAnswers(false)
        mScore += 10
        mCorrect++
        val score = mTopicScoreMap!!.get(mTopic)
        mTopicScoreMap!!.put(mTopic!!, score !!+ 1)
    }


    private fun goToNextQuestion() {
        mYourAnswersList!!.add(mAnswerNum)
        mQuestionNum++
        if (mQuestionNum >= NUM_QUESTIONS) {
            showQuizEndDialog()
        } else {
            createAQuestion()
            showQuestion()
        }
    }


    private fun wrongAnswerClicked(answerNum: Int) {
        when (answerNum) {
            1 -> rbAnswer[0]?.setTextColor(Color.RED)
            2 -> rbAnswer[1]?.setTextColor(Color.RED)
            3 -> rbAnswer[2]?.setTextColor(Color.RED)
            4 -> rbAnswer[3]?.setTextColor(Color.RED)
        }

        mWrong++
        enableAnswers(false)

    }

    /*void showExplanationDialog() {
        String titleString = "Correct Answer is \n";
        int m =  mCurrentQuestion.correctAnswer;
        switch (m) {
            case 1:
                titleString += mCurrentQuestion.answer1;
                break;
            case 2:
                titleString += mCurrentQuestion.answer2;
                break;
            case 3:
                titleString += mCurrentQuestion.answer3;
                break;
            case 4:
                titleString += mCurrentQuestion.answer4;
                break;
        }
        String explanaton = mCurrentQuestion.explanation;
        String str1 = titleString;
        if (!TextUtils.isEmpty(str1)) {
            str1 = str1 + "\n\n" + explanaton;
        }
        showExplanationPopUp(str1);
        *//*mExplanationDialog= new MyDialog(this,str1);

        mExplanationDialog.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextQuestion();
                mExplanationDialog.dismiss();
                mExplanationDialog = null;
            }
        });
        mExplanationDialog.otherButton.setText("View");
        mExplanationDialog.otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExplanationDialog.hide();
            }
        });
        mExplanationDialog.show();*//*

     *//*   AlertDialog.Builder builder  = new AlertDialog.Builder(this,android.R.style.Theme_Holo_Light_Dialog);

      //          android.R.style.Theme_Holo_Light_Dialog);//TODO set theme here?
        builder.setTitle("Wrong Answer");
        builder.setMessage(str1 );
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               goToNextQuestion();
            }
        });

        Dialog d = builder.create();
        d.show();*//*
    }
*/
    /*override
    fun onWindowFocusChanged(hasFocus: Boolean) {
        *//*

        int[] location = new int[2];
        LinearLayout layout = (LinearLayout) findViewById(R.id.answerlayout);


        // Get the x, y location and store it in the location[] array
        // location[0] = x, location[1] = y.
        layout.getLocationOnScreen(location);

        //Initialize the Point with x, and y positions
        mAnsLayoutPoint = new Point();
        mAnsLayoutPoint.x = location[0];
        mAnsLayoutPoint.y = location[1];

        mAnsLayoutWidth = layout.getWidth();
        mAnsLayoutHeight = layout.getHeight();
*//*

    }*/

   /* private fun showExplanationPopUp(str1: String) {
        val popupWidth = mAnsLayoutWidth
        val popupHeight = mAnsLayoutHeight

        // Inflate the popup_layout.xml
        val viewGroup = this.findViewById(R.id.popupdialoglayout) as LinearLayout
        val layoutInflater = this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = layoutInflater.inflate(R.layout.titlelessdialog, viewGroup)

        // Creating the PopupWindow
        val popup = PopupWindow(this)
        popup.setContentView(layout)
        popup.setWidth(popupWidth)
        popup.setHeight(popupHeight)
        popup.setFocusable(true)

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        val OFFSET_X = 10
        val OFFSET_Y = 10

        *//* // Clear the default translucent background
        popup.setBackgroundDrawable(new BitmapDrawable());*//*

        // Displaying the popup at the specified location, + offsets.
        popup.showAtLocation(layout, Gravity.NO_GRAVITY, mAnsLayoutPoint!!.x + OFFSET_X, mAnsLayoutPoint!!.y + OFFSET_Y)

        val tv = layout.findViewById(R.id.dialogmessage) as TextView
        tv.setText(str1)

        val okButton = layout.findViewById(R.id.okbutton) as Button
        okButton.setOnClickListener(object : View.OnClickListener() {
            override
            fun onClick(v: View) {
                goToNextQuestion()
                popup.dismiss()
            }
        })
        // Getting a reference to Close button, and close the popup when clicked.
        //  Button close = (Button) layout.findViewById(R.id.close);
    }*/

    /* void showExplanation() {
         String explanation= mCurrentQuestion.explanation;
         showTextInDialog(explanation);
     }*/
    /* private void showTextInDialog(String solution) {
        DiFrag diFragment = DiFrag.newInstance(solution);
        diFragment.show(getFragmentManager(), "dialog");


    }
*/
    private fun /*CObjQuestion*/ getQuestion() {
        mQuestionObj = CObjQuestion()
        mQuestionObj!!.question = mCursor!!.getString(mCursor!!.getColumnIndex("question"))
        mQuestionObj!!.topic = mCursor!!.getString(mCursor!!.getColumnIndex("topic"))
        mQuestionObj!!.answer1 = mCursor!!.getString(mCursor!!.getColumnIndex("answer1"))
        mQuestionObj!!.answer2 = mCursor!!.getString(mCursor!!.getColumnIndex("answer2"))
        mQuestionObj!!.answer3 = mCursor!!.getString(mCursor!!.getColumnIndex("answer3"))
        mQuestionObj!!.answer4 = mCursor!!.getString(mCursor!!.getColumnIndex("answer4"))
        mQuestionObj!!.correctAnswer = mCursor!!.getInt(mCursor!!.getColumnIndex("correctanswer"))
        mQuestionObj!!.explanation = mCursor!!.getString(mCursor!!.getColumnIndex("explanation"))
        val str = mCursor!!.getString(mCursor!!.getColumnIndex("questioncode"))
        if (str != null) {
            mQuestionObj!!.qncode = str
        } else {
            mQuestionObj!!.qncode = null
        }
        // return mQuestionObj;
    }


    private fun showAllAnswers() {
        //StringBuffer buf = new StringBuffer();
        val ansList = ArrayList<String>()
        for (i in 0..9) {
            val buf1 = StringBuffer()
            val qnNum = mQnNumList!!.get(i)
            mCursor!!.moveToPosition(qnNum)
            /*mQuestionObj = */getQuestion()
            val qn = mQuestionObj!!.question
            val qnCode = mQuestionObj!!.qncode
            val yourAnswer = mYourAnswersList!!.get(i)
            val correctAnswerNum = mQuestionObj!!.correctAnswer
            // int correctAnswerNum = Integer.valueOf(correctAnswerNumberString);
            val correctAnswer = getCorrectAnswer(correctAnswerNum)

            buf1.append("Question ").append(i + 1).append(")")
            buf1.append(qn)
            if (qnCode != null)
                buf1.append("\n").append(qnCode)
            buf1.append("\n\n Correct Answer :")
            buf1.append(correctAnswer)
            buf1.append("\n\n Your Answer :")
            if (yourAnswer == 0) {
                buf1.append("Did not attempt\n")
            } else {
                buf1.append(getCorrectAnswer(yourAnswer)).append("\n")
            }
            ansList.add(buf1.toString())
        }
        //  String str = buf1.toString();
        val builder = AlertDialog.Builder(this, R.style.DialogTheme)//,R.style.DialogTheme);//TODO set theme here?
        builder.setTitle("Correct Answers")
        val arr = arrayOfNulls<String>(ansList.size )
        builder.setItems(ansList.toArray(arr), null)
        builder.setPositiveButton("OK", object : DialogInterface.OnClickListener {

            override
            fun onClick(dialog: DialogInterface, which: Int) {
                this@QuizActivity.finish()
            }
        }
        )
        /*AlertDialog d =*/ builder.show()

        //  ListView lv = d.getListView();

        // TextView tv = (TextView)lv.findViewById(android.R.id.list);


        /*  if(tv!=null ){
            tv.setTextSize(mFontSize);
        }*/


    }

    private fun getCorrectAnswer(num: Int): String? {

        when (num) {
            1 -> return mQuestionObj!!.answer1
            2 -> return mQuestionObj!!.answer2
            3 -> return mQuestionObj!!.answer3
            4 -> return mQuestionObj!!.answer4
        }
        return null
    }

    override
    protected fun onResume() {
        super.onResume()
        super.onPause()
        if (mSqliteHelper == null) {
            mSqliteHelper = MySqliteHelper(this)
            if (mCursor == null) {
                mCursor = mSqliteHelper!!.allObjectiveQustions
            }
        }


    }

    override
    protected fun onPause() {
        super.onPause()

        if (mSqliteHelper != null) {
            mSqliteHelper!!.close()
        }
    }

    override
    fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val id = buttonView.getId()
        val checked = (buttonView as RadioButton).isChecked()
        if (checked) {
            when (id) {
                R.id.answer1 -> {
                    mAnswerNum = 1
                    if (mAnswer1Correct) {
                        correctAnswerClicked(1)
                    } else {
                        wrongAnswerClicked(1)
                    }
                }
                R.id.answer2 -> {
                    mAnswerNum = 2
                    if (mAnswer2Correct) {
                        correctAnswerClicked(2)
                    } else {
                        wrongAnswerClicked(2)
                    }
                }
                R.id.answer3 -> {
                    mAnswerNum = 3
                    if (mAnswer3Correct) {
                        correctAnswerClicked(3)
                    } else {
                        wrongAnswerClicked(3)
                    }
                }
                R.id.answer4 -> {
                    mAnswerNum = 4
                    if (mAnswer4Correct) {
                        correctAnswerClicked(4)
                    } else {
                        wrongAnswerClicked(4)
                    }
                }
            }
        }
    }

    companion object {
        private val HIGHSCORES = "highscores"
        private val SPACE = " "
        private val NUM_QUESTIONS = 25
        private val REQ_GRAPH = 12345
        private val GRADE_A = 90
        private val GRADE_B = 70
        private val GRADE_C = 60
    }
}
/*public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        int answerNum ;
        if(isChecked) {
            switch (id) {

                case R.id.ans1:
                    answerNum = 1;
                    if (mAnswer1Correct) {
                        correctAnswerClicked(1);
                    } else {
                        wrongAnswerClicked(1);
                    }

                    break;
                case R.id.ans2:
                    answerNum = 2;
                    if (mAnswer2Correct) {
                        correctAnswerClicked(2);
                    } else {
                        wrongAnswerClicked(2);
                    }

                    break;
                case R.id.ans3:
                    answerNum = 3;
                    if (mAnswer3Correct) {
                        correctAnswerClicked(3);
                    } else {
                        wrongAnswerClicked(3);
                    }

                    break;
                case R.id.ans4:
                    answerNum = 4;
                    if (mAnswer4Correct) {
                        correctAnswerClicked(4);
                    } else {
                        wrongAnswerClicked(4);
                    }

                    break;

                default:
                    return;
            }
            mYourAnswersList.add(answerNum);
        }
    }
}*/


