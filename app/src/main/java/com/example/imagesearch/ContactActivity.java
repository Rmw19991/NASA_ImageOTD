package com.example.imagesearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

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

    // Signs in to the users Gmail account and sends an email
    public void sendEmail(View v)
    {
        EditText senderText = findViewById(R.id.contact_NameEditText);
        EditText messageText = findViewById(R.id.contact_MessageEditText);
        EditText subjectText = findViewById(R.id.contact_SubjectEditText);
        String subject = subjectText.getText().toString();
        String sender = senderText.getText().toString();
        String message = messageText.getText().toString();

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"destination@emailhere.com"}); // USERS EMAIL WILL BE SENT TO THIS EMAIL ADDRESS
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "From: " + sender + "\n\n" + message);

        startActivity(Intent.createChooser(emailIntent, "GMAIL"));
    }
}
