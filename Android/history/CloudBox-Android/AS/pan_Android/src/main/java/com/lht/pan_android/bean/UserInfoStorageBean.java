package com.lht.pan_android.bean;

/**
 * @ClassName: UserInfoBean
 * @Description: UserInfo的实体类
 * @date 2015年11月26日 上午8:41:18
 * 
 * @author zhang
 * @version 1.0
 * @since JDK 1.6
 */
public class UserInfoStorageBean {

	private String username;
	private Storage storage;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Storage getStorage() {
		return storage;
	}

	public void setStorage(Storage storage) {
		this.storage = storage;
	}

	public class Storage {

		private String used;

		private String quota;

		public String getUsed() {
			return used;
		}

		public void setUsed(String used) {
			this.used = used;
		}

		public String getQuota() {
			return quota;
		}

		public void setQuota(String quota) {
			this.quota = quota;
		}
	}
}
