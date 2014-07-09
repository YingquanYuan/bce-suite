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

public class RtmpServer1 implements Daemon {

    private static final Logger logger = LoggerFactory.getLogger(RtmpServer1.class);

    static {
        RtmpConfig.configureServer();
        CHANNELS = new DefaultChannelGroup("server-channels");
        APPLICATIONS = new ConcurrentHashMap<String, ServerApplication>();
        TIMER = new HashedWheelTimer(RtmpConfig.TIMER_TICK_SIZE, TimeUnit.MILLISECONDS);
    }

    protected static final ChannelGroup CHANNELS;
    protected static final Map<String, ServerApplication> APPLICATIONS;
    public static final Timer TIMER;

    public static void main(String[] args) throws Exception {

        final ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        final ServerBootstrap bootstrap = new ServerBootstrap(factory);

        bootstrap.setPipelineFactory(new ServerPipelineFactory());
        bootstrap.setOption("child.tcpNoDelay", true);
        bootstrap.setOption("child.keepAlive", true);
        final InetSocketAddress socketAddress = new InetSocketAddress(RtmpConfig.SERVER_PORT);
        bootstrap.bind(socketAddress);
        logger.info("server started, listening on: {}", socketAddress);

        final BusinessMonitor bMonitor = new BusinessMonitor(RtmpConfig.BUSINESS_PORT);
        bMonitor.start();

        final Thread monitor = new StopMonitor(RtmpConfig.SERVER_STOP_PORT);
        monitor.start();
        monitor.join();

        TIMER.stop();
        bMonitor.setRunning(false);
        final ChannelGroupFuture future = CHANNELS.close();
        logger.info("closing channels");
        future.awaitUninterruptibly();
        logger.info("releasing resources");
        factory.releaseExternalResources();
        logger.info("server stopped");
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(DaemonContext arg0) throws Exception {
    }

    @Override
    public void start() throws Exception {
    }

    @Override
    public void stop() throws Exception {
    }

}
