package com.lht.pan_android.clazz;

/**
 * @ClassName: SendChunkJobs
 * @Description: 碎片
 * @date 2015年11月20日 下午2:52:27
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class Chunk {
	/**
	 * mChunkIndex:分片号
	 */
	private int chunkIndex;

	/**
	 * mParentMd5:父文件Md5
	 */
	private String parentMd5;

	/**
	 * mChunkKey:缓存key
	 */
	private String chunkKey;

	/**
	 * mCacheName:父文件缓存区名
	 */
	private String cacheName;

	/**
	 * mChunkSize:碎片分块标准
	 */
	private String chunkSize;

	private String chunkCount;

	// private AsyncUploadDetail detail;

	private String uploadPath;

	private String finalName;

	private boolean isProjectAccess;

	private boolean isOverwrite;

	/**
	 * actualSize:碎片的实际大小
	 */
	private long actualSize;

	public int getChunkIndex() {
		return chunkIndex;
	}

	public void setChunkIndex(int chunkIndex) {
		this.chunkIndex = chunkIndex;
	}

	public String getParentMd5() {
		return parentMd5;
	}

	public void setParentMd5(String parentMd5) {
		this.parentMd5 = parentMd5;
	}

	public String getChunkKey() {
		return chunkKey;
	}

	public void setChunkKey(String chunkKey) {
		this.chunkKey = chunkKey;
	}

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public String getChunkSize() {
		return chunkSize;
	}

	public void setChunkSize(String chunkSize) {
		this.chunkSize = chunkSize;
	}

	public String getChunkCount() {
		return chunkCount;
	}

	public void setChunkCount(String chunkCount) {
		this.chunkCount = chunkCount;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}

	public String getFinalName() {
		return finalName;
	}

	public void setFinalName(String finalName) {
		this.finalName = finalName;
	}

	public boolean isProjectAccess() {
		return isProjectAccess;
	}

	public void setProjectAccess(boolean isProjectAccess) {
		this.isProjectAccess = isProjectAccess;
	}

	public boolean isOverwrite() {
		return isOverwrite;
	}

	public void setOverwrite(boolean isOverwrite) {
		this.isOverwrite = isOverwrite;
	}

	public long getActualSize() {
		return actualSize;
	}

	public void setActualSize(long actualSize) {
		this.actualSize = actualSize;
	}

}
