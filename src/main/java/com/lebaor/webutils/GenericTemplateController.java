package com.lebaor.webutils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.wx.WxConstants;
import com.lebaor.wx.WxOAuthUtil;


public abstract class GenericTemplateController implements Controller {
    protected String viewPath;
    protected String domain;
    protected ViewUtils viewUtils = ViewUtils.getInstance();
    protected TextUtil textUtils = TextUtil.getInstance();
            
    protected String defaultURLEncoding = "UTF-8";
    protected String tomcatURLEncoding = "ISO-8859-1";
    
    protected static String SEND_REDIRECT = "send_redirect";
    protected static String REDIRECT_PATH = "redirect_path";
    protected static String VIEW_PATH = "view_path";
    protected static String JSON = "json";
    protected static String TEXT = "return_text";
    protected static String XML = "return_xml";
    
    
    public void setViewPath(String viewPath) {
        this.viewPath = viewPath;
    }
    public String getViewPath() {
        return viewPath;
    }
    
    public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	    
    protected void doLog(HttpServletRequest req,
            HttpServletResponse res){
        try {
                        
        } catch (Throwable e) {
            //ignore
        }
    }
    
    public String getUserIp(HttpServletRequest req) {
    	return req.getRemoteAddr();
    }
    
    public void setDefaultURLEncoding(String encoding) {
        defaultURLEncoding = encoding;
    }
    public void setTomcatURLEncoding(String encoding) {
        tomcatURLEncoding = encoding;
    }
    protected String redecode(String input)  {
    	try {
    		return redecode(input, this.defaultURLEncoding);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
    	return input;
    }
    
    protected String redecode(String input, String encoding)  {
    	try {
	        if(input == null) return null;
	        if(encoding == null) {
	            encoding = defaultURLEncoding;
	        }
	        
	        if (encoding.equalsIgnoreCase(tomcatURLEncoding)){
	            return input;
	        }
	        
	        String newString;
	        try {
	            newString = new String(input.getBytes(tomcatURLEncoding), encoding);
	        } catch(UnsupportedEncodingException e) {
	            newString = new String(input.getBytes(tomcatURLEncoding), defaultURLEncoding);
	        }
	        return newString;
	        
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	return input;
    }
    
    protected void setRetText(HashMap<String, Object> model, String text) {
    	model.put(TEXT, text);
    }
    
    protected void setRetXml(HashMap<String, Object> model, String xml) {
    	model.put(XML, xml);
    }
    
    protected void setRetJson(HashMap<String, Object> model, String json) {
    	model.put(JSON, json);
    }
    
    protected void setRedirectFlag(HashMap<String, Object> model) {
    	model.put(SEND_REDIRECT, "true");
    }
    
    protected boolean hasRedirectFlag(HashMap<String, Object> model) {
    	String s = (String)model.get(SEND_REDIRECT);
    	return s != null && s.equalsIgnoreCase("true");
    }
    
    protected void setRedirectPath(HashMap<String, Object> model, String redirectPath) {
    	this.setRedirectFlag(model);
    	model.put(REDIRECT_PATH, redirectPath);
    }
    
    protected String getRedirectPath(HashMap<String, Object> model) {
    	String s = (String)model.get(REDIRECT_PATH);
    	return s;
    }
    
    protected static final String FLAG_DEBUG = "lbtn";
    @SuppressWarnings("unchecked")
    protected void setCommonVar(HttpServletRequest req, HttpServletResponse res,
            HashMap<String, Object> model)throws Exception{
    	res.setContentType("text/html; charset=UTF-8");
    	
    	model.put("curTime", System.currentTimeMillis());
    	model.put("curYear", String.format("%1$tY", System.currentTimeMillis()));
        String userAgent = req.getHeader("User-Agent");
        if(userAgent != null) {
            int pos = 0;
            if((pos = userAgent.indexOf("MSIE")) != -1) {
                 model.put("userAgent", "ie");
                 String[] split = userAgent.substring(pos).split("[ ;/]+");
                 if(split.length > 1) {
                     model.put("userAgentVersion", split[1]);
                 }
            } else if((pos = userAgent.indexOf("Firefox")) != -1) {
                 model.put("userAgent", "firefox");
                 String[] split = userAgent.substring(pos).split("[ ;/]+");
                 if(split.length > 1) {
                     model.put("userAgentVersion", split[1]);
                 }
            } else {
                 model.put("userAgent", "other");
            }
        }
        
        Map<String, Object> parameters = req.getParameterMap();
        //CookieUtil.setViewModel(req,model);TODO
        String ue = req.getParameter("ue");
        for(String key : parameters.keySet()) {
            model.put(key, redecode(req.getParameter(key), ue));
        }

        
        model.put("viewUtils", viewUtils);
        model.put("textUtils", textUtils);
        model.put("host", getHostFromReq(req));

        //debug
        String reqIp = req.getRemoteAddr();
        model.put("reqIp", reqIp);
        
        model.put("viewPath", this.viewPath);
        
    }
    
    protected String getHostFromReq(HttpServletRequest req){
        if (req.getServerName() == null || req.getServerName().trim().length() == 0){
            return "";
        }
        return "http://"+req.getServerName();
    }
    
    protected boolean hasFlag(HttpServletRequest req,
            boolean isAllowedIp, String type){
        String flag = req.getParameter("flag");
        if(flag != null && flag.trim().length() > 0 && isAllowedIp) {
            String[] flags = flag.split(" +");
            for(String f : flags) {
                if(f.equalsIgnoreCase(type)) {
                    return true;
                } 
            }
        }
        return false;
    }
    
    protected String getOriginalQ(HttpServletRequest req, HttpServletResponse res,
            HashMap<String, Object> model, boolean debug) throws Exception{
        //the query string
        String ue = req.getParameter("ue"); // URL encoding
        String q = req.getParameter("q");
        LogUtil.WEB_LOG.debug("after getparameter: ["+ q+"] ["+ ue+"]");
        
        q = redecode(q, ue);//default should encode.
        LogUtil.WEB_LOG.debug("after redecode: ["+ q+"] ["+ ue+"]");
        
        if (q != null){
            model.put("originalQuery", q);
        }
        return q;
    }
    
    
    protected String getParameterValue(HttpServletRequest req, String paraName, String defaultValue) {
    	return this.getParameterValue(req, paraName, defaultValue, true);
    }
    
    protected boolean getBooleanParameterValue(HttpServletRequest req, String paraName, boolean defaultValue) {
    	String value = req.getParameter(paraName);
    	if (value == null) return defaultValue;
    	
    	if (value.trim().equalsIgnoreCase("true")) {
    		return true;
    	}  else if (value.trim().equalsIgnoreCase("false")) {
    		return false;
    	} else return defaultValue;
    }
    
    protected int getIntParameterValue(HttpServletRequest req, String paraName, int defaultValue) {
    	String value = req.getParameter(paraName);
    	if (value == null) return defaultValue;
    	
    	try {
    		return Integer.parseInt(value.trim());
    	} catch (Exception e) {}
    	return defaultValue;
    }
    
    protected long getLongParameterValue(HttpServletRequest req, String paraName,long defaultValue) {
    	String value = req.getParameter(paraName);
    	if (value == null) return defaultValue;
    	
    	try {
    		return Long.parseLong(value.trim());
    	} catch (Exception e) {}
    	return defaultValue;
    }
    
    protected String getParameterValue(HttpServletRequest req, String paraName, String defaultValue, 
            boolean redecode) {
        String s = req.getParameter(paraName);
        if (s == null) {
            return defaultValue;
        } else {
            if (redecode) {
                try {
                    return redecode(s, req.getParameter("ue"));
                } catch (Exception e) {
                    return s;
                }
            } else {
                return s;
            }
        }
    }
    
    private boolean fromWeixin(HttpServletRequest req) throws Exception {
		String ua = req.getHeader("User-Agent");
		if (ua != null && ua.toLowerCase().indexOf("micromessenger") != -1) {
			return true;
		}
		return false;
	}
    
    public ModelAndView handleRequest(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
    	LogUtil.info("begin handleRequest...");
		res.setContentType("text/html; charset=utf-8");
		HashMap<String, Object> model = new HashMap<String, Object>();
		setCommonVar(req, res, model);
		
		handleRequestInternal(req, res, model);
		
		LogUtil.info("end handleRequest...");
		
		if (this.hasRedirectFlag(model)) {
			String path = this.getRedirectPath(model);
			res.sendRedirect(path);
			return null;
		} else if (model.get(JSON) != null) {
			String json = (String)model.get(JSON);
			LogUtil.WEB_LOG.debug("json=" + json.substring(0, Math.min(500, json.length())));
			res.setContentType("application/json;charset=utf-8");
			res.setCharacterEncoding("utf-8");
			res.setStatus(200);
			res.getWriter().write(json);
			res.getWriter().flush();
			return null;
		} else if (model.get(TEXT) != null) {
			String text = (String)model.get(TEXT);
			LogUtil.WEB_LOG.debug("text=" + text.substring(0, Math.min(500, text.length())));
			res.setContentType("text/html;charset=utf-8");
			res.setCharacterEncoding("utf-8");
			res.setStatus(200);
			res.getWriter().write(text);
			res.getWriter().flush();
			return null;
		} else if (model.get(XML) != null) {
			String text = (String)model.get(XML);
			LogUtil.WEB_LOG.debug("xml=" + text.substring(0, Math.min(500, text.length())));
			res.setContentType("text/xml;charset=utf-8");
			res.setCharacterEncoding("utf-8");
			res.setStatus(200);
			res.getWriter().write(text);
			res.getWriter().flush();
			return null;
		} else {
			if (model.get(VIEW_PATH) != null) {
				return new ModelAndView((String)model.get(VIEW_PATH), model);	
			} else {
				return new ModelAndView(viewPath, model);
			}
		}
	}


	public void handleRequestInternal(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		//TODO
		LogUtil.WEB_LOG.debug("here");
	}
	
	

}
