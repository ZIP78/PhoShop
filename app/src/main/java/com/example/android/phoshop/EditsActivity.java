package com.example.android.phoshop;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.phoshop.Data.IngredientsContract.IngredientEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Paul on 12/6/2017.
 */

public class EditsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int PHO_LOADER = 0;
    private static final int PICK_IMAGE_REQUEST = 0;
    private static final String STATE_URI = "STATE_IMAGE_UR";
    //number of quantity
    public int numberOfQuantity;
    String[] projection = {
            IngredientEntry._ID,
            IngredientEntry.COLUMN_NAME,
            IngredientEntry.COLUMN_QUANTITY,
            IngredientEntry.COLUMN_PRICE,
            IngredientEntry.COLUMN_SUPPLIER,
            IngredientEntry.COLUMN_IMAGE
    };
    //Edit Text field for name
    private EditText mNameEditText;
    //Edit Text field for quantity
    private EditText mQuantityEditText;
    //Edit Text field for price
    private EditText mPriceEditText;
    //Edit text fields for supplier
    private EditText mSupplierEditText;
    //increase button for quantity
    private Button quantityIncrease;
    //Decrease quantity
    private Button quantityDecrease;
    //order button thru email
    private Button orderEmail;

    private Uri mCurrentPhoUri;
    private Button mAddImage;
    private String imageString;
    private boolean mPhoHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPhoHasChanged = true;
            return false;
        }
    };
    private Uri mImageUri;
    private ImageView mImageView;
    private Context mContext = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edits_main);
        orderEmail = (Button) findViewById(R.id.order);

        Intent intent = getIntent();
        mCurrentPhoUri = intent.getData();

        if (mCurrentPhoUri == null) {
            setTitle("Add an ingredient");
            orderEmail.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.existing_ingredient));
            getLoaderManager().initLoader(PHO_LOADER, null, this);

            orderEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    String emailString = mSupplierEditText.getText().toString().trim();
                    emailIntent.setData(Uri.parse("mailto:" + emailString));
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"seller's email address"});
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Order of Lime");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "I would like an order of...");
                    startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));

                }
            });
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_name);

        quantityIncrease = (Button) findViewById(R.id.increase);

        mQuantityEditText = (EditText) findViewById(R.id.edit_quantity);

        quantityDecrease = (Button) findViewById(R.id.decrease);

        mPriceEditText = (EditText) findViewById(R.id.edit_price);

        mSupplierEditText = (EditText) findViewById(R.id.edit_supplier);

        mImageView = (ImageView) findViewById(R.id.image);

        mAddImage = (Button) findViewById(R.id.button_image);

        //set edit text on onTouchListeners
        mNameEditText.setOnTouchListener(mTouchListener);
        quantityIncrease.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);
        quantityDecrease.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierEditText.setOnTouchListener(mTouchListener);
        mImageView.setOnTouchListener(mTouchListener);
        mAddImage.setOnTouchListener(mTouchListener);

        //increase quantity
        quantityIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentQuantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(currentQuantity)) {
                    Toast.makeText(EditsActivity.this, "Quantity is empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    numberOfQuantity = Integer.parseInt(currentQuantity);
                    mQuantityEditText.setText(String.valueOf(numberOfQuantity + 1));
                }
            }
        });

        //Decrease quantity
        quantityDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentQuantity = mQuantityEditText.getText().toString();
                if (TextUtils.isEmpty(currentQuantity)) {
                    Toast.makeText(EditsActivity.this, "Quantity is empty", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    numberOfQuantity = Integer.parseInt(currentQuantity);
                }
                if ((numberOfQuantity - 1) >= 0) {
                    mQuantityEditText.setText(String.valueOf(numberOfQuantity - 1));
                } else {
                    Toast.makeText(EditsActivity.this, "Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        });

        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        if (mImageUri != null)
            outState.putString(STATE_URI, mImageUri.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.containsKey(STATE_URI) &&
                !savedInstanceState.getString(STATE_URI).equals("")) {
            mImageUri = Uri.parse(savedInstanceState.getString(STATE_URI));

            ViewTreeObserver viewTreeObserver = mImageView.getViewTreeObserver();
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
                }
            });
        }
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void saveIngredients() {

        //read from inputs
        String nameString = mNameEditText.getText().toString().trim();
        if (TextUtils.isEmpty(nameString)) {
            mNameEditText.setError("Please enter the name of the ingredient");
            return;
        }

        String quantityString = mQuantityEditText.getText().toString().trim();
        if (TextUtils.isEmpty(quantityString)) {
            mQuantityEditText.setError("Please enter the quantity of the ingredient");
            return;
        }
        int quantity = Integer.parseInt(quantityString);

        String priceString = mPriceEditText.getText().toString().trim();
        if (TextUtils.isEmpty(priceString)) {
            mPriceEditText.setError("Please enter the price of the ingredient");
            return;
        }
        int price = Integer.parseInt(priceString);

        String supplierString = mSupplierEditText.getText().toString().trim();
        if (TextUtils.isEmpty(supplierString)) {
            mSupplierEditText.setError("Please enter the email address of the seller");
            return;
        }


        ContentValues pho = new ContentValues();
        pho.put(IngredientEntry.COLUMN_NAME, nameString);
        pho.put(IngredientEntry.COLUMN_QUANTITY, quantity);
        pho.put(IngredientEntry.COLUMN_PRICE, price);
        pho.put(IngredientEntry.COLUMN_SUPPLIER, supplierString);
        pho.put(IngredientEntry.COLUMN_IMAGE, imageString);

        if (imageString == null) {
            Toast.makeText(this, "Select a picture ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mCurrentPhoUri == null) {
            mCurrentPhoUri = getContentResolver().insert(IngredientEntry.CONTENT_URI, pho);


            // toast message here
            if (mCurrentPhoUri == null) {
                Toast.makeText(this, "there is an error with saving data", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "the data was saved successfully ", Toast.LENGTH_SHORT).show();
            }
        } else {

            int rowsAffected = getContentResolver().update(mCurrentPhoUri, pho, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.updated_failed), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();

            }
        }
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { //step 2
        super.onPrepareOptionsMenu(menu);
        // If this is a new ingredient, hide the "Delete" menu item.
        if (mCurrentPhoUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//respond to the click on save button option
            case R.id.action_save:
                saveIngredients();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mPhoHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditsActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        NavUtils.navigateUpFromSameTask(EditsActivity.this);
                    }
                };
                showUnsavedChanges(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pho ingredient.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deletePet() {
        // Only perform the delete if this is an existing ingredient.
        if (mCurrentPhoUri != null) {
            // Call the ContentResolver to delete the ingredient at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPHoUri
            // content URI already identifies the ingredient that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentPhoUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.deleted_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.deletion_pass),
                        Toast.LENGTH_SHORT).show();
            }
            finish(); //step 3
        }
    }

    @Override
    public void onBackPressed() {
        if (!mPhoHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        ;
                        finish();
                    }
                };
        showUnsavedChanges(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this,
                mCurrentPhoUri,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {


            int nameColumnIndex = cursor.getColumnIndex(IngredientEntry.COLUMN_NAME);
            int PriceIndex = cursor.getColumnIndex(IngredientEntry.COLUMN_PRICE);
            int quantityIndex = cursor.getColumnIndex(IngredientEntry.COLUMN_QUANTITY);
            int supplyIndex = cursor.getColumnIndex(IngredientEntry.COLUMN_SUPPLIER);
            final int imageIndex = cursor.getColumnIndex(IngredientEntry.COLUMN_IMAGE);

            String name = cursor.getString(nameColumnIndex);
            String supplier = cursor.getString(supplyIndex);
            int price = cursor.getInt(PriceIndex);
            int quantity = cursor.getInt(quantityIndex);
            String image = cursor.getString(imageIndex);

            if (image != null) {
                mImageUri = Uri.parse(image);
                imageString = mImageUri.toString();
            }

            mNameEditText.setText(name);
            mSupplierEditText.setText(supplier);
            mPriceEditText.setText(Integer.toString(price));
            mQuantityEditText.setText(Integer.toString(quantity));

            if (mImageUri != null) {
                mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText("");
        mSupplierEditText.setText("");
        mPriceEditText.setText("");
        mQuantityEditText.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && (resultCode == RESULT_OK)) {

            if (data != null) {


                mImageUri = data.getData();
                imageString = mImageUri.toString();

                mImageView.setImageBitmap(getBitmapFromUri(mImageUri));
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri imageUri) {
        if (imageUri == null || imageUri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = this.mImageView.getWidth();
        int targetH = this.mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(imageUri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e("Kidus", "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e("Kidus", "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    private void showUnsavedChanges(DialogInterface.OnClickListener discardButtonOnClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonOnClickListener);
        builder.setNegativeButton(R.string.keep_edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}
