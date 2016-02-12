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

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

import java.util.List;

/**
 * Entity class of Stream data.
 */
@JsonModel
public class StreamEntity {

    @JsonKey
    private int channel;

    @JsonKey
    private String channel_name;

    @JsonKey
    private String last_update;

    @JsonKey
    private List<StreamValueEntity> stream;

    @JsonKey
    private String aggr_type;

    @JsonKey
    private List<StreamValueEntity> result;

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

    public List<StreamValueEntity> getStream() {
        return stream;
    }

    public void setStream(List<StreamValueEntity> stream) {
        this.stream = stream;
    }

    public String getAggr_type() {
        return aggr_type;
    }

    public void setAggr_type(String aggr_type) {
        this.aggr_type = aggr_type;
    }

    public List<StreamValueEntity> getResult() {
        return result;
    }

    public void setResult(List<StreamValueEntity> result) {
        this.result = result;
    }
}
