package com.lebaor.webutils;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.lebaor.dbutils.Resource;

public class TipLoader implements Filter {

    Resource resource;
    String[] keywords;
    
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse res,
            FilterChain filter) throws IOException, ServletException {
        req.setAttribute("resource-bundle", resource);
        filter.doFilter(req, res);
    }

    public void init(FilterConfig config) throws ServletException {
        String resourceConfig = config.getInitParameter("resource-config");
        if(resourceConfig == null) {
            throw new ServletException("Filter MUST has \"resource-config\" attribute");
        }
        
        String base = config.getServletContext().getRealPath("/WEB-INF/");
        Config conf = null;
        try {
            File configFile = (resourceConfig.startsWith("/") || resourceConfig.startsWith("\\")) ?
                    new File(resourceConfig) : new File(base, resourceConfig);
            conf = Config.load(configFile);
        } catch (IOException e) {
            throw new ServletException(e.getMessage());
        }
        resource = new Resource(conf);
    }


}
