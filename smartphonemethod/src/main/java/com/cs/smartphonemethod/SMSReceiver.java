package com.cs.smartphonemethod;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class SMSReceiver extends BroadcastReceiver {

    // Event가 발생했을 때 호출되는 Method
    @Override
    public void onReceive(Context context, Intent intent) {
        // 문자 Message의 Data를 가져오기
        Bundle bundle = intent.getExtras();
        Object [] pdus = (Object []) bundle.get("pdus");
        SmsMessage [] messages = new SmsMessage[pdus.length];
        for(int i=0 ; i<pdus.length ; i=i+1){
            messages [i] = SmsMessage.createFromPdu((byte[])pdus[i]);
            try{
                String message = new String(messages[i].getMessageBody());
                String phonenumber = messages[i].getOriginatingAddress();

                // Notification 생성
                NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder builder = null;

                // 운영체제 별로 다르게 Builder 생성
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    String channelId = "one-channel";
                    String channelName = "My Channel One";
                    String channelDescrition = "My Channel One Description";

                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription(channelDescrition);

                    manager.createNotificationChannel(channel);
                    builder = new NotificationCompat.Builder(context, channelId);

                }else {
                    builder = new NotificationCompat.Builder(context);
                }
                Notification noti = builder.setSmallIcon(
                        android.R.drawable.ic_notification_overlay)
                        .setContentTitle("New Message")
                        .setContentText(message)
                        .setAutoCancel(true)
                        .build();
                manager.notify(111, noti);

            }catch (Exception e){
                Log.e("Message Sending Exception", e.getMessage());
            }
        }
    }
}
