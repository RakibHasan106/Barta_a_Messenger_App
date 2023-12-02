package com.example.barta_a_messenger_app;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "messenger_db";
    private static int DATABASE_VERSION = 15;

    public static String chat_table_name ;
    Context context;
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        Log.d("DBHelper","onCreate called");
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+chat_table_name+
                            " (MESSAGEID TEXT PRIMARY KEY," +
                            " MESSAGE TEXT, MESSAGETYPE TEXT ," +
                            "ISNOTIFIED TEXT,"+
                            "TIMESTAMP INTEGER, SENDER_ID TEXT);";

        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+chat_table_name+";");
        onCreate(sqLiteDatabase);
    }


}
