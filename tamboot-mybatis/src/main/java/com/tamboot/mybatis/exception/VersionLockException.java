package com.tamboot.mybatis.exception;

public class VersionLockException extends RuntimeException {

	private static final long serialVersionUID = 8641324512595744989L;

	public VersionLockException() {
		super();
	}
	
	public VersionLockException(String message) {
		super(message);
	}
}
