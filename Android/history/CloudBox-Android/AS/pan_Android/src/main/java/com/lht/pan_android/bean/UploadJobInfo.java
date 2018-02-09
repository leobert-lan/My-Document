package com.lht.pan_android.bean;

/**
 * 上传任务item，list元素
 * 
 * @author Leobert.lan
 * 
 */
public class UploadJobInfo {

	private int dbIndex;

	/**
	 * mFinalName:最终命名
	 */
	private String finalName;
	/**
	 * mUploadPath:上传路径
	 */
	private String uploadPath;

	private String username;

	private String localPath;

	/**
	 * mChunks:分片总数
	 */
	private String chunks;

	private long fileSize;
	/**
	 * mMd5:父文件MD5
	 */
	private String Md5;
	/**
	 * isProjectAccess:project文件
	 */
	private boolean isProjectAccess;
	/**
	 * overwrite:是否覆盖
	 */
	private boolean overwrite;

	public int getDbIndex() {
		return dbIndex;
	}

	public void setDbIndex(int dbIndex) {
		this.dbIndex = dbIndex;
	}

	public String getFinalName() {
		return finalName;
	}

	public void setFinalName(String finalName) {
		this.finalName = finalName;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}

	public String getChunks() {
		return chunks;
	}

	public void setChunks(String chunks) {
		this.chunks = chunks;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String getMd5() {
		return Md5;
	}

	public void setMd5(String md5) {
		Md5 = md5;
	}

	public boolean isProjectAccess() {
		return isProjectAccess;
	}

	public void setProjectAccess(boolean isProjectAccess) {
		this.isProjectAccess = isProjectAccess;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

}
