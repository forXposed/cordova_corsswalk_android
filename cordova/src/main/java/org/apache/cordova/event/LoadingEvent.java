package org.apache.cordova.event;

/**
 * Created by zhengxiaofeng on 2016/7/14.
 */
public class LoadingEvent {
    public final static int  Started = 0;
    public final static int  Error = 1;
    public final static int  Finished = 2;
    public final static int  Reload = 3;
    public final static int  closeAPP = 4;
    public int status;
}
