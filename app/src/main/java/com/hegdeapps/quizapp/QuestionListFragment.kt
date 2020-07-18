package com.hegdeapps.quizapp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.ListFragment

class QuestionListFragment : ListFragment(), SearchView.OnQueryTextListener     {
    private val mContext: Context?=null
    private var mCursor: Cursor? = null
    private var mTopic: String? = null
    internal var mAdapter: QnListAdapter? = null
    //   private Typeface mTypeface;
    private var mNeedToRefresh: Boolean = false
    private var mSqliteHelper: MySqliteHelper? = null
    private var mQnAdapter: QnListAdapter? = null
    private var mShowingFavorites: Boolean = false
    private var mFilterText = ""
    private var mDifficultySet: Boolean = false
    private var mDifficulty = "All"
    private var mSettingsDialog: Dialog? = null

    private val questionId: Int
        get() = if (null != mCursor && mCursor!!.moveToFirst()) {
            mCursor!!.getInt(mCursor!!.getColumnIndex("_id"))
        } else -1

    override
    fun onActivityCreated(  savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mSqliteHelper = getActivity()?.let { MySqliteHelper(it) }
        mTopic = (getActivity() as NavigationActivity).mTopic
        if (mTopic!!.equals("Favorites")) {
            mShowingFavorites = true
        }
        val act = getActivity() as NavigationActivity
        mCursor = act.mCursor
        mCursor!!.moveToFirst()
        act.setTitle(mTopic)

        mQnAdapter = QnListAdapter(act, R.layout.qn_list_row_new, mCursor!!)
        mQnAdapter!!.setFilterQueryProvider(object : FilterQueryProvider {
           override
            fun runQuery(constraint: CharSequence): Cursor? {
                return getCursorFromQuery(constraint)
            }
        })

        val lv = getListView()
        val tv = getActivity()?.findViewById<TextView>(android.R.id.empty)

        lv.setEmptyView(tv)
        setListAdapter(mQnAdapter)
     /*   val searchView = getActivity()?.findViewById<SearchView>(R.id.search)
        if(searchView!=null) {
            searchView.setIconifiedByDefault(false)
            searchView.setSubmitButtonEnabled(true)
            searchView.setOnQueryTextListener(this)
        }*/
        /* searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)) {
                    ((QnListAdapter) getListAdapter()).getFilter().filter(newText);
                }
                //(  (QnListAdapter)getListAdapter()).notifyDataSetChanged();
                return false;
            }
        }); */
        /*
        SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
        //  searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);*/
    }

    private fun getCursorFromQuery(filterText: CharSequence): Cursor? {
        val helper = getContext()?.let { MySqliteHelper(it) }
        if (TextUtils.isEmpty(filterText)) {
            mFilterText = ""
            if (helper != null) {
                return mTopic?.let { helper.getAllQuestions(  "questions") }
            }
        } else {
            mFilterText = filterText.toString()
            if (helper != null) {
                return mTopic?.let { helper.getFilteredLeveledCodingQuestions(
                    it,
                    filterText.toString()
                ) }
            }
        }
        return null
    }

    fun refreshListView(cur: Cursor) {
        mCursor = cur
        if (null == getActivity()) {
            mNeedToRefresh = true
            return
        }

        mQnAdapter = getActivity()?.let { QnListAdapter(it, R.layout.qn_list_row_new, cur) }

        mQnAdapter!!.notifyDataSetChanged()
        getListView().setAdapter(mQnAdapter)
        getListView().refreshDrawableState()
    }

    override
    fun onCreate(  savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        //    mTypeface =MyMethods.getTypefaceFromPref(getContext());
    }

    override
    fun onCreateView (inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater?.inflate(R.layout.qnlistfragment, container, false)!!
    }

