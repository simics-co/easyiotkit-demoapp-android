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

package com.kubotaku.android.sample.sensordataviewer;

import android.content.Context;
import android.content.SharedPreferences;

import com.kubotaku.android.sample.sensordataviewer.model.StreamEntity;

/**
 * This Application's SharedPreferences wrapper class.
 */
public class AppPreferences {

    /**
     * データ取得時刻設定：プリセットから選択する
     */
    public static final int SELECT_TIME_TYPE_PRESET = 0;

    /**
     * データ取得時刻設定：カスタム設定する
     */
    public static final int SELECT_TIME_TYPE_DETAIL = 1;

    /**
     * データ取得時刻設定プリセット（過去1hrのデータを取得する)
     */
    public static final int TIME_PRESET_1HOUR = 0;

    /**
     * データ取得時刻設定プリセット（過去3hrのデータを取得する)
     */
    public static final int TIME_PRESET_3HOUR = 1;

    /**
     * データ取得時刻設定プリセット（過去6hrのデータを取得する)
     */
    public static final int TIME_PRESET_6HOUR = 2;

    /**
     * データ取得時刻設定プリセット（過去12hrのデータを取得する)
     */
    public static final int TIME_PRESET_12HOUR = 3;

    /**
     * データ取得時刻設定プリセット（過去24hrのデータを取得する)
     */
    public static final int TIME_PRESET_24HOUR = 4;

    private static final String PREFS_NAME = "app_prefs";

    private static final String KEY_API_TOKEN = "key_api_token";

    private static final String KEY_SELECT_TIME_TYPE = "key_select_time_type";

    private static final String KEY_SELECT_TIME_PRESET_INDEX = "key_select_time_preset_index";

    private static final String KEY_SELECT_TIME_FROM = "key_select_time_from";

    private static final String KEY_SELECT_TIME_TO = "key_select_time_to";

    private static final String KEY_USE_WEATHER_INFO = "key_use_weather_info";

    private static final String KEY_WEATHER_API_KEY = "key_weather_api";

    private static final String KEY_WEATHER_PLACE_NAME = "key_weahter_place_name";

    private static final String KEY_WEATHER_PLACE_LAT = "key_weather_place_lat";

    private static final String KEY_WEATHER_PLACE_LON = "key_weather_place_lon";

    /**
     * 天気情報クラス。
     */
    public static class WeatherPlaceInfo {
        /**
         * 天気情報取得有無
         */
        public boolean useWeatherInfo;

        /**
         * 天気情報APIキー
         */
        public String apiKey;

        /**
         * 地点名称
         */
        public String name;

        /**
         * 天気情報取得地点・緯度
         */
        public double latitude;

        /**
         * 天気情報取得地点・経度
         */
        public double longitude;

        public WeatherPlaceInfo(final boolean useWeatherInfo,
                                final String apiKey,
                                final String name,
                                final double latitude, final double longitude) {
            this.useWeatherInfo = useWeatherInfo;
            this.apiKey = apiKey;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    /**
     * APIトークンを保存する。
     *
     * @param context  コンテキスト
     * @param apiToken APIトークン
     */
    public static void saveApiToken(Context context, final String apiToken) {
        SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_API_TOKEN, apiToken);
        editor.commit();
    }

    /**
     * 保存済みのAPIトークンを取得する。
     *
     * @param context コンテキスト
     * @return 保存されているAPIトークン文字列。未保存の場合、nullが返る。
     */
    public static String getApiToken(Context context) {
        SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getString(KEY_API_TOKEN, null);
    }

