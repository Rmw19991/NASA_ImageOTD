package com.example.imagesearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
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
    public final static String VERSION = "v0.1"; // Apps version #. displayed in the NavDrawer

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
        String url = "https://api.nasa.gov/planetary/apod?api_key=DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d&date=" + formattedDate;
        Query query = new Query();
        //query.fetchJsonData(url);
        query.executeQuery(url);
    }

    private class Query
    {
        String title;
        String date;
        String hdURL;
        String desc;
        Bitmap currentPicture;
        ProgressBar progressBar = findViewById(R.id.main_ProgressBar);

        private void executeQuery(String api_Url)
        {
            new Thread() {
                @Override
                public void run() {
                    StringBuilder jsonData = new StringBuilder();
                    try {
                        // progressbar
                        URL conURL = new URL(api_Url);

                        // Open the connection
                        HttpURLConnection urlConnection = (HttpURLConnection) conURL.openConnection();

                        // Verify the response code and store the contents of the JSON object in a StringBuilder object

                        int responseCode = urlConnection.getResponseCode();
                        if (responseCode != 200) {
                            throw new RuntimeException("HttpResponseCode: " + responseCode);
                        } else {
                            Scanner sc = new Scanner(conURL.openStream());
                            while (sc.hasNext()) {
                                jsonData.append(sc.nextLine());
                            }
                            sc.close();
                            progressBar.setProgress(25);
                        }
                    } catch (Exception e) {
                        //Log.e("ERROR", e.getMessage());
                    }
                    saveJsonData(jsonData.toString());
                    fetchImage();

                    // Update View
                   final Runnable runnable = () -> onPostExecute();
                   runOnUiThread(runnable);
                    progressBar.setProgress(100);
                }
            }.start();
        }

        private void saveJsonData(String jsonData)
        {
            try
            {
                // Create a JSON object using the string that holds the JSON data
                JSONObject jsonObject = new JSONObject(jsonData);

                if (!jsonObject.has("hdurl")) //Some images do not contain a hdurl so check the jsonObject to see if it has one
                {
                    hdURL = null;
                }
                // Store title, date, and description data from the JSON object
                title = jsonObject.getString("title");
                date = jsonObject.getString("date");
                desc = jsonObject.getString("explanation");
                hdURL = jsonObject.getString("hdurl");
            }
            catch (Exception e)
            {
                System.err.println(e.toString());
            }
            progressBar.setProgress(50);
        }

        private void fetchImage() {
            // If the hdurl is not null the program will retrieve the image
            if (hdURL == null) {
                hdURL = "Cannot provide HDURL";

            } else {
                try {
                    // Getting current picture
                    URL url = new URL(hdURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 8;
                        currentPicture = BitmapFactory.decodeStream(connection.getInputStream(), new Rect(), options); // reduces bitmap memory usage
                    }

                    FileOutputStream outputStream = openFileOutput(hdURL, Context.MODE_PRIVATE);
                    currentPicture.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                    outputStream.flush();
                    outputStream.close();

                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
            progressBar.setProgress(75);
        }

        private void onPostExecute()
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