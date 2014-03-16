package bce.server.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bce.java.entities.BCEPrivateKey;
import bce.server.entities.PersistentPrivateKey;
import bce.server.entities.PersistentUser;
import bce.server.servicers.MainPageServicer;
import bce.server.util.SpringUtil;

/**
 * 用于处理书城主页请求的控制器
 * 
 * @author robins
 *
 */
@WebServlet(name = "MainPageServlet", urlPatterns = { "/MainPageServlet.sl" }, description = "用于处理书城主页请求的控制器", displayName = "主页控制器")
public class MainPageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private final static String USER_LOGOUT = "1";
	private final static String APPLY_NEW_PRIVATE_KEY = "2";
	private final static String DOWNLOAD_PRIVATE_KEY = "3";
	private final static String DOWNLOAD_PARAMS_FILE = "4";

	MainPageServicer servicer;
	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MainPageServlet() {
		super();
	}
	
	/**
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		servicer = (MainPageServicer) SpringUtil.getBean("mainPageServicer");
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
	 * 处理请求的方法
	 * 
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		PersistentUser user = (PersistentUser) request.getSession().getAttribute(PersistentUser.ATTRIBUTE_KEY);
		String flag = request.getParameter("flag");
		
		if (user != null && flag == null) {
			
			List<PersistentPrivateKey> dataList = this.servicer.prepareBCEData(user);
			request.setAttribute("dataList", dataList);
			RequestDispatcher dispatcher = request.getRequestDispatcher("bce_user_main.jsp");
			dispatcher.forward(request, response);
			return;
		} else if (flag != null && MainPageServlet.USER_LOGOUT.equals(flag.trim())) {
			
			request.getSession().removeAttribute(PersistentUser.ATTRIBUTE_KEY);
			request.getSession().removeAttribute("havePassLoginCheck");
			request.getSession().removeAttribute("user");
			request.getSession().invalidate();
			response.sendRedirect("bce_user_login.jsp");
			return;
		} else if (flag != null && MainPageServlet.APPLY_NEW_PRIVATE_KEY.equals(flag.trim())) {
			String password = request.getParameter("pwd");
			String vcodeInRequest = request.getParameter("vc");
			String vcodeInSession = (String) request.getSession().getAttribute("vcode");
			if (!this.servicer.isValidPassword(password, user)) {
				PrintWriter out = response.getWriter();
				out.println("{\"result\":\"fail\", \"reason\":\"wrong password\"}");
				out.flush();
				out.close();
				return;
			}
			if (!this.servicer.isValidVcode(vcodeInRequest, vcodeInSession)) {
				PrintWriter out = response.getWriter();
				out.println("{\"result\":\"fail\", \"reason\":\"wrong vcode\"}");
				out.flush();
				out.close();
				return;
			}
			PersistentPrivateKey privateKey = this.servicer.applyBCEPrivateKey(user);
			response.setContentType("application/json;charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("{\"result\":\"success\", \"pkId\":\"" + privateKey.getPrivateKeyId() + "\", \"sysId\":\"" + privateKey.getBelongedBCESystem().getBceSystemId() + "\", \"uname\":\"" + privateKey.getBelongedUser().getUserName() + "\", \"legal\":\"" + (privateKey.getIsLegal() == 1? "Yes" : "No") + "\"}");
			out.flush();
			out.close();
			return;
		} else if (flag != null && MainPageServlet.DOWNLOAD_PRIVATE_KEY.equals(flag.trim())) {
			
			String pkId = request.getParameter("pkId");
			if (pkId == null)
				return;
			Integer privateKeyId = Integer.parseInt(pkId);
			BCEPrivateKey privateKey = this.servicer.prepareBCEPrivateKey(privateKeyId);
			response.setContentType("application/octet-stream");
			String fileName = URLEncoder.encode("BCEPK-" + privateKeyId + ".key", "UTF-8");
			response.addHeader("Content-Disposition","attachment; filename=" + fileName); 
			OutputStream out = response.getOutputStream();
			out.write(privateKey.toBytes());
			out.flush();
			out.close();
			return;
		} else if (flag != null && MainPageServlet.DOWNLOAD_PARAMS_FILE.equals(flag.trim())) {
			
			String sysId = request.getParameter("sysId");
			if (sysId == null)
				return;
			Integer systemId = Integer.parseInt(sysId);
			File paramsFile = this.servicer.prepareBCEClientParams(systemId);
			FileInputStream fis = new FileInputStream(paramsFile);
			byte[] buffer = new byte[1024];
			int marker;
			response.setContentType("application/octet-stream");
			String fileName = URLEncoder.encode(paramsFile.getName(), "UTF-8");
			response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
			OutputStream out = response.getOutputStream();
			while ((marker = fis.read(buffer)) > 0) {
				out.write(buffer, 0, marker);
			}
			out.flush();
			out.close();
			return;
		}
	}

}
