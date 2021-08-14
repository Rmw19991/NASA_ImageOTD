package com.example.imagesearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyOpener extends SQLiteOpenHelper
{

    private final static String DATABASE_NAME = "Image_Data";
    private final static int VERSION_NUM = 1;
    final static String TABLE_NAME = "USER_IMAGES";
    final static String COL_TITLE = "IMG_TITLE";
    final static String COL_DESC = "IMG_DESC";
    final static String COL_DATE = "IMG_DATE";
    final static String COL_HDURL = "HDURL";
    final static String COL_IMAGE_FILEPATH = "IMG_FILEPATH";
    final static String COL_ID = "_id";

    private SQLiteDatabase db;

    MyOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    // Create the database table if it doesn't exist
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " text,"
                + COL_DESC + " text,"
                + COL_DATE + " text,"
                + COL_HDURL + " text,"
                + COL_IMAGE_FILEPATH  + " text);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(db);
    }

    void insertData(String title, String desc, String date, String hdURL, String img_filepath)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                db = getWritableDatabase();
                ContentValues newRowValues = new ContentValues();

                newRowValues.put(MyOpener.COL_TITLE, title);
                newRowValues.put(MyOpener.COL_DESC, desc);
                newRowValues.put(MyOpener.COL_DATE, date);
                newRowValues.put(MyOpener.COL_HDURL, hdURL);
                newRowValues.put(MyOpener.COL_IMAGE_FILEPATH, img_filepath);
                db.insert(MyOpener.TABLE_NAME, null, newRowValues);

                System.out.println("DATA INSERTED");
                System.out.println(title + " " + desc + " " + date + " " + img_filepath);
                db.close();
            }
        }.start();
    }

    void deleteData(ImageObject imageObject)
    {
        db = getWritableDatabase();

        db.delete(MyOpener.TABLE_NAME, MyOpener.COL_ID + "= ?", new String[] {Long.toString(imageObject.getId())});
        db.close();
    }
}