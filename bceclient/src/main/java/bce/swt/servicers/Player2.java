package bce.swt.servicers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Player2
{
	private static final String MPLAYER = "/usr/bin/mplayer";
	private static final String VIDEO_FILE = "/home/robins/桌面/Others/Avengers.flv";

	public static Process getFlazr(String videoFileName) throws IOException {
		
		String[] cmd = new String[] {
			"/home/robins/文档/flazr-0.7-RC2/./client.sh",
			"-host", "127.0.0.1",
			"-port", "1935",
			"-rtmpe",
			"ACDC.flv",
			"/home/robins/ACDC.flv"
		};
		final Process process = Runtime.getRuntime().exec(cmd);
		final InputStream stderr = process.getErrorStream();
		final InputStream stdin = process.getInputStream();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final BufferedReader lReader = new BufferedReader(new InputStreamReader(stderr, "UTF-8"));
					for (String l = lReader.readLine(); l != null; l = lReader.readLine()) {
						System.out.println(l);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}

			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					final BufferedReader lReader = new BufferedReader(new InputStreamReader(stdin, "UTF-8"));
					for (String l = lReader.readLine(); l != null; l = lReader.readLine()) {
						System.out.println(l);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}).start();
		
		return process;
	}
	
	public static Process getPlayer(String videoFileName, final int hWnd) throws IOException
	{
		// 调用mplayer命令行
		String[] cmd = new String[] {
			MPLAYER,// mplayer路径
			"-vo", "x11",// linux下只能用x11和xv还有一个神码的,windows下用directX
			"-identify", // 输出详情
			"-slave", // slave模式播放
			"-wid", String.valueOf(hWnd),// 视频窗口的 handle
			"-colorkey", "0x010101",// 视频窗口的背景色
			"-osdlevel", String.valueOf(1),// osd样式
			videoFileName // 播放文件路径
		};

		final Process lProcess = Runtime.getRuntime().exec(cmd);
		final InputStream stderr = lProcess.getErrorStream();
		final InputStream stdin = lProcess.getInputStream();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					final BufferedReader lReader = new BufferedReader(new InputStreamReader(stderr, "UTF-8"));
					for (String l = lReader.readLine(); l != null; l = lReader.readLine()) {
						System.out.println(l);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}

			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					final BufferedReader lReader = new BufferedReader(new InputStreamReader(stdin, "UTF-8"));
					for (String l = lReader.readLine(); l != null; l = lReader.readLine()) {
						System.out.println(l);
					}
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}).start();

		return lProcess;
	}
	
	public static void main(String[] args) {
		try {
			final Process process = getFlazr("");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

//	public static void main(String[] pArgs) throws Exception
//	{
//		final Display wDisplay = new Display();
//		final Shell wShell = new Shell(wDisplay, SWT.SHELL_TRIM);
//		
//		// 在mplayer中你设置背景色为多少，在这里你就应该设置播放窗体的背景色为多少。
//		final Color bkColor = new Color(null, 0x01, 0x01, 0x01);
//		final FillLayout wLayout = new FillLayout();
//		Composite videoComposite;
//		videoComposite = new Composite(wShell, SWT.EMBEDDED);
//		videoComposite.setLayout(wLayout);
//		videoComposite.setBackground(bkColor);
//		videoComposite.setBounds(new Rectangle(0, 51, 720, 480));
//		wShell.setLayout(wLayout);
//		wShell.setSize(800, 600);
//		wShell.layout();
//		wShell.setVisible(true);
//		Button btn = new Button(videoComposite, SWT.PUSH);
//		btn.setVisible(true);
//		int han = 0;
//		
//		/*
//		 * handle ,windows下使用.handle来获取窗体句柄。但是linux下使用embeddedHandle来获取句柄，否则会出现
//		 * X11 error: BadDrawable (invalid Pixmap or Window parameter)
//		 * X11 error: BadWindow (invalid Window parameter)
//		 * 这样的错误
//		 */
//		han = videoComposite.embeddedHandle;
//		System.out.println(han);
//		
//		final Player player = Player.getInstance();
//		wShell.addDisposeListener(new DisposeListener() {
//			
//			@Override
//			public void widgetDisposed(DisposeEvent e) {
//				player.stop();
//			}
//		});
//		player.start(VIDEO_FILE, han);
//		
////		final Process lProcess = getPlayer(VIDEO_FILE, han);
//
//		while (!wShell.isDisposed()) {
//			if (!wDisplay.readAndDispatch()) {
//				wDisplay.sleep();
//			}
//		}
//
//		player.stop();
////		lProcess.destroy();
//	}
}