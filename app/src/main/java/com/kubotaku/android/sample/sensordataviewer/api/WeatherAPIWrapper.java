/**
 * Copyright 2016 kubotaku1119 <kubotaku1119@gmail.com>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kubotaku.android.sample.sensordataviewer.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.kubotaku.android.openweathermap.lib.IWeatherGetter;
import com.kubotaku.android.openweathermap.lib.LatLng;
import com.kubotaku.android.openweathermap.lib.OnWeatherGetListener;
import com.kubotaku.android.openweathermap.lib.WeatherGetter;
import com.kubotaku.android.openweathermap.lib.WeatherInfo;

import java.util.ArrayList;
import java.util.Locale;

/**
 * API Wrapper of Weather APIs.
 */
final public class WeatherAPIWrapper implements OnWeatherGetListener {

    private final String apiKey;

    public interface OnWeatherListener {
        void onGetWeatherInfo(WeatherInfo weatherInfo);
    }

    private Context context;

    private OnWeatherListener weatherListener;

    public WeatherAPIWrapper(@NonNull final Context context, @NonNull String apiKey, OnWeatherListener listener) {
        this.context = context;
        this.weatherListener = listener;
        this.apiKey = apiKey;
    }

    public void getWeather(final double latitude, final double longitude) {

        try {
            IWeatherGetter weatherGetter = WeatherGetter.getInstance(context, apiKey);

            Locale locale = Locale.getDefault();
            weatherGetter.setLocale(locale);

            weatherGetter.setEnableWeatherIcon(true);
            weatherGetter.setWeatherIconSize(128);

            LatLng latLng = new LatLng(latitude, longitude);
            weatherGetter.setLatLng(latLng);

            weatherGetter.getWeatherInfo(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetWeatherInfo(ArrayList<WeatherInfo> weatherInfos) {
        if (weatherInfos == null) {
            return;
        }

        if (weatherListener != null) {
            weatherListener.onGetWeatherInfo(weatherInfos.get(0));
        }
    }
}
