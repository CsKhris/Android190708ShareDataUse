package com.cs.smartphonemethod;

import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SensorListAdapter extends ArrayAdapter<Sensor> {

    // Instance 변수
    Context context; // View를 전개하기 위해서 필요
    List<Sensor> list; // 출력할 Data
    int viewid;

    public SensorListAdapter(Context context, int viewid, List<Sensor> list){
        super(context, viewid, list);
        this.context = context;
        this.list = list;
        this.viewid = viewid;
    }

    // 출력할 Data의 개수를 설정하는 Method
    // 이 Method가 만들어지면 나머지 Method들은 이 Method에서 Return한 숫자 만큼 반복 수행 합니다.
    @Override
    public int getCount(){
        return list.size();
    }

    // 각 Cell에 ID를 설정하는 Method
    @Override
    public long getItemId(int position){
        return position;
    }

    // 각 Cell의 항목을 설정하는 Method
    @Override
    public Sensor getItem(int position){
        return  list.get(position);
    }

    // 화면에 출력할 View를 만들어주는 Method
    // 첫번째 매개변수는 항목 번호
    // 두번째 매개변수는 재사용 가능한 View
    // 세번째 매개변수는 항목이 출력될 ListView
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View itemView = null;

        // 재사용 가능한 View가 없으면 만들고, 있으면 있는 것을 재사용 합니다.
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(viewid, null);
        }else {
            itemView = convertView;
        }

        // 현재 행번호에 해당하는 Data 찾아오기
        Sensor sensor = list.get(position);

        // Text View 3개를 찾아와서 Data 출력
        TextView txtName = (TextView)itemView.findViewById(R.id.txtname);
        TextView txtVendor = (TextView)itemView.findViewById(R.id.txtvendor);
        TextView txtVersion = (TextView)itemView.findViewById(R.id.txtversion);

        txtName.setText(sensor.getName());
        txtVendor.setText(sensor.getVendor());
        txtVersion.setText(sensor.getVersion()+"");

        return itemView;
    }
}
