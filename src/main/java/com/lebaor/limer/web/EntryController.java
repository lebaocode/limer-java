package com.lebaor.limer.web;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lebaor.webutils.GenericTemplateController;

public class EntryController extends GenericTemplateController {
	
	protected LebaoCache cache;
	
	public LebaoCache getCache() {
		return cache;
	}
	public void setCache(LebaoCache cache) {
		this.cache = cache;
	}
}
