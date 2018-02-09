package com.lht.pan_android.Interface;

import com.lht.pan_android.clazz.Chunk;

/**
 * @ClassName: ISendFileCallBack
 * @Description: 发送文件事件的回调接口集
 * @date 2015年11月20日 下午4:22:30
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface ISendFileCallBack {

	/**
	 * @ClassName: ICheckFileCallBack
	 * @Description: 检验文件存在回调接口
	 * @date 2015年11月20日 下午4:44:13
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface ICheckFileCallBack {
		/**
		 * @Title: OnFileExist
		 * @Description: 文件存在回调事件
		 * @author: leobert.lan
		 */
		void OnFileExist();
	}

	/**
	 * @ClassName: ICheckChunkCallBack
	 * @Description: 检验碎片是否存在
	 * @date 2015年12月1日 上午9:31:39
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface ICheckChunkCallBack {
		void sendStatus(int httpStatus);

		void isChunkExist(Chunk c, boolean isExist);
	}

	/**
	 * @ClassName: ISendChunkCallBack
	 * @Description: 上传碎片回调接口
	 * @date 2015年11月20日 下午4:44:46
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface ISendChunkCallBack {
		/**
		 * @Title: OnUploadSuccess
		 * @Description: 上传成功回调方法
		 * @author: leobert.lan
		 * @param c
		 */
		void OnUploadSuccess(Chunk c);

		/**
		 * @Title: OnUploadFailure
		 * @Description: 上传失败回调方法
		 * @author: leobert.lan
		 * @param c
		 */
		void OnUploadFailure(Chunk c);
	}

	public interface OnFileSendSuccessListener {
		/**
		 * @Title: onSuccess
		 * @Description: 文件上传成功
		 * @author: leobert.lan
		 * @param dbIndex
		 */
		void onSuccess(int dbIndex);

		// void onFailure(int db) 实际情况好像有上传失败的可能，不过接口好像没有说这玩意
	}

	/**
	 * @ClassName: ManageTransport
	 * @Description: 传输任务的控制接口
	 * @date 2015年12月30日 上午9:22:16
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface ManageTransport {

		/**
		 * @Title: onPause
		 * @Description: 暂停任务
		 * @author: leobert.lan
		 * @param dbIndex
		 */
		int onPause(int dbIndex);

		/**
		 * @Title: onStart
		 * @Description: 启动任务
		 * @author: leobert.lan
		 * @param dbIndex
		 */
		int onStart(int dbIndex);

		/**
		 * @Title: onDelete
		 * @Description: 删除任务
		 * @author: leobert.lan
		 * @param dbIndex
		 */
		boolean onDelete(int dbIndex);

		/**
		 * START_SUCCESS:启动成功
		 */
		public static int START_SUCCESS = 0;

		/**
		 * START_ONWAIT:排队中
		 */
		public static int START_ONWAIT = 1;

		/**
		 * START_NONETAVAILABLE:没有网络
		 */
		public static int START_NONETAVAILABLE = 2;

		/**
		 * START_REFUSE_MOBILE:不允许mobile网络
		 */
		public static int START_REFUSE_MOBILE = 3;

		// public static int START_WAIT_MOBILEPERMIT = 4;

		/**
		 * START_FAILURE:统一未启动的
		 */
		// public static int START_FAILURE = 9;

		/**
		 * STOP_SUCCESS：暂停成功
		 */
		public static int STOP_SUCCESS = 0;

		/**
		 * STOP_ONWAIT:暂停 缓存队列中的，不处理
		 */
		public static int STOP_ONWAIT = 1;

		/**
		 * STOP_ASYNC:暂停 特殊情况 已经完成了
		 */
		public static int STOP_ASYNC = 2;

	}
}
