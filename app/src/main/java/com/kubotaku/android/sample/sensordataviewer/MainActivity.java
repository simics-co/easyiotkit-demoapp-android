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

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kubotaku.android.sample.sensordataviewer.api.ScalenicsAccessor;
import com.kubotaku.android.sample.sensordataviewer.model.ChannelEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for Application Start view.
 */
public class MainActivity extends AppCompatActivity
        implements ApiTokenSettingDialogFragment.OnDialogDismissListener {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!checkApiToken()) {
            showSetupApiTokenDialog();
        } else {
            getChannelInfo();
        }
    }

    private boolean checkApiToken() {
        String apiToken = AppPreferences.getApiToken(this);
        if (apiToken == null) {
            return false;
        }
        return true;
    }

    private void showSetupApiTokenDialog() {
        ApiTokenSettingDialogFragment fragment = ApiTokenSettingDialogFragment.newInstance();
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onDismiss() {
        if (checkApiToken()) {
            getChannelInfo();
        }
    }

    private void getChannelInfo() {
        this.progressBar.setVisibility(View.VISIBLE);

        String apiToken = AppPreferences.getApiToken(this);
        new GetChannelInfoTask(apiToken).execute();
    }

    private void showChannelInfo(List<ChannelEntity> channelEntities) {
        this.progressBar.setVisibility(View.GONE);

        if (channelEntities == null) {
            return;
        }

        for (ChannelEntity channelEntity : channelEntities) {
            String channel_name = channelEntity.getChannel_name();
            if (channel_name.toUpperCase().contains("TEMPERATURE")) {
                channelEntity.setSenseorType(ChannelEntity.SENSOR_TEMPERATURE);
            } else if (channel_name.toUpperCase().contains("OCCUPANCY")) {
                channelEntity.setSenseorType(ChannelEntity.SENSOR_OCCUPANCY);
            } else if (channel_name.toUpperCase().contains("SWITCH")) {
                channelEntity.setSenseorType(ChannelEntity.SENSOR_ROCKER_SWITCH);
            } else {
                channelEntity.setSenseorType(ChannelEntity.SENSOR_UNKNOWN);
            }
        }

        ListView listView = (ListView) findViewById(R.id.main_list_channels);
        ChannelInfoAdapter adapter = new ChannelInfoAdapter(channelEntities);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(onChannelClickedListener);
    }

    private AdapterView.OnItemClickListener onChannelClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            ChannelEntity item = (ChannelEntity) parent.getAdapter().getItem(position);

            showDataViewActivity(item, view);
        }
    };

    private void showDataViewActivity(ChannelEntity item, View view) {
        Intent intent = SensorDataActivity.createIntent(this, item);

        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                new Pair<View, String>(view.findViewById(R.id.list_image_icon),
                        SensorDataActivity.VIEW_NAME_HEADER_IMAGE));

        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }

    // --------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.menu_set_api_token:
                showSetupApiTokenDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // --------------------------------------

    private class GetChannelInfoTask extends AsyncTask<Void, Void, List<ChannelEntity>> {

        private String apiToken;

        public GetChannelInfoTask(final String apiToken) {
            this.apiToken = apiToken;
        }

        @Override
        protected List<ChannelEntity> doInBackground(Void... params) {
            List<ChannelEntity> channelEntities = new ArrayList<>();

            try {
                ScalenicsAccessor scalenicsAccessor = new ScalenicsAccessor(this.apiToken);

                List<ChannelEntity> channelInfo1 = scalenicsAccessor.getChannelInfo("1");
                if (channelInfo1 != null) {
                    channelEntities.addAll(channelInfo1);
                }

                List<ChannelEntity> channelInfo2 = scalenicsAccessor.getChannelInfo("2");
                if (channelInfo2 != null) {
                    channelEntities.addAll(channelInfo2);
                }

                List<ChannelEntity> channelInfo3 = scalenicsAccessor.getChannelInfo("3");
                if (channelInfo3 != null) {
                    channelEntities.addAll(channelInfo3);
                }

                List<ChannelEntity> channelInfo4 = scalenicsAccessor.getChannelInfo("4");
                if (channelInfo4 != null) {
                    channelEntities.addAll(channelInfo4);
                }

                List<ChannelEntity> channelInfo5 = scalenicsAccessor.getChannelInfo("5");
                if (channelInfo5 != null) {
                    channelEntities.addAll(channelInfo5);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return channelEntities;
        }

        @Override
        protected void onPostExecute(List<ChannelEntity> channelEntities) {
            if ((channelEntities != null) && (channelEntities.size() != 0)) {
                showChannelInfo(channelEntities);
            } else {
                Toast toast = Toast.makeText(MainActivity.this, R.string.msg_fail_get_channel_info, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }

    // --------------------------------------

    private static class ViewHolder {
        ImageView icon;
        TextView channel;
        TextView channelName;
    }

    private class ChannelInfoAdapter extends BaseAdapter {

        private List<ChannelEntity> channelEntities;

        public ChannelInfoAdapter(List<ChannelEntity> channelEntities) {
            this.channelEntities = channelEntities;
        }

        @Override
        public int getCount() {
            int count = 0;
            if (channelEntities != null) {
                count = channelEntities.size();
            }
            return count;
        }

        @Override
        public ChannelEntity getItem(int position) {
            ChannelEntity item = null;
            if ((channelEntities != null) && (position < channelEntities.size())) {
                item = channelEntities.get(position);
            }
            return item;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();

                View view = getLayoutInflater().inflate(R.layout.layout_list_channel_infos, parent, false);
                convertView = view;

                holder.icon = (ImageView) view.findViewById(R.id.list_image_icon);
                holder.channel = (TextView) view.findViewById(R.id.list_text_channel);
                holder.channelName = (TextView) view.findViewById(R.id.list_text_channel_name);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ChannelEntity item = getItem(position);
            if (item != null) {

                int channel = item.getChannel();
                holder.channel.setText("CH : " + channel);

                String channelName = item.getChannel_name();
                holder.channelName.setText(channelName);

                int sensorType = item.getSensorType();
                switch (sensorType) {
                    case ChannelEntity.SENSOR_TEMPERATURE:
                        holder.icon.setImageResource(R.mipmap.ic_temperature);
                        break;

                    case ChannelEntity.SENSOR_OCCUPANCY:
                        holder.icon.setImageResource(R.mipmap.ic_launcher);
                        break;

                    case ChannelEntity.SENSOR_ROCKER_SWITCH:
                        holder.icon.setImageResource(R.mipmap.ic_bell_off);
                        break;

                    default:
                        holder.icon.setImageResource(R.mipmap.ic_launcher);
                        break;
                }
            }

            return convertView;
        }
    }
}
