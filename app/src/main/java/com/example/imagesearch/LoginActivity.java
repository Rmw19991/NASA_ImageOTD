package com.example.imagesearch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // This function brings a user to RegisterActivity
    // called in activity_login.xml
    public void goToSignUp(View view)
    {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    // Logs the user in and brings them to the favourites page
    // called in activity_login.xml
    public void loginUser(View view)
    {
        Intent intent = new Intent(LoginActivity.this, FavouritesActivity.class);
        startActivity(intent);
    }
}