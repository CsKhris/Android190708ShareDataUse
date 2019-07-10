package com.cs.smartphonemethod;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import java.util.List;

public class SensorDataActivity extends AppCompatActivity {

    TextView sensorName, sensorAc, sensorValue;

    // Sensor 목록을 가져오기 위한 변수
    SensorManager manager;
    List<Sensor> list;

    // Sensor 이름을 저장할 변수
    String sName;

    // 상위 Activity에서 넘겨받을 Sensor 번호를 저장할 변수
    int sensorIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_data);

        sensorName = (TextView)findViewById(R.id.sensorname);
        sensorAc = (TextView)findViewById(R.id.sensorac);
        sensorValue = (TextView)findViewById(R.id.sensorvalue);

        // Sensor 목록 가져오기
        // Android 내장 객체들을 만들 때 자주 이용 합니다.
        manager = (SensorManager)getSystemService(SENSOR_SERVICE);
        list = manager.getSensorList(Sensor.TYPE_ALL);

        // 상위 Activity Class에서 넘겨준 Data를 가지고 Sensor 이름을 찾아와서 Text View에 출력하기
        Intent intent = getIntent();
        sensorIndex = intent.getIntExtra("sensorIndex", -1);
        Sensor sensor = list.get(sensorIndex);
        sName = sensor.getName();
        sensorName.setText(sName);
    }

    // Sensor의 값이 변경되거나 정밀도가 변경되는 Event를 처리하는 Event Handler
    // 외부에서 만든 이유는 등록과 해체를 하기 위해서 입니다.
    SensorEventListener eventHandler = new SensorEventListener() {

        // Sensor의 값이 변경된 경우 호출되는 Method
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            String msg = "측정 시간 : " + sensorEvent.timestamp + "\n";

            // Sensor의 값 출력
            for(int idx=0 ; idx<sensorEvent.values.length ; idx=idx+1){
                msg += (idx+1) + ":" + sensorEvent.values[idx] + "\n";
            }
            sensorValue.setText(msg);
        }

        // Sensor의 정밀도가 변경된 경우 호출되는 Method
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            // 정밀도에 따른 문자열 출력
            switch (i){

                    case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    sensorAc.setText("Accuracy is High");
                    break;

                    case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    sensorAc.setText("Accuracy is Medium");
                    break;

                    case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    sensorAc.setText("Accuracy is Low");
                    break;

                    case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    sensorAc.setText("Accuracy UnReliable");
                    break;
            }
        }
    };

    // Activity가 화면에 출력될 때 호출되는 Method
    @Override
    public void onResume(){
        super.onResume();
        // Sensor Listener 등록
        manager.registerListener(eventHandler, list.get(sensorIndex), SensorManager.SENSOR_DELAY_UI);
    }

    // Activity가 화면에서 제거될 때 호출되는 Method
    @Override
    public void onPause(){
        super.onPause();
        manager.unregisterListener(eventHandler);
    }

    // 화면 Touch 처리 Method
    @Override
    public boolean onTouchEvent(MotionEvent event){
        finish();
        return super.onTouchEvent(event);
    }
}
