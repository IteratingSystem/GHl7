package com.ghl7.instrument;

/**
 * @Auther WenLong
 * @Date 2024/7/11 11:45
 * @Description
 **/
public abstract class BaseInstrument {
    public String mid;
    public int port;
    public boolean useSSL;

    public BaseInstrument(String mid,int port,boolean useSSL){
        this.port = port;
        this.mid = mid;
        this.useSSL = useSSL;
    }

    public abstract void start();
}
