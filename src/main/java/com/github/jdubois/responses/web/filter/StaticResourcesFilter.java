package com.github.jdubois.responses.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author Julien Dubois
 */
public class StaticResourcesFilter implements Filter {

    private static final int TTL_SECONDS = 60 * 60 * 24 * 7;
    private static final int TTL_MILLISECONDS = 60 * 60 * 24 * 7 * 1000;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpResp = (HttpServletResponse) response;
        Date now = new Date();
        httpResp.setDateHeader("Last-Modified", now.getTime());
        httpResp.setDateHeader("Expires", now.getTime() + TTL_MILLISECONDS);
        httpResp.setHeader("Cache-Control", "max-age=" + TTL_SECONDS);
        chain.doFilter(request, response);
    }

    public void destroy() {
    }
}
