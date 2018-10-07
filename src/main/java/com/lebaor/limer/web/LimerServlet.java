package com.lebaor.limer.web;

import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.DispatcherServlet;

import com.lebaor.utils.LogFormatter;

public class LimerServlet extends DispatcherServlet {
    
    private static final Logger LOG = LogFormatter.getLogger(LimerServlet.class);
    
    public void init(ServletConfig config) throws ServletException{
        LogFormatter.setShowLoggerNames(true);
        LOG.info("LimerServlet Initializing...");
        String home = config.getServletContext().getRealPath("WEB-INF");
        LOG.info("Home directory="+home);
        System.setProperty("limer.home", home);
        
        super.init(config);
        LOG.info("LimerServlet Initialized Success!");
    }
    
    public void doDispatch(HttpServletRequest req,HttpServletResponse resp) throws Exception{
        super.doDispatch(req, resp);
    }

}
