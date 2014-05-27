package bce.client.requests;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import bce.client.player.BCEHandler;

public class BCEFetchSizeRequest extends BCERequest {

    private String videoName;

    public BCEFetchSizeRequest(BCEHandler handler, URL url, String sessionId, String videoName) {
        super(handler, url, sessionId);
        this.videoName = videoName;
    }

    @Override
    public byte[] getRequestContent() {
        StringBuilder content = new StringBuilder();
        try {
            content.append(URLEncoder.encode("vn", "UTF-8")).append("=")
                    .append(URLEncoder.encode(this.videoName, "UTF-8")).append("&")
                    .append(URLEncoder.encode("flag", "UTF-8")).append("=")
                    .append(URLEncoder.encode("2", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content.toString().getBytes();
    }
}
