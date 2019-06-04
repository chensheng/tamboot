package com.tamboot.security.core;

import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmptyRequestCache implements RequestCache {

	@Override
	public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
	}

	@Override
	public SavedRequest getRequest(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	@Override
	public HttpServletRequest getMatchingRequest(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}

	@Override
	public void removeRequest(HttpServletRequest request, HttpServletResponse response) {
	}

}
