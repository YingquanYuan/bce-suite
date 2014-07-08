package com.flazr.rtmp.business;

import java.io.File;
import java.io.FilenameFilter;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpConfig;

public class PlaylistHandler extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistHandler.class);

    private Socket handlerSocket;

    public PlaylistHandler(Socket handlerSocket) {
        this.handlerSocket = handlerSocket;
    }

    private static final String getAllFileName(String directoryPath) {
        StringBuffer sb = new StringBuffer();
        File root = new File(directoryPath);
        if (root.isDirectory()) {
            String[] files = root.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return (name.endsWith(".f4v") || name.endsWith(".flv"));
                }
            });
            for (int i = 0; i < files.length; i++) {
                sb.append(files[i]).append(",");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.toString();
    }

    @Override
    public void run() {
        try {
            logger.info("tramsmitting playlist to bceserver ...");
            String fileList = getAllFileName(RtmpConfig.SERVER_HOME_DIR + "/apps/vod");
            OutputStream out = handlerSocket.getOutputStream();
            out.write(fileList.getBytes("UTF-8"));
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
