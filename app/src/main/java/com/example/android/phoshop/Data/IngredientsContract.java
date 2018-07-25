package com.example.android.phoshop.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Paul on 12/7/2017.
 */

public final class IngredientsContract {

    //all the strings to make content URI
    public static final String CONTENT_AUTHORITY = "com.example.android.phoshop";

    public static final Uri BASE_URI_CONTENT = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PHO = "phoshop";


    public static abstract class IngredientEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI_CONTENT, PATH_PHO);

        //table name and column constants
        public static final String TABLE_NAME = "phoshop";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_QUANTITY = "quantity";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_SUPPLIER = "supplier";
        public static final String COLUMN_IMAGE = "image";

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of Ingredient.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PHO;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single Ingredient.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PHO;
    }
}
