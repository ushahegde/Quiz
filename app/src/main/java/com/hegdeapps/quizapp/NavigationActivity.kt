package com.hegdeapps.quizapp

import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu

import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.ListFragment
import com.google.android.material.navigation.NavigationView

/*

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
*/

class NavigationActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mQnType = CConstants.CODING
    private  var mSqliteHelper: MySqliteHelper? =null
    private var qnListFragment: ListFragment? = null
    var mCursor: Cursor? = null
    var mTopic = "Array"
    internal var mObjQnListFragment: ObjQnListFragment? = null
    internal var mQnListFragment: QuestionListFragment? = null
    /*   private AdView mAdView;
*/
    override
    protected fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_nav)
        val intent = getIntent()
        if (null != intent) {
            val type = intent!!.getStringExtra(CConstants.TYPE)
            val topic = intent!!.getStringExtra(CConstants.TOPIC)
            mTopic = topic
            mQnType = type
        }
        val tb = findViewById(R.id.toolbar) as Toolbar
        if (null != tb) {
            tb!!.setTitle(mTopic)
            setSupportActionBar(tb)
            val ab = getSupportActionBar()
            if (null != ab) {
                ab!!.setDisplayShowHomeEnabled(false)
            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        val menu:Menu = navigationView.getMenu()

        //add topic names from database
        if(mSqliteHelper==null){
            mSqliteHelper = MySqliteHelper(this);
            val c:Cursor = mSqliteHelper!!.topicNames()
            c.moveToFirst()
            do{
                var str = c.getString(c.getColumnIndex("topic"))
                if(str!=null) {
                    str = str.trim()
                    if (!TextUtils.isEmpty(str)) {
                        menu.add(str)
                    }
                }
            }while(c.moveToNext())

        }
        navigationView.setNavigationItemSelectedListener(this)
        setCursor()
        /* mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("3C791098A3B50FC97E7ED72B94834A08").build();
        mAdView.loadAd(adRequest);*/
    }

    private fun setCursor() {
        mSqliteHelper = MySqliteHelper(this)
        if (mTopic.equals("Favorites")) {
            showFavoriteQuestions()
            return
        }
        if (mQnType.equals(CConstants.CODING)) {
            mCursor = mSqliteHelper!!.getDesQnsOfTopic(mTopic)
        } else {
            mCursor = mSqliteHelper!!.getObjQuestionOfTopic(mTopic)
        }
        if (null == mCursor) {
            Toast.makeText(this, "Unable to get the contents of database table...", Toast.LENGTH_SHORT).show()
            return
        }
        //     Toast.makeText(this,"Fetched "+mCursor.getCount()+"questions from database",Toast.LENGTH_SHORT).show();
        //   if(savedInstanceState==null) {   TODO do we need this?
        if (mQnType.equals(CConstants.CODING)) {
            mQnListFragment = QuestionListFragment()
            qnListFragment = mQnListFragment
        } else {
            mObjQnListFragment = ObjQnListFragment()
            qnListFragment = mObjQnListFragment
        }
        qnListFragment!!.setHasOptionsMenu(true)
        getSupportFragmentManager().beginTransaction().add(R.id.list_container, qnListFragment!!).commit()
        //  }
    }

    override
    fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (R.id.action_settings == id) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/
    private fun showQuestionsOnTopic(type: String) {
        if (mQnType.equals(CConstants.CODING)) {
            mCursor = mSqliteHelper!!.getDesQnsOfTopic(mTopic)
            (mQnListFragment as QuestionListFragment).refreshListView(mCursor!!)
        } else {
            mCursor = mSqliteHelper!!.getObjQuestionOfTopic(mTopic)
            (mObjQnListFragment as ObjQnListFragment).refreshListView(false)
        }

    }

    override
    fun onNavigationItemSelected( item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.getItemId()
        if (R.id.nav_coding === id) {
            if (!CConstants.CODING.equals(mQnType)) {
                mQnType = CConstants.CODING
                changeFragment()
            }
        } else if (R.id.nav_ob === id) {
            if (!CConstants.MCQ.equals(mQnType)) {
                mQnType = CConstants.MCQ
                changeFragment()
            }
        } /*else if (R.id.nav_favorites === id) {
            showFavoriteQuestions()
            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            drawer.closeDrawer(GravityCompat.START)
            return true
        }*/ else {
            mTopic = item.getTitle().toString()
        }
        getSupportActionBar()?.setTitle(mTopic)
        // setTitle(mTopic);
        showQuestionsOnTopic(mTopic)
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showFavoriteQuestions() {
        if (mQnType.equals(CConstants.CODING)) {
            mCursor = mSqliteHelper!!.favoriteDesQns
            getSupportActionBar()?.setTitle("Favorite Questions")//todo change this title wording??
        } else {
            mCursor = mSqliteHelper!!.favoriteObjQns
            getSupportActionBar()!!.setTitle("Favorite Questions")
        }
        if (null == qnListFragment) {
            if (mQnType.equals(CConstants.CODING)) {
                mQnListFragment = QuestionListFragment()
                qnListFragment = mQnListFragment
            } else {
                mObjQnListFragment = ObjQnListFragment()
                qnListFragment = mObjQnListFragment
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.list_container, qnListFragment!!).commit()
        } else {
            if (mQnType.equals(CConstants.CODING)) {
                (qnListFragment as QuestionListFragment).setFavoriteAdapter(mCursor!!)
            } else {
                (qnListFragment as ObjQnListFragment).refreshListView(true)
            }
        }
    }

    private fun changeFragment() {
        if (mQnType.equals(CConstants.MCQ)) {
            if (null == mObjQnListFragment) {
                mObjQnListFragment = ObjQnListFragment()
            }
            qnListFragment = mObjQnListFragment
        } else {
            if (null == mQnListFragment) {
                if (null == mCursor) {
                    mCursor = mSqliteHelper!!.getDesQnsOfTopic(mTopic)
                }
                mQnListFragment = QuestionListFragment()
            }
            qnListFragment = mQnListFragment
        }
        qnListFragment?.let {
            supportFragmentManager.beginTransaction().replace(R.id.list_container,
                it
            ).commit()
        }
        //  add(R.id.list_container, qnListFragment).commit();
    }

}
