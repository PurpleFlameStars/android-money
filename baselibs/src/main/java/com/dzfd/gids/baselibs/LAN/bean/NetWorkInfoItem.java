package com.dzfd.gids.baselibs.LAN.bean;

import android.net.DhcpInfo;

import com.GoLemon.supplier.BunItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zheng on 2019/2/18.
 */

public class NetWorkInfoItem implements BunItem{
    public static int NET_TYPE_NONET=0, NET_TYPE_WIFI=1,NET_TYPE_ETHRNET=2;
    public int ip;
    public  int netmask;
    public  List<Integer> dns;
    public  List<Integer> router;
    public  String domain;
    public byte[] mac;
    public int nettype;
    public NetWorkInfoItem(){
        mac=new byte[6];
        nettype=NET_TYPE_NONET;
        dns=new ArrayList<>();
        router=new ArrayList<>();
        domain="";
        ip=0;
        netmask=0;

    }
    public static NetWorkInfoItem FromDhCp(DhcpInfo dhcp){
        if(dhcp == null){
            return null;
        }
        NetWorkInfoItem retitem=new NetWorkInfoItem();
        retitem.ip=dhcp.ipAddress;
        retitem.netmask=dhcp.netmask;
        retitem.router.add(dhcp.gateway);
        retitem.dns.add(dhcp.dns1);
        retitem.dns.add(dhcp.dns2);
        retitem.nettype=NetWorkInfoItem.NET_TYPE_WIFI;
        return retitem;
    }
    public static  String formatNetWorkType(NetWorkInfoItem item){
        if(item.nettype == NET_TYPE_ETHRNET){
            return "ethernet";
        }else if(item.nettype == NET_TYPE_WIFI){
            return "wifi";
        }else{
            return "no network";
        }
    }

}
