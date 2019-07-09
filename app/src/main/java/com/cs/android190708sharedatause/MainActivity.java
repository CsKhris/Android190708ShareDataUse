package com.cs.android190708sharedatause;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    EditText editName, editPhone;
    Button upsert;

    // 읽어온 Data를 저장할 ArrayList
    ArrayList<Map<String, Object>> list;

    // 공유 Data의 URI를 저장할 변수
    Uri uri;

    // 수정 Mode인지 저장할 변수
    Boolean isUpdate;
    // 선택한 Cell의 ID를 저장할 변수
    String _id;

    // 공유 Data를 읽어서 ListView에 출력하는 Method
    private void setAdapter(){
        // Data를 저장할 Instance 생성
        list = new ArrayList<>();

        // Data 가져오기
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        // Data를 읽어서 저장하기
        while (cursor.moveToNext()){
            HashMap<String, Object> map = new HashMap<>();
            map.put("id", cursor.getString(0));
            map.put("name", cursor.getString(1));
            map.put("phone", cursor.getString(2));
            list.add(map);
        }

        // Data 출력
        SimpleAdapter adapter = new SimpleAdapter(
                MainActivity.this, list,
                android.R.layout.simple_list_item_2,
                new String[]{"name", "phone"},
                new int[]{android.R.id.text1, android.R.id.text2});
        listView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // View 찾아오기
        listView = (ListView) findViewById(R.id.listview);
        editName = (EditText) findViewById(R.id.editname);
        editPhone = (EditText) findViewById(R.id.editphone);
        upsert = (Button) findViewById(R.id.upsort);

        // Content Provider의 URI 생성
        uri = uri.parse("content://com.example.part.Provider");

        // Data를 읽어와서 출력하는 Method 호출
        setAdapter();

        // ListView의 Item을 길게 누르면 호출되는 EventHandler
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(
                    AdapterView<?> adapterView, View view, int i, long l) {
                // 선택한 Data 가져오기
                Map<String, Object> map = list.get(i);
                // 공유 Data 영역에서 삭제
                // URI 영역에서 _id가 선택한 Data의 ID인 것을 삭제
                getContentResolver().delete(uri, "_id=?", new String[]{map.get("id").toString()});

                // Data를 다시 출력
                setAdapter();
                return false;
            }
        });

        // ListView의 Cell을 Click 했을 때 Event 처리
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 선택한 Data 찾아오기
                Map<String, Object> map = list.get(i);

                // EditText에 출력하기
                editName.setText(map.get("name").toString());
                editPhone.setText(map.get("phone").toString());
                _id = map.get("id").toString();

                // 수정 Mode 변경
                isUpdate = true;
            }
        });

        // isUpdate 여부에 따라 Data를 삽입하거나 수정하는 Event Handler
        upsert.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String name = editName.getText().toString();
                String phone = editPhone.getText().toString();
                if(isUpdate == true){
                    ContentValues values = new ContentValues();
                    values.put("name", name);
                    values.put("phone", phone);
                    getContentResolver().update(uri, values, "_id=?", new String[]{_id});
                    isUpdate = false;
                    setAdapter();
                    editName.setText("");
                    editPhone.setText("");
                }else {
                    ContentValues values = new ContentValues();
                    values.put("name", name);
                    values.put("phone", phone);
                    getContentResolver().insert(uri, values);
                    setAdapter();
                    editName.setText("");
                    editPhone.setText("");
                }
            }
        });
    }
}
