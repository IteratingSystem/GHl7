package com.ghl7.instrument;

/**
 * @Auther WenLong
 * @Date 2024/7/11 11:45
 * @Description
 **/
public abstract class BaseInstrument {
    public String mid;
    public int port;
    public boolean useSTL;

    public BaseInstrument(String mid,int port,boolean useSTL){
        this.port = port;
        this.mid = mid;
        this.useSTL = useSTL;
    }

    public abstract void start();
    public abstract void dispose();
}
