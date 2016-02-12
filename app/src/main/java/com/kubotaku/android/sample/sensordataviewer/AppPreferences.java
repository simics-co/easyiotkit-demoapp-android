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

    private static final String PREFS_NAME = "app_prefs";

    private static final String KEY_API_TOKEN = "key_api_token";

    /**
     * APIトークンを保存する。
     *
     * @param context コンテキスト
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

    private static SharedPreferences getPrefs(Context context, String name) {
        return context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }
}
