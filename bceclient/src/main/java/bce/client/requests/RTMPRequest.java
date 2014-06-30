package bce.client.requests;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import bce.client.player.BCEHandler;

import com.flazr.rtmp.client.ClientOptions;
import com.flazr.rtmp.client.ClientPipelineFactory;

public abstract class RTMPRequest implements Runnable {

    protected BCEHandler handler;

    protected ClientOptions options;

    protected ClientBootstrap bootstrap;

    protected Channel channel;

    public RTMPRequest(BCEHandler handler, String url, String saveAs) {
        this.handler = handler;
        this.options = new ClientOptions(url, saveAs);
    }

    public RTMPRequest(BCEHandler handler) {
        this.handler = handler;
        options = new ClientOptions();
    }

    private static ClientBootstrap getBootstrap(final Executor executor, ClientOptions options) {
        final ChannelFactory factory = new NioClientSocketChannelFactory(executor, executor);
        final ClientBootstrap bootstrap = new ClientBootstrap(factory);
        bootstrap.setPipelineFactory(new ClientPipelineFactory(options));
        bootstrap.setOption("tcpNoDelay" , true);
        bootstrap.setOption("keepAlive", true);
        return bootstrap;
    }

    private void connect(ClientOptions options) throws IOException {
        bootstrap = getBootstrap(Executors.newCachedThreadPool(), options);
        final ChannelFuture future = bootstrap.connect(new InetSocketAddress(options.getHost(), options.getPort()));
        channel = future.getChannel();
        future.awaitUninterruptibly();
        if (!future.isSuccess()) {
            System.out.println("error creating client connection: " + future.getCause().getMessage());
            return;
        }
        this.handler.handleResponse("Connected to Flazr, transmitting ...".getBytes("UTF-8"));
    }

    @Override
    public void run() {
        try {
            final int count = options.getLoad();
            if(count == 1 && options.getClientOptionsList() == null)
                connect(options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientOptions getOptions() {
        return options;
    }

    public void setOptions(ClientOptions options) {
        this.options = options;
    }

    public ClientBootstrap getBootstrap() {
        return bootstrap;
    }

    public void setBootstrap(ClientBootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

}