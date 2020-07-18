package com.hegdeapps.quizapp

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.widget.FilterQueryProvider
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.ListFragment

class ObjQnListFragment : ListFragment() {

    private var mCursor: Cursor? = null
    private var mAdapter: ObjectiveCursorListAdapter? = null
    private var mTopic: String? = null
    private var mIsFavorites: Boolean = false
    private var mFilterText: CharSequence = ""
    private val mTextSize: String? = null


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val act = getActivity() as NavigationActivity
        // ObjQnListActivity act = (ObjQnListActivity) getActivity();
        //  Typeface tp =MyMethods.getTypefaceFromPref(getContext());
        //  mTextSize = MyMethods.getFontSizeFromPreferences(getContext());
        mCursor = act.mCursor
        mCursor!!.moveToFirst()
        mTopic = (getActivity() as NavigationActivity).mTopic
        if (mTopic == "Favorites") {
            mIsFavorites = true
        }
        //  mTopic = mCursor.getString(mCursor.getColumnIndex(CConstants.TOPIC));
        //  act.setTitle(mTopic);
        //   getListView().setDividerHeight(2);
        mAdapter = ObjectiveCursorListAdapter(act, mCursor!!)
        mAdapter!!.setFilterQueryProvider(FilterQueryProvider { constraint ->
            getCursorFromQuery(
                constraint
            )
        })
        /*set empty view*/
        val lv = getListView()
        lv.setTextFilterEnabled(true)
        val tv = getActivity()?.findViewById<TextView>(android.R.id.empty)
        lv.setEmptyView(tv)
       /* val sv = getActivity()?.findViewById<SearchView>(R.id.search)
        sv?.setIconifiedByDefault(false)
        val searchManager = getActivity()?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv?.setQueryHint("Search..")
        sv?.setSearchableInfo(searchManager.getSearchableInfo(getActivity()?.getComponentName()))
        sv?.setSubmitButtonEnabled(true)
        //   searchView.setOnQueryTextListener(this);
        sv?.setOnQueryTextListener(object : SearchView.OnQueryTextListener  {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                *//*   if(TextUtils.isEmpty(newText)){
                                                      getListView().clearTextFilter();
                                                      return true;
                                                  }else {*//*
                //getListAdapter().getFilter().filter(newText);
                (getListAdapter() as ObjectiveCursorListAdapter).getFilter().filter(newText)
                return true
                //  }
            }
        }
        )*/
        setListAdapter(mAdapter)
    }

    private fun getCursorFromQuery(filterText: CharSequence): Cursor? {
        val helper = getContext()?.let { MySqliteHelper(it) }
        if (TextUtils.isEmpty(filterText)) {
            mFilterText = ""
            if (helper != null) {
                return helper.getObjQuestionOfTopic(mTopic!!)
            }
        }
        if (null != mCursor) {
            mFilterText = filterText
//TODO use mCursor here??
            return helper?.getFilteredObjQuestions(mTopic!!, filterText.toString())
        }
        return null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.obj_qn_list_fragment, container, false)

    }


    fun refreshListView(isFavorites: Boolean) {
        if(getActivity()==null){
            return
        }
        val act = getActivity() as NavigationActivity ?: return
        mIsFavorites = isFavorites
        //    Typeface tp =MyMethods.getTypefaceFromPref(getActivity());
        mCursor = act.mCursor
        //   if(! mCursor.moveToFirst()){
        //        return;
        //  }
        //   mTopic = mCursor.getString(mCursor.getColumnIndex(CConstants.TOPIC));
        //   act.setTitle(mTopic);
        //   getListView().setDividerHeight(2);
        mAdapter = ObjectiveCursorListAdapter(act, mCursor!!)
        mAdapter!!.setFilterQueryProvider(FilterQueryProvider { constraint ->
            getCursorFromQuery(
                constraint
            )
        })
        setListAdapter(mAdapter)
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val intent = Intent()
        getActivity()?.let { intent.setClass(it, ObjQnDetailsActivity::class.java) }
        intent.putExtra(CConstants.CURSOR_POSITION, position)
        mTopic = (getActivity() as NavigationActivity).mTopic
        intent.putExtra(CConstants.TOPIC, mTopic)
        // String str = mAdapter.getFilter().toString();
        if (mIsFavorites) {
            intent.putExtra(CConstants.FAVORITES, true)
        }
        if (!TextUtils.isEmpty(mFilterText)) {
            intent.putExtra(CConstants.FILTER_TEXT, mFilterText)
        }
        startActivity(intent)
        //todo take care of search filter
        //
        /*    if(mListener!=null)
            mListener.OnItemSelected(position);*/
    }
    override
   fun   onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_nav,menu);
        val item:MenuItem = menu.findItem(R.id.action_search);
        val searchView:SearchView = item.getActionView() as SearchView
     //   searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener( object: SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query:String):Boolean {
                 return false;
                }


              override fun onQueryTextChange(  newText:String):Boolean {
                if(TextUtils.isEmpty(newText)){
                    return false;
                }else {
                    ( getListAdapter() as ObjectiveCursorListAdapter) .getFilter().filter(newText);
                    return false;
                }
            }
        }
        );
    }
}
