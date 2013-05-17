package com.variance.mimiprotect.util;

public class UploadedContent {

	private String fileName;
	private String fileExt;
	private String filePath;
	private int fileID;

	public int getFileID() {
		return fileID;
	}

	public void setFileID(int userID) {
		this.fileID = userID;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
}
