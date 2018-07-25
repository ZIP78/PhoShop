package com.example.android.phoshop.Data;

        import android.content.Context;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Paul on 12/7/2017.
 */

public class PhoDbHelper extends SQLiteOpenHelper { //extends SQLiteHelper

    public static final String DATABASE_NAME = "PhoShop.db";
    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + IngredientsContract.IngredientEntry.TABLE_NAME + "(" +
            IngredientsContract.IngredientEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            IngredientsContract.IngredientEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            IngredientsContract.IngredientEntry.COLUMN_QUANTITY + " INTEGER NOT NULL, " +
            IngredientsContract.IngredientEntry.COLUMN_PRICE + " INTEGER NOT NULL, " +
            IngredientsContract.IngredientEntry.COLUMN_SUPPLIER + " TEXT NOT NULL, " +
            IngredientsContract.IngredientEntry.COLUMN_IMAGE + " TEXT" + ")";

    public static final String SQL_DELETE_ENTRIES = "DROP TABLE IF IT EXIST " + IngredientsContract.IngredientEntry.TABLE_NAME;

    public PhoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);

    }

}
