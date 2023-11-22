package com.example.barta_a_messenger_app;



import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "messenger_db";
    private static int DATABASE_VERSION = 3;

    public static String sender_table_name , receiver_table_name;
    Context context;
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Toast.makeText(context,"OnCreate is called",Toast.LENGTH_SHORT).show();
        Log.d("DBHelper","onCreate called");
        String createTableQuery = "CREATE TABLE IF NOT EXISTS "+sender_table_name+
                            " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                            " MESSAGE TEXT, MESSAGETYPE TEXT ," +
                            "TIMESTAMP INTEGER, SENDER_ID TEXT);";

        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+sender_table_name);
        onCreate(sqLiteDatabase);
    }


}
