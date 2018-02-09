package com.lht.pan_android.Interface;

/**
 * @ClassName: IUrlManager
 * @Description: web接口地址管理
 * @date 2015年11月24日 下午6:24:35
 * @author leobert.lan
 * @version 1.0
 * 
 * @see ** pay attention! *<br>
 *      <li>login
 *      <li>logout
 *      <li>checktoken
 *      <li>userinfo
 *      <li>share相关 <b>使用address_v3</b></br>
 *      </br>
 *      </br>
 *      <li>filedelete
 *      <li>download_cb
 *      <li>imagepreview
 *      <li>search
 *      <li>userlist
 *      <li>delete
 *      <li>rename
 *      <li>createnewfolder
 *      <li>move<b>使用address_v3sub</b></br>
 *      </br>
 *      <li>share开头的</br>
 *      <li>searchuseronplatform <b>使用address_v3</b>
 * 
 * 
 * 
 */
public interface IUrlManager {

	public String LOGOUT_CHECK = "https://cbs.vsochina.com/v3/logout";

	public String DOMAIN = "https://cbs.vsochina.com/";

	// /**
	// * TODO 测试环境
	// */
	// public String _V3_TESTDOMAIN = "http://192.168.1.16:3000/";
	//
	// public String _STROAGE_TESTDOMAIN = "http://192.168.1.16:3100/";
	//
	// public String DOMAIN_V3 = _V3_TESTDOMAIN;
	//
	// public String DOMAIN_V3SUB = _STROAGE_TESTDOMAIN;
	//
	// public String ADDRESS_V3 = "users/";
	//
	// public String ADDRESS_V3SUB = "users/";
	// ==================

	public String DOMAIN_V3 = "https://cbs.vsochina.com/";

	public String DOMAIN_V3SUB = "https://cbs.vsochina.com/";

	public String ADDRESS_V3 = "v3/users/";

	public String ADDRESS_V3SUB = "storage/users/";

	/**
	 * @ClassName: CheckToken
	 * @Description: 登录状态检验 get方式 V3 https://cbs.vsochina.com/v3/loginstatus
	 * 
	 *               params:username,access_token,access_id
	 * 
	 * @date 2015年12月24日 下午2:28:28
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	interface CheckToken {

		public String DOMAIN = IUrlManager.DOMAIN_V3;

		public String FUNCTION = "v3/loginstatus";

		public String USERNAME = "?username=";

		public String ACCESS_TOKEN = "&access_token=";

		public String ACCESS_ID = "&access_id=";
	}

	/**
	 * @ClassName: VirtualApplication
	 * @Description: GET v3sub
	 *               https://cbs.vsochina.com/storage/users/{{username}
	 *               }/virtualappurl ?path=xxxx&width=xxx&height
	 *               =xxx&access_id=xxx&access_token=xxxx :username:用户名
	 *               path:文件绝对路径 width：虚拟应用屏幕宽度 height：虚拟应用屏幕高度
	 * @date 2016年4月13日 下午12:57:02
	 * 
	 * @author leobert.lan
	 */
	interface VirtualApplication {
		public String DOMAIN = IUrlManager.DOMAIN_V3SUB + "storage/";

		// // TODO test
		//
		// public String DOMAIN = IUrlManager.DOMAIN_V3SUB;

		public String USERS = "users/";

		public String FUNCTION = "/virtualappurl?";
	}

