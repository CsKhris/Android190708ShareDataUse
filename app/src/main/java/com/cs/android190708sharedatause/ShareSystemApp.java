package com.cs.android190708sharedatause;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ShareSystemApp extends AppCompatActivity {

    Button contactBtn, imageBtn, gallaryBtn;
    LinearLayout content;

    // 화면 전체 크기의 너비와 높이를 저장할 변수
    int reqWidth;
    int reqHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_system_app);

        contactBtn = (Button)findViewById(R.id.contactbtn);
        imageBtn = (Button)findViewById(R.id.imagebtn);
        gallaryBtn = (Button)findViewById(R.id.gallarybtn);
        content = (LinearLayout)findViewById(R.id.content);

        // 현재 Device의 전체 화면 크기 가져오기
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        reqWidth = metrics.widthPixels;
        reqHeight = metrics.heightPixels;

        // 동적으로 권한 요청하기 - 연락처 접근을 위한 권한 확인
        if(ContextCompat.checkSelfPermission(ShareSystemApp.this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ShareSystemApp.this,
                    new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }

        // 외부 저장 장치 읽기 권한 확인
        if(ContextCompat.checkSelfPermission(
                ShareSystemApp.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                        ShareSystemApp.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(
                    ShareSystemApp.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
        }

        // 연락처 Button을 눌러서 연락처 화면을 출력하도록 작성
        contactBtn.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                // 연락처 Intent 생성
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(Uri.parse("content://com.android.contacts/data/phones"));

                // Intent를 출력하고 10번으로 구분하여, Data를 넘겨받을 수 있도록 Intent를 출력
                startActivityForResult(intent, 10);
            }
        });

        // Gallery Button을 누르면 동작하는 Code
        gallaryBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                // Gallery App을 화면에 출력
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 30);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 연락처에서 Intent가 닫혔을 때
        if (requestCode == 10){
            // 선택한 Data의 ID 찾아오기
            String id = Uri.parse(data.getDataString()).getLastPathSegment();

            // Data 가져오기
            Cursor cursor = getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.Data._ID+"="+id, null, null);

            cursor.moveToNext();
            String name = cursor.getString(0);
            String phone = cursor.getString(1);

            // Text View를 동적으로 생성하여 Content 에 추가
            TextView textView = new TextView(ShareSystemApp.this);
            textView.setText(name + ":" + phone);
            textView.setTextSize(30);

            // 크기 설정
            LinearLayout.LayoutParams params = (new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));

            content.addView(textView);
        }
        else if(requestCode == 30 && requestCode == RESULT_OK){
            Log.e("Button Click Result", "Message");
            // 선택한 Data가 있다면
            if(data.getClipData() != null){
                Log.e("Data", data.getClipData().toString());
                ClipData clipData = data.getClipData();
                for(int i=0 ; i<clipData.getItemCount() ; i=i+1){
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    String filePath = getFilePathFromDocumentUri(ShareSystemApp.this, uri);
                    Log.e("File Path", filePath);
                    if(filePath != null){
                        insertImage(filePath);
                    }
                }
            }
        }
    }

    // Image File의 경로를 주면 Image를 읽어서 Image View에 출력해주는 Method
    private void insertImage(String filePath){
        if(filePath.equals("") == false){
            // File 경로를 가지고 File 객체 생성
            File file = new File(filePath);
            // Image Option 객체 생성
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try{
                InputStream is = new FileInputStream(file);
                BitmapFactory.decodeStream(is, null, options);
                is.close();
                is = null;
            }catch (Exception e){
                Log.e("Image Decoding Fail", e.getMessage());
            }

            // Image Size의 설정
            final int width = options.outWidth;
            int inSampleSize = 1;
            if(width > reqWidth){
                int widthRation = Math.round((float)width/(float)reqWidth);
                inSampleSize = widthRation;
            }

            // 가져올 Image의 Option을 설정할 Option을 생성하고 설정
            BitmapFactory.Options imageOptions = new BitmapFactory.Options();
            imageOptions.inSampleSize = inSampleSize;
            // Image 읽어오기
            Bitmap bitmap = BitmapFactory.decodeFile(filePath, imageOptions);
            // Image View를 생성하여 추가하기
            ImageView imageView = new ImageView(ShareSystemApp.this);
            imageView.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            content.addView(imageView);
        }
    }

    // 선택한 Image File의 URI를 가지고, Image File의 경로를 문자열로 Return해 주는 Method
    // (4.4(Kitkat) Version 이상에서 사용)
    private String getFilePathFromDocumentUri(Context context, Uri uri){
        // 선택한 Image의 ID 찾기
        String docId = DocumentsContract.getDocumentId(uri);

        // Image File의 ID는 image:id 로 구성
        // : 을 기준으로 분할 합니다.
        String[] split = docId.split(":");
        String type = split[0];
        Uri contentUri = null;

        // '상수'와 '변수'를 비교할 때 '상수'를 기준으로 비교하는 것이 좋습니다.
        // '변수'를 기준으로 비교할 경우 NullPointerException이 발생할 수 있지만,
        // '상수'를 기준으로 비교할 경우 NullPointerException이 발생하지 않습니다.
        // (되도록이면 '왼쪽'에 '상수'를 두고 비교하는 것이 좋습니다.)
        if("image".equals(type)){
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        // Image File의 경로 만들기 작업
        String selection = MediaStore.Images.Media._ID+"=?";
        String [] selectionArg = new String[]{split[1]};
        String column = "_data";
        String [] projection = {column};

        // ContentProvider에서 Data 가져오기
        Cursor cursor = context.getContentResolver().query(contentUri, projection, selection, selectionArg, null);
        String filePath = null;
        if(cursor != null && cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(column);
            filePath = cursor.getString(column_index);
        }
        cursor.close();
        return filePath;
    }
}
