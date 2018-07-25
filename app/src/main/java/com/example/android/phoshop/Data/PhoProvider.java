package com.example.android.phoshop.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.phoshop.Data.IngredientsContract.IngredientEntry;

/**
 * Created by Paul on 12/8/2017.
 */

public class PhoProvider extends ContentProvider {

    public static final String LOG_TAG = PhoProvider.class.getSimpleName();
    // Uri matcher code for the whole table
    private static final int PHO = 100;
    //uri matcher code for a specific row
    private static final int PHO_ID = 101;
    //uri matcher to match with a uri content
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer
    static {
        sUriMatcher.addURI(IngredientsContract.CONTENT_AUTHORITY, IngredientsContract.PATH_PHO, PHO);
        sUriMatcher.addURI(IngredientsContract.CONTENT_AUTHORITY, IngredientsContract.PATH_PHO + "/#", PHO_ID);
    }

    private PhoDbHelper mDhelper;

    @Override
    public boolean onCreate() {
        //create and initialize Dbhelper object to gain access to the pho database
        mDhelper = new PhoDbHelper(getContext());
        return false;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        //get readable database
        SQLiteDatabase database = mDhelper.getReadableDatabase();

        //this will hold the result of the query
        Cursor cursor = null;

        //figure of the uri matcher can match to specific code
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PHO:
                cursor = database.query(IngredientEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PHO_ID:
                selection = IngredientEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                cursor = database.query(IngredientEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), IngredientEntry.CONTENT_URI);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PHO:
                return IngredientEntry.CONTENT_LIST_TYPE;
            case PHO_ID:
                return IngredientEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {

        //figure of the uri matcher can match to specific code
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PHO:
                return insertPho(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertPho(Uri uri, ContentValues values) {

        String name = values.getAsString(IngredientEntry.COLUMN_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Ingredient requires a name"); //question about input validation with images
        }
        Integer price = values.getAsInteger(IngredientEntry.COLUMN_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Requires a valid price");
        }
        String supplier = values.getAsString(IngredientEntry.COLUMN_SUPPLIER);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier requires a valid name");
        }


        SQLiteDatabase db = mDhelper.getWritableDatabase();

        long id = db.insert(IngredientEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDhelper.getWritableDatabase();

        int deleteRow;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PHO:
                deleteRow = database.delete(IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PHO_ID:
                selection = IngredientEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                deleteRow = database.delete(IngredientEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for" + uri);
        }
        if (deleteRow != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteRow;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PHO:
                return updatePho(uri, contentValues, selection, selectionArgs);
            case PHO_ID:
                // For the PHO_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = IngredientEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri, null);
                return updatePho(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    public int updatePho(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(IngredientEntry.COLUMN_NAME)) {
            String name = values.getAsString(IngredientEntry.COLUMN_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Ingredient requires a name"); //question about input validation with images
            }
        }

        if (values.containsKey(IngredientEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(IngredientEntry.COLUMN_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Requires a valid price");
            }
        }

        if (values.containsKey(IngredientEntry.COLUMN_SUPPLIER)) {
            String supplier = values.getAsString(IngredientEntry.COLUMN_SUPPLIER);
            if (supplier == null) {
                throw new IllegalArgumentException("Supplier requires a valid name");
            }
        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDhelper.getWritableDatabase();

        //preform update and get the number of rows affected
        int rowsUpdated = database.update(IngredientEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
