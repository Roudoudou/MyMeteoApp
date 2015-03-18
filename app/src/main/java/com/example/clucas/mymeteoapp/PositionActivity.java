package com.example.clucas.mymeteoapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class PositionActivity extends ActionBarActivity implements LocationListener {

    private String TAG = "MyMeteoApp";
    LocationManager locationManager;
    double latitude, longitude;
    Context context;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailedweather);

        context = getApplicationContext();

        text = (TextView) findViewById(R.id.load_gps);
        text.setText("Chargement des données GPS en cours");


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if ( locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 3000, this);
        } else {
            text.setText("");
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar);
            pb.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Veuillez activer votre GPS et réessayez", Toast.LENGTH_LONG).show();
            new SleepAsyncTask().execute();
        }


    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        text.setText("");
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar);
        pb.setVisibility(View.GONE);

        new LocalWeatherAsyncTask(latitude, longitude).execute();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {}



    private class LocalWeatherAsyncTask extends AsyncTask<Void, Void, ArrayList<HashMap<String,String>>> {

        private double latitude, longitude;
        private String url;
        private String json;
        private String date;
        private String date2;
        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

        public LocalWeatherAsyncTask(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected ArrayList<HashMap<String,String>> doInBackground(Void... params) {

            try {
                url = "http://www.infoclimat.fr/public-api/gfs/json?_ll=" + latitude + "," + longitude + "&_auth=VU9RRgR6VXdecwYxBXNVfFA4VWAOeAkuB3sDYApvBXgHbARlAmJXMVI8VShXeAI0UXxSMQA7UGAAa1IqXy0DYlU%2FUT0Eb1UyXjEGYwUqVX5QflU0Di4JLgdtA2YKeQVnB2EEaAJ%2FVzNSO1UpV2MCMFF9Ui0APlBsAGFSM181A2BVNVE0BGJVN14uBnsFMFVkUGNVPA5iCTIHZgMxCmcFNwcxBGMCMlc8UiNVMlduAjBRa1IwAD1QagBnUipfLQMZVUVRKAQnVXVeZAYiBShVNFA9VWE%3D&_c=c5019d639fa57d3d4e40561c8a144de4";
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity httpEntity = response.getEntity();
                json = EntityUtils.toString((httpEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }


            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
            boolean isDay = true;

            for (int i = 0 ; i < 8 ; i++) {
                HashMap<String, String> map = new HashMap<String, String>();
                if (i != 0){
                    calendar.add(Calendar.DATE, 1);
                }
                date = dateFormat1.format(calendar.getTime());
                String dateHour = date+" 15:00:00";
                map = UsefulMethods.getWeather(json, dateHour, isDay);
                date2 = dateFormat2.format(calendar.getTime());
                map.put("date",date2);
                listItem.add(map);
            }



            return listItem;
        }

        @Override
        protected void onPostExecute(ArrayList<HashMap<String,String>> result) {
            ListView listView = (ListView) findViewById(R.id.listDetailedWeather);
            SimpleAdapter adapter = new SimpleAdapter(context, listItem, R.layout.item_detailed_weather,
                    new String[]{"date", "temperature", "logo"}, new int[]{R.id.date, R.id.temperature,
                    R.id.logo});

            listView.setAdapter(adapter);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Toast.makeText(this, "Menus item selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
            case R.id.submenufav:
                Intent intent1 = new Intent(this, FavoritesGestionActivity.class);
                startActivity(intent1);
                return true;
            case R.id.submenuacceuil:
               Intent intent2 = new Intent(this, MainActivity.class);
             startActivity(intent2);
            return true;
            default:
                return false;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private class SleepAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
            return null;
        }

    }

}