package bce.server.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 用于前后台字符编码转换的过滤器
 */
@WebFilter(filterName = "EncodingSwitchFilter", urlPatterns = { "/*" }, initParams = {
        @WebInitParam(name = "encoding", value = "UTF-8"),
        @WebInitParam(name = "enabled", value = "true") })
public class EncodingSwitchFilter implements Filter {

    protected String encodingName;

    protected FilterConfig encodingSwitchFilterConfig;

    protected boolean enabled;

    Log logger = LogFactory.getLog(EncodingSwitchFilter.class);

    /**
     * Default constructor.
     */
    public EncodingSwitchFilter() {
    }

    /**
     * @see Filter#destroy()
     */
    public void destroy() {
    }

    /**
     * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (this.enabled) {
            // try {
            request.setCharacterEncoding(this.encodingName);
            response.setCharacterEncoding(this.encodingName);
            response.setContentType("text/html;charset=" + this.encodingName);
            /** pass the request along the filter chain */
            chain.doFilter(request, response);
            // } catch (Exception e) {
            //
            // logger.info("字符编码转换过滤器故障！");
            // }
        } else {
            chain.doFilter(request, response);
        }
    }

    /**
     * @see Filter#init(FilterConfig)
     */
    public void init(FilterConfig fConfig) throws ServletException {
        this.encodingSwitchFilterConfig = fConfig;
        this.loadFilterSetting();
    }

    private void loadFilterSetting() {
        this.encodingName = this.encodingSwitchFilterConfig
                .getInitParameter("encoding");
        logger.info("encoding: " + encodingName);
        String enabled = this.encodingSwitchFilterConfig
                .getInitParameter("enabled");
        if (enabled != null && enabled.equals("true")) {
            this.enabled = true;
        } else {
            this.enabled = false;
        }
    }

}
