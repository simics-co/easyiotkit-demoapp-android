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

import android.support.annotation.NonNull;
import android.util.Log;

import com.kubotaku.android.sample.sensordataviewer.model.ChannelEntity;
import com.kubotaku.android.sample.sensordataviewer.model.ChannelEntityGen;
import com.kubotaku.android.sample.sensordataviewer.model.StreamEntity;
import com.kubotaku.android.sample.sensordataviewer.model.StreamEntityGen;

import net.vvakame.util.jsonpullparser.JsonFormatException;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Wrapper class of Scalenics web api.
 * <p>
 * https://sensinics.atlassian.net/wiki/pages/viewpage.action?pageId=7700553
 * </p>
 */
public class ScalenicsAccessor {

    private static final String TAG = ScalenicsAccessor.class.getSimpleName();

    private static final String BASE_URL = "https://api.scalenics.io/v1/";

    private static final String STREAM_URL = "stream/";

    private static final String CHANNEL_URL = "channel/";

    private static final String HEADER_API_TOKEN = "X-APITOKEN";

    private static final String HEADER_START_TIME = "X-STARTTIME";

    private static final String HEADER_END_TIME = "X-ENDTIME";

    private static final String HEADER_AGGREGATE = "X-AGGREGATE";

    private static final String PARAM_END_TIME_LAST = "last";

    private static final String PARAM_AGGREGATE_AVG = "avg";

    private static final String PARAM_AGGREGATE_SUM = "sum";

    private static final String PARAM_AGGREGATE_MIN = "min";

    private static final String PARAM_AGGREGATE_MAX = "max";

    private OkHttpClient okHttpClient = new OkHttpClient();

    private String apiToken;

    /**
     * コンストラクタ。
     *
     * @param apiToken ScalenicsのAPIトークン
     * @throws Exception
     */
    public ScalenicsAccessor(@NonNull final String apiToken) throws Exception {
        if (apiToken == null) {
            throw new Exception("Please set API token.");
        }

        this.apiToken = apiToken;
    }

    /**
     * 指定のChannel情報を取得する。
     *
     * @param channel channel ID
     * @return Channel情報リスト
     */
    public List<ChannelEntity> getChannelInfo(final String channel) {
        List<ChannelEntity> entities = null;

        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_API_TOKEN, this.apiToken);

        final String url = BASE_URL + CHANNEL_URL + channel;

        try {
            String response = runApi(url, headers);
            Log.d(TAG, response);

            entities = ChannelEntityGen.getList(response);
            if (entities != null) {
                int count = entities.size();
                for (int index = count - 1; index >= 0; index--) {
                    if (entities.get(index).getChannel() == 0) {
                        entities.remove(index);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonFormatException e) {
            e.printStackTrace();
        }

        return entities;
    }

    /**
     * 指定Channelの最新1件のStreamデータを取得する。
     *
     * @param channel channel ID
     * @return Streamデータ
     */
    public StreamEntity getNewestStreamData(final String channel) {

        StreamEntity entity = null;

        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_API_TOKEN, this.apiToken);
        headers.put(HEADER_END_TIME, PARAM_END_TIME_LAST);

        final String url = BASE_URL + STREAM_URL + channel;

        try {
            String response = runApi(url, headers);
            Log.d(TAG, response);

            entity = StreamEntityGen.get(response);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonFormatException e) {
            e.printStackTrace();
        }

        return entity;
    }

    /**
     * 指定Channelの過去Nhr分のStreamデータを取得する。
     *
     * @param channel channel ID
     * @param nHour   Get N hours ago data.
     * @return Streamデータ
     */
    public StreamEntity getLastNHourStreamData(final String channel, final int nHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String currentTimeText = createISO8601FormatTextWithoutTimeZone(calendar);
        currentTimeText = currentTimeText.replaceAll(Pattern.quote("T"), " ");
        currentTimeText = currentTimeText.replaceAll(Pattern.quote("+"), "%2B");

        // N hour ago
        calendar.add(Calendar.HOUR_OF_DAY, -nHour);
        String oneHourAgoText = createISO8601FormatTextWithoutTimeZone(calendar);
        oneHourAgoText = oneHourAgoText.replaceAll(Pattern.quote("T"), " ");
        oneHourAgoText = oneHourAgoText.replaceAll(Pattern.quote("+"), "%2B");

        return getStreamData(channel, oneHourAgoText, currentTimeText);
    }

    /**
     * 指定Channelの指定期間のStreamデータを取得する。
     *
     * @param channel  channel ID
     * @param dateFrom 開始時刻(Unix Time)
     * @param dateTo   終了時刻(Unix Time)
     * @return Streamデータ
     */
    public StreamEntity getSelectTimeStreamData(final String channel, final long dateFrom, final long dateTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateFrom));
        String dateFromText = createISO8601FormatTextWithoutTimeZone(calendar);
        dateFromText = dateFromText.replaceAll(Pattern.quote("T"), " ");
        dateFromText = dateFromText.replaceAll(Pattern.quote("+"), "%2B");

