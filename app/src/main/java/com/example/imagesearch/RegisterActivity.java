package com.example.imagesearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
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
}
