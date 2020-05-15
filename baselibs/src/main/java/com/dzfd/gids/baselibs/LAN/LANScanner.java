package com.dzfd.gids.baselibs.LAN;


import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LANScanner {

    /**
     * 核心池大小
     **/
    private static final int CORE_POOL_SIZE = 1;
    /**
     * 线程池最大线程数
     **/
    private static final int MAX_POOL_SIZE = 255;
    private String mLocalIpAddress; // 本机IP地址
    private String mNetworkSegment; // 局域网IP地址头,如:192.168.1.
    private static final String PING = "/system/bin/ping -c 1 -w 3 %s";// 其中 -c 1为发送的次数,-w 表示发送后等待响应的时间
    private ThreadPoolExecutor mExecutor;// 线程池对象


    private LANScanner() {
    }

    public static LANScanner get() {
        return new LANScanner();
    }
    public void Asynscan(final OnScanListener listener){
        Thread adthread = new Thread(new Runnable(){

            @Override
                public void run() {
                if(listener!=null){
                    listener.onBeginLoading();
                }
                scan(listener);
            }
        });
        adthread.start();
    }


    public void scan(final OnScanListener listener) {
        this.mLocalIpAddress = LocalHost.getLocalIPAddress();
        this.mNetworkSegment = LocalHost.getNetworkSegment(this.mLocalIpAddress);
        if (TextUtils.isEmpty(this.mLocalIpAddress)) {
            return;
        }

        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, 2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(CORE_POOL_SIZE));
        String ip;
        for (int i = 1; i < 255; i++) {
            ip = mNetworkSegment.concat(Integer.toString(i));
            if (this.mLocalIpAddress.equals(ip)) continue;

            mExecutor.execute(new PingThread(ip, listener));
        }

        mExecutor.shutdown();

        try {
            while (true) {
                if (mExecutor.isTerminated()) {
                    if (listener != null) {
                        listener.onFinished();
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mExecutor != null) {
            mExecutor.shutdownNow();
        }
    }
    public void AsynUdpScan(final OnScanListener listener) {
        //局域网内存在的ip集合
        final List<String> ipList = new ArrayList<>();
        final Map<String, String> map = new HashMap<>();

        //获取本机所在的局域网地址
        this.mLocalIpAddress = LocalHost.getLocalIPAddress();
        this.mNetworkSegment = LocalHost.getNetworkSegment(this.mLocalIpAddress);

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(listener!=null){
                    listener.onBeginLoading();
                }
                DatagramPacket dp = new DatagramPacket(new byte[0], 0, 0);
                DatagramSocket socket=null;
                try {
                    socket = new DatagramSocket();
                    int position = 2;
                    while (position < 255) {
                        dp.setAddress(InetAddress.getByName(mNetworkSegment + String.valueOf(position)));
                        socket.send(dp);
                        position++;
                        if (position == 125) {//分两段掉包，一次性发的话，达到236左右，会耗时3秒左右再往下发
                            socket.close();
                            socket = new DatagramSocket();
                        }
                    }

                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(socket!=null && !socket.isClosed()){
                        socket.close();
                    }
                    execCatForArp(listener);
                }
            }
        }).start();
    }
    /**
     * 执行 cat命令 查找android 设备arp表
     * arp表 包含ip地址和对应的mac地址
     */
    private void execCatForArp(final OnScanListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Map<String, String> map = new HashMap<>();
                    Process exec = Runtime.getRuntime().exec("cat proc/net/arp");
                    InputStream is = exec.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Log.e("kalshen", "run: " + line);
                        if (!line.contains("00:00:00:00:00:00")&&!line.contains("IP")) {
                            String[] split = line.split("\\s+");
                            Device device = new Device(split[0],split[3],"");
                            if(listener!=null){
                                listener.onFound(device);
                            }

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if(listener!=null){
                        listener.onFinished();
                    }
                }
            }
        }).start();
    }




    public static String getHostIPAddress() {
        String host = null;
        try {
            Enumeration enumeration = NetworkInterface.getNetworkInterfaces();
            InetAddress address = null;
            while (enumeration.hasMoreElements()) {
                NetworkInterface networks = (NetworkInterface) enumeration.nextElement();
                Enumeration<InetAddress> addresses = networks.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    address = addresses.nextElement();
                    if (address instanceof Inet6Address) {
                        continue;// skip ipv6
                    }
                    String ip = address.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        host = address.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
        }
        return host;
    }





    private static String readMacFromArp(final String ip) {
        if (null == ip) return null;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            String mac;
            while ((line = reader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4 && ip.equals(splitted[0])) {
                    // Basic sanity checkcd
                    mac = splitted[3];
                    if (mac.matches("..:..:..:..:..:..")) {
                        return mac;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static class UDPThread implements Runnable {
        private final String mTargetIp;
        private final OnScanListener mListener;

        private static final byte[] BUF = {(byte) 0x82, (byte) 0x28, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x1,
                (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x20, (byte) 0x43, (byte) 0x4B,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x0, (byte) 0x0, (byte) 0x21, (byte) 0x0, (byte) 0x1};

        private static final short PORT = 137;

        public UDPThread(final String ip, final OnScanListener listener) {
            this.mTargetIp = ip;
            this.mListener = listener;
        }

        @Override
        public synchronized void run() {
            if (mTargetIp == null || "".equals(mTargetIp)) return;

            DatagramSocket socket = null;
            InetAddress address = null;
            DatagramPacket packet = null;
            try {
                address = InetAddress.getByName(mTargetIp);
                packet = new DatagramPacket(BUF, BUF.length, address, PORT);
                socket = new DatagramSocket();
                socket.setSoTimeout(200);
                socket.send(packet);
                socket.close();
                if (mListener != null) {
                    mListener.onFound(new Device(mTargetIp, readMacFromArp(mTargetIp), address.getCanonicalHostName()));
                }
            } catch (SocketException se) {
                se.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }

    public static class PingThread implements Runnable {

        private final String mTargetIp;
        private final OnScanListener mListener;

        public PingThread(final String ip, final OnScanListener listener) {
            this.mTargetIp = ip;
            this.mListener = listener;
        }

        @Override
        public void run() {
            Process exec = null;
            try {
                exec = Runtime.getRuntime().exec(String.format(PING, mTargetIp));
                int result = exec.waitFor();
                if (0 == result) {
                    InetAddress address = InetAddress.getByName(mTargetIp);
                    if (mListener != null) {
                        mListener.onFound(new Device(mTargetIp, readMacFromArp(mTargetIp), address.getCanonicalHostName()));
                    }
                } else {
                    throw new IOException("Unable to get ping from runtime");
                }
            } catch (IOException | InterruptedException e) {
                try {
                    InetAddress address = InetAddress.getByName(mTargetIp);
                    if (address.isReachable(10000)) {
                        if (mListener != null) {
                            mListener.onFound(new Device(mTargetIp, readMacFromArp(mTargetIp), address.getCanonicalHostName()));
                        }
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } finally {
                if (exec != null) {
                    exec.destroy();
                }
            }
        }
    }


}
