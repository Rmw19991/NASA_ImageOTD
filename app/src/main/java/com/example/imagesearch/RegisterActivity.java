package com.example.imagesearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity
{

    private ProgressBar progressBar;
    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize variables
        progressBar = findViewById(R.id.register_ProgressBar);
        EditText enterUser = findViewById(R.id.register_EnterUserName);
        EditText enterPass = findViewById(R.id.register_EnterPassword);
        EditText confirmPass = findViewById(R.id.register_ConfirmPassword);

        // Text changed listeners used to update progress bar on completion of EditText fields on the Register page
        // USERNAME FIELD
        enterUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (progressBar.getProgress() == 0)
                {
                    progress += 30;
                }
                setProgressValue(enterUser);
            }
        });
        // PASSWORD FIELD
        enterPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (progressBar.getProgress() == 30)
                {
                    progress += 30;
                }
                setProgressValue(enterPass);
            }
        });
        // CONFIRM PASSWORD FIELD
        confirmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s)
            {
                if (progressBar.getProgress() == 60)
                {
                    progress += 30;
                }
                setProgressValue(confirmPass);
            }
        });
    }

    // Takes user back to login page once they click the CREATE ACCOUNT button
    // Called in activity_register.xml - though this may change
    public void backToLogin(View view)
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);

        // Temp Toast message
        Toast.makeText(this, "Account Creation is yet to be implemented!", Toast.LENGTH_LONG).show();
    }

    // set value of progress bar as user fills out registration page
    private void setProgressValue(EditText editText)
    {
        // Get the EditText view and then store its text in a string
        String strEditText = editText.getText().toString();

        // set the progress
        // If the EditText field is NOT empty, update progress
        if (!strEditText.matches(""))
        {
            progressBar.setProgress(progress);
        }
        else
        {
            // If the current EditText field is empty
            // de-increment the progress variable by 30, decreasing the progress
            progress -= 30;
            progressBar.setProgress(progress);
        }
    }
}