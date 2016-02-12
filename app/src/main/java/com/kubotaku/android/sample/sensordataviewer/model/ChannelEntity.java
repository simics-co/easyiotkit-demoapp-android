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

package com.kubotaku.android.sample.sensordataviewer.model;

import android.os.Parcel;
import android.os.Parcelable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

/**
 * Entity class of Channel information.
 */
@JsonModel
public class ChannelEntity implements Parcelable {

    public static final int SENSOR_UNKNOWN = 0;

    public static final int SENSOR_TEMPERATURE = 1;

    public static final int SENSOR_OCCUPANCY = 2;

    public static final int SENSOR_ROCKER_SWITCH = 3;

    private int sensorType;

    @JsonKey
    private int channel;

    @JsonKey
    private String channel_name;

    @JsonKey
    private String last_update;

    @JsonKey
    private String value1_name;

    @JsonKey
    private String value1_unit;

    @JsonKey
    private String value2_name;

    @JsonKey
    private String value2_unit;

    @JsonKey
    private String value3_name;

    @JsonKey
    private String value3_unit;

    @JsonKey
    private String value4_name;

    @JsonKey
    private String value4_unit;

    public ChannelEntity() {
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sensorType);
        dest.writeInt(channel);
        dest.writeString(channel_name);
        dest.writeString(last_update);
        dest.writeString(value1_name);
        dest.writeString(value1_unit);
        dest.writeString(value2_name);
        dest.writeString(value2_unit);
        dest.writeString(value3_name);
        dest.writeString(value3_unit);
        dest.writeString(value4_name);
        dest.writeString(value4_unit);
    }

    public static final Parcelable.Creator<ChannelEntity> CREATOR
            = new Parcelable.Creator<ChannelEntity>() {
        public ChannelEntity createFromParcel(Parcel in) {
            return new ChannelEntity(in);
        }

        public ChannelEntity[] newArray(int size) {
            return new ChannelEntity[size];
        }
    };

    private ChannelEntity(Parcel in) {
        sensorType = in.readInt();
        channel = in.readInt();
        channel_name = in.readString();
        last_update = in.readString();
        value1_name = in.readString();
        value1_unit = in.readString();
        value2_name = in.readString();
        value2_unit = in.readString();
        value3_name = in.readString();
        value3_unit = in.readString();
        value4_name = in.readString();
        value4_unit = in.readString();
    }

    public int getSensorType() {
        return sensorType;
    }

    public void setSenseorType(int senseorType) {
        this.sensorType = senseorType;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getChannel_name() {
        return channel_name;
    }

    public void setChannel_name(String channel_name) {
        this.channel_name = channel_name;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getValue1_name() {
        return value1_name;
    }

    public void setValue1_name(String value1_name) {
        this.value1_name = value1_name;
    }

    public String getValue1_unit() {
        return value1_unit;
    }

    public void setValue1_unit(String value1_unit) {
        this.value1_unit = value1_unit;
    }

    public String getValue2_name() {
        return value2_name;
    }

    public void setValue2_name(String value2_name) {
        this.value2_name = value2_name;
    }

    public String getValue2_unit() {
        return value2_unit;
    }

    public void setValue2_unit(String value2_unit) {
        this.value2_unit = value2_unit;
    }

    public String getValue3_name() {
        return value3_name;
    }

    public void setValue3_name(String value3_name) {
        this.value3_name = value3_name;
    }

    public String getValue3_unit() {
        return value3_unit;
    }

    public void setValue3_unit(String value3_unit) {
        this.value3_unit = value3_unit;
    }

    public String getValue4_name() {
        return value4_name;
    }

    public void setValue4_name(String value4_name) {
        this.value4_name = value4_name;
    }

    public String getValue4_unit() {
        return value4_unit;
    }

    public void setValue4_unit(String value4_unit) {
        this.value4_unit = value4_unit;
    }
}
