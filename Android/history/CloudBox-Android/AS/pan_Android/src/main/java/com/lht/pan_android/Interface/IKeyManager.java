package com.lht.pan_android.Interface;

/**
 * @ClassName: IKeyManager
 * @Description: TODO
 * @date 2015年11月25日 上午11:21:41
 * 
 * @author leobert.lan
 * @version 1.0
 */
public interface IKeyManager {
	interface Debug {
		public boolean DEBUG_MODE = false;
	}

	interface Ads {
		public final String KEY_ADID = "adsId";
	}

	/**
	 * @ClassName: Token
	 * @Description: 管理保存登录验证后核心数据的sp 的名称 和key
	 * @date 2015年11月25日 上午11:26:00
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface Token {

		public String SP_TOKEN = "sp_token";

		public String KEY_ACCESS_ID = "access_id";

		public String KEY_ACCESS_TOKEN = "access_token";

		public String KEY_USERNAME = "username";

	}

	interface Timer {
		public String SP_TIMER = "sp_timer";

		public String KEY_VERIFICATE = "timestamp_";
	}

	/**
	 * @ClassName: ShareInfo
	 * @Description: 分享的接口参数
	 * @date 2016年1月21日 下午3:08:34
	 * 
	 * @author zhangbin
	 * @version 1.0
	 */
	interface ShareInfo {

		public String PARAM_PATH = "path";

		public String PARAM_PAGE = "page";

		public String PARAM_PAGESIZE = "pagesize";

		public String contentType = "";

		/**
		 * TYPE_PUBLIC:0：公开分享
		 */
		public String SHARE_PUBLIC = "0";

		/**
		 * SHARE_PRIVATE:1:朋友分享
		 */
		public String SHARE_FRIEND = "1";

		/**
		 * SHARE_SECRET:2：未知
		 */
		public String SHARE_UNKNOW = "2";
		/**
		 * SHARE_SECRET:3：私密分享
		 */
		public String SHARE_PRIVATE = "3";
		/**
		 * TYPE_FILE:”0“是文件
		 */
		public String TYPE_FILE = "0";
		/**
		 * TYPE_FOLDER:"1"是文件夹
		 */
		public String TYPE_FOLDER = "1";
		/**
		 * TYPE_ALL:"2"是文件和文件夹
		 */
		public String TYPE_ALL = "2";
	}

	/**
	 * @ClassName: SMSProps
	 * @Description: 短信配置
	 * @date 2016年1月12日 下午2:13:26
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface SMSProps {
		public static String PHONENO_CT = "1069043428";// 2016-4-22 15:14:27升级
														// 电信

		public static String PHONENO_CMCC = "1069075873020";// 移动

		public static String PHONENO_CU = "1065502007311854274";// 联通

		public static int CODELENGTH = 6;
	}

	interface SettingKey {
		public String SETTING_KEY_ONLYTRANSONWIFI = "OnlyTransOnwifi";
	}

	interface SearchList {

		/**
		 * NAME:请求的名字
		 */
		public String NAME = "name";
		/**
		 * PARAM_PATH:请求的路径
		 */
		public String PARAM_PATH = "path";

		/**
		 * PARAM_TYPE:需要数据的类型
		 */
		public String PARAM_TYPE = "type";
		/**
		 * TYPE_FILE:类型为：文件
		 */
		public String TYPE_FILE = "0";
		/**
		 * TYPE_FOLDER:类型为：文件夹
		 */
		public String TYPE_FOLDER = "1";
		/**
		 * TYPE_ALL:类型为：全部 ——默认项
		 */
		public String TYPE_ALL = "2";
		/**
		 * PARAM_PAGE:分页控制第几页,start from 1
		 */
		public String PARAM_PAGE = "page";
		/**
		 * PARAM_PAGESIZE:分页控制分页大小， 默认20
		 */
		public String PARAM_PAGESIZE = "pagesize";

