package com.hegdeapps.quizapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.database.Cursor
import android.os.Bundle 
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment


/*import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;*/

/**
 * Created by usha on 11/12/17.
 */

class QuestionDetailActivity : AppCompatActivity() {
    private lateinit var shareProvider:androidx.appcompat.widget.ShareActionProvider
    private var tvQuestionCode: TextView?=null
    private val mTextMessage: TextView? = null
    private var mCursor: Cursor? = null
    private var tvQuestion: TextView? = null
    private var tvAnswer: TextView? = null
    private val scAnswer: ScrollView? = null
    private var btnShowAnswer: Button? = null
    private var btnExplain: Button? = null
    // private Typeface mTypeface;
    private var tvQuestionNum: TextView? = null
    private val mAnswerShown: Boolean = false
    private val mRelqn1: String? = null
    private val mRelqn2: String? = null
    private val mRelqn3: String? = null
    private var mSqliteHelper: MySqliteHelper? = null
    private var mExplanation: String? = null
   // private var mAdView: AdView? = null
    //private AdView mAdView;


    override
    fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_details2)
        mSqliteHelper = MySqliteHelper(this)
        val tb = findViewById(R.id.toolbar) as Toolbar
        tvQuestionNum = findViewById(R.id.qn_number)
        tvQuestion = findViewById(R.id.question)
        tvAnswer = findViewById(R.id.answercode)
        tvQuestionCode = findViewById(R.id.questioncode)
        if (null != tb) {
            tb!!.setTitle("")
            setSupportActionBar(tb)
            val ab = getSupportActionBar()
            ab?.setDisplayHomeAsUpEnabled(true)

        }
        val intent = getIntent()
        val pos = intent.getIntExtra(CConstants.CURSOR_POSITION, -1)
        if (0 > pos) {
            return
        }
        val topic = intent.getStringExtra(CConstants.TOPIC)
       val isFavorites = intent.getBooleanExtra(CConstants.FAVORITES, false)
        val filterText = intent.getStringExtra(CConstants.FILTER_TEXT)
      // val difficulty = intent.getStringExtra(CConstants.DIFFICULTY_LEVEL)
        mCursor = mSqliteHelper!!.getFilteredLeveledCodingQuestions(topic, filterText);//, difficulty)
        /*if(filterText!=null && (!TextUtils.isEmpty(filterText))){
            mCursor = mSqliteHelper.getFilteredCodingQuestions(topic,filterText);
        }/
        if(difficulty!=null&& (!TextUtils.isEmpty(difficulty))){
                mCursor = mSqliteHelper.getFilteredLeveledCodingQuestions(topic, filterText, difficulty);
        }*/
        if (isFavorites) {
            getSupportActionBar()?.setTitle("Favorites")
            mCursor = mSqliteHelper!!.favoriteDesQns
        } else {

            if (null == topic) {
                return
            }
            tb!!.setTitle(topic)
            /* String searchText = intent.getStringExtra(CConstants.FILTER_TEXT);
            if (null != searchText) {
                mCursor = mSqliteHelper.getFilteredCodingQuestions(topic, searchText);
            } else {
                mCursor = mSqliteHelper.getDesQnsOfTopic(topic);
            }*/
       }
        if (mCursor!!.getCount() === 0) {
            //  Toast.makeText(this,"Number of rows is zero",Toast.LENGTH_SHORT).show();
            this.finish()
        }
        if (!mCursor!!.moveToPosition(pos)) {
            this.finish()
        }
        val btnPrevious = findViewById(R.id.prev) as Button
        btnPrevious.setOnClickListener(object : View.OnClickListener  {
            
            override fun onClick(v: View) {
                mCursor!!.moveToPrevious()
                if (!mCursor!!.isBeforeFirst()) {
                    showCurrentQuestion()
                }
            }
        })
        val nextButton = findViewById(R.id.next) as Button
        nextButton.setOnClickListener(object : View.OnClickListener {
            override 
            fun onClick(v: View) {
                mCursor!!.moveToNext()
                if (!mCursor!!.isAfterLast()) {
                    showCurrentQuestion()
                }
            }
        })
        btnExplain = findViewById(R.id.explain) as Button
        btnExplain!!.setOnClickListener(object : View.OnClickListener  {
            override
            fun onClick(v: View) {
                if (!TextUtils.isEmpty(mExplanation)) {
                    showExplainDialog(mExplanation)
                }
            }
        })
        val btnFav = findViewById(R.id.favorites) as ImageButton
        btnFav.setOnClickListener(fun(it: View) {
            val id = mCursor!!.getInt(mCursor!!.getColumnIndex("_id"))
            val isRowBookmarked = isBookmarked(id)
            if (isRowBookmarked) {
                removeQnToBooks(id)
                btnFav.setImageDrawable(getResources().getDrawable(R.drawable.favoff))
            } else {
                addQnToBooks(id)
                btnFav.setImageDrawable(getResources().getDrawable(R.drawable.favon))
            }
        })



      /*  btnFav.setOnClickListener(  View.OnClickListener {

            fun onClick(v: View) {
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
        })*/



        /* mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("3C791098A3B50FC97E7ED72B94834A08").build();
        mAdView.loadAd(adRequest);*/
        if (savedInstanceState != null) {
            val cursorPos = savedInstanceState!!.getInt(CURSOR_POS)
            mCursor!!.moveToPosition(cursorPos)
        }
      /*  mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView!!.loadAd(adRequest)*/
        showQuestion()
    }

    private fun addQnToBooks(id: Int) {
        val helper = MySqliteHelper(this)
        helper.addQnToFav(id)
    }

    private fun removeQnToBooks(id: Int) {
        val helper = MySqliteHelper(this)
        helper.removeQnFromFavs(id)
    }

    private fun isBookmarked(id: Int): Boolean {
        val helper = MySqliteHelper(this)
        return helper.getDesQnBookmarkStatus(id)
    }

    override
    fun onCreateOptionsMenu(menu: Menu): Boolean {
        getMenuInflater().inflate(R.menu.menu_main, menu)
     //   val item:MenuItem = menu.findItem(R.id.action_share)
    //      shareProvider = MenuItemCompat.getActionProvider(item) as ShareActionProvider

        return true
    }

    override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()

        if (R.id.action_share === id) {
            /*val intent:Intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/html")
            val shareString:String = tvQuestion!!.text.toString()+
                    "\n\n\n" +
                    "For such solved questions download the app " +
                    CConstants.LINK_TO_APP
            intent.putExtra(Intent.EXTRA_TEXT,shareString)
            shareProvider.setShareIntent(intent)*/

            shareQuestion()

        }
      /*  if(id==R.id.action_prev){
            moveToPrevQuestion();
        }
        if(id==R.id.action_next){
            moveToNextQuestion();
        }*/
        return false
    }

    private fun moveToPrevQuestion() {
                mCursor!!.moveToPrevious()
                if (!mCursor!!.isBeforeFirst) {
                    showCurrentQuestion()
                }
    }
    private fun moveToNextQuestion() {
        mCursor!!.moveToNext()
        if (!mCursor!!.isAfterLast()) {
            showCurrentQuestion()
        }
    }
    private fun shareQuestion() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "text/plain"
      //  intent.setAction(Intent.ACTION_SEND)

        val shareString:String = tvQuestion!!.text.toString()+
        "\n\n\n" +
                "For such solved questions download the app " +
                CConstants.LINK_TO_APP
        intent.putExtra(Intent.EXTRA_TEXT, shareString)

        startActivity(Intent.createChooser(intent, "Share via"))
    }

    private fun showQuestion() {
     //    tvQuestionNum = findViewById(R.id.qn_number) as TextView
       // tvQuestion = findViewById(R.id.question) as TextView
        //tvAnswer = findViewById(R.id.answercode) as TextView
        //    scAnswer = (ScrollView)  findViewById(R.id.answerscrollview);
        btnShowAnswer = findViewById(R.id.showanswer) as Button
        btnExplain = findViewById(R.id.explain) as Button
        showCurrentQuestion()
    }


    /**
     * Shows related questions in a dialog
     * activated when option menu is pressed
     */
    /*private void showRelatedQuestionsDialog() {
            if (TextUtils.isEmpty(mRelqn1) && TextUtils.isEmpty(mRelqn2) && TextUtils.isEmpty(mRelqn3)) {
                return;
            }
            LayoutInflater factory = LayoutInflater.from(this);
            final View dialogView = factory.inflate(R.layout.relatedquestions, null);
            final AlertDialog dlgRelatedQns = new AlertDialog.Builder(this).create();
            //     dlgRelatedQns.setTitle("Related Questions");
            dlgRelatedQns.setView(dialogView);

            String topic = mCursor.getString(mCursor.getColumnIndex(CConstants.TOPIC));
            TextView tvRelQn1 = dialogView.findViewById(R.id.relqn1);
            if ((null != mRelqn1) && !TextUtils.isEmpty(mRelqn1)) {
                final int number = Integer.valueOf(mRelqn1);

                tvRelQn1.setVisibility(View.VISIBLE);
                tvRelQn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToQuestion(number);
                        dlgRelatedQns.dismiss();
                    }
                });
                String relatedQuestion1 = mSqliteHelper.getRelatedQuestion(mRelqn1, topic);
                if (null != relatedQuestion1) {
                    tvRelQn1.setText(relatedQuestion1);
                }
            } else {
                tvRelQn1.setVisibility(View.GONE);
            }
            TextView tvRelQn2 = dialogView.findViewById(R.id.relqn2);
            if ((null != mRelqn2) && !TextUtils.isEmpty(mRelqn2)) {
                final int number = Integer.valueOf(mRelqn2);
                tvRelQn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToQuestion(number);
                        dlgRelatedQns.dismiss();
                    }
                });
                String relatedQuestion = mSqliteHelper.getRelatedQuestion(mRelqn2, topic);
                tvRelQn2.setVisibility(View.VISIBLE);
                if (null != relatedQuestion) {
                    tvRelQn2.setText(relatedQuestion);
                }
            } else {
                tvRelQn2.setVisibility(View.GONE);
            }
            TextView tvRelQn3 = dialogView.findViewById(R.id.relqn3);
            if ((null != mRelqn3) && !TextUtils.isEmpty(mRelqn3)) {
                final int number = Integer.valueOf(mRelqn3);
                tvRelQn3.setVisibility(View.VISIBLE);
                tvRelQn3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        moveToQuestion(number);
                        dlgRelatedQns.dismiss();
                    }
                });
                String relatedQuestion =  mSqliteHelper.getRelatedQuestion(mRelqn3, topic);
                relatedQuestion = relatedQuestion.trim();
                if ((null != relatedQuestion) && (!TextUtils.isEmpty(relatedQuestion))) {
                    tvRelQn3.setText(relatedQuestion);
                }
            } else {
                tvRelQn3.setVisibility(View.GONE);
            }
            dlgRelatedQns.show();
        }*/

    private fun copyAnswerToClipboard() {
        val question = mCursor!!.getString(mCursor!!.getColumnIndex("question"))
        val answerCode = mCursor!!.getString(mCursor!!.getColumnIndex("answer"))
        if (null != answerCode) {
            val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val str:String = question  + '\n' + answerCode!!
            val data = ClipData.newPlainText("c_soln", str)
            manager.setPrimaryClip(data)
            Toast.makeText(this, "Answer code copied to clipboard..", Toast.LENGTH_SHORT).show()
        }
    }

 /*   override
    fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.action_related)
        //    MenuItem item = menu.getItem(1);
        if (!TextUtils.isEmpty(mRelqn1) ||
                !TextUtils.isEmpty(mRelqn2) ||
                !TextUtils.isEmpty(mRelqn3)) {
            item.setVisible(true)
        } else {
            item.setVisible(false)
        }
        return true
    }*/

    private fun showAnswerScroll(answer: String?) {


        var answer = answer
        val factory = LayoutInflater.from(this)
        val dialogView = factory.inflate(R.layout.qncode, null)
        val dlgQnCode = AlertDialog.Builder(this, R.style.DialogTheme).create()
        dlgQnCode.setView(dialogView)

        if(answer!=null) {

            answer = answer!! + "\n\n\n"//some how answer is getting clipped. So! usha 11Oct2018
        }
        val tvCode = dialogView.findViewById<TextView>(R.id.answercode)
        tvCode.setText(answer)
       /* val btnCopy = dialogView.findViewById<Button>(R.id.copy_clip)
        btnCopy.setOnClickListener(object : View.OnClickListener  {
            override
            fun onClick(v: View) {
                copyAnswerToClipboard()
            }
        })
        val btnClose = dialogView.findViewById<Button>(R.id.close_dlg)
        btnClose.setOnClickListener(object : View.OnClickListener  {
            override
            fun onClick(v: View) {
                dlgQnCode.dismiss()
            }
        })*/
        val answerDialog:AnswerDialog = AnswerDialog(answer)
        answerDialog.show(supportFragmentManager,"dialog")
        //dlgQnCode.show()

    }

    private fun showCurrentQuestion() {
        val question = mCursor!!.getString(mCursor!!.getColumnIndex("question"))
        var answercode = mCursor!!.getString(mCursor!!.getColumnIndex("answer"))
        val answer = mCursor!!.getString(mCursor!!.getColumnIndex("explanation"))
        val questioncode = mCursor!!.getString(mCursor!!.getColumnIndex("questioncode"))
        if (null == answercode || TextUtils.isEmpty(answercode)) {
            answercode = answer//because we want to show only answercode
        }
        //TextView tvQn = (TextView)getView().findViewById(R.id.question);
        tvQuestion!!.setText(question)
        if(questioncode!=null  && !TextUtils.isEmpty(questioncode)){
            tvQuestionCode!!.visibility = View.VISIBLE;
            tvQuestionCode!!.setText(questioncode)
        }else{
            tvQuestionCode!!.visibility=View.GONE
        }
        //   tvQuestion.setTypeface(mTypeface);
        val qnCount = mCursor?.getCount()
        val str =  ((mCursor?.getPosition() ?: 0) + 1).toString() + '/' + qnCount.toString()
        tvQuestionNum!!.setText(str)
        val qnNum =( mCursor!!.getPosition()  ) + 1
        val nextButton = findViewById(R.id.next) as Button
        val prevButton = findViewById(R.id.prev) as Button
        nextButton.setEnabled(true)
        prevButton.setEnabled(true)
        if (qnNum == qnCount) {
            nextButton.setEnabled(false)
        } else if (1 == qnNum) {
            prevButton.setEnabled(false)
        }
        val finalAnswercode = answercode
        btnShowAnswer!!.setOnClickListener(object : View.OnClickListener  {
            override
            fun onClick(v: View) {
                showAnswerScroll(finalAnswercode)
            }
        })
        val explanation = mCursor!!.getString(mCursor!!.getColumnIndex(CConstants.EXPLAIN))
        if (TextUtils.isEmpty(explanation)) {
            btnExplain!!.setVisibility(View.GONE)
        } else {
            btnExplain!!.setVisibility(View.VISIBLE)
            mExplanation = explanation
        }

        invalidateOptionsMenu()
        val id = mCursor!!.getInt(mCursor!!.getColumnIndex("_id"))
        val isFav = isBookmarked(id)
        val btnFav = findViewById(R.id.favorites) as ImageButton
        if (isFav) {
            btnFav.setImageDrawable(getResources().getDrawable(R.drawable.favon))
        } else {
            btnFav.setImageDrawable(getResources().getDrawable(R.drawable.favoff))
        }
    }

    private fun moveToQuestion(number: Int) {
        mCursor?.moveToPosition(number)
        showCurrentQuestion()
    }

    private fun showExplainDialog(explanation: String?) {
        val bldr = AlertDialog.Builder(this, R.style.DialogTheme)

        bldr.setMessage(explanation)
        bldr.setPositiveButton("OK", null)
        bldr.show()
    }


    override
    protected fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mCursor != null) {
            outState.putInt(CURSOR_POS, mCursor!!.getPosition())
        }
    }

    companion object {
        private val CURSOR_POS = "CursorPosition"
    }

}
class AnswerDialog(val answer: String?) : DialogFragment() {

    private lateinit var  ansMessage:String



    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it,R.style.AlerDialogCustom)
            // Get the layout inflater
        //    val inflater = requireActivity().layoutInflater;

            // Inflate and set the layout for the dialog
            // Pass null as the parent view because its going in the dialog layout
       //     builder.setView(inflater.inflate(R.layout.qncode2, null))

            builder.setMessage(answer)

                builder.setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                       this.dismiss()
                    })

            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
