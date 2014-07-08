package com.flazr.rtmp.business;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpConfig;

import bce.jni.utils.BCEUtils;

public class VideoSizeHandler extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(VideoSizeHandler.class);

    private Socket handlerSocket;

    private String videoName;

    public VideoSizeHandler(Socket handlerSocket, String videoName) {
        this.handlerSocket = handlerSocket;
        this.videoName = videoName;
    }

    private static final long getVideoFileSize(String videoName) {
        final String videoFileName = videoName;
        File root = new File(RtmpConfig.SERVER_HOME_DIR + "/apps/vod");
        File[] files = null;
        if (root.isDirectory()) {
            files = root.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.equals(videoFileName));
                }
            });
        }
        return files[0].length();
    }

    @Override
    public void run() {
        logger.info("transmitting video: {} videoSize to bceserver ...", videoName);
        long videoFileSize = getVideoFileSize(videoName);
        OutputStream out;
        try {
            out = handlerSocket.getOutputStream();
            out.write(BCEUtils.longToBytes(videoFileSize));
            out.flush();
            handlerSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Socket getHandlerSocket() {
        return handlerSocket;
    }

    public void setHandlerSocket(Socket handlerSocket) {
        this.handlerSocket = handlerSocket;
    }
}
