package com.example.lifen.serverdemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by LiFen on 2018/1/1.
 */

public class MyServer{
    public static ArrayList<Socket> socketList = new ArrayList<>();
    private boolean isEnable;
    private ServerSocket socket;

    /**
     * 启动Server(异步)
     */
    public void startAsync(){
        isEnable = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerSocket ss = null;
                try {
                    ss = new ServerSocket(30000);
                    while (true) {
                        Socket s = ss.accept();
                        socketList.add(s);
                        new Thread(new ServerThread(s)).start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 停止Server（异步）
     */
    public void stopAsync() {
        if(!isEnable){
            return;
        }
        isEnable = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = null;
    }
}