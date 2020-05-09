package com.example.dpnotificationredirector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class NotificationListener extends NotificationListenerService {

    NetworkThread networkThread;

    @Override
    public void onCreate() {
        super.onCreate();
        networkThread = new NetworkThread();
        new Thread(networkThread);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        sendBroadcast(networkThread.sendMessage(sbn.getNotification().toString(), sbn.getId()));
        Log.d("DPNotifReaderService", "onNotificationPosted: A notification got posted");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        sendBroadcast(networkThread.sendMessage(sbn.getNotification().toString(), sbn.getId()));
        Log.d("DPNotifReaderService", "onNotificationRemoved: A notification got removed");
    }

    public class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedNotification = intent.getStringExtra("Notification Code");
            if (receivedNotification != null) {
                networkThread.changeConnectionParameters(receivedNotification);
            }
            Log.d("DPNotifReaderService", "onReceive: received new connection properties:" + receivedNotification);
        }
    }
}
