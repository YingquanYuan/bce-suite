package bce.swt.clientUI;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.jboss.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bce.jni.utils.BCEUtils;
import bce.swt.conf.BCEErrorCode;
import bce.swt.conf.BCEURLProvider;
import bce.swt.requests.BCEFetchSizeRequest;
import bce.swt.requests.BCELoginRequest;
import bce.swt.requests.RTMPConnRequest;
import bce.swt.util.BCEHandler;
import bce.swt.util.Player;

public class ClientUI {

    private static final Logger logger = LoggerFactory.getLogger(ClientUI.class);

    protected String sessionId;
    protected BCEURLProvider provider;
    protected Player player;
    protected RTMPConnRequest rtmpRequest;
    protected String privateKeyFileName = null;
    protected String paramsFileName = null;
    protected StringBuffer localVideoName = null;
    protected String remoteVideoName = null;
    protected String userName = null;
    protected String password = null;
    protected boolean isLogin = false;
    protected boolean isCancel = false;
    protected double currentVideoSize;
    protected DecimalFormat format;

    protected Shell shell;
    private Display display;
    private Composite compositeUsr;
    private Composite compositePlay;
    private Composite compositeStatusBar;
    private Text txtUserName;
    private Text txtPassword;
    private Label lblLoginMsg;
    private Label lblStatus;
    private Label lblSeparator;
    private Label lblProgress;
    private CLabel lblImage;
    private Button btnLogin;
    private Button btnConnect;
    private Button btnDisconnect;
    private Button btnPlay;
    private Button btnStop;
    private Button btnChooseKey;
    private List playList;
    private ProgressBar progressBar;

