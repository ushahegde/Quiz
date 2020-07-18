package com.hegdeapps.quizapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager

import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.bottomnavigation.BottomNavigationView


class HomeActivity2 : AppCompatActivity/* implements View.OnClickListener */() {

    private var mTitle: TextView? = null
    private var listView: ListView? = null
    internal var arr: Array<String>?=null
    //  String[] arr = CConstants.topicArray; //included favorites 12/3/18
    private var mQuestionType: String? = null
    private val mTotalQnsArr: IntArray? = null
    private var mTotalQnsArrMcq: IntArray? = null
    private var mTotalQnsArrCoding: IntArray? = null
    private val mSettingsDialog: Dialog? = null
    private var mScoreArray: IntArray? = null
    internal var mAdapter: TopicListAdapter?=null
    private val buttonsLayout: LinearLayout? = null
    private val topicsLayout: LinearLayout? = null
    private val tvQuestionType: TextView? = null

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_coding -> {
                    showListView()
                    mQuestionType = resources.getString(R.string.coding)
                    mTitle!!.text = mQuestionType
                    mAdapter =
                        arr?.let { TopicListAdapter(this@HomeActivity2, it, mTotalQnsArrCoding!!) }
                    listView!!.adapter = mAdapter
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_objective -> {
                    showListView()
                    mQuestionType = resources.getString(R.string.mcq)
                    mTitle!!.text = mQuestionType
                    // getScoreArray(); //TODO i don't think we need it here.
                    mAdapter = arr?.let {
                        TopicListAdapter(this@HomeActivity2,
                            it, mTotalQnsArrMcq!!)
                    }
                    listView!!.adapter = mAdapter
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_test -> {
                    hideListView()
                    mTitle!!.setText(R.string.quiz)
                    startQuizActivity()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }
    private var mHelper: MySqliteHelper? = null

    private fun hideListView() {
        val lytTopicList = findViewById<View>(R.id.topicListLayout) as LinearLayout
        lytTopicList.visibility = View.GONE
        val lytTestDescription = findViewById<View>(R.id.testdDescritptionLayout) as LinearLayout
        lytTestDescription.visibility = View.VISIBLE
    }

    private fun showListView() {
        val lytTopicList = findViewById<View>(R.id.topicListLayout) as LinearLayout
        lytTopicList.visibility = View.VISIBLE
        val lytTestDescription = findViewById<View>(R.id.testdDescritptionLayout) as LinearLayout
        lytTestDescription.visibility = View.GONE
    }
    /*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);

        mTitle = (TextView) findViewById(R.id.title);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }*/


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.dsmain, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            /*   case R.id.action_settings: showSettings();
                break;*/
            R.id.action_showstat -> showBarGraph()
            R.id.action_feedback -> startFeedbackScreen()
            R.id.action_rateapp -> startRateApp()
        }/*   case R.id.action_otherapps:showOtherHegdeapps();
                break;*//*   case R.id.action_about:showAboutScreen();
                break;*/
        return true
    }

    private fun startRateApp() {
        //link to the app in play store. Last part is package name of app
        val url =
            "appgoogleplayurl"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }


    /*    private void showOtherHegdeapps() {
        String url  = "https://play.google.com/store/apps/developer?id=Hegdeapps";
        Intent intent = new  Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }*/

