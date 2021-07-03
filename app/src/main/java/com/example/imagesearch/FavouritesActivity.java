package com.example.imagesearch;

import androidx.appcompat.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.util.ArrayList;

public class FavouritesActivity extends ToolbarActivity
{
    public final static String VERSION = "v0.1"; // Apps version #. displayed in the NavDrawer
    private ListView myList;
    private MyAdapter adapter;

    private ArrayList<ImageObject> imageList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        findViewById(R.id.favourites_layout).setVisibility(View.VISIBLE);

        // load data from database
        loadDataFromDatabase();

        // load toolbar & nav
        loadToolbar(getString(R.string.navTitle_Favourites), VERSION);

        // Temporary SnackBar message that will be displayed after sign in
        //View activity_favourites = findViewById(R.id.activity_favourites_layout);
        Snackbar.make(findViewById(R.id.drawer_layout), "This is your favourites page!", Snackbar.LENGTH_LONG).show();

        // Initializing variables
        myList = findViewById(R.id.favourites_ListView);

        // Set adapter for ListView
        adapter = new MyAdapter();
        myList.setAdapter(adapter);

        // Click listener to display details about items in ListView
        myList.setOnItemClickListener( (list, view, position, id) -> {
            showAlertDialog(position);
        });

        // Long click listener for deleting items
        myList.setOnItemLongClickListener((list, view, position, id) -> {
            showDeleteAlertDialog(position);
            return true;
        });
    }

    // Displays alert dialog for the ListView's on item click listener
    private void showAlertDialog(int position)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(imageList.get(position).getTitle())
                .setMessage(imageList.get(position).getDate() + "\n" + imageList.get(position).getHdURL()
                        + "\n\n" + imageList.get(position).getDesc())
                // Ok Button
                .setPositiveButton("OK", (click, arg) -> { })
                .create().show();
    }

    // Displays an alert dialog that gives the user the option to delete images
    private void showDeleteAlertDialog(int position)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Would you like to delete this image?")
                // Yes button
                .setPositiveButton("Yes", (click, arg) -> {
                    MyOpener myOpener = new MyOpener(this);

                    myList.setAdapter(null);
                    myOpener.deleteData(imageList.get(position));
                    imageList.remove(position);
                    adapter.notifyDataSetChanged();
                    myList.setAdapter(adapter);
                    myOpener.close();
                })
                // No button
                .setNegativeButton("No", (click, arg) -> { })
                .create().show();
    }

    // load data from the database
    private void loadDataFromDatabase()
    {
        // Accessing database from its own thread
        new Thread()
        {
            @Override
            public void run()
            {
                //connect to the database
                MyOpener dbOpener = new MyOpener(FavouritesActivity.this);
                SQLiteDatabase db = dbOpener.getWritableDatabase();

                // get all columns in the db
                String [] columns = {MyOpener.COL_TITLE, MyOpener.COL_DESC, MyOpener.COL_DATE, MyOpener.COL_HDURL, MyOpener.COL_IMAGE_FILEPATH, MyOpener.COL_ID};
                //query all the results from the database:
                Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

                //Now the results object has rows of results that match the query.
                //find the column indices:
                int titleColIndex = results.getColumnIndex(MyOpener.COL_TITLE);
                int descColIndex = results.getColumnIndex(MyOpener.COL_DESC);
                int dateColIndex = results.getColumnIndex(MyOpener.COL_DATE);
                int hdURLColIndex = results.getColumnIndex(MyOpener.COL_HDURL);
                int imgFilePathColIndex = results.getColumnIndex(MyOpener.COL_IMAGE_FILEPATH);
                int idColIndex = results.getColumnIndex(MyOpener.COL_ID);

                //iterate over the results, return true if there is a next item:
                while(results.moveToNext())
                {
                    String title = results.getString(titleColIndex);
                    long id = results.getLong(idColIndex);
                    String desc = results.getString(descColIndex);
                    String date = results.getString(dateColIndex);
                    String hdURL = results.getString(hdURLColIndex);
                    String img_filepath = results.getString(imgFilePathColIndex);

                    //add the new image object to the array list:
                    imageList.add(new ImageObject(title, desc, date, hdURL, img_filepath, id));
                }
                results.close();
                db.close();
                //At this point, the image array has loaded every row from the cursor.
            }
        }.start();
    }

    // load image from the phone's internal storage
    private Bitmap loadImageFromStorage(String path, String filename) {
        Bitmap image;
        File f = new File(path, filename);
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            image = BitmapFactory.decodeFile(f.toString(), options); // reduce bitmap memory usage
        } catch (OutOfMemoryError e) {
            Log.e("MemoryError", e.getMessage());
            image = null;
        }
        return image;
    }

    private class MyAdapter extends BaseAdapter {
        private View newView;

        public int getCount() {
            return imageList.size();
        }

        public ImageObject getItem(int position) {
            return imageList.get(position);
        }

        public long getItemId(int position) {
            return getItem(position).getId();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            View row = convertView;
            LayoutInflater inflater = getLayoutInflater();

            if (convertView == null) {
                row = inflater.inflate(R.layout.listview_items, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = row.findViewById(R.id.list_TextView);
                viewHolder.imageView = row.findViewById(R.id.list_ImageView);

                // store the ViewHolder in the View
                row.setTag(viewHolder);
            } else {
                // instead of having to call findViewById() on every resource
                // just use the ViewHolder
                viewHolder = (ViewHolder) row.getTag();
            }

            viewHolder.textView.setText(getItem(position).getTitle());
            viewHolder.imageView.setImageBitmap(loadImageFromStorage(getItem(position).getImg_filepath(), getItem(position).getTitle()));
            newView = row;
            return newView;
        }
    }

    // ViewHolder
    static class ViewHolder
    {
        ImageView imageView;
        TextView textView;
    }
}