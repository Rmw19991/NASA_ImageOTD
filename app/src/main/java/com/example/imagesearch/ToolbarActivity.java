package com.example.imagesearch;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import com.google.android.material.navigation.NavigationView;

public class ToolbarActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
    }

    public void loadToolbar(String title, String version)
    {
        //For toolbar:
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        //For NavigationDrawer:
        DrawerLayout mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawer, toolbar, R.string.open, R.string.close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView nvDrawer = findViewById(R.id.nav_view);
        nvDrawer.setNavigationItemSelectedListener(this);

        // Set nav header title and version
        TextView navHeaderTitle = nvDrawer.getHeaderView(0).findViewById(R.id.navHeader_Title);
        TextView navHeaderVersion = nvDrawer.getHeaderView(0).findViewById(R.id.navHeader_Version);
        navHeaderTitle.setText(title);
        navHeaderVersion.setText(version);
    }

    // Displays an AlertDialog when the help menu item is clicked
    private void showHelpAlertDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Help")
                .setMessage("Searching for and saving images: " +
                        "\n\nTo search for an image navigate to the Image Search page, " +
                        "select a date, then click the Search button. \nIf you would like " +
                        "to save the image you searched for, click the Save Image button." +
                        "\n\nViewing and deleting saved images:" +
                        "\n\nTo see a list of your favourite images navigate to the Favourites page. " +
                        "To delete an image, click and hold it. To view the date, url and description of the image, " +
                        "click it once.")
                // Yes Button
                .setPositiveButton("OK", (click, arg) -> { })
                .create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Intent intent;

        switch (item.getItemId()) {
            case R.id.goto_ImageSearch:
                intent = new Intent(this, ImageSearchActivity.class);
                startActivity(intent);
                break;

            case R.id.goto_FavouritesPage:
                intent = new Intent(this, FavouritesActivity.class);
                startActivity(intent);
                break;

            case R.id.goto_Home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.goto_Contact:
                intent = new Intent(this, ContactActivity.class);
                startActivity(intent);
                break;

            case R.id.help_Menu:
                showHelpAlertDialog();
                break;
        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        Intent intent;
        
        switch (item.getItemId()) {
            case R.id.goto_ImageSearch:
                intent = new Intent(this, ImageSearchActivity.class);
                startActivity(intent);
                break;

            case R.id.goto_FavouritesPage:
                intent = new Intent(this, FavouritesActivity.class);
                startActivity(intent);
                break;

            case R.id.goto_Home:
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;

            case R.id.goto_Contact:
                intent = new Intent(this, ContactActivity.class);
                startActivity(intent);
                break;

            case R.id.help_Menu:
                showHelpAlertDialog();
                break;
        }
        return false;
    }
}