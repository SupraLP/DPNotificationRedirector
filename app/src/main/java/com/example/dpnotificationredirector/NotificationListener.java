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

public class NotificationListener extends NotificationListenerService implements Runnable {

    private Socket socketToDPApp;
    public String ipAddress = "192.168.17.72";
    public int port = 9002;

    @Override
    public void onCreate() {
        super.onCreate();
        buildUpSocketConnection();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        sendMessage(sbn.getNotification().toString(), sbn.getId());
        Log.d("DPNotifReaderService", "onNotificationPosted: A notification got posted");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        sendMessage(sbn.getNotification().toString(), sbn.getId());
        Log.d("DPNotifReaderService", "onNotificationRemoved: A notification got removed");
    }

    @Override
    public void run() {
        buildUpSocketConnection();
    }

    public class NotificationBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String receivedNotification = intent.getStringExtra("Notification Code");
            if (receivedNotification != null) {
                changeConnectionParameters(receivedNotification);
            }
            Log.d("DPNotifReaderService", "onReceive: received new connection properties:" + receivedNotification);
        }
    }

    public void changeConnectionParameters(String connectionString) {
        ipAddress = connectionString.split(":")[0];
        port = Integer.parseInt(connectionString.split(":")[1]);
        Log.d("DPNotifReaderService", "changeConnectionParameters: entered parameters, ip:" + ipAddress + " port:" + port);
        buildUpSocketConnection();
    }

    private void sendMessage(final String msg, final int msgId) {
        Intent intent = new  Intent("com.example.dpnotificationredirector");
        intent.putExtra("Notification Code", "[" + msgId + "] " + msg);
        sendBroadcast(intent);
        Log.d("DPNotifReaderService", "sendMessage: processing notification (sending network message)");

        try {
            OutputStream out = socketToDPApp.getOutputStream();

            PrintWriter output = new PrintWriter(out);

            output.println(msg);
            output.flush();

            output.close();
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private void buildUpSocketConnection() {
        try {
            socketToDPApp = new Socket(ipAddress, port);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
