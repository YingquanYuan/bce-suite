package bce.client.requests;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

import bce.client.player.BCEHandler;

public class BCELoginRequest extends BCERequest {

    private String userName;

    private String password;

    public BCELoginRequest(BCEHandler handler, URL url, String sessionId, String userName, String password) {
        super(handler, url, sessionId);
        this.userName = userName;
        this.password = password;
    }

    @Override
    public byte[] getRequestContent() {

        StringBuilder content = new StringBuilder();
        try {
            content.append(URLEncoder.encode("u", "UTF-8")).append("=")
                    .append(URLEncoder.encode(this.userName, "UTF-8")).append("&")
                    .append(URLEncoder.encode("p", "UTF-8")).append("=")
                    .append(URLEncoder.encode(this.password, "UTF-8")).append("&")
                    .append(URLEncoder.encode("flag", "UTF-8")).append("=")
                    .append(URLEncoder.encode("1", "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return content.toString().getBytes();
    }

}
