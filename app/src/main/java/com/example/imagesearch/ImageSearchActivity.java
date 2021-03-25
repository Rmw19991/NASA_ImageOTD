package com.example.imagesearch;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ImageSearchActivity extends ToolbarActivity
{
    public final static String VERSION = "v0.1";
    public final static String SAVE_MESSAGE = "Image Saved!";
    private String dateString;
    ImageQuery imageQuery;
    DatePickerFragment datePickerFragment = new DatePickerFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        findViewById(R.id.image_search_layout).setVisibility(View.VISIBLE);

        loadToolbar(getString(R.string.navTitle_ImageSearch), VERSION);

        // Variables
        String url = "https://api.nasa.gov/planetary/apod?api_key=DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d&date=";
        imageQuery = new ImageQuery();
        MyOpener myOpener = new MyOpener(this);

        Button searchButton = findViewById(R.id.imageSearch_SearchButtonView);
        searchButton.setOnClickListener( (click) -> {
            // get the date from the DatePicker and append it to the end of our url
            dateString = datePickerFragment.getDateString();
            imageQuery.execute(url + dateString);
        } );

        Button saveButton =  findViewById(R.id.imageSearch_SaveButtonView);
        saveButton.setOnClickListener( (click) -> {
            myOpener.insertData(imageQuery.title, imageQuery.description, imageQuery.date, imageQuery.hdURL, saveToInternalStorage(imageQuery.currentPicture));
            myOpener.close();
            Toast.makeText(this, SAVE_MESSAGE, Toast.LENGTH_LONG).show();
        } );
    }

    // Display DatePickerDialog fragment
    public void showDatePickerDialog(View v)
    {
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // Saves the image to the phones internal storage
    // then returns the filepath so it can be stored in the database
    private String saveToInternalStorage(Bitmap bitmapImage)
    {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/your_app/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory,imageQuery.title);

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(myPath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                fos.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private class ImageQuery extends AsyncTask<String, Integer, String>
    {
        String title;
        String description;
        String date;
        String url;
        String hdURL;
        Bitmap currentPicture;

        @Override
        protected String doInBackground(String... args)
        {
           try
           {
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
               }

               // Create a JSON object using the string that holds the JSON data
               JSONObject jsonObject = new JSONObject(inline);

               // Store data from JSON object
               title = jsonObject.getString("title");
               description = jsonObject.getString("explanation");
               date = jsonObject.getString("date");
               url = jsonObject.getString("url");

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
               Log.e("Error", e.getMessage());
           }

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
                   if (responseCode == 200) {
                       currentPicture = BitmapFactory.decodeStream(connection.getInputStream());
                   }

                   FileOutputStream outputStream = openFileOutput(hdURL, Context.MODE_PRIVATE);
                   currentPicture.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
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

           return "Done";
        }

        public void onPostExecute(String fromdoInBackground)
        {
            String dateString = getString(R.string.imageSearch_Date) + " " + date;
            String urlString = getString(R.string.imageSearch_URL) + " " + url;
            String hdURLString = getString(R.string.imageSearch_HDURL) + " " + hdURL;

            TextView textView1 = findViewById(R.id.imageSearch_DateTextView);
            TextView textView2 = findViewById(R.id.imageSearch_URLTextView);
            TextView textView3 = findViewById(R.id.imageSearch_HDURLTextView);

            textView1.setText(dateString);
            textView2.setText(urlString);
            textView3.setText(hdURLString);
            textView3.setMovementMethod(LinkMovementMethod.getInstance());
            textView3.setVisibility(View.VISIBLE);
        }
    }
}