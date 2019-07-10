package com.cs.smartphonemethod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

        // List View의 항목을 Click 했을 때의 Event 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 하위 Activity에 해당하는 Intent를 만들고,
                // sensorIndex라는 이름으로 선택한 항목의 Index를 저장하고 화면에 출력
                Intent intent = new Intent(SensorListActivity.this, SensorDataActivity.class);
                intent.putExtra("sensorIndex", i);
                startActivity(intent);
            }
        });
    }
}
