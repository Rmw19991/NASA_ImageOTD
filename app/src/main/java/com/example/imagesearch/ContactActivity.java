package com.example.imagesearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContactActivity extends ToolbarActivity
{
    public final static String VERSION = "v0.1"; // Apps version #. displayed in the NavDrawer
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        findViewById(R.id.contact_layout).setVisibility(View.VISIBLE);
        loadToolbar(getString(R.string.navTitle_Contact), VERSION);

        prefs = getSharedPreferences("Filename", Context.MODE_PRIVATE);
        String savedName = prefs.getString("Name", "");

        EditText editText = findViewById(R.id.contact_NameEditText);
        editText.setText(savedName);

        Button button = findViewById(R.id.contact_SendButton);
        button.setOnClickListener( (click) -> {
            saveSharedPrefs(editText.getText().toString());
            sendEmail();
        });

    }

    // Signs in to the users Gmail account and sends an email
    public void sendEmail()
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

    private void saveSharedPrefs(String stringToSave)
    {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Name", stringToSave);
        editor.apply();
    }
}
