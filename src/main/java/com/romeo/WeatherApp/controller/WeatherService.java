package com.romeo.WeatherApp.controller;


import okhttp3.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WeatherService {
    private OkHttpClient client;
    private Response response;

    private String cityName;
    private String unit;

    public JSONObject getWeather() throws JSONException {

        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://api.openweathermap.org/data/2.5/weather?q=" + getCityName() + "&units=" + getUnit() + "&appid=e97f60a6769e0f7ab626c2288e64082a")
                .build();

        try {
            response = client.newCall(request).execute();
            return new JSONObject(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONArray returnWeatherArray() throws JSONException {
        return getWeather().getJSONArray("weather");
    }

    public JSONObject returnMainObject() throws JSONException {
        return getWeather().getJSONObject("main");
    }

    public JSONObject returnWindObject() throws JSONException {
        return getWeather().getJSONObject("wind");
    }

    public JSONObject returnSunSet() throws JSONException {
        return getWeather().getJSONObject("sys");
    }

    private String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    private String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
