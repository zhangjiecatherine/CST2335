package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;
import static org.xmlpull.v1.XmlPullParser.TEXT;

public class WeatherForecast extends AppCompatActivity {

    ProgressBar loader;
    ImageView weatherImage;
    TextView currentTemperature, minTemperature, maxTemperature, uv;
    String maxTemp,minTemp, currentTemp, icon, uvvalue;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        loader = findViewById(R.id.progressBar);


        weatherImage = (ImageView)findViewById(R.id.imageView2);
        currentTemperature = (TextView)findViewById(R.id.textView1);
        minTemperature = (TextView)findViewById(R.id.textView2);
        maxTemperature = (TextView)findViewById(R.id.textView3);
        uv = (TextView)findViewById(R.id.textView4);



        ForecastQuery task = new ForecastQuery();
        task.execute();

        loader.setVisibility(View.VISIBLE);
    }

           private class ForecastQuery extends AsyncTask<String, Integer, String>{

            @Override                       //Type 1
            protected String doInBackground(String ... strings) {

                String ret = null;
                String queryURL1 = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric";
                String queryURL2 = "http://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389";
                String urlString;

                try {       // Connect to the server:
                    URL url = new URL(queryURL1);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream inStream = urlConnection.getInputStream();

                    //Set up the XML parser:
                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                    factory.setNamespaceAware(false);
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput( inStream  , "UTF-8");

                    //Iterate over the XML tags:
                    int EVENT_TYPE;         //While not the end of the document:
                    while((EVENT_TYPE = xpp.getEventType()) != XmlPullParser.END_DOCUMENT)
                    {
                        switch(EVENT_TYPE)
                        {
                            case START_TAG:         //This is a start tag < ... >
                                String tagName = xpp.getName(); // What kind of tag?
                                if(tagName.equals("temperature"))
                                {
                                    maxTemp = xpp.getAttributeValue(null, "max");
                                    publishProgress(25);

                                    minTemp = xpp.getAttributeValue(null, "min");
                                    publishProgress(50);

                                    currentTemp = xpp.getAttributeValue(null, "value");
                                    publishProgress(75);
                                }

                                else if(tagName.equals("weather")) {
                                    icon = xpp.getAttributeValue(null, "icon");
                                    urlString = "http://openweathermap.org/img/w/" + icon + ".png";

                                    if (fileExistance(icon +".png")){
                                        Log.i("Found", "Image already exist");
                                        FileInputStream fis = null;
                                        try {    fis = openFileInput(icon +".png");   }
                                        catch (FileNotFoundException e) {    e.printStackTrace();  }
                                        image = BitmapFactory.decodeStream(fis);

                                    }
                                    else{
                                        image = null;
                                        URL urlImage = new URL(urlString);
                                        HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();
                                        connection.connect();
                                        int responseCode = connection.getResponseCode();
                                        if (responseCode == 200) {
                                            image = BitmapFactory.decodeStream(connection.getInputStream());
                                        }

                                        FileOutputStream outputStream = openFileOutput( icon + ".png", Context.MODE_PRIVATE);
                                        image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                        outputStream.flush();
                                        outputStream.close();

                                    }


                                }
                                publishProgress(100);




                                break;
                            case END_TAG:           //This is an end tag: </ ... >
                                break;
                            case TEXT:              //This is text between tags < ... > Hello world </ ... >
                                break;
                        }
                        xpp.next(); // move the pointer to next XML element
                    }

                    //start of JSON reading of UV
                    //connect to the server
                    URL UVurl = new URL(queryURL2);
                    HttpURLConnection UVurlConnection = (HttpURLConnection) UVurl.openConnection();
                    inStream = UVurlConnection.getInputStream();

                    //create a JSON object
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    String result = sb.toString();

                    JSONObject jObject = new JSONObject(result);
                    uvvalue = jObject.getString("value");


                    //get weather image

                }
                catch(MalformedURLException mfe){ ret = "Malformed URL exception"; }
                catch(IOException ioe)          { ret = "IO Exception. Is the Wifi connected?";}
                catch(XmlPullParserException pe){ ret = "XML Pull exception. The XML is not properly formed" ;} catch (JSONException e) {
                    e.printStackTrace();
                }
                //What is returned here will be passed as a parameter to onPostExecute:
                return ret;
            }

        @Override                   //Type 3
        protected void onPostExecute(String sentFromDoInBackground) {
            super.onPostExecute(sentFromDoInBackground);
            currentTemperature.setText("Current Temperature is: " + currentTemp);
            minTemperature.setText("Lowest Temperature is: "+ minTemp);
            maxTemperature.setText("Highest Temperature is: "+ maxTemp);
            uv.setText("UV is" + uvvalue);
            weatherImage.setImageBitmap(image);
            loader.setVisibility(View.INVISIBLE);



        }

        //@Override                       //Type 2
        protected void onProgressUpdate(Integer... values) {
            loader.setVisibility(View.VISIBLE);
            loader.setProgress(values[0]);

        }



    }

    public boolean fileExistance(String fname){
        File file = getBaseContext().getFileStreamPath(fname);
        return file.exists();   }


}


