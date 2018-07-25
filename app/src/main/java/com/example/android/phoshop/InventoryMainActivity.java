package com.example.android.phoshop;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.phoshop.Data.IngredientsContract.IngredientEntry;
import com.example.android.phoshop.Data.PhoDbHelper;

public class InventoryMainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PHO_LOADER = 0;
    PhoDbHelper mDbhelper;
    PhoCursorAdapter mCursorAdapter;
    Uri mUri;
    String[] projection = {
            IngredientEntry._ID,
            IngredientEntry.COLUMN_NAME,
            IngredientEntry.COLUMN_QUANTITY,
            IngredientEntry.COLUMN_PRICE,
            IngredientEntry.COLUMN_SUPPLIER,
            IngredientEntry.COLUMN_IMAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_main);

        //set up Fab to add new products so it can go into the details page
        FloatingActionButton FAB = (FloatingActionButton) findViewById(R.id.fab);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InventoryMainActivity.this, EditsActivity.class);
                startActivity(intent);
            }
        });

        //listView to populate
        ListView displayView = (ListView) findViewById(R.id.ingredients_text_view);

        View emptyView = findViewById(R.id.empty_view);

        displayView.setEmptyView(emptyView);

        mCursorAdapter = new PhoCursorAdapter(this, null);

        displayView.setAdapter(mCursorAdapter);

        //setup item click listener
        displayView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(InventoryMainActivity.this, EditsActivity.class);

                Uri currentUri = ContentUris.withAppendedId(IngredientEntry.CONTENT_URI, id);

                intent.setData(currentUri);
                startActivity(intent);

            }
        });


        getLoaderManager().initLoader(PHO_LOADER, null, this);
    }

    public void deleteAllPho() {
        int rowDeleted = getContentResolver().delete(IngredientEntry.CONTENT_URI, null, null);
        Log.v("InventoryMainActivity", rowDeleted + " rows deleted from phoshop database");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_all_entries:
                deleteAllPho();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle bundle) {
        return new CursorLoader(this,
                IngredientEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}
