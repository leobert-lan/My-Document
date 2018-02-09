package com.lht.pan_android.bean;

import com.lht.pan_android.Interface.IKeyManager;

/**
 * @ClassName: UpLoadDbBead
 * @Description: TODO
 * @date 2015年12月3日 下午4:10:57
 * 
 * @author leobert.lan
 * @version 1.0
 */
public class UpLoadDbBean {

	// * id int，auto primary username text 当前操作者的username local_path
	// * text，unique 本地路径 md5 text 文件md5，最好存一下，也许会存在未知、未考虑问题，需要用到 remote_path
	// * 上传路径 name 最终文件名 Begin_time unix时间戳 End_time unix时间戳 size long
	// * 文件大小，比特值 complete_size long 已完成大小 overwrite Boolean 冲突是否覆盖 Project_id
	// * String 项目id Task_id string 任务id Chunk_size long 分片大小 status {0-wait 1
	// * – transferring 3-complete 2-pause 4-fail}

	private int id;

	private String username;

	private String local_path;

	private String md5;

	private String remote_path;

	private String finalName;

	private long begin_time;

	private long end_time;

	private long file_size;

	private long complete_size;

	private boolean overwrite;

	private String project_id;

	private String task_id;

	private long chunk_size;

	private int status = IKeyManager.UploadDataBaseKey.VALUE_STATUS_WAIT;

	private String contentType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getLocal_path() {
		return local_path;
	}

	public void setLocal_path(String local_path) {
		this.local_path = local_path;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getRemote_path() {
		return remote_path;
	}

	public void setRemote_path(String remote_path) {
		this.remote_path = remote_path;
	}

	public String getFinalName() {
		return finalName;
	}

	public void setFinalName(String finalName) {
		this.finalName = finalName;
	}

	public long getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(long begin_time) {
		this.begin_time = begin_time;
	}

	public long getEnd_time() {
		return end_time;
	}

	public void setEnd_time(long end_time) {
		this.end_time = end_time;
	}

	public long getFile_size() {
		return file_size;
	}

	public void setFile_size(long file_size) {
		this.file_size = file_size;
	}

	public long getComplete_size() {
		return complete_size;
	}

	public void setComplete_size(long complete_size) {
		this.complete_size = complete_size;
	}

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public String getProject_id() {
		return project_id;
	}

	public void setProject_id(String project_id) {
		this.project_id = project_id;
	}

	public String getTask_id() {
		return task_id;
	}

	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}

	public long getChunk_size() {
		return chunk_size;
	}

	public void setChunk_size(long chunk_size) {
		this.chunk_size = chunk_size;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