		/**
		 * PARAM_DESCENDANTFILES:是否获取数量
		 */
		public String PARAM_DESCENDANTFILES = "descendantFiles";
	}

	interface UserFolderList {

		/**
		 * NAME:请求的名字
		 */
		public String NAME = "name";
		/**
		 * PARAM_PATH:请求的路径
		 */
		public String PARAM_PATH = "path";

		/**
		 * PARAM_TYPE:需要数据的类型
		 */
		public String PARAM_TYPE = "type";
		/**
		 * TYPE_FILE:类型为：文件
		 */
		public String TYPE_FILE = "0";
		/**
		 * TYPE_FOLDER:类型为：文件夹
		 */
		public String TYPE_FOLDER = "1";
		/**
		 * TYPE_ALL:类型为：全部 ——默认项
		 */
		public String TYPE_ALL = "2";
		/**
		 * PARAM_PAGE:分页控制第几页,start from 1
		 */
		public String PARAM_PAGE = "page";
		/**
		 * PARAM_PAGESIZE:分页控制分页大小， 默认20
		 */
		public String PARAM_PAGESIZE = "pagesize";

		/**
		 * PARAM_DESCENDANTFILES:是否获取数量
		 */
		public String PARAM_DESCENDANTFILES = "descendantFiles";

		public String DEFAULT_VALUE_DESCENDANTFILES = "false";
	}

	interface FileCombine {
		/**
		 * PARAM_USERNAME:用户名
		 */
		public String PARAM_USERNAME = "username";
		/**
		 * PARAM_UPLOADPATH:上传路径
		 */
		public String PARAM_UPLOADPATH = "path";
		/**
		 * PARAM_CHUNKSIZE:分片大小
		 */
		public String PARAM_CHUNKSIZE = "chunkSize";
		/**
		 * PARAM_CHUNKCOUNT:分片数量
		 */
		public String PARAM_CHUNKCOUNT = "chunks";
		/**
		 * PARAM_MD5:全文件Md5
		 */
		public String PARAM_MD5 = "md5";
		/**
		 * PARAM_FINALNAME:最终命名
		 */
		public String PARAM_FINALNAME = "name";
		/**
		 * PARAM_PROJECTACCESS:是否为团队共享文件
		 */
		public String PARAM_PROJECTACCESS = "isProjectAccess";
		/**
		 * PARAM_OVERWRITE:命名冲突是否覆盖
		 */
		public String PARAM_OVERWRITE = "overwrite";

		/**
		 * VALUE_TRUE:表示：是
		 */
		public String VALUE_TRUE = "1";
		/**
		 * VALUE_FALSE:表示：否
		 */
		public String VALUE_FALSE = "0";

	}

	@Deprecated
	interface SelectImagesData {
		public String LIST_DATA = "list";

		public String SELECT_ITEMS = "select";
	}

	/**
	 * @ClassName: UploadDataBaseKey
	 * @Description: 上传数据库的key
	 * @date 2015年12月3日 下午3:04:24
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface UploadDataBaseKey {
		/*
		 * id int，auto primary username text 当前操作者的username local_path
		 * text，unique 本地路径 md5 text 文件md5，最好存一下，也许会存在未知、未考虑问题，需要用到 remote_path
		 * 上传路径 name 最终文件名 Begin_time unix时间戳 End_time unix时间戳 size long
		 * 文件大小，比特值 complete_size long 已完成大小 overwrite Boolean 冲突是否覆盖 Project_id
		 * String 项目id Task_id string 任务id Chunk_size long 分片大小 status {0-wait 1
		 * – transferring 3-complete 2-pause 4-fail}
		 */

		/**
		 * UPLOAD_DATABASE_NAME:数据库名
		 */
		public String UPLOAD_DATABASE_NAME = "db_pan_upload.db";

		/**
		 * UPLOAD_TABLE_NAME:表名
		 */
		public String UPLOAD_TABLE_NAME = "table_pan_upload";

		public int VERSION = 1;

		/**
		 * KEY_ID:id auto primary
		 */
		public String KEY_ID = "_id";

		/**
		 * KEY_USERNAME:操作者username
		 */
		public String KEY_USERNAME = "username";

		/**
		 * KEY_LOCAL_PATH:上传文件本机路径
		 */
		public String KEY_LOCAL_PATH = "local_path";

		/**
		 * KEY_MD5:上传文件MD5
		 */
		public String KEY_MD5 = "md5";

		/**
		 * KEY_REMOTE_PATH:上传路径
		 */
		public String KEY_REMOTE_PATH = "remote_path";

		/**
		 * KEY_FINAL_NAME:最终命名
		 */
		public String KEY_FINAL_NAME = "final_name";

		/**
		 * KEY_BEGIN_TIME:添加任务的时间
		 */
		public String KEY_BEGIN_TIME = "begin_time";

		/**
		 * KEY_END_TIME:任务结束的时间
		 */
		public String KEY_END_TIME = "end_time";

		/**
		 * KEY_FILE_SIZE:文件总大小 byte
		 */
		public String KEY_FILE_SIZE = "size";

		/**
		 * KEY_COMPLETE_SIZE:完成大小 byte
		 */
		public String KEY_COMPLETE_SIZE = "complete_size";

		/**
		 * KEY_OVERWRITE:覆盖否
		 */
		public String KEY_OVERWRITE = "overwrite";

		/**
		 * KEY_PROJECT_ID:项目路径的项目号
		 */
		public String KEY_PROJECT_ID = "project_id";

		/**
		 * KEY_TASK_ID:项目路径的 任务号
		 */
		public String KEY_TASK_ID = "task_id";

		/**
		 * KEY_CHUNK_SIZE:上传任务分片大小
		 */
		public String KEY_CHUNK_SIZE = "chunk_size";

		/**
		 * KEY_STATUS:任务状态
		 */
		public String KEY_STATUS = "status";

		/**
		 * KEY_CONTENTTYPE:上传文件类型
		 */
		public String KEY_CONTENTTYPE = "contenttype";

		// status {0-wait 1
		// * – transferring 3-complete 2-pause 4-fail}
		/**
		 * VALUE_STATUS_WAIT:状态值：等待
		 */
		public int VALUE_STATUS_WAIT = 0;

		/**
		 * VALUE_STATUS_UPLOADING:状态值：上传中
		 */
		public int VALUE_STATUS_UPLOADING = 1;

		/**
		 * VALUE_STATUS_PAUSE:状态值：暂停
		 */
		public int VALUE_STATUS_PAUSE = 2;

		/**
		 * VALUE_STATUS_COMPLETE:状态值：已完成
		 */
		public int VALUE_STATUS_COMPLETE = 3;

		/**
		 * VALUE_STATUS_FAIL:状态值：失败
		 */
		public int VALUE_STATUS_FAIL = 4;

		/**
		 * VALUE_STATUS_FAIL_FILEDELETED:失败情况：本地文件删除
		 */
		public int VALUE_STATUS_FAIL_FILEDELETED = 5;

	}

	/**
	 * @ClassName: downLoadDataBaseKey
	 * @Description: 下载数据库
	 * @date 2015年12月7日 上午8:34:39
	 * 
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	interface DownLoadDataBaseKey {

		/**
		 * downLoad_Database_name:数据库名
		 */
		public String DOWNLOAD_DATABASE_NAME = "db_pan_download.db";
		/**
		 * DOWNLOAD_TABLE_NAME:表名
		 */
		public String DOWNLOAD_TABLE_NAME = "table_pan_download";

		/**
		 * DOWNLOAD_VERSION:数据库版本
		 */
		public int DOWNLOAD_VERSION = 1;

		/**
		 * DOWNLOAD_KEY_ID:id auto primary
		 */
		public String DOWNLOAD_KEY_ID = "_id";

		/**
		 * DOWNLOAD_KEY_USERNAME:用户名username
		 */
		public String DOWNLOAD_KEY_USERNAME = "username";

		/**
		 * DOWNLOAD_KEY_LOCAL_PATH:下载的本地路径
		 */
		public String DOWNLOAD_KEY_LOCAL_PATH = "local_path";

		/**
		 * DOWNLOAD_KEY_REMOTE_PATH:下载的远程路径
		 */
		public String DOWNLOAD_KEY_REMOTE_PATH = "remote_path";

		/**
		 * XOWNLOAD_KEY_BEGIN_TIME:用处待定
		 */
		public String DOWNLOAD_KEY_BEGIN_TIME = "begin_time";

		/**
		 * DOWNLOAD_KEY_END_TIME:用处待定
		 */
		public String DOWNLOAD_KEY_END_TIME = "end_time";

		/**
		 * DOWNLOAD_KEY_FILE_SIZE:文件大小
		 */
		public String DOWNLOAD_KEY_FILE_SIZE = "size";

		/**
		 * DOWNLOAD_KEY_COMPLETE_SIZE:已完成下载的多少
		 */
		public String DOWNLOAD_KEY_COMPLETE_SIZE = "complete_size";

		/**
		 * DOWNLOAD_KEY_STATUS:下载的状态
		 */
		public String DOWNLOAD_KEY_STATUS = "status";

		/**
		 * DOWNLOAD_KEY_ICON:缩略图
		 */
		public String DOWNLOAD_KEY_ICON = "icon";

		// /**
		// * DOWNLOAD_KEY_SHAREID:分享id
		// */
		// public String DOWNLOAD_KEY_SHAREID = "shareId";
		//
		// /**
		// * DOWNLOAD_KEY_OWNER:分享所有者
		// */
		// public String DOWNLOAD_KEY_OWNER = "owner";
		//
		// /**
		// * DOWNLOAD_KEY_TYPE:区分常规下载还是分享下载
		// */
		// public String DOWNLOAD_KEY_TYPE = "type";

		/**
		 * VALUE_STATUS_WAIT:状态值：等待
		 */
		public int VALUE_STATUS_WAIT = 0;

		/**
		 * VALUE_STATUS_UPLOADING:状态值：上传中
		 */
		public int VALUE_STATUS_DOWNLOADING = 1;

		/**
		 * VALUE_STATUS_PAUSE:状态值：暂停
		 */
		public int VALUE_STATUS_PAUSE = 2;

		/**
		 * VALUE_STATUS_COMPLETE:状态值：已完成
		 */
		public int VALUE_STATUS_COMPLETE = 3;

		/**
		 * VALUE_STATUS_FAIL:状态值：失败
		 */
		public int VALUE_STATUS_FAIL = 4;

		/**
		 * VALUE_STATUS_FAIL_FILEDELETED:失败-服务端已删除
		 */
		public int VALUE_STATUS_FAIL_FILEDELETED = 5;

		/**
		 * VALUE_STATUS_FAIL_SERVERERR:下载-服务端异常
		 */
		public int VALUE_STATUS_FAIL_SERVERERR = 6;
	}

	/**
	 * @ClassName: ContentType
	 * @Description: 文件列表——文件类型
	 * @date 2015年12月8日 下午1:26:06
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface ContentType {
		public String IMAGE = "image";

		public String VIDEO = "video";
	}

	/**
	 * @ClassName: BroadCastProps
	 * @Description: 广播配置
	 * @date 2015年12月8日 下午1:28:31
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface BroadCastProps {
		/**
		 * UPLOAD_BROADCAST_ACTION:更新进度广播action
		 */
		public String UPLOAD_BROADCAST_ACTION = "com.lht.pan.upload.updateprogress";

		public String DOWNLOAD_BROADCAST_ACTION = "com.lht.pan.download.updateprogress";

		public String UPLOAD_BROADCAST_MESSAGE = "message";

		public String DOWNLOAD_BROADCAST_MESSAGE = "message2";
	}

	interface ShareContractSp {
		public String SP_SUFFIX = "Contract_";

		public String KEY_CONTENT = "content";
	}

	/**
	 * @ClassName: SearchHistroySp
	 * @Description: 搜索历史
	 * @date 2016年4月22日 下午1:15:16
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface SearchHistroySp {
		public String SP_SUFFIX = "SearchHistroy_";

		public String KEY_CONTENT = "content";

		public int COUNT = 5;
	}

	/**
	 * @ClassName: VersionSp
	 * @Description: 缓存服务器版本等信息
	 * @date 2016年2月29日 上午9:45:43
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface VersionSp {
		public String SP_VERSION = "version_info";

		/**
		 * KEY_CHECKEDSAVE:保存
		 */
		public String KEY_CHECKEDSAVE = "checked_save";

		/**
		 * KEY_IGNORED:该版本是否已经忽略
		 */
		public String KEY_IGNORED = "ignored";
	}

}
