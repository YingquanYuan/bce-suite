package bce.client.requests;

import bce.client.player.BCEHandler;

public class RTMPConnRequest extends RTMPRequest {

    public RTMPConnRequest(BCEHandler handler, String host, int port, String saveAs, boolean rtmpe, String streamName, int bceSystemId, String privateKeyFileName, String paramsFileName) {
        super(handler);
        options.setHost(host);
        options.setPort(port);
        options.setSaveAs(saveAs);
        options.setRtmpe(rtmpe);
        options.setStreamName(streamName);
        options.setBceSystemId(bceSystemId);
        options.setPrivateKeyFileName(privateKeyFileName);
        options.setParamsFileName(paramsFileName);
    }

    public RTMPConnRequest(BCEHandler handler, String url, String saveAs, int bceSystemId, String privateKeyFileName, String paramsFileName) {
        super(handler, url, saveAs);
        options.setBceSystemId(bceSystemId);
        options.setPrivateKeyFileName(privateKeyFileName);
        options.setParamsFileName(paramsFileName);
    }
}