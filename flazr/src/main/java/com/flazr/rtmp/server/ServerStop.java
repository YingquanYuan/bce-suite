package com.flazr.rtmp.server;

import com.flazr.rtmp.RtmpConfig;
import com.flazr.util.Utils;

/**
 * 通过套接字发送信号，远程停止流媒体服务器
 * @author robins
 *
 */
public class ServerStop {

    public static void main(String[] args) {
        Utils.sendStopSignal(RtmpConfig.configureServerStop());
    }

}
