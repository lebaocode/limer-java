package com.lebaor.limer.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.GenericTemplateController;

public class ErrorPageController extends EntryController{
	private boolean debug = false;
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    public ModelAndView handleRequest(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
        res.setContentType("text/html; charset=utf-8");
        
        Throwable ex = (Throwable) req.getAttribute("javax.servlet.error.exception");
        String uri = (String) req.getAttribute("javax.servlet.error.request_uri");
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("debug", debug);
        if(ex == null) {
            model.put("type", "not_found");
            model.put("request_uri", uri);
            model.put("msg",  "对不起，您找的页面好像不存在哦~");
        } else {
            model.put("type", "exception");
            model.put("exception", ex);
            model.put("msg",  "出错啦~ <br/>可能正在调试，过一会再看看吧~");
            LogUtil.WEB_LOG.warn("Exception", ex);
        }
        setCommonVar(req, res, model);
        return new ModelAndView(viewPath, model);
    }

}
