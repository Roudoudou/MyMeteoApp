package com.example.clucas.mymeteoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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


public class FavoritesGestionActivity extends ActionBarActivity {

    final Context context = this;
    private  String name_place;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Map<String,?> mapFavorites;
    ArrayList<HashMap<String, String>> places = new ArrayList<HashMap<String, String>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = pref.edit();
        mapFavorites = pref.getAll();

        int i = 0;
        Iterator iterator = mapFavorites.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry) iterator.next();
            String place = (String) mapEntry.getKey();
            HashMap<String, String> mapPlace = new HashMap<String, String>();
            mapPlace.put("place", place);
            places.add(mapPlace);
        }

        ListView listView = (ListView) findViewById(R.id.listViewFavorite);

        SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(), places, R.layout.item_favorite,
                new String[] {"place"}, new int[] {R.id.onePlaceFavorite});

        listView.setAdapter(adapter);

        Button btn = (Button) findViewById(R.id.btn_add_favorites);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FavoritesGestionActivity.this, ModuleFavoritesActivity.class);
                startActivity(intent);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        Toast.makeText(this, "Menus item selected: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        switch (item.getItemId()) {
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

    public void onClickDelete(View v) {

        LinearLayout linview = (LinearLayout) v.getParent();
        TextView text = (TextView) linview.getChildAt(0);
        name_place = (String) text.getText();

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Alert!");
        alert.setMessage("Enlever ce lieu de vos favoris? : "+ name_place);
        alert.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                erasePlace(name_place);
            }
        });
        alert.setNegativeButton("Non",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alert.show();
    }
    private void erasePlace(String name_place) {
        editor.remove(name_place);
        editor.commit();
        Intent intent = new Intent(context, FavoritesGestionActivity.class);
        startActivity(intent);
    }

}

