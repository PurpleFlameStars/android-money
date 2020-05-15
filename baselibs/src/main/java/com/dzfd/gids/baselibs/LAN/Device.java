package com.dzfd.gids.baselibs.LAN;

/**
 * Created by zheng on 2019/2/26.
 */

public  class Device {
    public String ip;
    public String mac;
    public String name;

    public Device(String ip, String mac, String name) {
        this.ip = ip;
        this.mac = mac;
        this.name = name;
    }
}
