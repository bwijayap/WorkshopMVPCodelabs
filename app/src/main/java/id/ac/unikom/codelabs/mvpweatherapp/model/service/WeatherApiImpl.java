package id.ac.unikom.codelabs.mvpweatherapp.model.service;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import id.ac.unikom.codelabs.mvpweatherapp.model.Weather;

/**
 * Created by Bayu WPP on 10/13/2016.
 */

public class WeatherApiImpl implements WeatherApi {
    private final static String URL_API = "http://api.openweathermap.org/data/2.5/group?id=1650357,1215502,1625084,1646170,1633070,1643776,1651531,1214520,1624647,1648473,1630789,1629001,1642911,1621177,1627896,1625822,8064082,1645528,1643837,2082600&units=metric&APPID=b35ad63f5f5ced9b4bceaf049edf6dfb";
    private List<Weather> weathers;

    @Override
    public void getWeathers(WeatherApiCallback<List<Weather>> callback) {
        new getWeathersFromApi(callback).execute();
    }

    private class getWeathersFromApi extends AsyncTask<Void, Void, List<Weather>> {

        private WeatherApiCallback callback;

        public getWeathersFromApi(WeatherApiCallback callback) {
            this.callback = callback;
        }

        @Override
        protected List<Weather> doInBackground(Void... params) {
            try {
                URL url = new URL(URL_API);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                StringBuilder content = new StringBuilder();

                String line;
                while ((line = in.readLine()) != null ){
                    content.append(line);
                }
                JSONArray list = new JSONObject(content.toString())
                                        .getJSONArray("list");
                List<Weather> weathers = new ArrayList<>();
                JSONObject object;
                for (int i=0; i< list.length(); i++){
                    object = list.getJSONObject(i);
                    float temp = BigDecimal.valueOf(
                            object.getJSONObject("main")
                                .getDouble("temp"))
                                .floatValue();
                    float windSpeed = BigDecimal.valueOf(
                            object.getJSONObject("wind")
                                .getDouble("speed"))
                                .floatValue();
                    weathers.add(new
                            Weather(temp,
                            windSpeed,
                            object.getString("name"),
                            object.getJSONArray("weather")
                                    .getJSONObject(0)
                                    .getString("description")));
                }
                if (weathers != null){
                    return weathers;
                }
            } catch (IOException | JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(List<Weather> weathers) {
            if (weathers != null){
                callback.onSuccess(weathers);
            } else {
                callback.onFailure();
            }
        }
    }
}
