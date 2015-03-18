package com.example.clucas.mymeteoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    static final String TAG = "MyMeteoApp";
    private static String currentDateHour;
    private static boolean isDay;
    private SharedPreferences preferences;
    Map<String,?> mapFavorites;
    ArrayList<Object> infoDay = new ArrayList<Object>();
    ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mapFavorites = preferences.getAll();

        //S'il n'y a pas de favoris on en ajoute un
        Button btn = (Button) findViewById(R.id.noFavorite);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ModuleFavoritesActivity.class);
                startActivity(intent);
            }
        });

        if(mapFavorites.size() != 0){
            btn.setVisibility(View.GONE);
        }

        //infoDay récupère l'heure actuelle et nous dit si'il fail jour ou nuit
        infoDay = UsefulMethods.settingCurrentDateHour();
        currentDateHour = infoDay.get(0).toString();
        isDay = (Boolean) infoDay.get(1);

        fillListItem();

        ListView listView = (ListView) findViewById(R.id.listViewMain);
        SimpleAdapter adapter = new SimpleAdapter(this.getBaseContext(), listItem, R.layout.item_weather,
                new String[]{"place", "temperature", "logo"}, new int[]{R.id.place, R.id.temperature,
                R.id.logo});

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                LinearLayout linview = (LinearLayout) view;
                LinearLayout linlay = (LinearLayout) linview.getChildAt(1);
                TextView text = (TextView) linlay.getChildAt(0);
                String name_place = (String) text.getText();
                Intent intent = new Intent(MainActivity.this, CityWeatherActivity.class);
                intent.putExtra("place", name_place);
                startActivity(intent);
            }
        });
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
                Intent intent2 = new Intent(this, PositionActivity.class);
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

    public void fillListItem() {
        Iterator iterator = mapFavorites.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            String value = (String) mapEntry.getValue();
            String[] tabValue = value.split("///");
            String json = tabValue[2];

            HashMap<String, String> mapWeather;
            mapWeather = UsefulMethods.getWeather(json, currentDateHour, isDay);

            String place = (String) mapEntry.getKey();
            mapWeather.put("place",place);

            listItem.add(mapWeather);

        }
    }

}


