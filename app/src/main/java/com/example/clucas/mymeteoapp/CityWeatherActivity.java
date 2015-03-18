package com.example.clucas.mymeteoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class CityWeatherActivity extends ActionBarActivity {

    String name_place;
    String date;
    String date2;
    String json;
    SharedPreferences preferences;
    Map<String,?> mapPref;
    ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailedweather);


        ProgressBar pb = (ProgressBar) findViewById(R.id.progressbar);
        pb.setVisibility(View.GONE);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mapPref = preferences.getAll();

        Bundle extras = getIntent().getExtras();
        name_place = extras.getString("place");

        setTitle(name_place);

        String infoPlace = (String) mapPref.get(name_place);
        json = infoPlace.split("///")[2];



        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        boolean isDay = true;


        for (int i = 0 ; i < 4 ; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            if (i != 0){
                calendar.add(Calendar.DATE, 1);
            }
            date = dateFormat1.format(calendar.getTime());
            String dateHour = date+" 00:00:00";
            map = UsefulMethods.getWeather(json, dateHour, isDay);
            date2 = dateFormat2.format(calendar.getTime());
            map.put("date", date2);
            listItem.add(map);
            String dateHourNight = date+" 12:00:00";
            map = UsefulMethods.getWeather(json, dateHourNight, !isDay);
            date2 = dateFormat2.format(calendar.getTime());
            map.put("date",date2);
            listItem.add(map);
        }

        ListView listView = (ListView) findViewById(R.id.listDetailedWeather);
        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), listItem, R.layout.item_detailed_weather,
                new String[]{"date", "temperature", "logo"}, new int[]{R.id.date, R.id.temperature,
                R.id.logo});

        listView.setAdapter(adapter);


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
            case R.id.submenupos:
                Intent intent = new Intent(this, PositionActivity.class);
                startActivity(intent);
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

}
