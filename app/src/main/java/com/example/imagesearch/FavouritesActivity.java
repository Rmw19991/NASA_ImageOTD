package com.example.imagesearch;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

public class FavouritesActivity extends AppCompatActivity
{
    ListView myList;
    String countryList[] = {"India", "China", "Australia", "America"}; // Temporary list to display in ListView
                                                                      // This will eventually hold images taken from NASA's web servers
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        // Temporary Snackbar message that will be displayed after sign in
        View test = findViewById(R.id.activity_favourites_layout);
        Snackbar.make(test, "This is a Snackbar message!", Snackbar.LENGTH_LONG).show();

        // Initializing variables
        myList = findViewById(R.id.favourites_ListView);

        // Set adapter for ListView
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.listview_items, R.id.testingView, countryList);
        myList.setAdapter(adapter);

        // Click listener to display details about items in ListView
        myList.setOnItemClickListener( (list, view, position, id) -> {
            showAlertDialog(position);
        });
    }


    // Displays alert dialog for the ListView's on item click listener
    protected void showAlertDialog(int position)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(countryList[position])
                .setMessage("You have selected row : " + position + "\nDetails regarding ListView items will be displayed here!")
                // Yes Button
                .setPositiveButton("OK", (click, arg) -> { })

                // No Button should go here

                .create().show();
    }
}