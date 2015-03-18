package com.example.clucas.mymeteoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ModuleFavoritesActivity extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Map<String,?> mapFavorites;

    public String TAG = "MyMeteoApp";
    AutoCompleteTextView textView;
    ArrayList<String> arrPlaces = new ArrayList<String>();
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_favorites);

        context = getApplicationContext();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteAddFavorite);

        textView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String search = textView.getText().toString();
                    arrPlaces.clear();

                    new AutoCompleteAsyncTask(search).execute();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String textPlace = ((TextView) view).getText().toString();

                new AddFavoriteAsyncTask(textPlace).execute();
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


    private class AutoCompleteAsyncTask extends AsyncTask<Void, Void, ArrayList<String>> {

        private String search;
        private String url;
        private String stringPredictions;

        public AutoCompleteAsyncTask(String search) {
            this.search = search;
        }


        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {

                url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="+ URLEncoder.encode(search, "UTF-8") +"&types=geocode&language=en&sensor=true&key=AIzaSyAW53ZbwyyltDn7YiqUDNEJhTEE8IkRx_s";
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                HttpResponse response = httpclient.execute(httpget);

                HttpEntity httpEntity = response.getEntity();
                stringPredictions = EntityUtils.toString((httpEntity));

                JSONObject predictions = new JSONObject(stringPredictions);
                JSONArray jaPredictions = new JSONArray(predictions.getString("predictions"));

                for (int i=0 ; i<jaPredictions.length(); i++) {
                    JSONObject onePrediction = (JSONObject) jaPredictions.get(i);
                    arrPlaces.add(onePrediction.getString("description"));
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return arrPlaces;
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, result);
            adapter.setNotifyOnChange(true);
            textView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private class AddFavoriteAsyncTask extends AsyncTask<Void, Void, String> {

        private String textPlace;
        private String json;
        private String url;

        public AddFavoriteAsyncTask(String textPlace) {
            this.textPlace = textPlace;
        }

        @Override
        protected String doInBackground(Void... params) {

            if(!checkIfInFavorites(textPlace)) {
                double latitude = 0, longitude = 0;


                Geocoder geocoder = new Geocoder(context);
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(textPlace, 1);
                    if (addresses.size() > 0) {
                        latitude = addresses.get(0).getLatitude();
                        longitude = addresses.get(0).getLongitude();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //On charge le json
                try {
                    url = "http://www.infoclimat.fr/public-api/gfs/json?_ll=" + latitude + "," + longitude + "&_auth=VE5VQgd5ASNWewQzA3UBKFM7V2IAdgcgUCwAYw9kUC0FYQJiD2wAYlU%2BA34PIFJ7WGoHeQw1Bj4Aa1YyC2JeIlQoVTIHZAFgVjEEZQM3ATRTf1coACIHPlAsAHgPa1AxBXgCZw9oAGdVJANlDz9SeFhpB2cMNQYhAHxWMAtiXj9UM1U4B2IBYFY6BGUDOgEqU39XMgA7B2pQZQBkD2VQMwU0AjMPagA2VToDYA86UnhYaAdmDDQGOABmVjILbl45VChVLgcdARBWJAQmA3EBYFMmVyoAagdhUGc%3D&_c=d520328d1ed42eb27e316615bba8692e";
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet(url);
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity httpEntity = response.getEntity();
                    json = EntityUtils.toString((httpEntity));
                } catch (IOException e) {
                    e.printStackTrace();
                }


                String saveToPreferences = String.valueOf(latitude) + "///" + String.valueOf(longitude) + "///" + json;
                return saveToPreferences;
            } else {
                return "place_In_Favorites";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.equals("place_In_Favorites")) {
                editor.putString(textPlace, result);
                editor.commit();
                Intent intent = new Intent(context, FavoritesGestionActivity.class);
                startActivity(intent);
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast toast = Toast.makeText(getApplicationContext(), "Ce lieu est déjà dans les favoris", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, -100);
                        toast.show();
                    }
                });

            }

        }

        public boolean checkIfInFavorites(String textPlace){
            mapFavorites = preferences.getAll();
            boolean contains = mapFavorites.containsKey(textPlace);
            return contains;
        }
    }


}
