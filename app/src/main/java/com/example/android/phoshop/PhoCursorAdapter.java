package com.example.android.phoshop;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.phoshop.Data.IngredientsContract;

/**
 * Created by Paul on 12/10/2017.
 */

public class PhoCursorAdapter extends CursorAdapter {

    public PhoCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override //where the items are displayed
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageButton saleButton = (ImageButton) view.findViewById(R.id.stock_button);
        ImageView picture = (ImageView) view.findViewById(R.id.image_list);
        //sales button
        final int ingredientId = cursor.getInt(cursor.getColumnIndex(IngredientsContract.IngredientEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(IngredientsContract.IngredientEntry.COLUMN_NAME);
        int priceColumnIndex = cursor.getColumnIndex(IngredientsContract.IngredientEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(IngredientsContract.IngredientEntry.COLUMN_QUANTITY);
        int pictureColumnIndex = cursor.getColumnIndex(IngredientsContract.IngredientEntry.COLUMN_IMAGE);

        String ingredientName = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        final int quantityInt = Integer.valueOf(quantity);
        String image = cursor.getString(pictureColumnIndex);
        Uri currentBookUri = Uri.parse(image);


        picture.setImageURI(currentBookUri);

        nameTextView.setText(ingredientName);
        priceTextView.setText("S" + price);
        quantityTextView.setText(quantity);

        saleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (quantityInt > 0) {
                    int newQuantity = quantityInt - 1;
                    Uri quantitytUri = ContentUris.withAppendedId(IngredientsContract.IngredientEntry.CONTENT_URI, ingredientId);

                    ContentValues values = new ContentValues();
                    values.put(IngredientsContract.IngredientEntry.COLUMN_QUANTITY, newQuantity);
                    context.getContentResolver().update(quantitytUri, values, null, null);
                } else {
                    Toast.makeText(context, "Sold out!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}