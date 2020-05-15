package com.dzfd.gids.baselibs.LAN;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;

import com.dzfd.gids.baselibs.LAN.bean.NetWorkInfoItem;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by zheng on 2019/2/19.
 */

public class LocalHost {

    public interface ScanLocalMacListener{
         void OnFire(byte[] ip,byte[] mac);
    }

    public static void ScanMac(Context context,ScanLocalMacListener callback) {
            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        continue;
                    }
                    Enumeration<InetAddress> ias = nif.getInetAddresses();
                    byte[] byteip=null;
                    while (ias.hasMoreElements()) {
                       InetAddress ia = ias.nextElement();
                        if (ia instanceof Inet6Address) {
                            continue;// skip ipv6
                        }
                        String ip = ia.getHostAddress();
                        // 过滤掉127段的ip地址
                        if (!"127.0.0.1".equals(ip)) {
                            byteip=ia.getAddress();
                            break;
                        }
                    }
                    if(callback !=null){
                        if(macBytes!=null && byteip !=null){
                            callback.OnFire(byteip,macBytes);
                        }
                    }


                }
            } catch (Exception ex) {
            }
    }
    /**
     * 获取本地mac地址
     *
     * @param context
     * @return
     * type =eth0/wlan0 ，无线还是有线
     */
    public static String getLocalMacAddress(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            return info.getMacAddress();
        } else {
            try {
                List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface nif : all) {

                    if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                    byte[] macBytes = nif.getHardwareAddress();
                    if (macBytes == null) {
                        return "";
                    }

                    StringBuilder sb = new StringBuilder();
                    for (byte b : macBytes) {
                        sb.append(String.format("%02X:", b));
                    }

                    if (sb.length() > 0) {
                        sb.deleteCharAt(sb.length() - 1);
                    }
                    return sb.toString();
                }
            } catch (Exception ex) {
            }
        }
        return "02:00:00:00:00:00";
    }

    public static String getLocalIPAddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (enumeration.hasMoreElements()) {
                NetworkInterface networks = enumeration.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> addresses = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && (address instanceof Inet4Address)) {
                        ip = address.getHostAddress();
                    }
                }
            }

        } catch (Exception e) {
            ip = null;
        }
        return ip;
    }
    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            // 遍历所用的网络接口
            while (enumeration.hasMoreElements()) {
                NetworkInterface networks = enumeration.nextElement();
                // 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> addresses = networks.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && (address instanceof Inet4Address)) {
                        return address;
                    }
                }
            }

        } catch (Exception e) {
            ip = null;
        }
        return ip;
    }
    /**
     * 获取本机IP前缀
     *
     * @param address
     * @return
     */
    public static String getNetworkSegment(String address) {
        if (!"".equals(address)) {
            return address.substring(0, address.lastIndexOf(".") + 1);
        }
        return null;
    }
    //get wifi info
    public static String  getMyWifiInfo(Context context){
        String str = "";
        WifiManager mWifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifi.isWifiEnabled()) {
//             List<ScanResult> scanResults = mWifi.getScanResults();  //getScanResults() 扫描到的当前设备的WiFi列表
            WifiInfo wifiInfo = mWifi.getConnectionInfo();
            String netName = wifiInfo.getSSID(); //获取被连接网络的名称
            String netMac =  wifiInfo.getBSSID(); //获取被连接网络的mac地址
            String localMac = wifiInfo.getMacAddress();// 获得本机的MAC地址
            int loalIP = wifiInfo.getIpAddress();
            int level = wifiInfo.getRssi();
            wifiInfo.getLinkSpeed();
            str = wifiInfo.toString();
        }
        return  str;
    }

    private static boolean isWifiAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected() && mWifi.isAvailable();
    }

    public static String intToIp(int i) {

        return ((i >> 24) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                (i & 0xFF);
    }
    public static String byteToMac(byte[] macBytes){
        StringBuilder sb = new StringBuilder();
        for (byte b : macBytes) {
            sb.append(String.format("%02X:", b));
        }

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static String byteToDotIP(byte[] ip) {
        if (ip == null || ip.length != 4) {
            return "";
        }
        return ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
    }

    public static Integer byteToIP(byte[] ip) {
        if (ip == null || ip.length != 4) {
            return 0;
        }

        int frist=ip[0]&0XFF;
        frist=(frist << 24);
        int second=ip[1]&0XFF;
        second=(second << 16);
        int three=ip[2]&0XFF;
        three=(three << 8);
        return frist +  second +  three+ ip[3];
    }
    public static Integer StringToIP(String strip) {
        if(TextUtils.isEmpty(strip)){
            return 0;
        }
        String[] parts = strip.split("\\.");
        if(parts==null || parts.length!=4){
            return 0;
        }

        int[] ip =new int[4];
        for(int index=0;index<4;index++){
           Integer value= Integer.valueOf(parts[index]);
            ip[index]=value;
        }
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }
    public static byte[] MacStringToByte(String maxstr){
        if(TextUtils.isEmpty(maxstr)){
            return new byte[6];
        }
        String[] parts = maxstr.split(":");
        if(parts==null || parts.length!=6){
            return new byte[6];
        }
        byte[] retmac=new byte[6];
        int index=0;
        for(String part:parts){
            int temp=Integer.valueOf(part,16);
            retmac[index]=(byte)temp;
            index++;
        }
        return retmac;
    }


    public static byte[] getSubNetMask(String addr) {
        String[] parts = addr.split("/");
        String ip = parts[0];
        int prefix;
        if (parts.length < 2) {
            prefix = 0;
        } else {
            prefix = Integer.parseInt(parts[1]);
        }
        int mask = 0xffffffff << (32 - prefix);
        System.out.println("Prefix=" + prefix);
        System.out.println("Address=" + ip);

        int value = mask;
        byte[] bytes = new byte[]{
                (byte) (value >>> 24), (byte) (value >> 16 & 0xff), (byte) (value >> 8 & 0xff), (byte) (value & 0xff)};

        return bytes;
    }
    public static Integer getSubNetMask(int prefixlen) {

        if(prefixlen<=0 || prefixlen>=32){
            return 0;
        }
        int prefix=prefixlen;

        int mask = 0xffffffff << (32 - prefix);
        return mask;
    }
    public static List<NetWorkInfoItem> getWifiNetWorkInfo(Context cxt){
        return getNetWorkInfo(cxt,NetWorkInfoItem.NET_TYPE_WIFI);
    }
    public static List<NetWorkInfoItem> getEtherNetWorkInfo(Context cxt){
        return getNetWorkInfo(cxt,NetWorkInfoItem.NET_TYPE_ETHRNET);

    }
    public static List<NetWorkInfoItem> getNetWorkInfo(Context cxt, int nettype){
        List<String> interfaceName=new ArrayList<>();
        List<NetWorkInfoItem> retlist=new ArrayList<>();

        if(nettype == NetWorkInfoItem.NET_TYPE_NONET){
            return retlist;
        }else if(nettype == NetWorkInfoItem.NET_TYPE_WIFI){
            interfaceName=LocalNetWorkInfo.getWiFiInterface();
        }else if(nettype == NetWorkInfoItem.NET_TYPE_ETHRNET){
            interfaceName=LocalNetWorkInfo.getEthernetInterface();
        }
        if(interfaceName.size()<=0){
            return retlist;
        }
        for(String index:interfaceName){
            byte[] biip=LocalNetWorkInfo.getIpAddress(index);
            Integer IP=byteToIP(biip);
            String mask=LocalNetWorkInfo.getLocalMask(index);
            Integer MASK=StringToIP(mask);
            String geteway=LocalNetWorkInfo.getLocalGATE(index);
            Integer GETEWAY=StringToIP(geteway);
            String dns1=LocalNetWorkInfo.getFirstDns(index);
            Integer DNS1 =StringToIP(dns1);
            String dns2=LocalNetWorkInfo.getSecondDns(index);
            Integer DNS2 =StringToIP(dns2);
            NetWorkInfoItem retitem= new NetWorkInfoItem();
            retitem.nettype=nettype;
            retitem.dns.add(DNS1);
            retitem.dns.add(DNS2);
            retitem.router.add(GETEWAY);
            retitem.netmask=MASK;
            retitem.ip=IP;
            retlist.add(retitem);
        }
      return   retlist;
    }

    public static List<NetWorkInfoItem> getLocalNetWork(Context context) {
        List<NetWorkInfoItem> retlist = new ArrayList<>();
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            NetworkInfo info = connManager.getActiveNetworkInfo();
            if(info == null){
                return retlist;
            }
            int nettype=info.getType();
            if(nettype==  ConnectivityManager.TYPE_WIFI){
                WifiManager wifiManager= (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                DhcpInfo dhcp=wifiManager.getDhcpInfo();
                NetWorkInfoItem retitem=NetWorkInfoItem.FromDhCp(dhcp);
                if(retitem != null){
                    retlist.add(retitem);
                }

            }else if (nettype == ConnectivityManager.TYPE_ETHERNET){
                retlist=getEtherNetWorkInfo(context);
            }

        } else {
            Network[] networks = connManager.getAllNetworks();
            if (networks.length <= 0) {
                return null;
            }
            for (Network networkitem : networks) {
                NetWorkInfoItem retitem=new NetWorkInfoItem();
                LinkProperties poper = connManager.getLinkProperties(networkitem);
                if (poper == null) {
                    continue;
                }
                String DOMAINS = poper.getDomains();
                List<Integer> DNSLIST = new ArrayList<>();
                List<InetAddress> dnslist = poper.getDnsServers();
                for (InetAddress ipitem : dnslist) {
                    if (!ipitem.isLoopbackAddress() && (ipitem instanceof Inet4Address)) {
                        byte[] byteip = ipitem.getAddress();
                        Integer intip = byteToIP(byteip);
                        if (intip!=0) {
                            DNSLIST.add(intip);
                        }
                    }
                }
                int IP=0,MASK=0;
                List<LinkAddress> iplist=poper.getLinkAddresses();
                for(LinkAddress ipitem:iplist) {
                    InetAddress inetad = ipitem.getAddress();
                    if (!inetad.isLoopbackAddress() && (inetad instanceof Inet4Address)) {
                        byte[] bytes = inetad.getAddress();
                        IP = byteToIP(bytes);
                        MASK=getSubNetMask(ipitem.getPrefixLength());
                    }
                }
                List<Integer> ROUTERLIST=new ArrayList<>();
                List<RouteInfo> rtlist=poper.getRoutes();
                for(RouteInfo rtitem:rtlist) {
                    InetAddress ipaddr = rtitem.getGateway();
                    if (!ipaddr.isLoopbackAddress() && (ipaddr instanceof Inet4Address)) {
                        byte[] byteip=ipaddr.getAddress();
                        Integer intip=byteToIP(byteip);
                        if(intip ==0){
                            continue;
                        }
                        ROUTERLIST.add(intip);
                    }
                }
                retitem.domain=DOMAINS;
                retitem.dns=DNSLIST;
                retitem.ip=IP;
                retitem.netmask=MASK;
                retitem.router=ROUTERLIST;
                retlist.add(retitem);
            }
        }
        if(retlist.size()>0){
            final List<NetWorkInfoItem> finalRetlist = retlist;
            ScanMac(context, new ScanLocalMacListener() {
                @Override
                public void OnFire(byte[] ip, byte[] mac) {
                    Integer intip=byteToIP(ip);
                    for(NetWorkInfoItem item: finalRetlist){
                        if(item.ip == intip){
                            item.mac=mac;
                            break;
                        }
                    }

                }
            });
        }//updata mac address
        return retlist;

    }
}
