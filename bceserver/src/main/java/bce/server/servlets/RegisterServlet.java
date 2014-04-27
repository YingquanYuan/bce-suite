package bce.server.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bce.server.entities.PersistentUser;
import bce.server.servicers.RegisterPageServicer;
import bce.server.util.SpringUtil;

/**
 * 用于处理BCE注册页面请求的控制器
 *
 * @author robins
 *
 */
@WebServlet(displayName = "注册页面控制器", description = "用于处理BCE注册页面请求的控制器", name = "RegisterServlet", urlPatterns = { "/RegisterServlet.sl" })
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected final static String PROCESS_FORM = "0";
    protected final static String PROCESS_USERNAME = "1";
    protected final static String PROCESS_EMAIL = "2";

    private RegisterPageServicer servicer;

    /**
     * @see javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {
        super.init();
        servicer = (RegisterPageServicer) SpringUtil.getBean("registerPageServicer");
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Encapsulated request processing layer of doPost method.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String flag = request.getParameter("flag");
        if (flag != null && flag.trim().equals(RegisterServlet.PROCESS_USERNAME)) {
            processUserName(request, response);
            return;
        } else if (flag != null && flag.trim().equals(RegisterServlet.PROCESS_EMAIL)) {
            processEmail(request, response);
            return;
        } else if (flag != null && flag.trim().equals(RegisterServlet.PROCESS_FORM)) {

            PersistentUser user = processForm(request, response);
            // return null means vc errors in the form, hence send it back for correction
            if (user == null) {
                request.setAttribute("errorInfo", "vc_error");
                RequestDispatcher dispatcher = request.getRequestDispatcher("bce_user_register.jsp");
                dispatcher.forward(request, response);
                return;
                // return not null means the form has been submitted successfully, hence forward to next step
            } else {
                request.getSession().setAttribute(PersistentUser.ATTRIBUTE_KEY, user);
                request.getSession().setAttribute("flag", "0");
                response.sendRedirect("MainPageServlet.sl");
                return;
            }
        }
    }

    /**
     * Process the uniqueness of the user email in database asynchronously.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void processEmail(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email").trim();
        System.out.println(email);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        if (this.servicer.asyncValidateEmail(email)) {
            out.println("{\"result\":\"success\"}");
        } else {
            out.println("{\"result\":\"fail\"}");
        }
        out.flush();
    }

    /**
     * Process the user name existence validation asynchronously.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected void processUserName(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String userName = request.getParameter("userName").trim();
        System.out.println(userName);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        if (this.servicer.asyncValidateUserName(userName)) {
            out.println("{\"result\":\"success\"}");
        } else {
            out.println("{\"result\":\"fail\"}");
        }
        out.flush();
    }

    /**
     * Process the data sent from the whole front page form.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException
     * @throws IOException
     */
    protected PersistentUser processForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        /** check validate code */
        String codeInRequest = request.getParameter("cd");
        String codeInSession = (String) request.getSession().getAttribute("vcode");
        if (!this.servicer.checkValidateCode(codeInRequest, codeInSession)) {
            System.out.println("验证失败！");
            return null;
        }

        /** if validate code is right, save new member info */
        Map<String, String> memberInfoMap = new HashMap<String, String>();
        memberInfoMap.put("userName", request.getParameter("un"));
        memberInfoMap.put("password", request.getParameter("p1"));
        memberInfoMap.put("email", request.getParameter("em"));

        PersistentUser user = this.servicer.save(memberInfoMap);
        return user;
    }

}
