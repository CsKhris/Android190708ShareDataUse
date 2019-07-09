package com.cs.smartphonemethod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

public class SensorListActivity extends AppCompatActivity {

    ListView listView;
    List<Sensor> list;
    SensorListAdapter adapter;

    SensorManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);

        // Sensor 목록 가져오기
        manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        list = manager.getSensorList(Sensor.TYPE_ALL);

        // 가져온 List로 Adapter 만들기
        adapter = new SensorListAdapter(
                SensorListActivity.this, R.layout.customcell, list);

        // ListView를 만들고 연결하기
        listView = (ListView)findViewById(R.id.sensorlist);
        listView.setAdapter(adapter);
    }
}
