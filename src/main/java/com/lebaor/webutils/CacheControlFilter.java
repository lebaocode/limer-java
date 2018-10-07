package com.lebaor.webutils;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CacheControlFilter implements Filter {
    private int maxAge = 3600;
    
    public void destroy() {
        
    }

    public void doFilter(ServletRequest request, 
            ServletResponse response, 
            FilterChain chain) throws IOException, ServletException {
        
        if (response instanceof HttpServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            if (maxAge <= 0) {
                httpResponse.addHeader("Pragma", "no-cache");
                httpResponse.addHeader("Cache-Control", "no-cache");
            } else {
                httpResponse.addDateHeader("Expires", System.currentTimeMillis() + maxAge * 1000);
                httpResponse.addHeader("Cache-Control", "max-age=" + maxAge);
            }
        }
        
        chain.doFilter(request, response);
    }

    public void init(FilterConfig conf) throws ServletException {
        String tmp = conf.getInitParameter("max_age");
        if (tmp != null) {
            maxAge = Integer.parseInt(tmp);
        }
    }

}
