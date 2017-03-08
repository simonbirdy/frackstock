package com.vos.myapplication;

import android.os.AsyncTask;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by simonvogel on 04.03.17.
 */
public class SendPacket extends AsyncTask<byte[],Integer,Long> {

    private Exception exception;

    public Long doInBackground(byte[]... bytes) {
        try {
            DatagramSocket ds;
            InetAddress serverAddr;
            DatagramPacket dp;
            ds = new DatagramSocket();
            serverAddr = InetAddress.getByName("192.168.4.1");
            dp = new DatagramPacket(bytes[0], 5, serverAddr, 30000);
            ds.send(dp);
            return (long)0;

        } catch (Exception e) {
            this.exception = e;
            return (long)0;

        }
    }

}