    override
    fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        //     int qnId = getQuestionId();
        val intent = Intent()
        getActivity()?.let { intent.setClass(it, QuestionDetailActivity::class.java) }
        //   int pos = mCursor.getPosition();
        intent.putExtra(CConstants.CURSOR_POSITION, position)
        val topic = (getActivity() as NavigationActivity).mTopic
        intent.putExtra(CConstants.TOPIC, topic)
        if (mShowingFavorites) {
            intent.putExtra(CConstants.FAVORITES, true)
        }
        if (!TextUtils.isEmpty(mFilterText)) {
            intent.putExtra(CConstants.FILTER_TEXT, mFilterText)
        }
        if (mDifficultySet) {
            intent.putExtra(CConstants.DIFFICULTY_LEVEL, mDifficulty)
        }
        startActivity(intent)
        //todo take care search option
    }

    /*@Override
    public void onAttach(Context context) {
        super.onAttach(context);
     *//*   if(mNeedToRefresh) {
            refreshListView();
            mNeedToRefresh = false;
        }*//*
    }*/
    override
    fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_nav, menu)
        val  item:MenuItem= menu.findItem(R.id.action_search);
        val searchView:SearchView = item.getActionView() as SearchView;
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener( object: SearchView.OnQueryTextListener  {
            override
              fun onQueryTextSubmit( query:String):Boolean {
              return false;
            }
            override
            fun   onQueryTextChange( newText:String):Boolean {
                if(!TextUtils.isEmpty(newText)) {
                    (  getListAdapter() as QnListAdapter).getFilter().filter(newText);
                }
                //(  (QnListAdapter)getListAdapter()).notifyDataSetChanged();
                return false;
            }
        })

       /* SearchManager searchManager = (SearchManager)
                getActivity().getSystemService(Context.SEARCH_SERVICE);
      //  searchMenuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();

        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);*/
    }

 /*   override
    fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        when (id) {
            R.id.action_settings -> showSettingsDialog()
        }
        return true
    }*/

  /*  private fun showSettingsDialog() {
        mSettingsDialog = getActivity()?.let { Dialog(it, R.style.DialogTheme) }
        mSettingsDialog!!.setContentView(R.layout.settings1)
        val rbEasy: RadioButton
        val rbAll: RadioButton
        val rbMedium: RadioButton
        val rbDifficult: RadioButton
      *//*  rbEasy = mSettingsDialog!!.findViewById(R.id.radioButtonEasy) as RadioButton
        rbAll = mSettingsDialog!!.findViewById(R.id.radioButtonAll) as RadioButton
        rbMedium = mSettingsDialog!!.findViewById(R.id.radioButtonMedium) as RadioButton
        rbDifficult = mSettingsDialog!!.findViewById(R.id.radioButtonDificult) as RadioButton*//*

       *//* rbEasy.setOnCheckedChangeListener(this)
        rbAll.setOnCheckedChangeListener(this)
        rbMedium.setOnCheckedChangeListener(this)
        rbDifficult.setOnCheckedChangeListener(this)*//*
        mSettingsDialog!!.show()
    }
*/
    fun setFavoriteAdapter(c: Cursor) {
        mCursor = c
        val ctx = getActivity()
        // mCursor =( (NavigationActivity)getActivity()).mCursor;
        val adapter = object : CursorAdapter(ctx, mCursor) {
            override
            fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
                val inflater = LayoutInflater.from(mContext)
                return inflater.inflate(R.layout.favorite_qn_list_row_new, null)
            }

            override
            fun bindView(view: View, context: Context, cursor: Cursor) {
                val pos = cursor.getPosition()
                val tv = view.findViewById(R.id.question) as TextView
                val question = cursor.getString(cursor.getColumnIndex("question"))
                tv.setText((pos+1).toString()+ '.' + question)
            }
        }
        getListView().setAdapter(adapter)
        mShowingFavorites = true
        //    TextView tv = new TextView(ctx);
        //    tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
        //                       ViewGroup.LayoutParams.MATCH_PARENT));
        //        tv.setTextSize(20.0f);
        //    getListView().setEmptyView(tv);
    }

    override
    fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    override
    fun onQueryTextChange(newText: String): Boolean {
        mQnAdapter!!.getFilter().filter(newText)
        return true
    }

   /* override
    fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        val id = buttonView.getId()
        val str = buttonView.getText().toString()
        val strAll = getActivity()?.getResources()?.getString(R.string.all)
        if (isChecked) {
            //  if(!str.equals(strAll)){
            mDifficultySet = true
            mDifficulty = str
            //    }
            when (id) {
                R.id.radioButtonEasy -> getQuestionsOfDifficulty(str)
                R.id.radioButtonAll -> getQuestionsOfDifficulty(str)
                R.id.radioButtonMedium -> getQuestionsOfDifficulty(str)
                R.id.radioButtonDificult -> getQuestionsOfDifficulty(str)
            }
            if (mSettingsDialog != null && mSettingsDialog!!.isShowing()) {
                mSettingsDialog!!.dismiss()
            }

        }


    }
*/
    /*private fun getQuestionsOfDifficulty(difficulty: String) {
        val cursor = mTopic?.let { mSqliteHelper!!.getCodingQuestionsOfDifficulty(it, difficulty) }
        mQnAdapter = null
        mQnAdapter = getActivity()?.let { cursor?.let { it1 ->
            QnListAdapter(it, R.layout.qn_list_row_new,
                it1
            )
        } }
        setListAdapter(mQnAdapter)
        mQnAdapter!!.setFilterQueryProvider(object : FilterQueryProvider  {
           override
            fun runQuery(constraint: CharSequence): Cursor? {
                return getCursorFromQuery(constraint)
            }
        })
    }
*/

    internal inner class QnListAdapter
    //        private boolean[] bookMarksArray;

    (var mContext: Context, var mLayoutId: Int, var mCursor1: Cursor)//  bookMarksArray = new boolean[c.getCount()];
        : CursorAdapter(mContext, mCursor1) {

        override
        fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
            val inflater = LayoutInflater.from(mContext)
            return inflater.inflate(mLayoutId, null)
        }

        override
        fun bindView(view: View, context: Context, cursor: Cursor) {
            val p = cursor.getPosition()// getListView().getPositionForView(view);
            val tv = view.findViewById(R.id.question) as TextView
            //    final ImageButton btn = (ImageButton) view.findViewById(R.id.bookmark);
            //   tv.setTypeface(mTypeface);
            val question = cursor.getString(cursor.getColumnIndex("question"))
            //final int id = cursor.getInt(cursor.getColumnIndex("_id"));
            //   int isFavorite = cursor.getInt(cursor.getColumnIndex("Bookmarked"));
            /*  if (1 == isFavorite) {
                btn.setImageDrawable(getResources().getDrawable(R.drawable.favon));
            } else {
                btn.setImageDrawable(getResources().getDrawable(R.drawable.favoff));
            }*/
            val text = (p + 1).toString() + ". " + question
            tv.setText(text)
            //   if(p>=0 && bookMarksArray[p]==true){
            //REMOVE FAVORITES FROM LISTVIEW AND PUT IT ONLY IN DETAILS SCREEN
            //UH 27/3/18
            /*   btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int isBookMarked = cursor.getInt(cursor.getColumnIndex("Bookmarked"));
                    if (1 != isBookMarked){
                        addQnToBooks(id);
                        btn.setImageDrawable(getResources().getDrawable(R.drawable.favon));
                    } else {
                        removeQnToBooks(id);
                        btn.setImageDrawable(getResources().getDrawable(R.drawable.favoff));
                    }
                }
            });*/
        }

        /*      private boolean isBookMarked(int id) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
            String bookMarks = pref.getString(CConstants.BOOKMARKED_IDS,null);
            if(bookMarks==null){
                return false;
            }
            String str = String.valueOf(id);
            if(bookMarks.contains(str))
        }

    }*/

    }

    companion object {
        private val EASY = 10
        private val ALL = 20
        private val MEDIUM = 30
        private val DIFFICULT = 40
    }
    /*  private void removeQnToBooks(int id) {
        MySqliteHelper helper = new MySqliteHelper(getActivity());
        helper.removeQnFromFavs(id);
        mCursor = helper.getDesQnsOfTopic(mTopic);
        mQnAdapter = new QnListAdapter(getActivity(),R.layout.qn_list_row,mCursor);
        getListView().setAdapter(mQnAdapter);
        getListView().refreshDrawableState();
        Toast.makeText(getContext(),"Question removed removed from bookmarks",Toast.LENGTH_SHORT).show();
    }*/

    /*private void addQnToBooks(int id) {
        Log.d("qnlistfragment","id is "+id);
        //mCursor.moveToPosition(pos);
      //  int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
        MySqliteHelper helper = new MySqliteHelper(getActivity());
     //   mCursor = helper.getDesQnsOfTopic(mTopic);
        //mQnAdapter = new QnListAdapter(getActivity(),R.layout.qn_list_row,mCursor);
        //getListView().setAdapter(mQnAdapter);
        helper.addQnToFav(id);
     *//*   int id = mCursor.getInt(mCursor.getColumnIndex("_id"));
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String allBookmarkedIds = pref.getString(CConstants.BOOKMARKED_IDS,"");
        allBookmarkedIds = allBookmarkedIds+" "+String.valueOf(id);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CConstants.BOOKMARKED_IDS,allBookmarkedIds);
        editor.commit();*//*
        Toast.makeText(getContext(),"Question bookmarked....",Toast.LENGTH_SHORT).show();
    }*/


}
