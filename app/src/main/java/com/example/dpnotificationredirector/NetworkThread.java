package com.example.dpnotificationredirector;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkThread extends AsyncTask<Void, Void, Void> {

    private Socket socketToDPApp;
    public String ipAddress = "192.168.17.72";
    public int port = 9002;

    public void changeConnectionParameters(String address, int port) {
        ipAddress = address;
        this.port = port;
        Log.d("DP_NetworkThread", "changeConnectionParameters: entered parameters, ip:" + ipAddress + " port:" + port);
        buildUpSocketConnection();
    }

    public Intent sendMessage(final String msg, final int msgId, boolean isAddedOrRemoved, String appName) {
        String sendString = msgId + ";" + msg + ";" + appName + ";" + isAddedOrRemoved;
        Intent intent = new  Intent("com.example.dpnotificationredirector.NOTIFICATION_LISTENER");
        intent.putExtra("Notification Code", sendString);
        Log.d("DP_NetworkThread", "sendMessage: processing notification (sending network message):\n" + sendString);

        try {
            OutputStream out = socketToDPApp.getOutputStream();

            PrintWriter output = new PrintWriter(out);

            output.println(sendString);
            output.flush();

            output.close();
            out.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return intent;
    }

    private void buildUpSocketConnection() {
        try {
            socketToDPApp = new Socket(ipAddress, port);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("DP_NetworkThread", "run: starting network thread");
        buildUpSocketConnection();
        Log.d("DP_NetworkThread", "run: built up connection");
        return null;
    }
}
