package sisy.support.ashokas.sisy.DbModel;

/**
 * Created by DARK-DEVIL on 1/3/2021.
 */

public class MDL_DbCash {
    String strName,strAddrs,strCash,strTime,strUID,strPaymntType;

    public MDL_DbCash() {
    }

    public MDL_DbCash(String strName, String strAddrs, String strCash, String strTime, String strUID, String strPaymntType) {
        this.strName = strName;
        this.strAddrs = strAddrs;
        this.strCash = strCash;
        this.strTime = strTime;
        this.strUID = strUID;
        this.strPaymntType = strPaymntType;
    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrAddrs() {
        return strAddrs;
    }

    public void setStrAddrs(String strAddrs) {
        this.strAddrs = strAddrs;
    }

    public String getStrCash() {
        return strCash;
    }

    public void setStrCash(String strCash) {
        this.strCash = strCash;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public String getStrUID() {
        return strUID;
    }

    public void setStrUID(String strUID) {
        this.strUID = strUID;
    }

    public String getStrPaymntType() {
        return strPaymntType;
    }

    public void setStrPaymntType(String strPaymntType) {
        this.strPaymntType = strPaymntType;
    }
}
