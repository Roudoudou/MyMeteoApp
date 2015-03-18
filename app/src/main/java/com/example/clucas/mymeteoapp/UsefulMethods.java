package com.example.clucas.mymeteoapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by clucas on 18/03/2015.
 */
//infoDay contient un String indiquant quelle est l'heure actuelle (on charge la météo précédente car infoclimat nous fournit la météo pour toutes les 3h.
//infoDay contient également un booléen indiquant s'il fait actuellement jour ou non (pour afficher la lune ou le soleil)
public class UsefulMethods {

    private static String TAG = "MyMeteoApp";
    private static String currentDateHour;
    private static boolean isDay;
    private static ArrayList<Object> infoDay = new ArrayList<Object>();

    static ArrayList<Object> settingCurrentDateHour() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(calendar.getTime());

        int currentHour = 0;
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int ecart = hours - currentHour;
        while(!(ecart >= 0 && ecart < 3)) {
            currentHour +=3;
            ecart = hours - currentHour;
        }
        String stringCurrentHour;
        stringCurrentHour = String.valueOf(currentHour);
        if(currentHour <10) {
            stringCurrentHour = "0"+ stringCurrentHour;
        }
        stringCurrentHour = stringCurrentHour +":00:00";

        //On concatene la date et l'heure
        currentDateHour = currentDate+" "+stringCurrentHour;
        infoDay.add(currentDateHour);

        switch(currentHour) {
            case 21:
            case 0:
            case 3:
            case 6:
                isDay = false;
                break;
            default:
                isDay = true;
        }
        infoDay.add(isDay);

        return infoDay;
    }




    static HashMap<String, String> getWeather(String json, String currentDateHour, boolean isDay) {

        HashMap<String, String> mapWeather = new HashMap<>();
        double temp = 0;
        String logo;
        int request_state;



        try {
            JSONObject jsonObj = new JSONObject(json);
            request_state = jsonObj.getInt("request_state");

            if (request_state == 200) {
                JSONObject jsonCurrentWeather = jsonObj.getJSONObject(currentDateHour);

                //Tepmpérature
                JSONObject jsonTemp = jsonCurrentWeather.getJSONObject("temperature");
                temp = jsonTemp.getDouble("2m");
                temp = (int) Math.round(temp - 273.15);
                String stringTemp = String.valueOf(temp);
                stringTemp += " °C";
                mapWeather.put("temperature", stringTemp);
                //Logo de météo en fonction du temps
                JSONObject jsonNeb = jsonCurrentWeather.getJSONObject("nebulosite");


            } else {
                mapWeather.put("temperature", "Nodata");
                mapWeather.put("logo", String.valueOf(R.drawable.triste));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!(mapWeather.containsKey("logo"))) {
            logo = settingWeatherLogo(isDay);
            mapWeather.put("logo", logo);
        }

        return mapWeather;
    }

    static String settingWeatherLogo(boolean isDay) {

       if (isDay){
        return String.valueOf(R.drawable.soleil);}
       else return String.valueOf(R.drawable.lune);


        }
    }


