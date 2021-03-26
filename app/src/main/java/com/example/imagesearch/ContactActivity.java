package com.example.imagesearch;

import android.os.Bundle;
import android.view.View;

public class ContactActivity extends ToolbarActivity
{
    public final static String VERSION = "v0.1";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        findViewById(R.id.contact_layout).setVisibility(View.VISIBLE);

        // load toolbar
        loadToolbar(getString(R.string.navTitle_Contact), VERSION);
    }
}
