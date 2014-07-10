package com.flazr.rtmp.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flazr.rtmp.RtmpConfig;
import com.flazr.util.BusinessMonitor;
import com.flazr.util.StopMonitor;

public class RtmpService implements Daemon {

    private static final Logger logger = LoggerFactory.getLogger(RtmpService.class);

    static {
        RtmpConfig.configureServer();
        CHANNELS = new DefaultChannelGroup("server-channels");
        APPLICATIONS = new ConcurrentHashMap<String, ServerApplication>();
        TIMER = new HashedWheelTimer(RtmpConfig.TIMER_TICK_SIZE, TimeUnit.MILLISECONDS);
    }

    protected static final ChannelGroup CHANNELS;
    protected static final Map<String, ServerApplication> APPLICATIONS;
    public static final Timer TIMER;

    private ChannelFactory factory;
    private ServerBootstrap bootstrap;
    private InetSocketAddress socketAddress;
    private BusinessMonitor businessMonitor;
    private Thread stopMonitor;

    @Override
    public void init(DaemonContext arg0) throws Exception {
        factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new ServerPipelineFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        socketAddress = new InetSocketAddress(RtmpConfig.SERVER_PORT);

        businessMonitor = new BusinessMonitor(RtmpConfig.BUSINESS_PORT);
        stopMonitor = new StopMonitor(RtmpConfig.SERVER_STOP_PORT);
    }

    @Override
    public void start() throws Exception {
        bootstrap.bind(socketAddress);
        logger.info("server started, listening on: {}", socketAddress);
        businessMonitor.start();
        stopMonitor.start();
    }

    @Override
    public void stop() throws Exception {
        TIMER.stop();
        businessMonitor.setRunning(false);
        final ChannelGroupFuture future = CHANNELS.close();
        logger.info("closing channels");
        future.awaitUninterruptibly();
        logger.info("server stopped");
    }

    @Override
    public void destroy() {
        factory.releaseExternalResources();
        logger.info("released resources");
    }

    private void waitForStopSignal() throws Exception {
        this.stopMonitor.join();
    }

    public static void main(String[] args) throws Exception {
        RtmpService server = new RtmpService();
        server.init(null);
        server.start();
        server.waitForStopSignal();
        server.stop();
        server.destroy();
    }

}
