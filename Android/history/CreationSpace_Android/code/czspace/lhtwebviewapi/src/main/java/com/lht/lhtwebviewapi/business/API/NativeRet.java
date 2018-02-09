package com.lht.lhtwebviewapi.business.API;

/**
 * @ClassName: NativeRet
 * @Description: TODO
 * @date 2016年2月23日 上午10:17:01
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface NativeRet {

	/**
	 * RET_SUCCESS:调用成功
	 */
	@Deprecated
	int RET_SUCCESS = 10000;
	
	/**
	 * RET_ERROR_FUNCTION_NOTFOUND:未注册的方法
	 */
	int RET_ERROR_FUNCTION_NOTFOUND = 11001;
	
	/**
	 * RET_ERROR_ESSENTIALARGUEMENT_MISS:必须参数缺失
	 */
	int RET_ERROR_ESSENTIALARGUEMENT_MISS = 12001;
	
	/**
	 * RET_ERROR_JSONSTRUCTURE:json结构错误
	 */
	int RET_ERROR_JSONSTRUCTURE = 12002;

	interface NativeGpsRet extends NativeRet {

		public int RET_FAILURE = 21000;
		// ...
	}

	interface NativeCallTelRet extends NativeRet {
		public int RET_FAILURE_PERMISSION = 26001;
	}

	interface NativeCopyToClipBorad extends NativeRet {
		/**
		 * RET_FAILURE:复制到剪切板内容为空
		 */
		public int RET_ERROR_CONTENTNULL = -1;
	}

	interface NativeGetClipBoard extends NativeRet {
		/**
		 * RET_FAILURE:获取剪切板内容失败
		 */
		public int RET_GETCLIPBORAD_FAILURE = 42000;
	}

	interface NativeSendMessage extends NativeRet {
		/**
		 * RET_CONTACTS_NULL:手机号为空
		 */
		public int RET_CONTACTS_NULL = 41001;
		
		/**
		 * RET_CONTENT_NULL:短信内容为空
		 */
		public int RET_CONTENT_NULL = 41002;
	}

	interface NativeSendEmail extends NativeRet {
		/**
		 * RET_ADDRESSES_NULL:Email地址为空
		 */
		public int RET_ADDRESSES_NULL = 51001;
		
		/**
		 * RET_MESSAGE_NULL:Email内容为空
		 */
		public int RET_MESSAGE_NULL = 51002;
		/**
		 * RET_FORMAT_FAILURE:Email格式错误
		 */
		public int RET_FORMAT_FAILURE = 51004;
	}

	interface NativeScanCodeRet extends NativeRet {

		/**
		 * RET_UNSUPPORT:不支持的码型
		 */
		public int RET_UNSUPPORT = 50001;

		/**
		 * ret_timeout:扫描超时
		 */
		public int RET_TIMEOUT = 50002;
		
		/**
		 * RET_CANCEL:用户取消
		 */
		public int RET_CANCEL = 50003;

	}
}
