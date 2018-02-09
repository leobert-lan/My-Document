package com.lht.pan_android.Interface;

/**
 * @ClassName: IThreadPoolManager
 * @Description: 线程池管理通信接口
 * @date 2015年11月20日 上午9:56:04
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IThreadPoolManager {

	// TODO 区分

	/**
	 * corePoolSize:核心线程数量
	 */
	public int CORE_POOL_SIZE = 1;
	/**
	 * maximumPoolSize:最大线程数量
	 */
	public int MAXIMUM_POOL_SIZE = 1;
	/**
	 * keepAliveTime:过载空闲后存活时间 second
	 */
	public int KEEPALIVE_TIME = 1;
	/**
	 * queueSize:初始化队列长度值
	 */
	public int QUEUE_SIZE = 0;

	/**
	 * @ClassName: UpLoadManager
	 * @Description: 上传管理
	 * @date 2015年12月2日 上午9:57:32
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface UpLoadManager {
		/**
		 * corePoolSize:核心线程数量
		 */
		public int CORE_POOL_SIZE = 1;
		/**
		 * maximumPoolSize:最大线程数量
		 */
		public int MAXIMUM_POOL_SIZE = 2;
		/**
		 * keepAliveTime:过载空闲后存活时间 second
		 */
		public int KEEPALIVE_TIME = 1;
		/**
		 * queueSize:初始化队列长度值
		 */
		public int QUEUE_SIZE = 0;
	}

}
