package com.example.dpnotificationredirector;

import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkThread implements Runnable {

    private Socket socketToDPApp;
    public String ipAddress = "192.168.17.72";
    public int port = 9002;

    @Override
    public void run() {
        buildUpSocketConnection();
    }

    public void changeConnectionParameters(String connectionString) {
        ipAddress = connectionString.split(":")[0];
        port = Integer.parseInt(connectionString.split(":")[1]);
        Log.d("DPNotifReaderService", "changeConnectionParameters: entered parameters, ip:" + ipAddress + " port:" + port);
        buildUpSocketConnection();
    }

    public Intent sendMessage(final String msg, final int msgId) {
        Intent intent = new  Intent("com.example.dpnotificationredirector");
        intent.putExtra("Notification Code", "[" + msgId + "] " + msg);
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
        return intent;
    }

    private void buildUpSocketConnection() {
        try {
            socketToDPApp = new Socket(ipAddress, port);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
