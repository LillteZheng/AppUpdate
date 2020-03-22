package com.zhengsr.zdwon_lib.entrance.imp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public static String URL = "url";
    public static String THREADID = "threadId";
    public static String THREAD_LENGTH = "threadLength";
    public static String THREAD_START = "startPos";
    public static String THREAD_END = "endPos";
    private static final String TABLE = "zdown.db";
    public static final String BOOK_TABLE = "zdown";
    private static final String CREATE_TABLE = "create table "+BOOK_TABLE+" ("
            + "_id integer primary key autoincrement, "
            + URL+" text, "
            + THREADID +" integer, "
            + THREAD_START +" integer, "
            + THREAD_END +" integer, "
            + THREAD_LENGTH+" integer)";
    /**
     * 这个是初始化的时候，创建数据库的，为了方便，这里就只要传入上下文即可
     * @param context
     */
    public DBHelper(Context context) {
        super(context, TABLE, null, 4); //1为版本号
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    //更新的时候执行
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}