package com.example.lifen.serverdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    private MyServer ms;
    public static String ipAddress;
    private TextView ip;

    ToggleButton tb_wifi_switch;
    TextView tv_ap_info;

    WifiApMgr mWifiApMgr;

    String apName = "freeWifi";
    String apPwd = null;

    Toast mToast;
    private EditText wifiName;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ms = new MyServer();
        ms.startAsync();
        ip = (TextView) findViewById(R.id.ip);
        ipAddress = getLocalIpAddress();
        ip.setText("服务器Ip:" + ipAddress);

        tb_wifi_switch = (ToggleButton) findViewById(R.id.tb_wifi_switch);
        tv_ap_info = (TextView) findViewById(R.id.tv_ap_info);
        wifiName = (EditText) findViewById(R.id.wifiName);
        password = (EditText) findViewById(R.id.password);


        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ms.stopAsync();
    }

    /*public String getLocalIpAddress() {
        try {
            String ipv4;
            ArrayList<NetworkInterface> nilist = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ni: nilist)
            {
                ArrayList<InetAddress> ialist = Collections.list(ni.getInetAddresses());
                for (InetAddress address: ialist){
                    if (!address.isLoopbackAddress() && !address.isLinkLocalAddress())
                    {
                        ipv4=address.getHostAddress();
                        return ipv4;
                    }
                }

            }

        } catch (SocketException ex) {
            Log.e("localip", ex.toString());
        }
        return null;
    }*/

    //写法二：
    /**
     * 获取内网ip地址
     * @return
     */
    public static String getLocalIpAddress() {

        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            Log.i("yao", "SocketException");
            e.printStackTrace();
        }
        return hostIp;
    }

    public void onClick(View view) {
        apName = wifiName.getText().toString().trim();
        apPwd = password.getText().toString().trim();
        mWifiApMgr = new WifiApMgr(this,apPwd,apName);

        switch (view.getId()) {
            case R.id.tb_wifi_switch:
                boolean isOn = mWifiApMgr.isApOn();
                boolean isSuccess = mWifiApMgr.setWifiApEnabled(!isOn);
                StringBuilder msg = new StringBuilder();
                msg.append(isOn ? "关闭" : "开启").append("AP").append(isSuccess ? "成功" : "失败");
                mToast.setText(msg);
                mToast.show();
                if (!isOn && isSuccess) {
                    tv_ap_info.setText("热点名称:" + apName + "\n"
                            + "热点密码:" + apPwd + '\n' + "当前IP:" + getLocalIpAddress());
                    tv_ap_info.setVisibility(View.VISIBLE);
                } else {
                    tv_ap_info.setVisibility(View.INVISIBLE);
                }
                break;
        }
        ip.setText("服务器Ip:" + getLocalIpAddress());
    }
}