    private fun startFeedbackScreen() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(CConstants.DEV_MAIL_ID))
        intent.putExtra(
            Intent.EXTRA_SUBJECT,
            "Feedback on " + resources.getString(R.string.app_name)
        )
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GRAPH_ACT) {
            if (resultCode == CConstants.NO_GRAPH_DATA) {
                Toast.makeText(
                    this,
                    "Insufficient data for graph\n Did you not complete the quiz on any topic?!!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showBarGraph() {
        val intent = Intent()
        intent.setClass(this, DrawGraph::class.java)
        startActivityForResult(intent, GRAPH_ACT)
    }

    /*private void showSettings() {
            mSettingsDialog= new Dialog(this);
            mSettingsDialog.setContentView(R.layout.settings);
            Button btnSerif, btnIndieFlower,btnShortStock;
            btnSerif = (Button) mSettingsDialog.findViewById(R.id.btnSerif);
            btnIndieFlower = (Button) mSettingsDialog.findViewById(R.id.btnindiflower);
            btnShortStock = (Button) mSettingsDialog.findViewById(R.id.btnShortStack);

            Button btnLarge = (Button) mSettingsDialog.findViewById(R.id.btnTextSizeLarge);
            Button btnMedium = (Button) mSettingsDialog.findViewById(R.id.btnTextSizeMedium);
            Button btnSmall = (Button) mSettingsDialog.findViewById(R.id.btnTextSizeSmall);
            btnSerif.setOnClickListener(this);
            btnShortStock.setOnClickListener(this);
            btnIndieFlower.setOnClickListener(this);

            btnLarge.setOnClickListener(this);
            btnMedium.setOnClickListener(this);
            btnSmall.setOnClickListener(this);
            Typeface indieflower = Typeface.createFromAsset(getAssets(),CConstants.INDI_FLOWER);
            Typeface shortStack = Typeface.createFromAsset(getAssets(),CConstants.SHORT_STAK);
            btnIndieFlower.setTypeface(indieflower);
            btnShortStock.setTypeface(shortStack);
            mSettingsDialog.show();
        }
    */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)
        mHelper = MySqliteHelper(this)
        val c = mHelper!!.topicNames()
        arr = getTopicNames(c)
        c.close()

        mTitle = findViewById<View>(R.id.title) as TextView
        val navigation = findViewById<View>(R.id.navigation) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        mQuestionType = resources.getString(R.string.coding)
        //  navigation.setItemBackgroundResource(R.drawable.buttonsel);

        //      setContentView(R.layout.content_dsmain2);
        //    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  toolbar.setTitle("C Questions");
        //    setSupportActionBar(toolbar);


        listView = findViewById<View>(R.id.topics_list) as ListView

        /*     new ArrayAdapter<String>(this, R.layout.topics_row, R.id.textView1, arr));*/
        listView!!.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                showQuestionsOnTopic(position)
            }

        val btn1: Button
        val btn2: Button
        val btn3: Button

        mQuestionType = resources.getString(R.string.coding)
        mTotalQnsArrMcq = IntArray(arr!!.size)
        mTotalQnsArrCoding = IntArray(arr!!.size)
        findNumQuestions(1)
        findNumQuestions(2)



        mAdapter = TopicListAdapter(this, arr!!, mTotalQnsArrCoding!!)
        listView!!.adapter = mAdapter
        mScoreArray = IntArray(arr!!.size)

        /*    MobileAds.initialize(this, "ca-app-pub-2080283086957033~8571920204");*/

        /* Typeface tfPeaceSans = Typeface.createFromAsset(getAssets(),"peacesans.otf");
            if(tfPeaceSans!=null){
                btn1.setTypeface(tfPeaceSans);
                btn2.setTypeface(tfPeaceSans);
                btn3.setTypeface(tfPeaceSans);
            }*/

        //    topicsLayout = (LinearLayout) findViewById(R.id.topiclayout);
        //     buttonsLayout = (LinearLayout)findViewById(R.id.buttonslayout);
        //    tvQuestionType = (TextView)findViewById(R.id.tvType);
        //     MobileAds.initialize(this,getResources().getString(R.string.admob_appid));
        //initially we are using coding
    }

    private fun getTopicNames(c: Cursor): Array<String>? {
        val size = c.count + 1
        val arr = Array<String>(size,{i->i.toString()})
        var i=1
        arr[0]="Favorites"
        c.moveToFirst()
        while (!c.isAfterLast) {
            val str = c.getString(c.getColumnIndex(CConstants.TOPIC))
            arr[i++]=str
            c.moveToNext()
        }

       return arr
    }

    //TODO add handling of favorites here
    private fun showQuestionsOnTopic(position: Int) {
        if(position==null||arr==null)
            return;
        else {
            if (arr!![position] == "Favorites") {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra(CConstants.TOPIC, arr!![position])
                if (mQuestionType == "Coding") {
                    intent.putExtra(CConstants.TYPE, CConstants.CODING)
                } else {
                    intent.putExtra(CConstants.TYPE, CConstants.MCQ)
                }
                startActivity(intent)
            }
            if (mQuestionType == resources.getString(R.string.coding)) {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra(CConstants.TYPE, CConstants.CODING)
                intent.putExtra(CConstants.TOPIC, arr!![position])
                startActivity(intent)
            } else if (mQuestionType == resources.getString(R.string.mcq)) {
                val intent = Intent(this, NavigationActivity::class.java)
                intent.putExtra(CConstants.TYPE, CConstants.MCQ)
                intent.putExtra(CConstants.TOPIC, arr!![position])
                startActivity(intent)
            }
        }
    }

    /*   @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               int id = buttonView.getId();
               if(isChecked){
                   switch(id){
                       case R.id.coding: mQuestionType = "Coding";
                           break;
                       case R.id.mcq: mQuestionType = "MCQ";
                           break;
                       case R.id.rdQuiz:mQuestionType = "Quiz";
                       break;
                   }
                   if (mQuestionType.equals("Quiz")){
                       startQuizActivity();
                       return;
                   }
                    if("Coding" .equals( mQuestionType)) {
                       mAdapter = new TopicListAdapter(this, arr, mTotalQnsArrCoding);
                   }else{
                       getScoreArray();
                       mAdapter = new TopicListAdapter(this, arr, mTotalQnsArrMcq);
                   }
                   listView.setAdapter(mAdapter);
               }
           }
       */
    private fun startQuizActivity() {
        val btn = findViewById<View>(R.id.startTest) as Button
        btn.setOnClickListener {
            val intent = Intent(this@HomeActivity2, QuizActivity::class.java)
            startActivity(intent)
        }

    }

    private fun getScoreArray() {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        for (i in arr!!.indices) {
            val str = arr!![i] + " Score"
            val score = pref.getInt(str, -1)
            mScoreArray!![i] = score
        }
    }

    internal fun findNumQuestions(type: Int) {

        for (i in arr!!.indices) {
            val numQns: Int
            if (arr!![i] == null)
                continue
            if (1 == type) {//mQuestionType.equals("MCQ")) {

                numQns = mHelper!!.getTotalObjectiveQuestions(arr!![i])
                mTotalQnsArrMcq!![i] = numQns
            } else {
                numQns = mHelper!!.getTotalCodingQuestions(arr!![i])
                mTotalQnsArrCoding!![i] = numQns
            }
        }
        if (type == 1) {
            val num = mHelper!!.objFavCount
            mTotalQnsArrMcq!![FAVORITES_INDEX] = num
        } else {
            val num = mHelper!!.desFavCount
            mTotalQnsArrCoding!![FAVORITES_INDEX] = num
        }
    }

    /*    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.btnSerif : saveTypefaceToPref(CConstants.SERIF);
                break;
            case R.id.btnindiflower:saveTypefaceToPref(CConstants.INDI_FLOWER);
                break;
            case R.id.btnShortStack : saveTypefaceToPref(CConstants.SHORT_STAK);
                break;
            case R.id.btnTextSizeLarge:saveTextSizeToPref(CConstants.LARGE_TEXT);
                break;
            case R.id.btnTextSizeMedium:saveTextSizeToPref(CConstants.MEDIUM_TEXT);
                break;
            case R.id.btnTextSizeSmall:saveTextSizeToPref(CConstants.SMALL_TEXT);
                break;
        }
    }*/

    private fun saveTextSizeToPref(textSize: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        editor.putString(CConstants.TEXTSIZE, textSize)
        editor.commit()
    }

    private fun saveTypefaceToPref(typefaceName: String) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = pref.edit()
        editor.putString(CConstants.TYPEFACE, typefaceName)
        editor.commit()
    }

    /*@Override
        public void onClick(View v) {
            int id = v.getId();
            if(id==R.id.rdQuiz){
                startQuizActivity();
                return;
            }
            else if((id == R.id.coding) || (id == R.id.mcq)){
                topicsLayout.setVisibility(View.VISIBLE);
                buttonsLayout.setVisibility(View.GONE);
            }
            if(id==R.id.coding){
                tvQuestionType.setText("CODING QUESTIONS");
                mQuestionType = "Coding";
                mAdapter =new TopicListAdapter(this, arr, mTotalQnsArrCoding);
            }
            else if (id==R.id.mcq) {
                tvQuestionType.setText("OBJECTIVE QUESTIONS");
                mQuestionType = "MCQ";
                getScoreArray();
                mAdapter = new TopicListAdapter(this, arr, mTotalQnsArrMcq);
            }
            listView.setAdapter(mAdapter);
        }
*/
    internal inner class TopicListAdapter(
        var mContext: Context,
        array: Array<String>,
        total: IntArray
    ) : BaseAdapter() {
        var topicsArray: Array<String>
        var totalQnsArray: IntArray
        var mInflater: LayoutInflater

        init {
            this.topicsArray = array.clone()
            this.totalQnsArray = total.clone()
            mInflater = LayoutInflater.from(mContext)
        }

        override fun getCount(): Int {
            return topicsArray.size
        }

        override fun getItem(position: Int): Any {
            return topicsArray[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.topics_row, null)
            }
            val tv1 = convertView!!.findViewById<View>(R.id.topicname) as TextView
            val tv2 = convertView.findViewById<View>(R.id.num_qns) as TextView
            tv1.text = topicsArray[position]
            val m = totalQnsArray[position]
            //    int score = mScoreArray[position];
            tv2.text = m.toString()
            /*if(mQuestionType.equals("MCQ")){
                if(score>0) {
                    tv2.setText(String.valueOf(score) + "/" + String.valueOf(m));
                }
            }*/
            return convertView
        }
    }

    fun closeSettingsDialog(v: View) {
        mSettingsDialog?.dismiss()
    }

    override fun onResume() {
        val helper = MySqliteHelper(this)
        var num = helper.objFavCount
        mTotalQnsArrMcq!![FAVORITES_INDEX] = num

        num = helper.desFavCount
        mTotalQnsArrCoding!![FAVORITES_INDEX] = num
        mAdapter!!.notifyDataSetChanged()
        listView!!.refreshDrawableState()
        super.onResume()
    }

    companion object {
        private val FAVORITES_INDEX = 0//favorites option is 0 in the list
        private val GRAPH_ACT = 200
    }

    /* private void updateFavoriteCount() {

        }*/
}