    /**
     * Launch the application.
     * @param args
     */
    public static void main(String[] args) {
        try {
            ClientUI window = new ClientUI();
            window.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the window.
     */
    public void open() {
        provider = BCEURLProvider.getInstance();
        player = Player.getInstance();
        format = new DecimalFormat("#.00");
        display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * Create contents of the window.
     */
    protected void createContents() {
        shell = new Shell();
        shell.setSize(849, 590);
        shell.setText("BCE Client");
        shell.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (rtmpRequest != null) {
                    ChannelFuture future = rtmpRequest.getChannel().disconnect();
                    if (future.isSuccess())
                        future = rtmpRequest.getChannel().close();
                    if (future.isSuccess())
                        rtmpRequest.getBootstrap().getFactory().releaseExternalResources();
                }
                isCancel = true;
                player.stop();
            }
        });

        compositeStatusBar = new Composite(shell, SWT.BORDER);
        compositeStatusBar.setBounds(10, 522, 620, 27);

        compositeUsr = new Composite(shell, SWT.BORDER);
        compositeUsr.setBounds(638, 10, 199, 539);

        final Color bkColor = new Color(null, 0x01, 0x01, 0x01);
        compositePlay = new Composite(shell, SWT.BORDER | SWT.EMBEDDED);
        compositePlay.setBounds(10, 10, 620, 506);
        compositePlay.setBackground(bkColor);

        lblLoginMsg = new Label(compositeUsr, SWT.RIGHT | SWT.BORDER);
        lblLoginMsg.setBounds(10, 10, 181, 23);
        lblLoginMsg.setText("Not Login");

        txtUserName = new Text(compositeUsr, SWT.BORDER);
        txtUserName.setBounds(88, 39, 103, 29);
        txtUserName.setText("");

        txtPassword = new Text(compositeUsr, SWT.BORDER | SWT.PASSWORD);
        txtPassword.setBounds(88, 74, 103, 29);
        txtPassword.setText("");

        btnLogin = new Button(compositeUsr, SWT.PUSH);
        btnLogin.setBounds(104, 109, 87, 29);
        btnLogin.setText("Login");
        btnLogin.addSelectionListener(new SelectionListener() {
            // @Override
            public void widgetSelected(SelectionEvent e) {
                if (isLogin) {
                    lblStatus.setText("Info: you have logined!");
                    return;
                }

                if (txtUserName.getText() == null || txtUserName.getText().equals("")) {
                    lblLoginMsg.setText("Null UserName");
                    return;
                }

                if (txtPassword.getText() == null || txtPassword.getText().equals("")) {
                    lblLoginMsg.setText("Null password");
                    return;
                }

                lblStatus.setText("Info: Connecting ...");
                BCELoginRequest request = new BCELoginRequest(new BCEHandler() {
                    @Override
                    public void handleResponse(byte[] responseData) throws IOException {
                        String responseStr = new String(responseData, "UTF-8");
                        String[] data = responseStr.split(";");
                        if (Integer.parseInt(data[0]) == BCEErrorCode.USER_NAME_NOT_EXISTS)
                            lblLoginMsg.setText("Wrong User Name");
                        if (Integer.parseInt(data[0]) == BCEErrorCode.PASSWORD_NOT_MATCH)
                            lblLoginMsg.setText("Wrong Password");
                        if (Integer.parseInt(data[0]) == BCEErrorCode.LOGIN_CHECK_PASSED) {
                            lblLoginMsg.setForeground(new Color(null, 255, 0, 0));
                            lblLoginMsg.setText("Welcome, " + data[1]);
                            sessionId = "JSESSIONID=" + data[2];
                            String[] playlist = data[3].split(",");
                            playList.removeAll();
                            for (int i = 0; i < playlist.length; i++)
                                playList.add(playlist[i]);
                            lblImage.setImage(new Image(Display.getDefault(), "/tmp/logo_user.png"));
                            userName = txtUserName.getText().trim();
                            password = txtUserName.getText().trim();
                            isLogin = true;
                            lblStatus.setText("Info: Logined");
                        }
                    }
                }, provider.getLoginURL(), sessionId, txtUserName.getText().trim(), txtPassword.getText().trim());
                display.asyncExec(request);
//				MessageDialog.open(MessageDialog.INFORMATION, shell, "alert", sessionId, MessageDialog.NONE);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        btnDisconnect = new Button(compositeUsr, SWT.PUSH);
        btnDisconnect.setBounds(104, 144, 87, 29);
        btnDisconnect.setText("Disconnect");
        btnDisconnect.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isCancel = true;
                if (rtmpRequest == null) {
                    lblStatus.setText("Info: Not connected!");
                    return;
                }
                ChannelFuture future = rtmpRequest.getChannel().disconnect();
                future.awaitUninterruptibly();
                if (future.isSuccess())
                    future = rtmpRequest.getChannel().close();
                future.awaitUninterruptibly();
                if (future.isSuccess())
                    rtmpRequest.getBootstrap().getFactory().releaseExternalResources();
                lblStatus.setText("Info: Disconnected to Flazr");
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        btnConnect = new Button(compositeUsr, SWT.PUSH);
        btnConnect.setBounds(10, 144, 90, 29);
        btnConnect.setText("Connect");
        btnConnect.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (localVideoName == null) {
                    lblStatus.setText("Info: please choose a video to play!");
                    return;
                }

                if (remoteVideoName == null) {
                    lblStatus.setText("Info: please choose a video to play!");
                    return;
                }

                if (privateKeyFileName == null) {
                    lblStatus.setText("Info: please choose private key file!");
                    return;
                }

                if (paramsFileName == null) {
                    lblStatus.setText("Info: please choose params file!");
                    return;
                }

                lblStatus.setText("Info: Connecting to Flazr ...");
                rtmpRequest = new RTMPConnRequest(new BCEHandler() {
                    @Override
                    public void handleResponse(byte[] responseData) throws IOException {
                        String feedback = new String(responseData, "UTF-8");
                        lblStatus.setText(feedback);

                        // 以下内容启动新线程处理进度条
                        final int min = progressBar.getMinimum();
                        final int max = progressBar.getMaximum();
                        isCancel = false;
                        final File videoFile = new File(localVideoName.toString());
                        // 创建更新进度条的线程
                        Runnable runnable = new Runnable() {
                            public void run() {
                                for (int i = min; i < max; i = (int) ((videoFile.length() / currentVideoSize) * max)) {
                                    final int selection = i;
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                    }
                                    if (isCancel) {
                                        break;
                                    }
                                    // 注意在更新进度条时加上了判断线程状态的条件
                                    shell.getDisplay().asyncExec(new Runnable() {
                                        public void run() {
                                            if (progressBar.isDisposed())
                                                return;
                                            progressBar.setSelection(selection);
                                            // 如果此时取消了线程，将进度条设置为初始状态
                                            if (isCancel) {
                                                progressBar.setSelection(0);
                                            }
                                        }
                                    });
                                    // 如果此时取消了线程，结束该循环，这个线程也就结束了，并重置线程状态
                                    if (isCancel) {
                                        break;
                                    }
                                }
                                logger.info("quit progress bar thread ...");
                            }
                        };
                        Thread pbThread = new Thread(runnable);
                        pbThread.start();
                    }
                }, "127.0.0.1", 1935, localVideoName.toString(), true, remoteVideoName, 1, privateKeyFileName, paramsFileName);
                display.asyncExec(rtmpRequest);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        btnPlay = new Button(compositeUsr, SWT.PUSH);
        btnPlay.setBounds(10, 179, 90, 29);
        btnPlay.setText("Play");
        btnPlay.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (localVideoName == null) {
                    lblStatus.setText("Info: no video chosen!");
                    return;
                }
                try {
                    lblStatus.setText("Currently playing: " + localVideoName.toString().substring(localVideoName.lastIndexOf("/") + 1));
                    player.start(localVideoName.toString(), compositePlay.embeddedHandle);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        btnStop = new Button(compositeUsr, SWT.PUSH);
        btnStop.setBounds(104, 179, 87, 29);
        btnStop.setText("Stop");
        btnStop.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                player.stop();
                lblStatus.setText("Info: player stopped");
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        btnChooseKey = new Button(compositeUsr, SWT.PUSH);
        btnChooseKey.setBounds(10, 109, 90, 29);
        btnChooseKey.setText("Setting");
        btnChooseKey.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog privateKeyDlg = new FileDialog(shell, SWT.OPEN);
                privateKeyDlg.setText("Select Private Key");
                privateKeyDlg.setFilterPath("/home/robins/桌面/密钥与参数文件");
                privateKeyDlg.setFilterExtensions(new String[] {"*.key"});
                privateKeyFileName = privateKeyDlg.open();
                logger.info("chosen privateKey file, name: {}", privateKeyFileName);

                FileDialog paramsDlg = new FileDialog(shell, SWT.OPEN);
                paramsDlg.setText("Select Private Key");
                paramsDlg.setFilterPath("/home/robins/桌面/密钥与参数文件");
                paramsDlg.setFilterExtensions(new String[] {"*.param"});
                paramsFileName = paramsDlg.open();
                logger.info("chosen curve params file, name: {}", paramsFileName);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        playList = new List(compositeUsr, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        playList.setBounds(10, 214, 181, 313);
        playList.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                final String videoName = playList.getItem(playList.getSelectionIndex()).split("-")[0];
                BCEFetchSizeRequest request = new BCEFetchSizeRequest(new BCEHandler() {
                    @Override
                    public void handleResponse(byte[] responseData) throws IOException {
                        currentVideoSize = BCEUtils.bytesToLong(responseData);
                        playList.setItem(playList.getSelectionIndex(), videoName + "-Size: " + format.format((currentVideoSize / (1024 * 1000))) + "MB");
                    }
                }, provider.getLoginURL(), sessionId, videoName);
                display.asyncExec(request);

                localVideoName = new StringBuffer().append("/home/robins/").append(videoName);
                remoteVideoName = videoName;
                lblStatus.setText("Info: " + videoName + " chosen");
//				MessageBox box = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
//				box.setMessage("You ordered the video: " + videoName + "\nPlease connect to media server now");
//				box.setText("prompt");
//				box.open();
//				MessageDialog.open(MessageDialog.INFORMATION, shell, "alert", "You ordered the video: " + playList.getItem(playList.getSelectionIndex()), MessageDialog.NONE);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        progressBar = new ProgressBar(compositeStatusBar, SWT.HORIZONTAL | SWT.SMOOTH);
        progressBar.setBounds(362, 5, 253, 16);
        progressBar.setMaximum(100);
        progressBar.setMinimum(0);

        lblStatus = new Label(compositeStatusBar, SWT.NONE);
        lblStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
        lblStatus.setBounds(8, 4, 258, 18);
        lblStatus.setText("Info: no info now");

        lblSeparator = new Label(compositeStatusBar, SWT.SEPARATOR | SWT.VERTICAL);
        lblSeparator.setBounds(272, 5, 8, 16);

        lblProgress = new Label(compositeStatusBar, SWT.NONE);
        lblProgress.setBounds(286, 4, 70, 18);
        lblProgress.setText("Progress:");

        lblImage = new CLabel(compositeUsr, SWT.CENTER);
        lblImage.setBounds(10, 39, 76, 64);
        lblImage.setImage(new Image(Display.getDefault(), "/tmp/logo.png"));
//		lblImage.setVisible(false);
    }
}