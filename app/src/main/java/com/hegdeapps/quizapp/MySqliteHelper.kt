package com.hegdeapps.quizapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.text.TextUtils
import android.util.Log


import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

/**
 * Created by usha on 06-12-2015.
 */
class MySqliteHelper
/*   // Post Table Columns
    private static final String KEY_POST_ID = "id";
    private static final String KEY_POST_USER_ID_FK = "userId";
    private static final String KEY_POST_TEXT = "text";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";*/


// String DB_PATH ;


/**
 * Constructor should be private to prevent direct instantiation.
 * Make a call to the static method "getInstance()" instead.
 */
    (private val mContext: Context) :
    SQLiteAssetHelper(mContext, DATABASE_NAME, null, DATABASE_VERSION) {
    internal var tablesPopulated: Boolean = false

    val favoriteObjQns: Cursor
        get() {
            val db = getReadableDatabase()
            val whereCondition = "Bookmarked = 1"
            return db.query(false, OBJECTIVE, null, whereCondition, null, null, null, "topic", null)
        }

    /*String mTopic*/ val favoriteDesQns: Cursor
        get() {
            val db = getReadableDatabase()
            val whereCondition = "Bookmarked = 1"
            return db.query(
                false,
                DESCRIPTIVE,
                null,
                whereCondition,
                null,
                null,
                null,
                "topic",
                null
            )
        }

    val objFavCount: Int
        get() {
            val db = getReadableDatabase()
            val whereCondition = "Bookmarked = 1"
            val c = db.query(false, OBJECTIVE, null, whereCondition, null, null, null, null, null)
            val count = c.getCount()
            c.close()
            return count
        }

    val desFavCount: Int
        get() {
            val db = getReadableDatabase()
            val whereCondition = "Bookmarked = 1"
            val c = db.query(false, DESCRIPTIVE, null, whereCondition, null, null, null, null, null)
            val count = c.getCount()
            c.close()
            return count
        }

    val allObjectiveQustions: Cursor
        get() {
            val database = getReadableDatabase()
            return database.query(OBJECTIVE, null, null, null, null, null, null)
        }

    public  fun topicNames(): Cursor {

            val database = getReadableDatabase()
            val qry = "Select distinct topic from questions where topic is not null;"
            return database.rawQuery(qry, null)

    }

    /*   // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    //    db.setForeignKeyConstraintsEnabled(true);
    }*/

    /*
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            copyDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/

    /*    @Override
    public void onCreate(SQLiteDatabase db) {

    }*/

    /*  // Called when the database is created for the FIRST time.
        // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
        @Override
        public void onCreate(SQLiteDatabase db) {

        }

        // Called when the database needs to be upgraded.
        // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
        // but the DATABASE_VERSION is different than the version of the database that exists on disk.

        // Insert a post into the database
        public void addQuestion(String statement) {
            // Create and/or open the database for writing
            SQLiteDatabase db = getWritableDatabase();

            // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
            // consistency of the database.
            db.beginTransaction();
            try {

                    db.execSQL(statement);



                db.setTransactionSuccessful();
            } catch (SQLException e) {
                Log.d("cqns", "Error while trying to add post to database");
            } finally {
                db.endTransaction();
            }
        }


        int numQuestions(){
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery("select * from questions;",null);
            int count = c.getCount();
            c.close();
            return count;
        }

        public Cursor getQuestionsOfTopic(String tableName,String topic) {
            Cursor c;
            SQLiteDatabase db = getReadableDatabase();

            if(topic==null){
                c = getAllQuestions(tableName);
            }else {
                c = db.query(tableName, null,
                        "Topic = '" + topic + "'", null, null, null, null, null);
            }

            return c;

        }
        public Cursor getQuestionsWithText(String tableName, String searchText) {
            Cursor c;
            SQLiteDatabase db = getReadableDatabase();

            if(searchText==null){
                c = getAllQuestions(tableName);
            }else {

                c = db.rawQuery("Select * from " + tableName + " where question like " + "'%" + searchText.trim() + "%'", null);
            }

            return c;

        }*/
    /*
    public void create( )
    {
        boolean mDataBaseExists = checkDataBase();
        if(!mDataBaseExists)
        {
            SQLiteDatabase db = this.getReadableDatabase();
            db.close();
            try
            {
                copyDataBase();
                Log.w("DSQns", "createDatabase database created");
            }
            catch (IOException mIOException)
            {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }
    //Check that the database exists here: /data/data/your package/databases/Da Name
    private boolean checkDataBase()
    {
        DB_PATH= "/data/data/" + mContext.getPackageName() + "/databases/";
        File dbFile = new File(DB_PATH + DATABASE_NAME);
        //Log.v("dbFile", dbFile + "   "+ dbFile.exists());
        return dbFile.exists();
    }

    //Copy the database from assets
    private void copyDataBase() throws IOException
    {
        DB_PATH= "/data/data/" + mContext.getPackageName() + "/databases/";
        InputStream mInput = mContext.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer))>0)
        {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }*/
    override
    fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_OBJECTIVE_QUESTIONS")
            onCreate(db)
        }
    }


    internal fun getAllQuestions(tablename: String): Cursor? {
        try {
            val db = getReadableDatabase()


            return db.rawQuery("Select * from $tablename", null)
        } catch (e: SQLException) {
            if (BuildConfig.DEBUG) {
                Log.d("C qns", "unable to open database" + e.message)
            }
            return null
        }


    }

    fun getDesQnsOfTopic(topic: String): Cursor {
        val c: Cursor
        val db = getReadableDatabase()
        c = db.rawQuery(
            "Select * from $DESCRIPTIVE where topic =  '$topic'",
            null
        )
        return c
    }

    fun getObjQuestionOfTopic(topic: String): Cursor {
        val db = getReadableDatabase()
        val c: Cursor
        c = db.rawQuery("Select * from $OBJECTIVE where topic = '$topic'", null)
        return c
    }

    fun getFilteredObjQuestions(topic: String, searchString: String): Cursor {
        val db = getReadableDatabase()
        val search = "%$searchString%"
        return db.rawQuery(
            "Select * from " + OBJECTIVE +
                    " where topic ='" + topic + "' AND question LIKE '" + search + "'", null
        )
    }

    fun getFilteredCodingQuestions(topic: String, s: String): Cursor {
        val db = getReadableDatabase()
        val search = "%$s%"
        return db.rawQuery(
            "Select * from " + DESCRIPTIVE +
                    " where topic ='" + topic + "' AND question LIKE '" + search + ";'", null
        )
    }

    fun getTotalObjectiveQuestions(topic: String): Int {
        var total = 0
        val db = getReadableDatabase()
        val c = db.rawQuery("Select  count(*) from $OBJECTIVE where topic = '$topic' ;", null)
        if (c.moveToFirst()) {
            total = c.getInt(0)
        }
        c.close()
        return total
    }

    /**
     *
     * @param topic
     * @return
     */
    fun getTotalCodingQuestions(topic: String): Int {
        var total = 0
        val db = getReadableDatabase()
        val c = db.rawQuery("Select  count(*) from $DESCRIPTIVE where topic = '$topic' ;", null)
        if (c.moveToFirst()) {
            total = c.getInt(0)
        }
        c.close()
        return total
    }

    /**
     * the related question numbers are stored in data base as the field numb. But numb should start with 0
     * Used only for descriptive qns
     * @param relqn1 number of related question (numb field)
     * @param topic topic such as linked list etc. Array has no related qns
     * @return  The entire question with that number.
     */

    fun getRelatedQuestion(relqn1: String, topic: String): String? {
        val db = getReadableDatabase()
        val c =
            db.rawQuery("Select * from " + DESCRIPTIVE + " where  topic = '" + topic + "'  and   numb =" + relqn1.trim { it <= ' ' } + ';'.toString(),
                null)
        if (c.moveToFirst()) {
            return c.getString(c.getColumnIndex(CConstants.QUESTION))
        }
        c.close()
        return null
    }

    fun addQnToFav(id: Int) {
        val db = getWritableDatabase()
        val cv = ContentValues()
        cv.put(CConstants.IS_BOOKMARKED, 1)
        db.update(DESCRIPTIVE, cv, "_id = $id", null)

    }

    fun removeQnFromFavs(id: Int) {
        val db = getWritableDatabase()
        val cv = ContentValues()
        cv.put(CConstants.IS_BOOKMARKED, 0)
        db.update(DESCRIPTIVE, cv, "_id = $id", null)
    }

    fun addObjQnsToFavs(id: Int) {
        val db = getWritableDatabase()
        val cv = ContentValues()
        cv.put(CConstants.IS_BOOKMARKED, 1)
        db.update(OBJECTIVE, cv, "_id = $id", null)
    }

    fun removeObjQnsFromFavs(id: Int) {
        val db = getWritableDatabase()
        val cv = ContentValues()
        cv.put(CConstants.IS_BOOKMARKED, 0)
        db.update(OBJECTIVE, cv, "_id = $id", null)
    }

    fun getObjectiveQnBookMarkStatus(id: Int): Boolean {
        val db = getReadableDatabase()
        val whereCondition = "_id =$id"
        val c = db.query(false, OBJECTIVE, null, whereCondition, null, null, null, "topic", null)
        if (c.moveToFirst()) {
            val m = c.getInt(c.getColumnIndex(CConstants.IS_BOOKMARKED))
            return 1 == m
        }
        c.close()
        return false
    }

    fun getDesQnBookmarkStatus(id: Int): Boolean {
        val db = getReadableDatabase()
        val whereCondition = "_id =$id"
        val c = db.query(false, DESCRIPTIVE, null, whereCondition, null, null, null, "topic", null)
        if (c.moveToFirst()) {
            val m = c.getInt(c.getColumnIndex(CConstants.IS_BOOKMARKED))
            return 1 == m
        }
        c.close()
        return false
    }

  /*  fun getCodingQuestionsOfDifficulty(topic: String?, difficulty: String): Cursor {
        val db = getReadableDatabase()
        val whereCondition: String
        if (difficulty == ALL) {
            whereCondition = " topic ='$topic'"
        } else {
            whereCondition = " topic ='$topic' AND difficulty = '$difficulty'"
        }
        return db.query(false, DESCRIPTIVE, null, whereCondition, null, null, null, "topic", null)
    }
*/
    fun getFilteredLeveledCodingQuestions(
        topic: String,
        search: String?
    ): Cursor {
        val database = getReadableDatabase()
        val c: Cursor
        var whereCondition: String
        //if (difficulty == null || difficulty == ALL) {
            whereCondition = " topic ='$topic'"
       // } else {
       //     whereCondition = " topic ='$topic' AND difficulty = '$difficulty'"
   //     }*/
        if (search != null && !TextUtils.isEmpty(search)) {
            val search1 = "%$search%"
            whereCondition = "$whereCondition AND question LIKE '$search1'"
        }
        val queryString = "Select * from questions where $whereCondition   "
        c = database.rawQuery(queryString, null)
        // c = database.query(DESCRIPTIVE,null,whereCondition,null,null,null,null);

        return c
    }

    companion object {
        // Database Info
        private val DATABASE_NAME = "questions.db"
        private val DATABASE_VERSION = 1

        // Table Names
        private val TABLE_QUESTIONS = "questions"
        private val TABLE_OBJECTIVE_QUESTIONS = "objectivequestions"
        private val DESCRIPTIVE = "questions"
        private val OBJECTIVE = "objectivequestions"
        private val ALL = "All"
    }

    /* public void createtables() {
        SQLiteDatabase db = getWritableDatabase();
    }*/
}