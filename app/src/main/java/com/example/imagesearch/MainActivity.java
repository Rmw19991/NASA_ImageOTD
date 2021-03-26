package com.example.imagesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

public class MainActivity extends ToolbarActivity
{
    public final static String VERSION = "v0.1";
    private Query query;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        findViewById(R.id.home_layout).setVisibility(View.VISIBLE);

        // load toolbar & nav
        loadToolbar(getString(R.string.navTitle_Home), VERSION);

        // Format current date to yyyy/mm/dd
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        Date date = new Date();
        String formattedDate = dateFormat.format(date);

        // append date to the url to get image of the day
        url = "https://api.nasa.gov/planetary/apod?api_key=DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d&date=" + formattedDate;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        query = new Query();
        query.execute(url);
    }

    @Override
    public void onDestroy()
    {
        if (query.getStatus() == AsyncTask.Status.RUNNING)
        {
            query.cancel(true);
        }
        query = null;
        super.onDestroy();
    }

    private class Query extends AsyncTask<String, Integer, String>
    {
        String title;
        String date;
        String hdURL;
        String desc;
        Bitmap currentPicture;
        ProgressBar progressBar;

        @Override
        protected String doInBackground(String... args)
        {
            try
            {
                progressBar = findViewById(R.id.main_ProgressBar);
                URL conURL = new URL(args[0]);

                // Open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) conURL.openConnection();

                // Verify the response code and store the contents of the JSON object in a string
                String inline = "";
                int responseCode = urlConnection.getResponseCode();
                if (responseCode != 200)
                {
                    throw new RuntimeException("HttpResponseCode: " + responseCode);
                }
                else
                {
                    Scanner sc = new Scanner(conURL.openStream());
                    while(sc.hasNext())
                    {
                        inline+=sc.nextLine();
                    }
                    sc.close();
                    progressBar.setProgress(25);
                }

                // Create a JSON object using the string that holds the JSON data
                JSONObject jsonObject = new JSONObject(inline);

                // Store data from JSON object
                title = jsonObject.getString("title");
                date = jsonObject.getString("date");
                hdURL = jsonObject.getString("hdurl");
                desc = jsonObject.getString("explanation");
               /* Some images do not contain a hdurl so this statement
               checks the jsonObject to see if it has one */
                if (jsonObject.has("hdurl"))
                {
                    hdURL = jsonObject.getString("hdurl");
                }
                else
                {
                    hdURL = null;
                }
            }
            catch (Exception e)
            {
                Log.e("ERROR", e.getMessage());
            }

            progressBar.setProgress(50);
            // If the hdurl is not null the program will retrieve the image
            if (hdURL != null)
            {
                try
                {
                    // Getting current picture
                    URL url = new URL(hdURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200)
                    {
                        try
                        {
                            currentPicture = BitmapFactory.decodeStream(connection.getInputStream());
                            progressBar.setProgress(75);
                        }
                        catch (OutOfMemoryError e)
                        {
                            // If file is too big, replace the image and set the TextView displaying
                            // the file too big error to visible
                            Log.e("MemoryError", e.getMessage());
                            currentPicture = BitmapFactory.decodeResource(getResources(), R.drawable.imagetoobig_icon);
                        }
                    }
                    FileOutputStream outputStream = openFileOutput(hdURL, Context.MODE_PRIVATE);
                    currentPicture.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                    outputStream.flush();
                    outputStream.close();

                }
                catch (Exception e)
                {
                    Log.e("Error", e.getMessage());
                }
            }
            else
            {
                hdURL = "Cannot provide HDURL";
            }
            progressBar.setProgress(100);
            return "Done";
        }

        public void onPostExecute(String fromdoInBackground)
        {
            ImageView imageView = findViewById(R.id.home_ImageView);
            TextView titleTextView = findViewById(R.id.home_TitleTextView);
            TextView dateTextView = findViewById(R.id.home_DateTextView);
            TextView urlTextView = findViewById(R.id.home_URLTextView);
            TextView descTextView = findViewById(R.id.home_DescTextView);

            imageView.setImageBitmap(currentPicture);
            titleTextView.setText(title);
            dateTextView.setText(date);
            urlTextView.setText(hdURL);
            descTextView.setText(desc);

            progressBar = findViewById(R.id.main_ProgressBar);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}