        calendar.setTime(new Date(dateTo));
        String dateToText = createISO8601FormatTextWithoutTimeZone(calendar);
        dateToText = dateToText.replaceAll(Pattern.quote("T"), " ");
        dateToText = dateToText.replaceAll(Pattern.quote("+"), "%2B");

        return getStreamData(channel, dateFromText, dateToText);
    }

    /**
     * 指定Channelの過去Nhr分のStreamデータの平均値を取得する。
     *
     * @param channel channel ID
     * @param nHour   Get N hours ago data.
     * @return 平均値データ。
     */
    public StreamEntity getLastNHourAvgStreamData(final String channel, final int nHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String currentTimeText = createISO8601FormatTextWithoutTimeZone(calendar);
        currentTimeText = currentTimeText.replaceAll(Pattern.quote("T"), " ");
        currentTimeText = currentTimeText.replaceAll(Pattern.quote("+"), "%2B");

        // N hour ago
        calendar.add(Calendar.HOUR_OF_DAY, -nHour);
        String oneHourAgoText = createISO8601FormatTextWithoutTimeZone(calendar);
        oneHourAgoText = oneHourAgoText.replaceAll(Pattern.quote("T"), " ");
        oneHourAgoText = oneHourAgoText.replaceAll(Pattern.quote("+"), "%2B");

        return getStreamData(channel, PARAM_AGGREGATE_AVG, oneHourAgoText, currentTimeText);
    }

    /**
     * 指定Channelの指定期間のStreamデータの平均値を取得する。
     *
     * @param channel  channel ID
     * @param dateFrom 開始時刻(Unix Time)
     * @param dateTo   終了時刻(Unix Time)
     * @return 平均値データ。
     */
    public StreamEntity getSelectTimeAvgStreamData(final String channel, final long dateFrom, final long dateTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateFrom));
        String dateFromText = createISO8601FormatTextWithoutTimeZone(calendar);
        dateFromText = dateFromText.replaceAll(Pattern.quote("T"), " ");
        dateFromText = dateFromText.replaceAll(Pattern.quote("+"), "%2B");

        calendar.setTime(new Date(dateTo));
        String dateToText = createISO8601FormatTextWithoutTimeZone(calendar);
        dateToText = dateToText.replaceAll(Pattern.quote("T"), " ");
        dateToText = dateToText.replaceAll(Pattern.quote("+"), "%2B");

        return getStreamData(channel, PARAM_AGGREGATE_AVG, dateFromText, dateToText);
    }

    /**
     * 指定Channelの過去1hr分のStreamデータの最小値を取得する。
     *
     * @param channel channel ID
     * @param nHour   Get N hours ago data.
     * @return 最小値データ。
     */
    public StreamEntity getLastNHourMinStreamData(final String channel, final int nHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String currentTimeText = createISO8601FormatTextWithoutTimeZone(calendar);
        currentTimeText = currentTimeText.replaceAll(Pattern.quote("T"), " ");
        currentTimeText = currentTimeText.replaceAll(Pattern.quote("+"), "%2B");

        // N hour ago
        calendar.add(Calendar.HOUR_OF_DAY, -nHour);
        String oneHourAgoText = createISO8601FormatTextWithoutTimeZone(calendar);
        oneHourAgoText = oneHourAgoText.replaceAll(Pattern.quote("T"), " ");
        oneHourAgoText = oneHourAgoText.replaceAll(Pattern.quote("+"), "%2B");

        return getStreamData(channel, PARAM_AGGREGATE_MIN, oneHourAgoText, currentTimeText);
    }

    /**
     * 指定Channelの指定期間のStreamデータの最小値を取得する。
     *
     * @param channel  channel ID
     * @param dateFrom 開始時刻(Unix Time)
     * @param dateTo   終了時刻(Unix Time)
     * @return 最小値データ。
     */
    public StreamEntity getSelectTimeMinStreamData(final String channel, final long dateFrom, final long dateTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateFrom));
        String dateFromText = createISO8601FormatTextWithoutTimeZone(calendar);
        dateFromText = dateFromText.replaceAll(Pattern.quote("T"), " ");
        dateFromText = dateFromText.replaceAll(Pattern.quote("+"), "%2B");

        calendar.setTime(new Date(dateTo));
        String dateToText = createISO8601FormatTextWithoutTimeZone(calendar);
        dateToText = dateToText.replaceAll(Pattern.quote("T"), " ");
        dateToText = dateToText.replaceAll(Pattern.quote("+"), "%2B");

        return getStreamData(channel, PARAM_AGGREGATE_MIN, dateFromText, dateToText);
    }

    /**
     * 指定Channelの過去1hr分のStreamデータの最大値を取得する。
     *
     * @param channel channel ID
     * @param nHour   Get N hours ago data.
     * @return 最大値データ。
     */
    public StreamEntity getLastNHourMaxStreamData(final String channel, final int nHour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        String currentTimeText = createISO8601FormatTextWithoutTimeZone(calendar);
        currentTimeText = currentTimeText.replaceAll(Pattern.quote("T"), " ");
        currentTimeText = currentTimeText.replaceAll(Pattern.quote("+"), "%2B");

        // N hour ago
        calendar.add(Calendar.HOUR_OF_DAY, -nHour);
        String oneHourAgoText = createISO8601FormatTextWithoutTimeZone(calendar);
        oneHourAgoText = oneHourAgoText.replaceAll(Pattern.quote("T"), " ");
        oneHourAgoText = oneHourAgoText.replaceAll(Pattern.quote("+"), "%2B");

        return getStreamData(channel, PARAM_AGGREGATE_MAX, oneHourAgoText, currentTimeText);
    }

    /**
     * 指定Channelの指定期間のStreamデータの最大値を取得する。
     *
     * @param channel  channel ID
     * @param dateFrom 開始時刻(Unix Time)
     * @param dateTo   終了時刻(Unix Time)
     * @return 最大値データ。
     */
    public StreamEntity getSelectTimeMaxStreamData(final String channel, final long dateFrom, final long dateTo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dateFrom));
        String dateFromText = createISO8601FormatTextWithoutTimeZone(calendar);
        dateFromText = dateFromText.replaceAll(Pattern.quote("T"), " ");
        dateFromText = dateFromText.replaceAll(Pattern.quote("+"), "%2B");

        calendar.setTime(new Date(dateTo));
        String dateToText = createISO8601FormatTextWithoutTimeZone(calendar);
        dateToText = dateToText.replaceAll(Pattern.quote("T"), " ");
        dateToText = dateToText.replaceAll(Pattern.quote("+"), "%2B");

        return getStreamData(channel, PARAM_AGGREGATE_MAX, dateFromText, dateToText);
    }

    /**
     * 指定Channelの指定期間のStreamデータを取得する。
     *
     * @param channel channel ID
     * @return Streamデータ。
     */
    public StreamEntity getStreamData(final String channel, final String dateFrom, final String dateTo) {
        return getStreamData(channel, null, dateFrom, dateTo);
    }

    /**
     * 指定Channelの指定期間の指定データを取得する。
     *
     * @param channel channel ID
     * @return 取得データ。
     */
    public StreamEntity getStreamData(final String channel, final String aggregateMode, final String dateFrom, final String dateTo) {
        StreamEntity entity = null;

        Map<String, String> headers = new HashMap<>();
        headers.put(HEADER_API_TOKEN, this.apiToken);
        if (aggregateMode != null) {
            headers.put(HEADER_AGGREGATE, aggregateMode);
        }
        headers.put(HEADER_START_TIME, dateFrom);
        headers.put(HEADER_END_TIME, dateTo);

        final String url = BASE_URL + STREAM_URL + channel;

        try {
            String response = runApi(url, headers);
            Log.d(TAG, response);

            entity = StreamEntityGen.get(response);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonFormatException e) {
            e.printStackTrace();
        }

        return entity;
    }

    /**
     * APIコールを実行する。
     *
     * @param url     URL
     * @param headers ヘッダー
     * @return 結果
     * @throws IOException
     */
    private String runApi(final String url, Map<String, String> headers) throws IOException {
        Request.Builder builder = new Request.Builder().url(url);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }

        Request request = builder.build();

        Response response = this.okHttpClient.newCall(request).execute();

        return response.body().string();
    }

    /**
     * 時刻情報からISO-8601形式の文字列を生成する。
     *
     * @param time 時刻情報
     * @return 文字列
     */
    public static String createISO8601FormatText(Calendar time) {

        String formattedText = createISO8601FormatTextWithoutTimeZone(time);

        int offsetInMillis = time.getTimeZone().getOffset(time.getTimeInMillis());
        String offset = String.format("%1$02d%2$02d", Math.abs(offsetInMillis / 3600000), Math.abs((offsetInMillis / 60000) % 60));
        offset = (offsetInMillis >= 0 ? "+" : "-") + offset;

        formattedText += offset;

        return formattedText;
    }

    /**
     * 時刻情報からISO-8601形式の文字列を生成する（タイムゾーンなし）。
     *
     * @param time 時刻情報
     * @return 文字列
     */
    public static String createISO8601FormatTextWithoutTimeZone(Calendar time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        String formattedText = sdf.format(time.getTime());

        return formattedText;
    }

    /**
     * ISO-8601形式文字列をMM/dd HH:mm:ss形式に変換する。
     *
     * @param iso8601Text ISO-8601形式文字列
     * @return 変換後の文字列
     */
    public static String convertISO8601ToShortDateFormatText(final String iso8601Text) {
        return convertISO8601ToSimpleDateFormatText(iso8601Text, "MM/dd HH:mm:ss");
    }

    /**
     * ISO-8601形式文字列をyyyy/MM/dd HH:mm:ss形式に変換する。
     *
     * @param iso8601Text ISO-8601形式文字列
     * @return 変換後の文字列
     */
    public static String convertISO8601ToNormalDateFormatText(final String iso8601Text) {
        return convertISO8601ToSimpleDateFormatText(iso8601Text, "yyyy/MM/dd HH:mm:ss");
    }

    /**
     * ISO-8601形式文字列をHH:mm:ss形式に変換する。
     *
     * @param iso8601Text ISO-8601形式文字列
     * @return 変換後の文字列
     */
    public static String convertISO8601ToShortTimeFormatText(final String iso8601Text) {
        return convertISO8601ToSimpleDateFormatText(iso8601Text, "HH:mm:ss");
    }

    /**
     * ISO-8601形式文字列を指定フォーマットに変換する。
     *
     * @param iso8601Text ISO-8601形式文字列
     * @return 変換後の文字列
     */
    public static String convertISO8601ToSimpleDateFormatText(final String iso8601Text, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date date = df.parse(iso8601Text);

            SimpleDateFormat sdf = new SimpleDateFormat(format);
            final String formatText = sdf.format(date);
            return formatText;

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return iso8601Text;
    }
}
