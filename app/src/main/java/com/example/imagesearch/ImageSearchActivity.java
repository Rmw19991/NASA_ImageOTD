package com.example.imagesearch;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ImageSearchActivity extends ToolbarActivity
{
    public final static String VERSION = "v0.1"; // Apps version #. displayed in the NavDrawer
    public final static String SAVE_MESSAGE = "Image Saved!"; // Message for Toast to display

    private String imageTitle;
    private String imageDesc;
    private String imageDate;
    private String imageHDURL;
    private Bitmap imageCurrentPicture;
    private DatePickerFragment datePickerFragment;

    public ImageSearchActivity()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        findViewById(R.id.image_search_layout).setVisibility(View.VISIBLE);
        loadToolbar(getString(R.string.navTitle_ImageSearch), VERSION);
    }

    // Display DatePickerDialog fragment
    public void showDatePickerDialog(View v)
    {
        datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void searchImage(View v)
    {
        String url = "https://api.nasa.gov/planetary/apod?api_key=";
        String dateString = url + datePickerFragment.getDateString();
        new ImageQuery().executeQuery(dateString);
    }

    public void saveToFavourites(View v)
    {
        MyOpener myOpener = new MyOpener(this);
        myOpener.insertData(imageTitle, imageDesc, imageDate, imageHDURL, saveToInternalStorage(imageCurrentPicture));
        myOpener.close();
        Toast.makeText(this, SAVE_MESSAGE, Toast.LENGTH_LONG).show();
    }

    // Saves the image to the phones internal storage
    // then returns the filepath so it can be stored in the database
    private String saveToInternalStorage(Bitmap bitmapImage)
    {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/your_app/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File myPath = new File(directory, imageTitle);

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


    private class ImageQuery
    {
        // holds image details
        String title;
        String description;
        String date;
        String url;
        String hdURL;
        Bitmap currentPicture;

        private void executeQuery(String api_Url) {
            new Thread() {
                @Override
                public void run() {
                    StringBuilder jsonData = new StringBuilder();
                    try {
                        URL conURL = new URL(api_Url);

                        // Open the connection
                        HttpURLConnection urlConnection = (HttpURLConnection) conURL.openConnection();
                        // Verify the response code and store the contents of the JSON object in a string
                        int responseCode = urlConnection.getResponseCode();
                        if (responseCode != 200) {
                            throw new RuntimeException("HttpResponseCode: " + responseCode);
                        } else {
                            Scanner sc = new Scanner(conURL.openStream());
                            while (sc.hasNext()) {
                                jsonData.append(sc.nextLine());
                            }
                            sc.close();
                        }
                        saveJsonData(jsonData.toString());
                        fetchImage();
                    } catch (Exception e) {
                        System.err.println(e.toString());
                    }

                    // Update View
                    final Runnable runnable = () -> onPostExecute();
                    runOnUiThread(runnable);
                }
            }.start();
        }

        private void saveJsonData(String jsonData) throws JSONException {
            // Create a JSON object using the string that holds the JSON data
            JSONObject jsonObject = new JSONObject(jsonData);

            // Store data from JSON object
            title = jsonObject.getString("title");
            description = jsonObject.getString("explanation");
            date = jsonObject.getString("date");
            url = jsonObject.getString("url");

                        /* Some images do not contain a hdurl so this statement
                           checks the jsonObject to see if it has one */
            if (jsonObject.has("hdurl")) {
                hdURL = jsonObject.getString("hdurl");
            } else {
                hdURL = null;
            }
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
                    currentPicture.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                    outputStream.flush();
                    outputStream.close();

                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            }
        }

        private void onPostExecute()
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

            imageTitle = title;
            imageDate = date;
            imageDesc = description;
            imageHDURL = hdURL;
            imageCurrentPicture = currentPicture;
        }
    }
}