    /**
     * データ取得時刻の選択タイプ(プリセットorカスタム）を保存する。
     *
     * @param context コンテキスト
     * @param type    選択タイプ
     *                {@link #SELECT_TIME_TYPE_PRESET} : プリセットから選ぶ
     *                {@link #SELECT_TIME_TYPE_DETAIL} : カスタム設定
     */
    public static void saveSelectTimeType(Context context, final int type) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SELECT_TIME_TYPE, type);
        editor.commit();
    }

    /**
     * データ取得時刻の選択タイプを取得する。
     *
     * @param context コンテキスト
     * @return 取得タイプ
     * @see #saveSelectTimeType(Context, int)
     */
    public static int getSelectTimeType(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getInt(KEY_SELECT_TIME_TYPE, SELECT_TIME_TYPE_PRESET);
    }

    /**
     * データ取得時刻設定で選択されたプリセットインデックスを保存する。
     *
     * @param context コンテキスト
     * @param index   プリセットインデックス
     *                {@link #TIME_PRESET_1HOUR}
     *                {@link #TIME_PRESET_3HOUR}
     *                {@link #TIME_PRESET_6HOUR}
     *                {@link #TIME_PRESET_12HOUR}
     *                {@link #TIME_PRESET_24HOUR}
     */
    public static void saveSelectTimePresetIndex(Context context, final int index) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SELECT_TIME_PRESET_INDEX, index);
        editor.commit();
    }

    /**
     * データ取得時刻設定で選択されたプリセットインデックスを取得する。
     *
     * @param context コンテキスト
     * @return プリセットインデックス
     */
    public static int getSelectTimePresetIndex(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getInt(KEY_SELECT_TIME_PRESET_INDEX, TIME_PRESET_1HOUR);
    }

    /**
     * 時刻設定プリセットインデックスを時間に変換する。
     *
     * @param presetIndex プリセットインデックス
     * @return 時間
     */
    public static int convertTimePresetIndexToTime(final int presetIndex) {
        int time;

        switch (presetIndex) {
            default:
            case TIME_PRESET_1HOUR:
                time = 1;
                break;

            case TIME_PRESET_3HOUR:
                time = 3;
                break;

            case TIME_PRESET_6HOUR:
                time = 6;
                break;

            case TIME_PRESET_12HOUR:
                time = 12;
                break;

            case TIME_PRESET_24HOUR:
                time = 24;
                break;

        }

        return time;
    }

    /**
     * データ取得時刻設定でカスタム設定された開始時刻を保存する。
     *
     * @param context  コンテキスト
     * @param timeFrom 開始時刻
     */
    public static void saveSelectTimeFrom(Context context, final long timeFrom) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_SELECT_TIME_FROM, timeFrom);
        editor.commit();
    }

    /**
     * データ取得時刻設定でカスタム設定された開始時刻を取得する。
     *
     * @param context コンテキスト
     * @return 開始時刻
     */
    public static long getSelectTimeFrom(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getLong(KEY_SELECT_TIME_FROM, -1);
    }

    /**
     * データ取得時刻設定でカスタム設定された修了時刻を保存する。
     *
     * @param context コンテキスト
     * @param timeTo  修了時刻
     */
    public static void saveSelectTimeTo(Context context, final long timeTo) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_SELECT_TIME_TO, timeTo);
        editor.commit();
    }

    /**
     * データ取得時刻設定でカスタム設定された修了時刻を取得する。
     *
     * @param context コンテキスト
     * @return 修了時刻
     */
    public static long getSelectTimeTo(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getLong(KEY_SELECT_TIME_TO, -1);
    }

    /**
     * 天気情報設定を保存する。
     *
     * @param context コンテキスト
     * @param info    天気情報設定
     */
    public static void saveWeatherPlaceSettings(Context context, final WeatherPlaceInfo info) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(KEY_USE_WEATHER_INFO, info.useWeatherInfo);
        editor.putString(KEY_WEATHER_API_KEY, info.apiKey);
        editor.putString(KEY_WEATHER_PLACE_NAME, info.name);
        editor.putString(KEY_WEATHER_PLACE_LAT, "" + info.latitude);
        editor.putString(KEY_WEATHER_PLACE_LON, "" + info.longitude);

        editor.commit();
    }

    /**
     * 天気情報設定を取得する。
     *
     * @param context コンテキスト
     * @return 天気情報設定
     */
    public static WeatherPlaceInfo getWeatherPlaceSettings(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);

        final boolean useWeatherInfo = prefs.getBoolean(KEY_USE_WEATHER_INFO, false);
        final String apiKey = prefs.getString(KEY_WEATHER_API_KEY, "");
        final String name = prefs.getString(KEY_WEATHER_PLACE_NAME, "Not Set");

        final String latText = prefs.getString(KEY_WEATHER_PLACE_LAT, "0");
        double lat = Double.parseDouble(latText);

        final String lonText = prefs.getString(KEY_WEATHER_PLACE_LON, "0");
        double lon = Double.parseDouble(lonText);

        WeatherPlaceInfo info = new WeatherPlaceInfo(useWeatherInfo, apiKey, name, lat, lon);
        return info;
    }

    private static SharedPreferences getPrefs(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