	/**
	 * @ClassName: LoginUrl
	 * @Description: 登录接口 post https://cbs.vsochina.com/v3/login params：
	 *               (x-www-…) username,password,appkey
	 * 
	 * @date 2016年4月13日 下午1:47:31
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface LoginUrl {
		public String DOMAIN = IUrlManager.DOMAIN_V3;

		// TODO
		public String LOGIN_CHECK = "v3/login";

		public String APPKEY = "YPMB";

		public String KEY_USERNAME = "username";

		public String KEY_PASSWORD = "password";

		public String KEY_APPKEY = "appkey";
	}

	/**
	 * @ClassName: ShareUrl
	 * @Description: 分享相关接口****************************************************
	 *               <li>我的分享列表 get方式 v3
	 *               https://cbs.vsochina.com/v3/users/{{username}}/share
	 * 
	 *               params: page,pagesize,access_id,access_token
	 * 
	 *               <li>分享给我的列表 get方式 v3
	 *               https://cbs.vsochina.com/v3/users/{{username}}/sharedtome
	 * 
	 *               params: page,pagesize,access_id,access_token
	 * 
	 *               <li>分享给我的中文件夹的内容列表获取接口 get方式
	 *               https://cbs.vsochina.com/v3/users/{
	 *               {username_owner}}/share/{{shareId}}/list
	 * 
	 *               params:path,page,pagesize,queryUsername,access_token,
	 *               access_id
	 * @date 2016年4月13日 下午2:10:31
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface ShareUrl {
		public String DOMAIN = IUrlManager.DOMAIN_V3;
		public String MINEADDRESS = IUrlManager.ADDRESS_V3;
		public String MYSHAREFUNCTION = "/share?";
		public String SHARETOMEFUNCTION = "/sharedtome?";
	}

	/**
	 * @ClassName: SharePublicAndPrivateUrl
	 * @Description: 链接形式分享接口***********************************************
	 *               <br>
	 *               <li>公开分享 POST v3 https://
	 *               cbs.vsochina.com/v3/users/{{username}}/share/link/public
	 *               ?access_token=xxxx&access_id=xxxxx
	 * 
	 *               <br>
	 *               params: entity json {“path”:”/”,”permission”:”read”} <br>
	 *               <b>permission：操作权限 read：为只读（默认）all：为完全控制</b>
	 *               <li>私密分享 POST v3 https://cbs
	 *               .vsochina.com/v3/users/{{username}}/share/link/private
	 *               ?access_token=xxxx&access_id=xxxxx
	 * 
	 *               <br>
	 *               params: entity json
	 *               {“path”:””,”permission”:””,”password”:””} <br>
	 *               <b>path：分享路径（文件 或 文件夹） permission：操作权限 （all：为完全控制 read：为只读）
	 *               password：分享密码</b>
	 * 
	 *               <br>
	 *               <li>私密分享修改密码 POST v3 https://cbs.vsochina.com/
	 *               v3/users/{{username}}/share/{{shareId
	 *               }}/password?access_token=xxxxx&access_id=xxxxxx
	 * 
	 *               <br>
	 *               <b>params: entity {”password”:””} password：新的分享密码</b>
	 * 
	 * 
	 * 
	 * @date 2016年4月13日 下午2:17:32
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface SharePublicAndPrivateUrl {
		public String DOMAIN = IUrlManager.DOMAIN_V3;
		public String ADDRESS = IUrlManager.ADDRESS_V3;
		public String PUBLICSHAREFUNCTION = "/share/link/public";
		public String PRIVATESHAREFUNCTION = "/share/link/private";
	}

	/**
	 * @ClassName: ShareDelete
	 * @Description: 我的分享删除
	 * @date 2016年1月27日 下午4:57:54
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	/**
	 * @ClassName: ShareDelete
	 * @Description: 取消分享 <br>
	 *               DELETE v3 https://cbs.vsochina.com/v3/users/{username}/
	 *               share?access_token=xxxx&access_id=xxxx
	 * 
	 *               <br>
	 *               <b>params entity {"shareId":[""]} shareId: 要删除的分享对应的id</b>
	 * 
	 * @date 2016年4月13日 下午2:37:22
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface ShareDelete {
		public String DOMAIN = IUrlManager.DOMAIN_V3;
		public String MINEADDRESS = IUrlManager.ADDRESS_V3;
		public String SHARE = "/share?";
	}

	/**
	 * @ClassName: ShareSaveAs
	 * @Description: 分享复制到我的网盘
	 * @date 2016年1月27日 上午8:31:24
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	/**
	 * @ClassName: ShareSaveAs
	 * @Description: 分享给我的-复制到我的云盘<br>
	 *               POST v3 <br>
	 *               https://cbs.vsochina.com/v3/users/{{username_owner}}
	 *               /share/{{shareId}}
	 *               /saveas?queryUsername=xxx&access_token=xxxx&access_id=xxxxx
	 *               <br>
	 *               <b> entity: {"from":"","to":""}
	 *               <li>username_owner ：分享者用户名
	 *               <li>shareId：分享Id queryUsername：当前登录者
	 *               <li>from：分享的文件或文件夹 用户目标的绝对路径
	 *               <li>to：目标路径，当前登录者的目标路径的绝对路径</b>
	 * 
	 * @date 2016年4月13日 下午2:43:23
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface ShareSaveAs {
		public String DOMAIN = IUrlManager.DOMAIN_V3;
		public String MINEADDRESS = IUrlManager.ADDRESS_V3;
		public String SHARE = "/share/";
		public String FUNCTION = "/saveas";
		public String KEY_ABSOLUTEPATH = "from";
		public String KEY_MOVETO = "to";
	}

	/**
	 * @ClassName: ShareIgnore
	 * @Description: 分享给我的-忽略<br>
	 *               POST v3<br>
	 *               https://cbs.vsochina.com/v3/users/{{username_owner}}/share/
	 *               {{shareId}}
	 *               /ignore?queryUsername=xxx&access_token=xxxx&access_id=xxxxx
	 *               <br>
	 *               <b>
	 *               <li>username_owner:分享者用户名
	 *               <li>shareId：分享Id
	 *               <li>queryUsername：请求的用户
	 * 
	 * @date 2016年4月13日 下午2:48:08
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface ShareIgnore {
		public String DOMAIN = IUrlManager.DOMAIN_V3;
		public String MINEADDRESS = IUrlManager.ADDRESS_V3;
		public String SHARE = "/share/";
		public String FUNCTION = "/ignore";
	}

	/**
	 * @ClassName: MineInfoUrl
	 * @Description: 获取用户信息<br>
	 *               GET v3<br>
	 *               https://cbs.vsochina.com/v3/users/{{username}}?access_id=1&
	 *               access_token=1
	 *               <p>
	 *               version2:<br>
	 *               {{server}}v3/users/:username/info?access_token=xxx&
	 *               access_id=xxx
	 * 
	 * @date 2016年4月13日 下午3:04:40
	 * 
	 * @author leobert.lan
	 * @version 2.0
	 * @since JDK 1.7
	 */
	interface MineInfoUrl {
		public String HOST = IUrlManager.DOMAIN_V3;
		public String PATH = IUrlManager.ADDRESS_V3 + "%s/info";
	}

