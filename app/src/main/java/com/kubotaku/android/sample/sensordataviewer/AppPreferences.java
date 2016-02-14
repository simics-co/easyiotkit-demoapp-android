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

/**
 * This Application's SharedPreferences wrapper class.
 */
public class AppPreferences {

    public static final int SELECT_TIME_TYPE_PRESET = 0;

    public static final int SELECT_TIME_TYPE_DETAIL = 1;

    public static final int TIME_PRESET_1HOUR = 0;

    public static final int TIME_PRESET_3HOUR = 1;

    public static final int TIME_PRESET_6HOUR = 2;

    public static final int TIME_PRESET_12HOUR = 3;

    public static final int TIME_PRESET_24HOUR = 4;

    private static final String PREFS_NAME = "app_prefs";

    private static final String KEY_API_TOKEN = "key_api_token";

    private static final String KEY_SELECT_TIME_TYPE = "key_select_time_type";

    private static final String KEY_SELECT_TIME_PRESET_INDEX = "key_select_time_preset_index";

    private static final String KEY_SELECT_TIME_FROM = "key_select_time_from";

    private static final String KEY_SELECT_TIME_TO = "key_select_time_to";

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

    public static void saveSelectTimeType(Context context, final int type) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SELECT_TIME_TYPE, type);
        editor.commit();
    }

    public static int getSelectTimeType(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getInt(KEY_SELECT_TIME_TYPE, SELECT_TIME_TYPE_PRESET);
    }

    public static void saveSelectTimePresetIndex(Context context, final int index) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_SELECT_TIME_PRESET_INDEX, index);
        editor.commit();
    }

    public static int getSelectTimePresetIndex(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getInt(KEY_SELECT_TIME_PRESET_INDEX, TIME_PRESET_1HOUR);
    }

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

    public static void saveSelectTimeFrom(Context context, final long timeFrom) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_SELECT_TIME_FROM, timeFrom);
        editor.commit();
    }

    public static long getSelectTimeFrom(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getLong(KEY_SELECT_TIME_FROM, -1);
    }

    public static void saveSelectTimeTo(Context context, final long timeTo) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_SELECT_TIME_TO, timeTo);
        editor.commit();
    }

    public static long getSelectTimeTo(Context context) {
        final SharedPreferences prefs = getPrefs(context, PREFS_NAME);
        return prefs.getLong(KEY_SELECT_TIME_TO, -1);
    }


    private static SharedPreferences getPrefs(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
