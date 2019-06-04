package com.tamboot.common.utils;

public class UrlUtils {
	public static HostAndPort resolveHostAndPort(String url) {
		if (url == null) {
			return null;
		}
		
		String[] splited = url.split(":");
		HostAndPort hostAndPort = new HostAndPort();
		hostAndPort.setHost(splited[0].trim());
		
		if (splited.length >= 2) {
			try {
				hostAndPort.setPort(Integer.parseInt(splited[1].trim()));
			} catch (NumberFormatException e) {
			}
		}
		
		return hostAndPort;
	}
	
	
	public static class HostAndPort {
		private String host;
		
		private Integer port;

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}
	}
}
