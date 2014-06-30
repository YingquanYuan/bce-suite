package com.flazr.util;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bce.jni.utils.BCEUtils;

import com.flazr.rtmp.business.PlaylistHandler;
import com.flazr.rtmp.business.VideoSizeHandler;

public class BusinessMonitor extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(BusinessMonitor.class);
    private static final int TRANSMIT_PLAYLIST = 1;
    private static final int TRANSMIT_VIDEO_SIZE = 2;

    private ServerSocket socket;

    private volatile boolean isRunning = true;

    public BusinessMonitor(int port) {
        setDaemon(true);
        setName("BusinessMonitor");
        try {
            socket = new ServerSocket(port, 30, InetAddress.getByName("127.0.0.1"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        logger.info("business monitor thread listening on: {}", socket);
        while (isRunning) {
            try {
                Socket accepted = socket.accept();
                InputStream in = accepted.getInputStream();
                byte[] command = new byte[4];
                in.read(command, 0, 4);
                byte[] dataLength = new byte[4];
                in.read(dataLength, 0, 4);
                byte[] data = null;
                int len;
                if ((len = BCEUtils.bytesToInt(dataLength)) > 0) {
                    data = new byte[len];
                    in.read(data, 0, len);
                }

                if (BCEUtils.bytesToInt(command) == TRANSMIT_PLAYLIST) {
                    logger.info("receive signal to transmit playlist, transmitting to bceserver...");
                    PlaylistHandler handler = new PlaylistHandler(accepted);
                    handler.start();
                } else if (BCEUtils.bytesToInt(command) == TRANSMIT_VIDEO_SIZE) {
                    logger.info("receive signal to transmit videoSize, transmitting to bceserver...");
                    VideoSizeHandler handler = new VideoSizeHandler(accepted, new String(data, "UTF-8"));
                    handler.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.info("business monitor die ...");
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
