package com.example.lifen.serverdemo;

/**
 * Created by LiFen on 2018/1/1.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;

public class ServerThread implements Runnable {
    Socket s = null;
    BufferedReader br = null;

    public ServerThread(Socket s) throws IOException {
        this.s = s;
        br = new BufferedReader(new InputStreamReader(s.getInputStream(), "utf-8"));
    }

    public void run() {
        try {
            String content = null;
            while ((content = readFromClient()) != null) {
                for (Iterator<Socket> it = MyServer.socketList.iterator(); it.hasNext();) {
                    Socket s = it.next();
                    try {
                        OutputStream os = s.getOutputStream();
                        os.write((content + "\n").getBytes("utf-8"));
                    } catch (SocketException e) {
                        e.printStackTrace();
                        it.remove();
                        System.out.println(MyServer.socketList);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromClient() {
        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            MyServer.socketList.remove(s);
        }
        return null;
    }
}