package bce.server.filters;

import java.io.IOException;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bce.server.entities.PersistentUser;

/**
 * 用于检查用户登录状态的过滤器
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = { "/MainPageServlet.sl" }, dispatcherTypes = {
		DispatcherType.FORWARD, DispatcherType.REQUEST, DispatcherType.INCLUDE,
		DispatcherType.ERROR, DispatcherType.ASYNC })
public class LoginCheckFilter implements Filter {

	/**
	 * Default constructor.
	 */
	public LoginCheckFilter() {
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		PersistentUser user = (PersistentUser) httpServletRequest.getSession().getAttribute(PersistentUser.ATTRIBUTE_KEY);
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		if (user == null) {
			httpServletResponse.sendRedirect("bce_user_login.jsp");
			return;
		}
		chain.doFilter(request, response);
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
