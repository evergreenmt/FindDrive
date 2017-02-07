package com.seunindustries.finddrive;

/**
 * Created by P12278 on 2016-09-26.
 */
public class RawData {
    String cmd;
    int DestID;
    String DestName;
    Double Lat;
    Double Lgt;
    String addr;
    int ListID;
    String ListName;

    public String getCmd() { return cmd; }
    public void setCmd(String cmd) { this.cmd = cmd; }

    public int getDestID() {
        return DestID;
    }
    public void setDestID(int destID) {
        DestID = destID;
    }

    public String getDestName() {
        return DestName;
    }
    public void setDestName(String destName) {
        DestName = destName;
    }

    public Double getLat() {
        return Lat;
    }
    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLgt() {
        return Lgt;
    }
    public void setLgt(Double lgt) {
        Lgt = lgt;
    }

    public String getAddr() {
        return addr;
    }
    public void setAddr(String addr) {
        this.addr = addr;
    }

    public int getListID() {
        return ListID;
    }
    public void setListID(int listID) {
        ListID = listID;
    }

    public String getListName() {
        return ListName;
    }
    public void setListName(String listName) {
        ListName = listName;
    }
}
