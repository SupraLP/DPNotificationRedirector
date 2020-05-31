package com.example.dpnotificationredirector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.StrictMode;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

public class NotificationListener extends NotificationListenerService {

    NetworkThread networkThread;

    private NotificationListenerBroadcastReceiver nlReceiver;

    @Override
    public void onCreate() {
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        nlReceiver = new NotificationListenerBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.dpnotificationredirector.NOTIFICATION_LISTENER_SERVICE");
        registerReceiver(nlReceiver,filter);

        Log.d("DP_Service", "onCreate: created NotificationListener");
        networkThread = new NetworkThread();
        networkThread.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        sendBroadcast(networkThread.sendMessage(sbn.getNotification().contentView.toString(), sbn.getId(), true, sbn.getPackageName()));
        // sbn.getNotification().contentView would return the notification contents as view...
        // that would need to be converted into a transferable format... or needs to be interpreted on the receiving end...
        Log.d("DP_Service", "onNotificationPosted: A notification got posted");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        sendBroadcast(networkThread.sendMessage("", sbn.getId(), false, sbn.getPackageName()));
        Log.d("DP_Service", "onNotificationRemoved: A notification got removed");
    }

    public class NotificationListenerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String address = intent.getStringExtra("address");
            String port = intent.getStringExtra("port");
            if (address != null && port != null) {
                networkThread.changeConnectionParameters(address, Integer.parseInt(port));
            }
            Log.d("DP_Service", "onReceive: received new connection properties:" + address + ":" + port);
        }
    }
}