	/**
	 * @ClassName: MineStorageInfoApi
	 * @Description: 获取存储容量接口：<br>
	 *               {{server}}/storage/users/:username/storage?access_token=1&
	 *               access_id=2
	 * @date 2016年8月10日 上午10:44:07
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface MineStorageInfoApi {
		public String HOST = IUrlManager.DOMAIN_V3SUB;
		public String PATH = IUrlManager.ADDRESS_V3SUB + "%s/storage";
	}

	/**
	 * @ClassName: MultiUserInfoApi
	 * @Description:
	 * @date 2016年5月20日 上午10:57:09
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface MultiUserInfoApi {
		public String DOMAIN = IUrlManager.DOMAIN_V3;

		public String ADDRESS = IUrlManager.ADDRESS_V3;

		public String FUNCTION = "info";

		public String KEY_USER = "users";
	}

	/**
	 * @ClassName: UpLoadFile
	 * @Description: 上传文件接口地址，真实环境
	 * @date 2015年12月1日 上午9:56:10
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface UpLoadFile {
		public String COMBINE = "https://uploader.vsochina.com:8000/server/combine.php";

		public String CHUNKCHECK = "https://uploader.vsochina.com:8000/server/chunk.php";

		public String CHUNKUPLOAD = "https://uploader.vsochina.com:8000/server/fileupload.php";
	}

	/**
	 * @ClassName: DownloadFile
	 * @Description: 下载url
	 * @date 2015年12月4日 上午10:43:27
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	/**
	 * @ClassName: DownloadFile
	 * @Description: 下载文件**********************<br>
	 *               <li>云盘下载 GET v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/
	 *               download?path={{path}}</br>
	 *               <li>分享给我的下载 GET v3<br>
	 *               https://cbs.vsochina.com/v3/users/{{username_owner}}/share/
	 *               {{shareId}}/download?path={{path}}&queryUsername={{username
	 *               }}
	 * 
	 * @date 2016年4月13日 下午3:13:16
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface DownloadFile {
		public String DOMAIN = IUrlManager.DOMAIN;
		public String ADDRESS_CB = IUrlManager.ADDRESS_V3SUB;
		public String ADDRESS_SHARE = IUrlManager.ADDRESS_V3;
		public String FUNCTION = "/download";
		public String PARAM = "?path=";
	}

	/**
	 * @ClassName: ImagePreview
	 * @Description: 图片预览
	 * @date 2015年12月4日 上午10:43:27
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	/**
	 * @ClassName: ImagePreview
	 * @Description: 图片预览****************<br>
	 *               <li>分享给我的预览 GET v3<br>
	 *               https://cbs.vsochina.com/v3/users/{{username_owner}}/share/
	 *               {{shareId}}/thumbnail?path={{path}}&queryUsername={{
	 *               username}}&width=1024&height=768
	 * 
	 *               <li>我的云盘预览 GET v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/
	 *               thumbnail?path={{path}}&width=1024&height=768
	 * 
	 * @date 2016年4月13日 下午3:20:29
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface ImagePreview {
		public String DOMAIN = IUrlManager.DOMAIN;
		public String ADDRESS_CB = IUrlManager.ADDRESS_V3SUB;
		public String ADDRESS_SHARE = IUrlManager.ADDRESS_V3;
		public String FUNCTION = "/thumbnail";
		public String PARAM = "?path=";
	}

	/**
	 * @ClassName: SearchListUrl
	 * @Description: 获取搜索列表
	 * @date 2016年1月7日 下午1:19:06
	 * 
	 * @author zhangbin
	 * @version 1.0
	 * @since JDK 1.6
	 */
	/**
	 * @ClassName: SearchListUrl
	 * @Description: 文件、文件夹搜索接口 搜索列表<br>
	 *               GET v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/search?
	 *               name=test&path=/&type=2&page=1&pagesize=50&descendantFiles=
	 *               false&access_id=xx&access_token=xxx <br>
	 *               <b>params： name
	 *               (查询的key)，path（默认全局:/），type（默认全部：2），page，pagesize
	 *               ，descendantFiles（计算内容量），access_id，access_token </b>
	 * @date 2016年4月13日 下午3:35:24
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface SearchListUrl {
		public String DOMAIN = IUrlManager.DOMAIN_V3SUB;
		public String ADDRESS = IUrlManager.ADDRESS_V3SUB;
		public String FUNCTION = "/search?";
	}

	/**
	 * @ClassName: UserListUrl
	 * @Description: 用户文件列表获取接口地址
	 * 
	 * @date 2015年12月1日 上午9:12:56
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	/**
	 * @ClassName: UserListUrl
	 * @Description: 文件列表及分类<br>
	 *               GET v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/list/{{
	 *               filter
	 *               }}?path=/&type=2&page=1&pagesize=50&descendantFiles=false
	 *               &access_id=xx&access_token=xxx <br>
	 *               <b>params：
	 * 
	 *               <li>{{filter}}：image;video;
	 *               document;audio;不使用filter去掉前面的separator对应全部类型
	 *               <li>path（使用filter时默认全局:/）
	 *               <li>type（默认全部：2，0：文件1：文件夹）
	 *               <li>page，pagesize
	 *               ，descendantFiles（计算内容量），access_id，access_token</b>
	 * 
	 * @date 2016年4月13日 下午3:48:06
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface UserListUrl {
		// https://cbs.vsochina.com/storage/
		public String DOMAIN = IUrlManager.DOMAIN_V3SUB;
		public String ADDRESS = IUrlManager.ADDRESS_V3SUB;
		public String FUNCTION_ALL = "/list";
		public String FUNCTION_IMAGE = "/list/image";
		public String FUNCTION_VIDEO = "/list/video";
		public String FUNCTION_DOC = "/list/document";
		public String FUNCTION_AUDIO = "/list/audio";
	}

	/**
	 * @ClassName: FileIcon
	 * @Description: 辅助去掉token信息，不需要自己拼接口
	 * @date 2016年4月13日 下午3:51:03
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface FileIcon {
		public String DOMAIN = IUrlManager.DOMAIN;
		public String FUNCTION_ICON_V3SUB = "storage/icon/";
		public String FUNCTION_ICON_V3 = "v3/icon/";
	}

	/**
	 * @ClassName: Delete
	 * @Description: 删除接口
	 *               <li>删除文件<br>
	 *               DELETE v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/files
	 *               <br>
	 *               <b>entity {"path": ["/file1.txt","/file2.txt"]}
	 *               <li>Path：文件路径数组 （绝对路径）</b><br>
	 *               删除文件夹 DELETE v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/folders
	 *               <br>
	 *               <b>entity {"path":["/folder4","/folder6"]}
	 *               <li>Path：文件夹路径数组 （绝对路径）</b>
	 * @date 2016年4月13日 下午4:00:29
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface Delete {
		public String DOMAIN = IUrlManager.DOMAIN_V3SUB;
		public String ADDRESS = IUrlManager.ADDRESS_V3SUB;
		public String FUNCTION_FILE = "/files";
		public String FUNCTION_FOLDER = "/folders";

	}

	/**
	 * @ClassName: Rename
	 * @Description: 文件重命名
	 * @date 2015年12月22日 上午9:17:38
	 * 
	 * @author zhangbin
	 * @version 1.0
	 */
	/**
	 * @ClassName: Rename
	 * @Description: 重命名****************<br>
	 *               <li>重命名文件 <br>
	 *               POST v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/files
	 *               <br>
	 *               <b>entity {"path": “”,”name”:””}
	 *               <li>Path：文件绝对路径
	 *               <li>name:新名称</b>
	 *               <li>重命名文件夹 <br>
	 *               POST v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/folders
	 *               <br>
	 *               <b>entity {"path": “”,”name”:””}
	 *               <li>Path：文件绝对路径
	 *               <li>name:新名称</b>
	 * 
	 * @date 2016年4月13日 下午4:04:17
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface Rename {
		public String DOMAIN = IUrlManager.DOMAIN_V3SUB;
		public String ADDRESS = IUrlManager.ADDRESS_V3SUB;
		public String FUNCTION_FILE = "/files";
		public String FUNCTION_FOLDER = "/folders";
		public String FUNCTION = "/rename";
	}

	/**
	 * @ClassName: CreateNewFolder
	 * @Description: 创建新文件夹<br>
	 *               POST v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/folders
	 *               <br>
	 *               <b>entity {"path": ""}
	 *               <li>Path：文件绝对路径</b>
	 * 
	 * @date 2016年4月13日 下午4:08:17
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface CreateNewFolder {
		public String DOMAIN = IUrlManager.DOMAIN_V3SUB;
		public String ADDRESS = IUrlManager.ADDRESS_V3SUB;
		public String FUNCTION = "/folders";
	}

	/**
	 * @ClassName: AdviceFeedback
	 * @Description:
	 * @date 2016年4月13日 下午4:09:13
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	/**
	 * @ClassName: AdviceFeedback
	 * @Description: 反馈接口<br>
	 *               POST v3<br>
	 *               https://cbs.vsochina.com/v3/users/{{username}}/feedback
	 *               <br>
	 *               <b>entity:{“app”:”YPANDROID”,”version”:””,"contact":””,
	 *               "content",””}
	 *               <li>contact:联系人信息
	 *               <li>content：反馈信息</b>
	 * 
	 * @date 2016年4月13日 下午4:20:20
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface AdviceFeedback {
		public String DOMAIN = IUrlManager.DOMAIN_V3;
		public String ADDRESS = IUrlManager.ADDRESS_V3;
		public String FUNCTION = "/feedback";
		public String KEY_ACCESSTOKEN = "access_token";
		public String KEY_ACCESSID = "access_id";

	}

	/**
	 * @ClassName: Move
	 * @Description: 移动接口
	 * @date 2016年1月7日 下午4:21:21
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	/**
	 * @ClassName: Move
	 * @Description: 移动文件、文件夹<br>
	 *               POST v3sub<br>
	 *               https://cbs.vsochina.com/storage/users/{{username}}/move
	 * 
	 *               <br>
	 *               <b>entity：{"path":["/folder2","/file2.txt"],"to":"/good"}
	 *               <li>path：文件、文件夹的路径数组 （绝对路径）
	 *               <li>to：指定的目录 </b>
	 * 
	 * @date 2016年4月13日 下午4:14:54
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface Move {
		// https://cbs.vsochina.com/v3/users/【username】/move
		public String DOMAIN = IUrlManager.DOMAIN_V3SUB;
		public String ADDRESS = IUrlManager.ADDRESS_V3SUB;
		public String FUNCTION = "/move";
		public String KEY_ACCESSTOKEN = "access_token";
		public String KEY_ACCESSID = "access_id";

		public String KEY_ABSOLUTEPATH = "path";
		public String KEY_MOVETO = "to";
	}

	/**
	 * @ClassName: Register
	 * @Description: 注册接口，获取验证码（true pwd）
	 * @date 2016年1月12日 上午9:46:36
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface Register {
		/*
		 * 注意，address不要copy
		 */
		public String DOMAIN = IUrlManager.DOMAIN;
		public String ADDRESS = "v3/register/mobile";
		// https://cbs.vsochina.com/v3/register/mobile
		public String KEY_MOBILE = "mobile";
	}

	/**
	 * @ClassName: ResetPassword
	 * @Description: 修改密码
	 * @date 2016年1月12日 上午10:17:03
	 * @author leobert.lan
	 * @version 1.0
	 */
	interface ResetPassword {
		// https://cbs.vsochina.com/v3/users/[username]/password
		public String DOMAIN = IUrlManager.DOMAIN;
		public String ADDRESS = "v3/users/";
		public String FUNCTION = "/password";

		public String KEY_OLDPWD = "oldPassword";
		public String KEY_NEWPWD = "newPassword";
	}

	/**
	 * @ClassName: WebUrlTemp
	 * @Description: 暂时使用web页面
	 * @date 2016年1月20日 上午9:04:47
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 */
	@Deprecated
	interface WebUrlTemp {
		@Deprecated
		public String REGISTER = "https://account.vsochina.com/user/register";
		@Deprecated
		public String FINDPWD = "https://account.vsochina.com/user/resetpassword";
	}

	/**
	 * @ClassName: SearchUsersOnPlatform
	 * @Description: 搜索用户<br>
	 *               GET v3<br>
	 *               https://cbs.vsochina.com/v3/users?username={{username}}&key
	 *               =xxx&limit=10&access_token=xxxx&access_id=xxxxx <br>
	 *               <b>
	 *               <li>username：当前用户
	 *               <li>key：搜索关键字
	 *               <li>limit：记录数</b>
	 * 
	 * @date 2016年4月13日 下午4:29:27
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface SearchUsersOnPlatform {

		public String DOMAIN = IUrlManager.DOMAIN;

		public String ADDRESS = "v3/users";

		public String KEY_USERNAME = "username";

		public String KEY_SEARCHKEY = "key";

		public String KEY_LIMIT = "limit";

		public String KEY_ACCESSTOKEN = "access_token";

		public String KEY_ACCESSID = "access_id";

		public String VALUE_DEFAULT_LIMIT = "100";
	}

	/**
	 * @ClassName: ShareToUser
	 * @Description: 分享给用户<br>
	 *               POST v3<br>
	 *               https://cbs.vsochina.com/v3/users/{{username}}/share/friend
	 *               ?access_token=1&access_id=2 <br>
	 *               <b>entity：
	 *               {"path":"/dayan.jpg","username":["zhou88","zhou81"]}
	 *               <li>path：分享的文件、文件夹的绝对路径
	 *               <li>entity中的username 分享用户数组</b>
	 * 
	 * @date 2016年4月13日 下午4:33:51
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface ShareToUser {

		public String DOMAIN = IUrlManager.DOMAIN;

		public String ADDRESS = "v3/users/";

		public String FUNCTION = "/share/friend";

		public String KEY_ACCESSTOKEN = "access_token";

		public String KEY_ACCESSID = "access_id";

		/**
		 * KEY_PATH:分享全路径
		 */
		public String KEY_PATH = "path";

		/**
		 * KEY_USERNAMES:分享给
		 */
		public String KEY_SHARETO = "username";

	}

	/**
	 * @ClassName: CheckUpdate
	 * @Description: 检查更新 <br>
	 *               POST<br>
	 *               http://soft.vsochina.com/admin.php?r=api/software/update";
	 *               <br>
	 *               <b>params：
	 *               <li>sys default android
	 *               <li>ver 当前版本
	 *               <li>sid default vso-cloud-android</b>
	 * 
	 * @date 2016年4月13日 下午4:25:39
	 * 
	 * @author leobert.lan
	 * @version 1.0
	 * @since JDK 1.7
	 */
	interface CheckUpdate {
		/**
		 * URL:访问地址,post方式
		 */
		public String URL = "http://soft.vsochina.com/admin.php?r=api/software/update";

		public String KEY_SYS = "sys";

		public String VALUE_SYS = "android";

		/**
		 * key_currentversion:当前版本
		 */
		public String KEY_CURRENTVERSION = "ver";

		/**
		 * key_sid:软件标识
		 */
		public String KEY_SID = "sid";

		/**
		 * value_sid:固定sid
		 */
		public String VALUE_SID = "vso-cloud-android";

	}

	interface ShareReport {
		public String DOMAIN = DOMAIN_V3;

		public String FUNCTION = "v3/report/share";
	}

	interface Ads {
		// String url = "http://192.168.1.16:3000/v3/ad/mobile/appboot";

		public String DOMAIN = DOMAIN_V3;

		public String FUNCTION = "v3/ad/mobile/appboot";
	}

	interface VideoPreview {
		// https://pan.vsochina.com/video.html?path=/video/zhaozilong.mp4
		// &access_token=36828176ef5c7052da5e023c80a125ba
		// &access_id=79087484287e484287cd762e29ae4a24&username=zhou88
		String HOST = "pan.vsochina.com";
		String PATH = "video.html";
	}

}
