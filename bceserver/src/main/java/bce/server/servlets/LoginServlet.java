package bce.server.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bce.jni.utils.BCEUtils;
import bce.server.entities.PersistentUser;
import bce.server.servicers.LoginPageServicer;
import bce.server.util.SpringUtil;

/**
 * 用于处理登陆页面请求的控制器
 *
 * @author robins
 *
 */
@WebServlet(name = "LoginServlet", urlPatterns = { "/LoginServlet.sl" }, description = "用于处理登陆页面请求的控制器", displayName = "注册页面控制器")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public final static String PROCESS_LOGIN = "0";
    public final static String PROCESS_CLIENT_LOGIN = "1";
    public final static String PROCESS_FETCH_SIZE = "2";
    public final static int USER_NAME_NOT_EXISTS = 0;
    public final static int PASSWORD_NOT_MATCH = 1;
    public final static int LOGIN_CHECK_PASSED = 2;

    protected LoginPageServicer servicer;

    protected Map<String, Long> videoNameSizeMap;

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();
        servicer = (LoginPageServicer) SpringUtil.getBean("loginPageServicer");
        videoNameSizeMap = new ConcurrentHashMap<String, Long>();
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Encapsulated request processing layer of doPost method.
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String flag = request.getParameter("flag");
        if (flag != null && flag.trim().equals(LoginServlet.PROCESS_LOGIN)) {
            this.processLogin(request, response);
            return;
        } else if (flag != null && flag.trim().equals(LoginServlet.PROCESS_CLIENT_LOGIN)) {
            this.processClientLogin(request, response);
            return;
        } else if (flag != null && flag.trim().equals(LoginServlet.PROCESS_FETCH_SIZE)) {
            this.processFetchVideoSize(request, response);
            return;
        }
    }

    protected void processFetchVideoSize(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String videoName = request.getParameter("vn").trim();
        System.out.println("videoName = " + videoName);
        if (videoNameSizeMap.containsKey(videoName)) {
            OutputStream out = response.getOutputStream();
            out.write(BCEUtils.longToBytes(videoNameSizeMap.get(videoName)));
            out.flush();
            out.close();
            return;
        }

        Socket socket = new Socket("127.0.0.1", 30000);
        OutputStream sOut = socket.getOutputStream();
        InputStream sIn = socket.getInputStream();
        // set command
        sOut.write(BCEUtils.intToBytes(2));
        // set data length
        sOut.write(BCEUtils.intToBytes(videoName.getBytes("UTF-8").length));
        // set data
        sOut.write(videoName.getBytes("UTF-8"));
        sOut.flush();
        byte[] tmp = new byte[16];
        int len;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while ((len = sIn.read(tmp)) > 0)
            buffer.write(tmp, 0, len);
        socket.close();
        long videoSize = BCEUtils.bytesToLong(buffer.toByteArray());
        videoNameSizeMap.put(videoName, videoSize);

        OutputStream out = response.getOutputStream();
        out.write(BCEUtils.longToBytes(videoSize));
        out.flush();
        out.close();
        return;
    }

    protected void processClientLogin(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("u").trim();
        String password = request.getParameter("p").trim();
        System.out.println("userName = " + userName + ", password = " + password);
        PersistentUser loginUser = new PersistentUser();
        int loginCheckResult = this.servicer.checkLogin(userName, password, loginUser);
        if (loginCheckResult == USER_NAME_NOT_EXISTS) {
            OutputStream out = response.getOutputStream();
            out.write((USER_NAME_NOT_EXISTS + "").getBytes("UTF-8"));
            out.flush();
            out.close();
            return;
        } else if (loginCheckResult == PASSWORD_NOT_MATCH) {
            OutputStream out = response.getOutputStream();
            out.write((PASSWORD_NOT_MATCH + "").getBytes("UTF-8"));
            out.flush();
            out.close();
            return;
        } else if (loginCheckResult == LOGIN_CHECK_PASSED) {
            request.getSession().setAttribute(PersistentUser.ATTRIBUTE_KEY, loginUser);
            System.out.println(request.getSession().getId());
            OutputStream out = response.getOutputStream();
            StringBuffer buf = new StringBuffer();
            buf.append(LOGIN_CHECK_PASSED).append(";").append(loginUser.getUserName()).append(";").append(request.getSession().getId());

            /** 命令 int 4字节/数据 int 4字节/发送的数据 string.getBytes() */
            Socket socket = new Socket("127.0.0.1", 30000);
            OutputStream out1 = socket.getOutputStream();
            InputStream in1 = socket.getInputStream();
            // set command
            out1.write(BCEUtils.intToBytes(1));
            // set data length
            out1.write(BCEUtils.intToBytes(0));
            out1.flush();
            byte[] tmp = new byte[16];
            int len;
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            while ((len = in1.read(tmp)) > 0)
                buffer.write(tmp, 0, len);
            socket.close();
            String temp = new String(buffer.toByteArray(), "UTF-8");
            System.out.println(temp);
            buf.append(";").append(temp);

            out.write(buf.toString().getBytes("UTF-8"));
            out.flush();
            out.close();
            return;
        } else {
            return;
        }
    }

    /**
     * 处理自动登录
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void processAutoLogin(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        session.setAttribute("user", request.getParameter("u").trim());
        session.setMaxInactiveInterval(120);
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setMaxAge(120);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 处理登录请求
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void processLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("u").trim();
        String password = request.getParameter("p").trim();
        String[] rememberPwd = request.getParameterValues("chkRemember");
        System.out.println("userName = " + userName + ", password = " + password);
        PersistentUser loginUser = new PersistentUser();
        int loginCheckResult = this.servicer.checkLogin(userName, password, loginUser);
        if (loginCheckResult == USER_NAME_NOT_EXISTS) {
            request.setAttribute("errorInfo", "unErr");
            RequestDispatcher dispatcher = request.getRequestDispatcher("bce_user_login.jsp");
            dispatcher.forward(request, response);
            return;
        } else if (loginCheckResult == PASSWORD_NOT_MATCH) {
            request.setAttribute("errorInfo", "pwdErr");
            RequestDispatcher dispatcher = request.getRequestDispatcher("bce_user_login.jsp");
            dispatcher.forward(request, response);
            return;
        } else if (loginCheckResult == LOGIN_CHECK_PASSED) {
            if (rememberPwd != null && rememberPwd[0].equals("checked"))
                this.processAutoLogin(request, response);
            request.getSession().setAttribute(PersistentUser.ATTRIBUTE_KEY, loginUser);
            request.getSession().setAttribute("flag", "0");
            response.sendRedirect("MainPageServlet.sl");
            return;
        } else {
            return;
        }
    }

}
