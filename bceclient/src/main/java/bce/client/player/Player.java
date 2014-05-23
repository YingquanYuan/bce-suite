package bce.client.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Player {

    private static final String MPLAYER = "/usr/bin/mplayer";

    private static Player player;

    private Process process;
    private InputStream stderr;
    private InputStream stdin;

    private Thread stdinThread;
    private Thread stderrThread;

    private boolean isStarted;
    private volatile boolean isRunning;

    private String[] cmd;

    static {
        if (player == null)
            player = new Player();
    }

    private Player() {
        this.isStarted = false;
        this.isRunning = false;
    }

    public static Player getInstance() {
        return player;
    }

    public synchronized void start(String videoFileName, int hWnd) throws IOException {
        if (this.isStarted)
            return;
        // 调用mplayer命令行
        this.cmd = new String[] {
                MPLAYER,						// mplayer路径
                "-vo", "x11",					// linux下只能用x11和xv还有一个神码的,windows下用directX
                "-identify",					// 输出详情
                "-slave", 						// slave模式播放
                "-wid", String.valueOf(hWnd),	// 视频窗口的 handle
                "-colorkey", "0x010101",		// 视频窗口的背景色
                "-osdlevel", String.valueOf(1),	// osd样式
                videoFileName 					// 播放文件路径
        };
        this.process = Runtime.getRuntime().exec(cmd);
        this.stderr = process.getErrorStream();
        this.stdin = process.getInputStream();

        this.isRunning = true;
        this.stderrThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final BufferedReader errReader = new BufferedReader(new InputStreamReader(stderr, "UTF-8"));
                    for (String l = errReader.readLine(); l != null; errReader.readLine()) {
                        if (!isRunning)
                            break;
//						System.out.println(l);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.stderrThread.start();

        this.stdinThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final BufferedReader inReader = new BufferedReader(new InputStreamReader(stdin, "UTF-8"));
                    for (String l = inReader.readLine(); l != null; l = inReader.readLine()) {
                        if (!isRunning)
                            break;
//						System.out.println(l);
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
        this.stdinThread.start();
        this.isStarted = true;
    }

    public void stop() {
        if (this.process == null)
            return;
        this.isRunning = false;
        try {
            Thread.sleep(100);
            this.stderr.close();
            this.stdin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.process.destroy();
        this.isStarted = false;
    }

    public Process getProcess() throws IOException {
        if (this.process != null)
            return this.process;
        return null;
    }

    public InputStream getStderr() {
        return stderr;
    }

    public InputStream getStdin() {
        return stdin;
    }

    public Thread getStdinThread() {
        return stdinThread;
    }

    public Thread getStderrThread() {
        return stderrThread;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
